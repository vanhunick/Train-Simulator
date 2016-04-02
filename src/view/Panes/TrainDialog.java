package view.Panes;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.Optional;

/**
 * Created by Nicky on 3/04/2016.
 */
public class TrainDialog {
    // Create the custom dialog.

    private int id;
    private int startID;
    private double length;

    public TrainDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Train");

        dialog.setHeaderText("Enter train details");

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField id = new TextField();
        id.setPromptText("ID");
        TextField start = new TextField();
        start.setPromptText("Start ID");
        TextField length = new TextField();
        length.setPromptText("Length");

        grid.add(new Label("Train ID:"), 0, 0);
        grid.add(id, 1, 0);
        grid.add(new Label("Start ID:"), 0, 1);
        grid.add(start, 1, 1);
        grid.add(new Label("Train Length:"), 0, 2);
        grid.add(length, 1, 2);


        // Enable/Disable login button depending on whether a username was entered.
//        Node myAddButton = dialog.getDialogPane().lookupButton(addButton);
//        myAddButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        id.textProperty().addListener((observable, oldValue, newValue) -> {
            id.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> id.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                //set the fields to return
                this.startID = Integer.parseInt(start.getText());
                this.id = Integer.parseInt(id.getText());
                this.length = Double.parseDouble(length.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(ids-> {
            this.id = Integer.parseInt(ids.getKey());//TODO check if int
            this.startID = Integer.parseInt(ids.getValue());//TODO check if int
        });
    }

    public int getId(){
        return this.id;
    }

    public int getStartId(){
        return this.startID;
    }

    public double getLength(){
        return this.length;
    }
}
