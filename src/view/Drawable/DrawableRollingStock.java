package view.Drawable;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import model.RollingStock;
import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;
import view.Visualisation;

import java.awt.Point;

/**
 * Created by vanhunick on 21/04/16.
 */
public class DrawableRollingStock implements Movable{

    // The rolling stock it represents
    private RollingStock rollingStock;

    // The train it is connected to
    private DrawableTrain connectedToTrain;

    // The current track it is on
    private DefaultTrack curTrack;

    // Rolling stock connected to us
    private DrawableRollingStock conToThis;

    // Image of rolling stock
    private Image rollingStockImage;

    // The imageView of the image
    private ImageView trainImageView;

    // How far the image should be rotated
    private double curRotation;

    // The location of the rolling stock
    private Point currentLocation;

    // Last point on the curve
    public int lastPointOnCurve;

    // Image drawing params
    private SnapshotParameters params;

    // The speed of the rolling stock
    private double speed;

    // Weather the current speed is the start speed
    private boolean start;

    // The direction the rolling stock is traveling
    private boolean direction;

    private DefaultTrack juncTrack;

    // The state of the rolling stock
    private boolean isCrashed;

    // Is connected to a train
    private boolean connected;

    /**
     * Creates a new drawable rolling stock object
     *
     * @param rollingStock the rolling stock to represent
     *
     * @param connectedToTrain the train it is connected to
     *
     * @param direction the direction it is traveling
     * */
    public DrawableRollingStock(RollingStock rollingStock, DrawableTrain connectedToTrain, boolean direction){
        this.connected = true;
        this.rollingStock = rollingStock;
        this.connectedToTrain = connectedToTrain;
        this.direction = direction;
        this.curRotation = 90;
        this.lastPointOnCurve = 0;
        this.start = true;

        setUpImage();
    }


    /**
     * Constructor for a Rollingstock not connected to a train
     * */
    public DrawableRollingStock(RollingStock rollingStock){
        this.connected = false;
        this.rollingStock = rollingStock;
        this.curRotation = 90;
        setUpImage();
    }

    public void setUpImage(){
        this.rollingStockImage = new Image("file:src/res/rolling_stock.png", 20, 80, false, false);
        this.trainImageView = new ImageView(rollingStockImage);
        this.params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    public void setStartNotConnected(DefaultTrack startingTrack){
        this.curTrack = startingTrack;
        this.currentLocation = new Point((int) curTrack.getInitialX(20),(int) curTrack.getInitialY(20));
    }

    /**
     * Sets the start of the train. The way it does it is by placing the rolling stock on top
     * of the train it is connect to and moving it back until it is no longer overlapping with it and
     * there is enough space for the connection. This is done by simulating the rolling stock moving backwards along
     * the track
     *
     * @param startPointOfConnection the start of the train
     *
     * @param vis the visualisation used to simulate the movement back
     * */
    public void setStart(Point startPointOfConnection, Visualisation vis){
        // Reverse direction so we can reverse it away from the train
        this.direction = !direction;

        // the middle of the train it is connected to
        this.currentLocation = new Point((int)startPointOfConnection.getX(),(int)startPointOfConnection.getY());

        double len = rollingStock.getLength() + 40;
        double increment = len/40;

        // set current track to the train we are connected to current track
        this.curTrack = connectedToTrain.getCurTrack();

        // The amount we have moved
        double total = 0;

        // While we have not moved back enough to account for our length keep updating
        while(total < len){
            vis.onSectionCheck(this,increment);

            if(curTrack instanceof JunctionTrack){
                JunctionTrack jt = (JunctionTrack)curTrack;
                this.currentLocation = jt.getNextPoint(this,increment);
                this.curRotation = jt.getNextRotation(this, increment);
            }
            else{
                // Get the next point for the train
                this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,increment, connectedToTrain.getOrientation(),this.getDirection());
                // Get the next rotation for the train
                this.curRotation = curTrack.getNextRotation(curRotation,increment, connectedToTrain.getOrientation(),this.getDirection());
            }
            lastPointOnCurve++;
            total+=increment;
        }

        // Set the current speed to be the increment
        speed = increment;

        // Set the direction back to normal
        setDirection(!this.direction);
    }


    /**
     * Sets the direction of the rolling stock
     *
     * @param directionToSet the direction it should go
     * */
    public void setDirection(boolean directionToSet){
        // Check the direction is actually different
        if(directionToSet != this.direction){
            // The point along the curve should be changed since we are now going to other way
            lastPointOnCurve = curTrack.getNumberOfPoints(speed) - lastPointOnCurve;
        }
        // Set the direction
        this.direction = directionToSet;
    }


    /**
     * Updates the location of the rolling stock
     *
     * @param pixels the number of pixels to move by
     * */
    public void update(double pixels){
        if(!connected)return;
        this.speed = pixels;
        // If it is the start we need to update the speed to match the train speed
        if(start){
            updateSpeed(pixels);
            start = !start;
        }


        if(curTrack instanceof JunctionTrack){
            JunctionTrack jt = (JunctionTrack)curTrack;
            this.currentLocation = jt.getNextPoint(this,pixels);
            this.curRotation = jt.getNextRotation(this, pixels);
        }
        else{
            // Get the next point for the train
            this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,pixels, connectedToTrain.getOrientation(),this.getDirection());
            // Get the next rotation for the train
            this.curRotation = curTrack.getNextRotation(curRotation,pixels, connectedToTrain.getOrientation(),this.getDirection());
        }

        // Increment the progress down the curve
        lastPointOnCurve++;
    }


    /**
     * Updates the speed of the rolling stock.
     *
     * @param moveBy the number of pixels to move by
     * */
    public void updateSpeed(double moveBy){
        lastPointOnCurve = curTrack.getCurPointAfterSpeedChange(moveBy,this.speed,lastPointOnCurve);
    }

    /**
     * Redraws the rolling stock on the screen
     *
     * @param g the graphics context to draw on
     * */
    public void refresh(GraphicsContext g){
        // Rotate the image by the current rotation
        trainImageView.setRotate(curRotation);
        Image rotatedImage = trainImageView.snapshot(params, null);
        rollingStockImage = rotatedImage;


        // Find the back of the train
        double conX = 0;
        double conY = 0;

        // Check if we are connected to a train
        if(connectedToTrain != null){
            conX = connectedToTrain.getCurrentLocation().getX() + ((connectedToTrain.getTrain().getLength()/2) * (Math.cos(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
            conY = connectedToTrain.getCurrentLocation().getY() + ((connectedToTrain.getTrain().getLength()/2) * (Math.sin(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
        }

        // Meaning we are connected to a rollingstock
        if(conToThis != null){
            conX = conToThis.getCurrentLocation().getX() + ((connectedToTrain.getTrain().getLength()/2) * (Math.cos(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
            conY = conToThis.getCurrentLocation().getY() + ((connectedToTrain.getTrain().getLength()/2) * (Math.sin(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
        }

        // Find the front of the rolling stock

        double frontX = currentLocation.getX() + ((80/2) * (Math.cos(Math.toRadians(curRotation-90))));
        double frontY = currentLocation.getY() + ((80/2) * (Math.sin(Math.toRadians(curRotation-90))));

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
        double startX = currentLocation.getX() - rollingStockImage.getWidth()/2;
        double startY = currentLocation.getY() - rollingStockImage.getHeight()/2;

        if(x >= startX && x <= startX + rollingStockImage.getWidth() && y > startY && y < startY + rollingStockImage.getHeight())return true;

        return false;
    }


    @Override
    public void setCurTrack(DefaultTrack curTrack){
        lastPointOnCurve = 0;
        this.curTrack = curTrack;
    }

    @Override
    public void setLastPointOnCurve(int point) {
        this.lastPointOnCurve = point;
    }

    @Override
    public Point getCurrentLocation(){
        return  this.currentLocation;
    }

    @Override
    public boolean getOrientation() {
        return connectedToTrain.getTrain().getOrientation();
    }

    @Override
    public DefaultTrack getCurTrack(){return this.curTrack;}

    @Override
    public DrawableRollingStock getRollingStockConnected() {
        return conToThis;
    }

    @Override
    public  void setCrashed(boolean crashed){
        this.isCrashed = crashed;
    }

    @Override
    public int getLastPointOnCurve(){
        return lastPointOnCurve;
    }

    public double getLength(){
        return rollingStock.getLength();//TODO decide if to store here or in rolling stock
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
    public void setJuncTrack(DefaultTrack jt) {
        this.juncTrack = jt;
    }

    @Override
    public double getCurRotation(){
        return  this.curRotation;
    }

    @Override
    public DefaultTrack getJuncTrack() {
        return juncTrack;
    }

    public boolean isConnected(){return this.connected;}

}
