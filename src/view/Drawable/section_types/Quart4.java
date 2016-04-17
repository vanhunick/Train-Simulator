package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart4 extends DefaultTrack {
    private static final int TRACK_WIDTH = 30;


    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart4(int length, int drawID, int id){
        super(length, drawID, id);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart4(int startX, int startY, int length, int drawID, String direction, int id){
        super(startX,startY,length,drawID,id, direction );
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("DOWN")){
            super.setDirection("RIGHT");

            if(from.getDrawID() == 1){
                startX = from.getStartX();
                startY = from.getStartY() + super.getLength()/2 - from.getLength()/2;
            }
            else if(from.getDrawID() == 2){
                startX = from.getStartX() + super.getLength()/2 + from.getLength()/2 - TRACK_WIDTH;
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 5){
                startX = from.getStartX()  - TRACK_WIDTH;//TODO might need to account for different sizes
                startY = from.getStartY() + from.getLength() - super.getLength()/2;
            }
        }
        else if(from.getDirection().equals("LEFT")){
            super.setDirection("UP");
            if(from.getDrawID() == 0){
                startX = from.getStartX() - super.getLength()/2;
                startY = from.getStartY() - super.getLength() + TRACK_WIDTH ;//
            }
            else if(from.getDrawID() == 2){
                startX = from.getStartX() + super.getLength()/2 - from.getLength()/2;
                startY = from.getStartY() - super.getLength()/2 - from.getLength()/2 + TRACK_WIDTH;
            }
            else if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() + from.getLength()/2 - super.getLength()/2;
            }
        }

        super.setStartX(startX);
        super.setStartY(startY);
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 &&
                y >= super.getStartY() + super.getLength()/2 && y <= super.getStartY() + super.getLength();
    }

    public void draw(GraphicsContext g) {
        if(super.getMouseOn() ){//|| super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();


        g.strokeArc(startX, startY, length, length, -90, -90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH * 2), length - (TRACK_WIDTH * 2), -90, -90, ArcType.OPEN);

        g.setStroke(Color.WHITE);
    }

    public double getNextRotation(Point newPoint, double oldX, double oldY){
        double deltaX = newPoint.getX() - oldX;
        double deltaY = newPoint.getY() - oldY;
        double degree = ((Math.atan2(deltaY, deltaX)));
        double angle = degree * 180 / 3.14;

        if(angle<0)
        {
            angle = 360+angle;
        }
        return angle;
    }

    public double getNextRotation(double curRotation, double speed){
        double l = lengthOfQuater();

        double updates = l/speed;

        double rotateCHange = 90/updates;

        if(super.getDirection().equals("UP")){
            return curRotation + rotateCHange;
        }
        else {
            return curRotation - rotateCHange;
        }
    }


    /**
     * Returns the next point to move to on the curve given the amount to move
     * */
    public Point getNextPoint(Point curPoint, int lastSubAngle, double moveBy){
        double lengthOfQauter = lengthOfQuater();
        double points = (int)(lengthOfQauter/moveBy);
        double angle = 90;


        if(super.getDirection().equals("UP")){
            lastSubAngle = (int)points - lastSubAngle;
        }

        double subAngle = (lastSubAngle/points)*Math.toRadians(angle);


        double radius = ((super.getLength())/2 -  TRACK_WIDTH/2);

        double x = super.getStartX() + TRACK_WIDTH/2;
        double y = super.getStartY() + super.getLength()/2;

        double a = 1.57079632679;
        a=a+angle*Math.PI;

        double fx = Math.cos(a);
        double fy = Math.sin(a);

        double lx = -(Math.sin(a));
        double ly = Math.cos(a);


        double xi = x + radius*(Math.sin(subAngle)*fx + (1-Math.cos(subAngle))*(-lx));
        double yi = y + radius*(Math.sin(subAngle)*fy + (1-Math.cos(subAngle))*(-ly));
        return new Point((int)xi,(int)yi);
    }

    //Not tested yet
    public boolean checkOnAfterUpdate(Point curPoint, double lastSubAnle, double moveBy){
        Point p = getNextPoint(curPoint, (int)lastSubAnle, moveBy);

        if(super.getDirection().equals("RIGHT")){
            if(p.getY() > super.getStartY() + super.getLength()){
                return false;
            }
            if(p.getX() > super.getStartX() + super.getLength()/2){
                return false;//No longer in this section
            }
        }
        else if(super.getDirection().equals("UP")){
            if(p.getY() < super.getStartY() + super.getLength()/2){
                return false;//No longer in this section
            }
            if(p.getX() < super.getStartX() ){
                return false;//No longer in this section
            }
        }
        return true;
    }


    public double lengthOfQuater(){
        double radius = (super.getLength()-TRACK_WIDTH/2)/2;
        double circumference = 2 * Math.PI * radius;
        return circumference/4;
    }
}
