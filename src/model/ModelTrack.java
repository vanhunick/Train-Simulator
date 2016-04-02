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
        for(Section t : sections){
            if(t.containsSection(id)){
                //update the section status
                t.getSection(id).toggleStatus();
            }
        }
    }



    public void sendOutSectionEvent(){

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
}
