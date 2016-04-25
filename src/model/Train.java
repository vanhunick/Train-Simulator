package model;

import java.util.List;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Train {
    private int id;
    private double length;

    // Weather it is going with the natural orientation of the track or not
    private boolean orientation;

    // Reversing or going forward
    private boolean direction;

    private  double speed;

    // The id of the section it is on
    private int position;

    public Train(int id, double length,double speed, int position,  boolean direction,boolean orientation){
        this.id = id;
        this.direction = direction;
        this.length = length;
        this.orientation = orientation;
        this.speed = speed;
        this.position = position;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public int  getId() {
        return id;
    }

    public double getLength() {
        return length;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean getOrientation() {
        return this.orientation;
    }

    public int getPosition() {
        return position;
    }

    public boolean getDirection(){
        return this.direction;
    }

    public void setDirection(boolean direction){
        this.direction = direction;
    }
}
