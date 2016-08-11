package view.Panes;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import view.ProgramController;
import view.SimulationUI;

import java.io.File;

/**
 * Created by Nicky on 8/06/2016.
 */
public class TopToolBar extends ToolBar {

    // Tool bar buttons
    Button play;
    Button pause;
    Button stop;
    Button event;
    Button newSec;
    Button sim;
    Button undoBut;
    Button saveBut;
    Button clear;
    Button control;
    Button simTrack;



    /**
     * The top tool bar on the user interface used to control the program
     *
     * @param controller the controller to pass the events to
     * */
    public TopToolBar(ProgramController controller){
        // The size of the buttons
        int imageSize = 20;

        //Icons made by Freepik from www.flaticon.com

        // Controller icon
        //Icon made by  Madebyoliver from www.flaticon.com


        // Simulation Icons
        Image playImage = new Image("file:src/res/play.png",imageSize,imageSize,false,false);
        Image pauseImage = new Image("file:src/res/pause.png",imageSize,imageSize,false,false);
        Image stopImage = new Image("file:src/res/stop.png",imageSize,imageSize,false,false);
        Image eventImage = new Image("file:src/res/event.png",imageSize,imageSize,false,false);
        Image saveImage = new Image("file:src/res/save.png",imageSize,imageSize,false,false);
        Image clearImage = new Image("file:src/res/clear.png",imageSize,imageSize,false,false);
        Image controlImage = new Image("file:src/res/control.png",imageSize,imageSize,false,false);

        // Track Builder Icons
        Image newSection = new Image("file:src/res/train-rails.png",imageSize,imageSize,false,false);
        Image undo = new Image("file:src/res/undo.png",imageSize,imageSize,false,false);
        Image simImage = new Image("file:src/res/metro.png",imageSize,imageSize,false,false);
        Image simTrackImage = new Image("file:src/res/checked.png",imageSize,imageSize,false,false);

        // Buttons with image inside
        play = new Button("", new ImageView(playImage));
        pause = new Button("",new ImageView(pauseImage));
        stop = new Button("",new ImageView(stopImage));
        event = new Button("",new ImageView(eventImage));
        newSec = new Button("",new ImageView(newSection));
        undoBut = new Button("",new ImageView(undo));
        sim = new Button("",new ImageView(simImage));
        saveBut = new Button("",new ImageView(saveImage));
        clear = new Button("",new ImageView(clearImage));
        control = new Button("", new ImageView(controlImage));
        simTrack = new Button("", new ImageView(simTrackImage));

        // Tool tips
        Tooltip playTip = new Tooltip("Starts the Simulation");
        Tooltip pauseTip = new Tooltip("Pauses the Simulation");
        Tooltip stopTip = new Tooltip("Stops and resets the Simulation");
        Tooltip eventTip = new Tooltip("Opens the event generation menu");
        Tooltip newSecTip = new Tooltip("End this section and start the next one");
        Tooltip undoTip = new Tooltip("Undo the last added track");
        Tooltip simTip = new Tooltip("Starts the Simulation with the created track");
        Tooltip saveTip = new Tooltip("Save the current track to file");
        Tooltip clearTip = new Tooltip("Clears the entire track");
        Tooltip controlTip = new Tooltip("Select a controller");
        Tooltip simTrackTip = new Tooltip("Click to simulate the created track");

        // Add the tooltips to buttons
        play.setTooltip(playTip);
        pause.setTooltip(pauseTip);
        stop.setTooltip(stopTip);
        event.setTooltip(eventTip);
        newSec.setTooltip(newSecTip);
        undoBut.setTooltip(undoTip);
        sim.setTooltip(simTip);
        saveBut.setTooltip(saveTip);
        clear.setTooltip(clearTip);
        control.setTooltip(controlTip);
        simTrack.setTooltip(simTrackTip );

        // use horizontal box to separate out the buttons
        HBox buttonBox = new HBox(5);
        buttonBox.setPrefWidth(this.getPrefWidth());
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getChildren().addAll(control,event,play,stop,pause,simTrack,sim,clear,saveBut,newSec,undoBut);
        HBox.setHgrow(buttonBox,Priority.ALWAYS);

        // Checks which mode the program is in the see which buttons should be disabled
        if(!controller.gerMode().equals(ProgramController.VISUALISATION_MODE)){
            enableButtons();
            disableBuilderButtons();
        }

        simTrack.setOnAction(e -> {
            if(controller.gerMode().equals(ProgramController.BUILDER_MODE)){
                if(controller.setSimulationFromBuilder()){
                    controller.setMode(ProgramController.VISUALISATION_MODE);
                }
            }
        });

        control.setOnAction(e -> {
            if(controller.gerMode().equals(ProgramController.VISUALISATION_MODE)){
                if(controller.getSimulationUI().getSim().getStarted()){
                    new ErrorDialog("Cannot change controller while simulation is running. Please stop simulation", "Simulation Started");
                    return;
                }
                controller.getSimulationUI().startControlerDialog();
            }
        });

        event.setOnAction(e -> {
            if(controller.gerMode().equals(ProgramController.VISUALISATION_MODE)){
                controller.getSimulationUI().startEventDialog();
            }
        });

        play.setOnAction(e -> {
            if(controller.gerMode().equals(ProgramController.VISUALISATION_MODE)){
                controller.getSimulationUI().getSim().start();
            }
        });

        pause.setOnAction(e -> {
            if(controller.gerMode().equals(ProgramController.VISUALISATION_MODE)){
                controller.getSimulationUI().getSim().pause();
            }
        });

        stop.setOnAction(e -> {
            if (controller.gerMode().equals(ProgramController.VISUALISATION_MODE)) {
                controller.getSimulationUI().getSim().restart();
            }
        });

        newSec.setOnAction(e -> {
            if (controller.gerMode().equals(ProgramController.BUILDER_MODE)) {
                controller.getTrackBuilder().newSection();
            }
        });

        sim.setOnAction(e -> {
            if (controller.gerMode().equals(ProgramController.BUILDER_MODE)) {
                controller.getTrackBuilder().simulateTrack();//TODO might not use
            }
        });

        undoBut.setOnAction(e -> {
            if (controller.gerMode().equals(ProgramController.BUILDER_MODE)) {
                controller.getTrackBuilder().undo();
            }
        });

        saveBut.setOnAction(e -> {
            if (controller.gerMode().equals(ProgramController.BUILDER_MODE)) {
                controller.getTrackBuilder().save();
            }
        });

        clear.setOnAction(e -> {
            if (controller.gerMode().equals(ProgramController.BUILDER_MODE)) {
                controller.getTrackBuilder().clear();
            }
        });

        // Make it a horizontal tool bar
        this.setOrientation(Orientation.HORIZONTAL);
        this.getItems().addAll(
                new Separator(),
                buttonBox,
                new Separator()
        );
    }

    /**
     * Enables all the buttons on the toolbar
     * */
    public void enableButtons(){
        play.setDisable(false);
        pause.setDisable(false);
        stop.setDisable(false);
        event.setDisable(false);
        newSec.setDisable(false);
        sim.setDisable(false);
        undoBut.setDisable(false);
        saveBut.setDisable(false);
        clear.setDisable(false);
        control.setDisable(false);
        simTrack.setDisable(false);
    }

    /**
     * Disables the builder buttons
     * */
    public void setBuilderButtons(){
        play.setDisable(true);
        stop.setDisable(true);
        pause.setDisable(true);
        event.setDisable(true);
    }

    /**
     * Disables the simulation buttons
     * */
    public void disableSimButtons(){
        play.setDisable(true);
        stop.setDisable(true);
        pause.setDisable(true);
        event.setDisable(true);
        control.setDisable(true);
    }

    public void disableBuilderButtons(){
        undoBut.setDisable(true);
        newSec.setDisable(true);
        saveBut.setDisable(true);
        clear.setDisable(true);
        sim.setDisable(true);
        simTrack.setDisable(true);
    }
}
