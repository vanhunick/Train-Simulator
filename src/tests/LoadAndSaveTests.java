package tests;

import org.json.JSONObject;
import org.junit.Test;
import util.save.Load;
import util.save.LoadedRailway;
import util.save.Save;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by Nicky on 6/06/2016.
 */
public class LoadAndSaveTests {

    @Test
    public void loadAndSaveEqaulity(){
        Load load = new Load();
        File f = new File("src/util.tracks/simple_track.json");
        LoadedRailway l = load.loadFromFile(f,"src/util.tracks/simple_track.json");


        String str = "";
        Scanner scan = null;
        try {
            scan = new Scanner(new File("src/util.tracks/simple_track.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();

        // build a JSON object
        JSONObject obj = new JSONObject(str);

        Save save = new Save();
        String JSON = save.save(l,"src/util.tracks/test_save");
        JSON.replaceAll("\\s+", "");
        String orign = obj.toString().replaceAll("\\s+", "");
        int count = 0;
        for(int i = 0; i < JSON.length(); i++){
            if(JSON.charAt(i) != orign.charAt(i)){
                count = i;
                break;
            }
        }
        //TODO test won't work because fields not inserted the same way
        System.out.println("Count equal" + count + " Out of " + orign.length() + " JSON " + JSON.substring(count));
        assert (JSON.toString().replaceAll("\\s+","").equals(obj.toString().replaceAll("\\s+","")));
    }
}
