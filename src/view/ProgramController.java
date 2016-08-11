package view;

import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import save.Load;
import save.LoadedRailway;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.DrawableSection;
import view.Panes.ErrorDialog;
import view.Panes.EventLog;
import view.Panes.TopToolBar;

import java.io.File;
import java.util.List;

public class ProgramController implements MouseEvents {

    // The possible modes in the program
    public static final String VISUALISATION_MODE = "visualisation_mode";
    public static final String BUILDER_MODE = "builder_mode";

    // The current mode
    private String mode = "NO_MODE";

    // The modes
    private SimulationUI simulationUI;
    private TrackBuilder trackBuilder;

    // The place to send the mouse events to
    private MouseEvents curMode;

    // Used for adding and removing the UI elements for different modes
    private BorderPane borderPane;

    private TopToolBar toolBar;
    private Canvas canvas;


    public ProgramController(){
        trackBuilder = new TrackBuilder(this);
        simulationUI = new SimulationUI();
    }

    public void setSimulationMode(String mode){
        if(!mode.equals(VISUALISATION_MODE))return;// TODO not in vis mode

        simulationUI.setSelectedMode(mode);
    }

    // Canvas

    /**
     * The default mode is the simulationUI mode with it default track and trains
     * */
    public void setDefMode(BorderPane borderPane, Canvas canvas){
        this.canvas = canvas;
        simulationUI.setCanvas(canvas);
//        simulationUI.addUIElementsToLayout(borderPane);
        setMode(ProgramController.VISUALISATION_MODE);
    }

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

            setVisualisationMode(null,null,null);
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
        else {
            //Error invalid mode
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
    public void setVisualisationMode(DrawableSection[] track, List<DrawableTrain> trains, List<DrawableRollingStock> stocks){
        trackBuilder.removeUIElementsFromLayout(borderPane);
        simulationUI.addUIElementsToLayout(borderPane);

        if(simulationUI.logShowing()){
            canvas.setWidth(canvas.getWidth() - EventLog.WIDTH);
        }

        // Enable relevant buttons
        toolBar.enableButtons();
        toolBar.disableBuilderButtons();
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

    public void setLoadedRailway(File file, LoadedRailway railway){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.startWithLoadedRailway(file, railway);
        }
    }

    public void handlePhysicsPressed(ActionEvent e){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.showPhysicsSliders(borderPane);
        }
        else{
            new ErrorDialog("You need to be in the Simulation mode to change physics settings", "Invalid Mode");
        }
    }


    public void setModeOfSimulation(String mode){
        if(!mode.equals(ProgramController.VISUALISATION_MODE))return;

        simulationUI.getSim().setMode(mode);
    }

    public void setToolBar(TopToolBar toolBar){
        this.toolBar = toolBar;
    }

    public void keyPressed(String code){
        curMode.keyPressed(code);
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

    public void setBorderPane(BorderPane borderPane){this.borderPane = borderPane;}

    public void toggleLogView(){
        if(mode.equals(VISUALISATION_MODE)){
            simulationUI.toggleLog(borderPane);
        }
    }

    public  BorderPane getBorderPane(){return this.borderPane;}

    public double getCanvasWidth(){
        return borderPane.getCenter().getLayoutBounds().getWidth();
    }

    public double getCanvasHeight(){
        return borderPane.getCenter().getLayoutBounds().getHeight();
    }

    public SimulationUI getSimulationUI(){return this.simulationUI;}

    public TrackBuilder getTrackBuilder(){
        return this.trackBuilder;
    }

    public String gerMode(){
        return this.mode;
    }
}
