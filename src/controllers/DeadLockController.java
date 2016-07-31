package controllers;

import model.*;

import java.util.Map;

/**
 * Created by vanhunick on 17/05/16.
 */
public class DeadLockController  extends DefaultController implements Controller {

    /**
     * Sets up the controller copies the information from the sections and the starting location of trains
     * into the controller objects
     *
     * @param  trainStartMap the trains and the starting locations
     *
     * @param sections the sections in the track
     *
     * @param model the model to send the events to and receive events from
     * */
    public DeadLockController(Map<Train, Integer> trainStartMap, Section[] sections, ModelTrack model){
        super(trainStartMap,sections,model);

        lockControllerSections();
    }


    /**
     * Called from the model to start the controller calling the trains
     * */
    public void startControlling(){
        updateTrains();
    }


    /**
     * Checks if any of the trains that are stopped can no go and acquire the locks of the
     * section they more into
     * */
    public void updateTrains(){
        for(ControllerTrain t : getTrains()){
            ControllerSection nextSec = getNextSection(t,getControllerSection(t.curSection));

            boolean locked = false;

            for(ControllerTrain ot : getTrains()){
                if(!t.equals(ot)){
                    if(ot.lockCur == nextSec.id || ot.lockNext == nextSec.id){
                        // the next section is locked
                        locked = true;
                        break;
                    }
                }
            }
            if(locked){
                getModel().setSpeed(t.id,0); // Make the train stop
            }
            else {
                getModel().setSpeed(t.id,400); // Make the train Go
                t.lockNext = nextSec.id;
            }
        }
    }

    /**
     * Returns the train the will be on the nextsection
     *
     * @param nextSectionID the id of the next section the train should move to
     * */
    public ControllerTrain getTrainForNextSection(int nextSectionID){
        for(ControllerTrain train : getTrains()){
            if(getNextSection(train,getControllerSection(train.curSection)).id == nextSectionID){
                return train;
            }

        }
        return  null;
    }


    /**
     * Called when a section on the track has changed its state
     *
     * @param sectionID the id of the section that changed state
     * */
    @Override
    public void receiveSectionEvent(int sectionID){
        // Go through all the controller sections to see which one it applies to
        for(ControllerSection cs : getContrlSections()){
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

                    // There is a train on the next section so set to true
                    getContrlSections()[cs.section.getToIndex()].on = true;

                    // Set the current section of the train to the next section
                    trainWithLock.curSection = getNextSection(trainWithLock, getControllerSection(trainWithLock.curSection)).id;
                }
                // Section Entry Event
                else {
                    // There is now something on the track
                    cs.on = true;

                    // There is now nothing on the track before it so set to false
                    getContrlSections()[cs.section.getFromIndex()].on = false;

                    // Get the train that holds the lock to this
                    ControllerTrain trainOnSectionBefore = getTrainThatHoldSectionLock(cs.id);
                    trainOnSectionBefore.lockCur = trainOnSectionBefore.lockNext;
                    trainOnSectionBefore.lockNext = -1;

                    trainOnSectionBefore.curSection = cs.id;
                }
                // With the new state update the trains
                updateTrains();
            }
        }
    }


    /**
     * Get train that hold the section lock
     * */
    public ControllerTrain getTrainThatHoldSectionLock(int sectionID){
        for(ControllerTrain t : getTrains()){
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
        for(ControllerTrain train : getTrains()){
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
    private void lockControllerSections(){
        // Lock the sections that have trains on them
        for(ControllerSection cs :getContrlSections()){
            if(cs.on){
                for(ControllerTrain ct : getTrains()){
                    if(ct.curSection == cs.section.getID()){
                        ct.lockCur = cs.id;
                    }
                }
            }
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
     * Returns the DeadLockController section using the id of the section it represents
     *
     * @param id the id of the section
     * */
    private ControllerSection getControllerSection(int id){
        for(ControllerSection cs : getContrlSections()){
            if(cs.id == id)return cs;
        }
        // Error
        return null;
    }
}
