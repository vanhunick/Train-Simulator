package simulation.Panes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.Simulation;

/**
 * Created by vanhunick on 11/08/16.
 */
public class ControllerSelection {

    private String curLockingSelection;

    public ControllerSelection(Simulation sim){
        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Select a controller");

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ObservableList<String> trainOptions = FXCollections.observableArrayList(sim.getControllers());

        ComboBox controllerSelectComboBox = new ComboBox(trainOptions);
        controllerSelectComboBox.setValue("Locking");
        curLockingSelection = "Locking";

        controllerSelectComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                valChangedController(ov, t, t1);
            }
        });

        grid.add(new Label("Add Train:"), 0, 1);
        grid.add(controllerSelectComboBox,1,1);

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }
    public void valChangedController(ObservableValue ov, String t, String t1){
        this.curLockingSelection = t1;
    }

    public String getCurLockingSelection(){
        return curLockingSelection;
    }
}
