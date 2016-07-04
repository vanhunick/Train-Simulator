package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;
import view.Drawable.DrawableTrain;
import view.Drawable.Movable;
import view.SimulationUI;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart2 extends DefaultTrack {

    private double radius = getLength()/2;
    private double midPointX = getStartX()  + radius - TRACK_WIDTH/2;
    private double midPointY = getStartY()  + radius + TRACK_WIDTH/2;

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

        setStartX(startX);
        setStartY(startY);

        radius = getLength()/2;
        midPointX = getStartX()  + radius - TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;
    }

    public void toggleDirection(){
        if(getDirection().equals("DOWN")){
            setDirection("LEFT");
        }
        else {
            setDirection("DOWN");
        }
    }

    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("DOWN")){
            if(id == 3 || id == 4 || id == 5){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        else if(getDirection().equals("LEFT")){
            if(id == 0 || id == 1 || id == 4){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        return false;
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() + super.getLength()/2 && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    public Point getConnectionPoint(){
        if(super.getDirection().equals("DOWN")){
            return new Point((int)(super.getStartX()+getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
        }
        else if(super.getDirection().equals("LEFT")){
            return new Point((int)(super.getStartX()+ getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
        }
        return null;
    }

    public Point getConnectionPointFrom(){
        if(super.getDirection().equals("DOWN")){
            return new Point((int)(super.getStartX()+ getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("LEFT")){
            return new Point((int)(super.getStartX()+getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
        }
        return null;
    }

    public Point getConnectionPointTo(){
        if(super.getDirection().equals("DOWN")){
            return new Point((int)(super.getStartX()+getLength() - TRACK_WIDTH),(int) (getStartY() + getLength()/2));
        }
        else if(super.getDirection().equals("LEFT")){
            return new Point((int)(super.getStartX()+ getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
        }
        return null;
    }


    public void setMid(){
        radius = getLength()/2;
        midPointX = getStartX()  + radius - TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;
    }

    public void setMid(double x, double y){
        setStartX(x - getLength()*0.75);
        setStartY(y - getLength()/4);

        radius = getLength()/2;
        midPointX = getStartX()  + radius - TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());
        if(super.getMouseOn() ){
            g.setStroke(Color.GREEN);
        }

        g.setStroke(DefaultTrack.BACKGROUND_COLOR);
        double degreesToMove = (90/lengthOfQuater()) * SimulationUI.RAIL_SEP*1.5;

            //TODO later still usefull
//        g.setLineWidth(TRACK_WIDTH+20);
//        g.strokeArc(getStartX()+10, super.getStartY()+5, getLength()-20, getLength()-20, 360, 90, ArcType.OPEN);



        g.setStroke(DefaultTrack.TIE_COLOR);
        for(int deg = 270; deg < 360; deg+=degreesToMove) {
            double sX = (int) (midPointX + TRACK_WIDTH/2 + ((radius+5) * (Math.cos(Math.toRadians(deg)))));
            double sY = (int) (midPointY - TRACK_WIDTH/2 + ((radius+5) * (Math.sin(Math.toRadians(deg)))));

            double eX = (int) (midPointX + TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.cos(Math.toRadians(deg)))));
            double eY = (int) (midPointY - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.sin(Math.toRadians(deg)))));


            g.setLineWidth(3);
            g.strokeLine(sX,sY,eX,eY);
        }

        if(super.getSelected()){
            g.setStroke(DefaultTrack.SELECTED_COLOR);
        }
        else {
            g.setStroke(DefaultTrack.RAIL_COLOR);
        }

        g.setLineWidth(2);
        g.strokeArc(getStartX(), super.getStartY(), getLength(), getLength(), 360, 90, ArcType.OPEN);
        g.strokeArc(getStartX() + TRACK_WIDTH, super.getStartY() + TRACK_WIDTH, getLength() - (TRACK_WIDTH*2), getLength() - (TRACK_WIDTH*2), 360, 90, ArcType.OPEN);
    }

    public double getNextPoint(Point curPoint,double curRot, double rotationDone, double speed, Movable movable){
        radius = getLength()/2;
        midPointX = getStartX()  + radius - TRACK_WIDTH/2;
        midPointY = getStartY()  + radius + TRACK_WIDTH/2;

        // Need to minus the degrees to change
        double degreesToMove = (90/lengthOfQuater()/2) * speed;

        double nextRotation = 0;
        if(super.getDirection().equals("LEFT")){
            if(forwardWithTrack(movable)){
                nextRotation = 0 - (degreesToMove + rotationDone) ;
                curRot-= degreesToMove*2;
            }
            else {
                nextRotation = 270 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
        }
        else if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
                nextRotation = 270 + (degreesToMove + rotationDone) ;
                curRot+= degreesToMove*2;
            }
            else {
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

    public boolean checkOnAfterUpdate(Point curPoint, double curRot, double rotationDone, double speed, Movable movable){

        getNextPoint(curPoint, curRot,rotationDone, speed, movable);

        Point p = curPoint;

        if(super.getDirection().equals("DOWN")){
            if(forwardWithTrack(movable)){
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
            if(forwardWithTrack(movable)){
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
}
