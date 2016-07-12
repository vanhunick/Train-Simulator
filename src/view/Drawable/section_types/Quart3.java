package view.Drawable.section_types;

import Util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import view.Drawable.Movable;
import view.SimulationUI;


/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart3 extends DefaultTrack {

    private double radius = getLength()/2;
    private double midPointX = getStartX() - ((TRACK_WIDTH/2)/2) + radius;
    private double midPointY = getStartY() - TRACK_WIDTH/2 + radius;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart3(int length, int drawID, int id){
        super(length, drawID, id);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart3(int startX, int startY, int length, int drawID, String direction, int id){
        super(startX,startY,length,drawID,id, direction );
    }

    /**
     * Works out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("DOWN")){
            super.setDirection("LEFT");

            if(from.getDrawID() == 1){
                startX = from.getStartX() - super.getLength() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2 - super.getLength()/2;
            }
            else if(from.getDrawID() == 2){
                startX = from.getStartX() + from.getLength()/2 -super.getLength()/2;
                startY = from.getStartY() + from.getLength()/2 - super.getLength()/2;
            }
            else if(from.getDrawID() == 5){
                startX = from.getStartX() - super.getLength();//  - TRACK_WIDTH
                startY = from.getStartY() + from.getLength() - super.getLength()/2;
            }
        }
        else if(from.getDirection().equals("RIGHT")){
            super.setDirection("UP");
            if(from.getDrawID() == 0){
                startX = from.getStartX() + from.getLength() -super.getLength()/2;
                startY = from.getStartY() - super.getLength() + TRACK_WIDTH;
            }
            else if(from.getDrawID() == 1){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() - super.getLength()/2 - from.getLength()/2 + TRACK_WIDTH;
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY();
            }
        }
        setStartX(startX);
        setStartY(startY);

        setMid();
    }

    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("LEFT")){
            if(id == 0 || id == 4 || id == 1){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        else if(getDirection().equals("UP")){
            if(id == 1 || id == 2 || id == 5){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        return false;
    }

    public void toggleDirection(){
        if(getDirection().equals("LEFT")){
            setDirection("UP");
        }
        else {
            setDirection("LEFT");
        }
    }

    public void setMid(){
        radius = getLength()/2;
        midPointX = getStartX() - TRACK_WIDTH/2 + radius;
        midPointY = getStartY() - TRACK_WIDTH/2 + radius;
    }


    // could return the rotation and just modify the point
    public double getNextPoint(Point2D curPoint, double curRot, double rotationDone, double speed, Movable movable){

        // Need to minus the degrees to change
        double degreesToMove = (90/lengthOfQuater()/2) * speed;

        double nextRotation = 0;
        if(super.getDirection().equals("UP")){
            if(forwardWithTrack(movable)){
                nextRotation = 90 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
            else {
                nextRotation = 0 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
        }
        else if(super.getDirection().equals("LEFT")){
            if(forwardWithTrack(movable)){
                nextRotation = 0 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
            else {
                nextRotation = 90 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
        }


        // Set the new point values
        curPoint.x = (int)(midPointX + (radius * (Math.cos(Math.toRadians(nextRotation)))));
        curPoint.y = (int)(midPointY + (radius * (Math.sin(Math.toRadians(nextRotation)))));

        movable.setDegDone(rotationDone + degreesToMove);

        return curRot;
    }


    public boolean checkOnAfterUpdate(Point2D curPoint, double curRot, double degDone, double speed, Movable movable){
        getNextPoint(curPoint, curRot, degDone, speed, movable);

        Point2D p = curPoint;

        if(super.getDirection().equals("LEFT")){//TODO check
            if(forwardWithTrack(movable)){
                if(p.getY() > super.getStartY() + super.getLength()){
                    return false;//No longer in this section
                }

                if(p.getX() < super.getStartX() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }
            else {
                if(p.getY() > super.getStartY() + super.getLength()){
                    return false;//No longer in this section
                }

                if(p.getX() < super.getStartX() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }

        }
        else if(super.getDirection().equals("UP")){
            if(forwardWithTrack(movable)){
                if(p.getX() > super.getStartX() + super.getLength()){
                    return false;//No longer in this section
                }
                if(p.getY() < super.getStartY() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }
            else {
                if(p.getY() > super.getStartY() + super.getLength()){
                    return false;//No longer in this section
                }

                if(p.getX() < super.getStartX() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }
        }
        return true;
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() + super.getLength()/2 && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() + super.getLength()/2 && y <= super.getStartY() + super.getLength();
    }

    public Point2D getConnectionPoint(){
        if(super.getDirection().equals("LEFT")){
            return new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + getLength() - TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("UP")){
            return new Point2D((int)(super.getStartX()+ getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
        }
        return null;
    }

    public Point2D getConnectionPointFrom(){
        if(super.getDirection().equals("LEFT")){
            return new Point2D((int)(super.getStartX()+ getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
        }
        else if(super.getDirection().equals("UP")){
            return new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + getLength() - TRACK_WIDTH/2));
        }
        return null;
    }

    public Point2D getConnectionPointTo(){
        if(super.getDirection().equals("LEFT")){
            return new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + getLength() - TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("UP")){
            return new Point2D((int)(super.getStartX()+ getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
        }
        return null;
    }

    public void setMid(double x, double y){
        setStartX(x - getLength()*0.75);
        setStartY(y - getLength()*0.75);

        setMid();
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());
        if(super.getMouseOn() ){//|| super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }


        // Set The background Color
        g.setFill(DefaultTrack.BACKGROUND_COLOR);

        double degreesToMove = (90/lengthOfQuater()) * SimulationUI.RAIL_SEP*1.5;


        // Set The background Color
        g.setStroke(DefaultTrack.TIE_COLOR);
        for(int deg = 0; deg < 90; deg+=degreesToMove) {
            double sX = (int) (midPointX + TRACK_WIDTH/2 + ((radius+5) * (Math.cos(Math.toRadians(deg)))));
            double sY = (int) (midPointY + TRACK_WIDTH/2 + ((radius+5) * (Math.sin(Math.toRadians(deg)))));

            double eX = (int) (midPointX + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.cos(Math.toRadians(deg)))));
            double eY = (int) (midPointY + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.sin(Math.toRadians(deg)))));

            g.setLineWidth(3);
            g.strokeLine(sX,sY,eX,eY);
        }


        // Set The background Color
        if(super.getSelected()){
            g.setStroke(DefaultTrack.SELECTED_COLOR);
        }
        else {
            g.setStroke(DefaultTrack.RAIL_COLOR);
        }
        g.setLineWidth(2);
        g.strokeArc(getStartX() , getStartY(), getLength(), getLength(), -90, 90, ArcType.OPEN);
        g.strokeArc(getStartX() + TRACK_WIDTH, getStartY()+ TRACK_WIDTH, getLength()- (TRACK_WIDTH*2), getLength()- (TRACK_WIDTH*2), -90, 90, ArcType.OPEN);
    }
}
