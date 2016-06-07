package save;

import org.json.JSONArray;
import org.json.JSONObject;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nicky on 4/06/2016.
 */
public class Save {



    public String save(LoadedRailway railway, String location, String name){

        // Create the top level JSON object
        JSONObject railwayObj = new JSONObject();

        // Save the sections includes the tracks
        railwayObj.put("sections", saveSections(railway.sections));

        // Save the trains
        railwayObj.put("trains", saveTrains(railway.trains));

        // Save the rolling stock
        railwayObj.put("stocks", saveRollingStocks(railway.stocks));

        try {
            FileWriter file = new FileWriter(location+""+name+".json");
            file.write(railwayObj.toString(1));
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Useful for testing
        return railwayObj.toString();
    }

    public JSONArray saveSections(DrawableSection[] sections){
        // Create the JSON array for each of the sections
        JSONArray sectionArray = new JSONArray();

        boolean start = true;

        for(DrawableSection s : sections){
            JSONObject sectionObject = new JSONObject();
            sectionObject.put("id",s.getSection().getID());
            if(start){
                sectionObject.put("start",true);
                start = false;
            }
            else{
                sectionObject.put("start",false);
            }

            sectionObject.put("detect",s.getSection().canDetect());
            sectionObject.put("from",s.getSection().getFrom());
            sectionObject.put("length",s.getSection().getLength());
            sectionObject.put("to",s.getSection().getTo());
            sectionObject.put("junction",0);//TODO implement later

            JSONArray trackArray = new JSONArray();

            for(DefaultTrack t : s.getTracks()){
                JSONObject trackObject = new JSONObject();
                trackObject.put("type", getTrackType(t));
                trackObject.put("id",t.getId());
                trackObject.put("to",t.getTo());
                trackObject.put("from",t.getFrom());
                trackObject.put("length",t.getLength());
                if(getTrackType(t).equals("Junction")){
                    trackObject.put("inbound",((JunctionTrack)t).inBound());
                }
                trackArray.put(trackObject);
            }
            sectionObject.put("tracks", trackArray);

            // Put the section in the section array
            sectionArray.put(sectionObject);
        }
        return sectionArray;
    }

    public JSONArray saveTrains(List<DrawableTrain> trainList){
        JSONArray trainArray = new JSONArray();

        for(DrawableTrain t : trainList){
            JSONObject trainObject = new JSONObject();

            trainObject.put("id",t.getTrain().getId());
            trainObject.put("curTrack",t.getCurTrack().getId());
            trainObject.put("length",t.getTrain().getLength());
            trainObject.put("maxSpeed",t.getTrain().getMaxSpeed());
            trainObject.put("direction",t.getTrain().getDirection());
            trainObject.put("orientation",t.getTrain().getOrientation());

            trainArray.put(trainObject);
        }

        return trainArray;
    }

    public JSONArray saveRollingStocks(List<DrawableRollingStock> rollingList){
        JSONArray stockArray = new JSONArray();

        for(DrawableRollingStock t : rollingList){
            JSONObject stockObject = new JSONObject();

            stockObject.put("id",t.getStock().getRollID());
            stockObject.put("length",t.getStock().getLength());
            stockObject.put("direction",t.getDirection());
            stockObject.put("orientation",t.getOrientation());

            if(t.getRollingStockConnected() !=null){
                stockObject.put("conToUs",t.getRollingStockConnected().getStock().getRollID());
            }
            else {
                stockObject.put("conToUs",-1);
            }

            // Check if we are connected to anything or anything is connected to us
            if(t.getConToThis() != null){
                stockObject.put("conTo",t.getConToThis().getStock().getRollID());
            }
            else {
                stockObject.put("conTo",t.getOrientation());
            }
        }
        return stockArray;
    }

    public String getTrackType(DefaultTrack track){
        if(track instanceof StraightHoriz){
            return "Straight horizontal";
        }
        else if(track instanceof StraightVert){
            return "Straight vertical";
        }
        else if(track instanceof Quart1){
            return "Q1";
        }
        else if(track instanceof Quart2){
            return "Q2";
        }
        else if(track instanceof Quart3){
            return "Q3";
        }
        else if(track instanceof Quart4){
            return "Q4";
        }
        else if(track instanceof JunctionTrack){
            return "Junction";
        }

        // Invalid type
        return "";
    }

    public static void main(String[] args){
        Load load = new Load();
        LoadedRailway l = load.loadFromFile("src/tracks/simple_track.json");

        Save save = new Save();
        save.save(l,"src/tracks/","test_save");
    }
}