package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import view.Drawable.Movable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanhunick on 14/04/16.
 */
public class JunctionTrack extends DefaultTrack {


    // Inbound Fields
    private int inboundFromThrown;
    private int inboundFromStraight;
    private int inboundTo;

    // Outbound Fields
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

    private List<DefaultTrack> junctionTracks;


    /**
     * Constructor for a piece that connects to another piece
     * */
    public JunctionTrack(int length, int drawID, int id,boolean thrown, boolean inbound){
        super(length, drawID,id);
        this.thrown = thrown;
        this.inbound = inbound;
        this.junctionTracks = new ArrayList<>();

        straightTrack = new StraightHoriz(length,0,1);
        junctionTracks.add(straightTrack);
        createAndAddTracks(length);
    }

    public void createAndAddTracks(int length){
        outRightTrack = new Quart1(length+(TRACK_WIDTH/2),1,1);//TODO possibly length/2
        inDown = new Quart2(length+(TRACK_WIDTH/2),2,1);
        outUpTrack = new Quart3(length+(TRACK_WIDTH/2),3,1);
        inRight = new Quart4(length+(TRACK_WIDTH/2),4,1);

        junctionTracks.add(outRightTrack);
        junctionTracks.add(inDown);
        junctionTracks.add(outUpTrack);
        junctionTracks.add(inRight);
    }



    @Override
    public void setSelected(boolean selected){
        super.setSelected(selected);

        if(selected) {
            if (thrown) {
                inDown.setSelected(true);
                inRight.setSelected(true);
                outUpTrack.setSelected(true);
                outRightTrack.setSelected(true);
                straightTrack.setSelected(false);
            } else {
                inDown.setSelected(false);
                inRight.setSelected(false);
                outUpTrack.setSelected(false);
                outRightTrack.setSelected(false);
                straightTrack.setSelected(true);
            }
        }
        else {
            inDown.setSelected(false);
            inRight.setSelected(false);
            outUpTrack.setSelected(false);
            outRightTrack.setSelected(false);
            straightTrack.setSelected(false);
        }
    }

    /**
     * Constructor for the starting piece
     * */
    public JunctionTrack(int startX, int startY, int length, int drawID,int id, String direction, boolean thrown, boolean inbound){

        super(startX,startY,length,drawID,id, direction);
        this.thrown = thrown;
        this.inbound = inbound;
        this.junctionTracks = new ArrayList<>();

        straightTrack = new StraightHoriz(startX,startY,length,0,1,"RIGHT");
        createAndAddTracks(length);
        setLocation(null,direction,startX,startY);
    }

    public void setLocation(DefaultTrack from, String direction, double startX, double startY){
        if(from != null){
            direction = from.getDirection();
            straightTrack.setStart(from);
        }
        else {
            straightTrack.setStartX(startX);
            straightTrack.setStartY(startY);
        }

        if(direction.equals("RIGHT")){
            super.setDirection("RIGHT");
            straightTrack.setDirection("RIGHT");


            if(inbound){
                inRight.setStartX(straightTrack.getStartX() + straightTrack.getLength() - ((inRight.getLength()-TRACK_WIDTH/2)/2) - TRACK_WIDTH);
                inRight.setStartY(straightTrack.getStartY() - inRight.getLength() + TRACK_WIDTH);

                inRight.setDirection("UP");// Direction not actually left but it means we can use the method to connect the next peice
                inDown.setStart(inRight);
                inDown.setDirection("DOWN");
                inRight.setDirection("RIGHT");// Change the direction back to what it is supposed to be
                inRight.setMid();
                inDown.setMid();
            }
            else{
                outUpTrack.setStartX(straightTrack.getStartX() - ((outUpTrack.getLength()-TRACK_WIDTH/2)/2));
                outUpTrack.setStartY(straightTrack.getStartY() - outUpTrack.getLength() + TRACK_WIDTH);
                outUpTrack.setDirection("UP");
                outUpTrack.setMid();
                outRightTrack.setStart(outUpTrack);
                outRightTrack.setMid();
                outUpTrack.setMid();
            }
        }
        else if(direction.equals("LEFT")){
            super.setDirection("LEFT");
            straightTrack.setDirection("LEFT");
            straightTrack.setStartX(startX);
            straightTrack.setStartY(startY);

            if(inbound){
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
            else{
                inRight.setStartX(straightTrack.getStartX() + straightTrack.getLength() - inRight.getLength()/2);
                inRight.setStartY(straightTrack.getStartY() - inRight.getLength() + TRACK_WIDTH);

                inRight.setDirection("UP");// Direction not actually left but it means we can use the method to connect the next peice
                inDown.setStart(inRight);
                inDown.setMid();
                inRight.setMid();
            }
        }

        super.setStartX(straightTrack.getStartX());
        super.setStartY(straightTrack.getStartY());


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

    public boolean canConnectThrown(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("RIGHT")){
            if(id == 0 || id == 2 || id == 3){
                if(Math.abs(outRightTrack.getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(outRightTrack.getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        else if(getDirection().equals("LEFT")){//TODO
            if(id == 0 || id == 1 || id == 4){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        return false;
    }


    public double getLength() {
        return super.getLength();
    }

    private void setTrackColors(boolean thrown){
        if(thrown){
            inDown.setColor(Color.BLUE);
            inRight.setColor(Color.BLUE);
            outUpTrack.setColor(Color.BLUE);
            outRightTrack.setColor(Color.BLUE);
            straightTrack.setColor(Color.BLUE);
        }
        else {
            inDown.setColor(Color.WHITE);
            inRight.setColor(Color.WHITE);
            outUpTrack.setColor(Color.WHITE);
            outRightTrack.setColor(Color.WHITE);
            straightTrack.setColor(Color.WHITE);
        }
    }

    public void setColor(Color color){
        junctionTracks.forEach(t -> t.setColor(color));
    }

    public void draw(GraphicsContext g){
        straightTrack.draw(g);
        setTrackColors(thrown);

        if(super.getDirection().equals("RIGHT")){
            if(inbound){
                inDown.draw(g);
                inRight.draw(g);
            }
            else{
                outUpTrack.draw(g);
                outRightTrack.draw(g);
            }
        }
        else if(super.getDirection().equals("LEFT")){
            if(inbound){
                outUpTrack.draw(g);
                outRightTrack.draw(g);
            }
            else{
                inDown.draw(g);
                inRight.draw(g);
            }
        }
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

        boolean nat = false;
        if(dt.getOrientation() && dt.getDirection() || !dt.getOrientation() && !dt.getDirection() ){
            nat = true;
        }

        // Not on the current section anymore
        if(dt.getJuncTrack().getDrawID() == 0){
            dt.setJuncTrack(null);
            return false;// No longer on the straight track so it is not on the junction
        }
        else if(dt.getJuncTrack().getDrawID() == 1){
            if(nat){
                dt.setJuncTrack(null);
                return false;
            }
            else{
                dt.setDegDone(0);
                dt.setJuncTrack(outUpTrack);
                return true;
            }
        }
        else if(dt.getJuncTrack().getDrawID() == 2){
            if(nat){
                dt.setDegDone(0);
                dt.setJuncTrack(inRight);
                return true;
            }
            else {
                dt.setJuncTrack(null);
                return false;
            }
        }
        else if(dt.getJuncTrack().getDrawID() == 3){
            if(nat){
                dt.setDegDone(0);
                dt.setJuncTrack(outRightTrack);
                return true;
            }
            else {
                dt.setJuncTrack(null);
                return false;
            }
        }
        else if(dt.getJuncTrack().getDrawID() == 4){
            if(nat){
                dt.setJuncTrack(null);
                return false;
            }
            else {
                dt.setDegDone(0);
                dt.setJuncTrack(inDown);
                return true;
            }
        }

        // Should never get here
        return false;
    }

    public void setMid(double x, double y){
        updateLocation(x - getLength()/2,y + getLength()/2);
    }

    public int getToOutbound(){
        if(thrown){
            return outboundToThrown;
        }
        else{
            return outBoundTotraight;
        }
    }

    public int getInboundFrom(){
        if(thrown){
            return inboundFromThrown;
        }
        else{
            return inboundFromStraight;
        }
    }

    public Point getConnectionThrown(){
        if(inbound){
            return inDown.getConnectionPointFrom();
        }
        else {
            return outRightTrack.getConnectionPointTo();
        }
    }

    public Point getConnectionPointTo(){
        return straightTrack.getConnectionPointTo();
    }

    public Point getConnectionPointFrom(){
        return straightTrack.getConnectionPointFrom();
    }

    public DefaultTrack getInboundThrownJuncTrack(){
        return inDown;
    }

    public DefaultTrack getInboundThrownNotNatJuncTrack(){
        return inRight;
    }

    public DefaultTrack getOutBoundThrownJuncTrack(){
        return outUpTrack;
    }

    public DefaultTrack getOutBoundNotNatThrownJuncTrack(){
        return outRightTrack;
    }


    public DefaultTrack getStraightTrack(){
        return straightTrack;
    }


    // Inbound getters
    public int getInboundFromThrown() {return inboundFromThrown;}
    public int getInboundFromStraight() {
        return inboundFromStraight;
    }
    public int getInboundTo() {
        return inboundTo;
    }

    // Outbound getters
    public int getOutboundToThrown() {
        return outboundToThrown;
    }
    public int getOutBoundTotraight() {
        return outBoundTotraight;
    }
    public int getOutboundFrom() {
        return outboundFrom;
    }


    // Inbound setters
    public void setInboundFromThrown(int inboundFromThrown) {
        this.inboundFromThrown = inboundFromThrown;
    }
    public void setInboundFromStraight(int inboundFromStraight) {
        this.inboundFromStraight = inboundFromStraight;
    }
    public void setInboundTo(int inboundTo) {
        this.inboundTo = inboundTo;
    }

    // Outbound setters
    public void setOutBoundTotraight(int outBoundTotraight) {
        this.outBoundTotraight = outBoundTotraight;
    }
    public void setOutboundToThrown(int outboundToThrown) {
        this.outboundToThrown = outboundToThrown;
    }
    public void setOutboundFrom(int outboundFrom) {
        this.outboundFrom = outboundFrom;
    }


    // Setts or gets if the juncton is thrown or not
    public void setThrown(boolean thrown){
        this.thrown = thrown;
    }
    public boolean getThrown(){
        return this.thrown;
    }

    // Returns if the track is inbound or not
    public boolean inBound(){
        return this.inbound;
    }

    /**
     * Used for figuring out where to go inside the junction track
     * */
    public DefaultTrack getToTrack(){
        if(inbound){
            return straightTrack;
        }

        if(thrown){
            return outUpTrack;
        }
        else {
            return straightTrack;
        }
    }

    /**
     * Used for figuring out where to go inside the junction track
     * */
    public DefaultTrack getFromTrack(){
        if(inbound){
            if(thrown){
                return inRight;
            }
            else{
                return straightTrack;
            }
        }
        else {
            return straightTrack;
        }
    }

    public boolean containsPoint(double x, double y){
        if(inbound){
            return straightTrack.containsPoint(x,y) || inDown.containsPoint(x,y) || inRight.containsPoint(x,y);
        }
        else {
            return straightTrack.containsPoint(x,y) || outUpTrack.containsPoint(x,y) || outRightTrack.containsPoint(x,y);
        }
    }


    public DefaultTrack getTrackThrown(){
        if(inBound()){
            return this.inDown;
        }
        else{
            return outRightTrack;
        }
    }

    public boolean canConnect(double x, double y){


        return false;
    }

    //TODO support the top tracks
    public Point getConnectionPoint(){
        if(inbound){
            if(getDirection().equals("RIGHT")){
                return inDown.getConnectionPoint();
            }
            return inDown.getConnectionPoint();
        }
        else {
            return outRightTrack.getConnectionPoint();
        }
    }



    @Override
    public String getDirection(){
        return straightTrack.getDirection();
    }
}
