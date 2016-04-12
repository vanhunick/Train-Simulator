package model;

import java.util.List;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Train {
    private int id;
    private double length;
    private boolean orientation;
    private  double speed;


    // The id of the section it is on
    private int position;

    public Train(int id, double length,double speed, int position, boolean orientation){
        this.id = id;
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

    public boolean orientation() {
        return orientation;
    }

    public int getPosition() {
        return position;
    }
}
