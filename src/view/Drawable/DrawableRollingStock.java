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

    // The rolling stock it represents
    private RollingStock rollingStock;

    // Connection can be null
    private DrawableTrain connectedToTrain; // The train we are connected to
    private DrawableRollingStock connectedToStock;// The rolling stock pulling or pushing us
    private DrawableRollingStock conToThis;// Rolling stock we are pulling or pushing

    // Image drawing fields
    private SnapshotParameters params; // Drawing parameters
    private ImageView trainImageView; // The image view of the rolling stock
    private Image rollingStockImage; // The image of the rolling stock
    private double curRotation;// How far the image should be rotated

    // Rolling stock state information
    private boolean connected; // Connected to a train or not
    private boolean isCrashed; // Currently crashed or not
    private boolean direction; // Forward or backwards direction
    private double currentSpeed; // The current speed of the stock
    private Circle frontConnection; // Circle representing front where a connection with a stock or train can be make
    private Circle backConnection; // Circle representing back where a connection with a stock or train can be make
    private DefaultTrack juncTrack; // The junction track is is on if it is on one
    private DefaultTrack curTrack; // The current track it is on
    private Point currentLocation;// The location of the rolling stock
    private double degDone = 0; // The degrees through the curve


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


    /**
     * Sets up the image and image params for the rolling stock
     * */
    public void setUpImage(){
        this.rollingStockImage = new Image("file:src/res/rolling_stock.png", 20, 80, false, false);
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
        this.currentLocation = new Point((int) curTrack.getInitialX(20),(int) curTrack.getInitialY(20));
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
        // Set the direction back to normal
        setDirection(!this.direction);

    }

    private void setConnectionsLocatons(){
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
        if(!connected)return;

        setConnectionsLocatons();

        if(connected){
            if(connectedToTrain != null){
                this.currentSpeed= connectedToTrain.getForce();
            }
            else {
                this.currentSpeed = connectedToStock.getForce();//TODO check if theis is how I want to work
            }
        }
        else {
            this.decelerate(); // No longer connected so slow it down
        }

        if(curTrack instanceof JunctionTrack){
            JunctionTrack jt = (JunctionTrack)curTrack;
            this.curRotation = jt.getNextPoint(this,currentSpeed);
        }
        else{
            this.curRotation = curTrack.getNextPoint(currentLocation,curRotation, degDone,currentSpeed, this);
        }
    }

    /**
     * Decelerates the rolling stock down
     * */
    public void decelerate(){
        double timeChanged = 20;// ms
        this.currentSpeed -= rollingStock.getDeceleration()*(1000/timeChanged);
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
        if(connectedToStock != null){
            conX = connectedToStock.getCurrentLocation().getX() + ((connectedToStock.getLengthPixels()/2) * (Math.cos(Math.toRadians(connectedToStock.getCurRotation()-90+180))));
            conY = connectedToStock.getCurrentLocation().getY() + ((connectedToStock.getLengthPixels()/2) * (Math.sin(Math.toRadians(connectedToStock.getCurRotation()-90+180))));
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
        double startX = currentLocation.getX() - getLengthPixels()/2;
        double startY = currentLocation.getY() - getLengthPixels()/2;

        if(x >= startX && x <= startX + getWidthPixels() && y > startY && y < startY + getLengthPixels())return true;

        return false;
    }

    /**
     * Sets the rolling stock connected to a train.
     *
     * @param train train to connect to
     * */
    public void setTrainConnection(DrawableTrain train){
        this.connectedToTrain = train;
        this.direction = train.getDirection();
        this.currentSpeed = train.getCurrentSpeed();
        this.connected = true;
    }

    /**
     * Sets the rolling stock connected to a train.
     *
     * @param stock rolling stock to connect to
     * */
    public void setStockConnection(DrawableRollingStock stock){
        this.connectedToStock = stock;
        this.direction = stock.getDirection();
        this.currentSpeed = stock.getCurrentSpeed();
        this.connected = true;
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


    /**
     * Returns the amount to move in pixels
     * */
    public double getForce(){
        return this.currentSpeed;
    }//TODO change to proper physics later

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
        System.out.println(this.rollingStock.getRollID());
        if(connected){
            if(connectedToStock != null){
               return connectedToStock.getOrientation();
            }
            return connectedToTrain.getTrain().getOrientation();
        }

        return true;//TODO fix later should be our own orientation
    }

    @Override
    public Point getCurrentLocation(){
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
        public DrawableRollingStock getRollingStockConnected() {return connectedToStock;
    }

    @Override
    public  void setCrashed(boolean crashed){
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
        if(connectedToStock != null){
            return connectedToStock.getDirection();
        }

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
}
