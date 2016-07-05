package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by vanhunick on 17/05/16.
 */
public class DeadLockController implements Controller {

    // List of trains to control
    private List<ControllerTrain> trains;

    // List of sections that generate events
    private Section[] sections;

    // Used because the controller should not be able to access information inside the track sections
    private ControllerSection[] contrlSections;

    // The model that receives and sends events
    private ModelTrack model;


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
                model.setSpeed(t.id,400); // Make the train Go
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
        for(ControllerTrain train : trains){
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
                // With the new state update the trains
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
     * Returns the DeadLockController section using the id of the section it represents
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


    /**
     * Represents a train being controlled by the controller
     * */
    private class ControllerTrain {
        // The id of the train
        int id;

        // The direction and orientation of the train
        boolean direction;
        boolean orientation;

        // The id of the train it is currently on
        int curSection;

        // Lock of the current section it is on
        int lockCur;

        // Lock of the next section it is trying to get to
        int lockNext;

        /**
         * Creates a controller train
         *
         * @param id the id of the train
         *
         * @param direction the direction the train is going
         *
         * @param orientation if the train is going along the natural orientation or against
         * */
        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection){
            this.id = id;
            this.direction = direction;
            this.orientation = orientation;
            this.curSection = startingSection;
            this.lockNext = -1;
            this.lockCur = -1;
        }
    }


    /**
     * Represents a section on the track being controlled
     * */
    private class ControllerSection {
        Section section;

        // Id of the seciont
        int id;

        // If there is currently a train on it or not
        boolean on;
        Junction junction;
        boolean containsJunction;


        /**
         * Creates a controller section
         * */
        public ControllerSection(Section section, boolean on){
            this.id = section.getID();
            this.section = section;
            this.on = on;

            if(section.hasJunctionTrack()){
                containsJunction = true;
                junction = createJunction(section);
            }
        }

        public Junction createJunction(Section s){
            return new Junction(s.getJunction().getThrown(), s.getFromID(), s.getToID(),s.getJunction().getId(),  s.getJuncSectionIndex(),s.getJunction().inBound());
        }

        public ControllerSection getNextSection(ControllerTrain train){
            if(!containsJunction){
                if(forwardWithTrack(train)){

                }
            }


            return null;
        }
    }

    //
    private class Junction {
        final int junctionID;

        // Section index of the section that came before the section containing this junction
        final int fromID;


        final int toThrownID;

        // Section index of the section that comes after the section containing this junction
        final int toID;

        // If the junction is thrown or not
        boolean thrown;

        // If the junction is inbound or not
        boolean inBound;

        public Junction(boolean thrown, int fromID, int toID, int toThrownID, int junctionID, boolean inbound){
            this.fromID = fromID;
            this.toThrownID = toThrownID;
            this.thrown = thrown;
            this.toID = toID;
            this.junctionID = junctionID;
        }

    }
}
