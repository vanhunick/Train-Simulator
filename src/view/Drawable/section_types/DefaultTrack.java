package view.Drawable.section_types;


import Util.Point2D;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.paint.Color;
import view.Drawable.Movable;



/**
 * Created by Nicky on 25/03/2016.
 */
public abstract class DefaultTrack {
    private boolean mouseOn;
    private Color color = Color.WHITE;

    public static final Color TIE_COLOR = new Color(0.5,0.29,0,1);
    public static final Color RAIL_COLOR = new Color(0.5,0.5,0.5,1);
    public static final Color BACKGROUND_COLOR = new Color(0.8,0.8,0.8,1);
    public static Color SELECTED_COLOR = new Color(0.9,0.0,0.0,1);

    public static final int CONNECT_SENS = 10;

    public static final int TRACK_WIDTH = 10;
    private double startX;
    private double startY;
    private double length;
    private boolean startPiece;
    private String direction;

    private int to;
    private int from;

    private int juncFrom;
    private int juncTo;

    // 0 is straight line 1 to 4 represent the section of a ring 5 is down straight piece
    private int drawID;
    private int id;

    private boolean selected;

    public static double STATIC_FRICTRION = 0.7;
    public static double  KINETIC_FRICTION = 0.5;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public DefaultTrack(int length, int drawID, int id){
        this.length = length;
        this.drawID = drawID;
        this.id = id;
        this.juncFrom = -1;
        this.juncTo = -1;
    }

    public boolean canConnect(DefaultTrack track){
        System.out.println("Can connect should be implemented in subclass");
        return false;
    }

    public Point2D getConnectionPointFrom(){
        System.out.println("point null should be implemented in subclass");
        return null;
    }

    public void toggleDirection(){
        System.out.println("toggle direction should be implemented in subclass");
    }

    public Point2D getConnectionPointTo(){
        System.out.println("point from should be implemented in subclass");
        return null;
    }

    public void setMid(double x, double y){
        System.out.println("set mid should be implemented in subclass");
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
    }

    public Point2D getConnectionPoint(){
        System.out.println("Should be implemented in subclass getConnectionPoint");
        return null;
    }

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public boolean getSelected(){
        return this.selected;
    }

    public Color getColor(){
        return this.color;
    }

    public void setColor(Color color){
        this.color = color;
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

    public int getFrom(){return this.from;}

    public int getId(){return this.id;}

    public void draw(GraphicsContext g){
        System.out.println("Should be implemented in subclass");
    }

    public boolean checkOnAfterUpdate(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){System.out.println("Should be implemented in subclass checkOnAdterUpate");return true;}

    public double getNextPoint(Point2D curPoint,double curRot, double rotationDone, double speed, Movable movable){
        System.out.println("Should be implemented in subclass getNextPoint");
        return 0;
    }

    public void setStart(DefaultTrack from){System.out.println("Should be implemented in subclass");}

    public void setFrom(int from){this.from = from;}

    public double getNextRotation(double curRotation, double speed, boolean nat, boolean direction){System.out.println("Should be implemented in subclass");return 90;}

    public double getInitialX(double trainWidth){System.out.println("Should be implemented in subclass");return 0;}

    public double getInitialY(double trainWidth){System.out.println("Should be implemented in subclass"); return 0;}

    public boolean containsPoint(double x, double y){System.out.println("Should be implemented in subclass");return false;}

    public int getJuncFrom(){
        return this.juncFrom;
    }

    public int getJuncTo(){
        return this.juncTo;
    }

    public void setJuncTo(int juncTo){
        this.juncTo = juncTo;
    }

    public void setJuncFrom(int juncFrom){
        this.juncFrom = juncFrom;
    }

    public double lengthOfQuater(){
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
