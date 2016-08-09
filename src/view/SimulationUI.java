package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.*;
import save.LoadedRailway;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.DefaultTrack;
import view.Panes.ErrorDialog;
import view.Panes.EventGen;
import view.Panes.EventLog;
import view.Panes.TrackMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nicky on 24/05/2016.
 */
public class SimulationUI implements MouseEvents{

    // Width of buttons
    public static final int WIDTH = 150;
    public static final int RAIL_SEP = 12;

    // Gui pane to send events to for user
    private EventLog eventLog;

    // buttons for visualisation
    private VBox vBox;

    // Is the log currently shows on by def
    private boolean logShown;

    // The simulation
    private Simulation sim;

    private String lastEvent;

    private DrawableTrain selectedTrain;

    private List<Slider> physicsSliders;
    private List<Label> physicsLabels;

    private Canvas canvas;



    /**
     * Constructs a new visualisation object with a default track and trains
     * */
    public SimulationUI(){
        this.pyhsSliders = false;
        this.physicsSliders = getPhysicsSliders();
        this.physicsLabels = getPhysicsLabels();
        this.sim = new Simulation(this);
        sim.setDefault();
//        sim.controlMode();
        this.logShown = false;
        this.vBox = getVisualisationButtons();
        this.eventLog = new EventLog();
    }

    public void setCanvas(Canvas canvas){
        this.canvas = canvas;
    }

    /**
     * Displays the events on the log on the screen if the log is showing
     *
     * @param event string representing the event
     * */
    public void sendToeventLog(String event, int status){
        if(logShown){
            if(event.equals(lastEvent))return;
            lastEvent = event;
            eventLog.appendText(event);
            if(status == 1){
                eventLog.setStyle("-fx-text-fill: black; -fx-font-size: 12;");
            }
            else if(status == 2){
                eventLog.setStyle("-fx-text-fill: red; -fx-font-size: 12;");
            }
        }
    }


    public String getSelectedMode(){
        return this.selectedMode;
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
        drawTrainInfoPanel(g);
    }

    /**
     * Called when user presses the event button the send an event to a train
     * */
    public void startEventDialog(){
        if(sim.getStarted() == false){
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
        TrackMenu menu = new TrackMenu(dt, sim);

        // Checks if a train should be added to the track
        if(menu.addTrain()){
            String selectedTrain = menu.getCurTrainSelection();
            if(selectedTrain.equals("British Rail Class 25")){
                Train train1 = new Train(menu.getId(), 15, 500, true, menu.naturalOrientation(),71000);
                DrawableTrain drawableTrain1 = new DrawableTrain(train1, sim.getSection(dt),dt);

                sim.addTraintoSimulation(drawableTrain1,menu.getNumbRollingStock());
            }
            else if(selectedTrain.equals("British Rail Class 108 (DMU)")){

            }
            else if(selectedTrain.equals("British Rail Class 101 (DMU)")){

            }
        }

        // Checks if a rolling stock should be added to the track
        if(menu.addRollingStocl()){
            // Rolling stock should be added TODO WHEN THERE IS NOT TRAIN TO ADD ONLY STOCK
        }
    }

    private String selectedMode;

    final ToggleGroup mode = new ToggleGroup();

    public void setSelectedMode(String mode){

        if(mode.contains("Test")){
            selectedMode = Simulation.MODE_TEST;
        }
        else if(mode.contains("Controller")){
            selectedMode = Simulation.MODE_CONTROLLER;
        }
        else if(mode.contains("User")){
            selectedMode = Simulation.MODE_USER;
        }
    }

    /**
     * Creates the buttons for the visualisation and sets up the listners
     * */
    private VBox getVisualisationButtons(){
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(5,5,5,5));
        vBox.setStyle("-fx-background-color: #414A4C;");

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

        vBox.getChildren().addAll(user, test,controller);
        vBox.setPrefWidth(WIDTH);

        return vBox;
    }

    private boolean pyhsSliders = false;

    public void showPhysicsSliders(BorderPane borderPane){
        if(pyhsSliders){
            pyhsSliders = false;
            borderPane.getChildren().remove(vBox);
            canvas.setWidth(canvas.getWidth() + WIDTH);
            for(int i = 0; i < physicsSliders.size(); i++){
                vBox.getChildren().remove(physicsLabels.get(i));
                vBox.getChildren().remove(physicsSliders.get(i));
            }
        }
        else {
            pyhsSliders = true;
            borderPane.setLeft(vBox);
            canvas.setWidth(canvas.getWidth() - WIDTH);
            for(int i = 0; i < physicsSliders.size(); i++){
                vBox.getChildren().add(physicsLabels.get(i));
                vBox.getChildren().add(physicsSliders.get(i));
            }

        }
    }

    public void setSelectedTrain(DrawableTrain train){
        this.selectedTrain = train;
    }

    public void drawTrainInfoPanel(GraphicsContext g){
        if(selectedTrain == null)return;
        int startX =1000;
        int startY = 20;

        g.setFill(Color.BLACK);
        g.fillRect(startX,startY,200,150);
        g.setFill(new Color(0,0,0.96,1));
        g.setLineWidth(1);
        g.strokeRect(startX,startY,200,150);

        g.strokeText("Crashed: " + selectedTrain.isCrashed(),startX+5,startY+=15);
        g.strokeText("Direction: " + selectedTrain.getDirection(),startX+5,startY+=15);
        g.strokeText("Weight: " + selectedTrain.getTrain().getWeight(),startX+5,startY+=20);
        g.strokeText("Engine Power : " + selectedTrain.getEngineForce(),startX+5,startY+=20);

        g.strokeText("Target Speed m/s: " + String.format("%.2f", selectedTrain.getTrain().getTargetSpeed()), startX + 5, startY += 20);
        g.strokeText("Speed m/s: " + String.format("%.2f", selectedTrain.getCurrentSpeed()), startX + 5, startY += 20);
        g.strokeText("Acceleration m/s: " + String.format("%.2f", selectedTrain.getAcceleration()),startX+5,startY+=20);

    }

    private List<Slider> getPhysicsSliders(){
        List<Slider> sliders = new ArrayList<>();

        Slider friction = new Slider();
        friction.setMin(0);
        friction.setMax(1);
        friction.setValue(0.5);
        friction.setShowTickLabels(true);
        friction.setShowTickMarks(true);
        friction.setMajorTickUnit(5);
        friction.setMinorTickCount(10);
        friction.setBlockIncrement(0.2);

        Slider trainWeight = new Slider();
        trainWeight.setMin(0);
        trainWeight.setMax(100000);
        trainWeight.setValue(50000);
        trainWeight.setShowTickLabels(true);
        trainWeight.setShowTickMarks(true);
        trainWeight.setMajorTickUnit(20000);
        trainWeight.setMinorTickCount(10);
        trainWeight.setBlockIncrement(10000);

        Slider trainpower = new Slider();
        trainpower.setMin(0);
        trainpower.setMax(80000);
        trainpower.setValue(40000);
        trainpower.setShowTickLabels(true);
        trainpower.setShowTickMarks(true);
        trainpower.setMajorTickUnit(20000);
        trainpower.setMinorTickCount(10);
        trainpower.setBlockIncrement(20000);

        sliders.add(trainpower);
        sliders.add(trainWeight);
        sliders.add(friction);

        return sliders;
    }

    public List<Label> getPhysicsLabels(){
        List<Label> labels = new ArrayList<>();

        Label trainThrottle = new Label("Train Throttle ");
        Label trainWeight = new Label("Train Weight:");
        Label trackFriction = new Label("Track Friction:");

        labels.add(trainThrottle);
        labels.add(trainWeight);
        labels.add(trackFriction);

        return labels;
    }

    public void startWithLoadedRailway(File file, LoadedRailway railway){
        sim.loadRailway(file, railway);
    }


    /**
     * Adds the UI elements for the visualisation from the pane. Used when switching mode
     * */
    public void addUIElementsToLayout(BorderPane bp){
        if(logShowing()){
            bp.setRight(eventLog);
        }
        if(pyhsSliders){
            bp.setLeft(vBox);
        }
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
            canvas.setWidth(canvas.getWidth()+eventLog.WIDTH);
            logShown = !logShown;
        }
        else {
            canvas.setWidth(canvas.getWidth()-eventLog.WIDTH);
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

        }
        else if(code.equals("DOWN")){

        }
    }

    public Simulation getSim(){
        return this.sim;
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
