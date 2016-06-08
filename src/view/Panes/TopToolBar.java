package view.Panes;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import view.ProgramController;

/**
 * Created by Nicky on 8/06/2016.
 */
public class TopToolBar extends ToolBar {

    public TopToolBar(ProgramController controller){
        Button play = new Button("p");
        Button pause = new Button("p");
        Button stop = new Button("s");

        // use hbox
        HBox buttonBox = new HBox(5);
        buttonBox.setPrefWidth(this.getPrefWidth());
        buttonBox.setAlignment(Pos.BASELINE_RIGHT);
        buttonBox.getChildren().addAll(play,stop,pause);
        HBox.setHgrow( buttonBox, Priority.ALWAYS );



        this.setOrientation(Orientation.HORIZONTAL);
        this.getItems().addAll(
                new Separator(),
                buttonBox,
//                pause,
//                stop,
                new Separator()
        );
    }
}
