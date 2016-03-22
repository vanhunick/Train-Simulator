package view;

import Test.TestSimpleTrack;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.Section;
import model.Train;
import view.Drawable.DrawableSection;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    public static final  int SCREEN_WIDTH = 800;
    public static final  int SCREEN_HEIGHT = 500;

    ArrayList<String> input = new ArrayList<>();

    private ViewLogic viewLogic;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        TestSimpleTrack tst = new TestSimpleTrack();



        viewLogic = setupBasicExample();

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.rgb(0, 0, 0));
        primaryStage.setHeight(1000);
        primaryStage.setWidth(1500);
        //responds to a key being pressed
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
        scene.setOnMousePressed(e -> viewLogic.mousePressed(e.getX(), e.getY()));
        scene.setOnMouseReleased(e -> viewLogic.mouseReleased(e.getX(), e.getY()));
        scene.setOnMouseClicked(e -> viewLogic.mouseClicked(e.getX(), e.getY()));
        scene.setOnMouseMoved(e -> viewLogic.mouseMoved(e.getX(), e.getY()));
        scene.setOnMouseDragged(e -> viewLogic.mouseDragged(e.getX(), e.getY()));

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

                Line line = new Line();
                line.setEndX(3);
                line.setStrokeWidth(20);

                // clear screen
                gc.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());


                //redraw all elements on the screen
                viewLogic.refresh(gc);

                viewLogic.update();
                // save the origin or the current state of the Graphics Context.
                gc.save();

                // reset Graphics Context to last saved point.
                gc.restore();
            }
        }.start();

        // add the single node onto the scene graph
        root.getChildren().add(canvas);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public ViewLogic setupBasicExample(){
        TestSimpleTrack tst = new TestSimpleTrack();
        Section[] sections = tst.createTrack(10);
        Train t = tst.createTrain();

        DrawableSection[] ds = new DrawableSection[sections.length];

        // Create the drawable train and sections
//        for(int i = 0; i < sections.length; i++){
//            if(sections[i].getID() == 0 || sections[i].getID() == 1 || sections[i].getID() == 3 || sections[i].getID() == 5 || sections[i].getID() == 6 || sections[i].getID() == 8){
//                ds[i] = new DrawableSection(sections[i],100,100);
//            }
//        }

        return null;
    }
}
