package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vanhunick on 17/05/16.
 */
public class Controller {

    // List of trains to control
    List<ControllerTrain> trains;

    // List of sections that generate events
    List<Section> sections;

    // Used because the controller should not be able to access information inside the track sections
    List<ControllerSection> contrlSections;

    public Controller(Map<Train, Integer> trainStartMap, List<Section> sections){
        this.sections = sections;
        this.contrlSections = new ArrayList<>();

        // Add all the trains to the list of trains
        for(Train t : trainStartMap.keySet()){
            trains.add(new ControllerTrain(t.getId(),t.getDirection(),t.getOrientation(),trainStartMap.get(t)));// TODO chuck in equals meth for train
        }

        createControllerSections(trainStartMap);


    }


    public void recieveSectionEvent(int sectionID){

    }


    private void createControllerSections(Map<Train,Integer> trainStartMap){
        for(Section s : sections){
            contrlSections.add(new ControllerSection(s.getID(),false,false));
        }

    }



    private class ControllerSection {
        int id;
        boolean on;
        boolean locked;

        public ControllerSection(int id, boolean on, boolean locked){
            this.on = on;
            this.id = id;
            this.locked = locked;
        }
    }

    private class ControllerTrain {
        int id;
        boolean direction;
        boolean orientation;
        int curSection;

        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection){
            this.direction = direction;
            this.orientation = orientation;
            this.curSection = startingSection;
        }
    }
}
