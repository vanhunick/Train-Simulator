package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart2 extends DefSection {
    private static final int TRACK_WIDTH = 30;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart2(Section section, int length, int drawID){
        super(section,length, drawID);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart2(Section section, int startX, int startY, int length, int drawID, String direction){
        super(section,startX,startY,length,drawID, direction);
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefSection from){
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
                startY = from.getStartY() - from.getLength()/2;
            }
            else if(from.getDrawID() == 4){
                startX = from.getStartX() - super.getLength() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2 - super.getLength()/2;
            }
        }
        super.setStartX(startX);
        super.setStartY(startY);
    }

    public double getNextX(double curX, double moveBy){
        if(super.getDirection().equals("DOWN")){
            if(curX + moveBy > super.getStartX() + super.getLength() - 20){//
                return -1;//No longer in this section TODO update later
            }
            else{
                System.out.println("Moving x");
                return curX + moveBy;
            }
        }
        else if(super.getDirection().equals("LEFT")){
            if(curX - moveBy < super.getStartX() - super.getLength()){
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
                System.out.println("Lower than the y");
                return -1;//No longer in this section TODO update later
            }
            else{
                return curY + moveBy;
            }
        }
        else if(super.getDirection().equals("LEFT")){
            if(curY - moveBy < super.getStartY() - super.getLength()/2){
                return -1;//No longer in this section TODO update later
            }
            else{
                return curY - moveBy;
            }
        }
        return -1;
    }

    public boolean checkOnSectionAfterMovement(double curX, double curY, double dist){
        if(getNextX(curX,dist) == -1 )return false;
        if(getNextY(curY,dist) == -1 )return false;// TODO dont need this
        return true;
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() + super.getLength()/2 && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + super.getLength()/2;
    }

    public void draw(GraphicsContext g) {
        if(super.getMouseOn()){
            g.setStroke(Color.GREEN);
        }

        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();


        g.strokeArc(startX , startY, length, length, 360, 90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH*2), length - (TRACK_WIDTH*2), 360, 90, ArcType.OPEN);
    }
}
