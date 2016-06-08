package view;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import view.Panes.EventLog;
import view.Panes.TopMenuBar;
import view.Panes.TopToolBar;

import java.util.ArrayList;

public class Main extends Application {

    // Screen sizes
    public static final  int SCREEN_WIDTH = 1600;
    public static final  int SCREEN_HEIGHT = (SCREEN_WIDTH/16) * 9;

    private ArrayList<String> input = new ArrayList<>();
    private static BorderPane bl;

    // Fps counter fields
    public boolean debug = true;
    private final long ONE_SECOND = 1000000000;
    private long currentTime = 0;
    private long lastTime = 0;
    private int fps = 0;
    private double delta = 0;
    private int fpsText;


    // Controls the flow of the program
    private ProgramController controller;

    @Override
    public void start(final Stage primaryStage) {

        // Initialises the control that will control the flow of the program
        this.controller = new ProgramController();

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT, Color.rgb(0, 0, 0));
        Canvas canvas = new Canvas();

        // Responds to a key being pressed
        scene.setOnKeyPressed(e -> {String code = e.getCode().toString();if ( !input.contains(code) ){
            input.add( code );
            controller.keyPressed(code);
        }
        });

        //responds to a key being released
        scene.setOnKeyReleased(e -> {String code = e.getCode().toString();input.remove( code );});

        //Update the controller with any mouse events
        canvas.setOnMousePressed(e -> controller.mousePressed(e.getX(), e.getY(), e));
        canvas.setOnMouseReleased(e -> controller.mouseReleased(e.getX(), e.getY(),e));
        canvas.setOnMouseClicked(e -> controller.mouseClicked(e.getX(), e.getY(),e));
        canvas.setOnMouseMoved(e -> controller.mouseMoved(e.getX(), e.getY(),e));
        canvas.setOnMouseDragged(e -> controller.mouseDragged(e.getX(), e.getY(),e));


        setupGUI(primaryStage, scene, root,canvas);
        controller.setDefMode(bl,canvas);

        lastTime = System.nanoTime();
        setupAnimationTimer(primaryStage,canvas);
    }


    public void setupGUI(Stage primaryStage, Scene scene, Group root, Canvas canvas){
        primaryStage.setTitle("Train Simulator");

        bl = new BorderPane();
        controller.setBorderPane(bl);

        TopToolBar topToolBar = new TopToolBar(controller);
        topToolBar.setPrefWidth(SCREEN_WIDTH/2);
        TopMenuBar topMenuBar = new TopMenuBar(controller);
        topMenuBar.setPrefWidth(SCREEN_WIDTH/2);
        topMenuBar.setMinHeight(35);
        topToolBar.setPrefHeight(35);

        HBox barBox = new HBox();

        barBox.getChildren().addAll(topMenuBar,topToolBar);



        primaryStage.setHeight(SCREEN_HEIGHT);
        primaryStage.setWidth(SCREEN_WIDTH);

//        bl.setTop(topMenuBar);//Need to add first as it is being used to calculate offset
        bl.setTop(barBox);//Need to add first as it is being used to calculate offset
        bl.setCenter(canvas);

        canvas.setWidth(SCREEN_WIDTH- EventLog.WIDTH - Simulation.WIDTH);//TODO do better later
        canvas.setHeight(SCREEN_HEIGHT - TopMenuBar.HEIGHT);


        root.getChildren().add(bl);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setupAnimationTimer(Stage primaryStage, Canvas canvas){
        final GraphicsContext gc = canvas.getGraphicsContext2D();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Shows fps counter in debug mode
                if(debug){
                    currentTime = now;
                    fps++;
                    delta += currentTime-lastTime;

                    if(delta > ONE_SECOND){
                        fpsText = fps;
                        delta -= ONE_SECOND;
                        fps = 0;
                    }
                    lastTime = currentTime;
                }

                // Clear the screen
                gc.setStroke(Color.BLACK);
                gc.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());
                gc.setStroke(Color.WHITE);


                // Update and draw the controller
                controller.update();
                controller.refresh(gc);

                // Draw the fps counter
                if(debug){
                    gc.strokeText("FPS " + fpsText, 0, 20);
                }

                gc.strokeText("         20m ", 800, 25);

                gc.setStroke(Color.WHITE);
                gc.strokeLine(800,30,800 + (20*Simulation.METER_MULTIPLIER),30);

                gc.strokeLine(800,25,800,35);
                gc.strokeLine(800 + (20*Simulation.METER_MULTIPLIER),25, 800 + (20*Simulation.METER_MULTIPLIER),35);

                gc.save();
                gc.restore();
            }
        }.start();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
