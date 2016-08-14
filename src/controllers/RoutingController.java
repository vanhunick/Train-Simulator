package controllers;

import model.ModelTrack;
import model.Section;
import model.Train;

import java.util.List;
import java.util.Map;

/**
 * Created by User on 30/07/2016.
 */
public class RoutingController extends DefaultController implements Controller {

    private ModelTrack model;

    private SectionGraph routes;

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
    public RoutingController(Map<Train, Integer> trainStartMap, Section[] sections, ModelTrack model){
        super(trainStartMap,sections,model);
        this.model = model;
        this.routes = new SectionGraph(sections);
    }

    public void startTrains(){

        for(ControllerTrain t : getTrains()){
            model.setSpeed(t.id, 27);

            if(t.id == 1){
                t.destinationID = 7;
            }
            if(t.id == 2){
                t.destinationID = 2;
            }
        }
    }

    public void startControlling(){
        startTrains();
        updateTrains();
    }


    @Override
    public void receiveSectionEvent(int sectionID) {
        for(ControllerSection cs : getContrlSections()){
            if(cs.id == sectionID){

                // Section Exit Event
                if(cs.on){
                    // There is no longer a train on the current section
                    cs.on = false;

                    // Find the train that holds the lock
                    ControllerTrain trainWithLock = getTrainOnSection(cs.id);
                    // There is a train on the next section so set to true
                    getContrlSections()[cs.section.getToIndex()].on = true;

                    // Set the current section of the train to the next section
                    trainWithLock.curSectionID = getNextSection(trainWithLock, cs).id;// Was something else
                }
                // Section Entry Event
                else {
                    // There is now something on the track
                    cs.on = true;


                    // Check if the section has a junction and therefore can come from different section
                    int juncIndex = cs.section.getJuncSectionIndex(); // The index in the array of the section it could come from

                    if(juncIndex != -1 && trainOnSection(getContrlSections()[juncIndex].id)){
                        int juncId = getContrlSections()[juncIndex].id;// The id of the section from
                        getContrlSections()[juncIndex].on = false; // Set the from track to not have a train on it anymore
                        getTrainOnSection(juncId).curSectionID = cs.id; // Set the current track for the relevant train
                        updateTrains(); // Update the trains
                        return;
                    }

                    // There is now nothing on the track before it so set to false
                    getContrlSections()[cs.section.getFromIndex()].on = false;// TODO problem is from is junction

                    // Get the train that holds the lock to this
                    ControllerTrain trainOnSectionBefore = getTrainOnSection(getContrlSections()[cs.section.getFromIndex()].id);
                    trainOnSectionBefore.curSectionID = cs.id;

                }
                // With the new state update the trains
                updateTrains();
            }
        }
    }

    public ControllerTrain getTrainOnSection(int sectionID){
        for(ControllerTrain t : getTrains()){
            if(t.curSectionID == sectionID)return t;
        }
        return null;
    }

    public int findIndexOfFromJunctionSection(ControllerSection c){
        int index = getSectionIndex(c.section.getID());

        for(Section s : getSections()){
            if(s.getJuncSectionIndex() == index){
                return getSectionIndex(s.getID());
            }
        }
        return -1;
    }



    public void updateTrains(){
        for(ControllerTrain t : getTrains()){
            // Find the route for the train
            //getSection(t.destinationSection)
            List<Integer> route = routes.getRoute(getSection(t.curSectionID),getSection(t.destinationID),forwardWithTrack(t));
            System.out.println("Train " + t.id + " Route " + route);



            if(t.curSectionID ==  getSection(t.destinationID).getID()){
               // model.setSpeed(t.id, 0);// Stop the train for now
            }
            if(route.size() > 2){ // might have to toggle a junction
                if(getSection(route.get(1)).hasJunctionTrack()){// the next track is junction track
                    Section juncSection = getSection(route.get(1));
                    // check if we need to toggle the junction track
                    if(!(juncSection.getToIndexNat() == getSectionIndex(route.get(2)))){
                        if(!juncSection.getJunction().getThrown() && !juncSection.getJunction().inBound() ){
                            model.setJunction(juncSection.getJunction().getId(), true);
                            System.out.println("Toggling Junction");
                        }
                    }else {
                        if(juncSection.getJunction().getThrown() && !juncSection.getJunction().inBound()){
                            System.out.println("Toggling");
                            model.setJunction(juncSection.getJunction().getId(), false);
                        }
                    }
                }
            }
        }
    }

    public Section getSection(int id){
        for(Section s : getSections()){
            if(s.getID() == id)return s;
        }
        return null;
    }

    public int getSectionIndex(int id){
        for(int i = 0; i < getSections().length; i++){
            if(getSections()[i].getID() == id)return i;
        }
        return 0;// ERROR
    }

    public boolean trainOnSection(int sectionID){
        for(ControllerTrain t : getTrains()){
            if(t.curSectionID == sectionID)return true;
        }
        return false;
    }
}
