package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import view.Drawable.DrawableTrain;
import view.Drawable.Movable;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart1 extends DefaultTrack {

    private double radius;
    private double midPointX;
    private double midPointY;

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

        // Train fields once start is known
        setMid();
    }

    public void setMid(){
        radius = getLength()/2;
        midPointX = getStartX()  + radius + TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;
    }



    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());

        if(super.getMouseOn()){// ||super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        g.strokeArc(super.getStartX(), super.getStartY(), super.getLength(), super.getLength(), 90, 90, ArcType.OPEN);
        g.strokeArc(super.getStartX() + TRACK_WIDTH, super.getStartY()+ TRACK_WIDTH, super.getLength() - (TRACK_WIDTH* 2), super.getLength() - (TRACK_WIDTH* 2), 90, 90, ArcType.OPEN);
    }


    public double getNextPoint(Point curPoint,double curRot, double rotationDone, double speed, Movable movable){
        // Need to minus the degrees to change
        double degreesToMove = (90/lengthOfQuater()/2) * speed;

        double nextRotation = 0;
        if(super.getDirection().equals("RIGHT")){
            if(forwardWithTrack(movable)){
                nextRotation = 180 + (degreesToMove + rotationDone) ;// 180
                curRot+= degreesToMove*2;
            }
            else {
                nextRotation = 270 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
        }
        else if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
                nextRotation = 270 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
            else {
                nextRotation = 180 + (degreesToMove + rotationDone) ;// 180
                curRot+= degreesToMove*2;
            }
        }

        // Set the new point values
        curPoint.x = (int)(midPointX + (radius * (Math.cos(Math.toRadians(nextRotation)))));
        curPoint.y = (int)(midPointY + (radius * (Math.sin(Math.toRadians(nextRotation)))));

        movable.setDegDone(rotationDone + degreesToMove);
        return curRot;
    }

    public boolean checkOnAfterUpdate(Point curPoint,double curRot, double rotationDone, double speed, Movable movable){
        getNextPoint(curPoint, curRot, rotationDone, speed, movable);
        Point p = curPoint;

        if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
                if(p.getY() > super.getStartY() + super.getLength()/2){
                    return false;//No longer in this section
                }
                if(p.getX()< super.getStartX() ){
                    return false;//No longer in this section
                }
            }
            else{
                if(p.getX() > super.getStartX() + super.getLength()/2){//TODO
                    return false;//No longer in this section
                }
                if(p.getY() < super.getStartY() - super.getLength()/2){
                    return false;//No longer in this section
                }
            }
        }
        else if(super.getDirection().equals("RIGHT")){
            if(forwardWithTrack(movable)){
                if(p.getY() < super.getStartY()){// + super.getLength()/2
                    return false;//No longer in this section
                }
                if(p.getX() > super.getStartX() + super.getLength()/2 ){//TODO just changed
                    return false;//No longer in this section
                }
            }
            else {
                if(p.getX() < super.getStartX()){//TODO
                    return false;//No longer in this section
                }
                if(p.getY() > super.getStartY() + super.getLength()/2){
                    return false;//No longer in this section
                }
            }
        }
        return true;
    }
}
