package view.Drawable;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.RollingStock;
import view.Drawable.section_types.DefaultTrack;
import view.Visualisation;

import java.awt.*;

/**
 * Created by vanhunick on 21/04/16.
 */
public class DrawableRollingStock{

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
        this.lastPointOnCurve = 0;
        this.start = true;


        //Image setup
        this.rollingStockImage = new Image("file:src/res/rolling_stock.png", 30, 100, false, false);
        this.trainImageView = new ImageView(rollingStockImage);
        this.params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
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
            this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,increment, connectedToTrain.getTrain().getOrientation(),this.getDirection());
            curRotation = curTrack.getNextRotation(curRotation,increment, connectedToTrain.getTrain().getOrientation(),this.getDirection());
            lastPointOnCurve++;
            total+=increment;
        }

        // Set the current speed to be the increment
        speed = increment;

        // Set the direction back to normal
        setDirection(!this.direction);
    }


    /**
     * Returns the direction of the rolling stock
     *
     * @return direction
     * */
    public boolean getDirection(){
        return this.direction;
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
        // If it is the start we need to update the speed to match the train speed
        if(start){
            updateSpeed(pixels);
            start = !start;
        }

        // Get the next point for the rolling stock
        this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,pixels, connectedToTrain.getTrain().getOrientation(),this.getDirection());

        // Get the next rotation of the rolling stock
        curRotation = curTrack.getNextRotation(curRotation,pixels, connectedToTrain.getTrain().getOrientation(),this.getDirection());

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

        // Draw the image
        g.drawImage(rollingStockImage, currentLocation.getX() - rollingStockImage.getWidth()/2, currentLocation.getY() - rollingStockImage.getHeight()/2);
    }


    /**
     * Returns the train we are connected to
     *
     * @return train
     * */
    public DrawableTrain getConnectedToTrain(){
        return this.connectedToTrain;
    }


    /**
     * Returns out current location
     *
     * @return location
     * */
    public Point getCurrentLocation(){return  this.currentLocation;}


    /**
     * Returns the current track it is on
     *
     * @return track
     * */
    public DefaultTrack getCurTrack(){return this.curTrack;}


    /**
     * Sets the track it is on
     *
     * @param curTrack the track it is on
     * */
    public void setCurTrack(DefaultTrack curTrack){
        lastPointOnCurve = 0;
        this.curTrack = curTrack;
    }


    /**
     * Return the rolling stock connected to this rolling stock
     *
     * @return rolling stock
     * */
    public DrawableRollingStock getConToThis(){
        return this.conToThis;
    }
}
