package model;

import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

import java.util.List;

/**
 * Created by vanhunick on 22/03/16.
 */
public class ModelTrack implements Events{

    // List of trains on the railway
    private List<Train> trains;

    // The sections that make up the railway
    private Section[] sections;

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
                System.out.println("Section changing");
                //update the section status
                s.setTrainOn(!s.getTrainOn());
            }
        }
    }

    @Override
    public void setSpeed(int trainID, double speed) {
        for(Train t : trains){
            if(t.getId() == trainID){
                t.setMaxSpeed(speed);
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
}
