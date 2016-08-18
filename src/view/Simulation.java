package view;

import Util.CustomTracks;
import controllers.*;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.*;
import save.Load;
import save.LoadedRailway;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.Movable;
import view.Drawable.section_types.*;


import java.io.File;
import java.util.*;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Simulation implements MouseEvents {

    // Modes
    public static final String MODE_USER = "user";
    public static final String MODE_TEST = "test";
    public static final String MODE_CONTROLLER = "controller";
    public static final String NO_MODE = "controller";

    public static final int MAX_CONNECTION_SPEED = 2;

    private String currentMode = NO_MODE;

    private Movable selectedMovable;

    // Each pixel is 1/5 of a meter
    public static final double METER_MULTIPLIER = 5;

    // Model to send events to
    private ModelTrack modelTrack;

    // Simulation state
    private boolean started = false;

    // Trains and Sections
    private List<DrawableTrain> trains;
    private List<DrawableRollingStock> drawableRollingStocks;
    private DrawableSection[] railway;
    private List<Movable> movable;

    // Used when restarting a loaded track
    private File loadedFile;

    // The tracks
    private DefaultTrack tracks[];

    // Last time the logic was updated
    private long lastUpdate;


    private SimulationUI userInterface;


    /**
     * Constructs a new visualisation object with a default track and trains
     * */
    public Simulation(SimulationUI userInterface){
        this.trains = new ArrayList<>();
        this.drawableRollingStocks = new ArrayList<>();
        this.movable = new ArrayList<>();
        this.userInterface = userInterface;
    }

    public void setMode(String mode){
        if(mode.equals("User")){
            sendEventToUI("Controlled by user events",0);

        } else if(mode.equals("Locking")){
            sendEventToUI("Controlling with Locking controller",0);

            controlMode(new DeadLockController(convertoControllerSections(getSections()),convertToControlTrains(trains)));

        } else if(mode.equals("Routing")){
            sendEventToUI("Controlling with routing controller",0);
            controlMode(new RoutingController(convertoControllerSections(getSections()),convertToControlTrains(trains)));
        }
    }

    /**
     * The controller cannot use the Section class so we must turn them into Controller Sections
     * */
    public ControllerSection[] convertoControllerSections(Section[] sections){
        ControllerSection[] controllerSections = new ControllerSection[sections.length];

        for(int i = 0; i < sections.length; i++){
            controllerSections[i] = new ControllerSection(sections[i].getID(),sections[i].getFromIndex(),sections[i].getToIndex(),sections[i].getJuncSectionIndex(),sections[i].getLength());

            if(sections[i].hasJunctionTrack()){
                JunctionTrack j = sections[i].getJunction();
                controllerSections[i].addJunction(j.getId(),j.inBound(),j.getThrown());
            }
        }
        return controllerSections;
    }

    /**
     * The controller cannot use the Train class so we must turn them into Controller Trains
     * */
    public List<ControllerTrain> convertToControlTrains(List<DrawableTrain> trains){
        List<ControllerTrain> controllerTrains = new ArrayList<>();

        for(DrawableTrain t : trains){
            controllerTrains.add(new ControllerTrain(t.getTrain().getId(),t.getDirection(),t.getOrientation(),t.getCurSection().getSection().getID()));
        }
        return controllerTrains;
    }



    /**
     * Returns the available list of controllers for the user interface
     * */
    public List<String> getControllers(){
        List<String> controllers = new ArrayList<>();
        controllers.add("User");
        controllers.add("Locking");
        controllers.add("Routing");
        return controllers;
    }



    /**
     * Sets the default track and trains
     * */
    public void setDefault(){
        CustomTracks c = new CustomTracks("FULL");
        tracks = c.getFullTracks();
        railway = c.getFullSection(tracks);

        // Two trains
        trains = CustomTracks.getDefaultTrains(railway);
        movable = CustomTracks.createMovableList(trains,drawableRollingStocks);

        this.modelTrack = new ModelTrack(getTrains(), getSections());
    }

    public void setFromBuilderMode(LoadedRailway loadedRailway){
        tracks = loadedRailway.tracks;
        railway = loadedRailway.sections;
        trains = loadedRailway.trains;
        drawableRollingStocks = loadedRailway.stocks;
        movable = CustomTracks.createMovableList(trains,drawableRollingStocks);

        this.modelTrack = new ModelTrack(getTrains(), getSections());
    }

    /**
     * Mode used for testing
     * */
    public void testMode(){
        for(DrawableTrain t : trains){
            modelTrack.setSpeed(t.getTrain().getId(), 28);
        }
        modelTrack.setSpeed(trains.get(0).getTrain().getId(), 27);
        started = true;
    }



    public void loadRailway(File file, LoadedRailway loadedRailway) {
        this.loadedFile = file;
        this.railway = loadedRailway.sections;
        this.tracks = loadedRailway.tracks;
        this.trains = loadedRailway.trains;
        this.drawableRollingStocks = loadedRailway.stocks;

        // Set up the images for the trains and stocks
        trains.forEach(t -> t.setUpImage());
        trains.forEach(t -> movable.add(t));
        drawableRollingStocks.forEach(s -> s.setUpImage());
        drawableRollingStocks.forEach(s -> movable.add(s));

        this.drawableRollingStocks = loadedRailway.stocks;

        movable = CustomTracks.createMovableList(trains, drawableRollingStocks);
        this.modelTrack = new ModelTrack(getTrains(), getSections());
    }

    public Map<Train, Integer> getStartMap(){
        Map<Train, Integer> startMap = new HashMap<>();
        for(DrawableTrain train : trains){
            startMap.put(train.getTrain(), train.getCurSection().getSection().getID());
        }

        return  startMap;
    }

    public void controlMode(DefaultController c){
        modelTrack.setController(c);
        modelTrack.useController(true);

        modelTrack.register(c);
        c.register(modelTrack);

        c.startControlling();
    }

    public void start(){
        String mode = userInterface.getSelectedMode();
        // If the section the train is on can detect set on to be true
        for(DrawableTrain t : trains){
            if(t.getCurSection().getSection().canDetect()){
                t.getCurSection().getSection().setTrainOn(true);
            }
        }

        if(mode.equals(MODE_CONTROLLER)){
//            controlMode();
        }
        else if(mode.equals(MODE_TEST)){
            testMode();
        }
    }

    public void restart(){
        started = false;// Stop the updates

        // Reload the file if simulation started from file
        if(loadedFile != null){
            Load load = new Load();
            LoadedRailway railway = load.loadFromFile(loadedFile,loadedFile.getAbsolutePath());
            loadRailway(loadedFile,railway);
        }else {
            setDefault();
        }
    }

    /**
     * Pauses the simulation
     * */
    public void pause(){
        started = false;
    }

    public void setRailway(DrawableSection[] railway){
        this.railway = railway;
    }

    /**
     * Updates the trains
     * */
    public void update(){
        if(started){
            trains.forEach(t -> {
                checkCollision();
                onSectionCheck(t,0);
                t.update();
            });

            drawableRollingStocks.forEach(r -> {
                checkCollision();
                onSectionCheck(r,r.getDistanceMoved());
                r.update();
            });
        }
    }


    /**
     * Redraws all the elements on the screen
     * */
    public void refresh(GraphicsContext g){
        // Draw the sections which will draw the tracks
        for(DrawableSection d : railway){
            d.draw(g);
        }

        // Updates all the things that move on the track
        movable.forEach(m -> m.draw(g));
    }

    /**
     * Checks if a movable object will still be on the junction track after it has moved.
     * Sets the track it should be on if no longer on it.
     * */
    public void onSectionCheckJunction(Movable t, double pixelsToMove, JunctionTrack jt){

        // Check if it will be on the junction after it moves
        if(!jt.checkOnAfterUpdate(t,pixelsToMove)){// Can update the cur junction track
            DefaultTrack destinationTrack = null;

            // Check if the train is going along the nat track orientation
            if(forwardWithTrack(t)){
                if(jt.inBound()){
                    destinationTrack = tracks[jt.getTo()];
                } else{
                    destinationTrack = tracks[jt.getToOutbound()];// Not inbound and going forward
                }
            } else{// Going backwards or forwards but against nat orientation
                if(jt.inBound()){
                    destinationTrack = tracks[jt.getInboundFrom()];
                } else {
                    destinationTrack = tracks[jt.getFrom()];
                }
            }
            t.setCurTrack(destinationTrack);

            // We only care about section changes is it is a train
            if(t instanceof DrawableTrain){
                checkSectionChangedEvent((DrawableTrain)t,((DrawableTrain)t).getCurSection(),destinationTrack);
            }
        }
    }

    /**
     * Check if any of the trains or rolling stocks crash into each other.
     * If they are set the trains involved to crashed
     * */
    public void checkCollision(){
        for(int i = 0; i < movable.size(); i++){
            for(int j = 0; j < movable.size(); j++){
                if(j !=i){
                    if((movable.get(j).containsPoint(movable.get(i).getFront().getX(),movable.get(i).getFront().getY()) || movable.get(j).containsPoint(movable.get(i).getBack().getX(),movable.get(i).getBack().getY()))){
                        collided(movable.get(i), movable.get(j));
                    }
                }
            }
        }
    }


    /**
     * Called when two movable items collide into each other. Connects rolling stock to train
     * if possible
     * */
    public void collided(Movable movable1, Movable movable2){
        if(!notConnected(movable1,movable2))return;

        // First check the speed of the collision if they are going to fast the rest does not matter
        if(movable1.getCurrentSpeed() + movable2.getCurrentSpeed() > Simulation.MAX_CONNECTION_SPEED || movable1 instanceof DrawableTrain && movable2 instanceof DrawableTrain){
            crash(movable1,movable2);
            return;
        }

        if(movable1 instanceof DrawableRollingStock && movable2 instanceof DrawableRollingStock){// TODO put something in here that checks if the rolling stocks are contained in the same unit
            return;
        }

        DrawableTrain t = movable1 instanceof DrawableTrain ? (DrawableTrain) movable1 : (DrawableTrain)movable2;
        DrawableRollingStock r = movable1 instanceof DrawableRollingStock ? (DrawableRollingStock) movable1 : (DrawableRollingStock) movable2;

        if(r.getFrontConnection().intersects(t.getConnection().getLayoutBounds()) || r.getBackConnection().intersects(t.getConnection().getLayoutBounds()) ){
            r.setConnection(t);
            t.setRollingStockConnected(r);
        } else {
            crash(movable1,movable2);
        }
    }

    private void crash(Movable m1, Movable m2 ){
        m1.setCrashed(true);
        m2.setCrashed(true);
        sendEventToUI("Collision ", 2);
    }

    /**
     * Returns if the two movable items are not connected
     * */
    public boolean notConnected(Movable m1, Movable m2){
        if(m1 instanceof DrawableTrain && m2 instanceof DrawableTrain)return true;
        if(m2.getRollingStockConnected() != null && m2.getRollingStockConnected().equals(m1))return false;
        if(m1.getRollingStockConnected() != null && m1.getRollingStockConnected().equals(m2))return false;
        return true;// they are not connected
    }

    /**
     * Returns if the train is going along with the natural orientation of the track
     *
     * @param t train to check
     * */
    public boolean forwardWithTrack(Movable t){
        return t.getOrientation() && t.getDirection() || !t.getOrientation() && !t.getDirection();
    }

    /**
     * Checks if the new track the train have moved into is in the current section if not send out an
     * event for a section change to the model track and for the event log
     * */
    public void checkSectionChangedEvent(DrawableTrain t,DrawableSection curSection, DefaultTrack destinationTrack){
        // Check if the current section contains the new track to move to
        if(!curSection.containsTrack(destinationTrack)){

            // If the current section can detect we must send an event since it has changed state
            if(curSection.getSection().canDetect()){
                if(userInterface != null){
                    userInterface.sendToeventLog(updateTrainOnSection(t.getTrain(), curSection.getSection(),curSection.getSection()),1);
                }
                modelTrack.sectionChanged(curSection.getSection().getID());
            }

            //  Find where it belongs to
            for(DrawableSection ds : railway){
                if(ds.containsTrack(destinationTrack)){
                    DrawableSection last = t.getCurSection();
                    t.setCurSection(ds);//have to do it this way since the destination is not always the same
                    if(ds.getSection().canDetect()){
                        sendEventToUI(updateTrainOnSection(t.getTrain(), last.getSection(), ds.getSection()),1);
                        modelTrack.sectionChanged(ds.getSection().getID());
                    }

                }
            }
        }
    }

    public double getDistanceToMoveFromTrain(DrawableTrain drawableTrain){
        double speedInPixels = drawableTrain.getCurrentSpeed() * Simulation.METER_MULTIPLIER;//
        long curTime = System.currentTimeMillis();
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*speedInPixels;
        lastUpdate = System.currentTimeMillis();
        return pixelsToMove;
    }

    /**
     * Checks if a drawable train is on a track given after it has moved a certain amount based on its speed
     * changes the trains track is no longer on the same track and send this information to the controller.
     * */
    public void onSectionCheck(Movable t, double pixelsToMove){
        if(t.isCrashed())return;

        DrawableSection curSection = null;
        if(t instanceof DrawableTrain){
            DrawableTrain dt = (DrawableTrain)t;
            curSection = dt.getCurSection();

            pixelsToMove = getDistanceToMoveFromTrain(dt);
        }

        DefaultTrack curTrack = t.getCurTrack();
        if(curTrack instanceof JunctionTrack){
            onSectionCheckJunction(t,pixelsToMove,(JunctionTrack)curTrack);
            return;
        }

        // Check if the train will be on another track after the update
        if(!curTrack.checkOnAfterUpdate(t.getCurrentLocation(),t.getCurRotation(),t.getDegDone() ,pixelsToMove,t)){

            DefaultTrack destinationTrack = null;

            // If the train is going forward along the natural orientation of the current track
            if(forwardWithTrack(t)){

                // The track is the end
                if(curTrack.getTo() == -1){
                    t.setCrashed(true);
                    return;
                }
                destinationTrack = tracks[curTrack.getTo()];

                if(curTrack.getJuncTo() != -1 && ((JunctionTrack) tracks[curTrack.getJuncTo()]).getThrown()) {// Check if it is possible it is going to a junction track
                    destinationTrack = tracks[curTrack.getJuncTo()];
                }
            } else{
                if(curTrack.getFrom() == -1){
                    t.setCrashed(true);
                    return;
                }
                destinationTrack = tracks[curTrack.getFrom()];// There is only one track coming from it
                if(curTrack.getJuncFrom() != -1 && ((JunctionTrack)tracks[curTrack.getJuncFrom()]).getThrown()){
                    destinationTrack = tracks[curTrack.getJuncFrom()];
                }
            }

            // Sets the next track
            int prevTrackID = getTrackIndex(curTrack);
            t.setCurTrack(destinationTrack);

            // If the destination is a junction we need to work out which track inside the junction track it goes to
            if(destinationTrack instanceof JunctionTrack){
                JunctionTrack jt = (JunctionTrack)destinationTrack;
                t.setJuncTrack(jt.getTrackToStartOn(prevTrackID));
            }

            // Check if the track it moves to is in a different section
            if(curSection != null){// Will be null if movable is rolling stock
                checkSectionChangedEvent((DrawableTrain)t,curSection,destinationTrack);
            }
        }
    }

    public int getTrackIndex(DefaultTrack track){
        for(int i = 0; i < tracks.length; i++){
            if(tracks[i].equals(track))return i;
        }
        return -1;
    }

    public boolean validTrainID(int id){
        for(DrawableTrain t : trains){
            if(t.getTrain().getId() == id)return false;
        }
        return true;
    }

    /**
     * Used for testing
     * */
    public void setModelTrack(ModelTrack model){
        this.modelTrack = model;
    }


    /**
     * Returns the Sections from the drawable sections
     * */
    public Section[] getSections(){
        Section[] sections = new Section[railway.length];
        for(int i =0; i < railway.length; i++){
            sections[i] = railway[i].getSection();
        }
        return sections;
    }

    /**
     * Returns the list of trains on the track
     * */
    public List<Train> getTrains(){
        List<Train> trains = new ArrayList<>();

        for(DrawableTrain dt : this.trains){
            trains.add(dt.getTrain());
        }
        return trains;
    }


    /**
     * Sets the trains on the track
     * */
    public void setTrains(List<DrawableTrain> trains){
        this.trains = trains;
    }

    /**
     * If the point clicked is on a Junction it toggles the junction on that point
     *
     * @param x location of the click
     *
     * @param y location of the click
     * */
    public void toggleJunctionOnPoint(double x, double y){
        for(DefaultTrack t : tracks){
            if(t.containsPoint(x,y)){
                if(t instanceof JunctionTrack){
                    ((JunctionTrack) t).setThrown(!((JunctionTrack) t).getThrown());
                }
            }
        }
    }

    public String updateTrainOnSection(Train t, Section newSection, Section prevSection){
        return ("Train ID:" + t.getId() + " Changed from ID:" + prevSection.getID() + " To section ID:" + newSection.getID() + "\n \n");
    }


    public boolean onMovable(double x, double y){
        for(Movable m : movable){
            if(m.containsPoint(x,y)){
                if(m instanceof DrawableTrain){
                    userInterface.setSelectedTrain((DrawableTrain)m);
                }
                selectedMovable = m;
                return true;
            }
        }
        return false;
    }

    public DrawableSection getSection(DefaultTrack dt){
        for(DrawableSection ds : railway){
            if(ds.containsTrack(dt))return ds;
        }
        return null;
    }


    public DefaultTrack getOnTrack(double x, double y){
        for(DefaultTrack dt : tracks){
            if(dt.containsPoint(x,y)){
                return dt;
            }
        }
        return null;
    }

    public int getNextTrainID(){
        int maxID = 0;
        for(DrawableTrain t : trains){
            if(t.getTrain().getId() > maxID){
               maxID = t.getTrain().getId();
            }
        }
        maxID++;
        return maxID;
    }

    // New methods
    public void addTraintoSimulation(DrawableTrain train, int numberOfRollingStock){

        // Add a rolling stock if the user enters a amount
        if(numberOfRollingStock > 0){

            // Always create one
            DrawableRollingStock drawableRollingStock = new DrawableRollingStock(new RollingStock(15,1,50000), train, train.getDirection(), train.getOrientation());

            // Places it on the location of the train in from of it and reverses until in place
            drawableRollingStock.setStart(train.getCurrentLocation(),this);

            movable.add(drawableRollingStock);
            drawableRollingStocks.add(drawableRollingStock);
            drawableRollingStock.setUpImage();

            // Connect it to the train
            train.setRollingStockConnected(drawableRollingStock);

            // Used to connect the next rolling stock to this one
            DrawableRollingStock currentRollingStock = drawableRollingStock;

            // If the user enters a number greater than one make more
            for(int i = 1; i < numberOfRollingStock; i++){
                DrawableRollingStock nRS = new DrawableRollingStock(new RollingStock(15,1,10000), currentRollingStock, train.getDirection(),train.getOrientation());
                nRS.setStart(currentRollingStock.getCurrentLocation(), this);
                currentRollingStock = nRS;

                // Add rolling stock to simulation
                movable.add(nRS);
                drawableRollingStocks.add(nRS);
                nRS.setUpImage();
            }
        }

        // Add the train to simulation
        train.setUpImage();
        trains.add(train);
        movable.add(train);
        modelTrack.addTrain(train.getTrain());
        train.getTrain().setTargetSpeed(27);//TODO replace with what the user specifies
    }

    public void addRollingStocktoSimulation(DrawableRollingStock stock){
        drawableRollingStocks.add(stock);
        movable.add(stock);
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {
//        for(DefaultTrack track : tracks){
//            if(track.containsPoint(x,y)){
//                track.setColor(Color.GREEN);
//            }
//            else {
//                track.setColor(Color.WHITE);
//            }
//        }
    }

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {
        if(e.getButton().equals(MouseButton.PRIMARY)){
            if(e.getClickCount() == 2){
                toggleJunctionOnPoint(x,y);
                if(getOnTrack(x,y) != null){
                    DefaultTrack dt = getOnTrack(x,y);
                    userInterface.showTrackMenu(dt);
                }
            }
            else{
                if(onMovable(x,y)){
                    userInterface.sendToeventLog("Train Selected",1);
                }
            }
        }
    }

    public void setTracks(DefaultTrack[] tracks){
        this.tracks = tracks;
    }

    /**
     * Should only be used for testing
     * */
    public void setStart(boolean start){
        this.started = start;
    }

    /**
     * Checks if the user interface exists before sending the event. It will be null if
     * test are run
     */
    public void sendEventToUI(String event, int status){
        if(userInterface != null){
            userInterface.sendToeventLog(event, status);
        }
    }

    public boolean getStarted(){
        return this.started;
    }

    public ModelTrack getModelTrack(){
        return this.modelTrack;
    }

    @Override
    public void keyPressed(String code) {}

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}
}
