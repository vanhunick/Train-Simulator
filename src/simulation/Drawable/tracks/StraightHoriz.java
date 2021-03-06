package simulation.Drawable.tracks;

import javafx.scene.paint.Color;
import simulation.Simulation;
import util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import simulation.Drawable.Movable;

import simulation.ui.SimulationUI;


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


    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("RIGHT")){
            if(id == 0 || id == 2 || id == 3 || id == 6){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        else if(getDirection().equals("LEFT")){
            if(id == 0 || id == 1 || id == 4 || id == 6){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        return false;
    }

    public void toggleDirection(){
        if(getDirection().equals("RIGHT")){
            setDirection("LEFT");
        }
        else {
            setDirection("RIGHT");
        }
    }


    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + TRACK_WIDTH;
    }


    public double getNextPoint(Point2D cur, double curRot, double rotDone, double moveBy, Movable movable){
        cur.setLocation(getNextX(cur.x,moveBy,movable),getNextY(cur.getY(),moveBy,movable.getOrientation()));
        return getNextRotation(curRot,moveBy,movable.getOrientation(),movable.getDirection());
    }


    public double getNextX(double curX, double moveBy, Movable movable){
        if(getDirection().equals("RIGHT") && forwardWithTrack(movable) || getDirection().equals("LEFT") && !forwardWithTrack(movable)) {
            return (curX + moveBy < getStartX() + getLength() ? curX + moveBy : -1);
        } else{
            return curX - moveBy > getStartX() ? curX - moveBy : -1;
        }
    }



    /**
     * Returns the distance left to move after getting to the end of the track
     * */
    public double pixelsLeftAfterMove(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        if(getDirection().equals("RIGHT") && forwardWithTrack(movable) || getDirection().equals("LEFT") && !forwardWithTrack(movable)) {
            return ((curPoint.x + speed) - (getStartX() + getLength()));
        } else{
            return getStartX() - (curPoint.x - speed);
        }
    }


    /**
     * Return the y value in the middle of the track
     * */
    public double getNextY(double curY, double moveBy, boolean nat){
        return curY;
    }

    public double getInitialX(double trainWidth){
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

    public void setMid(double x, double y){
        setStartX(x - getLength()/2);
        setStartY(y - (TRACK_WIDTH/2));
    }

    /**
     * USed to put the train in the middle of the track when first drawn
     * */
    public double getInitialY(double trainWidth){
        return super.getStartY() + TRACK_WIDTH/2;
    }

    public boolean checkOnAfterUpdate(Point2D curPoint, double rotation,double rotDone, double dist, Movable movable){
        if(getNextX(curPoint.getX(),dist, movable) == -1 ){
            return false;
        }
        return true;
    }


    public Point2D getConnectionPointFrom(){
        if(super.getDirection().equals("RIGHT")){
            return new Point2D((int)(super.getStartX()),(int) (getStartY() + TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("LEFT")){
            return new Point2D((int)(super.getStartX()+ getLength()),(int) (getStartY() + TRACK_WIDTH/2));
        }
        return null;
    }

    public Point2D getConnectionPointTo(){
        if(super.getDirection().equals("RIGHT")){
            return new Point2D((int)(super.getStartX()+ getLength()),(int) (getStartY() + TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("LEFT")){
            return new Point2D((int)(super.getStartX()),(int) (getStartY() + TRACK_WIDTH/2));
        }
        return null;
    }


    @Override
    public double getRailspaceLeft(){
        return ((getLength() - getRailOffSet()) % SimulationUI.RAIL_SEP) - SimulationUI.RAIL_SEP;
    }

    public void draw(GraphicsContext g) {
        g.setStroke(DefaultTrack.TIE_COLOR);
        g.setLineWidth(4);

        double y = super.getStartY() - DefaultTrack.RAIL_OFFSET;
        double ey = super.getStartY() + TRACK_WIDTH + DefaultTrack.RAIL_OFFSET;

        for(double x = getStartX() + getRailOffSet(); x <= getStartX() + getLength(); x+= SimulationUI.RAIL_SEP){
            g.strokeLine(x,y,x,ey);
        }

        double sx = getStartX();
        double sy = getStartY();
        double l = getLength();

        g.setLineWidth(1);
        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeLine(sx, sy-1, sx + l, sy-1);

        g.setStroke(Color.WHITE);
        g.strokeLine(sx, sy, sx + l, sy);

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeLine(sx, sy+1, sx + l, sy+1);

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeLine(sx, sy-1 + TRACK_WIDTH, sx + l, sy-1 +TRACK_WIDTH);

        g.setStroke(Color.WHITE);
        g.strokeLine(sx, sy + TRACK_WIDTH, sx + l, sy + TRACK_WIDTH);

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeLine(sx, sy + 1 + TRACK_WIDTH, sx + l, sy+ 1 + TRACK_WIDTH);
    }
}
