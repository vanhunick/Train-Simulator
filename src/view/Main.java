package view;

import Test.TestSimpleTrack;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Section;
import model.Train;
import view.Drawable.DrawableSection;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.*;
import view.Panes.TopMenuBar;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    public static final  int SCREEN_WIDTH = 1200;
    public static final  int SCREEN_HEIGHT = 800;

    ArrayList<String> input = new ArrayList<>();

    private Visualisation visualisation;

    private static String mode = "Simulation";


    private TrackBuilder trackBuilder;
    private static BorderPane bl;


    // Fps counter fields
    private final long ONE_SECOND = 1000000000;
    private long currentTime = 0;
    private long lastTime = 0;
    private int fps = 0;
    private double delta = 0;
    private int fpsText;



    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Train Simulator");



        bl = new BorderPane();
        Canvas canvas = new Canvas();




        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.rgb(0, 0, 0));





        TopMenuBar topMenuBar = new TopMenuBar(this);

        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setWidth(SCREEN_WIDTH);


        //Create the custom menu bar


        trackBuilder = new TrackBuilder(this);
        visualisation = new Visualisation(trackBuilder);

        bl.setTop(topMenuBar);//Need to add first as it is being used to calculate offset
        bl.setCenter(canvas);

//        bl.setCache(true);
//        bl.setCacheHint(CacheHint.SPEED);
//
//        canvas.setCache(true);
//        canvas.setCacheHint(CacheHint.SPEED);
//
//        root.setCache(true);
//        root.setCacheHint(CacheHint.SPEED);



        scene.setOnKeyPressed(
                e -> {
                    String code = e.getCode().toString();

                    // only add once... prevent duplicates
                    if ( !input.contains(code) )
                        input.add( code );
                });
        //responds to a key being released
        scene.setOnKeyReleased(
                e -> {
                    String code = e.getCode().toString();
                    input.remove( code );
                });

        //mouse events
        scene.setOnMousePressed(e -> visualisation.mousePressed(e.getX(), e.getY()));
        scene.setOnMouseReleased(e -> visualisation.mouseReleased(e.getX(), e.getY()));
        scene.setOnMouseClicked(e -> visualisation.mouseClicked(e.getX(), e.getY(),e));
        scene.setOnMouseMoved(e -> visualisation.mouseMoved(e.getX(), e.getY()));
        scene.setOnMouseDragged(e -> visualisation.mouseDragged(e.getX(), e.getY()));

        // create a MainWindow node


        // bind the dimensions when the user resizes the window.
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());
        visualisation.addUIElementsToLayout(bl);//Add UI elements since it default

        // obtain the GraphicsContext (drawing surface)
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        lastTime = System.nanoTime();
        // Set the canvas to the center of the border layout


        // create an animation (update & render loop)
        new AnimationTimer() {
            @Override
            public void handle(long now) {


                currentTime = now;
                fps++;
                delta += currentTime-lastTime;


                if(delta > ONE_SECOND){
                    fpsText = fps;
                    delta -= ONE_SECOND;
                    fps = 0;
                }

                lastTime = currentTime;

                // Clear the screen
                gc.setStroke(Color.BLACK);
                gc.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());
                gc.setStroke(Color.WHITE);

                gc.strokeText("FPS " + fpsText, 0, 20);

                double widthOffset = bl.getLeft().getLayoutBounds().getWidth();
                double heightOffset = bl.getTop().getLayoutBounds().getHeight();
                //redraw all elements on the screen
                if(mode.equals("Simulation")){
                    visualisation.setOffesets(widthOffset,heightOffset);//TODO not sure if this is the right way to go about it
                    visualisation.draw(gc);
                }
                else if(mode.equals("Builder")){
                    trackBuilder.setScreenHeightAndWidth(SCREEN_WIDTH - widthOffset, SCREEN_HEIGHT - heightOffset);//TODO not sure if this is the right way to go about it
                    trackBuilder.draw(gc);
                }
                visualisation.update();

                gc.save();
                gc.restore();
            }
        }.start();

        // Add the layout to the root
        root.getChildren().add(bl);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public void setMode(String modeToSet){
        if(modeToSet.equals("Builder") && !mode.equals(modeToSet)){
            visualisation.removeUIElementsFromLayout(bl);
            trackBuilder.addUIElementsToLayout(bl);

        }
        else if(modeToSet.equals("Simulation") && !mode.equals(modeToSet)){
            trackBuilder.removeUIElementsFromLayout(bl);
            visualisation.addUIElementsToLayout(bl);
        }
        mode = modeToSet;
    }

    public void setVisualisationWithTrack(List<DefSection> track, List<DrawableTrain> trains){
        visualisation.setRailway(track);
        visualisation.setTrains(trains);
        trackBuilder.removeUIElementsFromLayout(bl);
        visualisation.addUIElementsToLayout(bl);
        mode = "Simulation";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
