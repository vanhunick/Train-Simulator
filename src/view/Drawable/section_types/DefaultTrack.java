package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

/**
 * Created by Nicky on 25/03/2016.
 */
public abstract class DefaultTrack {
    private boolean mouseOn;

    public static final int TRACK_WIDTH = 10;
    private double startX;
    private double startY;
    private double length;
    private boolean startPiece;
    private String direction;

    private int to;
    private int from;

    // 0 is straight line 1 to 4 represent the section of a ring 5 is down straight piece
    private int drawID;
    private int id;


    /**
     * Constructor for a piece that connects to another piece
     * */
    public DefaultTrack(int length, int drawID, int id){
        this.length = length;
        this.drawID = drawID;
        this.id = id;
    }

    /**
     * Constructor for the starting piece
     * */
    public DefaultTrack(int startX, int startY, int length, int drawID,int id, String direction){
        this.startX = startX;
        this.startY = startY;
        this.length = length;
        this.drawID = drawID;
        this.id = id;
        this.startPiece = true;
        this.direction = direction;
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

    public void setLength(double length){this.length = length;}

    public int getTo(){return this.to;}

    public void setTo(int to){this.to = to;}

    public int getId(){return this.id;}

    public Point getNextPoint(Point cur, int lastSubAngle, double moveBy, boolean nat){System.out.println("Should be implemented in subclass");return null;}

    public void draw(GraphicsContext g){
        System.out.println("Should be implemented in subclass");
    }

    public boolean checkOnAfterUpdate(Point curPoint,double lastSubAnle, double moveBy, boolean nat){System.out.println("Should be implemented in subclass");return true;}

    public void setStart(DefaultTrack from){System.out.println("Should be implemented in subclass");}

    public void setFrom(int from){this.from = from;}

    public double getNextRotation(double curRotation, double speed){System.out.println("Should be implemented in subclass");return 90;}

    public double getInitialX(double trainWidth){System.out.println("Should be implemented in subclass");return 0;}

    public double getInitialY(double trainWidth){System.out.println("Should be implemented in subclass"); return 0;}

    public boolean containsPoint(double x, double y){System.out.println("Should be implemented in subclass");return false;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DefaultTrack)) return false;

        DefaultTrack that = (DefaultTrack) o;

        if (getMouseOn() != that.getMouseOn()) return false;
        if (Double.compare(that.getStartX(), getStartX()) != 0) return false;
        if (Double.compare(that.getStartY(), getStartY()) != 0) return false;
        if (Double.compare(that.getLength(), getLength()) != 0) return false;
        if (isStartPiece() != that.isStartPiece()) return false;
        if (getDrawID() != that.getDrawID()) return false;
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
        result = 31 * result + (isStartPiece() ? 1 : 0);
        result = 31 * result + getDirection().hashCode();
        result = 31 * result + getDrawID();
        return result;
    }
}
