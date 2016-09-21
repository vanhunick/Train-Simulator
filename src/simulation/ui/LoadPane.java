package simulation.ui;

import javafx.stage.FileChooser;
import util.save.Load;
import util.save.LoadedRailway;

import java.io.File;

/**
 * Created by Nicky on 6/06/2016.
 */
public class LoadPane {


    private LoadedRailway railway;

    public void loadRailway(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Track File");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            parseFile(file);
        }
    }

    public void parseFile(File file){
        Load load = new Load();
        railway = load.loadFromFile(file,file.getAbsolutePath(), null);
    }

    public LoadedRailway getRailway(){
        return railway;
    }
}
