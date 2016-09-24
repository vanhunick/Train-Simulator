package simulation.Drawable;

import util.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import simulation.model.RollingStock;
import simulation.Drawable.tracks.DefaultTrack;
import simulation.Drawable.tracks.JunctionTrack;
import simulation.Simulation;


/**
 * Created by vanhunick on 21/04/16.
 */
public class DrawableRollingStock implements Movable{

    // The rolling stock it represents
    private RollingStock rollingStock;

    // Connection can be null
    private Movable connectedToMovable;
    private DrawableRollingStock conToThis;// Rolling stock we are pulling or pushing

    // Image drawing fields
    private SnapshotParameters params; // Drawing parameters
    private ImageView trainImageView; // The image simulation of the rolling stock
    private Image rollingStockImage; // The image of the rolling stock
    private double curRotation;// How far the image should be rotated

    // Rolling stock state information
    private boolean connected; // Connected to a train or not
    private boolean isCrashed; // Currently crashed or not
    private boolean direction; // Forward or backwards direction
    private boolean orientation;
    private double currentSpeed; // The current speed of the stock
    private Circle frontConnection; // Circle representing front where a connection with a stock or train can be make
    private Circle backConnection; // Circle representing back where a connection with a stock or train can be make
    private DefaultTrack juncTrack; // The junction track is is on if it is on one
    private DefaultTrack curTrack; // The current track it is on
    private Point2D currentLocation;// The location of the rolling stock
    private double degDone = 0; // The degrees through the curve


    /**
     * Creates a new drawable rolling stock object
     *
     * @param rollingStock the rolling stock to represent
     *
     * @param connectedToMovable the train it is connected to
     *
     * @param direction the direction it is traveling
     * */
    public DrawableRollingStock(RollingStock rollingStock, Movable connectedToMovable, boolean direction, boolean orientation){
        this.rollingStock = rollingStock;
        this.connectedToMovable = connectedToMovable;
        this.direction = direction;
        this.orientation = orientation;
        this.curRotation = 90;
        this.frontConnection = new Circle();
        this.backConnection = new Circle();

        if(connectedToMovable != null){
            connected = true;
        }
    }

    public double getRollingStocConnectedkWeight(){
        if(conToThis != null){
            return rollingStock.getWeight() + conToThis.getRollingStocConnectedkWeight();
        }
        return rollingStock.getWeight();
    }

    /**
     * Sets up the image and image params for the rolling stock
     * */
    public void setUpImage(){
        this.rollingStockImage = new Image("file:src/res/rolling_stock.gif", rollingStock.getWidth() * Simulation.METER_MULTIPLIER, rollingStock.getLength() * Simulation.METER_MULTIPLIER, false, false);
        this.trainImageView = new ImageView(rollingStockImage);
        this.params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }


    /**
     * Used when rolling stock is started on a track not connected to a train.
     * places the rolling stock on the track
     *
     * @param startingTrack the track to place the stock on
     * */
    public void setStartNotConnected(DefaultTrack startingTrack){
        this.curTrack = startingTrack;
        this.currentLocation = new Point2D((int) curTrack.getInitialX(20),(int) curTrack.getInitialY(20));
        setConnectionsLocatons();
    }


    /**
     * Sets the start of the train. The way it does it is by placing the rolling stock on top
     * of the train it is connect to and moving it back until it is no longer overlapping with it and
     * there is enough space for the connection. This is done by simulating the rolling stock moving backwards along
     * the track
     *
     * @param startPointOfConnection the start of the train
     *
     * @param sim the visualisation used to simulate the movement back
     * */
    public void setStart(Point2D startPointOfConnection, Simulation sim){
        // Reverse direction so we can reverse it away from the train
        this.direction = !direction;

        // the middle of the train it is connected to
        this.currentLocation = new Point2D((int)startPointOfConnection.getX(),(int)startPointOfConnection.getY());
        System.out.println(connectedToMovable.getCurRotation());
        this.degDone = Math.abs(connectedToMovable.getDegDone() -90);
        this.curRotation = connectedToMovable.getCurRotation();

        double len = getLengthPixels()/2 + 80;
        double increment = len/80;

        // set current track to the train we are connected to current track
        this.curTrack = connectedToMovable.getCurTrack();

        // The amount we have moved
        double total = 0;

        // While we have not moved back enough to account for our length keep updating
        while(total < len){
            sim.onSectionCheck(this,increment);

            if(curTrack instanceof JunctionTrack){
                JunctionTrack jt = (JunctionTrack)curTrack;
                this.curRotation= jt.getNextPoint(this,increment);
            }
            else{
                // Get the next point for the train
                this.curRotation = curTrack.getNextPoint(currentLocation,curRotation, degDone,increment, this);
            }
            total+=increment;
        }
        // Set the direction back to normal
        setDirection(!this.direction);
    }

    private void setConnectionsLocatons(){
        frontConnection.setCenterX(getFront().getX());
        frontConnection.setCenterY(getFront().getY());
        frontConnection.setRadius(20);
        backConnection.setCenterX(getBack().getX());
        backConnection.setCenterY(getBack().getY());
        backConnection.setRadius(20);
    }


    /**
     * Sets the direction of the rolling stock
     *
     * @param directionToSet the direction it should go
     * */
    public void setDirection(boolean directionToSet){
        // Check the direction is actually different
        if(directionToSet != this.direction){
            degDone = Math.abs(90- degDone);//TODO test
        }
        // Set the direction
        this.direction = directionToSet;
    }


    /**
     * Updates the location of the rolling stock
     * */
    public void update(){
        if(!connected || isCrashed)return;

        setConnectionsLocatons();

        if(connected){
            this.distMoved = connectedToMovable.getDistanceMoved();
        }

        if(curTrack instanceof JunctionTrack){
            JunctionTrack jt = (JunctionTrack)curTrack;
            this.curRotation = jt.getNextPoint(this,distMoved);
        } else{
            this.curRotation = curTrack.getNextPoint(currentLocation,curRotation, degDone,distMoved, this);
        }
    }

    private double distMoved;

    public double getDistanceMoved(){
        return this.distMoved;
    }

    /**
     * Redraws the rolling stock on the screen
     *
     * @param g the graphics context to draw on
     * */
    public void draw(GraphicsContext g){
        // Rotate the image by the current rotation
        trainImageView.setRotate(curRotation);
        Image rotatedImage = trainImageView.snapshot(params, null);
        rollingStockImage = rotatedImage;


        // Find the back of the train
        double conX = 0;
        double conY = 0;

        // Check if we are connected to a train
        if(connected){
            conX = connectedToMovable.getCurrentLocation().getX() + ((connectedToMovable.getLengthPixels()/2) * (Math.cos(Math.toRadians(connectedToMovable.getCurRotation()-90+180))));
            conY = connectedToMovable.getCurrentLocation().getY() + ((connectedToMovable.getLengthPixels()/2) * (Math.sin(Math.toRadians(connectedToMovable.getCurRotation()-90+180))));
        }

        // Find the front of the rolling stock
        double frontX = currentLocation.getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(curRotation-90))));
        double frontY = currentLocation.getY() + ((getLengthPixels()/2) * (Math.sin(Math.toRadians(curRotation-90))));

        // Only draw the line if the train is connected to something
        if(connected){
            g.setStroke(Color.GREEN);
            g.strokeLine(frontX, frontY,conX,conY);
        }

        // Draw the image
        g.drawImage(rollingStockImage, currentLocation.getX() - rollingStockImage.getWidth()/2, currentLocation.getY() - rollingStockImage.getHeight()/2);
    }


    /**
     * Returns if the point at x,y is on the rolling stock
     *
     * @param x the x location to check
     *
     * @param y the y location to check
     * */
    public boolean containsPoint(double x, double y) {
        double startX = currentLocation.getX() - getLengthPixels()/2;
        double startY = currentLocation.getY() - getWidthPixels()/2;

        if(x >= startX && x <= startX + getWidthPixels() && y > startY && y < startY + getLengthPixels()){
            System.out.println("True");
            return containsPointAccurate(x,y);
        }

        return false;
    }

    public boolean containsPointAccurate(double x, double y){

        double backX = this.getCurrentLocation().getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(this.getCurRotation()-90+180))));
        double backY = this.getCurrentLocation().getY() + ((getLengthPixels()/ 2) * (Math.sin(Math.toRadians(this.getCurRotation() - 90 + 180))));

        double frontX = this.getCurrentLocation().getX() - ((getLengthPixels()/2) * (Math.cos(Math.toRadians(this.getCurRotation()-90+180))));
        double frontY = this.getCurrentLocation().getY() - ((getLengthPixels()/2) * (Math.sin(Math.toRadians(this.getCurRotation() - 90 + 180))));

        // Cross product
        double x1 = ((backY - frontY)*1) - (0*0);
        double y1 = (0*0) - ((backX - frontX)*1);

        // Find the magnitude
        double mag = Math.sqrt((x1*x1) + (y1*y1));

        // 21 is the width of the image
        double xOffset = (0.5 * 21) * (x1/mag);
        double yOffset = (0.5 * 21) * (y1/mag);


        double aX = frontX - xOffset;//A
        double aY = frontY - yOffset;

        double bX = frontX + xOffset;// B
        double bY = frontY + yOffset;

        double cX = backX + xOffset; // C
        double cY = backY + yOffset;

        double dX = backX - xOffset; // D
        double dY = backY - yOffset;

        // ABP
        double t1 = 0.5 * Math.abs((aX*(bY - y)) + (bX*(y - aY)) + (x*(aY - bY)));

        // BCP
        double t2 = 0.5 * Math.abs((bX*(cY - y)) + (cX*(y - bY)) + (x*(bY - cY)));

        // CDP
        double t3 = 0.5 * Math.abs((cX*(dY - y)) + (dX*(y - cY)) + (x*(cY - dY)));

        // DAP
        double t4 = 0.5 * Math.abs((dX*(aY - y)) + (aX*(y - dY)) + (x*(dY - aY)));

        double rectArea = getWidthPixels() * getLengthPixels(); //TODO should be the width of the image instead of 21

        // if area is bigger point outside the rectangle
        if(t1 + t2 + t3 + t4 > rectArea){
            return false;
        }
        else {
            return true;
        }
    }

    public void setConnection(Movable movable){
        this.connectedToMovable = movable;
        this.direction = movable.getDirection();
        this.currentSpeed = movable.getCurrentSpeed();
        this.connected = true;
    }

    private Point2D front = new Point2D(0,0);
    private Point2D back = new Point2D(0,0);

    @Override
    public Point2D getFront(){
        front.x = currentLocation.getX() + (getLengthPixels()/2) * (Math.cos(Math.toRadians(curRotation-90)));
        front.y = currentLocation.getY() + ((getLengthPixels()/2) * (Math.sin(Math.toRadians(curRotation-90))));
        return front;
    }

    @Override
    public Point2D getBack(){
        back.x = currentLocation.getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(curRotation-90+180))));
        back.y = currentLocation.getY() + ((getLengthPixels()/2) * (Math.sin(Math.toRadians(curRotation-90+180))));
        return back;
    }


    /**
     * Returns if the rolling stock is connected to a train or rolling stock or not
     * */
    public boolean isConnected(){return this.connected;}


    /**
     * Sets the rolling stock connected to us
     * */
    public void setRollingStockConToUs(DrawableRollingStock stock){
        this.conToThis = stock;
    }

    public DrawableRollingStock getConToThis(){
        return conToThis;
    }


    /**
     * Returns the circle representing area of the front connection point of the rolling stock
     * */
    public Circle getFrontConnection(){
        return this.frontConnection;
    }

    /**
     * Returns the circle representing area of the back connection point of the rolling stock
     * */
    public Circle getBackConnection(){
        return this.backConnection;
    }

    /**
     * Returns the width of the train in pixels
     *
     * @return width in pixels
     * */
    public double getWidthPixels(){
        return rollingStock.getWidth()*Simulation.METER_MULTIPLIER;//TODO decide if to store here or in rolling stock
    }

    @Override
    public void setJuncTrack(DefaultTrack jt) {
        degDone = 0;
        this.juncTrack = jt;
    }

    @Override
    public void setCurTrack(DefaultTrack curTrack){
        degDone = 0;
        this.curTrack = curTrack;
    }

    @Override
    public boolean getOrientation() {
        return orientation;
    }

    @Override
    public Point2D getCurrentLocation(){
        return  this.currentLocation;
    }

    @Override
    public double getDegDone() {
        return degDone;
    }

    @Override
    public void setDegDone(double degDone) {
        this.degDone = degDone;
    }

    @Override
    public DefaultTrack getCurTrack(){return this.curTrack;}

    @Override
    public DrawableRollingStock getRollingStockConnected() {
        return null;// TODO remove
    }


    @Override
    public  void setCrashed(boolean crashed){
        System.out.println("Crashed");
        this.isCrashed = crashed;
    }

    @Override
    public double getLengthPixels(){
        return rollingStock.getLength()*Simulation.METER_MULTIPLIER;
    }

    @Override
    public double getCurrentSpeed() {
        return this.currentSpeed;
    }

    @Override
    public boolean isCrashed(){
        return this.isCrashed;
    }

    @Override
    public boolean getDirection(){
        return this.direction;
    }

    @Override
    public double getCurRotation(){
        return this.curRotation;
    }

    @Override
    public DefaultTrack getJuncTrack() {
        return juncTrack;
    }

    public RollingStock getStock(){
        return this.rollingStock;
    }
}
