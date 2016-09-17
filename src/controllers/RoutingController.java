package controllers;

import simulation.model.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 30/07/2016.
 */
public class RoutingController extends DefaultController implements Controller {

    private SectionGraph routes;

    public RoutingController(ControllerSection[] sections, List<ControllerTrain> trains){
        super(sections,trains);
        this.routes = new SectionGraph(getSections());
    }

    public RoutingController(String configFilePath){
        super(configFilePath);
        this.routes = new SectionGraph(getSections());
    }


    public void startTrains(){

        for(ControllerTrain t : getTrains()){
            send(new Event.SpeedChanged(t.id,27)); //TODO change to percentage

            if(t.id == 1){
                t.destinationID = 6;
                t.destinationIDs = new ArrayList<>();
                t.destinationIDs.add(6);
                t.destinationIDs.add(2);

                t.curDest = 2;
            }
            if(t.id == 2){
                t.destinationID = 2;
                t.destinationIDs = new ArrayList<>();
                t.destinationIDs.add(2);
                t.curDest = 2;

            }
        }
    }

    public void startControlling(){
        startTrains();
        updateTrains();
    }

    @Override
    public void notify(Event e){
        if(e instanceof Event.SectionChanged){
            this.receiveSectionEvent(((Event.SectionChanged) e).getSection());
        }
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
                    getContrlSections()[cs.toIndex].on = true;

                    // Set the current section of the train to the next section
                    trainWithLock.curSectionID = getNextSection(trainWithLock, cs).id;// Was something else
                }
                // Section Entry Event
                else {
                    // There is now something on the track
                    cs.on = true;


                    // Check if the section has a junction and therefore can come from different section
                    int juncIndex = cs.junctionIndex; // The index in the array of the section it could come from

                    if(juncIndex != -1 && trainOnSection(getContrlSections()[juncIndex].id)){
                        int juncId = getContrlSections()[juncIndex].id;// The id of the section from
                        getContrlSections()[juncIndex].on = false; // Set the from track to not have a train on it anymore
                        getTrainOnSection(juncId).curSectionID = cs.id; // Set the current track for the relevant train
                        updateTrains(); // Update the trains
                        return;
                    }

                    // There is now nothing on the track before it so set to false
                    getContrlSections()[cs.fromIndex].on = false;// TODO problem is from is junction

                    // Get the train that holds the lock to this
                    ControllerTrain trainOnSectionBefore = getTrainOnSection(getContrlSections()[cs.fromIndex].id);
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
        int index = getSectionIndex(c.id);

        for(ControllerSection s : getSections()){
            if(s.junctionIndex == index){
                return getSectionIndex(s.id);
            }
        }
        return -1;
    }



    public void updateTrains(){
        for(ControllerTrain t : getTrains()){

            List<Integer> route = routes.getRoute(getSection(t.curSectionID),getSection(t.curDest),forwardWithTrack(t));
            System.out.println("Train " + t.id + " Route " + route);

            if(t.curSectionID ==  getSection(t.curDest).id){
                System.out.println("Reached destination " + t.curDest);

                for(int i = 0 ; i < t.destinationIDs.size(); i++){
                    if(t.destinationIDs.get(i) == t.curDest){
                        if(i == t.destinationIDs.size()-1){
                            t.curDest = t.destinationIDs.get(0);
                        } else {
                            t.curDest = t.destinationIDs.get(i+1);
                        }
                        break;
                    }
                }
               // simulation.model.setSpeed(t.id, 0);// Stop the train for now
            }
            if(route.size() > 2){ // might have to toggle a junction
                if(getSection(route.get(1)).containsJunction){// the next track is junction track
                    ControllerSection juncSection = getSection(route.get(1));
                    // check if we need to toggle the junction track
                    if(!(juncSection.toIndex == getSectionIndex(route.get(2)))){
                        if(!juncSection.junction.thrown && !juncSection.junction.inbound ){
                            send(new Event.TurnoutChanged(juncSection.junction.id,true));
                            juncSection.junction.thrown = !juncSection.junction.thrown;
                            System.out.println("Toggling Junction");
                        }
                    }else {
                        if(juncSection.junction.thrown && !juncSection.junction.inbound){
                            System.out.println("Toggling");
                            send(new Event.TurnoutChanged(juncSection.junction.id,false));
                            juncSection.junction.thrown = !juncSection.junction.thrown;
//                            simulation.model.setJunction(juncSection.getJunction().getId(), false);
                        }
                        if(juncSection.junction.thrown && juncSection.junction.inbound){
                            System.out.println("Toggling");
                            juncSection.junction.thrown = !juncSection.junction.thrown;
//                            simulation.model.setJunction(juncSection.getJunction().getId(), false);
                            send(new Event.TurnoutChanged(juncSection.junction.id,true));
                        }

                    }
                }

                // check if current section can go down a inbound junction
                int juncIndexInbound = getSection(t.curSectionID).junctionIndex;

                if(juncIndexInbound != -1){
                    System.out.println("Here " + juncIndexInbound);
                    if(getSection(route.get(2)).id == juncIndexInbound){ // Means we need to go down it
                        System.out.println("Going down");
                        ControllerSection juncSection = getSection(route.get(1));
                        if(!(juncSection.junction.thrown)){
                            System.out.println("Toggling");
                            juncSection.junction.thrown = !juncSection.junction.thrown;
                            send(new Event.TurnoutChanged(juncSection.junction.id,false));
                        }
                    }
                }

            }
        }
    }

    public ControllerSection getSection(int id){
        for(ControllerSection s : getSections()){
            if(s.id == id)return s;
        }
        return null;
    }

    public int getSectionIndex(int id){
        for(int i = 0; i < getContrlSections().length; i++){
            if(getSections()[i].id == id)return i;
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
