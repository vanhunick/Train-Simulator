package view;

import Util.CustomTracks;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.ModelTrack;
import model.RollingStock;
import model.Section;
import model.Train;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.*;
import view.Panes.EventGen;
import view.Panes.EventLog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Visualisation implements MouseEvents {

    //Width of buttons
    public static final int WIDTH = 150;

    // Model to send events to
    private ModelTrack modelTrack;

    // Simulation state
    private boolean started = false;

    // Gui pane to send events to for user
    private  EventLog eventLog;

    // Trains and Sections
    private List<DrawableTrain> trains;
    private DrawableSection[] railway;

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
        CustomTracks ct = new CustomTracks("FULL");

        this.tracks = ct.getTracks();
        this.railway = ct.getSections();
    }


    /**
     * Updates the trains
     * */
    public void update(){
        if(started){
            for(DrawableTrain t : trains){
                onSectionCheck(t);
                t.update();
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

        //Draw the trains
        for(DrawableTrain t : trains){
            t.draw(g);
            if(t.getRollingStockConnected() !=null){
                //t.getRollingStockConnected().refresh(g);
            }
        }
    }
    public void onSectionCheckJunction(DrawableTrain t, double pixelsToMove, JunctionTrack jt){
        if(t.getJuncTrack() == null){
            if(t.getTrain().getOrientation() && t.getTrain().getDirection() || !t.getTrain().getOrientation() && !t.getTrain().getDirection() ){
                t.setJuncTrack(jt.getToTrack());
            }
            else {
                t.setJuncTrack(jt.getFromTrack());
            }
        }

        // No longer on the junction
        if(!jt.checkOnAfterUpdate(t,pixelsToMove)){// Can update the cur junction track
            DefaultTrack destinationTrack = null;

            // Check if the train is going along the nat track orientation
            if(t.getTrain().getOrientation() && t.getTrain().getDirection() || !t.getTrain().getOrientation() && !t.getTrain().getDirection() ){
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

            checkSectionChangedEvent(t,t.getCurSection(),destinationTrack);
        }
    }

    public void onSectionCheck(DrawableRollingStock rollingStock, double pixelsToMove){
        DefaultTrack curTrack = rollingStock.getCurTrack();
        DrawableTrain t = rollingStock.getConnectedToTrain();

        if(rollingStock.getConToThis() !=null){
            onSectionCheck(t.getRollingStockConnected(), pixelsToMove);
        }

        if(!curTrack.checkOnAfterUpdate(rollingStock.getCurrentLocation(),rollingStock.lastPointOnCurve,pixelsToMove,t.getTrain().getOrientation(),rollingStock.getDirection())){
            DefaultTrack destinationTrack = null;

            if(t.getTrain().getOrientation() && rollingStock.getDirection() || !t.getTrain().getOrientation() && !rollingStock.getDirection()){
                destinationTrack = tracks[curTrack.getTo()];
            }
            else{
                if(tracks[curTrack.getFrom()] instanceof JunctionTrack){
                    JunctionTrack jt = (JunctionTrack)tracks[curTrack.getFrom()];
//                    if(curTrack.getId() == tracks[jt.getToNotThrownTrack()].getId()){
//                        destinationTrack = tracks[jt.getToNotThrownTrack()];
//                    }
//                    else{
//                        destinationTrack = tracks[jt.getThrownTrack()];
//                    }
                }
                else {
                    destinationTrack = tracks[curTrack.getFrom()];
                }
            }
            // Sets the next track
            rollingStock.setCurTrack(destinationTrack);
        }
    }


    /**
     * Checks if a drawable train is on a track given after it has moved a certain amount based on its speed
     * changes the trains track is no longer on the same track and send this information to the controller.
     * */
    public void onSectionCheck(DrawableTrain t){
        DrawableSection curSection = t.getCurSection();
        DefaultTrack curTrack = t.getCurTrack();

        double speed = t.getTrain().getSpeed();
        long curTime = System.currentTimeMillis();
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*speed;
        lastUpdate = System.currentTimeMillis();

        if(t.getRollingStockConnected() !=null){
            //onSectionCheck(t.getRollingStockConnected(), pixelsToMove);
        }

        if(curTrack instanceof JunctionTrack){
            onSectionCheckJunction(t,pixelsToMove,(JunctionTrack)curTrack);
            return;
        }

        if(!curTrack.checkOnAfterUpdate(t.getCurrentLocation(),t.lastPointOnCurve,pixelsToMove,t.getTrain().getOrientation(),t.getTrain().getDirection())){

            DefaultTrack destinationTrack = null;

            if(t.getTrain().getOrientation() && t.getTrain().getDirection() || !t.getTrain().getOrientation() && !t.getTrain().getDirection() ){
                destinationTrack = tracks[curTrack.getTo()];
            }
            else{
                destinationTrack = tracks[curTrack.getFrom()];
            }

            // Sets the next track
            t.setCurTrack(destinationTrack);

            if(destinationTrack instanceof JunctionTrack){
                JunctionTrack jt = (JunctionTrack)destinationTrack;
                if(t.getTrain().getOrientation() && t.getTrain().getDirection() || !t.getTrain().getOrientation() && !t.getTrain().getDirection() ){
                    t.setJuncTrack(jt.getToTrack());
                }
                else {
                    System.out.println("Not going forward " + jt.getFromTrack());
                    t.setJuncTrack(jt.getFromTrack());
                }
            }

            // Check if the track it moves to is in a different section
            checkSectionChangedEvent(t,curSection,destinationTrack);
        }
    }

    public void checkSectionChangedEvent(DrawableTrain t,DrawableSection curSection, DefaultTrack destinationTrack){

        // Check if the current section contains the new track to move to
        if(!curSection.containsTrack(destinationTrack)){
            // Does not contain the track so have to update cur track

            //find where it belongs to
            for(DrawableSection ds : railway){
                if(ds.containsTrack(destinationTrack)){
                    t.setCurSection(ds);//have to do it this way since the destination is not always the same
                    if(ds.getSection().canDetect()){
                        eventLog.appendText(updateTrainOnSection(t.getTrain(), ds.getSection(),curSection.getSection()));
                        modelTrack.sectionChanged(curSection.getSection().getID());
                    }
                }
            }
        }
    }


    /**
     * Called when user presses the event button the send an event to a train
     * */
    public void startEventDialog(){
        new EventGen(modelTrack);
    }

    /**
     * Stops the simulation
     * */
    public void stopSimulation(){
        started = false;
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
        started = true;
        this.modelTrack = new ModelTrack(getTrains(), getSections());
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
                Train train = new Train(1, 50, 120, true,true);

                //RollingStock rollingStock = new RollingStock(100,828282);

                // Create the drawable train
                DrawableTrain drawableTrain = new DrawableTrain(train, ds,ds.getTracks()[0]);

               // DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock,drawableTrain,drawableTrain.getTrain().getDirection());
                //drawableRollingStock.setStart(drawableTrain.getCurrentLocation(),this);

                //drawableTrain.setRollingStockConnected(drawableRollingStock);

                trains.add(drawableTrain);
            }
        }
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

    /**
     * Returns if the log is showing or not
     * */
    public boolean logShowing(){
        return this.logShown;
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
     * Creates the buttons for the visualisation and sets up the listners
     * */
    private VBox getVisualisationButtons(){
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(5,5,5,5));
        Button sim = new Button("Start Simulation");
        Button stop = new Button("Stop");
        Button pause = new Button("Pause");
        Button event = new Button("Event");
        Button toggleJunc = new Button("Toggle Junction");


        //Starts the simulation
        sim.setOnAction(e -> startSimulation());
        stop.setOnAction(e -> stopSimulation());
        pause.setOnAction(e -> pause());
        event.setOnAction(e -> startEventDialog());
        toggleJunc.setOnAction(e -> toggleJunction());

        vBox.getChildren().addAll(sim,stop, pause,event,toggleJunc);
        vBox.setPrefWidth(WIDTH);

        return vBox;
    }

    public void toggleJunction(){
        for(DrawableSection ds : railway){
            for(DefaultTrack dt : ds.getTracks()){
                if(dt instanceof JunctionTrack){
                    JunctionTrack junk = (JunctionTrack)dt;
                    junk.setThrown(!junk.getThrown());
                }
            }
        }
    }

    public void clickTogleJunction(double x, double y){
        for(DefaultTrack t : tracks){
            if(t.containsPoint(x,y)){
                if(t instanceof JunctionTrack){
                    ((JunctionTrack) t).setThrown(!((JunctionTrack) t).getThrown());
                    System.out.println("Toggling");
                }
            }
        }
    }

    public String updateTrainOnSection(Train t, Section newSection, Section prevSection){
        return ("Train ID:" + t.getId() + " Changed from ID:" + prevSection.getID() + " To section ID:" + newSection.getID() + "\n \n");
    }

    // Where mouse events will be generated might use later for something

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {
        if(e.getButton().equals(MouseButton.PRIMARY)){
            if(e.getClickCount() == 2){
                clickTogleJunction(x,y);
            }
        }
    }


    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {}

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {}

}
