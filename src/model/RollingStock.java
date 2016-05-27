package model;

import view.Simulation;

/**
 * Created by vanhunick on 21/04/16.
 */
public class RollingStock {

    // Length of rolling stock
    private double length;

    // The id of the rolling stock
    private int rollID;

    private double deceleration;

    /**
     * Creates a new rolling stock object
     *
     * @param length the length of the rolling stock
     *
     * @param rollID the id of the rolling stock
     * */
    public RollingStock(double length, int rollID, double deceleration){
        this.length = length;
        this.rollID = rollID;
        this.deceleration = deceleration;
    }

    public double getDeceleration(){
        return this.deceleration;
    }

    /**
     * Returns the length of the rolling stock
     *
     * @return the length of the rolling stock
     * */
    public double getLength(){
        return this.length;
    }

    /**
     * Returns the length of the rolling stock
     *
     * @return the id of the rolling stock
     * */
    public int getRollID(){
        return this.rollID;
    }
}
