package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

/**
 * Created by Nicky on 25/03/2016.
 */
public class Quart4 extends DefSection {
    private static final int TRACK_WIDTH = 30;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public Quart4(Section section, int length, int drawID){
        super(section,length, drawID);
    }

    /**
     * Constructor for the starting piece
     * */
    public Quart4(Section section, int startX, int startY, int length, int drawID, String direction){
        super(section,startX,startY,length,drawID, direction);
    }

    /**
     * Workds out where to start drawing the piece based on the piece it came from
     * */
    public void setStart(DefSection from){

        if(from.getDirection().equals("RIGHT")){
            if(from.getDrawID() == 0){

            }
            else if(from.getDrawID() == 1){

            }
            else if(from.getDrawID() == 4){

            }
        }
        else if(from.getDirection().equals("LEFT")){
            if(from.getDrawID() == 0){

            }
            else if(from.getDrawID() == 2){

            }
            else if(from.getDrawID() == 3){

            }
        }
    }

    public boolean containsPoint(double x, double y){
        return x >= super.getStartX() && x <= super.getStartX() + super.getLength()/2 &&
                y >= super.getStartY() + super.getLength()/2 && y <= super.getStartY() + super.getLength();
    }


    public void draw(GraphicsContext g) {
        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();

        g.setStroke(Color.GREEN);
        g.strokeArc(startX, startY, length, length, -90, -90, ArcType.OPEN);
        g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH * 2), length - (TRACK_WIDTH * 2), -90, -90, ArcType.OPEN);
    }
}
