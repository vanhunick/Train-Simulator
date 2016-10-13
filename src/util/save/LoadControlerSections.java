package util.save;

import controllers.ControllerSection;
import controllers.ControllerTrain;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by vanhunick on 18/08/16.
 */
public class LoadControlerSections {

    public class LoadedControlRailway {
        public ControllerSection[] sections;
        public List<ControllerTrain> trains;

        public LoadedControlRailway(ControllerSection[] sections, List<ControllerTrain> trains){
            this.sections = sections;
            this.trains = trains;
        }
    }

    public LoadedControlRailway loadedControlRailway(String filePath){

        String str = "";
        Scanner scan = null;
        try {
            scan = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scan.hasNext())
            str += scan.nextLine();
        scan.close();

        // build a JSON object from the file string
        JSONObject obj = new JSONObject(str);

        // Load the sections and trains and return
        return new LoadedControlRailway(loadSections(obj), loadControlTrains(obj));
    }


    public ControllerSection[] loadSections(JSONObject obj) {

        ControllerSection[] sections = null;

            JSONArray sectionsArray = obj.getJSONArray("sections");

            sections = new ControllerSection[sectionsArray.length()];

            for (int i = 0; i < sectionsArray.length(); i++) {

                JSONObject sectionObject = sectionsArray.getJSONObject(i);

                // Create the section object

                // Grab the id of the section
                int id = sectionObject.getInt("id");
                int length = sectionObject.getInt("length");
                int from = sectionObject.getInt("from");
                int to = sectionObject.getInt("to");

                boolean hasJunc = sectionObject.getBoolean("hasJunc");
                int junctionIndex = sectionObject.getInt("junctionIndex");

                sections[i] = new ControllerSection(id,from, to, junctionIndex, length);

                if(hasJunc){
                    int juncId = sectionObject.getInt("juncID");
                    boolean inbound = sectionObject.getBoolean("inbound");
                    sections[i].addJunction(juncId,inbound,false); // False by default
                }
            }

        return sections;
    }

    public List<ControllerTrain> loadControlTrains(JSONObject obj){
        List<ControllerTrain> trains = new ArrayList<>();

        JSONArray trainArray = obj.getJSONArray("trains");

        for(int i = 0; i < trainArray.length(); i++){
            JSONObject trainObject = trainArray.getJSONObject(i);

            int id = trainObject.getInt("id");
            boolean dir = trainObject.getBoolean("direction");
            boolean ori = trainObject.getBoolean("orientation");
            int curSectionID = trainObject.getInt("curSectionID");

            ControllerTrain t = new ControllerTrain(id,dir,ori,curSectionID);
            trains.add(t);
        }
        return trains;
    }

}
