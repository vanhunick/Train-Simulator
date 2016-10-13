package simulation.ui;

import javafx.scene.control.Alert;

/**
 * Created by vanhunick on 13/10/16.
 */
public class HelpDialog {

    public HelpDialog(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Simulation \n \n"+
                             "To toggle a junction right click on it \n"+
                             "To add a train double click on a Track \n "+
                             "Double click on a train to access attribute menu single click for current info \n \n"+
                             "Track Builder \n \n"+
                             "Double right click on track after setting sections to add trains and rolling stock");

        alert.showAndWait();
    }
}
