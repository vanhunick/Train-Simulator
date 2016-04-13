package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public class StraightVert extends DefSection {
    private static final int TRACK_WIDTH = 30;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public StraightVert(Section section, int length, int drawID){
        super(section,length, drawID);
    }

    /**
     * Constructor for the starting piece
     * */
    public StraightVert(Section section, int startX, int startY, int length, int drawID, String direction){
        super(section,startX,startY,length,drawID, direction);
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefSection from){
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

    public double getNextRotation(Point newPoint, double oldX, double oldY){
        if(super.getDirection().equals("UP")){
            return 0;
        }
        else if(super.getDirection().equals("DOWN")){
            return 180;
        }
        // Error
        return 0;
    }


    public double getNextRotation(double curRotation, double speed){
        if(super.getDirection().equals("UP")){
            return 0;
        }
        else if(super.getDirection().equals("DOWN")){
            return 180;
        }
        // Error
        return 0;
    }


    public Point getNextPoint(Point cur, int lastSubAngle, double moveBy){
        cur.setLocation(getNextX(cur.getX(),moveBy),getNextY(cur.getY(),moveBy));
        return cur;
    }

    public double getNextX(double curX, double moveBy){
        //Going down or up never changes x value
        return curX;
    }

    public double getNextY(double curY, double moveBy){
        if(super.getDirection().equals("DOWN")){
            if(curY + moveBy > super.getStartY() + super.getLength()){
                return -1;//No longer in this section TODO update later
            }
            else{
                return curY + moveBy;
            }
        }
        else if(super.getDirection().equals("UP")){
            if(curY - moveBy < super.getStartY()){// 40 being the train size
                return -1;//No longer in this section TODO update later
            }
            else{
                return curY - moveBy;
            }
        }
        return -1;
    }

    public boolean checkOnAfterUpdate(Point curPoint, double lastSubAnle, double dist){
        if(getNextX(curPoint.getX(),dist) == -1 )return false;
        if(getNextY(curPoint.getY(),dist) == -1 )return false;
        return true;
    }



    public void draw(GraphicsContext g) {
        if(super.getMouseOn() || super.getSection().getTrainOn()){
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
