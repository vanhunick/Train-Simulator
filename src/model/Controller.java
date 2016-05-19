package model;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vanhunick on 17/05/16.
 */
public class Controller {

    // List of trains to control
    private List<ControllerTrain> trains;

    // List of sections that generate events
    private Section[] sections;

    // Used because the controller should not be able to access information inside the track sections
    private ControllerSection[] contrlSections;

    private ModelTrack model;


    public Controller(Map<Train, Integer> trainStartMap, Section[] sections, ModelTrack model){
        this.model = model;
        this.sections = sections;
        this.contrlSections = new ControllerSection[sections.length];
        trains = new ArrayList<>();

        // Add all the trains to the list of trains
        for(Train t : trainStartMap.keySet()){
            System.out.println(t);

            System.out.println(trainStartMap.get(t));
            trains.add(new ControllerTrain(t.getId(),t.getDirection(),t.getOrientation(),trainStartMap.get(t)));// TODO chuck in equals meth for train
        }

        createControllerSections();

        for(ControllerSection c : contrlSections){
            System.out.println(c.id);
        }
    }


    public void startControlling(){
        acquireLock();
    }

    /**
     * Returns the train the will be on the nextsection
     *
     * @param nextSectionID the id of the next section the train should move to
     * */
    public ControllerTrain getTrainForNextSection(int nextSectionID){
        System.out.println("Section id " + nextSectionID);
        for(ControllerTrain train : trains){
            System.out.println("Train next id " + getNextSection(train,getControllerSection(train.curSection)).id);
            if(getNextSection(train,getControllerSection(train.curSection)).id == nextSectionID){
                return train;
            }

        }

        System.out.println("Returning null getTrainForNextSection");
        return  null;
    }

    int count = 0;
    /**
     * Called when a section on the track has changed its state
     *
     * @param sectionID the id of the section that changed state
     * */
    public void receiveSectionEvent(int sectionID){
        System.out.println("Section ID Event " + sectionID + " Count " + count );
        count++;

        for(ControllerSection cs : contrlSections){
            if(cs.id == sectionID){
                // The section already had a train on it so now it has exited the track
                if(cs.on){
                    System.out.println("Exit event");
                    cs.releaseLock();// Release the lock as the train has now left the track
                    updateTrain(findTrainOnTrack(sectionID));

                }
                else {
                    System.out.println("Enter Event");
                    // A train has entered
                    // The only one that can enter if the one with the lock
                    contrlSections[cs.section.getFromID()].releaseLock();

//                    getControllerSection(getTrainForNextSection(sectionID).curSection).releaseLock();// Release the lock of the section is came from
                    updateTrain(getTrainForNextSection(sectionID));
                }
            }
        }
    }


    /**
     * Called when a section changed has occoured and the train has changed sections
     *
     * @param train the train that changed section
     * */
    public void updateTrain(ControllerTrain train){
        train.curSection = getNextSection(train, getControllerSection(train.curSection)).id;
        acquireLock(train);// Try move to the next section
    }


    /**
     * Called when a train moves from a section to another
     *
     * @param train the train to acquire a lock for
     * */
    public void acquireLock(ControllerTrain train){

            // No need to check direction as that is done with the
            ControllerSection nextSection = getNextSection(train, getControllerSection(train.curSection));
            if(nextSection.acquireLock()){
                model.setSpeed(train.id,400); // Make the train go
            }
            else {
                model.setSpeed(train.id,0); // Make the train stop
            }
    }


    /**
     * Finds the train on the track using the id of the track
     *
     * @param trackId the id of the track
     * */
    public ControllerTrain findTrainOnTrack(int trackId){
        for(ControllerTrain train : trains){
            if(train.curSection == trackId){
                return train;
            }
        }
        // No train on that track
        System.out.println("Null find train on track");
        return null;
    }


    /**
     * Acquire the locks when the trains are first started after that trains should be checked when events occur
     * */
    public void acquireLock(){
        for(ControllerTrain train : trains){
                // No need to check direction as that is done with the
                ControllerSection nextSection = getNextSection(train, getControllerSection(train.curSection));
                if(nextSection.acquireLock()){
                    model.setSpeed(train.id,400); // Make the train go
                }
                else {
                    model.setSpeed(train.id,0); // Make the train stop
                }
        }
    }


    /**
     * Sets up the controller section for the controller and locks the tracks the starting trains are on
     * */
    private void createControllerSections(){
//        for(Section s : sections){
//            contrlSections.add(new ControllerSection(s,false,false));
//        }

        for(int i = 0; i < sections.length; i++){
            contrlSections[i] = new ControllerSection(sections[i],false,false);
        }

        // Lock the sections that have trains on them
        for(ControllerSection cs :contrlSections){
            for(ControllerTrain ct : trains){
                if(ct.curSection == cs.section.getID()){
                    cs.on = true;
                    cs.acquireLock() ;// Lock the section the train starts on TODO check for errors if
                }
            }
        }
    }


    /**
     * Returns the next section for the train. Based on the direction it is heading in and the next section
     * along the track
     *
     * @param train the train to check
     *
     * @param currentSection the current section the train is on
     * */
    public ControllerSection getNextSection(ControllerTrain train, ControllerSection currentSection){
        //if(currentSection.section.hasJunctionTrack())return handleSpecialCaseJunction(currentSection);

        System.out.println("Train " + train + " Sec " + currentSection);


        int id;
        if(forwardWithTrack(train)){
    //        System.out.println(currentSection);
//            System.out.println(currentSection.section);
  //          id = currentSection.section.getTo().getID();

            return contrlSections[currentSection.section.getToID()];
        }
        else {
      //      id = currentSection.section.getFrom().getID();
            return contrlSections[currentSection.section.getFromID()];
        }
    }

    /**
     * Used when a section contains a junction section, important because it means the section
     * can lead to multiple sections. Need to check the state of the junction to determine which one
     *
     * @param currentSection the current section the train is on
     * */
    public ControllerSection handleSpecialCaseJunction(ControllerSection currentSection){
        return null;
    }


    /**
     * Returns if the train is going along with the natural orientation of the track
     *
     * @param t train to check
     * */
    public boolean forwardWithTrack(ControllerTrain t){
        return t.orientation && t.direction || !t.orientation && !t.direction;
    }


    /**
     * Returns the Controller section using the id of the section it represents
     *
     * @param id the id of the section
     * */
    private ControllerSection getControllerSection(int id){
        for(ControllerSection cs : contrlSections){
            if(cs.id == id)return cs;
        }
        // Error
        System.out.println("Null in controller section");
        return null;
    }

    // Classes for trains and section that the controller modifies to keep track of state
    private class ControllerTrain {
        int id;
        boolean direction;
        boolean orientation;
        int curSection;

        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection){
            this.id = id;
            this.direction = direction;
            this.orientation = orientation;
            this.curSection = startingSection;
        }
    }

    private class ControllerSection {
        Section section;
        int id;
        boolean on;
        private boolean locked;

        public ControllerSection(Section section, boolean on, boolean locked){
            this.id = section.getID();
            this.section = section;
            this.on = on;
            this.locked = locked;
        }

        public boolean acquireLock(){
            if(locked)return false;
            else {
                locked = true;
                return true;
            }
        }

        public void releaseLock(){
            this.locked = false;
        }
    }
}
