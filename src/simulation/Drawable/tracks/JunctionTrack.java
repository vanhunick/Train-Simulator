package simulation.Drawable.tracks;

import util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simulation.Drawable.Movable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vanhunick on 14/04/16.
 */
public class JunctionTrack extends DefaultTrack {

    // Inbound Fields indexes
    private int inboundFromThrown = -1;
    private int inboundTo = -1;

    // Outbound Fields indexes
    private int outboundToThrown = -1;

    // If the junction is thrown
    private boolean thrown;

    // If the junction is inbound or outbound
    private boolean inbound;

    // The straight track in the junction
    private StraightHoriz straightTrack;

    // Outbound util.tracks
    private Quart3 outUpTrack;
    private Quart1 outRightTrack;

    // Inbound util.tracks
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
     * Creates the util.tracks that make up the junction track and adds them to the
     * list of util.tracks that make up the junction
     * */
    private void createAndAddTracks(int length){
        outRightTrack = new Quart1(length,1,1);//+(TRACK_WIDTH/2)
        inDown        = new Quart2(length,2,1);//+(TRACK_WIDTH/2)
        outUpTrack    = new Quart3(length,3,1);///2+(TRACK_WIDTH/2)
        inRight       = new Quart4(length ,4,1);//+(TRACK_WIDTH/2)
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
                inRight.setDirection("UP");
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

                inDown.setMid();
                inRight.setMid();
            } else if(direction.equals("LEFT")){
                outRightTrack.setStartX(straightTrack.getStartX() + straightTrack.getLength() - inDown.getLength()/2);
                outRightTrack.setStartY(straightTrack.getStartY());
                outRightTrack.setDirection("DOWN");

                outUpTrack.setStart(outRightTrack);
                inRight.setDirection("LEFT");
                outRightTrack.setMid();
                outUpTrack.setMid();
            }
        }

    }

    public DefaultTrack getTrackToStartOn(int originTrackID){
        // outbound condition
        if(!inbound){

            // Coming into the junction wrong way
            if(originTrackID == getTo()){
                return straightTrack;
            }

            // Coming into the junction wrong way
            if(originTrackID == getOutboundToThrown()){
                return getEndTrack();
            }

            if(originTrackID == getFrom()){
                if(thrown){
                    return getInnerTrack();
                } else {
                    return straightTrack;
                }
            }
        }

        // inbound condition
        if(inbound){

            // Coming into the junction wrong way through straight
            if(originTrackID == getFrom()){
                return straightTrack;
            }

            // Coming into the junction wrong through thrown
            if(originTrackID == getInboundFromThrown()){
                return getInnerTrack();
            }

            if(originTrackID == getTo()){
                if(thrown){
                    return getEndTrack();
                } else {
                    return straightTrack;
                }
            }
        }

        return straightTrack;
    }

    /**
     * Sets the location the junction should be drawn at also affects the util.tracks within the junction
     * */
    public void setLocation(DefaultTrack from, String direction, double startX, double startY){
        if(from != null){
            direction = from.getDirection();
            straightTrack.setStart(from);
        }
        else {
            straightTrack.setStartX(startX);
            straightTrack.setStartY(startY);
            straightTrack.setMid(startX,startY);
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

        setStartX(straightTrack.getStartX());
        setStartY(straightTrack.getStartY());// TODO just changed
    }

    public void updateLocation(double startX, double startY){
        setLocation(null,getDirection(),startX,startY);
    }

    //TODO this is not a good way to do it but all alternatives will take too much time
    public void moveToThrown(){
        if(!inBound())return;

        if(drawDirection.equals("DOWN")){
            straightTrack.setStartY((straightTrack.getStartY() - getLength() + DefaultTrack.TRACK_WIDTH/2));
            getInnerTrack().setStartY((getInnerTrack().getStartY() - getLength()) + DefaultTrack.TRACK_WIDTH/2);
            getEndTrack().setStartY((getEndTrack().getStartY() - getLength() + DefaultTrack.TRACK_WIDTH/2));
        }

        if(drawDirection.equals("UP")){

            straightTrack.setStartY((straightTrack.getStartY() + getLength() - DefaultTrack.TRACK_WIDTH));
            getInnerTrack().setStartY((getInnerTrack().getStartY() + getLength()) - DefaultTrack.TRACK_WIDTH);
            getEndTrack().setStartY((getEndTrack().getStartY() + getLength() - DefaultTrack.TRACK_WIDTH));
        }
        getInnerTrack().setMid();
        getEndTrack().setMid();
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

        // If it is not inbound
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
        junctionTracks.forEach(t -> t.setColor(thrown ? Color.BLUE : DefaultTrack.RAIL_COLOR));
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
        return dt.getJunctionTrack().getNextPoint(dt.getCurrentLocation(),dt.getCurRotation(),dt.getDegDone(),moveBy, dt);
    }

    /**
     * Junctions tracks need to be able to change the last subable and need to be able to change the orientation
     * */
    public boolean checkOnAfterUpdate(Movable dt, double moveBy){
        if(dt.getJunctionTrack().checkOnAfterUpdate(dt.getCurrentLocation(), dt.getCurRotation(),dt.getDegDone(), moveBy, dt)){
            return true;
        }

        if(dt.getJunctionTrack().equals(straightTrack)){
            dt.setJunctionTrack(null);
            return false;
        }

        if(forwardWithTrack(dt)){
            if(dt.getJunctionTrack().equals(getEndTrack())){
                dt.setJunctionTrack(null);
                return false;
            } else {
                dt.setJunctionTrack(getEndTrack());
                return true;
            }
        } else {
            if(dt.getJunctionTrack().equals(getInnerTrack())){
                dt.setJunctionTrack(null);
                return false;
            } else {
                dt.setJunctionTrack(getInnerTrack());
                return true;
            }
        }
    }

    public void setMid(double x, double y){
        if(drawDirection.equals("DOWN")){
            y-= 22;
        }
        else {
            y+=22;
        }
        updateLocation(x,y);
    }

    public boolean forwardWithTrack(Movable t){
        return t.getOrientation() && t.getDirection() || !t.getOrientation() && !t.getDirection();
    }

    public int getToOutbound(){
        if(thrown){
            return outboundToThrown;
        }
        else{
            return getTo();
        }
    }

    /**
     * Returns the index in the util.tracks array of the track
     * */
    public int getInboundFrom(){
        if(thrown){
            return inboundFromThrown;
        }
        else{
            return getFrom();
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
        } else{
            if(getDirection().equals("RIGHT")){
                return outRightTrack;
            } else {
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
                } else {
                    return  outRightTrack;
                }
            } else { // Left
                if(drawDirection.equals("UP")){
                    return outUpTrack;
                } else {
                    return inDown;
                }
            }
        } else {
            if(getDirection().equals("RIGHT")){
                if(drawDirection.equals("UP")){
                    return outRightTrack;
                } else {
                    return inRight;
                }
            } else {
                if(drawDirection.equals("UP")){
                    return inDown;
                } else {
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
                } else {
                    return outUpTrack;
                }
            } else { // Left
                if(drawDirection.equals("UP")){
                    return outRightTrack;
                } else {
                    return inRight;
                }
            }
        } else {// outbound
            if(getDirection().equals("RIGHT")){
                if(drawDirection.equals("UP")){
                    return outUpTrack;
                } else {
                    return inDown;
                }
            } else { // Left
                if(drawDirection.equals("UP")){
                    return inRight;
                } else {
                    //TODO should be here
                    return outRightTrack;
                }
            }
        }
    }


    /**
     * Checks if the movable goes over the junction backwards
     * */
    public boolean checkThrownCrash(Movable m){
        if(inBound() && forwardWithTrack(m)){ // The train can only crash if it moving against normal orientation
            if(!(m.getJunctionTrack() instanceof StraightHoriz)){
                return !thrown;
            } else{
                return thrown;// True when thrown means it
            }
        }

        if(!inBound() && !forwardWithTrack(m)){ // The train can only crash if it moving against normal orientation
            if(!(m.getJunctionTrack() instanceof StraightHoriz)){
                return !thrown;
            } else{
                return thrown;// True when thrown means it
            }
        }
        return false;
    }

    /**
     * Gets the connection point for a track connected in the direction the
     * straight track is going
     * */
    public Point2D getConnectionPointTo(){return straightTrack.getConnectionPointTo();}

    public Point2D getConnectionPointFrom(){return straightTrack.getConnectionPointFrom();}

    public DefaultTrack getStraightTrack(){return straightTrack;}

    // Inbound getters
    public int getInboundFromThrown() {return inboundFromThrown;}

    // Outbound getters
    public int getOutboundToThrown() {return outboundToThrown;}

    // Inbound setters
    public void setInboundFromThrown(int inboundFromThrown) {this.inboundFromThrown = inboundFromThrown;}
    public void setInboundTo(int inboundTo) {this.inboundTo = inboundTo;}

    // Outbound setters
    public void setOutboundToThrown(int outboundToThrown) {this.outboundToThrown = outboundToThrown;}

    // Setts or gets if the juncton is thrown or not
    public void setThrown(boolean thrown){this.thrown = thrown;}
    public boolean getThrown(){return this.thrown;}

    // Returns if the track is inbound or not
    public boolean inBound(){return this.inbound;}

    public String getDrawDirection(){return this.drawDirection;}

    @Override
    public boolean containsPoint(double x, double y){
        return straightTrack.containsPoint(x,y) || getInnerTrack().containsPoint(x,y) || getEndTrack().containsPoint(x,y);
    }

    @Override
    public double pixelsLeftAfterMove(Point2D curPoint, double curRot, double rotationDone, double speed, Movable movable) {
        return movable.getJunctionTrack().pixelsLeftAfterMove(curPoint,curRot,rotationDone,speed,movable);
    }

    @Override
    public String getDirection(){return straightTrack.getDirection();}
}
