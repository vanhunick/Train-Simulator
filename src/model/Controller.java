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
            trains.add(new ControllerTrain(t.getId(),t.getDirection(),t.getOrientation(),trainStartMap.get(t)));// TODO chuck in equals meth for train
        }
        createControllerSections();
    }


    public void startControlling(){
        updateTrains();
    }

    public void updateTrains(){
        for(ControllerTrain t : trains){
            ControllerSection nextSec = getNextSection(t,getControllerSection(t.curSection));

            boolean locked = false;

            for(ControllerTrain ot : trains){
                if(!t.equals(ot)){
                    if(ot.lockCur == nextSec.id || ot.lockNext == nextSec.id){
                        // the next section is locked
                        locked = true;
                        break;
                    }
                }
            }
            if(locked){
                model.setSpeed(t.id,0); // Make the train stop
            }
            else {
                model.setSpeed(t.id,400); // Make the train GO
                t.lockNext = nextSec.id;// TODO check
            }
        }
    }




    /**
     * Returns the train the will be on the nextsection
     *
     * @param nextSectionID the id of the next section the train should move to
     * */
    public ControllerTrain getTrainForNextSection(int nextSectionID){

        for(ControllerTrain train : trains){
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

        for(ControllerSection cs : contrlSections){
            if(cs.id == sectionID){

                // Section Exit Event
                if(cs.on){
                    // There is no longer a train on the current section
                    cs.on = false;

                    // Find the train that holds the lock
                    ControllerTrain trainWithLock = getTrainThatHoldSectionLock(cs.id);

                    // Release the lock becasue we exit
                    trainWithLock.lockCur = trainWithLock.lockNext;
                    trainWithLock.lockNext = -1;

//                    trainWithLock.lock = contrlSections[cs.section.getToID()].id;// releases the lock


                    // There is a train on the next section so set to true
                    contrlSections[cs.section.getToID()].on = true;

                    // Set the current section of the train to the next section
                    trainWithLock.curSection = getNextSection(trainWithLock, getControllerSection(trainWithLock.curSection)).id;
                }
                // Section Entry Event
                else {
                    // There is now something on the track
                    cs.on = true;

                    // There is now nothing on the track before it so set to false
                    contrlSections[cs.section.getFromID()].on = false;

                    // Get the train that holds the lock to this
                    ControllerTrain trainOnSectionBefore = getTrainThatHoldSectionLock(cs.id);
                    trainOnSectionBefore.lockCur = trainOnSectionBefore.lockNext;
                    trainOnSectionBefore.lockNext = -1;

                    trainOnSectionBefore.curSection = cs.id;
                }
                updateTrains();
            }
        }
    }

    /**
     * Get train that hold the section lock
     * */
    public ControllerTrain getTrainThatHoldSectionLock(int sectionID){
        for(ControllerTrain t : trains){
            if(t.lockCur == sectionID || t.lockNext == sectionID){
                return t;
            }
        }

        // No train holds the lock
        return null;
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

        return null;
    }




    /**
     * Sets up the controller section for the controller and locks the tracks the starting trains are on
     * */
    private void createControllerSections(){

        for(int i = 0; i < sections.length; i++){
            contrlSections[i] = new ControllerSection(sections[i],false);
        }

        // Lock the sections that have trains on them
        for(ControllerSection cs :contrlSections){
            for(ControllerTrain ct : trains){
                if(ct.curSection == cs.section.getID()){

                    // There is a train starting on this section
                    cs.on = true;
                    ct.lockCur = cs.id;
                    // Need to aquire the lock for the section since it should have it
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

        // Check if train is going along nat track direction
        if(forwardWithTrack(train)){
            return contrlSections[currentSection.section.getToID()];
        }
        else {
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
        return null;
    }

    // Classes for trains and section that the controller modifies to keep track of state
    private class ControllerTrain {
        int id;
        boolean direction;
        boolean orientation;
        int curSection;

        int lockCur; // id of the section it is locking
        int lockNext;

        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection){
            this.id = id;
            this.direction = direction;
            this.orientation = orientation;
            this.curSection = startingSection;
            this.lockNext = -1;
            this.lockCur = -1;
        }
    }

    private class ControllerSection {
        Section section;
        int id;
        boolean on;

        public ControllerSection(Section section, boolean on){
            this.id = section.getID();
            this.section = section;
            this.on = on;
        }
    }
}
