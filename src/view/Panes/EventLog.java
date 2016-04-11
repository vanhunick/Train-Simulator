package view.Panes;


import javafx.scene.control.TextArea;

/**
 * Created by vanhunick on 11/04/16.
 */
public class EventLog extends TextArea {

    public static  final int WIDTH = 250;

    public EventLog(){
        super();

        this.setPrefRowCount(10);
        this.setPrefColumnCount(100);
        this.setWrapText(true);
        this.setPrefWidth(250);
    }
}
