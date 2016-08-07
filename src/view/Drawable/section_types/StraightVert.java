package view.Drawable.section_types;

import Util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import view.Drawable.Movable;
import view.SimulationUI;

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

    @Override
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

        setStartX(startX);
        setStartY(startY);
    }

    @Override
    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("DOWN")){
            if(id == 3 || id == 4 || id == 5){
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

    @Override
    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + TRACK_WIDTH &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength();
    }

    @Override
    public void toggleDirection(){
        setDirection(getDirection().equals("UP") ? "DOWN" : "UP");
    }

    @Override
    public Point2D getConnectionPointFrom(){
        if(super.getDirection().equals("UP")){
            return new Point2D((int)(super.getStartX() + TRACK_WIDTH/2),(int) (getStartY() + getLength()));
        }
        else if(super.getDirection().equals("DOWN")){
            return new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY()));
        }
        return null;
    }

    @Override
    public Point2D getConnectionPointTo(){
        if(super.getDirection().equals("UP")){
            return new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY()));
        }
        else if(super.getDirection().equals("DOWN")){
            return new Point2D((int)(super.getStartX() + TRACK_WIDTH/2),(int) (getStartY() + getLength()));
        }
        return null;
    }

    @Override
    public void setMid(double x, double y){
        setStartX(x + TRACK_WIDTH/2);
        setStartY(y - getLength()/2);
    }

    @Override
    public boolean checkOnAfterUpdate(Point2D curPoint, double rotation,double rotDone, double dist, Movable movable){
        if(getNextY(curPoint.getY(),dist, movable.getOrientation(), movable.getDirection()) == -1 )return false;
        if(getNextX(curPoint.getX(),dist, movable.getOrientation()) == -1 )return false;
        return true;
    }

    @Override
    public double getNextRotation(double curRotation, double speed, boolean nat, boolean forward){
        if(super.getDirection().equals("DOWN")){
            if(nat)return 180;
            return 0;
        }
        else if(super.getDirection().equals("UP")){
            if(nat)return 0;
            return 180;
        }
        // Error
        return 0;
    }

    @Override
    public double getNextPoint(Point2D cur, double curRot, double rotDone, double moveBy, Movable movable){
        cur.setLocation(getNextX(cur.getX(),moveBy,movable.getOrientation()),getNextY(cur.getY(),moveBy,movable.getOrientation(), movable.getDirection()));
        return getNextRotation(curRot,moveBy,movable.getOrientation(),movable.getDirection());
    }


    public double getNextY(double curY, double moveBy, boolean nat, boolean forward){
        if(super.getDirection().equals("DOWN")){
            if(nat && forward || !nat && !forward){
                if(curY + moveBy > super.getStartY() + super.getLength()){
                    return -1;//No longer in this section
                }
                else{
                    return curY + moveBy;
                }
            }
            else{
                if(curY - moveBy < super.getStartY()){
                    return -1;//No longer in this section
                }
                else{
                    return curY - moveBy;
                }
            }
        }
        else if(super.getDirection().equals("UP")){
            if(nat && forward || !nat && !forward){
                if(curY - moveBy < super.getStartY()){
                    return -1;//No longer in this section
                }
                else{
                    return curY - moveBy;
                }
            }
            else{
                if(curY + moveBy > (super.getStartY() + super.getLength())){
                    return -1;//No longer in this section
                }
                else{
                    return curY + moveBy;
                }
            }
        }
        return -1;
    }
    

    public double getNextX(double curX, double moveBy, boolean nat){
        return curX;
    }


    public void draw(GraphicsContext g) {
        g.setStroke(DefaultTrack.TIE_COLOR);
        g.setLineWidth(3);

        double x = super.getStartX() - 5 - TRACK_WIDTH;
        double eX = super.getStartX() + 5;

        for(double y = super.getStartY(); y < super.getStartY() + super.getLength(); y += SimulationUI.RAIL_SEP){
            g.strokeLine(x,y,eX,y);
        }

        g.setStroke(DefaultTrack.RAIL_COLOR);
        g.setLineWidth(2);

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();


        if(super.getSelected()){
            g.setStroke(DefaultTrack.SELECTED_COLOR);
        }
        else {
            g.setStroke(DefaultTrack.RAIL_COLOR);
        }
        g.strokeLine(startX, startY, startX, startY + length);
        g.strokeLine(startX - TRACK_WIDTH, startY, startX - TRACK_WIDTH, startY + length);

        g.setStroke(Color.WHITE);
    }
}
