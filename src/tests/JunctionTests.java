package tests;

import Util.CustomTracks;
import model.ModelTrack;
import model.RollingStock;
import model.Train;
//import org.junit.*;
import org.junit.Test;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;
import view.Drawable.section_types.StraightHoriz;
import view.Simulation;
import view.TrackBuilder;

import java.util.List;


/**
 * Created by Nicky on 4/07/2016.
 */
public class JunctionTests {

    public Simulation getSimulation(){
        Simulation s = new Simulation(null);
        return s;
    }


    @Test
    public void exitEventTest(){
        if(1==1)return;
        Simulation simulation = getSimulation();
        CustomTracks.Railway railway = CustomTracks.getHorizontalRailWay();

        simulation.setRailway(railway.sections);
        simulation.setTracks(railway.tracks);


        Train t = new Train(1,100,500,true,true,71000);
        DrawableTrain dt = new DrawableTrain(t,railway.sections[0],railway.tracks[0]);

        simulation.addTraintoSimulation(dt);

        ModelTrack model = new ModelTrack(simulation.getTrains(),simulation.getSections());

        simulation.setModelTrack(model);


        // Set the target speed for the train
        model.setSpeed(1,200);

        simulation.setStart(true);

        boolean success = false;

        System.out.println(dt.getCurSection().getSection().getID());

        for(int i = 0; i < 80; i++){
            simulation.update();
            if(dt.getCurSection().getSection().getID() == 1){
                success = true;
                break;
            }
        }
        assert (success);
    }

    @Test
    public void testEndTrackMethod(){
//        int startX, int startY, int length, int drawID,int id, String direction, boolean thrown, boolean inbound, String drawDirection

        // Inbound
        JunctionTrack j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,true,"UP");
        assert (j.getEndTrack().getDrawID() == 4);

        j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,true,"DOWN");
        assert (j.getEndTrack().getDrawID() == 1);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,true,"UP");
        assert (j.getEndTrack().getDrawID() == 3);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,true,"DOWN");
        assert (j.getEndTrack().getDrawID() == 2);

        // Outbound
        j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,false,"UP");
        assert (j.getEndTrack().getDrawID() == 1);

        j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,false,"DOWN");
        assert (j.getEndTrack().getDrawID() == 4);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,false,"UP");
        assert (j.getEndTrack().getDrawID() == 2);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,false,"DOWN");
        assert (j.getEndTrack().getDrawID() == 3);

    }

    @Test
    public void testInnerTrackMethod(){

        // Inbound
        JunctionTrack j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,true,"UP");
        assert (j.getInnerTrack().getDrawID() == 2);

        j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,true,"DOWN");
        assert (j.getInnerTrack().getDrawID() == 3);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,true,"UP");
        assert (j.getInnerTrack().getDrawID() == 1);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,true,"DOWN");
        assert (j.getInnerTrack().getDrawID() == 4);

        // Outbound
        j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,false,"UP");
        assert (j.getInnerTrack().getDrawID() == 3);

        j = new JunctionTrack(0,0,200,6, 0,"RIGHT", false,false,"DOWN");
        assert (j.getInnerTrack().getDrawID() == 2);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,false,"UP");
        assert (j.getInnerTrack().getDrawID() == 4);

        j = new JunctionTrack(0,0,200,6, 0,"LEFT", false,false,"DOWN");
        assert (j.getInnerTrack().getDrawID() == 1);
    }

    @Test
    public void testConnectionMethodStraightPiece() {

        // Create the track builder object
        TrackBuilder tb = new TrackBuilder();

        StraightHoriz s1 = new StraightHoriz(0,0,100,0,0,"RIGHT");

        StraightHoriz s2 = new StraightHoriz(100,0,100,0,1,"RIGHT");

        List<DefaultTrack> tracks = tb.getAllTracks();
        tracks.add(s1);
        tracks.add(s2);


        System.out.println(s2.getFrom());
        tb.connectDestinations();

        // The track s1 should now be connected to s2
        assert (s1.getTo() == 1);



    }

    @Test
    public void testConnectionMethodJunctionPiece() {

        // Create the track builder object
        TrackBuilder tb = new TrackBuilder();

        StraightHoriz s1 = new StraightHoriz(0,0,47,0,0,"RIGHT");

        JunctionTrack j = new JunctionTrack(100,100,100,6,1,"RIGHT",false,true,"UP");

        List<DefaultTrack> tracks = tb.getAllTracks();
        tracks.add(s1);
        tracks.add(j);

        System.out.println(j.getInnerTrack().getStartX() + j.getInnerTrack().getLength()/2);
        System.out.println(j.getInnerTrack().getConnectionPointFrom().getX());
        System.out.println(j.getInnerTrack().getStartY());

        tb.connectDestinations();

        System.out.println("TO " + s1.getTo());
        // The track s1 should now be connected to s2
        assert (s1.getTo() == 1);



    }
}
