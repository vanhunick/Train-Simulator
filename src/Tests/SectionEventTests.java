package Tests;


import Util.CustomTracks;
import model.Controller;
import model.ModelTrack;
import model.Train;
import org.junit.*;
import view.Drawable.DrawableTrain;
import view.Simulation;

/**
 * Created by vanhunick on 25/05/16.
 */
public class SectionEventTests {

    public Simulation getSimulation(){
        Simulation s = new Simulation(null);
        s.setTestMode(true);
        return s;
    }

    @Test
    public void exitEventTest(){
        Simulation simulation = getSimulation();
        CustomTracks.Railway railway = CustomTracks.getHorizontalRailWay();

        simulation.setRailway(railway.sections);
        simulation.setTracks(railway.tracks);


        Train t = new Train(1,100,500,true,true,0.9,0.9);
        DrawableTrain dt = new DrawableTrain(t,railway.sections[0],railway.tracks[0]);

        simulation.addTraintoSimulation(dt);

        ModelTrack model = new ModelTrack(simulation.getTrains(),simulation.getSections());

        simulation.setModelTrack(model);


        // Set the target speed for the train
        model.setSpeed(1,200);



        Controller c = new Controller() {

            @Override
            public void receiveSectionEvent(int sectionID) {
                assert (true);
            }
        };

        model.setController(c);

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


}
