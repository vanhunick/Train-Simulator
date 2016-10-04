package simulation.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.Drawable.DrawableTrain;
import simulation.Drawable.tracks.DefaultTrack;
import simulation.Simulation;

import java.util.Optional;

/**
 * Created by vanhunick on 4/10/16.
 */
public class PhysicsMenu {

    /**
     * Menu used to adjust friction in tracks
     * */
    public PhysicsMenu(){
        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Modify Train");
        dialog.setHeaderText("Enter Details");

        double tempKineteicFriction = DefaultTrack.KINETIC_FRICTION;
        double tempStaticFriction = DefaultTrack.STATIC_FRICTION;

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Max speed of train
        Slider staticF = new Slider();
        staticF.setMin(0.1);
        staticF.setMax(1);
        staticF.setValue(tempStaticFriction);
        staticF.setShowTickLabels(true);
        staticF.setShowTickMarks(true);
        staticF.setBlockIncrement(5);
        grid.add(new Label("Static Friction:"), 0, 0);
        // col row
        grid.add(staticF, 0, 1);

        Slider kineticF = new Slider();
        kineticF.setMin(0.1);
        kineticF.setMax(1);
        kineticF.setValue(tempKineteicFriction);
        kineticF.setShowTickLabels(true);
        kineticF.setShowTickMarks(true);
        kineticF.setBlockIncrement(5);
        grid.add(new Label("Kinetic Friction:"), 0, 2);
        grid.add(kineticF, 0, 3);

        dialog.getDialogPane().setContent(grid);

        // Set up slider listeners
        // Handle Slider value change events.
        staticF.valueProperty().addListener((observable, oldValue, newValue) -> {
            DefaultTrack.STATIC_FRICTION = newValue.doubleValue();
        });

        kineticF.valueProperty().addListener((observable, oldValue, newValue) -> {
            DefaultTrack.KINETIC_FRICTION= newValue.doubleValue();
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if(result.get().getText().equalsIgnoreCase("CANCEL")){
                DefaultTrack.KINETIC_FRICTION = tempKineteicFriction;
                DefaultTrack.STATIC_FRICTION = tempStaticFriction;
            }
        }
    }
}
