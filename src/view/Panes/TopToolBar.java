package view.Panes;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import view.ProgramController;

import java.io.File;

/**
 * Created by Nicky on 8/06/2016.
 */
public class TopToolBar extends ToolBar {

    Button play;
    Button pause;
    Button stop;
    Button event;
    Button newSec;
    Button newTrain;
    Button undoBut;



    public TopToolBar(ProgramController controller){
        int imageSize = 20;

        //Icon made by Freepik from www.flaticon.com

        // Simulation Icons
        Image playImage = new Image("file:src/res/play.png",imageSize,imageSize,false,false);
        Image pauseImage = new Image("file:src/res/pause.png",imageSize,imageSize,false,false);
        Image stopImage = new Image("file:src/res/stop.png",imageSize,imageSize,false,false);
        Image eventImage = new Image("file:src/res/event.png",imageSize,imageSize,false,false);

        // Track Builder Icons
        Image newSection = new Image("file:src/res/train-rails.png",imageSize,imageSize,false,false);
        Image undo = new Image("file:src/res/undo.png",imageSize,imageSize,false,false);
        Image train = new Image("file:src/res/metro.png",imageSize,imageSize,false,false);


        play = new Button("", new ImageView(playImage));
        pause = new Button("",new ImageView(pauseImage));
        stop = new Button("",new ImageView(stopImage));
        event = new Button("E",new ImageView(eventImage));

        newSec = new Button("",new ImageView(newSection));
        undoBut = new Button("",new ImageView(undo));
        newTrain = new Button("",new ImageView(train));


        // use hbox
        HBox buttonBox = new HBox(5);
        buttonBox.setPrefWidth(this.getPrefWidth());
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getChildren().addAll(newSec,undoBut,newTrain,event,play,stop,pause);
        HBox.setHgrow( buttonBox, Priority.ALWAYS );


        if(!controller.gerMode().equals(ProgramController.VISUALISATION_MODE)){
            enableButtons(false);
        }

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

        this.setOrientation(Orientation.HORIZONTAL);
        this.getItems().addAll(
                new Separator(),
                buttonBox,
                new Separator()
        );
    }

    public void enableButtons(boolean enable){
        if(enable){
            play.setDisable(false);
            stop.setDisable(false);
            pause.setDisable(false);
            event.setDisable(false);


        }
        else{
            play.setDisable(true);
            stop.setDisable(true);
            pause.setDisable(true);
            event.setDisable(true);
        }
    }
}
