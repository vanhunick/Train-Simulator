package model;

/**
 * Created by vanhunick on 22/03/16.
 */
public interface Events {

    /**
     * Should be called when a section has gone from having a train on it to not.
     * Or when a section goes from not having a train on it to having a train on it
     * */
    public void sectionChanged(int sectionID);

    /**
     * Sets the speed for one of the trains on the track
     * */
    public void setSpeed(int trainID, double speed);

    /**
     * Occours when a junction has changed state
     * */
    public void setJunction(int junctionID, boolean toggle);
}
