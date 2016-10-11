package tests;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import simulation.Drawable.DrawableRollingStock;
import simulation.Drawable.DrawableTrain;
import simulation.Drawable.tracks.DefaultTrack;
import simulation.Drawable.tracks.DrawableSection;
import simulation.Drawable.tracks.StraightHoriz;
import simulation.model.RollingStock;
import simulation.model.Section;
import simulation.model.Train;
import util.save.Load;
import util.save.LoadedRailway;
import util.save.Save;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nicky on 6/06/2016.
 */
public class LoadAndSaveTests {
//
//    @Test
//    public void loadAndSaveEqaulity(){
//        Load load = new Load();
//        File f = new File("src/util.tracks/simple_track.json");
//        LoadedRailway l = load.loadFromFile(f,"src/util.tracks/simple_track.json");
//
//
//        String str = "";
//        Scanner scan = null;
//        try {
//            scan = new Scanner(new File("src/util.tracks/simple_track.json"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        while (scan.hasNext())
//            str += scan.nextLine();
//        scan.close();
//
//        // build a JSON object
//        JSONObject obj = new JSONObject(str);
//
//        Save save = new Save();
//        String JSON = save.getJSONString(l);
//        JSON.replaceAll("\\s+", "");
//        String orign = obj.toString().replaceAll("\\s+", "");
//        int count = 0;
//        for(int i = 0; i < JSON.length(); i++){
//            if(JSON.charAt(i) != orign.charAt(i)){
//                count = i;
//                break;
//            }
//        }
//        //TODO test won't work because fields not inserted the same way
//        System.out.println("Count equal" + count + " Out of " + orign.length() + " JSON " + JSON.substring(count));
//        assert (JSON.toString().replaceAll("\\s+","").equals(obj.toString().replaceAll("\\s+","")));
//    }


    /**
     * A helper function that returns a loaded railway
     * */
    public LoadedRailway getEmptyLoadedRailway(){
        List<DrawableTrain> trainList = new ArrayList<>();
        List<DrawableRollingStock> stockList = new ArrayList<>();
        return new LoadedRailway(null,new DrawableSection[0],new DefaultTrack[0],trainList,stockList);
    }

    public DefaultTrack getStandardTrack(){
        return new StraightHoriz(100,100,100,0,1,"RIGHT");
    }

    public DrawableSection getStandardDrawableSection(DefaultTrack[] tracks){
        return new DrawableSection(getStandardSection(tracks));
    }

    public Section getStandardSection(DefaultTrack[] tracks){
        return new Section(1,100,0,1,tracks);
    }

    public DefaultTrack[] getDefaultTrackArrayWithOneTrack(){
        return new DefaultTrack[]{getStandardTrack()};
    }


    @Test
    public void SaveTrainTest(){
        Save save = new Save();

        // Grabs an empty loaded railway required by the save class
        LoadedRailway loadedRailway = getEmptyLoadedRailway();

        // Create the train to test against
        Train train = new Train(1,100,200,true,false,300,400);

        // Get a track array with one track in it needed to the current track id
        DefaultTrack[] tracks = getDefaultTrackArrayWithOneTrack();

        // Create the drawable train with the trac array
        DrawableTrain drawableTrain = new DrawableTrain(train,getStandardDrawableSection(tracks),tracks[0]);

        // Add the train to the loaded railway so it can be saved
        loadedRailway.trains.add(drawableTrain);

        // Save it
        String json = save.getJSONString(loadedRailway);

        // Load it back into a JSON object
        JSONObject obj = new JSONObject(json);

        JSONObject trainObject = obj.getJSONArray("trains").getJSONObject(0);

        // Asserts to check all of the train values are identical
        assert train.getId() == trainObject.getInt("id");

        assert drawableTrain.getCurTrack().getId() == trainObject.getInt("curTrack");

        assert train.getLength() == trainObject.getInt("length");

        assert train.getMaxSpeed() == trainObject.getInt("maxSpeed");

        assert train.getDirection() == trainObject.getBoolean("direction");

        assert train.getOrientation() == trainObject.getBoolean("orientation");
    }

    @Test
    public void SaveTrainRandomValues(){
        Save save = new Save();

        // Grabs an empty loaded railway required by the save class
        LoadedRailway loadedRailway = getEmptyLoadedRailway();

        int randomID = (int)(Math.random()*1000);
        int randomLength = (int)(Math.random()*1000);
        int randomSpeed= (int)(Math.random()*1000);
        boolean randomOrientation = (int)(Math.random()*1) == 1 ? true : false;
        boolean randomDirection = (int)(Math.random()*2) == 1 ? true : false;
        double randomWeight = (int)(Math.random()*1000);
        double randomAcceleration = (int)(Math.random()*1000);
        System.out.println("ID " + randomID + " L " + randomLength + " S " + randomSpeed +" O " + randomOrientation+ " D " + randomDirection +"  ");
        // Create the train to test against
        Train train = new Train(randomID,randomLength,randomSpeed,randomDirection,randomOrientation,randomWeight,randomAcceleration);

        // Get a track array with one track in it needed to the current track id
        DefaultTrack[] tracks = getDefaultTrackArrayWithOneTrack();

        // Create the drawable train with the trac array
        DrawableTrain drawableTrain = new DrawableTrain(train,getStandardDrawableSection(tracks),tracks[0]);

        // Add the train to the loaded railway so it can be saved
        loadedRailway.trains.add(drawableTrain);

        // Save it
        String json = save.getJSONString(loadedRailway);

        // Load it back into a JSON object
        JSONObject obj = new JSONObject(json);

        JSONObject trainObject = obj.getJSONArray("trains").getJSONObject(0);

        // Asserts to check all of the train values are identical
        assert train.getId() == trainObject.getInt("id");

        assert drawableTrain.getCurTrack().getId() == trainObject.getInt("curTrack");

        assert train.getLength() == trainObject.getInt("length");

        assert train.getMaxSpeed() == trainObject.getInt("maxSpeed");

        assert train.getDirection() == trainObject.getBoolean("direction");

        assert train.getOrientation() == trainObject.getBoolean("orientation");
    }

    @Test
    public void saveRollingStock(){
        RollingStock stock = new RollingStock(100,1,200);

        DrawableRollingStock drawableRollingStock = new DrawableRollingStock(stock,null,true,false);
        drawableRollingStock.setCurTrack(getStandardTrack());

        LoadedRailway loadedRailway = getEmptyLoadedRailway();

        loadedRailway.stocks.add(drawableRollingStock);

        Save save = new Save();
        String json = save.getJSONString(loadedRailway);

        JSONObject obj = new JSONObject(json);
        System.out.println(json);
        JSONObject stockObject = obj.getJSONArray("stocks").getJSONObject(0);

        assert stock.getRollID() == stockObject.getInt("id");

        assert drawableRollingStock.getCurTrack().getId() == stockObject.getInt("curTrack");

        assert stock.getLength() == stockObject.getDouble("length"); // TODO // FIXME: 8/10/16 Length in rolling stock

        assert drawableRollingStock.getOrientation() == stockObject.getBoolean("orientation");

        assert drawableRollingStock.getDirection() == stockObject.getBoolean("direction");

        assert stock.getWeight() == stockObject.getDouble("weight");
    }

}
