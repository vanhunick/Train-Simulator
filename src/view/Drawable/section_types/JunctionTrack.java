package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import view.Drawable.DrawableTrain;
import view.Drawable.Movable;

import java.awt.*;

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


    /**
     * Constructor for a piece that connects to another piece
     * */
    public JunctionTrack(int length, int drawID, int id,boolean thrown, boolean inbound){
        super(length, drawID,id);
        this.thrown = thrown;
        this.inbound = inbound;


        straightTrack = new StraightHoriz(length,0,1);
        outRightTrack = new Quart1(length+(TRACK_WIDTH/2),1,1);//TODO possibly length/2
        inDown = new Quart2(length+(TRACK_WIDTH/2),2,1);
        outUpTrack = new Quart3(length+(TRACK_WIDTH/2),3,1);
        inRight = new Quart4(length+(TRACK_WIDTH/2),4,1);
    }

    /**
     * Constructor for the starting piece
     * */
    public JunctionTrack(int startX, int startY, int length, int drawID,int id, String direction, boolean thrown){
        super(startX,startY,length,drawID,id, direction);
        this.thrown = thrown;
    }

    public void setStart(DefaultTrack from){
        if(from.getDirection().equals("RIGHT")){
            super.setDirection("RIGHT");
            straightTrack.setDirection("RIGHT");
            straightTrack.setStart(from);

            if(inbound){
                inRight.setStartX(straightTrack.getStartX() + straightTrack.getLength() - ((inRight.getLength()-TRACK_WIDTH/2)/2) - TRACK_WIDTH);
                inRight.setStartY(straightTrack.getStartY() - inRight.getLength() + TRACK_WIDTH);

                inRight.setDirection("UP");// Direction not actually left but it means we can use the method to connect the next peice
                inDown.setStart(inRight);
                inDown.setDirection("DOWN");
                inRight.setDirection("RIGHT");// Change the direction back to what it is supposed to be
            }
            else{
                outUpTrack.setStartX(straightTrack.getStartX() - ((outUpTrack.getLength()-TRACK_WIDTH/2)/2));
                outUpTrack.setStartY(straightTrack.getStartY() - outUpTrack.getLength() + TRACK_WIDTH);
                outUpTrack.setDirection("UP");
                outUpTrack.setMid();
                outRightTrack.setStart(outUpTrack);
            }
        }
        else if(from.getDirection().equals("LEFT")){
            super.setDirection("LEFT");
            straightTrack.setDirection("LEFT");
            straightTrack.setStart(from);

            if(inbound){
                outUpTrack.setStartX(straightTrack.getStartX() - outUpTrack.getLength()/2);
                outUpTrack.setStartY(straightTrack.getStartY() - outUpTrack.getLength() + TRACK_WIDTH);
                outUpTrack.setDirection("UP");

                outRightTrack.setStart(outUpTrack);
            }
            else{
                inRight.setStartX(straightTrack.getStartX() + straightTrack.getLength() - inRight.getLength()/2);
                inRight.setStartY(straightTrack.getStartY() - inRight.getLength() + TRACK_WIDTH);

                inRight.setDirection("UP");// Direction not actually left but it means we can use the method to connect the next peice
                inDown.setStart(inRight);
                inRight.setDirection("RIGHT");// Change the direction back to what it is supposed to be
            }
        }

        super.setStartX(straightTrack.getStartX());
        super.setStartY(straightTrack.getStartY());
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





    @Override
    public String getDirection(){
        return straightTrack.getDirection();
    }
}
