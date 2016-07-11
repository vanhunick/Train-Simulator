package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import view.Drawable.Movable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vanhunick on 14/04/16.
 */
public class JunctionTrack extends DefaultTrack {

    // Inbound Fields indexes
    private int inboundFromThrown;
    private int inboundFromStraight;
    private int inboundTo;

    // Outbound Fields indexes
    private int outboundToThrown;
    private int outBoundTotraight;
    private int outboundFrom;

    // If the junction is thrown
    private boolean thrown;

    // If the junction is inbound or outbound
    private boolean inbound;

    // The straight track in the junction
    private StraightHoriz straightTrack;

    // Outbound tracks
    private Quart3 outUpTrack;
    private Quart1 outRightTrack;

    // Inbound tracks
    private Quart2 inDown;
    private Quart4 inRight;

    private String drawDirection;

    private List<DefaultTrack> junctionTracks;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public JunctionTrack(int length, int drawID, int id,boolean thrown, boolean inbound, String drawDirection){
        super(length, drawID,id);
        this.drawDirection = drawDirection;
        this.thrown = thrown;
        this.inbound = inbound;
        this.straightTrack = new StraightHoriz(length,0,1);
        createAndAddTracks(length);
    }

    /**
     * Constructor for the starting piece
     * */
    public JunctionTrack(int startX, int startY, int length, int drawID,int id, String direction, boolean thrown, boolean inbound, String drawDirection){
        super(startX,startY,length,drawID,id, direction);
        this.drawDirection = drawDirection;
        this.thrown = thrown;
        this.inbound = inbound;
        this.straightTrack = new StraightHoriz(startX,startY,length,0,1,"RIGHT");

        createAndAddTracks(length);
        setLocation(null,direction,startX,startY);
    }

    /**
     * Creates the tracks that make up the junction track and adds them to the
     * list of tracks that make up the junction
     * */
    private void createAndAddTracks(int length){
        outRightTrack = new Quart1(length+(TRACK_WIDTH/2),1,1);//TODO possibly length/2
        inDown        = new Quart2(length+(TRACK_WIDTH/2),2,1);
        outUpTrack    = new Quart3(length+(TRACK_WIDTH/2),3,1);
        inRight       = new Quart4(length+(TRACK_WIDTH/2),4,1);

        junctionTracks = new ArrayList<>(Arrays.asList(outRightTrack,inDown,outUpTrack,inRight,straightTrack));
    }


    @Override
    public void setSelected(boolean selected){
        super.setSelected(selected);
        junctionTracks.forEach(t -> t.setSelected(selected));
    }

    public void setInboundLocation(String direction){
        if(drawDirection.equals("UP")){
            if(direction.equals("RIGHT")){
                straightTrack.setDirection("RIGHT");

                inRight.setStartX(straightTrack.getStartX() + straightTrack.getLength() - ((inRight.getLength()-TRACK_WIDTH/2)/2) - TRACK_WIDTH);
                inRight.setStartY(straightTrack.getStartY() - inRight.getLength() + TRACK_WIDTH);

                inRight.setDirection("UP");// Direction not actually left but it means we can use the method to connect the next peice
                inDown.setStart(inRight);
                inDown.setDirection("DOWN");
                inRight.setDirection("RIGHT");// Change the direction back to what it is supposed to be
                inRight.setMid();
                inDown.setMid();

            } else if(direction.equals("LEFT")){
                straightTrack.setDirection("LEFT");

                outUpTrack.setStartX(straightTrack.getStartX() - outUpTrack.getLength()/2);
                outUpTrack.setStartY(straightTrack.getStartY() - outUpTrack.getLength() + TRACK_WIDTH);
                outUpTrack.setDirection("UP");
                outRightTrack.setStart(outUpTrack);

                // Need to change direction
                outRightTrack.setDirection("DOWN");
                outUpTrack.setDirection("LEFT");
                outRightTrack.setMid();
                outUpTrack.setMid();
            }
        } else if(drawDirection.equals("DOWN")){
            if(direction.equals("RIGHT")){
                outRightTrack.setStartX(straightTrack.getStartX() + straightTrack.getLength() - inDown.getLength()/2);
                outRightTrack.setStartY(straightTrack.getStartY());
                outRightTrack.setDirection("DOWN");
                outUpTrack.setStart(outRightTrack);
                outRightTrack.setDirection("RIGHT");
                outUpTrack.setDirection("UP");

                outRightTrack.setMid();
                outUpTrack.setMid();

            } else if(direction.equals("LEFT")){
                inDown.setStartX(straightTrack.getStartX() - inDown.getLength()/2);
                inDown.setStartY(straightTrack.getStartY());
                inDown.setDirection("DOWN");

                inRight.setStart(inDown);
                inRight.setMid();
                inDown.setMid();
            }
        }
    }

    public void setOutBoundLocation(String direction){
        if(drawDirection.equals("UP")){
            if(direction.equals("RIGHT")){
                    outUpTrack.setStartX(straightTrack.getStartX() - ((outUpTrack.getLength()-TRACK_WIDTH/2)/2));
                    outUpTrack.setStartY(straightTrack.getStartY() - outUpTrack.getLength() + TRACK_WIDTH);
                    outUpTrack.setDirection("UP");
                    outUpTrack.setMid();
                    outRightTrack.setStart(outUpTrack);
                    outRightTrack.setMid();
                    outUpTrack.setMid();
            } else if(direction.equals("LEFT")){
                    inRight.setStartX(straightTrack.getStartX() + straightTrack.getLength() - inRight.getLength()/2);
                    inRight.setStartY(straightTrack.getStartY() - inRight.getLength() + TRACK_WIDTH);

                    inRight.setDirection("UP");// Direction not actually left but it means we can use the method to connect the next peice
                    inDown.setStart(inRight);
                    inDown.setMid();
                    inRight.setMid();
            }
        } else if(drawDirection.equals("DOWN")){
            if(direction.equals("RIGHT")){
                inDown.setStartX(straightTrack.getStartX() - inRight.getLength()/2);
                inDown.setStartY(straightTrack.getStartY());
                inDown.setDirection("DOWN");

                inRight.setStart(inDown);
                inRight.setDirection("RIGHT");
                inRight.setDirection("RIGHT");
                inDown.setMid();
                inRight.setMid();
            } else if(direction.equals("LEFT")){
                outRightTrack.setStartX(straightTrack.getStartX() + straightTrack.getLength() - inDown.getLength()/2);
                outRightTrack.setStartY(straightTrack.getStartY());
                outRightTrack.setDirection("DOWN");

                outUpTrack.setStart(outRightTrack);
                outRightTrack.setMid();
                outUpTrack.setMid();
            }
        }

    }

    /**
     * Sets the location the junction should be drawn at also affects the tracks within the junction
     * */
    public void setLocation(DefaultTrack from, String direction, double startX, double startY){
        if(from != null){
            direction = from.getDirection();
            straightTrack.setStart(from);
        }
        else {
            straightTrack.setStartX(startX);
            straightTrack.setStartY(startY);
        }
        straightTrack.setDirection(direction);
        setStartX(straightTrack.getStartX());
        setStartY(straightTrack.getStartY());
        setDirection(direction);

        if(inbound){
            setInboundLocation(direction);
        } else {
            setOutBoundLocation(direction);
        }

    }

    public void updateLocation(double startX, double startY){
        setLocation(null,getDirection(),startX,startY);
    }

    @Override
    public void toggleDirection(){
        if(getDirection().equals("LEFT")){
            setDirection("RIGHT");
            straightTrack.setDirection("RIGHT");
        }
        else{
            setDirection("LEFT");
            straightTrack.setDirection("LEFT");
        }
    }

    public void setStart(DefaultTrack from){
        setLocation(from,getDirection(),0,0);
    }

    /**
     * Returns if the track passed in is able to connect to the end of the junction
     * */
    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("RIGHT")){
            if(id == 0 || id == 2 || id == 3 || id == 6){
                if(Math.abs(straightTrack.getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(straightTrack.getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        else if(getDirection().equals("LEFT")){
            if(id == 0 || id == 1 || id == 4 || id == 6){
                if(Math.abs(straightTrack.getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(straightTrack.getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        return false;
    }

    /**
     * Returns if the track passed in is able to connect to the end of the junction if it is thrown
     * */
    public boolean canConnectThrown(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        // If it is inbound only
        if(!inBound()) {

            DefaultTrack endTrack = getEndTrack();

            if (getDirection().equals("RIGHT")) {
                if (id == 0 || id == 2 || id == 3) {
                    if (Math.abs(endTrack.getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                            Math.abs(endTrack.getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)
                        return true;
                }
            } else if (getDirection().equals("LEFT")) {
                if (id == 0 || id == 1 || id == 4) {
                    if (Math.abs(endTrack.getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                            Math.abs(endTrack.getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the junction blue if thrown white if not
     * */
    private void setTrackColors(boolean thrown){
        junctionTracks.forEach(t -> t.setColor(thrown ? Color.BLUE : Color.WHITE));
    }

    /**
     * Sets the junction the color passed in
     * */
    public void setColor(Color color){
        junctionTracks.forEach(t -> t.setColor(color));
    }

    /**
     * Draws the junction on the screen
     * */
    public void draw(GraphicsContext g){
        straightTrack.draw(g);
        setTrackColors(thrown);
        getEndTrack().draw(g);
        getInnerTrack().draw(g);
    }

    public double getNextPoint(Movable dt, double moveBy){
        double p = dt.getJuncTrack().getNextPoint(dt.getCurrentLocation(),dt.getCurRotation(),dt.getDegDone(),moveBy, dt);
        return p;
    }

    /**
     * Junctions tracks need to be able to change the lastsubable and need to be able to change the orientation
     * */
    public boolean checkOnAfterUpdate(Movable dt, double moveBy){
        if(dt.getJuncTrack().checkOnAfterUpdate(dt.getCurrentLocation(), dt.getCurRotation(),dt.getDegDone(), moveBy, dt)){
            return true;
        }

        if(dt.getJuncTrack().equals(straightTrack)){
            dt.setJuncTrack(null);
            return false;
        }

        if(forwardWithTrack(dt)){
            if(dt.getJuncTrack().equals(getEndTrack())){
                dt.setJuncTrack(null);
                return false;
            } else {
                dt.setJuncTrack(getEndTrack());
                return true;
            }
        } else {
            if(dt.getJuncTrack().equals(getInnerTrack())){
                dt.setJuncTrack(null);
                return false;
            } else {
                dt.setJuncTrack(getInnerTrack());
                return true;
            }
        }
    }

    public void setMid(double x, double y){
        updateLocation(x - getLength()/2,y + getLength()/2);
    }

    public boolean forwardWithTrack(Movable t){
        return t.getOrientation() && t.getDirection() || !t.getOrientation() && !t.getDirection();
    }

    public int getToOutbound(){
        if(thrown){
            return outboundToThrown;
        }
        else{
            return outBoundTotraight;
        }
    }

    /**
     * Returns the index in the tracks array of the track
     * */
    public int getInboundFrom(){
        if(thrown){
            return inboundFromThrown;
        }
        else{
            return inboundFromStraight;
        }
    }

    public Point getConnectionThrown(){
        if(getDirection().equals("RIGHT")){
            return outRightTrack.getConnectionPointTo();
        }
        else {
            return inDown.getConnectionPointTo();
        }
    }

    /**
     * Returns the track
     * */
    public DefaultTrack getTrackThrown(){
        if(inBound()){
            if(getDirection().equals("RIGHT")){
                return this.inDown;
            }
            return outRightTrack;
        }
        else{
            if(getDirection().equals("RIGHT")){
                return outRightTrack;
            }
            else {
                return inDown;
            }
        }
    }

    /**
     * Returns the internal last track of the junction following natural orientation
     * */
    public DefaultTrack getEndTrack(){
        if(inbound){
            if(getDirection().equals("RIGHT")){
                if(drawDirection.equals("UP")){
                    return  inRight;
                }
                else {
                    return  outRightTrack;
                }
            } else {
                if(drawDirection.equals("UP")){
                    return outUpTrack;
                }
                else {
                    return inDown;
                }
            }
        } else {
            if(getDirection().equals("RIGHT")){
                if(drawDirection.equals("UP")){
                    return outRightTrack;
                }
                else {
                    return inRight;
                }
            }
            else {
                if(drawDirection.equals("UP")){
                    return inDown;
                }
                else {
                    return outUpTrack;
                }
            }
        }
    }

    public DefaultTrack getInnerTrack(){
        if(inbound){
            if(getDirection().equals("RIGHT")){
                if(drawDirection.equals("UP")){
                    return  inDown;
                }
                else {
                    return outUpTrack;
                }
            } else {
                if(drawDirection.equals("UP")){
                    return outRightTrack;
                }
                else {
                    return inRight;
                }
            }
        } else {
            if(getDirection().equals("RIGHT")){
                if(drawDirection.equals("UP")){
                    return outUpTrack;
                }
                else {
                    return inDown;
                }
            }
            else {
                if(drawDirection.equals("UP")){
                    return inRight;
                }
                else {
                    return outRightTrack;
                }
            }
        }
    }

    /**
     * Gets the connection point for a track connected in the direction the
     * straight track is going
     * */
    public Point getConnectionPointTo(){return straightTrack.getConnectionPointTo();}

    public Point getConnectionPointFrom(){return straightTrack.getConnectionPointFrom();}

    public DefaultTrack getInboundThrownJuncTrack(){return inDown;}

    public DefaultTrack getInboundThrownNotNatJuncTrack(){return inRight;}

    public DefaultTrack getOutBoundThrownJuncTrack(){return outUpTrack;}

    public DefaultTrack getOutBoundNotNatThrownJuncTrack(){return outRightTrack;}

    public DefaultTrack getStraightTrack(){return straightTrack;}

    // Inbound getters
    public int getInboundFromThrown() {return inboundFromThrown;}
    public int getInboundFromStraight() {return inboundFromStraight;}
    public int getInboundTo() {return inboundTo;}

    // Outbound getters
    public int getOutboundToThrown() {return outboundToThrown;}
    public int getOutBoundTotraight() {return outBoundTotraight;}
    public int getOutboundFrom() {return outboundFrom;}

    // Inbound setters
    public void setInboundFromThrown(int inboundFromThrown) {this.inboundFromThrown = inboundFromThrown;}
    public void setInboundFromStraight(int inboundFromStraight) {this.inboundFromStraight = inboundFromStraight;}
    public void setInboundTo(int inboundTo) {this.inboundTo = inboundTo;}

    // Outbound setters
    public void setOutBoundTotraight(int outBoundTotraight) {this.outBoundTotraight = outBoundTotraight;}
    public void setOutboundToThrown(int outboundToThrown) {this.outboundToThrown = outboundToThrown;}
    public void setOutboundFrom(int outboundFrom) {this.outboundFrom = outboundFrom;}

    // Setts or gets if the juncton is thrown or not
    public void setThrown(boolean thrown){this.thrown = thrown;}
    public boolean getThrown(){return this.thrown;}

    // Returns if the track is inbound or not
    public boolean inBound(){return this.inbound;}

    @Override
    public boolean containsPoint(double x, double y){
        return straightTrack.containsPoint(x,y) || getInnerTrack().containsPoint(x,y) || getEndTrack().containsPoint(x,y);
    }

    @Override
    public String getDirection(){return straightTrack.getDirection();}
}
