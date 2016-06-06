package save;

import com.sun.org.apache.xpath.internal.SourceTree;
import model.Section;
import org.json.JSONArray;
import org.json.JSONObject;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nicky on 4/06/2016.
 */
public class Load {

    public class LoadedRailway{
        public DrawableSection[] sections;
        public DefaultTrack[] tracks;
        public List<DrawableTrain> trains;
        public List<DrawableRollingStock> stocks;

        public LoadedRailway(DrawableSection[] sections, DefaultTrack[] tracks, List<DrawableTrain> trains, List<DrawableRollingStock> stocks){
            this.sections = sections;
            this.tracks = tracks;
            this.trains = trains;
            this.stocks = stocks;
        }
    }



    public LoadedRailway loadFromFile(String filePath){
        System.out.println("Loading File");
        try {
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

                    track.setFrom(from);
                    track.setTo(to);
                    tracks.add(track);
                    tracksInSection[j] = track;
                }

                // Create the section object

                // Grab the id of the section
                int id = sectionObject.getInt("id");
                int length = sectionObject.getInt("length");
                int from = sectionObject.getInt("from");
                int to = sectionObject.getInt("to");

                sections[i] = new DrawableSection(new Section(id,length,from,to,tracksInSection));//TODO node sure if will work
            }

            List<DrawableTrain> trains = new ArrayList<>();
            List<DrawableRollingStock> stocks = new ArrayList<>();

            LoadedRailway railway = new LoadedRailway(sections,tracks.toArray(new DefaultTrack[tracks.size()]),trains,stocks);

            // Set the starts of the tracks
            setUpStartingLocations(tracks.toArray(new DefaultTrack[tracks.size()]),startingTrack,0);

            System.out.println("Succes Loading Railway File");
            return railway;

        } catch (FileNotFoundException e) {
            System.out.println("Failed Loading File");
            e.printStackTrace();
        }

        System.out.println("Returning null");
        return null;
    }

    public void setUpStartingLocations(DefaultTrack[] tracks, int startTrackIndex, int count){
        if(count == tracks.length-1){
            return;
        }
        for(int i = 0; i < tracks.length; i++){
            if(tracks[i].getFrom() == startTrackIndex){
                System.out.println("ID " + i + " From " + tracks[i].getFrom() + " l " + tracks[i].getLength());
                tracks[i].setStart(tracks[startTrackIndex]);
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
            case "Straight":
                return new StraightHoriz(length,0,id);
            case "Junction":
                boolean inbound = trackObject.getBoolean("inbound");
                return new JunctionTrack(length,6,id,false,inbound);
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
            case "Straight":
                return new StraightHoriz(x,y,length,0,id,"RIGHT");
            case "Junction":
                boolean inbound = trackObject.getBoolean("inbound");
                return new JunctionTrack(x,y,length,6,id,"RIGHT",false,inbound);
            default:
                System.out.println("No Match for type");
        }

        // Should never happen
        return null;
    }



    public static void main(String[] args){
        Load load = new Load();
        LoadedRailway l = load.loadFromFile("src/tracks/simple_track.json");
        for(DefaultTrack t : l.tracks){
           // System.out.println(t);
            System.out.println("X " + t.getStartX() + " Y " + t.getStartY() + " ID " + t.getId());
        }

    }


}
