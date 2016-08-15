package Util;

import model.RollingStock;
import model.Section;
import model.Train;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.Movable;
import view.Drawable.section_types.*;
import view.Drawable.section_types.JunctionTrack;
import view.Simulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanhunick on 16/04/16.
 */
public class CustomTracks {

    // Drawable section of the railway
    private DrawableSection[] sections;

    // The tracks in the railway
    private DefaultTrack[] tracks;//tracks in the

    /**
     * Creates a new CustomTracks objects used to create some default tracks
     *
     * @param trackType the type of track to create
     * */
    public CustomTracks(String trackType){
        if(trackType.equals("DEF")){
            this.tracks = getDefTracks();
            this.sections = createDefSections(tracks);
        }
        else if(trackType.equals("FULL")){
            this.tracks = getFullTracks();
            this.sections = getFullSection(tracks);
        }
        else if(trackType.equals("JUNC")){
            this.tracks = junctionTestTracks();
            this.sections = getJunctionTestSect(tracks);
        }
    }

    public DefaultTrack[] getFullTracks(){
        int curID = 0;

        int length = 260;
        int innerOffset = length/2;
        int lQ = length*2;
        return  new DefaultTrack[]{
                // s1
                new StraightHoriz(550,650,length,0,curID++,"RIGHT"), // 0
                // s2
                new StraightHoriz(length,0,curID++), // 1
                // s3
                new JunctionTrack(65,6,curID++,false,true, "UP"), // 2
                new Quart3(lQ,3,curID++), // 3
                // s4
                new Quart2(lQ,2,curID++),// 4
                // s5
                new StraightHoriz(length + length/4,0,curID++), // 5
                // s6
                new StraightHoriz(length + length/4,0,curID++), // 6
                // s7
                new Quart1(lQ,1,curID++),// 7
                // s8
                new Quart4(lQ,4,curID++),// 8
                new JunctionTrack(length/4,6,curID++,false,false,"UP"), // 9
                // s9
                new StraightHoriz(length,0,curID++), // 10
                // s10
                new StraightHoriz(length,0,curID++), // 11
                // s11
                new JunctionTrack(length/4,6,curID++,false,true,"UP"), // 12
                new Quart3(lQ - innerOffset,3,curID++), // 13
                // s12
                new Quart2(lQ - innerOffset,2,curID++),// 14
                // s13
                new StraightHoriz(length + length/4,0,curID++), // 15
                // s14
                new StraightHoriz(length + length/4,0,curID++), // 16
                // s15
                new Quart1(lQ - innerOffset,1,curID++),// 17
                // s16
                new Quart4(lQ - innerOffset,4,curID++),// 18
                new JunctionTrack(length/4,6,curID++,false,false,"UP"), // 19
                // s17
                new StraightHoriz(length,0,curID++), // 20
                // s18
                new StraightHoriz(length,0,curID++), // 21
        };
    }

    public DrawableSection[] getFullSection(DefaultTrack[] tracks){
        DrawableSection[] railway = new DrawableSection[18];

        int curID = 0;

        // Outer loop
        railway[0] = new DrawableSection(new Section(99,200,new DefaultTrack[]{tracks[0]}));
        railway[1] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[1]}));
        railway[2] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[2],tracks[3]}));
        railway[3] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[4]}));
        railway[4] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[5]}));
        railway[5] =  new DrawableSection(new Section(101,300,new DefaultTrack[]{tracks[6]}));
        railway[6] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[7]}));
        railway[7] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[8],tracks[9]}));


        railway[8] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[10]}));
        railway[9] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[11]}));
        railway[10] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[12],tracks[13]}));
        railway[11] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[14]}));
        railway[12] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[15]}));
        railway[13] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[16]}));
        railway[14] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[17]}));
        railway[15] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[18],tracks[19]}));
        railway[16] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[20]}));
        railway[17] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[21]}));


        railway[0].getSection().setCandetect(true);
        railway[2].getSection().setCandetect(true);
        railway[4].getSection().setCandetect(true);
        railway[6].getSection().setCandetect(true);
        railway[8].getSection().setCandetect(true);
        railway[10].getSection().setCandetect(true);
        railway[12].getSection().setCandetect(true);
        railway[14].getSection().setCandetect(true);
        railway[16].getSection().setCandetect(true);

        // Set up Section connections

        // First outside straight section
        railway[0].getSection().setToIndex(1);
        railway[0].getSection().setFromIndex(7);// 7 should be second junction

        // Second outside straight section
        railway[1].getSection().setToIndex(2);
        railway[1].getSection().setFromIndex(0);

        // First outside turn with junction at the start
        railway[2].getSection().setToIndex(3);
        railway[2].getSection().setFromIndex(1);
        railway[2].getSection().setHasJunctionTrack(true);
        railway[2].getSection().setJuncSectionIndex(9); // The inbound junction

        // Second outside turn
        railway[3].getSection().setToIndex(4);
        railway[3].getSection().setFromIndex(2);

        // First top outside straight
        railway[4].getSection().setToIndex(5);
        railway[4].getSection().setFromIndex(3);

        // Second top outside straight
        railway[5].getSection().setToIndex(6);
        railway[5].getSection().setFromIndex(4);

        // Third outside corner
        railway[6].getSection().setToIndex(7);
        railway[6].getSection().setFromIndex(5);

        // Fourth corner with junction
        railway[7].getSection().setToIndex(0);
        railway[7].getSection().setFromIndex(6);
        railway[7].getSection().setHasJunctionTrack(true);
        railway[7].getSection().setJuncSectionIndex(8); // The outbound junction

        // First inside straight
        railway[8].getSection().setToIndex(9);
        railway[8].getSection().setFromIndex(7);


        // Second inside straight
        railway[9].getSection().setToIndex(10);
        railway[9].getSection().setFromIndex(8);
        railway[9].getSection().setJuncSectionIndex(2); // TODO just added

        // First inside corner
        railway[10].getSection().setToIndex(11);
        railway[10].getSection().setFromIndex(9);
        railway[10].getSection().setHasJunctionTrack(true);
        railway[10].getSection().setJuncSectionIndex(17); // The inbound junction

        // Second inside corner
        railway[11].getSection().setToIndex(10);
        railway[11].getSection().setFromIndex(12);

        // First inside top straight
        railway[12].getSection().setToIndex(13);
        railway[12].getSection().setFromIndex(11);

        // Second inside top straight
        railway[13].getSection().setToIndex(14);
        railway[13].getSection().setFromIndex(12);

        // Third inside corner
        railway[14].getSection().setToIndex(15);
        railway[14].getSection().setFromIndex(13);

        //Fourth inside corner with junction
        railway[15].getSection().setToIndex(8);// first inside straight
        railway[15].getSection().setFromIndex(14);
        railway[15].getSection().setHasJunctionTrack(true);
        railway[15].getSection().setJuncSectionIndex(16); // The inbound junction

        // First most inside straight
        railway[16].getSection().setToIndex(17);
        railway[16].getSection().setFromIndex(15);

        // First most inside straight
        railway[17].getSection().setToIndex(15);
        railway[17].getSection().setFromIndex(13);



        // Set up the drawing of the sections
        tracks[1].setStart(tracks[0]);

        // Junction track
        tracks[2].setStart(tracks[1]);

        tracks[3].setStart(((JunctionTrack)tracks[2]).getStraightTrack());
        tracks[4].setStart(tracks[3]);
        tracks[5].setStart(tracks[4]);
        tracks[6].setStart(tracks[5]);
        tracks[7].setStart(tracks[6]);
        tracks[8].setStart(tracks[7]);
        tracks[9].setStart(tracks[8]);

        tracks[10].setStart(((JunctionTrack)tracks[9]).getTrackThrown());

        tracks[11].setStart(tracks[10]);

        // Junction track
        tracks[12].setStart(tracks[11]);

        tracks[13].setStart(((JunctionTrack)tracks[12]).getStraightTrack());
        tracks[14].setStart(tracks[13]);
        tracks[15].setStart(tracks[14]);
        tracks[16].setStart(tracks[15]);
        tracks[17].setStart(tracks[16]);
        tracks[18].setStart(tracks[17]);

        // Junction track
        tracks[19].setStart(tracks[18]);

        tracks[20].setStart(((JunctionTrack)tracks[19]).getTrackThrown());
        tracks[21].setStart(tracks[20]);


        // Set up Junctions 2 , 9 , 12 19
        JunctionTrack jt1 = (JunctionTrack)tracks[2];
        jt1.setInboundFromThrown(11);
        jt1.setTo(3);
        jt1.setFrom(1);


        JunctionTrack jt2 = (JunctionTrack)tracks[9];
        jt2.setOutboundToThrown(10);
        jt2.setFrom(8);
        jt2.setTo(0);


        JunctionTrack jt3 = (JunctionTrack)tracks[12];
        jt3.setInboundFromThrown(21);
        jt3.setTo(13);
        jt3.setFrom(11);

        JunctionTrack jt4 = (JunctionTrack)tracks[19];
        jt4.setOutboundToThrown(20);
        jt4.setTo(10);
        jt4.setFrom(18);

        // Setup normal tracks

        // Start
        tracks[0].setFrom(9);
        tracks[0].setTo(1);

        tracks[1].setFrom(0);
        tracks[1].setTo(2);

        // Outer First corner
        tracks[3].setFrom(2);
        tracks[3].setTo(4);

        // Outer Second corner
        tracks[4].setFrom(3);
        tracks[4].setTo(5);

        // Outer Top Straight part 1
        tracks[5].setFrom(4);
        tracks[5].setTo(6);

        // Outer Top Straight part 2
        tracks[6].setFrom(5);
        tracks[6].setTo(7);

        // Outer third corner
        tracks[7].setFrom(6);
        tracks[7].setTo(8);

        // Outer fourth corner
        tracks[8].setFrom(7);
        tracks[8].setTo(9);


        // First inner straight
        tracks[10].setFrom(19);//TODO check if it could be multiple tracks fourth junction
        tracks[10].setJuncFrom(9);//Second Junction
        tracks[10].setTo(11);

        // Second inner straight
        tracks[11].setFrom(10);
        tracks[11].setTo(12);
        tracks[11].setJuncTo(2);

        // Third junction track inbound so from is to
//        tracks[12].setFrom(13);

        // First inner corner
        tracks[13].setFrom(12);
        tracks[13].setTo(14);

        // Second inner corner
        tracks[14].setFrom(13);
        tracks[14].setTo(15);

        // First inner top straight
        tracks[15].setFrom(14);
        tracks[15].setTo(16);

        // Second inner top straight
        tracks[16].setFrom(15);
        tracks[16].setTo(17);

        // Third inner corner
        tracks[17].setFrom(16);
        tracks[17].setTo(18);

        // Fourth inner corner
        tracks[18].setFrom(17);
        tracks[18].setTo(19);

        // Last junction
//        tracks[19].setFrom(18);

        tracks[20].setFrom(19);
        tracks[20].setTo(21);

        tracks[21].setFrom(20);
        tracks[21].setTo(12);// Goes to third junction

        return railway;
    }

    public DefaultTrack[] junctionTestTracks(){
        int curID = 0;
        return  new DefaultTrack[]{
                new StraightHoriz(300,600,200,0,curID++,"RIGHT"),
                new JunctionTrack(100,6,curID++,false, false,"UP"),

                new StraightHoriz(200,0,curID++),
                new JunctionTrack(100,6,curID++,false, true,"UP"),
                new StraightHoriz(200,0,curID++),

                // Left tracks
                new StraightHoriz(900,200,200,0,curID++,"LEFT"),
                new JunctionTrack(100,6,curID++,false, false,"UP"),

                new StraightHoriz(200,0,curID++),
                new JunctionTrack(100,6,curID++,false, true,"UP"),
                new StraightHoriz(200,0,curID++),
        };
    }

    public DrawableSection[] getJunctionTestSect(DefaultTrack[] tracks){
        DrawableSection[] railway = new DrawableSection[6];
        int curID = 0;

        // Right direction junctions
        railway[0] = new DrawableSection(new Section(99,200,new DefaultTrack[]{tracks[0],tracks[1]}));
        tracks[1].setStart(tracks[0]);

        railway[1] = new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[2]}));
        tracks[2].setStart(tracks[1]);

        railway[2] = new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[3],tracks[4]}));
        tracks[3].setStart(tracks[2]);
        tracks[4].setStart(tracks[3]);

        // Left direction junctions
        railway[3] = new DrawableSection(new Section(101,200,new DefaultTrack[]{tracks[5],tracks[6]}));
        tracks[6].setStart(tracks[5]);

        railway[4] = new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[7]}));
        tracks[7].setStart(tracks[6]);

        railway[5] = new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[8],tracks[9]}));
        tracks[8].setStart(tracks[7]);
        tracks[9].setStart(tracks[8]);

        return railway;
    }


    /**
     * Creates the default tracks
     * */
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
                new JunctionTrack(100,6,curID++,false, false,"UP"),
                new StraightHoriz(200,0,curID++),
                new StraightHoriz(100,0,curID++),

                new JunctionTrack(100,6,curID++,false, true,"UP"),
        };
    }

    /**
     * Creates the default Sections
     * */
    public DrawableSection[] createDefSections(DefaultTrack[] tracks){
        DrawableSection[] railway = new DrawableSection[7];
        int curID = 0;

        railway[0] = new DrawableSection(new Section(99,200,new DefaultTrack[]{tracks[0],tracks[1]}));
        tracks[1].setStart(tracks[0]);

        // First turn
        railway[1] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[11],tracks[2]}));


        tracks[2].setStart(((JunctionTrack)tracks[11]).getStraightTrack());
        railway[1].getSection().setCandetect(true);

        // Second turn
        railway[2] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[3]}));
        tracks[3].setStart(tracks[2]);

        // Top straight bit
        railway[3] =  new DrawableSection(new Section(curID++,200,new DefaultTrack[]{tracks[4],tracks[5],tracks[10]}));
        tracks[4].setStart(tracks[3]);
        tracks[5].setStart(tracks[4]);
        tracks[10].setStart(tracks[5]);

        // Make it be able to detect
        railway[3].getSection().setCandetect(true);

        // Third turn
        railway[4] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[6]}));
        tracks[6].setStart(tracks[10]);

        // Last turn with junction
        railway[5] =  new DrawableSection(new Section(curID++,300,new DefaultTrack[]{tracks[7],tracks[8]}));
        tracks[7].setStart(tracks[6]);
        tracks[8].setStart(tracks[7]);

        railway[6] =  new DrawableSection(new Section(curID++,150,new DefaultTrack[]{tracks[9]}));


        // Set to
        tracks[0].setTo(1);
        tracks[1].setTo(2);
        tracks[2].setTo(3);
        tracks[3].setTo(4);
        tracks[4].setTo(5);

        tracks[5].setTo(10);

        tracks[10].setTo(6);
        tracks[6].setTo(7);
        tracks[7].setTo(8);

        tracks[10].setFrom(5);

        tracks[0].setFrom(8);
        tracks[7].setFrom(6);

        tracks[6].setFrom(5);
        tracks[5].setFrom(4);
        tracks[4].setFrom(3);
        tracks[3].setFrom(2);
        tracks[2].setFrom(1);
        tracks[1].setFrom(0);

        tracks[9].setFrom(8);

        JunctionTrack jt = (JunctionTrack)tracks[8];
//        jt.setToNotThrownTrack(0);
//        jt.setToThrownTrack(9);


        tracks[9].setStart(jt.getTrackThrown());

        tracks[11].setStart(tracks[9]);
        tracks[11].setFrom(9);
        JunctionTrack jtIn = (JunctionTrack)tracks[11];


        return railway;
    }

    /**
     * Returns the tracks
     * */
    public DefaultTrack[] getTracks(){
        return this.tracks;
    }

    /**
     * Returns the sections
     * */
    public DrawableSection[] getSections(){
        return this.sections;
    }


    public static List<DrawableTrain> getDefaultTrains(DrawableSection[] railway){
        List<DrawableTrain> trains = new ArrayList<>();

        // Add a train to the track
        for(DrawableSection ds : railway) {
            if (ds.getSection().getID() == 99) {
                //Create the train
                Train train = new Train(1, 15, 100, true,true,71000);
                DrawableTrain drawableTrain = new DrawableTrain(train, ds,ds.getTracks()[0]);
                 drawableTrain.setUpImage();
                trains.add(drawableTrain);

            }
            if(ds.getSection().getID() == 101){
                Train train1 = new Train(2, 15, 100, true,true,71000);


                DrawableTrain drawableTrain1 = new DrawableTrain(train1, ds,ds.getTracks()[0]);
                trains.add(drawableTrain1);
                drawableTrain1.setUpImage();
            }
        }
        return trains;
    }

    public static List<DrawableTrain> getConnectTestTrains(DrawableSection[] railway, Simulation sim){
        List<DrawableTrain> trains = new ArrayList<>();

        // Add a train to the track
        for(DrawableSection ds : railway) {
            if (ds.getSection().getID() == 0) {
                //Create the train
                Train train = new Train(1, 15, 500, false,true,71000);
                DrawableTrain drawableTrain = new DrawableTrain(train, ds,ds.getTracks()[0]);
                drawableTrain.setUpImage();
                RollingStock rollingStock = new RollingStock(80,5,0.9);
                DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock,drawableTrain,drawableTrain.getTrain().getDirection(),train.getOrientation());
                drawableRollingStock.setStart(drawableTrain.getCurrentLocation(),sim);

//                drawableTrain.setRollingStockConnected(drawableRollingStock);

//                drawableRollingStocks.add(drawableRollingStock);
                trains.add(drawableTrain);

//                movable.add(drawableRollingStock);
            }

        }

        return trains;
    }

    public static List<DrawableRollingStock> getConnectTestRollingStock(DrawableSection[] railway){
        List<DrawableRollingStock> stocks = new ArrayList<>();

        for(DrawableSection ds : railway) {
            if (ds.getSection().getID() == 99) {

                RollingStock rollingStock = new RollingStock(15,828282,50000);
                DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock,null,true, true);
                drawableRollingStock.setUpImage();

                drawableRollingStock.setStartNotConnected(ds.getSection().getTracks()[0]);

                stocks.add(drawableRollingStock);
            }
            if (ds.getSection().getID() == 101) {
                RollingStock rollingStock = new RollingStock(15,28282,50000);
                DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock,null,true, true);
                drawableRollingStock.setUpImage();

                drawableRollingStock.setStartNotConnected(ds.getSection().getTracks()[0]);

                stocks.add(drawableRollingStock);
            }
        }
        return stocks;
    }



    public static List<Movable> createMovableList(List<DrawableTrain> trains, List<DrawableRollingStock> stocks){
        List<Movable> movables = new ArrayList<>();

        trains.forEach(t -> movables.add(t));
        stocks.forEach(s -> movables.add(s));

        return movables;
    }

    public static class Railway {
        public static DrawableSection[] sections;
        public static DefaultTrack[] tracks;

        public Railway(DrawableSection[] sections, DefaultTrack[] tracks){
            this.sections = sections;
            this.tracks = tracks;
        }
    }

    // TRACKS FOR TESTING
    public static Railway  getHorizontalRailWay(){
        int curID = 0;

        DefaultTrack[] tracks = new DefaultTrack[]{
                // s1
                new StraightHoriz(350, 600, 200, 0, 0, "RIGHT"), // 0
                // s2
                new StraightHoriz(200, 0, 1)
        };

        DrawableSection[] sections = new DrawableSection[2];

        sections[0] = new DrawableSection(new Section(0,200,new DefaultTrack[]{tracks[0]}));
        sections[1] = new DrawableSection(new Section(1,200,new DefaultTrack[]{tracks[1]}));
        sections[0].getSection().setCandetect(true);

        sections[0].getSection().setToIndex(1);
        sections[1].getSection().setFromIndex(0);

        tracks[1].setStart(tracks[0]);

        tracks[0].setTo(1);
        tracks[1].setFrom(0);

        return new Railway(sections,tracks);
    }
}
