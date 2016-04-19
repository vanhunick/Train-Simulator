package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.awt.*;

/**
 * Created by vanhunick on 14/04/16.
 */
public class JunctionTrack extends DefaultTrack {

    private int fromTrack;
    private int toThrownTrack;
    private int toNotThrownTrack;

    private boolean thrown;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public JunctionTrack(int length, int drawID, int id,boolean thrown){
        super(length, drawID,id);
        this.thrown = thrown;
    }

    /**
     * Constructor for the starting piece
     * */
    public JunctionTrack(int startX, int startY, int length, int drawID,int id, String direction, boolean thrown){
        super(startX,startY,length,drawID,id, direction);
        this.thrown = thrown;
    }

    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("RIGHT")){
            super.setDirection("RIGHT");

            if(from.getDrawID() == 4){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() + from.getLength() - super.getLength();
            }
        }

        super.setStartX(startX);
        super.setStartY(startY);
    }


    public double getLength() {
        return super.getLength();
    }


    @Override
    public void draw(GraphicsContext g){
        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();

        g.setFill(Color.WHITE);

        if(thrown){
            g.setStroke(Color.ORANGE);
        }
        else{
            g.setStroke(Color.GREEN);
        }

        g.strokeArc(startX , startY, length, length, -90, 90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH*2), length - (TRACK_WIDTH*2), -90, 90, ArcType.OPEN);

        g.strokeArc(startX + length - TRACK_WIDTH, startY, length, length, 90, 90, ArcType.OPEN);
        g.strokeArc(startX + length, startY + TRACK_WIDTH, length - (TRACK_WIDTH * 2), length - (TRACK_WIDTH * 2), 90, 90, ArcType.OPEN);


        g.setStroke(Color.WHITE);
    }

    private boolean onFirstSec = true;

    public Point getNextPoint(Point cur, int lastSubAngle, double moveBy, boolean nat, boolean forward){
        if(onFirstSec){
            double lengthOfQauter = lengthOfQuater();
            double points = (int)(lengthOfQauter/moveBy);
            double angle = 90;

            if(super.getDirection().equals("LEFT")){
                lastSubAngle = (int)points - lastSubAngle;
            }


            double subAngle = (lastSubAngle/points)*Math.toRadians(angle);


            double radius = ((super.getLength())/2 -  TRACK_WIDTH/2);

            double x = super.getStartX() + super.getLength()/2;
            double y = super.getStartY() + super.getLength() - TRACK_WIDTH/2;

            double a = 1.57079632679;
            a=a-angle*Math.PI/180;

            double fx = Math.cos(a);
            double fy = Math.sin(a);

            double lx = -(Math.sin(a));
            double ly = Math.cos(a);


            double xi = x + radius*(Math.sin(subAngle)*fx + (1-Math.cos(subAngle))*(-lx));
            double yi = y + radius*(Math.sin(subAngle)*fy + (1-Math.cos(subAngle))*(-ly));
            return new Point((int)xi,(int)yi);
        }
        else{
            double lengthOfQauter = lengthOfQuater();
            double points = (int)(lengthOfQauter/moveBy);
            double angle = 90;


            if(super.getDirection().equals("RIGHT")){
                lastSubAngle = (int)points - lastSubAngle;
            }



            double subAngle = (lastSubAngle/points)*Math.toRadians(angle);


            double radius = ((super.getLength())/2 -  TRACK_WIDTH/2);

            double x = super.getStartX() + super.getLength()/2;
            double y = super.getStartY() + TRACK_WIDTH/2;

            double a = 1.57079632679;
            a=a+angle*Math.PI/180;

            double fx = Math.cos(a);
            double fy = Math.sin(a);

            double lx = -(Math.sin(a));
            double ly = Math.cos(a);


            double xi = x + radius*(Math.sin(subAngle)*fx + (1-Math.cos(subAngle))*(-lx));
            double yi = y + radius*(Math.sin(subAngle)*fy + (1-Math.cos(subAngle))*(-ly));
            return new Point((int)xi,(int)yi);
        }
    }

    public double getNextRotation(double curRotation, double speed, boolean nat, boolean forward){
        if(onFirstSec){
            double l = lengthOfQuater();

            double updates = l/speed;

            double rotateCHange = 90/updates;

            if(super.getDirection().equals("LEFT")){
                return curRotation + rotateCHange;
            }
            else {
                return curRotation - rotateCHange;
            }
        }
        else {
            double l = lengthOfQuater();

            double updates = l/speed;

            double rotateCHange = 90/updates;

            if(super.getDirection().equals("LEFT")){
                return curRotation + rotateCHange;
            }
            else {
                return curRotation - rotateCHange;
            }
        }
    }

    private String firstDirection = "UP";

    public boolean checkOnAfterUpdate(Point curPoint, double lastSubAnle, double moveBy, boolean nat, boolean forward){
        if(!thrown)return false;//since it just moves onto the next track
        Point p = getNextPoint(curPoint, (int)lastSubAnle, moveBy, nat, forward);

        if(onFirstSec){

            if(firstDirection.equals("LEFT")){
                if(p.getY() > super.getStartY() + super.getLength()){
                    onFirstSec = false;
                }

                if(p.getX() < super.getStartX() + super.getLength()/2){
                    onFirstSec = false;
                }
            }
            else if(firstDirection.equals("UP")){
                if(p.getX() > super.getStartX() + super.getLength()){
                    onFirstSec = false;
                }
                if(p.getY() < super.getStartY() + super.getLength()/2){
                    onFirstSec = false;
                }

            }
            return true;
        }
        else{
            if(super.getDirection().equals("DOWN")){
                if(p.getY() > super.getStartY() + super.getLength()/2){
                    return false;//No longer in this section
                }
                if(p.getX()< super.getStartX() ){
                    return false;//No longer in this section
                }
            }
            else if(super.getDirection().equals("RIGHT")){
                if(p.getX() > super.getStartX() + super.getLength()/2 - 20){//
                    return false;//No longer in this section
                }
                if(p.getY() < super.getStartY() - super.getLength()/2){
                    return false;//No longer in this section
                }
            }
            return true;
        }
    }


    public double lengthOfQuater(){
        double radius = (super.getLength()-TRACK_WIDTH/2)/2;
        double circumference = 2 * Math.PI * radius;
        return circumference/4;
    }


    public int getFrom() {
        return fromTrack;
    }


    public void setFrom(int from){
        //even though from can be from multiple pieces it does not matter from this tracks perspective
        this.fromTrack = from;
    }

    public int getTo() {
        if(thrown){
            return toThrownTrack;
        }
        else {
            return toNotThrownTrack;
        }
    }

    public void setThrown(boolean thrown){
        this.thrown = thrown;
    }

    public boolean getThrown(){
        return this.thrown;
    }

    public int getToNotThrownTrack() {
        return toNotThrownTrack;
    }

    public void setToThrownTrack(int toThrownTrack) {
        this.toThrownTrack = toThrownTrack;
    }

    public void setToNotThrownTrack(int toNotThrownTrack) {
        this.toNotThrownTrack = toNotThrownTrack;
    }

    public void setFromTrack(int fromTrack) {
        this.fromTrack = fromTrack;
    }
}
