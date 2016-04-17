package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by vanhunick on 14/04/16.
 */
public interface Track {

    /**
     * Returns the track the track came from
     * */
    public Track getFrom();


    /**
     * Returns the track the track leads to
     * */
    public Track getTo();


    /**
     * Draws the track on the graphics context
     * */
    public void draw(GraphicsContext gc);


    /**
     * Gets the length of the track
     * */
    public double getLength();


    public void setFrom(Track from);

    public int getID();

}
