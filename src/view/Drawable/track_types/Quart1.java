package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart1 extends DefSection {
    private static final int TRACK_WIDTH = 30;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart1(Section section, int length, int drawID){
        super(section,length, drawID);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart1(Section section, int startX, int startY, int length, int drawID, String direction){
        super(section,startX,startY,length,drawID, direction);
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefSection from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("UP")){
            super.setDirection("RIGHT");

            if(from.getDrawID() == 3){
                startX = from.getStartX();
                startY = from.getStartY() + from.getLength() - TRACK_WIDTH;
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
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    public double getNextX(double curX, double moveBy){
        if(super.getDirection().equals("RIGHT")){
            if(curX + moveBy > super.getStartX() + super.getLength()/2 - 20){//

                return -1;//No longer in this section TODO update later
            }
            else{
                return curX + moveBy;
            }
        }
        else if(super.getDirection().equals("DOWN")){
            if(curX - moveBy < super.getStartX() ){
                return -1;//No longer in this section TODO update later
            }
            else{
                return curX - moveBy;
            }
        }

        return -1;
    }

    public double getNextY(double curY, double moveBy){
        if(super.getDirection().equals("DOWN")){
            System.out.println(curY + moveBy  + " " + super.getStartY() + super.getLength()/2);
            if(curY + moveBy > super.getStartY() + super.getLength()/2){
                return -1;//No longer in this section TODO update later
            }
            else{
                return curY + moveBy;
            }
        }
        else if(super.getDirection().equals("RIGHT")){
            if(curY - moveBy < super.getStartY() - super.getLength()/2){
                System.out.println("-1");
                return -1;//No longer in this section TODO update later
            }
            else{
                return curY - moveBy;
            }
        }
        return -1;
    }

    /**
     * USed to put the train in the middle of the track when first drawn
     * */
    public double getInitialY(double trainWidth){
        return super.getStartY() + TRACK_WIDTH/2 - trainWidth/2;
    }

    public boolean checkOnSectionAfterMovement(double curX, double curY, double dist){
        if(getNextX(curX,dist) == -1 )return false;
        if(getNextY(curY,dist) == -1 )return false;// TODO dont need this
        return true;
    }


    public void draw(GraphicsContext g) {
        if(super.getMouseOn()){
            g.setStroke(Color.GREEN);
        }

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();


        g.strokeArc(startX, startY, length, length, 90, 90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH * 2), length - (TRACK_WIDTH * 2), 90, 90, ArcType.OPEN);


        g.fillOval(100,100, 10,10);
        g.setFill(Color.RED);
        for(Point p : getDrawPoints()){
            g.fillOval(p.getX(),p.getY(),5,5);
        }
        g.setFill(Color.WHITE);
    }

    public ArrayList<Point> getDrawPoints(){
        ArrayList<Point> points = new ArrayList<>();

        double angle = 90;
        double radius = ((super.getLength())/2 -  TRACK_WIDTH/2);

        double x = super.getStartX() + super.getLength()/2;
        double y = super.getStartY() + TRACK_WIDTH/2;

        double a = 1.57079632679;
        a=a+angle*Math.PI/180;

        double fx = Math.cos(a);
        double fy = Math.sin(a);

        double lx = -(Math.sin(a));
        double ly = Math.cos(a);


        for(double i = 0; i < 15; i++){
            double subAngle = (i/15)*Math.toRadians(angle);


            double xi = x + radius*(Math.sin(subAngle)*fx + (1-Math.cos(subAngle))*(-lx));
            double yi = y + radius*(Math.sin(subAngle)*fy + (1-Math.cos(subAngle))*(-ly));
            points.add(new Point((int)xi,(int)yi));
        }

        return points;
    }

    public double lengthOfQuater(){
        double radius = (super.getLength()-TRACK_WIDTH/2)/2;
        double circumference = 2 * Math.PI * radius;
        return circumference/4;
    }
}
