package view.Drawable.track_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;

import java.awt.*;

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
        this.getSection().setTrainOn(true);
    }

    public void draw(GraphicsContext g){System.out.println("Should be implemented in subclass");

        if(getSection().getTrainOn()){
            g.setStroke(Color.GREEN);
        }

    }
//
//    public boolean checkOnSectionAfterMovement(double curX, double curY, double dist){
//        System.out.println("Should be implemented in subclass");
//        return false;
//    }

    public boolean checkOnAfterUpdate(Point curPoint,double lastSubAnle, double moveBy){
        System.out.println("Should be implemented in subclass");
        return true;
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

    public Point getNextPoint(Point cur, int lastSubAngle, double moveBy){
        return null;
    }

    public double getNextX(double curX, double moveBy){
        System.out.println("Should be implemented in subclass");
        return 0;
    }

    public double getNextRotation(Point newPoint, double oldX, double oldY){
        System.out.println("Should be implemented in subclass");
        return 90;
    }

    public double getNextRotation(double curRotation, double speed){
        System.out.println("Should be implemented in subclass");
        return 90;
    }

    public double getInitialX(double trainWidth){
        System.out.println("Should be implemented in subclass");
        return 0;
    }


    public double getInitialY(double trainWidth){
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefSection)) return false;

        DefSection that = (DefSection) o;

        if (getMouseOn() != that.getMouseOn()) return false;
        if (Double.compare(that.getStartX(), getStartX()) != 0) return false;
        if (Double.compare(that.getStartY(), getStartY()) != 0) return false;
        if (Double.compare(that.getLength(), getLength()) != 0) return false;
        if (isStartPiece() != that.isStartPiece()) return false;
        if (getDrawID() != that.getDrawID()) return false;
        if (!getSection().equals(that.getSection())) return false;
        return getDirection().equals(that.getDirection());

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (getMouseOn() ? 1 : 0);
        temp = Double.doubleToLongBits(getStartX());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getStartY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLength());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getSection().hashCode();
        result = 31 * result + (isStartPiece() ? 1 : 0);
        result = 31 * result + getDirection().hashCode();
        result = 31 * result + getDrawID();
        return result;
    }
}
