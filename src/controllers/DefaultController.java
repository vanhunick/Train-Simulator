package controllers;

import model.ModelTrack;
import model.Section;
import model.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 31/07/2016.
 */
public abstract class DefaultController implements Controller {

    // List of trains to control
    private List<ControllerTrain> trains;

    // List of sections that generate events
    private Section[] sections;

    // Used because the controller should not be able to access information inside the track sections
    private ControllerSection[] contrlSections;

    // The model that receives and sends events
    private ModelTrack model;

    // Used to route the train
    private SectionGraph sectionGraph;


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
    public DefaultController(Map<Train, Integer> trainStartMap, Section[] sections, ModelTrack model){
        this.model = model;
        this.sections = sections;
        this.contrlSections = new ControllerSection[sections.length];
        trains = new ArrayList<>();

        // Add all the trains to the list of trains
        for(Train t : trainStartMap.keySet()){
            trains.add(new ControllerTrain(t.getId(),t.getDirection(),t.getOrientation(),trainStartMap.get(t),t.getDestinationID()));// TODO chuck in equals meth for train
        }
        createControllerSections();
    }

    public abstract void startControlling();

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
            return getContrlSections()[currentSection.section.getToIndex()];
        }
        else {
            return getContrlSections()[currentSection.section.getFromIndex()];
        }
    }

    /**
     * Returns if the train is going along with the natural orientation of the track
     *
     * @param t train to check
     * */
    public boolean forwardWithTrack(ControllerTrain t){
        return t.orientation && t.direction || !t.orientation && !t.direction;
    }

    public List<ControllerTrain> getTrains() {
        return trains;
    }

    public void setTrains(List<ControllerTrain> trains) {
        this.trains = trains;
    }

    public Section[] getSections() {
        return sections;
    }

    public void setSections(Section[] sections) {
        this.sections = sections;
    }

    public ControllerSection[] getContrlSections() {
        return contrlSections;
    }

    public void setContrlSections(ControllerSection[] contrlSections) {
        this.contrlSections = contrlSections;
    }

    public ModelTrack getModel() {
        return model;
    }

    public void setModel(ModelTrack model) {
        this.model = model;
    }

    public SectionGraph getSectionGraph() {
        return sectionGraph;
    }

    public void setSectionGraph(SectionGraph sectionGraph) {
        this.sectionGraph = sectionGraph;
    }
}
