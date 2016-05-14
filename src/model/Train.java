package model;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Train {

    // The id of the train
    private int id;

    // The length of the train
    private double length;

    // If it is going with the natural orientation of the track or not
    private boolean orientation;

    // Reversing or going forward
    private boolean direction;

    // Speed of the train
    private  double speed;

    private double maxSpeed;

    private double acceleration;

    /**
     * Creates a new Train object
     *
     * @param id the id of the train
     *
     * @param length the length of the train
     *
     * @param speed the speed of the train
     *
     * @param direction if it is going forwards or backwards
     *
     * @param orientation if it is starting with or again the natural orientation of the track
     * */
    public Train(int id, double length,double speed,  boolean direction,boolean orientation){
        this.id = id;
        this.direction = direction;
        this.length = length;
        this.orientation = orientation;
        this.speed = speed;
    }

    /**
     * Sets the speed of the train
     *
     * @param speed the speed the train should be
     * */
    public void setSpeed(double speed){
        this.speed = speed;
    }

    /**
     * Returns the id of the train
     *
     * @return id
     * */
    public int  getId() {
        return id;
    }

    /**
     * Returns the length
     *
     * @return the length
     * */
    public double getLength() {
        return length;
    }

    /**
     * Get the speed of the train
     *
     * @return speed
     * */
    public double getSpeed() {
        return speed;
    }

    /**
     * Returns the orientation of the train
     *
     * @return the orientation
     * */
    public boolean getOrientation() {
        return this.orientation;
    }

    /**
     * Returns the direction of the track
     *
     * @return the direction
     * */
    public boolean getDirection(){
        return this.direction;
    }

    /**
     * Set the direction of the train
     *
     * @param direction the direction to set
     * */
    public void setDirection(boolean direction){
        this.direction = direction;
    }
}
