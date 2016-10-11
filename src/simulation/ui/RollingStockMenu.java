package simulation.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.Drawable.tracks.DefaultTrack;
import simulation.model.RollingStock;

import java.util.Optional;

/**
 * Created by vanhunick on 6/10/16.
 */
public class RollingStockMenu {

    public RollingStockMenu(RollingStock rollingStock){
        double oldweight = rollingStock.getWeight();

        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Modify Rolling stock");
        dialog.setHeaderText("Enter Rolling stock Weight");

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Max speed of train
        Slider weightSlider = new Slider();
        weightSlider.setMin(1);
        weightSlider.setMax(1000);
        weightSlider.setValue(oldweight/1000);
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);

        weightSlider.setBlockIncrement(5);
        grid.add(new Label("Weight Ton:"), 0, 0);
        // col row
        grid.add(weightSlider, 0, 1);

        dialog.getDialogPane().setContent(grid);

        // Set up slider listeners
        // Handle Slider value change events.
        weightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            rollingStock.setWeight(newValue.doubleValue()*1000);
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if(result.get().getText().equalsIgnoreCase("CANCEL")){
                rollingStock.setWeight(oldweight);
            }
        }
    }
}
