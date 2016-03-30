package view;

import Test.TestSimpleTrack;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.Section;
import model.Train;
import view.Drawable.DrawableSection;
import view.Drawable.track_types.*;

import java.util.ArrayList;
import java.util.List;


public class Main extends Application {

    public static final  int SCREEN_WIDTH = 1200;
    public static final  int SCREEN_HEIGHT = 800;

    ArrayList<String> input = new ArrayList<>();

    private ViewLogic viewLogic;



    public static void main(String[] args) {
        Application.launch(args);
    }

    DrawableSection ds;
    DrawableSection ds2;
    DrawableSection ds3;
    DrawableSection ds4;
    DrawableSection ds5;
    DrawableSection ds6;
    DrawableSection ds7;
    DrawableSection ds8;
    DrawableSection ds9;
    DrawableSection ds10;
    DrawableSection ds11;
    DrawableSection ds12;
    DrawableSection ds13;
    DrawableSection ds14;

    List<DefSection> situations = new ArrayList<>();
    List<DefSection> sections = new ArrayList<>();

    TrackBuilder trackBuilder;

    @Override
    public void start(final Stage primaryStage) {

        trackBuilder = new TrackBuilder(sections);

        testStraightSituations(situations);
        testVerticalSituations(situations);
        testQuart1Situations(situations);
        testQuart2Situations(situations);

        ds = new DrawableSection(new Section(2, 100, null, null, null), 300, 150, 200,0);
        ds2 = new DrawableSection(new Section(2, 100, null, null, null), 300, 0,ds);
        ds3 = new DrawableSection(new Section(2, 100, null, null, null), 600, 2,ds2);
        ds4 = new DrawableSection(new Section(2, 100, null, null, null), 200, 5,ds3);
        ds5 = new DrawableSection(new Section(2, 100, null, null, null), 400, 3,ds4);
        ds6 = new DrawableSection(new Section(2, 100, null, null, null), 300, 0,ds5);
        ds7 = new DrawableSection(new Section(2, 100, null, null, null), 200, 4,ds6);
        ds8 = new DrawableSection(new Section(2, 100, null, null, null), 200, 2,ds7);
        ds9 = new DrawableSection(new Section(2, 100, null, null, null), 300, 4,ds8);
        ds10 = new DrawableSection(new Section(2, 100, null, null, null), 200, 1,ds9);
        ds11 = new DrawableSection(new Section(2, 100, null, null, null), 200, 3,ds10);
        ds12 = new DrawableSection(new Section(2, 100, null, null, null), 200, 1,ds11);
        ds13 = new DrawableSection(new Section(2, 100, null, null, null), 200, 2,ds12);
        ds14 = new DrawableSection(new Section(2, 100, null, null, null), 200, 4,ds13);

        TestSimpleTrack tst = new TestSimpleTrack();

        viewLogic = setupBasicExample();
        viewLogic.addTrakcBuilder(trackBuilder);

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.rgb(0, 0, 0));
        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setWidth(SCREEN_WIDTH);
        //responds to a key being pressed


        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                trackBuilder.screenWidthChanged(newSceneWidth.doubleValue());
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                trackBuilder.screenHeightChanged(newSceneHeight.doubleValue());
            }
        });


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
//                ds.draw(gc);
//                ds2.draw(gc);
//                ds3.draw(gc);
//                ds4.draw(gc);
//                ds5.draw(gc);
//                ds6.draw(gc);
//                ds7.draw(gc);
//                ds8.draw(gc);
//                ds9.draw(gc);
//                ds10.draw(gc);
//                ds11.draw(gc);
//                ds12.draw(gc);
//                ds13.draw(gc);
//                ds14.draw(gc);

                for(DefSection d : sections){
                    gc.setLineWidth(5);
                    d.draw(gc);
                    gc.setLineWidth(1);
                }

                trackBuilder.draw(gc);



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

    public List<DefSection> testSituations(){
        List<DefSection> sections = new ArrayList<>();

//        DefSection ds = new StraightHoriz(new Section(2, 100, null, null, null), 300, 150, 200,0, "RIGHT");
        DefSection ds = new Quart4(new Section(2, 100, null, null, null), 300, 150, 300,4, "RIGHT");
        DefSection ds2 = new StraightHoriz(new Section(2, 100, null, null, null), 200,0);
        ds2.setStart(ds);

        sections.add(ds);
        sections.add(ds2);

        return sections;
    }

    public List<DefSection> testStraightSituations(List<DefSection> sections){


        int x = 100;

        DefSection ds = new Quart4(new Section(2, 100, null, null, null), x, 100, 150,4, "RIGHT");
        DefSection ds1 = new StraightHoriz(new Section(2, 100, null, null, null), 75,0);
        ds1.setStart(ds);

        sections.add(ds);
        sections.add(ds1);

        x+= 300;

        DefSection ds2 = new Quart1(new Section(2, 100, null, null, null), x, 100, 150,1, "RIGHT");
        DefSection ds3 = new StraightHoriz(new Section(2, 100, null, null, null), 75,0);
        ds3.setStart(ds2);


        sections.add(ds2);
        sections.add(ds3);

        x+=300;

        DefSection ds4 = new Quart2(new Section(2, 100, null, null, null), x, 100, 150,2, "LEFT");
        DefSection ds5 = new StraightHoriz(new Section(2, 100, null, null, null), 75,0);
        ds5.setStart(ds4);


        sections.add(ds4);
        sections.add(ds5);

        x+=300;

        DefSection ds6 = new Quart3(new Section(2, 100, null, null, null), x, 100, 150, 3, "LEFT");
        DefSection ds7 = new StraightHoriz(new Section(2, 100, null, null, null), 75,0);
        ds7.setStart(ds6);


        sections.add(ds6);
        sections.add(ds7);

        return sections;
    }

    public List<DefSection> testVerticalSituations(List<DefSection> sections){


        int x = 100;

        DefSection ds = new Quart2(new Section(2, 100, null, null, null), x, 400, 150,2, "RIGHT");
        DefSection ds1 = new StraightVert(new Section(2, 100, null, null, null), 75,5);
        ds1.setStart(ds);

        sections.add(ds);
        sections.add(ds1);

        x+= 300;

        DefSection ds2 = new Quart1(new Section(2, 100, null, null, null), x, 400, 150,1, "LEFT");
        DefSection ds3 = new StraightVert(new Section(2, 100, null, null, null), 75,5);
        ds3.setStart(ds2);

        sections.add(ds2);
        sections.add(ds3);

        x+= 300;

        DefSection ds4 = new Quart3(new Section(2, 100, null, null, null), x, 400, 150,3, "RIGHT");
        DefSection ds5 = new StraightVert(new Section(2, 100, null, null, null), 75,5);
        ds5.setStart(ds4);

        sections.add(ds4);
        sections.add(ds5);

        x+= 300;

        DefSection ds6 = new Quart4(new Section(2, 100, null, null, null), x, 400, 150,4, "LEFT");
        DefSection ds7 = new StraightVert(new Section(2, 100, null, null, null), 75,5);
        ds7.setStart(ds6);

        sections.add(ds6);
        sections.add(ds7);

        x+= 300;

        return sections;
    }

    public List<DefSection> testQuart1Situations(List<DefSection> sections){


        int x = 100;

        DefSection ds = new Quart3(new Section(2, 100, null, null, null), x, 240, 150,3, "RIGHT");
        DefSection ds1 = new Quart1(new Section(2, 100, null, null, null), 150,1);
        ds1.setStart(ds);

        sections.add(ds);
        sections.add(ds1);

        x += 300;

        DefSection ds2 = new StraightHoriz(new Section(2, 100, null, null, null), x, 250, 150,0, "LEFT");
        DefSection ds3 = new Quart1(new Section(2, 100, null, null, null), 150,1);
        ds3.setStart(ds2);

        sections.add(ds2);
        sections.add(ds3);

        x += 300;

        DefSection ds4 = new Quart2(new Section(2, 100, null, null, null), x, 250, 150,2, "LEFT");
        DefSection ds5 = new Quart1(new Section(2, 100, null, null, null), 150,1);
        ds5.setStart(ds4);

        sections.add(ds4);
        sections.add(ds5);

        x+=300;

        DefSection ds6 = new Quart3(new Section(2, 100, null, null, null), x, 250, 150,3, "LEFT");
        DefSection ds7 = new Quart1(new Section(2, 100, null, null, null), 150,1);
        ds7.setStart(ds6);

        sections.add(ds6);
        sections.add(ds7);

        x+=300;

        DefSection ds8 = new Quart4(new Section(2, 100, null, null, null), x, 250, 150,4, "LEFT");
        DefSection ds9 = new Quart1(new Section(2, 100, null, null, null), 150,1);
        ds9.setStart(ds8);

        sections.add(ds8);
        sections.add(ds9);

        return sections;
    }

    public List<DefSection> testQuart2Situations(List<DefSection> sections){


        int x = 100;

        DefSection ds = new StraightHoriz(new Section(2, 100, null, null, null), x, 600, 150,0, "RIGHT");
        DefSection ds1 = new Quart2(new Section(2, 100, null, null, null), 150,2);
        ds1.setStart(ds);

        sections.add(ds);
        sections.add(ds1);

        x += 300;

        DefSection ds2 = new Quart1(new Section(2, 100, null, null, null), x, 600, 150,1, "RIGHT");
        DefSection ds3 = new Quart2(new Section(2, 100, null, null, null), 150,2);
        ds3.setStart(ds2);

        sections.add(ds2);
        sections.add(ds3);

        x += 300;

        DefSection ds4 = new Quart4(new Section(2, 100, null, null, null), x, 600, 150,4, "RIGHT");
        DefSection ds5 = new Quart2(new Section(2, 100, null, null, null), 150,2);
        ds5.setStart(ds4);

        sections.add(ds4);
        sections.add(ds5);

        x += 300;

        DefSection ds6 = new StraightVert(new Section(2, 100, null, null, null), x, 600, 150,5, "LEFT");
        DefSection ds7 = new Quart2(new Section(2, 100, null, null, null), 150,2);
        ds7.setStart(ds6);

        sections.add(ds6);
        sections.add(ds7);

        x += 300;

        DefSection ds8 = new Quart4(new Section(2, 100, null, null, null), x, 600, 200,4, "LEFT");
        DefSection ds9 = new Quart2(new Section(2, 100, null, null, null), 150,2);
        ds9.setStart(ds8);

        sections.add(ds8);
        sections.add(ds9);

        x += 300;

        return sections;
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

        return new ViewLogic(null,null);
    }
}
