package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart1 extends DefaultTrack {

    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart1(int length, int drawID, int id){
        super(length, drawID, id);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart1(int startX, int startY, int length, int drawID, String direction, int id){
        super(startX,startY,length,drawID,id, direction );
    }

    /**
     * Works out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("UP")){
            super.setDirection("RIGHT");

            if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength() - TRACK_WIDTH;
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX();
                startY = from.getStartY()  - super.getLength()/2 + from.getLength()/2;
            }
            else if(from.getDrawID() == 5){
                startX = from.getStartX() - TRACK_WIDTH;
                startY = from.getStartY()  - super.getLength()/2;
            }
        }
        else if(from.getDirection().equals("LEFT")){
            super.setDirection("DOWN");
            if(from.getDrawID() == 0){
                startY = from.getStartY();
                startX = from.getStartX() - super.getLength()/2;
            }
            else if(from.getDrawID() == 2){
                startY = from.getStartY();
                startX = from.getStartX() + from.getLength()/2 - (super.getLength()/2);
            }
            else if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() + (from.getLength()/2) + super.getLength()/2 - TRACK_WIDTH;
            }
        }

        setStartX(startX);
        setStartY(startY);
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    public void draw(GraphicsContext g) {
        if(super.getMouseOn()){// ||super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();

        g.strokeArc(startX, startY, length, length, 90, 90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH* 2), length - (TRACK_WIDTH* 2), 90, 90, ArcType.OPEN);

        g.setStroke(Color.WHITE);
    }

    public double getNextRotation(double curRotation, double speed, boolean nat){
        double l = lengthOfQuater();

        double updates = l/speed;

        double rotateCHange = 90/updates;

        if(super.getDirection().equals("RIGHT") || !nat){
            return curRotation + rotateCHange;
        }
        else {
            return curRotation - rotateCHange;
        }
    }


    /**
     * Returns the next point to move to on the curve given the amount to move
     * */
    public Point getNextPoint(Point cur, int lastSubAngle, double moveBy, boolean nat){
        double lengthOfQauter = lengthOfQuater();
        double points = (int)(lengthOfQauter/moveBy);
        double angle = 90;


        if(super.getDirection().equals("RIGHT") || !nat){
            lastSubAngle = (int)points - lastSubAngle;
        }

        double subAngle = (lastSubAngle/points)*Math.toRadians(angle);

        double radius = ((super.getLength())/2 - TRACK_WIDTH/2);

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


    public boolean checkOnAfterUpdate(Point curPoint,double lastSubAnle, double moveBy, boolean nat){
        Point p = getNextPoint(curPoint, (int)lastSubAnle, moveBy, nat);

        if(super.getDirection().equals("DOWN") && nat){
            if(p.getY() > super.getStartY() + super.getLength()/2){
                return false;//No longer in this section
            }
            if(p.getX()< super.getStartX() ){
                return false;//No longer in this section
            }
        }
        else if(super.getDirection().equals("RIGHT") || !nat){
            if(p.getX() > super.getStartX() + super.getLength()/2){//TODO
                return false;//No longer in this section
            }
            if(p.getY() < super.getStartY() - super.getLength()/2){
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
