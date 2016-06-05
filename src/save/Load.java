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
        DrawableSection[] sections;
        DefaultTrack[] tracks;
        List<DrawableTrain> trains;
        List<DrawableRollingStock> stocks;

        public LoadedRailway(DrawableSection[] sections, DefaultTrack[] tracks, List<DrawableTrain> trains, List<DrawableRollingStock> stocks){
            this.sections = sections;
            this.tracks = tracks;
            this.trains = trains;
            this.stocks = stocks;
        }
    }



    public LoadedRailway loadSimpleTrack(){
        try {
            String str = "";
            Scanner scan = new Scanner(new File("src/tracks/simple_track.json"));
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
            for (int i = 0; i < sectionsArray.length(); i++){

                JSONObject sectionObject = sectionsArray.getJSONObject(i);



                JSONArray trackArray = sectionObject.getJSONArray("tracks");


                DefaultTrack[] tracksInSection = new DefaultTrack[trackArray.length()];

                // Go through and create the track array
                for(int j = 0; j < trackArray.length(); j++){
                    JSONObject trackObject = trackArray.getJSONObject(i);
                    tracksInSection[i] = getTrackFromObject(trackObject);
                    tracks.add(getTrackFromObject(trackObject));
                }

                // Create the section object

                // Grab the id of the section
                int id = sectionObject.getInt("id");
                int length = sectionObject.getInt("length");
                int from = sectionObject.getInt("from");
                int to = sectionObject.getInt("to");

                sections[i] = new DrawableSection(new Section(id,length,sections[from].getSection(),sections[to].getSection(),tracksInSection));//TODO node sure if will work
            }

            List<DrawableTrain> trains = new ArrayList<>();
            List<DrawableRollingStock> stocks = new ArrayList<>();

            LoadedRailway railway = new LoadedRailway(sections,tracks.toArray(new DefaultTrack[tracks.size()]),trains,stocks);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }
//
//    public DefaultTrack[] getTracksFromSections(DrawableSection[] sections, int numbTracks){
//        DefaultTrack[] tracks = new DefaultTrack[numbTracks];
//
//        for(int i = 0; i < sections.length; i++){
//            DefaultTrack[] secTracks = sections[i].getTracks();
//            for(int j = 0; j < secTracks.length; j++){
//
//            }
//        }
//    }

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

    public static void main(String[] args){
        Load load = new Load();
        load.loadSimpleTrack();
    }


}
