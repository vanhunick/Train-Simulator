package view;

import Test.TestSimpleTrack;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import model.Section;
import model.Train;
import view.Drawable.DrawableSection;
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


    List<DefSection> simpleTrack;
    TrackBuilder trackBuilder;

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Train Simulator");

        trackBuilder = new TrackBuilder();
        visualisation = new Visualisation();

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.rgb(0, 0, 0));
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setWidth(SCREEN_WIDTH);


        //Create the custom menu bar
        TopMenuBar topMenuBar = new TopMenuBar();


        BorderPane bl = new BorderPane();
        bl.setTop(topMenuBar);


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
        Canvas canvas = new Canvas();

        // bind the dimensions when the user resizes the window.
        canvas.widthProperty().bind(primaryStage.widthProperty());
        canvas.heightProperty().bind(primaryStage.heightProperty());

        // obtain the GraphicsContext (drawing surface)
        final GraphicsContext gc = canvas.getGraphicsContext2D();


        // create an animation (update & render loop)
        new AnimationTimer() {
            @Override
            public void handle(long now) {

                // clear screen
                gc.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());

                //redraw all elements on the screen
                if(mode.equals("Simulation")){
                    visualisation.draw(gc);
                }
                else if(mode.equals("Builder")){
                    trackBuilder.draw(gc);
                }

                visualisation.update();

                gc.save();
                gc.restore();
            }
        }.start();

        // add the single node onto the scene graph

        // Set the canvas to the center of the border layout
        bl.setCenter(canvas);

        // Add the layout to the root
        root.getChildren().add(bl);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void setMode(String modeToSet){
        mode = modeToSet;
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
