package controllers;

/**
 * Created by User on 31/07/2016.
 */
import model.Section;

/**
 * Represents a section on the track being controlled
 * */
public class ControllerSection {
    Section section;

    // Id of the section
    int id;

    // If there is currently a train on it or not
    boolean on;
    boolean containsJunction;

    // The index of the section the junction leads to
    int toJunctionIndex;


    ControllerJunction junction;

    /**
     * Creates a controller section
     * */
    public ControllerSection(Section section, boolean on){
        this.id = section.getID();
        this.section = section;
        this.on = on;

        if(section.hasJunctionTrack()){
            containsJunction = true;
            toJunctionIndex = section.getJuncSectionIndex();
            junction = new ControllerJunction(section.getJunction().getId(),section.getJunction().inBound(), section.getJunction().getThrown());
        }
    }
}