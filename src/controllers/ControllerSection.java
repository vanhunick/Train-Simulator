package controllers;

/**
 * Created by User on 31/07/2016.
 */
import model.Section;

/**
 * Represents a section on the track being controlled
 * */
public class ControllerSection {
    // Id of the section
    final int id;

    // If there is currently a train on it or not
    boolean on;
    boolean containsJunction;

    // The index of the section the junction
    int junctionIndex;

    // From and to index of the section
    final int fromIndex;
    final int toIndex;

    // Length of the section
    final double length;


    ControllerJunction junction;

    public ControllerSection(int id, int fromIndex, int toIndex, int junctionIndex, double length){
        this.id = id;
        this.fromIndex = fromIndex;
        this.toIndex = toIndex;
        this.junctionIndex = junctionIndex;
        this.length = length;
    }

    public void addJunction(int junctionID, boolean inBound, boolean thrown){
        junction = new ControllerJunction(junctionID, inBound, thrown);
        containsJunction = true;
    }

    public String toString(){
        return "Id " + id + " from " + fromIndex + " to " + toIndex + " has Junc " + containsJunction + " on " + on;
    }
}