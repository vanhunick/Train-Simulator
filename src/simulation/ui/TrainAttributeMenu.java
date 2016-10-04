package simulation.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.Drawable.DrawableTrain;

import java.util.Optional;

/**
 * Created by vanhunick on 3/10/16.
 */
public class TrainAttributeMenu {

    public double length;

    public double weight;

    public double acceleration;

    public double maxSpeed;

    public boolean canceled;

    public TrainAttributeMenu(DrawableTrain t){
        this.length = t.getTrain().getLength();
        this.weight = t.getTrain().getWeight()/1000;
        this.acceleration = t.getTrain().getAcceleration();
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
        Slider acceleration = new Slider();
        acceleration.setMin(1);
        acceleration.setMax(20);
        acceleration.setValue(this.acceleration);
        acceleration.setShowTickLabels(true);
        acceleration.setShowTickMarks(true);
        acceleration.setMinorTickCount(5);
        acceleration.setMajorTickUnit(5);
        grid.add(new Label("Acceleration (ms2):"), 0, 2);
        grid.add(acceleration, 0, 3);

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

        acceleration.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.acceleration = newValue.intValue();
        });

        length.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.length= newValue.intValue();
        });

        weight.valueProperty().addListener((observable, oldValue, newValue) -> {
            this.weight = newValue.intValue();
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
                canceled = result.get().getText().equalsIgnoreCase("CANCEL");
        }
    }
}
