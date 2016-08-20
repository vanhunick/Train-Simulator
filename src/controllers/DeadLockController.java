package controllers;

import model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by vanhunick on 17/05/16.
 */
public class DeadLockController  extends DefaultController implements Controller {

    /**
     * Sets up the controller copies the information from the sections and the starting location of trains
     * into the controller objects
     *
     * @param sections the sections in the track
     * */
    public DeadLockController(ControllerSection[] sections, List<ControllerTrain> trains){
        super(sections,trains);

        lockControllerSections();
    }


    /**
     * Called from the model to start the controller calling the trains
     * */
    public void startControlling(){
        setTrainSpeed();
        updateTrains();
    }


    public void setTrainSpeed(){

        for(ControllerTrain t : getTrains()){
            if(t.id == 1){
                send(new Event.SpeedChanged(t.id,40));
            } else {
                send(new Event.SpeedChanged(t.id,20));
            }
        }
    }



    /**
     * Checks if any of the trains that are stopped can no go and acquire the locks of the
     * section they more into
     * */
    public void updateTrains(){
        for(ControllerTrain t : getTrains()){
            ControllerSection nextSec = getNextSection(t,getControllerSection(t.curSectionID));

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
                send(new Event.SpeedChanged(t.id,0));
            }
            else {
                if(t.id == 1){
                    send(new Event.SpeedChanged(t.id,40));
                } else {
                    send(new Event.SpeedChanged(t.id,5));
                }
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
            if(getNextSection(train,getControllerSection(train.curSectionID)).id == nextSectionID){
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
                    getContrlSections()[cs.toIndex].on = true;

                    // Set the current section of the train to the next section
                    trainWithLock.curSectionID = getNextSection(trainWithLock, getControllerSection(trainWithLock.curSectionID)).id;
                }
                // Section Entry Event
                else {
                    // There is now something on the track
                    cs.on = true;

                    // There is now nothing on the track before it so set to false
                    getContrlSections()[cs.fromIndex].on = false;

                    // Get the train that holds the lock to this
                    ControllerTrain trainOnSectionBefore = getTrainThatHoldSectionLock(cs.id);
                    trainOnSectionBefore.lockCur = trainOnSectionBefore.lockNext;
                    trainOnSectionBefore.lockNext = -1;

                    trainOnSectionBefore.curSectionID = cs.id;
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
            if(train.curSectionID == trackId){
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
                    if(ct.curSectionID == cs.id){
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

    @Override
    public void notify(Event e){
        if(e instanceof Event.SectionChanged){
            this.receiveSectionEvent(((Event.SectionChanged) e).getSection());
        }
    }
}
