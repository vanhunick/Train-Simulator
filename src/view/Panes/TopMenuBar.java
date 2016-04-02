package view.Panes;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import view.Main;

/**
 * Created by Nicky on 2/04/2016.
 */
public class TopMenuBar extends MenuBar {
    private Main main;

    public TopMenuBar(Main main){
        super();

        this.main = main;

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

        //Add file items
        menuFile.getItems().add(newTrackItem);
        menuFile.getItems().add(loadTrackItem);
        menuFile.getItems().add(simulateItem);

        //Add to the menu bar
        this.getMenus().addAll(menuFile, menuEdit, menuView);
    }

    public void handleNewTrackPressed(ActionEvent e){
        main.setMode("Builder");
    }

    public void handleLoadTrackPressed(ActionEvent e){
        System.out.println("Load Track Pressed");
    }


    public void handleSimulatePressed(ActionEvent e){
        main.setMode("Simulation");
    }




}
