package simulation.Drawable;


import util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import simulation.Drawable.tracks.DefaultTrack;


/**
 * Created by Nicky on 9/05/2016.
 */
public interface Movable {

    /**
     * Returns how far through the movable is through the curve
     * */
    double getDegDone();

    /**
     * Sets how far the movable is through the curve.
     * Used for switch directions in a curve
     * */
    void setDegDone(double degDone);

    /**
     * Draws the movable on the screen
     *
     * @param g the graphics context to draw on
     * */
    void draw(GraphicsContext g);

    /**
     * Updates the state of the movable
     * */
    void update();

    /**
     * Returns the current track it is on
     *
     * @return track
     * */
    DefaultTrack getCurTrack();

    /**
     * Returns the rolling stock if there is one connected null if none
     *
     * @return the rolling stock
     * */
    DrawableRollingStock getRollingStockConnected();

    /**
     * Returns out current location
     *
     * @return location
     * */
    Point2D getCurrentLocation();

    /**
     * Returns the orientation of the movable
     *
     * @return the orientation
     * */
    boolean getOrientation();

    /**
     * Returns if the movable is crashed or not
     *
     * @return crashed
     * */
    boolean isCrashed();

    /**
     * Returns the current rotation of the movable used for drawing it
     *
     * @return the rotation in degrees
     * */
    double getCurRotation();

    /**
     * Sets the movable to crashed when it collides with another train
     *
     * @param crashed crashed or not
     * */
    void setCrashed(boolean crashed);

    /**
     * Returns the direction of the rolling stock
     *
     * @return direction
     * */
    boolean getDirection();

    /**
     * Sets the junction track the movable is currently on
     * */
    void setJuncTrack(DefaultTrack jt);

    /**
     * Sets the track the movable is currently on
     * */
    void setCurTrack(DefaultTrack curTrack);

    /**
     * Returns the junction track of the movable if it is on one
     * */
    DefaultTrack getJuncTrack();

    /**
     * Returns if the point at x,y is on the movable
     *
     * @param x the x location
     *
     * @param y the y location
     * */
    boolean containsPoint(double x, double y);

    /**
     * Returns the length of the movable
     *
     * @return length
     * */
    double getLengthPixels();

    /**
     * Returns the speed of the movable
     * */
    double getCurrentSpeed();


    /**
     * Returns the last distance moved in pixels of the train
     * */
    double getDistanceMoved();

    /**
     * Returns the front point of the movable
     * */
    Point2D getFront();

    /**
     * Returns the back point of the movable
     * */
    Point2D getBack();
}
