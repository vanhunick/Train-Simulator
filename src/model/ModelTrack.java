package model;

import controllers.Controller;
import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanhunick on 22/03/16.
 */
public class ModelTrack implements Events, Event.Listener{

    // List of trains on the railway
    private List<Train> trains;

    private Controller controller;

    // The sections that make up the railway
    private Section[] sections;

    private boolean useController;

    private List<Event.Listener> listeners;

    /**
     * Created a ModelTrack object for sending and receiving events
     *
     * @param  trains the trains on the railway
     *
     *@param sections the section that make up the railway
     * */
    public ModelTrack(List<Train> trains, Section[] sections){
        this.trains = trains;
        this.sections = sections;
        this.listeners = new ArrayList<>();
    }

    public void register(Event.Listener l){
        listeners.add(l);
    }


    /**
     * Return the trains on the railway
     *
     * @return the list of trains on the track
     * */
    public List<Train> getTrains() {
        return trains;
    }


    /**
     * Returns the train that matches the id
     *
     * @param id The id to match
     *
     * @return the train that matches the id
     * */
    public Section getSection(int id){
        for(Section s : sections){
            if(s.getID() == id)return s;
        }
        return null;
    }


    @Override
    public void sectionChanged(int id) {
        for(Section s : sections){
            if(s.getID() == id){
                if(s.getTrainOn()){
                    eventFromSim(new Event.SectionChanged(id,false));
                }
                else {
                    eventFromSim(new Event.SectionChanged(id,true));
                }
                //update the section status
                s.setTrainOn(!s.getTrainOn());
            }
        }
    }

    @Override
    public void setSpeed(int trainID, double speed) {
        for(Train t : trains){
            if(t.getId() == trainID){
                t.setTargetSpeedPercentage((float)speed);
            }
        }
    }

    @Override
    public void setDirection(int trainID, boolean direction){
        for(Train t : trains){
            if(t.getId() == trainID){
                t.setDirection(direction);
            }
        }
    }

    @Override
    public void setJunction(int junctionID, boolean toggle) {
        for(Section s : sections){
            for(DefaultTrack dt : s.getTracks()){
                if(dt.getId() == junctionID){
                    JunctionTrack jt = (JunctionTrack)dt;//Should be a junction if not there was an incorrect config at the start
                    jt.setThrown(!jt.getThrown());// Toggle the junction
                }
            }
        }
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    public void useController(boolean use){
        this.useController = use;
    }

    public void addTrain(Train train){
        trains.add(train);
    }

    public void eventFromSim(Event e){
        listeners.forEach(l -> l.notify(e));
    }


    @Override
    public void notify(Event e) {
        // Change speed event
        if(e instanceof Event.SpeedChanged){
            System.out.println("Speed update");
            Event.SpeedChanged s = (Event.SpeedChanged)e;
            setSpeed(s.getLocomotive(), s.getSpeed());
        }

        // Change direction event
        if(e instanceof Event.DirectionChanged){
            Event.DirectionChanged d = (Event.DirectionChanged)e;
            setDirection(d.getLocomotive(),d.getDirection());
        }

        // Change junction event
        if(e instanceof Event.TurnoutChanged){
            Event.TurnoutChanged t = (Event.TurnoutChanged)e;
            setJunction(t.getTurnout(),t.getThrown());
        }
    }
}
