package controllers;

import model.Event;

/**
 * Created by vanhunick on 25/05/16.
 */
public interface Controller extends Event.Listener{

    public void receiveSectionEvent(int sectionID);

}
