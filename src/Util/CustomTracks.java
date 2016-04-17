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

    /**
     * Creates a list of section to use for a defualt track on startup
     * */
//    public static ArrayList<DefaultTrack> createBasicTrack(){
//        ArrayList<DefaultTrack> sections = new ArrayList<>();
//        int trackLength = 150;
//
//        DefaultTrack ds1 = new StraightHoriz(new Section(1, 100, null, null, null), 300, 50, trackLength,0, "RIGHT");
//
//        StraightTrack[] tracks = new StraightTrack[2];
////        tracks[0] = new JunctionTrack()
//
//        DefaultTrack ds2 = new StraightHoriz(new Section(2, 100, null, null, null), trackLength,0);
//        ds1.getSection().setCandetect(true);
//        ds2.getSection().setCandetect(false);
//        ds2.setStart(ds1);
//
//
//        DefaultTrack ds3 = new Quart2(new Section(3, 100, null, null, null), trackLength*4,2);
//        ds3.getSection().setCandetect(true);
//        ds3.setStart(ds2);
//
//        DefaultTrack ds4 = new StraightVert(new Section(4, 100, null, null, null), trackLength,5);
//        ds4.getSection().setCandetect(false);
//        ds4.setStart(ds3);
//
//        DefaultTrack ds5 = new Quart3(new Section(5, 100, null, null, null), trackLength*4,3);
//        ds5.getSection().setCandetect(true);
//        ds5.setStart(ds4);
//
//        DefaultTrack ds6 = new StraightHoriz(new Section(6, 100, null, null, null), trackLength,0);
//        ds6.getSection().setCandetect(false);
//        ds6.setStart(ds5);
//
//        DefaultTrack ds7 = new StraightHoriz(new Section(7, 100, null, null, null), trackLength,0);
//        ds7.getSection().setCandetect(true);
//        ds7.setStart(ds6);
//
//        DefaultTrack ds8 = new Quart4(new Section(8, 100, null, null, null), trackLength*4,4);
//        ds8.getSection().setCandetect(false);
//        ds8.setStart(ds7);
//
//        DefaultTrack ds9 = new StraightVert(new Section(9, 100, null, null, null), trackLength,5);
//        ds9.getSection().setCandetect(true);
//        ds9.setStart(ds8);
//
//        DefaultTrack ds10 = new Quart1(new Section(10, 100, null, null, null), trackLength*4,1);
//        ds10.getSection().setCandetect(false);
//        ds10.setStart(ds9);
//
//        sections.add(ds1);
//        sections.add(ds2);
//        sections.add(ds3);
//        sections.add(ds4);
//        sections.add(ds5);
//        sections.add(ds6);
//        sections.add(ds7);
//        sections.add(ds8);
//        sections.add(ds9);
//        sections.add(ds10);
//
//        new TrackBuilder(null).linkUpSections(sections);
//
//        return sections;
//    }

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
                new Quart4(400,4,curID++)
        };

        linkTracks(tracks);

        // Start section
        DrawableSection ds =  new DrawableSection(new Section(99,200,new DefaultTrack[]{tracks[0],tracks[1]}));


        // First turn
        DrawableSection firstQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[2]}));

        // Second turn
        DrawableSection secondQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[3]}));

        // Top straight bit
        DrawableSection ds2 =  new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[4],tracks[5]}));

        // Third turn
        DrawableSection thirdQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[6]}));

        // Last turn
        DrawableSection fourthQ =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[7]}));


        railway.add(ds);
        railway.add(firstQ);
        railway.add(secondQ);
        railway.add(ds2);
        railway.add(thirdQ);
        railway.add(fourthQ);

        return railway;
    }

    private static void linkTracks(DefaultTrack[] tracks){
        if(tracks.length < 2)return;

        for(int i = 1; i < tracks.length; i++){
            tracks[i].setStart(tracks[i-1]);
        }

        // TODO later consider multiple too tracks
        for(int i = 0; i < tracks.length-1; i++){
               tracks[i].setTo(tracks[i+1]);
        }

        tracks[tracks.length-1].setTo(tracks[0]);
    }


//    public static ArrayList<DefaultTrack> createModelTrack(){
//        ArrayList<DefaultTrack> sections = new ArrayList<>();
//
//        double canvasWidth = 1200;
//
//        int curID = 0;
//
////        public DrawableSection(int id, double length, DrawableSection from, DrawableSection to, StraightTrack[] tracks){
////        StraightHoriz(DrawableSection section, int startX,  int startY, int length, int drawID, String direction){
//
//        double padding = 100;
//        double stdSize = (canvasWidth - padding)/5;// 5 being the number of standard pieces in the width of the track
//
//        double startX = stdSize + 50;
//        double startY = stdSize*2 + 100;
//
//        // The first section of the track is a standard straight section where def direction is RIGHT
//        Track[] tracks = new Track[1];
//        tracks[0] = new StraightTrack(curID, stdSize);
//        Section section1 = new Section(curID,stdSize, tracks);
//        DefaultTrack s1 = new StraightHoriz(section1,(int)startX, (int)startY, (int)stdSize, 0, "RIGHT");
//
//        sections.add(s1);
//        curID++;
//
//        // second track is also a straight section with one track in it
//        Track[] tracks1 = new Track[1];
//        tracks1[0] = new StraightTrack(curID, stdSize);
//        Section section2 = new Section(curID,stdSize, tracks1);
//        DefaultTrack s2 = new StraightHoriz(section2, (int)stdSize, 0);
//
//        sections.add(s2);
//        curID++;
//
//        // Third track has a junction and a curved track
//        Track[] tracks3 = new Track[2];
//        Section section3 = new Section(curID,stdSize, tracks3);//TODO figure out how big a junction should be
////        tracks[0] = new JunctionTrack(section3,curID,false);
//        tracks[1] = new StraightTrack(curID,stdSize);//TODO is actually curved but not sure how I want to implement this yet draw track or just draw curve since it does nothing extra
//        DefaultTrack
//
//
////        sections.add(section3);
//
//
//        return sections;
//
//    }

}
