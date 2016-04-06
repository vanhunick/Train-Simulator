package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.input.MouseEvent;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.DefSection;

import java.util.List;

public class Controller implements MouseEvents {

    public static final String VISUALISATION_MODE = "visualisation_mode";
    public static final String BUILDER_MODE = "builder_mode";

    private String mode;

    private Visualisation visualisation;
    private TrackBuilder trackBuilder;
    private MouseEvents curMode;

    private BorderPane borderPane;

    public Controller (){


    }

    public void setDefMode(BorderPane borderPane){
        visualisation = new Visualisation();// Since by default it starts in this mode
        visualisation.addUIElementsToLayout(borderPane);

        // By default add one train
        visualisation.testMovement();
        setMode(Controller.VISUALISATION_MODE);
    }

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

    public void setVisualisationMode(List<DefSection> track, List<DrawableTrain> trains){
        visualisation = new Visualisation();
        visualisation.setRailway(track);
        visualisation.setTrains(trains);
        trackBuilder.removeUIElementsFromLayout(borderPane);
        visualisation.addUIElementsToLayout(borderPane);

    }


    public void setBuilderMode(){
        this.trackBuilder = new TrackBuilder(this);// Pass the controller to the builder

        visualisation.removeUIElementsFromLayout(borderPane);
        trackBuilder.addUIElementsToLayout(borderPane);
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

    public double getCanvasWidth(){
        System.out.println(borderPane.getCenter().getLayoutBounds().getWidth());
        return borderPane.getCenter().getLayoutBounds().getWidth();
    }

    public double getCanvasHeight(){
        System.out.println(borderPane.getCenter().getLayoutBounds().getHeight());
        return borderPane.getCenter().getLayoutBounds().getHeight();
    }
}
