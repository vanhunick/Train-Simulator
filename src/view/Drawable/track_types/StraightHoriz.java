package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;

/**
 * Created by Nicky on 25/03/2016.
 */
public class StraightHoriz extends DefSection {
    private static final int TRACK_WIDTH = 30;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public StraightHoriz(Section section, int length, int drawID){
        super(section,length, drawID);
   }

    /**
     * Constructor for the starting piece
     * */
    public StraightHoriz(Section section, int startX,  int startY, int length, int drawID, String direction){
        super(section,startX,startY,length,drawID, direction);
    }

    public void setStart(DefSection from){

        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("RIGHT")){
            super.setDirection("RIGHT");
            if(from.getDrawID() == 0){
                System.out.println("Setting x");
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
        }
        else if(from.getDirection().equals("LEFT")){
            super.setDirection("LEFT");
            if(from.getDrawID() == 0){
                startX =from.getStartX() - super.getLength();
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
        }
        super.setStartX(startX);
        super.setStartY(startY);
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength() &&
                y >= super.getStartY() && y <= super.getStartY() + TRACK_WIDTH;
    }

    public double getNextX(double curX, double moveBy){
        if(super.getDirection().equals("RIGHT")){
            if(curX + moveBy > super.getStartX() + super.getLength()){
                return 0;//No longer in this section TODO update later
            }
            else{
                return curX + moveBy;
            }
        }
        else if(super.getDirection().equals("LEFT")){
            if(curX - moveBy < super.getStartX() - super.getLength()){
                return 0;//No longer in this section TODO update later
            }
            else{
                return curX - moveBy;
            }
        }

        return 0;
    }

    public double getNextY(double curY, double moveBy){
        //going straight y never changes on this section
        return curY;
    }




    public void draw(GraphicsContext g) {
        g.setStroke(Color.RED);
        g.strokeLine(super.getStartX(), super.getStartY(), super.getStartX() + super.getLength(), super.getStartY());
        g.strokeLine(super.getStartX(), super.getStartY() + TRACK_WIDTH, super.getStartX() + super.getLength(), super.getStartY()+ TRACK_WIDTH);
    }

}
