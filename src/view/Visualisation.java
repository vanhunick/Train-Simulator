package view;

import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.ModelTrack;
import model.Section;
import model.Train;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.*;
import view.Panes.EventGen;
import view.Panes.EventLog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Visualisation implements MouseEvents {

    //Width of
    public static final int WIDTH = 150;

    // Model to send events to
    private ModelTrack modelTrack;
    private boolean started = false;
    private  EventLog eventLog;

    // Train and track
    private List<DrawableTrain> trains;
    private List<DefSection> railway;

    // buttons for visualisation
    private VBox vBox;

    // Last time the logic was updated
    private long lastUpdate;


    /**
     * Constructs a new visualisation object with a default track and trains
     * */
    public Visualisation(){
        this.vBox = getVisualisationButtons();
        this.eventLog = new EventLog();

        // Set the default track
        this.trains = new ArrayList<>();
        this.railway = createBasicTrack();
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

        // Draw the track
        for(DefSection d : railway){
            d.draw(g);
        }

        //Draw the trains
        for(DrawableTrain t : trains){
            t.draw(g);
        }
    }

    /**
     * Checks if a drawable train is on a track given after it has moved a certain amount based on its speed
     * changes the trains track is no longer on the same track and send this information to the controller.
     * */
    public void onSectionCheck(DrawableTrain t){
        DefSection curSection = t.getCurSection();
        double speed = t.getTrain().getSpeed();

        if(lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }

        long curTime = System.currentTimeMillis();
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*speed;
        lastUpdate = System.currentTimeMillis();

        // Checks if the train will still be on the same track after moving if not update the current track
        if(!curSection.checkOnAfterUpdate(t.getCurentLocation(),t.lastPointOnCurve,pixelsToMove)){

            for(int i = 0; i < railway.size(); i++){

                // Check if the current track
                if(railway.get(i).getSection().getID() == curSection.getSection().getID()){

                    // Notifies the model the section has changed state
                    if(curSection.getSection().canDetect()){
                        System.out.println(curSection.getSection());
                        modelTrack.sectionChanged(curSection.getSection().getID());
                    }


                    // Check if the current railway is the last in the list
                    if(i == railway.size() -1){

                        // Tell the first section in the railway it has changed
                        if(railway.get(0).getSection().canDetect()){
                        modelTrack.sectionChanged(railway.get(0).getSection().getID());
                        }

                        // Grab the string representing the event and sent to event log
                        eventLog.appendText(modelTrack.updateTrainOnSection(t.getTrain(),railway.get(0).getSection(),curSection.getSection()));

                        //Set the current section for the track
                        t.setCurSection(railway.get(0));
                    }
                    else {
                        // The current section is not the last in the list so just increment section

                        if(railway.get(i+1).getSection().canDetect()){
                        modelTrack.sectionChanged(railway.get(i+1).getSection().getID());
                        }

                        eventLog.appendText(modelTrack.updateTrainOnSection(t.getTrain(),railway.get(i+1).getSection(),curSection.getSection()));
                        t.setCurSection(railway.get(i+1));
                    }
                    return;
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

    //TODO make it do what is says it does
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
        for(DefSection d : railway){
            System.out.println("Section " + d.getSection());
        }
        this.modelTrack = new ModelTrack(getTrains(), new TrackBuilder(null).linkUpSections(railway));
        lastUpdate = System.currentTimeMillis();
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
     * Adds some default trains train to the starting track
     * */
    public void addDefaultTrains(){
        // Add a train to the track
        for(DefSection ds : railway) {
            if (ds.getSection().getID() == 2) {
                //Create the train
                Train train = new Train(1, 50, 120, 1, true);

                // Create the drawable train
                DrawableTrain drawableTrain = new DrawableTrain(train, ds);
                trains.add(drawableTrain);
            }

            if (ds.getSection().getID() == 1) {
                //Create the train
                Train train = new Train(2, 50, 120, 1, true);

                // Create the drawable train
                DrawableTrain drawableTrain = new DrawableTrain(train, ds);
                trains.add(drawableTrain);
            }
        }
    }


    /**
     * Sets the railway to draw
     * */
    public void setRailway(List<DefSection> rail){
        this.railway = rail;
    }


    /**
     * Sets the trains on the track
     * */
    public void setTrains(List<DrawableTrain> trains){
        this.trains = trains;
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

        //Starts the simulation
        sim.setOnAction(e -> startSimulation());
        stop.setOnAction(e -> stopSimulation());
        pause.setOnAction(e -> pause());
        event.setOnAction(e -> startEventDialog());

        vBox.getChildren().addAll(sim,stop, pause,event);
        vBox.setPrefWidth(WIDTH);

        return vBox;
    }

    /**
     * Creates a list of section to use for a defualt track on startup
     * */
    public ArrayList<DefSection> createBasicTrack(){
        ArrayList<DefSection> sections = new ArrayList<>();

        DefSection ds1 = new StraightHoriz(new Section(1, 100, null, null, null), 200, 100, 100,0, "RIGHT");
        DefSection ds2 = new StraightHoriz(new Section(2, 100, null, null, null), 100,0);
        ds1.getSection().setCandetect(true);
        ds2.getSection().setCandetect(false);
        ds2.setStart(ds1);


        DefSection ds3 = new Quart2(new Section(3, 100, null, null, null), 200,2);
        ds3.getSection().setCandetect(true);
        ds3.setStart(ds2);

        DefSection ds4 = new StraightVert(new Section(4, 100, null, null, null), 100,5);
        ds4.getSection().setCandetect(false);
        ds4.setStart(ds3);

        DefSection ds5 = new Quart3(new Section(5, 100, null, null, null), 200,3);
        ds5.getSection().setCandetect(true);
        ds5.setStart(ds4);

        DefSection ds6 = new StraightHoriz(new Section(6, 100, null, null, null), 100,0);
        ds6.getSection().setCandetect(false);
        ds6.setStart(ds5);

        DefSection ds7 = new StraightHoriz(new Section(7, 100, null, null, null), 100,0);
        ds7.getSection().setCandetect(true);
        ds7.setStart(ds6);

        DefSection ds8 = new Quart4(new Section(8, 100, null, null, null), 200,4);
        ds8.getSection().setCandetect(false);
        ds8.setStart(ds7);

        DefSection ds9 = new StraightVert(new Section(9, 100, null, null, null), 100,5);
        ds9.getSection().setCandetect(true);
        ds9.setStart(ds8);

        DefSection ds10 = new Quart1(new Section(10, 100, null, null, null), 200,1);
        ds10.getSection().setCandetect(false);
        ds10.setStart(ds9);

        sections.add(ds1);
        sections.add(ds2);
        sections.add(ds3);
        sections.add(ds4);
        sections.add(ds5);
        sections.add(ds6);
        sections.add(ds7);
        sections.add(ds8);
        sections.add(ds9);
        sections.add(ds10);

        new TrackBuilder(null).linkUpSections(sections);

        return sections;
    }

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {}

    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {}

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {}

}
