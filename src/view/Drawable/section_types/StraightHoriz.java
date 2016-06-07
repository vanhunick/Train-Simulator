package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import view.Drawable.Movable;

import java.awt.*;


/**
 * Created by Nicky on 25/03/2016.
 */
public class StraightHoriz extends DefaultTrack {

    /**
     * Constructor for a piece that connects to another piece
     * */
    public StraightHoriz(int length, int drawID, int id){
        super(length, drawID,id);
   }

    /**
     * Constructor for the starting piece
     * */
    public StraightHoriz(int startX,  int startY, int length, int drawID,int id, String direction){
        super(startX, startY, length, drawID, id, direction);
    }

    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("RIGHT")){
            super.setDirection("RIGHT");
            if(from.getDrawID() == 0){
                startX = from.getStartX() + from.getLength();
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 1){
                startX = from.getStartX() + from.getLength()/2;
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() + from.getLength()/2;
                startY = from.getStartY() + from.getLength() - TRACK_WIDTH;
            }
            else if(from.getDrawID() == 6){
                startX = from.getStartX() + from.getLength();
                startY = from.getStartY();
            }
        }
        else if(from.getDirection().equals("LEFT")){
            super.setDirection("LEFT");
            if(from.getDrawID() == 0){
                startX = from.getStartX() - super.getLength();
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 2){
                startX = from.getStartX() + from.getLength()/2 - super.getLength();
                startY = from.getStartY();
            }
            else if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength()/2 - super.getLength();
                startY = from.getStartY() + from.getLength() - TRACK_WIDTH;
            }
            else if(from.getDrawID() == 6){
                startX = from.getStartX() - super.getLength();
                startY = from.getStartY();
            }
        }
        super.setStartX(startX);
        super.setStartY(startY);
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + TRACK_WIDTH;
    }


    public double getNextPoint(Point cur, double curRot, double rotDone, double moveBy, Movable movable){
        cur.setLocation(getNextX(cur.getX(),moveBy,movable.getOrientation(), movable.getDirection()),getNextY(cur.getY(),moveBy,movable.getOrientation()));
        return getNextRotation(curRot,moveBy,movable.getOrientation(),movable.getDirection());
    }

    public double getNextX(double curX, double moveBy, boolean nat, boolean forward){
        if(super.getDirection().equals("RIGHT")){
            if(nat && forward || !nat && !forward){
                if(curX + moveBy > (super.getStartX() + super.getLength())){
                    return -1;//No longer in this section TODO update later
                }
                else{
                    return curX + moveBy;
                }
            }
            else{
                if(curX - moveBy < super.getStartX()){
                    return -1;//No longer in this section TODO update later
                }
                else{
                    return curX - moveBy;
                }
            }
        }
        else if(super.getDirection().equals("LEFT")){
            if(nat && forward || !nat && !forward){
                if(curX - moveBy < super.getStartX()){
                    return -1;//No longer in this section TODO update later
                }
                else{
                    return curX - moveBy;
                }
            }
            else{
                if(curX + moveBy > (super.getStartX() + super.getLength())){
                    return -1;//No longer in this section TODO update later
                }
                else{
                    return curX + moveBy;
                }
            }
        }
        return -1;
    }


    /**
     * Return the y value in the middle of the track
     * */
    public double getNextY(double curY, double moveBy, boolean nat){
        return curY;
    }

    public double getInitialX(double trainWidth){
        System.out.println("Getting initial");
        return super.getStartX() + super.getLength()/2;//place it in the middle of the track
    }

    public double getNextRotation(double curRotation, double speed, boolean nat, boolean forward){
        if(super.getDirection().equals("RIGHT")){
            if(nat)return 90;
            return 270;
        }
        else if(super.getDirection().equals("LEFT")){
            if(nat)return 270;
            return 90;
        }
        // Error
        return 0;
    }

    /**
     * USed to put the train in the middle of the track when first drawn
     * */
    public double getInitialY(double trainWidth){
        return super.getStartY() + TRACK_WIDTH/2;
    }

    public boolean checkOnAfterUpdate(Point curPoint, double rotation,double rotDone, double dist, Movable movable){

        if(getNextX(curPoint.getX(),dist, movable.getOrientation(), movable.getDirection()) == -1 )return false;
        if(getNextY(curPoint.getY(),dist, movable.getOrientation()) == -1 )return false;
        return true;
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());
        if(super.getMouseOn() ){//|| super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        g.strokeLine(super.getStartX(), super.getStartY(), super.getStartX() + super.getLength(), super.getStartY());
        g.strokeLine(super.getStartX(), super.getStartY() + TRACK_WIDTH, super.getStartX() + super.getLength(), super.getStartY()+ TRACK_WIDTH);
        g.setStroke(Color.WHITE);
    }
}
