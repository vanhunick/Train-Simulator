package simulation.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.Drawable.DrawableTrain;

/**
 * Created by vanhunick on 3/10/16.
 */
public class TrainAttributeMenu {

    public double length;

    public double weight;

    public double maxPower;

    public double maxSpeed;

    public TrainAttributeMenu(DrawableTrain t){
        this.length = t.getTrain().getLength();
        this.weight = t.getTrain().getWeight()/1000;
        this.maxPower = t.getTrain().getMaxPower()/1000;
        this.maxSpeed = t.getTrain().getMaxSpeed();

        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Modify Train");
        dialog.setHeaderText("Enter Details");

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Max speed of train
        Slider speed = new Slider();
        speed.setMin(1);
        speed.setMax(50);
        speed.setValue(maxSpeed);
        speed.setShowTickLabels(true);
        speed.setShowTickMarks(true);
        speed.setBlockIncrement(5);
        grid.add(new Label("Max Speed (m/s):"), 0, 0);
        // col row
        grid.add(speed, 0, 1);

        // Power of train
        Slider maxPower = new Slider();
        maxPower.setMin(1);
        maxPower.setMax(1000);
        maxPower.setValue(this.maxPower);
        maxPower.setShowTickLabels(true);
        maxPower.setShowTickMarks(true);
        maxPower.setMinorTickCount(5);
        maxPower.setMajorTickUnit(500);
        grid.add(new Label("Power (Kn):"), 0, 2);
        grid.add(maxPower, 0, 3);

        // length of train
        Slider length = new Slider();
        length.setMin(10);
        length.setMax(20);
        length.setValue(this.length);
        length.setShowTickLabels(true);
        length.setShowTickMarks(true);
        length.setMajorTickUnit(5);
        length.setMinorTickCount(5);
        grid.add(new Label("Length (m):"), 0, 4);
        grid.add(length, 0, 5);

        // Weight of train
        Slider weight = new Slider();
        weight.setMin(1);
        weight.setMax(100);
        weight.setValue(this.weight);
        weight.setShowTickLabels(true);
        weight.setShowTickMarks(true);
        weight.setMajorTickUnit(50);
        weight.setMinorTickCount(5);
        grid.add(new Label("Weight (Ton):"), 0, 6);
        grid.add(weight, 0, 7);

        dialog.getDialogPane().setContent(grid);

        // Set up slider listeners
        // Handle Slider value change events.
        speed.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.maxSpeed = newValue.intValue();
        });

        maxPower.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.maxPower = newValue.intValue();
        });

        length.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.length= newValue.intValue();
        });

        weight.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.weight = newValue.intValue();
        });

        dialog.showAndWait();
    }
}
