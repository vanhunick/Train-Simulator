package view.Drawable;

import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

import java.awt.*;

/**
 * Created by Nicky on 9/05/2016.
 */
public interface Movable {

    public double getDegDone();

    public void setDegDone(double degDone);

    /**
     * Returns the current track it is on
     *
     * @return track
     * */
    public DefaultTrack getCurTrack();

    public DrawableRollingStock getRollingStockConnected();

    /**
     * Returns out current location
     *
     * @return location
     * */
    public Point getCurrentLocation();

    public boolean getOrientation();

    public int getLastPointOnCurve();

    public boolean isCrashed();

    public double getCurRotation();

    public void setCrashed(boolean crashed);

    /**
     * Returns the direction of the rolling stock
     *
     * @return direction
     * */
    public boolean getDirection();

    public void setJuncTrack(DefaultTrack jt);

    public void setCurTrack(DefaultTrack curTrack);

    public DefaultTrack getJuncTrack();

    public void setLastPointOnCurve(int point);

    public boolean containsPoint(double x, double y);

    public double getLength();
}
