package view.Drawable.section_types;


import Util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import view.Drawable.Movable;
import view.SimulationUI;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart2 extends DefaultTrack {

    private double radius; // Radius of the circle
    private double midPointX;
    private double midPointY;

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

    @Override
    public void setStart(DefaultTrack from){
        double startX = 0; double startY = 0;

        if(from.getDirection().equals("RIGHT")){
            super.setDirection("DOWN");
            if(from.getDrawID() == 0){
                startX = from.getStartX() + from.getLength() - super.getLength()/2;
                startY = from.getStartY();
            } else if(from.getDrawID() == 1){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY();
            } else if(from.getDrawID() == 4){
                startX = from.getStartX() + from.getLength()/2 - super.getLength()/2;
                startY = from.getStartY() + from.getLength()/2 + super.getLength()/2 - TRACK_WIDTH;
            }
        }
        else if(from.getDirection().equals("UP")){
            super.setDirection("LEFT");
            if(from.getDrawID() == 5){
                startX = from.getStartX() - super.getLength();
                startY = from.getStartY() - super.getLength()/2;
            } else if(from.getDrawID() == 3){
                startX = from.getStartX();
                startY = from.getStartY();
            } else if(from.getDrawID() == 4){
                startX = from.getStartX() - super.getLength() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2 - super.getLength()/2;
            }
        }
        setStartX(startX);
        setStartY(startY);
        setMid();
    }

    @Override
    public void toggleDirection(){
        setDirection(getDirection().equals("DOWN") ? "LEFT" : "DOWN");
    }

    @Override
    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();
        if(getDirection().equals("DOWN") && (id == 3 || id == 4 || id == 5)){
            if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                    Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
        } else if(getDirection().equals("LEFT") && (id == 0 || id == 1 || id == 4 || id == 6)){
            if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                    Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;

        }
        return false;
    }

    @Override
    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() + super.getLength()/2 && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    @Override
    public Point2D getConnectionPointFrom(){
        return getDirection().equals("DOWN") ? new Point2D((int)(super.getStartX()+ getLength()/2),(int) (getStartY() + TRACK_WIDTH/2)) :
                new Point2D((int)(super.getStartX()+getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
    }

    @Override
    public Point2D getConnectionPointTo(){
        return getDirection().equals("DOWN") ? new Point2D((int)(super.getStartX()+getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2)) :
                new Point2D((int)(super.getStartX()+ getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
    }

    public void setMid(){
        radius = getLength()/2;
        midPointX = getStartX()  + radius - TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;
    }

    @Override
    public void setMid(double x, double y){
        setStartX(x - getLength()*0.75);
        setStartY(y - getLength()/4);
        setMid();
    }

    @Override
    public void draw(GraphicsContext g) {
        g.setStroke(DefaultTrack.TIE_COLOR);
        for(int deg = 270; deg < 360; deg+=(90/ lengthOfQuarter()) * SimulationUI.RAIL_SEP*1.5) {
            double sX = (int) (midPointX + TRACK_WIDTH/2 + ((radius+DefaultTrack.RAIL_OFFSET) * (Math.cos(Math.toRadians(deg)))));
            double sY = (int) (midPointY - TRACK_WIDTH/2 + ((radius+DefaultTrack.RAIL_OFFSET) * (Math.sin(Math.toRadians(deg)))));
            double eX = (int) (midPointX + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-DefaultTrack.RAIL_OFFSET) * (Math.cos(Math.toRadians(deg)))));
            double eY = (int) (midPointY - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-DefaultTrack.RAIL_OFFSET) * (Math.sin(Math.toRadians(deg)))));
            g.setLineWidth(3);
            g.strokeLine(sX,sY,eX,eY);
        }

        g.setStroke(getSelected() ? DefaultTrack.SELECTED_COLOR : DefaultTrack.RAIL_COLOR);
        g.setLineWidth(2);
        g.strokeArc(getStartX(), getStartY(), getLength(), getLength(), 360, 90, ArcType.OPEN);
        g.strokeArc(getStartX() + TRACK_WIDTH, getStartY() + TRACK_WIDTH, getLength() - (TRACK_WIDTH*2), getLength() - (TRACK_WIDTH*2), 360, 90, ArcType.OPEN);
    }

    @Override
    public double getNextPoint(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        setMid();
        // Need to minus the degrees to change
        double degreesToMove = (90/ lengthOfQuarter()/2) * speed;

        double nextRotation = 0;
        if(super.getDirection().equals("LEFT")){
            if(forwardWithTrack(movable)){
                nextRotation = 0 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            } else {
                nextRotation = 270 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
        } else if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
                nextRotation = 270 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            } else {
                nextRotation = 0 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
        }
        // Set the new point values
        curPoint.x = (int)(midPointX + (radius * (Math.cos(Math.toRadians(nextRotation)))));
        curPoint.y = (int)(midPointY + (radius * (Math.sin(Math.toRadians(nextRotation)))));
        movable.setDegDone(rotationDone + degreesToMove);
        return curRot;
    }

    @Override
    public boolean checkOnAfterUpdate(Point2D curPoint, double curRot, double rotationDone, double speed, Movable movable){
        getNextPoint(curPoint, curRot,rotationDone, speed, movable);
        Point2D p = curPoint;

        if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
                if(p.getY() > super.getStartY() + super.getLength()/2){
                    return false;
                }
                if(p.getX() > super.getStartX() + super.getLength()){//Not so important in this case Y matters more
                    return false;
                }
            } else{
                if(p.getY() < super.getStartY() - super.getLength()/2){
                    return false;
                }
                if(p.getX() < super.getStartX() + super.getLength()/2) {// X is important in this case
                    return false;
                }
            }
        } else if(super.getDirection().equals("LEFT")){
            if(forwardWithTrack(movable)){
                if(p.getY() < super.getStartY() - super.getLength()/2){
                    return false;
                }
                if(p.getX() < super.getStartX() + super.getLength()/2) {// X is important in this case
                    return false;
                }
            } else {
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
}
