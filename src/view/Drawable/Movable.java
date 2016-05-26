package view.Drawable;

import javafx.scene.canvas.GraphicsContext;
import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

import java.awt.*;

/**
 * Created by Nicky on 9/05/2016.
 */
public interface Movable {

    /**
     * Returns how far through the movable is through the curve
     * */
    public double getDegDone();

    /**
     * Sets how far the movable is through the curve.
     * Used for switch directions in a curve
     * */
    public void setDegDone(double degDone);

    /**
     * Draws the movable on the screen
     *
     * @param g the graphics context to draw on
     * */
    public void draw(GraphicsContext g);

    /**
     * Updates the state of the movable
     * */
    public void update();

    /**
     * Sets the direction the movable should move in
     *
     * @param direction sets the direction the movable should move in
     * */
    public void setDirection(boolean direction);

    /**
     * Returns the current track it is on
     *
     * @return track
     * */
    public DefaultTrack getCurTrack();

    /**
     * Returns the rolling stock if there is one connected null if none
     *
     * @return the rolling stock
     * */
    public DrawableRollingStock getRollingStockConnected();

    /**
     * Returns out current location
     *
     * @return location
     * */
    public Point getCurrentLocation();

    /**
     * Returns the orientation of the movable
     *
     * @return the orientation
     * */
    public boolean getOrientation();

    /**
     * Returns if the movable is crashed or not
     *
     * @return crashed
     * */
    public boolean isCrashed();

    /**
     * Returns the current rotation of the movable used for drawing it
     *
     * @return the rotation in degrees
     * */
    public double getCurRotation();

    /**
     * Sets the movable to crashed when it collides with another train
     *
     * @param crashed crashed or not
     * */
    public void setCrashed(boolean crashed);

    /**
     * Returns the direction of the rolling stock
     *
     * @return direction
     * */
    public boolean getDirection();

    /**
     * Sets the junction track the movable is currently on
     * */
    public void setJuncTrack(DefaultTrack jt);

    /**
     * Sets the track the movable is currently on
     * */
    public void setCurTrack(DefaultTrack curTrack);

    /**
     * Returns the junction track of the movable if it is on one
     * */
    public DefaultTrack getJuncTrack();

    /**
     * Returns if the point at x,y is on the movable
     *
     * @param x the x location
     *
     * @param y the y location
     * */
    public boolean containsPoint(double x, double y);

    /**
     * Returns the length of the movable
     *
     * @return length
     * */
    public double getLength();

    /**
     * Returns the speed of the movable
     * */
    public double getCurrentSpeed();
}
