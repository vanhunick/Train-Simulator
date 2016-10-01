package simulation.Drawable.tracks;

import util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import simulation.Drawable.Movable;
import simulation.ui.SimulationUI;



/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart1 extends DefaultTrack {

    private double radius; // The radius of the quart
    private double midPointX; // The middle of the track x value
    private double midPointY;// The middle of the track y value

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

    @Override
    public void setStart(DefaultTrack from){
        double startX = 0; double startY = 0;

        if(from.getDirection().equals("UP")){
            super.setDirection("RIGHT");
            if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength() - TRACK_WIDTH;
                startY = from.getStartY();
            } else if(from.getDrawID() == 4){
                startX = from.getStartX();
                startY = from.getStartY()  - super.getLength()/2 + from.getLength()/2;
            } else if(from.getDrawID() == 5){
                startX = from.getStartX() - TRACK_WIDTH;
                startY = from.getStartY()  - super.getLength()/2;
            }
        } else if(from.getDirection().equals("LEFT")){
            super.setDirection("DOWN");
            if(from.getDrawID() == 0){
                startY = from.getStartY();
                startX = from.getStartX() - super.getLength()/2;
            } else if(from.getDrawID() == 2){
                startY = from.getStartY();
                startX = from.getStartX() + from.getLength()/2 - (super.getLength()/2);
            } else if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() + (from.getLength()/2) + super.getLength()/2 - TRACK_WIDTH;
            }
        }
        setStartX(startX);
        setStartY(startY);
        setMid();
    }

    @Override
    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();
        if(getDirection().equals("RIGHT") && (id == 0 || id == 2 || id == 3 || id == 6)){
            if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;

        } else if(getDirection().equals("DOWN") && (id == 3 || id == 4 || id == 5)){
            if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
        }
        return false;
    }

    @Override
    public void toggleDirection(){setDirection(getDirection().equals("RIGHT") ? "DOWN" : "RIGHT");}

    public void setMid(){
        radius = getLength()/2;
        midPointX = getStartX()  + radius + TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;
    }

    @Override
    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 && y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }



    @Override
    public Point2D getConnectionPointFrom(){
        return getDirection().equals("RIGHT") ? new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY() + getLength()/2)) :
                new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
    }

    @Override
    public Point2D getConnectionPointTo(){
        return getDirection().equals("RIGHT") ? new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + TRACK_WIDTH/2)) :
                new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY() + getLength()/2));
    }

    @Override
    public void setMid(double x, double y){
        setStartX(x - getLength()/4);
        setStartY(y - getLength()/4);
        setMid();
    }

    @Override
    public void draw(GraphicsContext g) {
        g.setFill(DefaultTrack.BACKGROUND_COLOR);
        g.setStroke(DefaultTrack.TIE_COLOR);
        for(int deg = 180; deg < 270; deg+=(90/ lengthOfQuarter()) * SimulationUI.RAIL_SEP*1.5) {
            g.setStroke(DefaultTrack.TIE_COLOR);
            g.setLineWidth(3);
            g.strokeLine((int) (midPointX -TRACK_WIDTH/2 + ((radius+DefaultTrack.RAIL_OFFSET) * (Math.cos(Math.toRadians(deg))))),
                    (int) (midPointY  - TRACK_WIDTH/2 + ((radius+DefaultTrack.RAIL_OFFSET) * (Math.sin(Math.toRadians(deg))))),
                    (int) (midPointX - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-DefaultTrack.RAIL_OFFSET) * (Math.cos(Math.toRadians(deg))))),
                    (int) (midPointY - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-DefaultTrack.RAIL_OFFSET) * (Math.sin(Math.toRadians(deg))))));
        }
        g.setLineWidth(2);
        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : getColor());
        g.strokeArc(getStartX(), getStartY(), getLength(), getLength(), 90, 90, ArcType.OPEN);
        g.strokeArc(getStartX() + TRACK_WIDTH, getStartY()+ TRACK_WIDTH, getLength() - (TRACK_WIDTH* 2), getLength() - (TRACK_WIDTH* 2), 90, 90, ArcType.OPEN);
    }

    @Override
    public double getNextPoint(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        double degreesToMove = (90/ lengthOfQuarter()/2) * speed;// Need to minus the degrees to change

        double nextRotation = 0;
        if(super.getDirection().equals("RIGHT")){
            if(forwardWithTrack(movable)){
                nextRotation = 180 + (degreesToMove + rotationDone) ;// 180
                curRot+= degreesToMove*2;
            } else {
                nextRotation = 270 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
        } else if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
                nextRotation = 270 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            } else {
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
    @Override
    public boolean checkOnAfterUpdate(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        Point2D tempPoint = new Point2D(curPoint.x,curPoint.y); // need to copy it because the method modifies it

        getNextPoint(tempPoint, curRot, rotationDone, speed, movable);
        Point2D p = tempPoint;

        if(getDirection().equals("DOWN")){
            if(forwardWithTrack(movable) && (p.getY() > getStartY() + getLength()/2 || p.getX()< getStartX())){
                return false;//No longer in this section
            } else if(!forwardWithTrack(movable) && (p.getX() > getStartX() + getLength()/2 || p.getY() < getStartY() - getLength()/2)){
                return false;
            }
        } else if(getDirection().equals("RIGHT")){
            if(forwardWithTrack(movable) && (p.getY() < getStartY() || p.getX() > getStartX() + getLength()/2 )){
                return false;
            } else if(!forwardWithTrack(movable) && (p.getX() < getStartX() || p.getY() > getStartY() + getLength()/2)){
                return false;
            }
        }
        return true;
    }

    @Override
    public double pixelsLeftAfterMove(Point2D curPoint, double curRot, double rotationDone, double speed, Movable movable) {
        Point2D tempPoint = new Point2D(curPoint.x, curPoint.y); // need to copy it because the method modifies it
        getNextPoint(tempPoint, curRot, rotationDone, speed, movable);
        Point2D p = tempPoint;

        if ((getDirection().equals("RIGHT") && forwardWithTrack(movable)) || (getDirection().equals("DOWN") && !forwardWithTrack(movable))) {
                return (p.x + speed) - (getStartX() + getLength()/2);
        } else {
            return (p.y + speed) - (getStartY() + getLength() / 2);
        }
    }
}
