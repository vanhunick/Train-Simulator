package Util;

import model.Section;
import view.Drawable.section_types.*;
import view.Drawable.section_types.JunctionTrack;

/**
 * Created by vanhunick on 16/04/16.
 */
public class CustomTracks {

    private DrawableSection[] sections;
    private DefaultTrack[] tracks;//tracks in the

    public CustomTracks(String trackType){
        if(trackType.equals("DEF")){// the defualt track
            this.tracks = getDefTracks();
            this.sections = createDefSections(tracks);
        }
    }

    public DefaultTrack[] getDefTracks(){
        int curID = 0;
        return  new DefaultTrack[]{
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
    }

    public DrawableSection[] createDefSections(DefaultTrack[] tracks){
        DrawableSection[] railway = new DrawableSection[7];
        int curID = 0;

        railway[0] = new DrawableSection(new Section(99,200,new DefaultTrack[]{tracks[0],tracks[1]}));
        tracks[1].setStart(tracks[0]);

        // First turn
        railway[1] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[2]}));
        tracks[2].setStart(tracks[1]);
        railway[1].getSection().setCandetect(true);

        // Second turn
        railway[2] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[3]}));
        tracks[3].setStart(tracks[2]);

        // Top straight bit
        railway[3] =  new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[4],tracks[5]}));
        tracks[4].setStart(tracks[3]);
        tracks[5].setStart(tracks[4]);

        // Make it be able to detect
        railway[3].getSection().setCandetect(true);

        // Third turn
        railway[4] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[6]}));
        tracks[6].setStart(tracks[5]);

        // Last turn with junction
        railway[5] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[7],tracks[8]}));
        tracks[7].setStart(tracks[6]);
        tracks[8].setStart(tracks[7]);

        railway[6] =  new DrawableSection(new Section(curID++,150,new DefaultTrack[]{tracks[9]}));
        tracks[9].setStart(tracks[8]);

        // Set to
        tracks[0].setTo(1);
        tracks[1].setTo(2);
        tracks[2].setTo(3);
        tracks[3].setTo(4);
        tracks[4].setTo(5);
        tracks[5].setTo(6);
        tracks[6].setTo(7);
        tracks[7].setTo(8);

        tracks[0].setFrom(7);
        tracks[7].setFrom(6);

        tracks[6].setFrom(5);
        tracks[5].setFrom(4);
        tracks[4].setFrom(3);
        tracks[3].setFrom(2);
        tracks[2].setFrom(1);
        tracks[1].setFrom(0);
//        tracks[8].setFrom(7);
        // Set junction
        JunctionTrack jt = (JunctionTrack)tracks[8];
        jt.setToNotThrownTrack(0);
        jt.setToThrownTrack(9);

        return railway;
    }

    public DefaultTrack[] getTracks(){
        return this.tracks;
    }

    public DrawableSection[] getSections(){
        return this.sections;
    }
}
