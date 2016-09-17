package simulation.ui;

import javafx.scene.control.Alert;

/**
 * Created by vanhunick on 4/04/16.
 */
public class ErrorDialog {

    public ErrorDialog(String message, String title){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
