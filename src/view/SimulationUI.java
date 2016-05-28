package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.*;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.DefaultTrack;
import view.Panes.ErrorDialog;
import view.Panes.EventGen;
import view.Panes.EventLog;
import view.Panes.TrackMenu;


/**
 * Created by Nicky on 24/05/2016.
 */
public class SimulationUI implements MouseEvents{

    //Width of buttons
    public static final int WIDTH = 150;

    // Simulation state
    private boolean started = false;

    // Gui pane to send events to for user
    private EventLog eventLog;

    // buttons for visualisation
    private VBox vBox;

    // Is the log currently shows on by def
    private boolean logShown;

    private Simulation sim;


    /**
     * Constructs a new visualisation object with a default track and trains
     * */
    public SimulationUI(){
        this.sim = new Simulation(this);
        sim.setDefault();
        this.logShown = true;
        this.vBox = getVisualisationButtons();
        this.eventLog = new EventLog();

    }

    public void sendToeventLog(String event){
        if(logShown){
            eventLog.appendText(event);
        }
    }


    /**
     * Updates the trains
     * */
    public void update(){
        sim.update();
    }

    /**
     * Redraws all the elements on the screen
     * */
    public void refresh(GraphicsContext g){

            sim.refresh(g);

    }

    /**
     * Called when user presses the event button the send an event to a train
     * */
    public void startEventDialog(){
        if(started == false){
            new ErrorDialog("Start simulation before sending event", "Event Start Error");
            return;
        }
        new EventGen(sim.getModelTrack());
    }


    /**
     * Pops up a menu where you can add tracks or trains to a track or modify attributes of the track
     *
     * @param dt the track to modify or add a train or stock to
     * */
    public void showTrackMenu(DefaultTrack dt){
        TrackMenu menu = new TrackMenu(dt);

        // Checks if a train should be added to the track
        if(menu.addTrain()){
            String selectedTrain = menu.getCurTrainSelection();
            if(selectedTrain.equals("British Rail Class 25")){
                Train train1 = new Train(sim.getNextTrainID(), 80, 500, true,true, 0.2, 0.5);
                DrawableTrain drawableTrain1 = new DrawableTrain(train1, sim.getSection(dt),dt);
                sim.addTraintoSimulation(drawableTrain1);
            }
            else if(selectedTrain.equals("British Rail Class 108 (DMU)")){

            }
            else if(selectedTrain.equals("British Rail Class 101 (DMU)")){

            }
        }

        // Checks if a rolling stock should be added to the track
        if(menu.addRollingStocl()){
            RollingStock rollingStock = new RollingStock(80,100,0.8);
            DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock);
            drawableRollingStock.setStartNotConnected(dt);
            sim.addRollingStocktoSimulation(drawableRollingStock);
        }
    }

    private String selectedMode;

    final ToggleGroup mode = new ToggleGroup();

    /**
     * Creates the buttons for the visualisation and sets up the listners
     * */
    private VBox getVisualisationButtons(){
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(5,5,5,5));
        Button start = new Button("Start Simulation");
        Button restart = new Button("Restart");
        Button pause = new Button("Pause");
        Button event = new Button("Event");

        RadioButton test = new RadioButton("Test");
        test.setToggleGroup(mode);
        test.setSelected(true);

        RadioButton controller = new RadioButton("DeadLockController");
        controller.setToggleGroup(mode);

        selectedMode = Simulation.MODE_TEST;

        RadioButton user = new RadioButton("User");
        user.setToggleGroup(mode);

        mode.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (mode.getSelectedToggle() != null) {
                    String m = mode.getSelectedToggle().toString();

                    if(m.contains("Test")){
                        selectedMode = Simulation.MODE_TEST;
                    }
                    else if(m.contains("DeadLockController")){
                        selectedMode = Simulation.MODE_CONTROLLER;
                    }
                    else if(m.contains("User")){
                        selectedMode = Simulation.MODE_USER;
                    }
                }
            }
        });


        start.setOnAction(e -> sim.start(selectedMode));
        restart.setOnAction(e -> sim.restart());
        pause.setOnAction(e -> sim.pause());
        event.setOnAction(e -> startEventDialog());

        vBox.getChildren().addAll(start,restart, pause,event, user, test,controller);
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


    /**
     * Returns if the log is showing or not
     * */
    public boolean logShowing(){
        return this.logShown;
    }


    /**
     * Called when a key is pressed
     *
     * @param code the code of the key pressed
     * */
    public void keyPressed(String code){
        if(code.equals("UP")){
            sim.moveTrain(true);
        }
        else if(code.equals("DOWN")){
            sim.moveTrain(false);
        }
    }

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {
        sim.mouseClicked(x,y,e);
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {
        sim.mouseMoved(x,y,e);
    }

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {}

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}
}