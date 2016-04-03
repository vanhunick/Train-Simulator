package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;

/**
 * Created by Nicky on 25/03/2016.
 */
public abstract class DefSection {
    private boolean mouseOn;

    public static final int TRACK_WIDTH = 30;
    private double startX;
    private double startY;
    private double length;//TODO might just use the train length
    private Section section;
    private DefSection from;
    private boolean startPiece;
    private String direction;


    // 0 is straight line 1 to 4 represent the section of a ring 5 is down straight piece
    private int drawID;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public DefSection(Section section, int length, int drawID){
        this.section = section;
        this.length = length;
        this.from = from;
        this.section = section;
        this.drawID = drawID;

    }
    /**
     * Constructor for the starting piece
     * */
    public DefSection(Section section, int startX, int startY, int length, int drawID, String direction){
        this.section = section;
        this.startX = startX;
        this.startY = startY;
        this.length = length;
        this.section = section;
        this.drawID = drawID;
        this.startPiece = true;
        this.direction = direction;
    }

    public void draw(GraphicsContext g){


    }


    public void setStart(DefSection from){
        System.out.println("Should be implemented in subclass");
    }

    public int getDrawID() {
        return drawID;
    }


    public String getDirection() {
        return direction;
    }

    public boolean isStartPiece() {
        return startPiece;
    }

    public DefSection getFrom() {
        return from;
    }

    public Section getSection() {
        return section;
    }

    public double getLength() {
        return length;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public void setDirection(String direction){this.direction = direction;}

    public boolean getMouseOn(){return  this.mouseOn;}

    public void setMouseOn(boolean on){
        this.mouseOn = on;
    }

    public double getNextX(double curX, double moveBy){
        System.out.println("Should be implemented in subclass");

        return 0;
    }

    public boolean containsPoint(double x, double y){
        return false;
    }

    public double getNextY(double curY, double moveBy){
        System.out.println("Should be implemented in subclass");

        return 0;
    }
}
