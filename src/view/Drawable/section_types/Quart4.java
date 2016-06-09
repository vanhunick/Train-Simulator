package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import view.Drawable.Movable;
import view.SimulationUI;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart4 extends DefaultTrack {

    private double radius = getLength()/2;
    private double midPointX = getStartX() + radius + TRACK_WIDTH/2;
    private double midPointY = getStartY() + radius - TRACK_WIDTH/2;


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
        super(startX, startY, length, drawID, id, direction);
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
            else if(from.getDrawID() == 6){
                startX = from.getStartX() - super.getLength()/2;
                startY = from.getStartY() - super.getLength() + TRACK_WIDTH;
            }
        }
        setStartX(startX);
        setStartY(startY);

        radius = getLength()/2;
        midPointX = getStartX() + radius + TRACK_WIDTH/2;
        midPointY = getStartY() + radius - TRACK_WIDTH/2;
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 &&
                y >= super.getStartY() + super.getLength()/2 && y <= super.getStartY() + super.getLength();
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());
        if(super.getMouseOn() ){
            g.setStroke(Color.GREEN);
        }

        // Set The background Color
        g.setFill(DefaultTrack.BACKGROUND_COLOR);

        double degreesToMove = (90/lengthOfQuater()) * SimulationUI.RAIL_SEP*1.5;

        g.setStroke(DefaultTrack.RAIL_COLOR);
        for(int deg = 90; deg < 180; deg+=degreesToMove) {

            double sX = (int) (midPointX - TRACK_WIDTH/2 + ((radius+5) * (Math.cos(Math.toRadians(deg)))));
            double sY = (int) (midPointY + TRACK_WIDTH/2 + ((radius+5) * (Math.sin(Math.toRadians(deg)))));

            double eX = (int) (midPointX - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.cos(Math.toRadians(deg)))));
            double eY = (int) (midPointY + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.sin(Math.toRadians(deg)))));

            g.setLineWidth(3);
            g.strokeLine(sX,sY,eX,eY);
        }

        g.setStroke(DefaultTrack.RAIL_COLOR);
        g.setLineWidth(2);

        g.strokeArc(getStartX(), getStartY(), getLength(), getLength(), -90, -90, ArcType.OPEN);
        g.strokeArc(getStartX() + TRACK_WIDTH, getStartY() + TRACK_WIDTH, getLength() - (TRACK_WIDTH * 2), getLength() - (TRACK_WIDTH * 2), -90, -90, ArcType.OPEN);
    }


    public double getNextPoint(Point curPoint,double curRot, double rotationDone, double speed, Movable movable){
        radius = getLength()/2;
        midPointX = getStartX() + radius + TRACK_WIDTH/2;
        midPointY = getStartY() + radius - TRACK_WIDTH/2;

        // Need to minus the degrees to change
        double degreesToMove = (90/lengthOfQuater()/2) * speed;

        double nextRotation = 0;
        if(super.getDirection().equals("RIGHT")){
            if(forwardWithTrack(movable)){
                nextRotation = 180 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
            else {
                nextRotation = 90 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
        }
        else if(super.getDirection().equals("UP")){
            if(forwardWithTrack(movable)){
                nextRotation = 90 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
            else {
                nextRotation = 180 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
        }


        // Set the new point values
        curPoint.x = (int)(midPointX + (radius * (Math.cos(Math.toRadians(nextRotation)))));
        curPoint.y = (int)(midPointY + (radius * (Math.sin(Math.toRadians(nextRotation)))));

        movable.setDegDone(rotationDone + degreesToMove);

        return curRot;
    }


    public boolean checkOnAfterUpdate(Point curPoint,double curRot, double rotationDone, double speed, Movable movable){
        getNextPoint(curPoint, curRot,rotationDone, speed, movable);
        Point p = curPoint;

        if(super.getDirection().equals("RIGHT")){
            if(forwardWithTrack(movable)){
                if(p.getY() > super.getStartY() + super.getLength()){
                    return false;
                }
                if(p.getX() > super.getStartX() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }
            else {
                if(p.getY() < super.getStartY() + super.getLength()/2){
                    return false;//No longer in this section
                }
                if(p.getX() < super.getStartX() ){
                    return false;//No longer in this section
                }
            }
        }
        else if(super.getDirection().equals("UP")){
            if(forwardWithTrack(movable)){
                if(p.getY() < super.getStartY() + super.getLength()/2){
                    return false;//No longer in this section
                }
                if(p.getX() < super.getStartX() ){
                    return false;//No longer in this section
                }
            }
            else{
                if(p.getY() > super.getStartY() + super.getLength()){
                    return false;
                }
                if(p.getX() > super.getStartX() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }
        }
        return true;
    }
}
