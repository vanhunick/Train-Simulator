package view.Drawable;




import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import model.Train;
import view.Drawable.section_types.*;

import java.awt.*;


/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Movable{

    // The width of the train
    private double width = 40;

    // The train to draw
    private Train train;

    // Time of the lastUpdate
    private long lastUpdate;

    // The current track it is on
    private DefaultTrack curTrack;

    // Only used to work out which track it is on inside a junction track
    private DefaultTrack juncTrack;// TODO not ideal

    // The current section it is on
    private DrawableSection curSection;

    // The rolling stock connected if any
    private DrawableRollingStock rollingStockConnected;

    // Our previous speed used for location calculations around curves
    private double lastSpeed;

    // The previous direction
    private boolean lastDirection;

    private boolean crashed;

    // Drawing fields
    private Image trainImage;
    private ImageView trainImageView;
    private double curRotation = 90;
    private Point currentLocation;
    public int lastPointOnCurve = 0;
    private SnapshotParameters params;


    /**
     * Creates a new drawable train object
     *
     * @param train train to draw
     *
     * @param curSection the section it is on
     *
     * @param curTrack the track it is on
     * */
    public DrawableTrain(Train train,DrawableSection curSection, DefaultTrack curTrack){
        this.crashed = false;
        this.curSection = curSection;
        this.train = train;
        this.curTrack = curTrack;
        this.currentLocation = new Point((int) curTrack.getInitialX(width),(int) curTrack.getInitialY(width));
        this.lastDirection = train.getDirection();

        //Image setup
        this.trainImage= new Image("file:src/res/train.gif", 20, 80, false, false);
        this.trainImageView = new ImageView(trainImage);
        this.params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        if(train.getOrientation()){
            if(curTrack.getDirection().equals("LEFT")){
                this.curRotation = 270;// Not nat orientation
            }
            else {
                this.curRotation = 90;// Nat orientation
            }

        }
        else{//TODO check if need to consider other direction here too
            this.curRotation = 270;// Not nat orientation
        }
    }


    /**
     * Redraws the train on the screen
     *
     * @param g the graphics context to draw on
     * */
    public void draw(GraphicsContext g){
        // Set the image rotation
        trainImageView.setRotate(curRotation);
        Image rotatedImage = trainImageView.snapshot(params, null);
        trainImage = rotatedImage;


        // Draw the image
        g.drawImage(trainImage, currentLocation.getX() - trainImage.getWidth()/2, currentLocation.getY() - trainImage.getHeight()/2);
    }


    /**
     * Updates the location of the train
     * */
    public void update(){
        if(crashed)return;


        if(lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
        long curTime = System.currentTimeMillis();

        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*train.getSpeed();

        // Check if the speed have changed
        if(lastSpeed != pixelsToMove && lastPointOnCurve != 0){
            lastPointOnCurve =curTrack.getCurPointAfterSpeedChange(pixelsToMove,lastSpeed,lastPointOnCurve);
        }
        lastSpeed = pixelsToMove;

        // Check if direction has changed
        if(lastDirection != train.getDirection()){
            lastPointOnCurve = curTrack.getNumberOfPoints(pixelsToMove) - lastPointOnCurve;
            if(rollingStockConnected != null){
                rollingStockConnected.setDirection(train.getDirection());
            }
        }
        lastDirection = train.getDirection();

        if(curTrack instanceof JunctionTrack){
            JunctionTrack jt = (JunctionTrack)curTrack;
            this.currentLocation = jt.getNextPoint(this,pixelsToMove);
            this.curRotation = jt.getNextRotation(this, pixelsToMove);
        }
        else{
            // Get the next point for the train
            this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,pixelsToMove, train.getOrientation(),train.getDirection());
            // Get the next rotation for the train
            this.curRotation = curTrack.getNextRotation(curRotation,pixelsToMove, train.getOrientation(),train.getDirection());
        }

        // Increment the progress on the current track
        lastPointOnCurve++;

        // Update the rolling stock if there is one connected
        if(rollingStockConnected != null){
            rollingStockConnected.update(pixelsToMove);
        }
    }

    public double getCurRotation(){
        return this.curRotation;
    }


    /**
     * Returns the train it represents
     *
     * @return train
     * */
    public Train getTrain(){
        return this.train;
    }

    /**
     * Returns the current track the train is on
     *
     * @return current track
     * */
    public DefaultTrack getCurTrack(){
        return this.curTrack;
    }

    /**
     * Sets the current track and resets the progress along the curve field
     *
     * @param track the current track
     * */
    public void setCurTrack(DefaultTrack track){
        lastPointOnCurve = 0;
        this.curTrack = track;
    }

    /**
     * Sets the current section
     *
     * @param curSection
     * */
    public void setCurSection(DrawableSection curSection){
        this.curSection = curSection;
    }

    /**
     * Returns the current section
     *
     * @return drawable section
     * */
    public DrawableSection getCurSection(){
        return  this.curSection;
    }

    /**
     * Returns the x value of the location
     *
     * @return x
     * */
    public double getX(){
        return this.currentLocation.getX();
    }

    /**
     * Returns the y value of the location
     *
     * @return y
     * */
    public double getY(){
        return  this.currentLocation.getY();
    }

    /**
     * Returns a point representing the current location
     *
     * @return current location
     * */
    @Override
    public Point getCurrentLocation(){
        return  this.currentLocation;
    }

    @Override
    public boolean getOrientation() {
        return this.getTrain().getOrientation();
    }

    @Override
    public int getLastPointOnCurve() {
        return lastPointOnCurve;
    }

    /**
     * Returns the connected rolling stock
     *
     * @return rolling stock connected
     * */
    public DrawableRollingStock getRollingStockConnected(){
        return this.rollingStockConnected;
    }

    /**
     * Sets the rolling stock connected
     *
     * @param dr the rolling stock that should be connected to this train
     * */
    public void setRollingStockConnected(DrawableRollingStock dr){
        this.rollingStockConnected = dr;
    }

    @Override
    public DefaultTrack getJuncTrack(){
        return this.juncTrack;
    }

    @Override
    public void setLastPointOnCurve(int point) {
        this.lastPointOnCurve = point;
    }

    public void setCrashed(boolean crashed){
        this.crashed = crashed;
    }

    public boolean isCrashed(){
        return this.crashed;
    }

    @Override
    public boolean getDirection() {
        return this.getTrain().getDirection();
    }

    public boolean containsPoint(double x, double y){

        double startX = currentLocation.getX() - trainImage.getWidth()/2;
        double startY = currentLocation.getY() - trainImage.getHeight()/2;

        if(x >= startX && x <= startX + trainImage.getWidth() && y > startY && y < startY + trainImage.getHeight())return true;

        if(rollingStockConnected != null){
            return rollingStockConnected.containsPoint(x,y);
        }

        // The point is not on the train or any of it's rolling stock
        return false;
    }


    public void setJuncTrack(DefaultTrack juncTrack){
        lastPointOnCurve = 0;
        this.juncTrack = juncTrack;
    }
}
