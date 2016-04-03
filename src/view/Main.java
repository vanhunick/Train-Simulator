package view;

import Test.TestSimpleTrack;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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


    private TrackBuilder trackBuilder;
    private static BorderPane bl;

    @Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("Train Simulator");

        bl = new BorderPane();

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.rgb(0, 0, 0));
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setWidth(SCREEN_WIDTH);


        //Create the custom menu bar
        TopMenuBar topMenuBar = new TopMenuBar(this);

        trackBuilder = new TrackBuilder(this);
        visualisation = new Visualisation(trackBuilder);
        bl.setTop(topMenuBar);//Need to add first as it is being used to calculate offset



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
        visualisation.addUIElementsToLayout(bl);//Add UI elements since it default

        // obtain the GraphicsContext (drawing surface)
        final GraphicsContext gc = canvas.getGraphicsContext2D();


        // create an animation (update & render loop)
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println(bl.getLeft().getLayoutBounds().getWidth());
                // Clear the screen
                gc.setStroke(Color.BLACK);
                gc.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());


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



        // add the single node onto the scene graph

        // Set the canvas to the center of the border layout
        bl.setCenter(canvas);

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

    public void setVisualisationWithTrack(List<DefSection> track){
        visualisation.setRailway(track);
        trackBuilder.removeUIElementsFromLayout(bl);
        visualisation.addUIElementsToLayout(bl);
        mode = "Simulation";
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
