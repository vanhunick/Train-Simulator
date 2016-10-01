package simulation;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import simulation.ui.SimulationUI;
import util.save.LoadedRailway;
import simulation.Drawable.DrawableRollingStock;
import simulation.Drawable.DrawableTrain;
import simulation.Drawable.tracks.DrawableSection;
import simulation.ui.ErrorDialog;
import simulation.ui.EventLog;
import simulation.ui.TopToolBar;

import java.io.File;
import java.util.List;

public class ProgramController implements MouseEvents {

    // The possible modes in the program
    public static final String VISUALISATION_MODE = "visualisation_mode";
    public static final String BUILDER_MODE = "builder_mode";

    private String mode = "NO_MODE"; // The current mode

    private SimulationUI simulationUI; // The user interface of the simulation
    private TrackBuilder trackBuilder;

    private MouseEvents curMode; // The place to send the mouse events to

    private BorderPane borderPane; // Used for adding and removing the UI elements for different modes

    private TopToolBar toolBar; // The top right toolbar with the buttons

    private Canvas canvas; // The canvas the railroad is drawn on

    /**
     * Creates a new Program Controller, used to switch between track builder and simulation modes
     * */
    public ProgramController(){
        trackBuilder = new TrackBuilder(this);
        simulationUI = new SimulationUI();
    }

    /**
     * Sets the mode of the program to the railway simulation
     *
     * @param mode the mode to set
     * */
    public void setSimulationMode(String mode){
        if(!mode.equals(VISUALISATION_MODE))return;// TODO not in vis mode
        simulationUI.setSelectedMode(mode);
    }


    /**
     * The default mode is the simulationUI mode with it default track and trains
     * */
    public void setDefMode(Canvas canvas){
        this.canvas = canvas;
        simulationUI.setCanvas(canvas);
        setMode(ProgramController.VISUALISATION_MODE);
    }


    /**
     * Switches modes from builder mode to simulation with the railway from the builder
     * */
    public boolean setSimulationFromBuilder(){
        LoadedRailway l = trackBuilder.getLoadedRailway();
        if(l != null){
            simulationUI.getSim().setFromBuilderMode(l);
            return true;
        }
        return false;
    }


    /**
     * Sets the current mode of the program
     *
     * @param  modeToSet the mode to change to
     * */
    public void setMode(String modeToSet){
        if(modeToSet.equals(mode))return;//mode passed in already set

        if(modeToSet.equals(VISUALISATION_MODE)){
            curMode = simulationUI;
            // Reset canvas
            canvas.setWidth(Main.SCREEN_WIDTH);
            canvas.setHeight(Main.SCREEN_HEIGHT);

            toolBar.enableButtons();
            toolBar.disableBuilderButtons();
            this.mode = VISUALISATION_MODE;
        }
        else if(modeToSet.equals(BUILDER_MODE)){
            toolBar.enableButtons();
            toolBar.disableSimButtons();
            this.mode = BUILDER_MODE;
            setBuilderMode();

            // Reset canvas
            canvas.setWidth(Main.SCREEN_WIDTH);
            canvas.setHeight(Main.SCREEN_HEIGHT);

            trackBuilder.updateSize();
            this.curMode = trackBuilder;
        }
    }


    /**
     * Updates the part of the program that is in focus
     * */
    public void update(){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.update();
        }
        else if(mode.equals(BUILDER_MODE)){
            trackBuilder.update();
        }
    }


    /**
     * Draws all the elements on the screen for the current mode in focus
     * */
    public void refresh(GraphicsContext g){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.refresh(g);
        }
        else if(mode.equals(BUILDER_MODE)){
            trackBuilder.refresh(g);
        }
    }


    /**
     * Sets the mode of the program by removing UI elements from other mode and adding it's own.
     * */
    public void setBuilderMode(){
        simulationUI.removeUIElementsFromLayout(borderPane);
        trackBuilder.addUIElementsToLayout(borderPane);
        toolBar.enableButtons();
        toolBar.setBuilderButtons();
    }


    /**
     * Starts the simulation with a configuration file or the loadedrailway
     * */
    public void setLoadedRailway(File file, LoadedRailway railway){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.startWithLoadedRailway(file, railway);
        }
    }


    /**
     * Shows the physics sliders on the UI
     * */
    public void handlePhysicsPressed(ActionEvent e){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.showPhysicsSliders(borderPane);
        }
        else{
            new ErrorDialog("You need to be in the Simulation mode to change physics settings", "Invalid Mode");
        }
    }


    /**
     * Called when the save menu item is clicked passes responsibility over to track builder or
     * simulation depending on the current mode.
     * */
    public void handleSavePressed(){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.getSim().save();
        }
        else{
            trackBuilder.save();
        }
    }


    /**
     * Sets the toolbar for the user interface
     * */
    public void setToolBar(TopToolBar toolBar){
        this.toolBar = toolBar;
    }


    /**
     * Passes key press through to the current mode
     * */
    public void keyPressed(String code){
        curMode.keyPressed(code);
    }

    public void toggleLogView(){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.toggleLog(borderPane);
        }
    }

    /**
     * Sets the border pane
     * */
    public void setBorderPane(BorderPane borderPane){this.borderPane = borderPane;}

    /**
     * Returns the user interface of the simulation
     * */
    public SimulationUI getSimulationUI(){return this.simulationUI;}

    /**
     * Returns the track builder
     * */
    public TrackBuilder getTrackBuilder(){
        return this.trackBuilder;
    }

    /**
     * Returns the current mode
     * */
    public String gerMode(){
        return this.mode;
    }

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {
        curMode.mousePressed(x,y,e);
    }

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {
        curMode.mouseReleased(x,y,e);
    }

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {
        curMode.mouseClicked(x,y,e);
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {
        curMode.mouseMoved(x,y,e);
    }

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {
        curMode.mouseDragged(x,y,e);
    }
}
