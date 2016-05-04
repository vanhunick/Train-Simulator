package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart2 extends DefaultTrack {

    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart2(int length, int drawID, int id){
        super(length, drawID, id);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart2(int startX, int startY, int length, int drawID, String direction, int id){
        super(startX, startY, length, drawID, id, direction);
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("RIGHT")){
            super.setDirection("DOWN");
            if(from.getDrawID() == 0){
                startX = from.getStartX() + from.getLength() - super.getLength()/2;
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 1){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() + from.getLength()/2 + super.getLength()/2 - TRACK_WIDTH;
            }
        }
        else if(from.getDirection().equals("UP")){
            super.setDirection("LEFT");
            if(from.getDrawID() == 5){
                startX = from.getStartX() - super.getLength();
                startY = from.getStartY() - super.getLength()/2;
            }
            else if(from.getDrawID() == 3){
                startX = from.getStartX();
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() - super.getLength() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2 - super.getLength()/2;
            }
        }
        super.setStartX(startX);
        super.setStartY(startY);
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() + super.getLength()/2 && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());
        if(super.getMouseOn() ){//|| super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();

        g.strokeArc(startX , startY, length, length, 360, 90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH*2), length - (TRACK_WIDTH*2), 360, 90, ArcType.OPEN);

        g.setStroke(Color.WHITE);
    }

    public double getNextRotation(double curRotation, double speed, boolean nat, boolean forward){
        double l = lengthOfQuater();

        double updates = l/speed;

        double rotateCHange = 90/updates;

        if((super.getDirection().equals("DOWN") && ((nat && forward || !nat && !forward)))){//  ||
            return curRotation + rotateCHange;
        }
        else if(super.getDirection().equals("LEFT") && (nat && !forward) || (!nat && forward)){
            return curRotation + rotateCHange;
        }
        else {
            return curRotation - rotateCHange;
        }
    }

    /**
     * Returns the next point to move to on the curve given the amount to move
     * */
    public Point getNextPoint(Point cur, int lastSubAngle, double moveBy, boolean nat, boolean forward){
        double lengthOfQauter = lengthOfQuater();
        double points = (int)(lengthOfQauter/moveBy);
        double angle = 90;

        //TODO I think this is where the problem lies
        if((super.getDirection().equals("DOWN") && ((nat && forward || !nat && !forward)))){
            lastSubAngle = (int)points - lastSubAngle;
        }

        if(super.getDirection().equals("LEFT") && (nat && !forward) || (!nat && forward)){
            lastSubAngle = (int)points - lastSubAngle;
        }

        double subAngle = (lastSubAngle/points)*Math.toRadians(angle);


        double radius = ((super.getLength())/2 -  TRACK_WIDTH/2);

        double x = super.getStartX() + super.getLength() - TRACK_WIDTH/2;
        double y = super.getStartY() + super.getLength()/2;

        double a = 1.57079632679;
        a=a+angle*Math.PI/90;

        double fx = Math.cos(a);
        double fy = Math.sin(a);

        double lx = -(Math.sin(a));
        double ly = Math.cos(a);


        double xi = x + radius*(Math.sin(subAngle)*fx + (1-Math.cos(subAngle))*(-lx));
        double yi = y + radius*(Math.sin(subAngle)*fy + (1-Math.cos(subAngle))*(-ly));

        return new Point((int)xi,(int)yi);
    }

    public boolean checkOnAfterUpdate(Point curPoint, double lastSubAnle, double moveBy, boolean nat, boolean forward){
        Point p = getNextPoint(curPoint, (int)lastSubAnle, moveBy, nat, forward);

        if(super.getDirection().equals("DOWN")){
            if(nat && forward || !nat && !forward){
                if(p.getY() > super.getStartY() + super.getLength()/2){
                    return false;
                }
                if(p.getX() > super.getStartX() + super.getLength()){//Not so important in this case Y matters more
                    return false;
                }
            }
            else{
                if(p.getY() < super.getStartY() - super.getLength()/2){
                    return false;
                }
                if(p.getX() < super.getStartX() + super.getLength()/2) {// X is important in this case
                    return false;
                }
            }

        }
        else if(super.getDirection().equals("LEFT")){
            if(nat && forward || !nat && !forward){
                if(p.getY() < super.getStartY() - super.getLength()/2){
                    return false;
                }
                if(p.getX() < super.getStartX() + super.getLength()/2) {// X is important in this case
                    return false;
                }
            }
            else {
                if(p.getY() > super.getStartY() + super.getLength()/2){
                    return false;
                }
                if(p.getX() > super.getStartX() + super.getLength()){//Not so important in this case Y matters more
                    return false;
                }
            }

        }
        return true;
    }


    public int getCurPointAfterSpeedChange(double newSpeed, double oldSpeed, double curPointAlong){
        double points = (int)(lengthOfQuater()/newSpeed);

        double pointsPrev = (int)(lengthOfQuater()/oldSpeed);
        double percentageThrough = curPointAlong/pointsPrev;


        curPointAlong = (int)(points*percentageThrough);

        return (int)curPointAlong;
    }

    public double lengthOfQuater(){
        double radius = (super.getLength()-TRACK_WIDTH/2)/2;
        double circumference = 2 * Math.PI * radius;
        return circumference/4;
    }

    public int getNumberOfPoints(double moveBy){
        return (int)(lengthOfQuater()/moveBy);
    }
}
