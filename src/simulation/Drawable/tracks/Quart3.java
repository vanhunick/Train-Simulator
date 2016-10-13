package simulation.Drawable.tracks;

import javafx.scene.paint.Color;
import util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

import simulation.Drawable.Movable;
import simulation.ui.SimulationUI;


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
                startY = from.getStartY() - super.getLength() + TRACK_WIDTH;
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 6){// Junction
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
        radius    = getLength()/2;
        midPointX = getStartX() - TRACK_WIDTH/2 + radius;
        midPointY = getStartY() - TRACK_WIDTH/2 + radius;
    }


    // could return the rotation and just modify the point
    public double getNextPoint(Point2D curPoint, double curRot, double rotationDone, double speed, Movable movable){

        // Need to minus the degrees to change
        double degreesToMove = (90/ lengthOfQuarter()/2) * speed;

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
        Point2D tempPoint = new Point2D(curPoint.x,curPoint.y); // need to copy it because the method modifies it

        getNextPoint(tempPoint, curRot, degDone, speed, movable);
        Point2D p = tempPoint;

        if(getDirection().equals("LEFT")){
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

    @Override
    public double pixelsLeftAfterMove(Point2D curPoint, double curRot, double rotationDone, double speed, Movable movable) {
        Point2D tempPoint = new Point2D(curPoint.x, curPoint.y); // need to copy it because the method modifies it
        getNextPoint(tempPoint, curRot, rotationDone, speed, movable);
        Point2D p = tempPoint;

        if ((getDirection().equals("LEFT") && forwardWithTrack(movable)) || (getDirection().equals("UP") && !forwardWithTrack(movable))) {
            return (getStartX() + getLength()/2) - (p.x - speed);
        } else {
            return (getStartY() + getLength() / 2) - (p.y - speed);
        }
    }

    @Override
    public double getRailspaceLeft(){
        return ((lengthOfQuarter() - getRailOffSet()) % SimulationUI.RAIL_SEP) - SimulationUI.RAIL_SEP;
    }

    public void draw(GraphicsContext g) {
        // Set The background Color
        g.setFill(DefaultTrack.BACKGROUND_COLOR);

        double degreesToMove = (90/ lengthOfQuarter()) * SimulationUI.RAIL_SEP*1;

                    double extraDeg = (90/ lengthOfQuarter()) * getRailOffSet();
            //
        // Set The background Color
        g.setStroke(DefaultTrack.TIE_COLOR);
        for(int deg = 0 + (int)extraDeg; deg < 90; deg+=degreesToMove) {
            double sX = (int) (midPointX + TRACK_WIDTH/2 + ((radius+DefaultTrack.RAIL_OFFSET) * (Math.cos(Math.toRadians(deg)))));
            double sY = (int) (midPointY + TRACK_WIDTH/2 + ((radius+DefaultTrack.RAIL_OFFSET) * (Math.sin(Math.toRadians(deg)))));

            double eX = (int) (midPointX + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-DefaultTrack.RAIL_OFFSET) * (Math.cos(Math.toRadians(deg)))));
            double eY = (int) (midPointY + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-DefaultTrack.RAIL_OFFSET) * (Math.sin(Math.toRadians(deg)))));

            g.setLineWidth(4);
            g.strokeLine(sX,sY,eX,eY);
        }

        double sx = getStartX();
        double sy = getStartY();
        double l = getLength();

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.setLineWidth(1);

        g.strokeArc(sx+1 , sy, l, l+1,-90, 90, ArcType.OPEN);

        g.setStroke(DefaultTrack.SPECIAL_GREY);
        g.strokeArc(sx , sy, l, l, -90, 90, ArcType.OPEN);

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeArc(sx-1 , sy, l, l-1, -90, 90, ArcType.OPEN);

        g.strokeArc(sx+1 + TRACK_WIDTH, sy+ TRACK_WIDTH, l- (TRACK_WIDTH*2), l- (TRACK_WIDTH*2) +1, -90, 90, ArcType.OPEN);

        g.setStroke(DefaultTrack.SPECIAL_GREY);
        g.strokeArc(sx + TRACK_WIDTH, sy+ TRACK_WIDTH, l- (TRACK_WIDTH*2), l- (TRACK_WIDTH*2), -90, 90, ArcType.OPEN);

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeArc(sx-1 + TRACK_WIDTH, sy+ TRACK_WIDTH, l- (TRACK_WIDTH*2), l- (TRACK_WIDTH*2) -1, -90, 90, ArcType.OPEN);
    }
}
