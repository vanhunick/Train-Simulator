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

    // Max Speed of the train
    private  double maxSpeed;

    private double targetSpeed;

    // Train attributes
    private double acceleration;
    private double deceleration;
    private double weight;

    private double horsePower;
    private double torque;
    private double rpm;

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
    public Train(int id, double length,double maxSpeed,  boolean direction,boolean orientation, double acceleration, double deceleration){
        this.id = id;
        this.direction = direction;
        this.length = length;
        this.orientation = orientation;
        this.maxSpeed = maxSpeed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
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

    public double getDeceleration(){return this.deceleration;}

    public void setTargetSpeed(double speed){
        this.targetSpeed = speed;
    }

    public double getTargetSpeed(){return this.targetSpeed;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Train)) return false;

        Train train = (Train) o;

        if (id != train.id) return false;
        if (Double.compare(train.length, length) != 0) return false;
        if (orientation != train.orientation) return false;
        if (direction != train.direction) return false;
        if (Double.compare(train.maxSpeed, maxSpeed) != 0) return false;
        if (Double.compare(train.targetSpeed, targetSpeed) != 0) return false;
        return Double.compare(train.acceleration, acceleration) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        temp = Double.doubleToLongBits(length);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (orientation ? 1 : 0);
        result = 31 * result + (direction ? 1 : 0);
        temp = Double.doubleToLongBits(maxSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(targetSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(acceleration);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
