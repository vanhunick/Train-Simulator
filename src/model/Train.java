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
    private  double maxSpeed;

    private double targetSpeed;

    private double acceleration;

    /**
     * Creates a new Train object
     *
     * @param id the id of the train
     *
     * @param length the length of the train
     *
     * @param maxSpeed the maxSpeed of the train
     *
     * @param direction if it is going forwards or backwards
     *
     * @param orientation if it is starting with or again the natural orientation of the track
     * */
    public Train(int id, double length,double maxSpeed,  boolean direction,boolean orientation, double acceleration){
        this.id = id;
        this.direction = direction;
        this.length = length;
        this.orientation = orientation;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
    }

    /**
     * Sets the maxSpeed of the train
     *
     * @param maxSpeed the maxSpeed the train should be
     * */
    public void setMaxSpeed(double maxSpeed){
        this.maxSpeed = maxSpeed;
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
     * Get the maxSpeed of the train
     *
     * @return maxSpeed
     * */
    public double getMaxSpeed() {
        return maxSpeed;
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

    /**
     * Gets the acceleration of the train
     * */
    public double getAcceleration(){return this.acceleration;}

    public void setTargetSpeed(double speed){
        this.targetSpeed = speed;
    }

    public double getTargetSpeed(){return this.targetSpeed;}
}
