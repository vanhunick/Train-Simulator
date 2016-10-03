package simulation.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.Drawable.tracks.DefaultTrack;

import java.util.Optional;


/**
 * Created by vanhunick on 12/04/16.
 */
public class AddTrainMenu {

    // The track id
    private int id;

    // The length of section in pixels
    private double length;

    // The train they want to create
    private String curTrainSelection;

    // If they want to add a train
    private CheckBox addTrain;

    // If they want to add a rolling stock
    private CheckBox addRollingstock;

    // The orientation of the train to be created
    private CheckBox natural;

    // The number of rolling stock to connect to the train
    private  int numbRollingStock;

    public boolean canceled;


    public AddTrainMenu(DefaultTrack section, int validID) {
        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Add Train or Stock to section ID: " + section.getId());
        dialog.setHeaderText("Enter Train and Stock details");

        // Set the button types.
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
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

        // Create checkbox for adding a train
        addTrain = new CheckBox("Add train");
        addTrain.setIndeterminate(false);

        // Create checkbox for orientation
        natural= new CheckBox("Natural Orientation");
        natural.setIndeterminate(false);

        // Create a checkbox for adding a rolling stock
        addRollingstock = new CheckBox("Add Rolling stock");
        addRollingstock.setIndeterminate(false);

        // ID
        TextField id = new TextField();
        id.setPromptText("ID");
        id.setText(""+validID);// get the next valid ID
        grid.add(new Label("Train ID:"), 0, 0);
        grid.add(id, 1, 0);
        id.setText(""+validID);

        // Length of train
        TextField length = new TextField();
        length.setPromptText("Length");
        grid.add(new Label("Length:"), 0, 2);
        grid.add(length, 1, 2);
        length.setText("15");

        // Number of stock
        TextField numbStock = new TextField();
        numbStock.setPromptText("Number of Rolling stock");
        grid.add(new Label("Number of Rolling stock:"), 0, 3);
        grid.add(numbStock, 1, 3);

        // Add train
        grid.add(new Label("Add Train:"), 0, 4);
        grid.add(trainSelectComboBox,1,4);

        grid.add(addTrain,1,5);
        grid.add(addRollingstock,1,6);
        grid.add(natural,1,7);

        numbStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!validateNumber(newValue)){
                numbStock.setText("");
            }
        });

        // validation
        length.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!validateNumber(newValue)){
                length.setText(oldValue);
            }
        });

        id.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!validateNumber(newValue)){
                id.setText(oldValue);
            }
        });

        id.setDisable(true);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the id field by default
        Platform.runLater(() -> id.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                //set the fields to return
                if(!id.getText().equals("")){
                    this.id = validID;
                }
                if(!length.getText().equals("")){
                    this.length = Double.parseDouble(length.getText());
                }

                if(!numbStock.getText().equals("")){
                    this.numbRollingStock = Integer.parseInt(numbStock.getText());
                }
            }
            return null;
        });

//
//        Optional<ButtonType> result = dialog.showAndWait();
//        if (result.get() == addButton){
//            this.canceled = false;
//        } else {
//            this.canceled = true;
//        }
    }

    public boolean validateNumber(String string){return string.matches("[0-9]*");}

    public void valChangedTrain(ObservableValue ov, String t, String t1){
        this.curTrainSelection = t1;
    }

    public boolean addTrain(){return addTrain.isSelected();}

    public boolean addRollingStocl(){
        return addRollingstock.isSelected();
    }

    public boolean naturalOrientation(){return natural.isSelected();}

    public int getId(){
        return this.id;
    }

    public String getCurTrainSelection(){return this.curTrainSelection;}

    public int getNumbRollingStock(){return this.numbRollingStock;}

    public double getLength(){
        return this.length;
    }
}

