package simulation.model;

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
     *
     * @param trainID the id of the train
     *
     * @param speed the speed to set the train to
     * */
    public void setSpeed(int trainID, double speed);

    /**
     * Occurs when a junction has changed state
     *
     * @param junctionID the id of the junction to set
     *
     * @param toggle the state to set the junction with
     * */
    public void setJunction(int junctionID, boolean toggle);

    /**
     * Sets the direction of a train
     *
     * @param trainId the id of the train to change direction
     *
     * @param direction the direction to set the train with
     * */
    public void setDirection(int trainId, boolean direction);
}
