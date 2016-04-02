package view.Drawable;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import model.Section;

/**
 * Created by Nicky van Hulst on 22/03/16.
 */
public class DrawableSection implements Drawable{

    private static final int TRACK_WIDTH = 30;
    private double startX;
    private double startY;
    private double length;//TODO might just use the train length
    private Section section;
    private DrawableSection from;
    private boolean startPiece;

    // 0 is straight line 1 to 4 represent the section of a ring
    private int drawID;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public DrawableSection(Section section, int length, int drawID, DrawableSection from){
        this.section = section;
        this.length = length;
        this.from = from;
        this.section = section;
        this.drawID = drawID;
        workoutStartingLocation();
    }

    /**
     * Constructor for the starting piece
     * */
    public DrawableSection(Section section, int startX,  int startY, int length, int drawID){
        this.section = section;
        this.startX = startX;
        this.startY = startY;
        this.length = length;
        this.section = section;
        this.drawID = drawID;
        this.startPiece = true;
    }

    public void workoutStartingLocation(){
        if(drawID == 0){// Straight
            drawStraightSection();
        }
        else if(drawID == 1){
            drawFirstQuater();
        }
        else if(drawID == 2){
            drawSecondQuater();
        }
        else if(drawID == 3){
            drawThirdQuater();
        }
        else if(drawID == 4){
            drawFourthQuater();
        }else if(drawID == 5){
            drawFithQuater();
        }
    }

    public int getDrawID(){return drawID;}


    @Override
    public void draw(GraphicsContext g){
        g.setLineWidth(5);
        g.setStroke(Color.WHITE);
        if(drawID == 0){
            g.strokeLine(startX, startY, startX + length, startY);
            g.strokeLine(startX, startY + TRACK_WIDTH, startX + length, startY + TRACK_WIDTH);
        }
        else if(drawID == 1){
            g.setStroke(Color.RED);
            g.strokeArc(startX, startY, length, length, 90, 90, ArcType.OPEN);
            g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH * 2), length - (TRACK_WIDTH * 2), 90, 90, ArcType.OPEN);
        }
        else if(drawID == 2){
            g.setStroke(Color.BLUE);
            g.strokeArc(startX , startY, length, length, 360, 90, ArcType.OPEN);
            g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH*2), length - (TRACK_WIDTH*2), 360, 90, ArcType.OPEN);
        }
        else if(drawID == 3){
//            g.setStroke(Color.YELLOW);
            g.strokeArc(startX , startY, length, length, -90, 90, ArcType.OPEN);
            g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH*2), length - (TRACK_WIDTH*2), -90, 90, ArcType.OPEN);
        }
        else if(drawID == 4){
            g.setStroke(Color.GREEN);
            g.strokeArc(startX, startY, length, length, -90, -90, ArcType.OPEN);
            g.strokeArc(startX + TRACK_WIDTH, startY + TRACK_WIDTH, length - (TRACK_WIDTH * 2), length - (TRACK_WIDTH * 2), -90, -90, ArcType.OPEN);
        } else if(drawID == 5){//straight down piece
            g.setStroke(Color.PURPLE);
            g.strokeLine(startX, startY, startX, startY + length);
            g.strokeLine(startX - TRACK_WIDTH, startY, startX - TRACK_WIDTH, startY + length);
        }
    }

    public void drawStraightSection(){
        if(from.getDrawID() == 0){// Straight
            startX  = from.getStartX() + from.getLength();
            startY =  from.getStartY();
        }
        else if(from.getDrawID() == 3){
            double half = from.length/2;
            double moveBack = length - half;

            startX  = from.getStartX() - moveBack ;
//            startX  = from.getStartX() - ((from.getLength()/length)*(length));//should be its own length drawing left to right
            startY =  from.getStartY() + from.getLength() - TRACK_WIDTH;
        }
    }

    public void drawFirstQuater(){
        if(from.getDrawID() == 0){// Straight
            startX  = from.getStartX() + from.getLength();
            startY =  from.getStartY() + from.getLength();
        }
        else if(from.getDrawID() == 3){
            startX = from.getStartX() + from.getLength() - TRACK_WIDTH;
            startY = from.getStartY() + (from.getLength()/2) - length/2;
        }
        else if(from.getDrawID() == 4){
            double half = length/2;
            double fromY = from.getStartY() + (from.getLength()/2);

            startX = from.getStartX();
            startY = fromY- half;
        }
    }

    public void drawSecondQuater(){
        if(from.getDrawID() == 0){// Straight
            startX  = from.getStartX() + from.getLength() -  (length/2);
            startY =  from.getStartY();
        }
        else if(from.getDrawID() == 1){
            startY = from.getStartY();
            startX  = from.getStartX() + (from.getLength()/2) -  (length/2);
        }
        else if(from.getDrawID() == 4){
            double half =  (from.getLength()/2);
            double moveBack = half - (length/2);

            startX = from.getStartX() - length + TRACK_WIDTH;
            startY = from.getStartY() + moveBack;
        }
    }

    public void drawThirdQuater(){
        if(from.getDrawID() == 1){
            startX = from.getStartX() + (from.getLength()/2) - length/2;
            startY = from.getStartY() - (length/2) - length/2 + TRACK_WIDTH;
        }
        else if(from.getDrawID() == 4){

        }
        else if(from.getDrawID() == 5){
            startX = from.getStartX() - ((length/from.getLength())*(from.getLength()));
            startY = from.getStartY() + from.getLength() - (length/2);
        }
    }

    public void drawFourthQuater(){
        if(from.getDrawID() == 0){// Straight
            startX  = from.getStartX() - ((length/from.getLength())*(from.getLength()) - length/2);
            startY =  from.getStartY() - length + TRACK_WIDTH;
        }
        else if(from.getDrawID() == 3){//TODO TEST
            double half = length/2;
            double fromX = from.getStartX() + (from.getLength()/2);

            startX = fromX - half;
            startY = from.getStartY() + TRACK_WIDTH;
        }
        else if(from.getDrawID() == 2){
            double moveBack =  (length/2);

            startX = from.getStartX() + (from.getLength()/2)-moveBack;
            startY = from.getStartY() - length + TRACK_WIDTH;
        }
    }

    public void drawFithQuater(){
        if(from.getDrawID() == 2){// Straight
            startX  = from.getStartX() + from.getLength();
            startY =  from.getStartY() + from.getLength()/2;
        }
        else if(from.getDrawID() == 4){

        }
        else if(from.getDrawID() == 2){

        }
    }



    public double getStartX(){return startX;}
    public double getLength(){return length;}
    public double getStartY(){return startY;}

}
