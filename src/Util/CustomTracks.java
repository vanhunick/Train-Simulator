package Util;

import model.Section;
import view.Drawable.section_types.*;
import view.Drawable.section_types.JunctionTrack;
import view.Drawable.section_types.StraightTrack;
import view.Drawable.track_types.Track;
import view.TrackBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanhunick on 16/04/16.
 */
public class CustomTracks {



    public static List<DrawableSection> createBasicDrawTrack(){
        List<DrawableSection> railway = new ArrayList<>();

        int curID = 0;

        // Create all the tracks first makes it easy for linking
        DefaultTrack[] tracks = new DefaultTrack[]{
                new StraightHoriz(300,600,200,0,curID++,"RIGHT"),
                new StraightHoriz(200,0,curID++),
                new Quart3(400,3,curID++),
                new Quart2(400,2,curID++),
                new StraightHoriz(200,0,curID++),
                new StraightHoriz(200,0,curID++),
                new Quart1(400,1,curID++),
                new Quart4(400,4,curID++),
                new JunctionTrack(100,6,curID++,false),
                new StraightHoriz(200,0,curID++)
        };

//        linkTracks(tracks);

        // Start section
        DrawableSection ds =  new DrawableSection(new Section(99,200,new DefaultTrack[]{tracks[0],tracks[1]}));
        tracks[1].setStart(tracks[0]);

        // First turn
        DrawableSection firstQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[2]}));
        tracks[2].setStart(tracks[1]);

        // Second turn
        DrawableSection secondQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[3]}));
        tracks[3].setStart(tracks[2]);

        // Top straight bit
        DrawableSection ds2 =  new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[4],tracks[5]}));
        tracks[4].setStart(tracks[3]);
        tracks[5].setStart(tracks[4]);

        // Third turn
        DrawableSection thirdQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[6]}));
        tracks[6].setStart(tracks[5]);

        // Last turn with junction
        DrawableSection fourthQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[7],tracks[8]}));
        tracks[7].setStart(tracks[6]);
        tracks[8].setStart(tracks[7]);

        DrawableSection afterJuncSec =  new DrawableSection(new Section(curID++,150,new DefaultTrack[]{tracks[9]}));
        tracks[9].setStart(tracks[8]);

        // Set to
        tracks[0].setTo(tracks[1]);
        tracks[1].setTo(tracks[2]);
        tracks[2].setTo(tracks[3]);
        tracks[3].setTo(tracks[4]);
        tracks[4].setTo(tracks[5]);
        tracks[5].setTo(tracks[6]);
        tracks[6].setTo(tracks[7]);
        tracks[7].setTo(tracks[8]);

        // Set junction
        JunctionTrack jt = (JunctionTrack)tracks[8];
        jt.setToNotThrownTrack(tracks[0]);
        jt.setToThrownTrack(tracks[9]);

        railway.add(ds);
        railway.add(firstQ);
        railway.add(secondQ);
        railway.add(ds2);
        railway.add(thirdQ);
        railway.add(fourthQ);
        railway.add(afterJuncSec);

        return railway;
    }

    private static void linkTracks(DefaultTrack[] tracks){
        if(tracks.length < 2)return;

        for(int i = 1; i < tracks.length; i++){
            tracks[i].setStart(tracks[i-1]);
        }

        // TODO later consider multiple too tracks
        for(int i = 0; i < tracks.length-1; i++){
            if(tracks[i] instanceof JunctionTrack)continue;//Ignore junction tracks they should be set somewhere else
               tracks[i].setTo(tracks[i + 1]);
        }

        tracks[tracks.length-1].setTo(tracks[0]);
    }
}
