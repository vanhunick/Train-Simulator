package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;

/**
 * Created by Nicky on 25/03/2016.
 */
public class StraightVert extends DefualtDrawableSection{
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
    public void setStart(DefualtDrawableSection from){
        double startX = 0;
        double startY = 0;

        if(from.getDirection().equals("RIGHT")){
            if(from.getDrawID() == 2){
                   startX = from.getStartX() + from.getLength();
                   startY = from.getStartY() + from.getLength()/2;
            }
            else if(from.getDrawID() == 3){
                startX = from.getStartX() + from.getLength();
                startY = from.getStartY() + from.getLength()/2 - super.getLength();
            }
            else if(from.getDrawID() == 5){
                startX = from.getStartX();
                startY = from.getStartY() + from.getLength()/2;
            }
        }
        else if(from.getDirection().equals("LEFT")){
            if(from.getDrawID() == 1){
                startX = from.getStartX() + TRACK_WIDTH;
                startY = from.getStartY() + from.getLength()/2;
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

    public void draw(GraphicsContext g) {
        double startX = super.getStartX();
        double startY = super.getStartY();
        double length = super.getLength();

        g.setStroke(Color.PURPLE);
        g.strokeLine(startX, startY, startX, startY + length);
        g.strokeLine(startX - TRACK_WIDTH, startY, startX - TRACK_WIDTH, startY + length);
    }
}
