package tests;


import Util.CustomTracks;
import model.ModelTrack;
import model.RollingStock;
import model.Train;
import org.junit.*;
import view.Drawable.DrawableRollingStock;
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
    public void connectToRollingStockTest(){
        Simulation simulation = getSimulation();
        CustomTracks.Railway railway = CustomTracks.getHorizontalRailWay();

        simulation.setRailway(railway.sections);
        simulation.setTracks(railway.tracks);

        // Create a train
        Train t = new Train(1,100,500,false,true,0.9,0.9);
        DrawableTrain dt = new DrawableTrain(t,railway.sections[1],railway.tracks[1]);

        RollingStock rollingStock = new RollingStock(15,2,0.9);
        DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock,null,true);

        drawableRollingStock.setStartNotConnected(railway.tracks[0]);

        simulation.addTraintoSimulation(dt);
        simulation.addRollingStocktoSimulation(drawableRollingStock);

        ModelTrack model = new ModelTrack(simulation.getTrains(),simulation.getSections());

        // Set the target speed for the train
        model.setSpeed(1,100);

        simulation.setStart(true);

        boolean success = false;
        for(int i = 0; i < 80; i++){
            simulation.update();
            if(dt.getRollingStockConnected() != null){
                success = true;
                break;
            }
        }
        assert (success);
    }





}
