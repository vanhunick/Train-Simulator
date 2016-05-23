package view;

import Util.CustomTracks;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.*;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.Movable;
import view.Drawable.section_types.*;
import view.Panes.ErrorDialog;
import view.Panes.EventGen;
import view.Panes.EventLog;
import view.Panes.TrackMenu;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Visualisation implements MouseEvents {

    private Movable seclectedMovable;

    //Width of buttons
    public static final int WIDTH = 150;

    // Model to send events to
    private ModelTrack modelTrack;

    // Simulation state
    private boolean started = false;

    // Gui pane to send events to for user
    private EventLog eventLog;

    // Trains and Sections
    private List<DrawableTrain> trains;
    private List<DrawableRollingStock> drawableRollingStocks;
    private DrawableSection[] railway;
    private List<Movable> movable;

    // The tracks
    private DefaultTrack tracks[];

    // buttons for visualisation
    private VBox vBox;

    // Last time the logic was updated
    private long lastUpdate;

    // Is the log currently shows on by def
    private boolean logShown;


    /**
     * Constructs a new visualisation object with a default track and trains
     * */
    public Visualisation(){
        this.logShown = true;
        this.vBox = getVisualisationButtons();
        this.eventLog = new EventLog();

        // Set the default track
        this.trains = new ArrayList<>();
        this.drawableRollingStocks = new ArrayList<>();
        this.movable = new ArrayList<>();
        CustomTracks ct = new CustomTracks("FULL");


        this.tracks = ct.getTracks();
        this.railway = ct.getSections();
        addDefaultTrains();
        this.modelTrack = new ModelTrack(getTrains(), getSections());
        setSpeeds();//TODO just used for testing movement with keys
        System.out.println("Setting speeds");
    }

    public void setSpeeds(){
        for(DrawableTrain t : trains){
            modelTrack.setSpeed(t.getTrain().getId(), 400);

//            if(t.getTrain().getId() == 1){
//                modelTrack.setSpeed(t.getTrain().getId(), 500);
//            }
//            else{
//                modelTrack.setSpeed(t.getTrain().getId(), 100);
//            }
        }
    }


    /**
     * Updates the trains
     * */
    public void update(){
        if(started){
            for(DrawableTrain t : trains){
                checkCollision();
                onSectionCheck(t,0);
                t.update();

            }

            // Update the rolling stocks need to be done after as the need to know how much to move based on the train
            // they are connected to
            for(DrawableRollingStock drs : drawableRollingStocks){
                drs.update();
            }
        }
    }


    /**
     * Redraws all the elements on the screen
     * */
    public void refresh(GraphicsContext g){
        g.setStroke(Color.WHITE);

        // Draw the sections which will draw the tracks
        for(DrawableSection d : railway){
            d.draw(g);
        }

        // Updates all the things that move on the track
        for(Movable m : movable){
            m.draw(g);
        }
    }


    public void onSectionCheckJunction(Movable t, double pixelsToMove, JunctionTrack jt){

        // Check if it will be on the junction after it moves
        if(!jt.checkOnAfterUpdate(t,pixelsToMove)){// Can update the cur junction track
            DefaultTrack destinationTrack = null;

            // Check if the train is going along the nat track orientation
            if(forwardWithTrack(t)){
                if(jt.inBound()){
                    destinationTrack = tracks[jt.getInboundTo()];
                }
                else{
                    destinationTrack = tracks[jt.getToOutbound()];// Not inbound and going forward
                }
            }
            else{// Going backwards or forwards but against nat orientation
                if(jt.inBound()){
                    destinationTrack = tracks[jt.getInboundFrom()];
                }
                else {
                    destinationTrack = tracks[jt.getOutboundFrom()];
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

            // Find the front of the train
            double frontX = movable.get(i).getCurrentLocation().getX() + ((movable.get(i).getLength()/2) * (Math.cos(Math.toRadians(movable.get(i).getCurRotation()-90))));
            double frontY = movable.get(i).getCurrentLocation().getY() + ((movable.get(i).getLength()/2) * (Math.sin(Math.toRadians(movable.get(i).getCurRotation()-90))));

            // Find the back of the train
            double backX = movable.get(i).getCurrentLocation().getX() + ((movable.get(i).getLength()/2) * (Math.cos(Math.toRadians(movable.get(i).getCurRotation()-90+180))));
            double backY = movable.get(i).getCurrentLocation().getY() + ((movable.get(i).getLength()/2) * (Math.sin(Math.toRadians(movable.get(i).getCurRotation()-90+180))));

            for(int j = 0; j < movable.size(); j++){
                if(j !=i){
                    if((movable.get(j).containsPoint(frontX,frontY) || movable.get(j).containsPoint(backX,backY)) && notConnected(movable.get(i), movable.get(j))){
                        movable.get(i).setCrashed(true);
                        movable.get(j).setCrashed(true);
                    }
                }
            }
        }
    }

    //TODO CHANGE THIS LATER IT'S VERY BAD
    public boolean notConnected(Movable m1, Movable m2){
        if(m1 instanceof DrawableTrain && m2 instanceof DrawableTrain)return true;
        if(m2.getRollingStockConnected() != null){
            if(m2.getRollingStockConnected().equals(m1))return false;
        }
        if(m1.getRollingStockConnected() != null){
            if(m1.getRollingStockConnected().equals(m2))return false;
        }
        return true;// they are connected
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
                eventLog.appendText(updateTrainOnSection(t.getTrain(), curSection.getSection(),curSection.getSection()));
                modelTrack.sectionChanged(curSection.getSection().getID());
            }

            //find where it belongs to
            for(DrawableSection ds : railway){
                if(ds.containsTrack(destinationTrack)){
                    DrawableSection last = t.getCurSection();
                    t.setCurSection(ds);//have to do it this way since the destination is not always the same
                    if(ds.getSection().canDetect()){
                        eventLog.appendText(updateTrainOnSection(t.getTrain(), last.getSection(),ds.getSection()));
                        modelTrack.sectionChanged(ds.getSection().getID());
                    }
                }
            }
        }
    }

    public double getDistanceToMoveFromTrain(DrawableTrain drawableTrain){
        double speed = drawableTrain.getCurrentSpeed();
        long curTime = System.currentTimeMillis();
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*speed;
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

            if(pixelsToMove == 0){
                pixelsToMove = getDistanceToMoveFromTrain(dt);
            }
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
                destinationTrack = tracks[curTrack.getTo()];

                if(curTrack.getJuncTo() != -1 && ((JunctionTrack) tracks[curTrack.getJuncTo()]).getThrown()) {// Check if it is possible it is going to a junction track
                    destinationTrack = tracks[curTrack.getJuncTo()];
                }
            }
            else{
                destinationTrack = tracks[curTrack.getFrom()];// There is only one track coming from it
                if(curTrack.getJuncFrom() != -1 && ((JunctionTrack)tracks[curTrack.getJuncFrom()]).getThrown()){
                    destinationTrack = tracks[curTrack.getJuncFrom()];
                }
            }

            // Sets the next track
            int prevTrackID = curTrack.getId();
            t.setCurTrack(destinationTrack);

            // If the destination is a junction we need to work out which track inside the junction track it goes to
            if(destinationTrack instanceof JunctionTrack){
                JunctionTrack jt = (JunctionTrack)destinationTrack;

                t.setJuncTrack(jt.getStraightTrack());

                // Train going along the track orientation
                if(forwardWithTrack(t)){

                    if(jt.inBound()){
                        if(prevTrackID == jt.getInboundFromThrown()){
                            t.setJuncTrack(jt.getInboundThrownJuncTrack());
                        }
                    }
                    // Not inbound
                    else {
                        if(jt.getThrown()){
                            t.setJuncTrack(jt.getOutBoundThrownJuncTrack());
                        }
                    }
                }
                else {// Not going a along with track
                    if(jt.inBound()){
                        if(jt.getThrown()){
                            t.setJuncTrack(jt.getInboundThrownNotNatJuncTrack());
                        }
                    }
                    else {// Not inbound
                        if(prevTrackID == jt.getOutboundToThrown()){
                            t.setJuncTrack(jt.getOutBoundNotNatThrownJuncTrack());
                        }
                    }
                }
            }

            // Check if the track it moves to is in a different section
            if(curSection != null){// Will be null if movable is rolling stock
                checkSectionChangedEvent((DrawableTrain)t,curSection,destinationTrack);
            }
        }
    }



    /**
     * Called when user presses the event button the send an event to a train
     * */
    public void startEventDialog(){
        if(started == false){
            new ErrorDialog("Start simulation before sending event", "Event Start Error");
            return;
        }
        new EventGen(modelTrack);
    }

    /**
     * Stops the simulation
     * */
    public void restartSimulation(){
        started = false;// Stop the updates
        CustomTracks ct = new CustomTracks("FULL");

        this.tracks = ct.getTracks();
        this.railway = ct.getSections();
        trains.clear();
        movable.clear();
        addDefaultTrains();

        this.drawableRollingStocks.clear();
    }

    /**
     * Pauses the simulation
     * */
    public void pause(){
        started = false;
    }


    /**
     * Starts the simulation with the given track and trains
     * */
    public void startSimulation(){
        this.modelTrack = new ModelTrack(getTrains(), getSections());

        for(DrawableTrain t : trains){
            if(t.getCurSection().getSection().canDetect()){
                t.getCurSection().getSection().setTrainOn(true);
            }
        }

        setSpeeds();
        started = true;

        lastUpdate = System.currentTimeMillis();
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
     * Adds some default trains train to the starting track
     * */
    public void addDefaultTrains(){
        // Add a train to the track
        for(DrawableSection ds : railway) {
            if (ds.getSection().getID() == 99) {

                //Create the train
                Train train = new Train(1, 80, 500, true,true,0.2, 0.8);
                DrawableTrain drawableTrain = new DrawableTrain(train, ds,ds.getTracks()[0]);
//
//                RollingStock rollingStock = new RollingStock(80,828282);
//                DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock,drawableTrain,drawableTrain.getTrain().getDirection());
//                drawableRollingStock.setStart(drawableTrain.getCurrentLocation(),this);
//
//                drawableTrain.setRollingStockConnected(drawableRollingStock);

//                drawableRollingStocks.add(drawableRollingStock);
                trains.add(drawableTrain);
                movable.add(drawableTrain);
//                movable.add(drawableRollingStock);
            }
            if(ds.getSection().getID() == 101){
                Train train1 = new Train(2, 80, 100, true,true, 0.8, 0.8);
                DrawableTrain drawableTrain1 = new DrawableTrain(train1, ds,ds.getTracks()[0]);

//
//                RollingStock rollingStock1 = new RollingStock(80,847584578);
//                DrawableRollingStock drawableRollingStock1 = new DrawableRollingStock(rollingStock1,drawableTrain1,drawableTrain1.getTrain().getDirection());
//                drawableRollingStock1.setStart(drawableTrain1.getCurrentLocation(),this);
//
//                drawableTrain1.setRollingStockConnected(drawableRollingStock1);

                trains.add(drawableTrain1);
                movable.add(drawableTrain1);
//                movable.add(drawableRollingStock1);
            }
        }
    }



    /**
     * Sets the railway to draw
     * */
    public void setRailway(DrawableSection[] rail){
        this.railway = rail;
    }

    /**
     * Sets the trains on the track
     * */
    public void setTrains(List<DrawableTrain> trains){
        this.trains = trains;
    }



    /**
     * Sets the trains to be controlled by the controller
     * */
    public void useController(){
        this.modelTrack = new ModelTrack(getTrains(), getSections());

        Map<Train, Integer> startMap = new HashMap<>();
        for(DrawableTrain train : trains){
            startMap.put(train.getTrain(), train.getCurSection().getSection().getID());
        }

        Controller controller = new Controller(startMap,getSections(),modelTrack);
        modelTrack.setController(controller);
        modelTrack.useController(true);

        // If the section the train is on can detect set on to be true
        for(DrawableTrain t : trains){
            if(t.getCurSection().getSection().canDetect()){
                t.getCurSection().getSection().setTrainOn(true);
            }
        }

        // Set the controller to take control
        controller.startControlling();

        // Start the updates
        started = true;
        lastUpdate = System.currentTimeMillis();
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


    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {
        if(e.getButton().equals(MouseButton.PRIMARY)){
            if(e.getClickCount() == 2){
                toggleJunctionOnPoint(x,y);
                if(getOnTrack(x,y) != null){
                    DefaultTrack dt = getOnTrack(x,y);
                    showTrackMenu(dt);
                }
            }
            else{
                if(onMovable(x,y)){
                    eventLog.appendText("Train Selected");
                }
            }
        }
    }

    public boolean onMovable(double x, double y){
        for(Movable m : movable){
            if(m.containsPoint(x,y)){
                seclectedMovable = m;
                return true;
            }
        }
        return false;
    }

    public void showTrackMenu(DefaultTrack dt){
        TrackMenu menu = new TrackMenu(dt);

        if(menu.addTrain()){
            String selectedTrain = menu.getCurTrainSelection();
            if(selectedTrain.equals("British Rail Class 25")){
                Train train1 = new Train(getNextTrainID(), 80, 500, true,true, 0.2, 0.5);
                DrawableTrain drawableTrain1 = new DrawableTrain(train1, getSection(dt),dt);

                trains.add(drawableTrain1);
                movable.add(drawableTrain1);

            }
            else if(selectedTrain.equals("British Rail Class 108 (DMU)")){

            }
            else if(selectedTrain.equals("British Rail Class 101 (DMU)")){

            }
        }

        if(menu.addRollingStocl()){
            RollingStock rollingStock = new RollingStock(80,100,0.8);
            DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock);
            drawableRollingStock.setStartNotConnected(dt);
            drawableRollingStocks.add(drawableRollingStock);
            movable.add(drawableRollingStock);
        }
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
        maxID+=1;
        return maxID;
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {
        for(DefaultTrack track : tracks){
            if(track.containsPoint(x,y)){
                track.setColor(Color.GREEN);
            }
            else {
                track.setColor(Color.WHITE);
            }
        }
    }

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {}


    // UI METHODS
    /**
     * Creates the buttons for the visualisation and sets up the listners
     * */
    private VBox getVisualisationButtons(){
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(5,5,5,5));
        Button sim = new Button("Start Simulation");
        Button restart = new Button("Restart");
        Button pause = new Button("Pause");
        Button event = new Button("Event");
        Button controller = new Button("Use Controller");

        sim.setOnAction(e -> startSimulation());
        restart.setOnAction(e -> restartSimulation());
        pause.setOnAction(e -> pause());
        event.setOnAction(e -> startEventDialog());
        controller.setOnAction(e -> useController());

        vBox.getChildren().addAll(sim,restart, pause,event, controller);
        vBox.setPrefWidth(WIDTH);

        return vBox;
    }

    /**
     * Adds the UI elements for the visualisation from the pane. Used when switching mode
     * */
    public void addUIElementsToLayout(BorderPane bp){
        bp.setRight(eventLog);
        bp.setLeft(vBox);
    }

    /**
     * Removes the UI elements for the visualisation from the pane. Used when switching mode
     * */
    public void removeUIElementsFromLayout(BorderPane bp){
        bp.getChildren().remove(eventLog);
        bp.getChildren().remove(vBox);
    }

    /**
     * Toggles if the log is showing or not
     * */
    public void toggleLog(BorderPane bp){
        if(logShown){
            bp.getChildren().remove(eventLog);
            logShown = !logShown;
        }
        else {
            bp.setRight(eventLog);
            logShown = !logShown;
        }
    }

    public void moveTrain(boolean forward){
        if(seclectedMovable != null){
            seclectedMovable.setDirection(forward);

            for(int i = 0; i < 100; i++){
                System.out.println("updating");
                seclectedMovable.update();
            }
        }
    }

    /**
     * Returns if the log is showing or not
     * */
    public boolean logShowing(){
        return this.logShown;
    }

    public void keyPressed(String code){
        System.out.println(code);
        if(code.equals("UP")){
            moveTrain(true);
            System.out.printf("Forwards");
        }
        else if(code.equals("DOWN")){
            moveTrain(false);
            System.out.println("Backwards");
        }
    }

}
