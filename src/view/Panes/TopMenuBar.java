package view.Panes;

import Util.CustomTracks;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import save.Load;
import save.LoadedRailway;
import view.ProgramController;
import view.Simulation;
import view.SimulationUI;

/**
 * Created by Nicky on 2/04/2016.
 */
public class TopMenuBar extends MenuBar {
    private ProgramController controller;


    public static final int HEIGHT= 20;


    public TopMenuBar(ProgramController controller){
        super();

        this.controller = controller;
        this.setPrefHeight(HEIGHT);

        //Menu items
        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        Menu modeView = new Menu("Mode");

        //List for file
        MenuItem newTrackItem = new MenuItem("New Track");
        MenuItem loadTrackItem = new MenuItem("Load Track");
        MenuItem simulateItem= new MenuItem("Simulate");

        // List for edit
        MenuItem physics = new MenuItem("Physics");

        //Handle file events
        newTrackItem.setOnAction(e -> handleNewTrackPressed(e));
        loadTrackItem.setOnAction(e -> handleLoadTrackPressed(e));
        simulateItem.setOnAction(e -> handleSimulatePressed(e));
        physics.setOnAction(e -> handlePhysicsPressed(e));

        // List for View
        MenuItem log = new MenuItem("Log view");

        // List for Mode
        CheckMenuItem testItem = new CheckMenuItem("Test");

        // The default setting
        testItem.setSelected(true);

        CheckMenuItem userItem = new CheckMenuItem("User Control");
        CheckMenuItem controllerItem = new CheckMenuItem("Controller");

        modeView.getItems().add(testItem);
        modeView.getItems().add(userItem);
        modeView.getItems().add(controllerItem);

        testItem.setOnAction(e -> {
            userItem.setSelected(false);
            controllerItem.setSelected(false);
            controller.setSimulationMode("Test");
        });

        userItem.setOnAction(e -> {
            controllerItem.setSelected(false);
            testItem.setSelected(false);
            controller.setSimulationMode("User");
        });

        controllerItem.setOnAction(e -> {
            userItem.setSelected(false);
            testItem.setSelected(false);
            controller.setSimulationMode("Controller");
        });

        // Actions for view items
        log.setOnAction(e -> logToggled(e));

        //Add file items
        menuFile.getItems().add(newTrackItem);
        menuFile.getItems().add(loadTrackItem);
        menuFile.getItems().add(simulateItem);

        // Add the view items
        menuView.getItems().add(log);

        // Add the edit items
        menuEdit.getItems().add(physics);


        //Add to the menu bar
        this.getMenus().addAll(menuFile, menuEdit, menuView, modeView);

    }

    public void handlePhysicsPressed(ActionEvent e){
        controller.handlePhysicsPressed(e);
    }


    public void handleNewTrackPressed(ActionEvent e){
        controller.setMode(ProgramController.BUILDER_MODE);
    }

    public void logToggled(ActionEvent e){controller.toggleLogView();}

    public void handleSimulatePressed(ActionEvent e){
        if(controller.setSimulationFromBuilder()){
            controller.setMode(ProgramController.VISUALISATION_MODE);
        }
    }

    public void handleTestModePressed(){
        controller.setModeOfSimulation("Test");
    }

    public void handleUserModePressed(){
        controller.setModeOfSimulation("User");
    }

    public void handleControllerModePressed(){

    }


    public void handleLoadTrackPressed(ActionEvent e){
        LoadPane l = new LoadPane();

        l.loadRailway();
        LoadedRailway railway = l.getRailway();
        controller.setLoadedRailway(railway.file,railway);
    }


}
