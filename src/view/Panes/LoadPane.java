package view.Panes;

import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import save.Load;
import save.LoadedRailway;

import java.io.File;

/**
 * Created by Nicky on 6/06/2016.
 */
public class LoadPane {


    private LoadedRailway railway;

    public void loadRailway(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Track File");
//        fileChooser.showOpenDialog(null);

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            parseFile(file);
        }
    }

    public void parseFile(File file){
        Load load = new Load();
        railway = load.loadFromFile(file.getAbsolutePath());
    }

    public LoadedRailway getRailway(){
        return railway;
    }
}
