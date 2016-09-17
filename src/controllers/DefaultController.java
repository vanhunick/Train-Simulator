package controllers;

import simulation.model.Event;
import simulation.model.ModelTrack;
import util.save.LoadControlerSections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 31/07/2016.
 */
public abstract class DefaultController implements Controller {

    // List of trains to control
    private List<ControllerTrain> trains;

    // Used because the controller should not be able to access information inside the track sections
    private ControllerSection[] contrlSections;

    // The simulation.model that receives and sends events
    private ModelTrack model;

    // Used to route the train
    private SectionGraph sectionGraph;

    boolean ignoreEvents;

    // Listeners to update when event occours
    private List<Event.Listener> listeners;

    public DefaultController(ControllerSection[] sections, List<ControllerTrain> trains){
        this.listeners = new ArrayList<>();
        this.contrlSections = sections;
        this.trains = trains;

        createControllerSections();
    }

    public DefaultController(String filePath){
        this.listeners = new ArrayList<>();

        LoadControlerSections.LoadedControlRailway  loadedRailway = new LoadControlerSections().loadedControlRailway(filePath);

        this.contrlSections = loadedRailway.sections;
        this.trains = loadedRailway.trains;

        createControllerSections();
    }

    public void register(Event.Listener l){
        listeners.add(l);
    }

    public void send(Event e){
        listeners.forEach(l -> l.notify(e));
    }

    public abstract void startControlling();

    /**
     * Sets up the controller section for the controller and locks the util.tracks the starting trains are on
     * */
    private void createControllerSections(){

        // Lock the sections that have trains on them
        for(ControllerSection cs :contrlSections){
            for(ControllerTrain ct : trains){
                if(ct.curSectionID == cs.id){
                    // There is a train starting on this section
                    cs.on = true;
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
            return getContrlSections()[currentSection.toIndex-1];
        }
        else {
            return getContrlSections()[currentSection.fromIndex-1];
        }
    }

    /**
     * Returns if the train is going along with the natural orientation of the track
     *
     * @param t train to check
     * */
    public boolean forwardWithTrack(ControllerTrain t){return t.orientation && t.direction || !t.orientation && !t.direction;}

    public List<ControllerTrain> getTrains() {
        return trains;
    }

    public void setTrains(List<ControllerTrain> trains) {
        this.trains = trains;
    }

    public ControllerSection[] getSections() {
        return contrlSections;
    }

    public void setSections(ControllerSection[] sections) {
        this.contrlSections = sections;
    }

    public ControllerSection[] getContrlSections() {
        return contrlSections;
    }

    public ModelTrack getModel() {
        return model;
    }

    public void setModel(ModelTrack model) {
        this.model = model;
    }

}
