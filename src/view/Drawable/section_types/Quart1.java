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

    public boolean canConnect(DefaultTrack trackToConnect){
        int id = trackToConnect.getDrawID();

        if(getDirection().equals("RIGHT")){
            if(id == 0 || id == 2 || id == 3){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        else if(getDirection().equals("DOWN")){
            if(id == 3 || id == 4 || id == 5){
                if(Math.abs(getConnectionPointTo().getX() - trackToConnect.getConnectionPointFrom().getX()) < DefaultTrack.CONNECT_SENS &&
                        Math.abs(getConnectionPointTo().getY() - trackToConnect.getConnectionPointFrom().getY()) < DefaultTrack.CONNECT_SENS)return true;
            }
        }
        return false;
    }

    public void toggleDirection(){
        if(getDirection().equals("RIGHT")){
            setDirection("DOWN");
        }
        else {
            setDirection("RIGHT");
        }
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

    public Point2D getConnectionPoint(){
        if(super.getDirection().equals("RIGHT")){
            return new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("DOWN")){
            return new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY() + getLength()/2));
        }
        return null;
    }


    public Point2D getConnectionPointFrom(){
        if(super.getDirection().equals("RIGHT")){
            return new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY() + getLength()/2));
        }
        else if(super.getDirection().equals("DOWN")){
            return new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
        }
        return null;
    }

    public Point2D getConnectionPointTo(){
        if(super.getDirection().equals("RIGHT")){
            return new Point2D((int)(super.getStartX()+getLength()/2),(int) (getStartY() + TRACK_WIDTH/2));
        }
        else if(super.getDirection().equals("DOWN")){
            return new Point2D((int)(super.getStartX()+ TRACK_WIDTH/2),(int) (getStartY() + getLength()/2));
        }
        return null;
    }


    public void setMid(double x, double y){
        setStartX(x - getLength()/4);
        setStartY(y - getLength()/4);

        setMid();
    }

    public void draw(GraphicsContext g) {
        g.setStroke(super.getColor());

        if(super.getMouseOn()){// ||super.getSection().getTrainOn()
            g.setStroke(Color.GREEN);
        }

        g.setFill(DefaultTrack.BACKGROUND_COLOR);

        double degreesToMove = (90/lengthOfQuater()) * SimulationUI.RAIL_SEP*1.5;

        g.setStroke(DefaultTrack.TIE_COLOR);
        for(int deg = 180; deg < 270; deg+=degreesToMove) {
            double sX = (int) (midPointX -TRACK_WIDTH/2 + ((radius+5) * (Math.cos(Math.toRadians(deg)))));
            double sY = (int) (midPointY  - TRACK_WIDTH/2 + ((radius+5) * (Math.sin(Math.toRadians(deg)))));

            double eX = (int) (midPointX - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.cos(Math.toRadians(deg)))));
            double eY = (int) (midPointY - TRACK_WIDTH/2 + ((radius - TRACK_WIDTH-5) * (Math.sin(Math.toRadians(deg)))));

            g.setStroke(DefaultTrack.TIE_COLOR);
            g.setLineWidth(3);
            g.strokeLine(sX,sY,eX,eY);
        }


        g.setLineWidth(2);
        if(super.getSelected()){
            g.setStroke(DefaultTrack.SELECTED_COLOR);
        }
        else {
            g.setStroke(DefaultTrack.RAIL_COLOR);
        }

        g.strokeArc(super.getStartX(), super.getStartY(), super.getLength(), super.getLength(), 90, 90, ArcType.OPEN);
        g.strokeArc(super.getStartX() + TRACK_WIDTH, super.getStartY()+ TRACK_WIDTH, super.getLength() - (TRACK_WIDTH* 2), super.getLength() - (TRACK_WIDTH* 2), 90, 90, ArcType.OPEN);
    }



    @Override
    public double getNextPoint(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
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

    public boolean checkOnAfterUpdate(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        getNextPoint(curPoint, curRot, rotationDone, speed, movable);
        Point2D p = curPoint;

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
