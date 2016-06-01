package view.Panes;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import view.Drawable.section_types.DefaultTrack;


/**
 * Created by vanhunick on 12/04/16.
 */
public class TrackMenu {

    // The track id
    private int id;

    // The length of section in pixels
    private double length;

    private boolean canDetect;
    private String curDetectSelection;
    private String curTrainSelection;

    private CheckBox addTrain;
    private CheckBox addRollingstock;

    private DefaultTrack section;

    public TrackMenu(DefaultTrack section) {
        this.section = section;

        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Edit StraightTrack ID: " + section.getId());

        dialog.setHeaderText("Enter track details");

        // Set the button types.
        ButtonType addButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        ObservableList<String> trainOptions = FXCollections.observableArrayList("British Rail Class 25","British Rail Class 108 (DMU)","British Rail Class 101 (DMU)");

        ComboBox trainSelectComboBox = new ComboBox(trainOptions);
        trainSelectComboBox.setValue("British Rail Class 25");
        curTrainSelection = "British Rail Class 25";

        trainSelectComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                valChangedTrain(ov, t, t1);
            }
        });

        // Create an option box for the trains
        ObservableList<String> options = FXCollections.observableArrayList("Yes","No");
        curDetectSelection = "True";

        ComboBox trainComboBox = new ComboBox(options);

        trainComboBox.setValue("Yes");

        trainComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                valChanged(ov,t,t1);
            }
        });


        // Create checkbox for adding a train
        addTrain = new CheckBox("Add train");
        addTrain.setIndeterminate(false);

        // Create a checkbox for adding a rollingstock
        addRollingstock = new CheckBox("Add Rolling stock");
        addRollingstock.setIndeterminate(false);


        grid.add(new Label("Can Detect:"), 0, 0);
        grid.add(trainComboBox,1,0);


        TextField id = new TextField();
        id.setPromptText("ID");
        grid.add(new Label("Train ID:"), 0, 1);
        grid.add(id, 1, 1);


        TextField length = new TextField();
        length.setPromptText("Length");
        grid.add(new Label("Length:"), 0, 2);
        grid.add(length, 1, 2);

        grid.add(new Label("Add Train:"), 0, 3);
        grid.add(trainSelectComboBox,1,3);

        grid.add(addTrain,1,4);
        grid.add(addRollingstock,1,5);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the id field by default
        Platform.runLater(() -> id.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                //set the fields to return

                if(!id.getText().equals("")){
                    this.id = Integer.parseInt(id.getText());
                }
                if(!length.getText().equals("")){
                    this.length = Double.parseDouble(length.getText());
                }

                this.canDetect = Boolean.parseBoolean(curDetectSelection);
            }
            return null;
        });

        dialog.showAndWait();
    }

    public void valChanged(ObservableValue ov, String t, String t1){
        this.curDetectSelection = t1;
    }

    public void valChangedTrain(ObservableValue ov, String t, String t1){
        this.curTrainSelection = t1;
    }

    public boolean addTrain(){
        return addTrain.isSelected();
    }
    public boolean addRollingStocl(){
        return addRollingstock.isSelected();
    }

    public int getId(){
        return this.id;
    }

    public boolean canDetect(){
        return this.canDetect;
    }

    public String getCurTrainSelection(){return this.curTrainSelection;}

    public double getLength(){
        return this.length;
    }
}

