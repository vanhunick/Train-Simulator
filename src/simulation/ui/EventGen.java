package simulation.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import simulation.model.ModelTrack;
import simulation.model.Train;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanhunick on 11/04/16.
 */
public class EventGen {

    private ModelTrack model;

    private String trainId;

    private double speed;

    private boolean direction;

    private String curDerSelection = "Forward";

    public EventGen(ModelTrack model) {
        this.model = model;

        Dialog dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Event Generator");

        dialog.setHeaderText("Choose an event");

        List<Train> trains = model.getTrains();

        // Set the button types.
        ButtonType addButton = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));


        List<String> names = new ArrayList<>();
        for(Train t : trains){
            names.add("" + t.getId());
        }

        // Create an option box for the trains
        ObservableList<String> options = FXCollections.observableArrayList(names);

        ComboBox trainComboBox = new ComboBox(options);

        if(trains.size() == 0){
            trainComboBox.setValue("No trains to send events to");
        }
        else {
            trainComboBox.setValue(""+trains.get(0).getId());
            trainId = ""+trains.get(0).getId();//setting def value
        }

        trainComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                valChanged(ov,t,t1);
            }
        });

        TextField speed = new TextField();
        speed.setPromptText("Speed %");

        // Create an option box for the trains
        ObservableList<String> optionsDirection = FXCollections.observableArrayList("Stop","Forward","Reverse");
        curDerSelection = "Stop";

        ComboBox trainComboBoxDir = new ComboBox(optionsDirection);

        trainComboBoxDir.setValue("Stop");

        trainComboBoxDir.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {
                valChangedDir(ov, t, t1);
            }
        });



        grid.add(trainComboBox,0,0);
        grid.add(new Label("Train ID:"), 0, 1);
        grid.add(trainComboBoxDir,1,0);
        grid.add(new Label("Train Direction:"), 1, 1);
        grid.add(speed, 2, 0);

        dialog.getDialogPane().setContent(grid);

        speed.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!validatePercent(newValue)){
                speed.setText(oldValue);
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {

                int id = Integer.parseInt(trainId);

                if(curDerSelection.equals("Stop")){
                    System.out.println("Stopping");
                    model.setSpeed(id,0);
                } else if(!speed.getText().equals("")){
                    double speedValue = Double.parseDouble(speed.getText());
                    model.setSpeed(id,speedValue);
                }


                boolean dir = true;
                if(!curDerSelection.equals("Stop")){
                    dir = curDerSelection.equals("Forward") ? true : false;
                    model.setDirection(id,dir);
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    public boolean validatePercent(String value){
        if(value.matches("[0-9]*")){
            if(value.equals(""))return true;
            double number = Double.parseDouble(value);
            return number >=0 && number <= 100;
        }
        return  false;// Not a number
    }

    public void valChangedDir(ObservableValue ov, String t, String t1){
        this.curDerSelection = t1;
    }

    public void valChanged(ObservableValue ov, String t, String t1){
        this.trainId = t1;
    }
}
