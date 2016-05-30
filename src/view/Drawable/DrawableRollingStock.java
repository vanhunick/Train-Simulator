package view.Drawable;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import model.RollingStock;
import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;
import view.Simulation;

import java.awt.Point;

/**
 * Created by vanhunick on 21/04/16.
 */
public class DrawableRollingStock implements Movable{

    // Current Speed
    private double currentSpeed;

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
//    public int lastPointOnCurve;

    // Image drawing params
    private SnapshotParameters params;

    // The speed of the rolling stock
    private double speed;


    // The direction the rolling stock is traveling
    private boolean direction;

    private DefaultTrack juncTrack;

    // The state of the rolling stock
    private boolean isCrashed;

    // Is connected to a train
    private boolean connected;

    private Circle frontConnection;
    private Circle backConnection;

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
        this.rollingStock = rollingStock;
        this.connectedToTrain = connectedToTrain;
        this.direction = direction;
        this.curRotation = 90;
        this.frontConnection = new Circle();
        this.backConnection = new Circle();

        if(connectedToTrain != null){
            connected = true;
        }
    }


    public Circle getFrontConnection(){
        return this.frontConnection;
    }

    public Circle getBackConnection(){
        return this.backConnection;
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
    public void setStart(Point startPointOfConnection, Simulation vis){
        // Reverse direction so we can reverse it away from the train
        this.direction = !direction;

        // the middle of the train it is connected to
        this.currentLocation = new Point((int)startPointOfConnection.getX(),(int)startPointOfConnection.getY());

        double len = getLengthPixels() + 40;
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
                this.curRotation= jt.getNextPoint(this,increment);
            }
            else{
                // Get the next point for the train
                this.curRotation = curTrack.getNextPoint(currentLocation,curRotation, degDone,increment, this);
            }
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
        System.out.println("Setting direction");
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
        if(!connected)return;

        double frontX = currentLocation.getX() + ((80/2) * (Math.cos(Math.toRadians(curRotation-90))));
        double frontY = currentLocation.getY() + ((80/2) * (Math.sin(Math.toRadians(curRotation-90))));

        double backX = currentLocation.getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(getCurRotation()-90+180))));
        double backY = currentLocation.getY() + ((getLengthPixels()/2) * (Math.sin(Math.toRadians(getCurRotation()-90+180))));


        frontConnection.setCenterX(frontX);
        frontConnection.setCenterY(frontY);
        frontConnection.setRadius(20);

        backConnection.setCenterX(backX);
        backConnection.setCenterY(backY);
        backConnection.setRadius(20);

        if(connected){
            this.currentSpeed= connectedToTrain.getForce();

        }
        else {
            this.decelerate(); // No longer connected so slow it down
        }

        if(curTrack instanceof JunctionTrack){
            System.out.println(currentSpeed);
            JunctionTrack jt = (JunctionTrack)curTrack;
            this.curRotation = jt.getNextPoint(this,currentSpeed);
        }
        else{
            System.out.println(curTrack);
            this.curRotation = curTrack.getNextPoint(currentLocation,curRotation, degDone,currentSpeed, this);
        }
    }

    public void decelerate(){
        double timeChanged = 20;// ms
        this.speed -= rollingStock.getDeceleration()*(1000/timeChanged);
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
        if(connectedToTrain != null){
            conX = connectedToTrain.getCurrentLocation().getX() + ((connectedToTrain.getLengthPixels()/2) * (Math.cos(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
            conY = connectedToTrain.getCurrentLocation().getY() + ((connectedToTrain.getLengthPixels()/2) * (Math.sin(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
        }

        // Meaning we are connected to a rollingstock
        if(conToThis != null){
            conX = conToThis.getCurrentLocation().getX() + ((connectedToTrain.getLengthPixels()/2) * (Math.cos(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
            conY = conToThis.getCurrentLocation().getY() + ((connectedToTrain.getLengthPixels()/2) * (Math.sin(Math.toRadians(connectedToTrain.getCurRotation()-90+180))));
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
        double startX = currentLocation.getX() - getWidthPixels()/2;
        double startY = currentLocation.getY() - getLengthPixels()/2;

        if(x >= startX && x <= startX + getWidthPixels() && y > startY && y < startY + getLengthPixels())return true;

        return false;
    }


    @Override
    public void setCurTrack(DefaultTrack curTrack){
        degDone = 0;
        this.curTrack = curTrack;
    }


    @Override
    public Point getCurrentLocation(){
        return  this.currentLocation;
    }

    @Override
    public boolean getOrientation() {
        if(connected){
            return connectedToTrain.getTrain().getOrientation();
        }

        return true;//TODO fix later should be our own orientation
    }

    @Override
    public double getDegDone() {
        return degDone;
    }

    private double degDone = 0;

    @Override
    public void setDegDone(double degDone) {
        System.out.println("Setting deg done ");
        this.degDone = degDone;
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


    public double getLengthPixels(){
        return rollingStock.getLength()*Simulation.METER_MULTIPLIER;//TODO decide if to store here or in rolling stock
    }

    public double getWidthPixels(){
        return rollingStock.getWidth()*Simulation.METER_MULTIPLIER;//TODO decide if to store here or in rolling stock
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
    public void setJuncTrack(DefaultTrack jt) {
        degDone = 0;
        this.juncTrack = jt;
    }

    @Override
    public double getCurRotation(){
        return this.curRotation;
    }

    @Override
    public DefaultTrack getJuncTrack() {
        return juncTrack;
    }

    public boolean isConnected(){return this.connected;}

    public void setTrainConnection(DrawableTrain train){
        this.connected = true;
        this.connectedToTrain = train;
        this.direction = train.getDirection();
        this.currentSpeed = train.getCurrentSpeed();
    }

}
