package model;

import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

import java.util.List;

/**
 * Created by vanhunick on 22/03/16.
 */
public class ModelTrack implements Events{

    private List<Train> trains;
    private Section[] sections;

    public ModelTrack(List<Train> trains, Section[] sections){
        this.trains = trains;
        this.sections = sections;
    }

    @Override
    public void sectionChanged(int id) {
        for(Section s : sections){
            if(s.getID() == id){
                //update the section status
                s.setTrainOn(!s.getTrainOn());//TODO most likely change to toggle
            }
        }
    }

    @Override
    public void setSpeed(int trainID, double speed) {
        for(Train t : trains){
            if(t.getId() == trainID){
                t.setSpeed(speed);
            }
        }
    }

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

    public List<Train> getTrains() {
        return trains;
    }

    public Section[] getSections() {
        return sections;
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

    /**
     * Returns the train that matches the id
     *
     * @param id The id to match
     *
     * @return the train that matches the id
     * */
    public Train getTrain(int id){
        for(Train t : trains){
            if(t.getId() == id)return t;
        }
        return null;
    }
}
