package save;

import model.RollingStock;
import model.Section;
import model.Train;
import org.json.JSONArray;
import org.json.JSONObject;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Nicky on 4/06/2016.
 */
public class Load {

    public LoadedRailway loadFromFile(File file, String filePath){
        System.out.println("Loading File");
        try {
            // Load in the JSON file into a string
            String str = "";
            Scanner scan = new Scanner(new File(filePath));
            while (scan.hasNext())
                str += scan.nextLine();
            scan.close();

            // build a JSON object
            JSONObject obj = new JSONObject(str);

            // Grab the sections array
            JSONArray sectionsArray = obj.getJSONArray("sections");

            // Create a new array the size of the array in the file
            DrawableSection[] sections = new DrawableSection[sectionsArray.length()];

            // Go through each of the sections
            List<DefaultTrack> tracks = new ArrayList<>();
            int startingTrack = -1;
            for (int i = 0; i < sectionsArray.length(); i++){

                JSONObject sectionObject = sectionsArray.getJSONObject(i);

                JSONArray trackArray = sectionObject.getJSONArray("tracks");

                DefaultTrack[] tracksInSection = new DefaultTrack[trackArray.length()];

                // Go through and create the track array
                for(int j = 0; j < trackArray.length(); j++){
                    JSONObject trackObject = trackArray.getJSONObject(j);
                    DefaultTrack track = getTrackFromObject(trackObject);

                    if(j == 0 && sectionObject.getBoolean("start")){
                        track = getStartTrackFromObject(trackObject);
                        startingTrack = tracks.size();
                    }

                    int from = trackObject.getInt("from");
                    int to = trackObject.getInt("to");
                    int toJunc = trackObject.getInt("toJunc");
                    int fromJunc = trackObject.getInt("fromJunc");

                    track.setFrom(from);
                    track.setTo(to);
                    track.setJuncFrom(fromJunc);
                    track.setJuncTo(toJunc);
                    tracks.add(track);
                    tracksInSection[j] = track;
                }

                // Create the section object

                // Grab the id of the section
                int id = sectionObject.getInt("id");
                int length = sectionObject.getInt("length");
                int from = sectionObject.getInt("from");
                int to = sectionObject.getInt("to");
                boolean detect = sectionObject.getBoolean("detect");
                int junctionIndex = 0;
                if(sectionObject.getBoolean("hasJunc")){
                    junctionIndex = sectionObject.getInt("junctionIndex");
                }

                sections[i] = new DrawableSection(new Section(id,length,from,to,tracksInSection));//TODO node sure if will work
                sections[i].getSection().setCandetect(detect);
                sections[i].getSection().setJuncSectionIndex(junctionIndex);
            }

            Set<Integer> s = new HashSet<>();
            for(int i = 1; i < tracks.size(); i++){
                s.add(i);
            }
            setupTracks(tracks.get(0),tracks.toArray(new DefaultTrack[tracks.size()]),s);

            // Load the trains and rolling stocks
            List<DrawableTrain> trains = loadTrains(obj, sections);
            List<DrawableRollingStock> stocks = loadRollingStocks(obj,sections, trains);

            LoadedRailway railway = new LoadedRailway(file,sections,tracks.toArray(new DefaultTrack[tracks.size()]),trains,stocks);

            System.out.println("Success Loading Railway File");
            return railway;

        } catch (FileNotFoundException e) {
            System.out.println("Failed Loading File");
            e.printStackTrace();
        }
        return null;
    }

    public List<DrawableTrain> loadTrains(JSONObject obj, DrawableSection[] sections){
        List<DrawableTrain> trains = new ArrayList<>();

        JSONArray trainArray = obj.getJSONArray("trains");

        for(int i = 0; i < trainArray.length(); i++){
            JSONObject trainObject = trainArray.getJSONObject(i);

            int id = trainObject.getInt("id");
            int curTrack = trainObject.getInt("curTrack");
            int length = trainObject.getInt("length");
            int maxSpeed = trainObject.getInt("maxSpeed");
            boolean dir = trainObject.getBoolean("direction");
            boolean ori = trainObject.getBoolean("orientation");

            Train t = new Train(id,length,maxSpeed,dir,ori,71000);//TODO save weight

            DefaultTrack track = null;
            DrawableSection section = null;
            for(DrawableSection s : sections){
                if(s.getTrackWithID(curTrack) != null){
                    track = s.getTrackWithID(curTrack);
                    section = s;
                    break;
                }
            }
            DrawableTrain dt = new DrawableTrain(t,section,track);
            trains.add(dt);
        }
        return trains;
    }

    public List<DrawableRollingStock> loadRollingStocks(JSONObject obj, DrawableSection[] sections, List<DrawableTrain> trains){
        List<DrawableRollingStock> stocks = new ArrayList<>();

        JSONArray rollingStockArray = obj.getJSONArray("stocks");

        for(int i = 0; i < rollingStockArray.length(); i++){
            JSONObject stockObject = rollingStockArray.getJSONObject(i);

            int id = stockObject.getInt("id");
            int curTrack = stockObject.getInt("curTrack");
            int length = stockObject.getInt("length");
            boolean dir = stockObject.getBoolean("direction");
            boolean ori = stockObject.getBoolean("orientation");
            double weight = stockObject.getDouble("weight");

            RollingStock stock = new RollingStock(length, id, weight);
            DrawableRollingStock drawStock = new DrawableRollingStock(stock,null,dir,ori);

            DefaultTrack track = null;
            for(DrawableSection s : sections){
                if(s.getTrackWithID(curTrack) != null){
                    track = s.getTrackWithID(curTrack);
                    break;
                }
            }
            drawStock.setCurTrack(track);
            stocks.add(drawStock);
        }

        // We have to go through again to connect them all up
        for(int i = 0; i < rollingStockArray.length(); i++){
            JSONObject stockObject = rollingStockArray.getJSONObject(i);

            // Get the id of the stock we are connected to
            int conToUs = stockObject.getInt("conToUs");
            int conTo = stockObject.getInt("conTo");

            for(DrawableRollingStock s : stocks){
                if(conToUs != -1 && s.getStock().getRollID() == conToUs){
                    stocks.get(i).setRollingStockConToUs(s);// I has to be the same in the list as the array
                }
                if(conTo != -1 &&s.getStock().getRollID() == conTo){
                    stocks.get(i).setConnection(s);// I has to be the same in the list as the array
                }
            }

            // Connect to train
            for(DrawableTrain t : trains){
                if(conToUs != -1 && t.getTrain().getId() == conTo){
                    stocks.get(i).setConnection(t);// I has to be the same in the list as the array
                }
            }
        }
        return stocks;
    }


    public void setupTracks(DefaultTrack currentTrack, DefaultTrack[] tracks, Set<Integer> todo){
        if(currentTrack instanceof JunctionTrack){
            JunctionTrack jt = (JunctionTrack)currentTrack;
            int toIndex = 0;
            if(jt.inBound()){
                toIndex = jt.getInboundTo();
            } else {
                toIndex = jt.getTo();
            }
            if(todo.contains(toIndex)){
                tracks[toIndex].setStart(jt);
                todo.remove(toIndex);
                setupTracks(tracks[toIndex], tracks,todo);
            }
            if(!jt.inBound() && todo.contains(jt.getOutboundToThrown())){
                tracks[jt.getOutboundToThrown()].setStart(jt.getTrackThrown());
                setupTracks(tracks[jt.getOutboundToThrown()], tracks,todo);
            }
        }
        else {
            if(todo.contains(currentTrack.getTo())){
                tracks[currentTrack.getTo()].setStart(currentTrack);
                setupTracks(tracks[currentTrack.getTo()], tracks,todo);
            }
        }
    }


    /**
     * Creates and returns a track in the railway. From the JSON object
     *
     * @param trackObject the track to get the data from
     *
     * @return The track
     * */
    public static DefaultTrack getTrackFromObject(JSONObject trackObject){
        int id = trackObject.getInt("id"); // Id of the track
        int length = trackObject.getInt("length"); // Length of the track

        // Process the type of track
        switch (trackObject.getString("type")){
            case "Q1":
                return new Quart1(length,1,id);
            case "Q2":
                return new Quart2(length,2,id);
            case "Q3":
                return new Quart3(length,3,id);
            case "Q4":
                return new Quart4(length,4,id);
            case "Straight horizontal":
                return new StraightHoriz(length,0,id);
            case "Straight vertical":
                return new StraightVert(length,5,id);
            case "Junction":
                boolean inbound = trackObject.getBoolean("inbound");
                String drawDirection = trackObject.getString("draw");
                JunctionTrack junctionTrack = new JunctionTrack(length,6,id,false,inbound,drawDirection);
                int thrownIndex = trackObject.getInt("thrown");
                if(!inbound){
                    junctionTrack.setOutboundToThrown(thrownIndex);
                } else{
                  junctionTrack.setInboundFromThrown(thrownIndex);
                }
                return junctionTrack;
            default:
                System.out.println("No Match for type");
        }
        return null;// Should never happen
    }

    /**
     * Creates and returns the starting track of the railway. From the JSON object
     *
     * @param trackObject the track to get the data from
     *
     * @return The starting track
     * */
    public static DefaultTrack getStartTrackFromObject(JSONObject trackObject){

        int id = trackObject.getInt("id"); // The id of the track
        int length = trackObject.getInt("length"); // The length of the track
        int x = trackObject.getInt("x"); // The starting x value of the location
        int y = trackObject.getInt("y"); // The starting y value of the location

        // Process the type of track
        switch (trackObject.getString("type")){
            case "Q1":
                return new Quart1(x,y,length,1,"RIGHT",id);//TODO put direction in file
            case "Q2":
                return new Quart2(x,y,length,2,"RIGHT",id);
            case "Q3":
                return new Quart3(x,y,length,3,"RIGHT",id);
            case "Q4":
                return new Quart4(x,y,length,4,"RIGHT",id);
            case "Straight horizontal":
                return new StraightHoriz(x,y,length,0,id,"RIGHT");
            case "Straight vertical":
                return new StraightVert(x,y,length,5,"RIGHT",id);
            case "Junction":
                return new JunctionTrack(x,y,length,6,id,"RIGHT",false,trackObject.getBoolean("inbound"), trackObject.getString("draw"));
            default:
                System.out.println("No Match for type");
        }
        return null;// Should never happen
    }
}
