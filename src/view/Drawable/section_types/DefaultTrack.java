package view.Drawable.section_types;

import Util.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import view.Drawable.Movable;

/**
 * Created by Nicky on 25/03/2016.
 */
public abstract class DefaultTrack {
    private Color color = Color.WHITE;

    public static final double STATIC_FRICTION = 0.7;
    public static final double  KINETIC_FRICTION = 0.5;
    public static final Color TIE_COLOR = new Color(0.5,0.29,0,1);
    public static final Color RAIL_COLOR = new Color(0.5,0.5,0.5,1);
    public static final Color BACKGROUND_COLOR = new Color(0.8,0.8,0.8,1);
    public static Color SELECTED_COLOR = new Color(0.9,0.0,0.0,1);
    public static final int CONNECT_SENS = 10;
    public static final int TRACK_WIDTH = 10;
    public static final int RAIL_OFFSET = 2;

    private double startX; // Start x position
    private double startY; // Start y position
    private double length; // The length of the track
    private boolean startPiece; // If it is a start piece or not
    private String direction; // The natural direction of the track

    private int to = -1; // The index of the track it leads to
    private int from = -1; // The index of the track it comes from

    private int juncFrom; // The index of the junction track it could come from
    private int juncTo; // The index of the junction track it could go to

    private int drawID; // 0 is straight line 1 to 4 represent the section of a ring 5 is down straight piece
    private int id; // The ID of the track

    private boolean selected; // Wether the track is selected or not


    /**
     * Constructor for a piece that connects to another piece
     * */
    public DefaultTrack(int length, int drawID, int id){
        this.length = length;
        this.drawID = drawID;
        this.id = id;
        this.juncFrom = -1;
        this.juncTo = -1;
        this.to = -1;
        this.from = - 1;
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
        this.juncFrom = -1;
        this.juncTo = -1;
        this.from = -1;
    }

    /***
     * Returns the length of a quarter track
     * */
    public double lengthOfQuarter(){
        double radius = (getLength()-TRACK_WIDTH/2)/2;
        double circumference = 2 * Math.PI * radius;
        return circumference/4;
    }

    /**
     * Returns if the train is going along with the natural orientation of the track
     *
     * @param t train to check
     * */
    public boolean forwardWithTrack(Movable t){
        return t.getOrientation() && t.getDirection() || !t.getOrientation() && !t.getDirection();
    }

    // Setters
    public void setSelected(boolean selected){this.selected = selected;}
    public void setStartY(double startY) {
        this.startY = startY;
    }
    public void setStartX(double startX) {
        this.startX = startX;
    }
    public void setDirection(String direction){this.direction = direction;}
    public void setColor(Color color){
        this.color = color;
    }

    public void setLength(double length){this.length = length;}
    public void setTo(int to){this.to = to;}
    public void setFrom(int from){this.from = from;}
    public void setJuncTo(int juncTo){
        this.juncTo = juncTo;
    }
    public void setJuncFrom(int juncFrom){
        this.juncFrom = juncFrom;
    }
    public abstract void setStart(DefaultTrack from);

    // Getters
    public boolean getSelected(){
        return this.selected;
    }
    public Color getColor(){
        return this.color;
    }
    public int getDrawID() {
        return drawID;
    }
    public String getDirection() {
        return direction;
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
    public int getTo(){return this.to;}
    public int getFrom(){return this.from;}
    public int getId(){return this.id;}
    public int getJuncFrom(){
        return this.juncFrom;
    }
    public int getJuncTo(){
        return this.juncTo;
    }
    public double getInitialX(double trainWidth){System.out.println("Should be implemented in subclass");return 0;}
    public double getInitialY(double trainWidth){System.out.println("Should be implemented in subclass"); return 0;}

    public void setMid(){

    }

    // Abstract Methods
    public abstract boolean canConnect(DefaultTrack track);
    public abstract Point2D getConnectionPointFrom();
    public abstract void toggleDirection();
    public abstract Point2D getConnectionPointTo();
    public abstract void setMid(double x, double y);
    public abstract void draw(GraphicsContext g);
    public abstract boolean containsPoint(double x, double y);

    public boolean checkOnAfterUpdate(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        System.out.println("Should be implemented in subclass checkOnAfterUpdate");
        return false;
    }

    public double getNextRotation(double curRotation, double speed, boolean nat, boolean direction){
        System.out.println("Should be implemented in subclass getNextRotation");
        return 0;
    }

    public double getNextPoint(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        System.out.println("Should be implemented in subclass getNextPoint");
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultTrack that = (DefaultTrack) o;


        if (Double.compare(that.startX, startX) != 0) return false;
        if (Double.compare(that.startY, startY) != 0) return false;
        if (Double.compare(that.length, length) != 0) return false;
        if (startPiece != that.startPiece) return false;
        if (to != that.to) return false;
        if (from != that.from) return false;
        if (juncFrom != that.juncFrom) return false;
        if (juncTo != that.juncTo) return false;
        if (drawID != that.drawID) return false;
        if (id != that.id) return false;
        if (selected != that.selected) return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        return direction != null ? direction.equals(that.direction) : that.direction == null;

    }

    @Override
    public int hashCode() {
        int result = 0;
        long temp;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        temp = Double.doubleToLongBits(startX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(startY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(length);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (startPiece ? 1 : 0);
        result = 31 * result + (direction != null ? direction.hashCode() : 0);
        result = 31 * result + to;
        result = 31 * result + from;
        result = 31 * result + juncFrom;
        result = 31 * result + juncTo;
        result = 31 * result + drawID;
        result = 31 * result + id;
        result = 31 * result + (selected ? 1 : 0);
        return result;
    }
}