package view.Panes;

import Util.CustomTracks;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import save.Load;
import save.LoadedRailway;
import view.ProgramController;

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

        //List for file
        MenuItem newTrackItem = new MenuItem("New Track");
        MenuItem loadTrackItem = new MenuItem("Load Track");
        MenuItem simulateItem= new MenuItem("Simulate");

        //Handle file events
        newTrackItem.setOnAction(e -> handleNewTrackPressed(e));
        loadTrackItem.setOnAction(e -> handleLoadTrackPressed(e));
        simulateItem.setOnAction(e -> handleSimulatePressed(e));

        // List for View
        MenuItem log = new MenuItem("Log view");

        // Actions for view items
        log.setOnAction(e -> logToggled(e));

        //Add file items
        menuFile.getItems().add(newTrackItem);
        menuFile.getItems().add(loadTrackItem);
        menuFile.getItems().add(simulateItem);

        // Add the view items
        menuView.getItems().add(log);



        //Add to the menu bar
        this.getMenus().addAll(menuFile, menuEdit, menuView);

    }

    public void handleNewTrackPressed(ActionEvent e){
        controller.setMode(ProgramController.BUILDER_MODE);
    }

    public void logToggled(ActionEvent e){controller.toggleLogView();}

    public void handleSimulatePressed(ActionEvent e){
        controller.setMode(ProgramController.VISUALISATION_MODE);
    }

    // TODO implement
    public void handleLoadTrackPressed(ActionEvent e){
        LoadPane l = new LoadPane();

        l.loadRailway();
        LoadedRailway railway = l.getRailway();
        controller.setLoadedRailway(railway);
    }


}
