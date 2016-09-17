package controllers;

/**
 * Created by User on 31/07/2016.
 */

/**
 * Represents a section on the track being controlled
 * */
public class ControllerSection {

    final int id; // Id of the section

    boolean on; // If there is currently a train on it or not
    boolean containsJunction; // If the section contains a junction or not

    int junctionIndex; // The index of the section the junction

    final int fromIndex; // From index of section that comes before it
    final int toIndex; // To index of the section that comes after it

    final double length; // Length of the section

    ControllerJunction junction; // Junction inside the section if it has one

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