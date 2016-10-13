package util.save;

import org.json.JSONArray;
import org.json.JSONObject;
import simulation.Drawable.DrawableRollingStock;
import simulation.Drawable.DrawableTrain;
import simulation.Drawable.tracks.*;
import simulation.Simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Nicky on 4/06/2016.
 */
public class Save {

    public void save(LoadedRailway railway, String location){
        try {
            FileWriter file = new FileWriter(location+".json");
            file.write(getJSONString(railway));
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getJSONString(LoadedRailway railway){

        // Create the top level JSON object
        JSONObject railwayObj = new JSONObject();

        // Save the sections includes the util.tracks
        railwayObj.put("sections", saveSections(railway.sections));

        // Save the trains
        railwayObj.put("trains", saveTrains(railway.trains));

        // Save the rolling stock
        railwayObj.put("stocks", saveRollingStocks(railway.stocks));

        // Useful for testing
        return railwayObj.toString(1);
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
            }
            else{
                sectionObject.put("start",false);
            }

            sectionObject.put("detect",s.getSection().canDetect());
            sectionObject.put("from",s.getSection().getFromIndexNat());
            sectionObject.put("length",s.getSection().getLength()/Simulation.METER_MULTIPLIER);
            sectionObject.put("to",s.getSection().getToIndexNat());
            sectionObject.put("hasJunc",s.getSection().hasJunctionTrack());


            // Always pyt junction index default is -1
            sectionObject.put("junctionIndex",s.getSection().getJuncSectionIndex());

            // Save junction information
            if(s.getSection().hasJunctionTrack()){
                JunctionTrack jt = s.getSection().getJunction();
                sectionObject.put("inbound",jt.inBound());
                sectionObject.put("juncID",jt.getId());
            }

            JSONArray trackArray = new JSONArray();

            for(DefaultTrack t : s.getTracks()){
                JSONObject trackObject = new JSONObject();
                trackObject.put("type", getTrackType(t));
                trackObject.put("id",t.getId());
                trackObject.put("to",t.getTo());
                trackObject.put("toJunc",t.getJuncTo());
                trackObject.put("fromJunc",t.getJuncFrom());

                if(start){
                    trackObject.put("x",t.getStartX());
                    trackObject.put("y",t.getStartY());
                    start = false;
                }

                trackObject.put("from",t.getFrom());
                trackObject.put("length",t.getLength()/Simulation.METER_MULTIPLIER);
                if(getTrackType(t).equals("Junction")){
                    JunctionTrack jt = (JunctionTrack)t;
                    trackObject.put("inbound",jt.inBound());
                    if(jt.inBound()){
                        trackObject.put("thrown",jt.getInboundFromThrown());
                    }
                    else {
                        trackObject.put("thrown",jt.getOutboundToThrown());
                    }
                    trackObject.put("draw",jt.getDrawDirection());
                }
                trackArray.put(trackObject);
            }
            sectionObject.put("tracks", trackArray);

            // Put the section in the section array
            sectionArray.put(sectionObject);
            start = false;
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
            trainObject.put("curSectionID",t.getCurSection().getSection().getID());
            trainObject.put("acceleration",t.getTrain().getAcceleration());

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
            stockObject.put("curTrack",t.getCurTrack().getId());
            stockObject.put("weight",t.getStock().getWeight());


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
            stockArray.put(stockObject);
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
        File f = new File("src/util.tracks/simple_track.json");
        LoadedRailway l = load.loadFromFile(f,"src/util.tracks/simple_track.json");

        Save save = new Save();
        save.save(l,"src/util.tracks/test_save");
    }
}
