package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import save.Load;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.DrawableSection;
import view.Panes.EventLog;

import java.util.List;

public class ProgramController implements MouseEvents {

    // The possible modes in the program
    public static final String VISUALISATION_MODE = "visualisation_mode";
    public static final String BUILDER_MODE = "builder_mode";

    // The current mode
    private String mode;

    // The modes
    private SimulationUI visualisation;
    private TrackBuilder trackBuilder;

    // The place to send the mouse events to
    private MouseEvents curMode;

    // Used for adding and removing the UI elements for different modes
    private BorderPane borderPane;


    // Canvas
    private Canvas canvas;
    /**
     * The default mode is the visualisation mode with it default track and trains
     * */
    public void setDefMode(BorderPane borderPane, Canvas canvas){
        visualisation = new SimulationUI();
        this.canvas = canvas;
        visualisation.addUIElementsToLayout(borderPane);

        setMode(ProgramController.VISUALISATION_MODE);
    }


    /**
     * Sets the current mode of the program
     *
     * @param  modeToSet the mode to change to
     * */
    public void setMode(String modeToSet){
        if(modeToSet.equals(mode))return;//mode passed in already set

        if(modeToSet.equals(VISUALISATION_MODE)){
            this.mode = VISUALISATION_MODE;
            curMode = visualisation;
        }
        else if(modeToSet.equals(BUILDER_MODE)){
            this.mode = BUILDER_MODE;
            setBuilderMode();
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
            visualisation.update();
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
            visualisation.refresh(g);
        }
        else if(mode.equals(BUILDER_MODE)){
            trackBuilder.refresh(g);
        }
    }


    /**
     * Sets the mode of the program by removing UI elements from other mode and adding it's own.
     * */
    public void setVisualisationMode(DrawableSection[] track, List<DrawableTrain> trains){
        canvas.setWidth(canvas.getWidth() - EventLog.WIDTH);

        visualisation = new SimulationUI();

        trackBuilder.removeUIElementsFromLayout(borderPane);
        visualisation.addUIElementsToLayout(borderPane);
        setMode(VISUALISATION_MODE);
    }


    /**
     * Sets the mode of the program by removing UI elements from other mode and adding it's own.
     * */
    public void setBuilderMode(){
        this.trackBuilder = new TrackBuilder(this);// Pass the controller to the builder
        visualisation.removeUIElementsFromLayout(borderPane);
        trackBuilder.addUIElementsToLayout(borderPane);

        if(visualisation.logShowing()){
            canvas.setWidth(canvas.getWidth() + EventLog.WIDTH);// WHY 20?????
        }


        setMode(BUILDER_MODE);
    }

    public void setLoadedRailway(Load.LoadedRailway railway){
        if(mode.equals(VISUALISATION_MODE)){
            visualisation.startWithLoadedRailway(railway);
        }
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
            visualisation.toggleLog(borderPane);

            if(visualisation.logShowing()){
                canvas.setWidth(canvas.getWidth() - EventLog.WIDTH);
            }
            else {
                canvas.setWidth(canvas.getWidth() + EventLog.WIDTH);
            }

        }
    }

    public  BorderPane getBorderPane(){return this.borderPane;}

    public double getCanvasWidth(){
        return borderPane.getCenter().getLayoutBounds().getWidth();
    }

    public double getCanvasHeight(){
        return borderPane.getCenter().getLayoutBounds().getHeight();
    }

    public SimulationUI getVisualisation(){return this.visualisation;}
}
