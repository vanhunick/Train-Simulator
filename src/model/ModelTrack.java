package model;

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

    public String updateTrainOnSection(Train t, Section newSection, Section prevSection){

        return ("Train ID:" + t.getId() + " Changed from ID:" + prevSection.getID() + " To section ID:" + newSection.getID() + "\n \n");
    }


    @Override
    public void setSpeed(int trainID, double speed) {
        for(Train t : trains){
            if(t.getId() == trainID){
                t.setSpeed(speed);
            }
        }
    }

    @Override
    public void setJunction(int junctionID, boolean toggle) {
        //TODO implement junctions
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
