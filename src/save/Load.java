package save;

import com.sun.org.apache.xpath.internal.SourceTree;
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





    public LoadedRailway loadFromFile(String filePath){
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




            // Set the starts of the tracks
//            setUpStartingLocations(tracks.toArray(new DefaultTrack[tracks.size()]),startingTrack,0);

            Set<Integer> s = new HashSet<>();
            for(int i = 1; i < tracks.size(); i++){
                s.add(i);
            }
            setupTracks(tracks.get(0),tracks.toArray(new DefaultTrack[tracks.size()]),s);

            // Load the trains and rolling stocks
            List<DrawableTrain> trains = loadTrains(obj, sections);
            List<DrawableRollingStock> stocks = loadRollingStocks(obj,sections);

            LoadedRailway railway = new LoadedRailway(sections,tracks.toArray(new DefaultTrack[tracks.size()]),trains,stocks);

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

            //TODO acc and decel
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

    public List<DrawableRollingStock> loadRollingStocks(JSONObject obj, DrawableSection[] sections){
        List<DrawableRollingStock> trains = new ArrayList<>();

        JSONArray rollingStockArray = obj.getJSONArray("stocks");


        return trains;
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
            System.out.println(toIndex);
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




    public void setUpStartingLocations(DefaultTrack[] tracks, int startTrackIndex, int count){
        if(count == tracks.length-1){
            return;
        }

        //thing at starting index has been created
        for(int i = 0; i < tracks.length; i++){
            if(tracks[i].getFrom() == startTrackIndex){
                if(tracks[startTrackIndex] instanceof JunctionTrack){
                    JunctionTrack jt = (JunctionTrack)tracks[startTrackIndex];

                    if(!jt.inBound()){
                        if(jt.getOutboundToThrown() == i){
                            tracks[i].setStart(jt.getTrackThrown());
                        }else {
                            tracks[i].setStart(jt.getStraightTrack());// TODO NOT SURE IF WILL WORK IN ALL SITUATIONS
                        }
                    }
                }
                else {
                    tracks[i].setStart(tracks[startTrackIndex]);
                }

                count++;

                setUpStartingLocations(tracks,i,count);

                return;
            }
        }
    }

    public static DefaultTrack getTrackFromObject(JSONObject trackObject){
        int id = trackObject.getInt("id");
        int length = trackObject.getInt("length");

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

                JunctionTrack junctionTrack = new JunctionTrack(length,6,id,false,inbound);
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

        // Should never happen
        return null;
    }

    public static DefaultTrack getStartTrackFromObject(JSONObject trackObject){
        int id = trackObject.getInt("id");
        int length = trackObject.getInt("length");
        int x = 300;
        int y = 100;

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
                boolean inbound = trackObject.getBoolean("inbound");//TODO
                return new JunctionTrack(x,y,length,6,id,"RIGHT",false,inbound);
            default:
                System.out.println("No Match for type");
        }

        // Should never happen
        return null;
    }



    public static void main(String[] args){
        Load load = new Load();
        LoadedRailway l = load.loadFromFile("src/tracks/atrackandtrains.json");
//         l = load.loadFromFile("src/tracks/simple_track.json");

        for(DefaultTrack t : l.tracks){
            System.out.println(t);
        }

        for(DrawableTrain dt : l.trains){
            System.out.println(dt);
        }

    }


}
