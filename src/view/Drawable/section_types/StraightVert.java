package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;
import view.Drawable.Movable;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class StraightVert extends DefaultTrack {

    /**
     * Constructor for a piece that connects to another piece
     * */
    public StraightVert(int length, int drawID, int id){
        super(length, drawID, id);
    }

    /**
     * Constructor for the starting piece
     * */
    public StraightVert(int startX, int startY, int length, int drawID, String direction, int id){
        super(startX,startY,length,drawID,id, direction );
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefaultTrack from){
        double startX = 0;
        double startY = 0;

        //if a vertical section is added the direction from the piece it comes from can only be up or down
        if(from.getDirection().equals("DOWN")){
            super.setDirection("DOWN");

            if(from.getDrawID() == 1){
                startX = from.getStartX() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2;
            }
            else if(from.getDrawID() == 2){
                startX = from.getStartX() + from.getLength();
                startY = from.getStartY() + from.getLength()/2;
            }
            else if(from.getDrawID() == 5){
                startX = from.getStartX();
                startY = from.getStartY() + from.getLength();
            }
        }
        else if(from.getDirection().equals("UP")){
            super.setDirection("UP");

            if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength();
                startY = from.getStartY() + from.getLength()/2 - super.getLength();
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2 - super.getLength();
            }
            else if(from.getDrawID() == 5){
                startX = from.getStartX();
                startY = from.getStartY() - super.getLength();
            }
        }

        super.setStartX(startX);
        super.setStartY(startY);
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + TRACK_WIDTH &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength();
    }

    public boolean checkOnAfterUpdate(Point curPoint, double rotation,double rotDone, double dist, Movable movable){

        if(getNextX(curPoint.getX(),dist, movable.getOrientation(), movable.getDirection()) == -1 )return false;
        if(getNextY(curPoint.getY(),dist, movable.getOrientation()) == -1 )return false;
        return true;
    }

    public double getNextRotation(double curRotation, double speed, boolean nat, boolean forward){
        if(super.getDirection().equals("UP")){
            if(nat)return 0;
            return 180;
        }
        else if(super.getDirection().equals("DOWN")){
            if(nat)return 180;
            return 0;
        }
        // Error
        return 0;
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

    public double getNextY(double curY, double moveBy, boolean nat){
        return curY;
    }


    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());
        if(super.getMouseOn() ){//|| super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();

        g.strokeLine(startX, startY, startX, startY + length);
        g.strokeLine(startX - TRACK_WIDTH, startY, startX - TRACK_WIDTH, startY + length);

        g.setStroke(Color.WHITE);
    }
}
