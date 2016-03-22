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


    //TODO work out how to represent this, on the track or section or location on the section or track
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

    public double getId() {
        return id;
    }

    public double getLength() {
        return length;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isOrientation() {
        return orientation;
    }

    public double getPosition() {
        return position;
    }
}
