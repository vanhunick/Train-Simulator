package model;

/**
 * Created by vanhunick on 21/04/16.
 */
public class RollingStock {

    private double length;
    private int trainID;

    public RollingStock(double length, int trainID){
        this.length = length;
        this.trainID = trainID;
    }

    public double getLength(){
        return this.length;
    }
}
