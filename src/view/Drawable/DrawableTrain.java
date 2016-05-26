package view.Drawable;




import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import model.Train;
import view.Drawable.section_types.*;
import view.Simulation;

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
    private DefaultTrack juncTrack;//

    // The current section it is on
    private DrawableSection curSection;

    // The rolling stock connected if any
    private DrawableRollingStock rollingStockConnected;

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

    private double currentSpeed;

    // Used by any connected rolling stock
    private double distMoved;

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
        this.currentSpeed = 0;

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

    public void setUpImage(){
        //Image setup
        this.trainImage= new Image("file:src/res/train.gif", train.getWidth() * Simulation.METER_MULTIPLIER, train.getLength() * Simulation.METER_MULTIPLIER, false, false);
        this.trainImageView = new ImageView(trainImage);
        this.params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
    }

    public double getForce(){
        return distMoved;
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

        g.setStroke(Color.RED);
    }


    /**
     * Updates the location of the train
     * */
    public void update(){
        if(crashed)return;


        // Check if the train is still accelerating and the current speed is less than the max speed
        if(currentSpeed < train.getTargetSpeed() && currentSpeed < train.getMaxSpeed()){
            applyAcceleration();
        }
        if(currentSpeed > train.getTargetSpeed()){
            deaccelerate();
        }

        if(lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
        long curTime = System.currentTimeMillis();

        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*currentSpeed;

        distMoved = pixelsToMove;

        // Check if direction has changed
        if(lastDirection != train.getDirection()){
            degDone = Math.abs(90- degDone);//TODO test

            if(rollingStockConnected != null){
                rollingStockConnected.setDirection(train.getDirection());
            }
        }
        lastDirection = train.getDirection();

        if(curTrack instanceof JunctionTrack){
            JunctionTrack jt = (JunctionTrack)curTrack;
            this.curRotation = jt.getNextPoint(this,pixelsToMove);
        }
        else{
            this.curRotation = curTrack.getNextPoint(currentLocation,curRotation, degDone,pixelsToMove, this);
        }

        // Update the rolling stock if there is one connected
        if(rollingStockConnected != null){
            rollingStockConnected.update();
        }
    }

    private double degDone = 0;

    public double getDegDone(){
        return this.degDone;
    }

    public void setDegDone(double done){
        this.degDone = done;
    }

    public void applyAcceleration(){
        double timeChanged = 20;// ms
        this.currentSpeed += train.getAcceleration()*(1000/timeChanged);
    }

    public void deaccelerate(){
        double timeChanged = 20;// ms
        this.currentSpeed -= train.getDeceleration()*(1000/timeChanged);
    }

    /**
     * Returns if the point at x,y is on the train
     *
     * @param x the x location to check
     *
     * @param y the y location to check
     * */
    public boolean containsPoint(double x, double y){
        double startX = currentLocation.getX() - train.getLength()/2;// Might be width
        double startY = currentLocation.getY() - train.getLength()/2;

        if(x >= startX && x <= startX + train.getWidth()*Simulation.METER_MULTIPLIER   && y > startY && y < startY + train.getLength()*Simulation.METER_MULTIPLIER)return true;

        // The point is not on the train or any of it's rolling stock
        return false;
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


    public double getCurrentSpeed(){
        return this.currentSpeed;
    }

    public double getLastDistMoved(){
        return distMoved;
    }

    public double getLength(){
        return train.getLength();//TODO need to decide on where to store full length including rolling stock or not to
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
    public void setJuncTrack(DefaultTrack juncTrack){
        lastPointOnCurve = 0;
        degDone = 0;
        this.juncTrack = juncTrack;
    }

    @Override
    public DefaultTrack getCurTrack(){
        return this.curTrack;
    }

    @Override
    public void setCurTrack(DefaultTrack track){
        lastPointOnCurve = 0;
        degDone = 0;
        this.curTrack = track;
    }

    @Override
    public DrawableRollingStock getRollingStockConnected(){
        return this.rollingStockConnected;
    }

    @Override
    public DefaultTrack getJuncTrack(){
        return this.juncTrack;
    }

    @Override
    public void setCrashed(boolean crashed){
        this.crashed = crashed;
    }

    @Override
    public boolean isCrashed(){
        return this.crashed;
    }

    @Override
    public double getCurRotation(){
        return this.curRotation;
    }

    @Override
    public boolean getDirection() {
        return this.getTrain().getDirection();
    }

    public void setDirection(boolean forward){
        this.train.setDirection(forward);
    }

    @Override
    public Point getCurrentLocation(){
        return  this.currentLocation;
    }

    @Override
    public boolean getOrientation() {
        return this.getTrain().getOrientation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DrawableTrain)) return false;

        DrawableTrain that = (DrawableTrain) o;

        if (Double.compare(that.width, width) != 0) return false;
        if (lastDirection != that.lastDirection) return false;
        if (crashed != that.crashed) return false;
        if (Double.compare(that.currentSpeed, currentSpeed) != 0) return false;
        if (!train.equals(that.train)) return false;
        if (!rollingStockConnected.equals(that.rollingStockConnected)) return false;
        return currentLocation.equals(that.currentLocation);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(width);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + train.hashCode();
        result = 31 * result + rollingStockConnected.hashCode();
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (lastDirection ? 1 : 0);
        result = 31 * result + (crashed ? 1 : 0);
        result = 31 * result + currentLocation.hashCode();
        temp = Double.doubleToLongBits(currentSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
