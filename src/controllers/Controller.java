package controllers;

import simulation.model.Event;

/**
 * Created by vanhunick on 25/05/16.
 */
public interface Controller extends Event.Listener{

    /**
     * Responds to a train changing sections
     *
     * @param sectionID the ID of the section that triggered the event
     * */
    void receiveSectionEvent(int sectionID);
}
