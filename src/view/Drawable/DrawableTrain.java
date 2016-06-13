package view.Drawable;




import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Train;
import view.Drawable.section_types.*;
import view.Simulation;

import java.awt.*;


/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Movable{

    // The train to draw
    private Train train;

    // The rolling stock connected if any
    private DrawableRollingStock rollingStockConnected;
    private double connectionSize = 10;// The size of the circle that represent the connection

    // Fields for the state of the train
    private long lastUpdate; // Time of the lastUpdate
    private DefaultTrack curTrack; // The current track it is on
    private DefaultTrack juncTrack; // Only used to work out which track it is on inside a junction track
    private DrawableSection curSection; // The current section it is on
    private boolean lastDirection; // The previous direction
    private boolean crashed; // If the train is crashed or not
    private double width = 40; // The width of the train
    private double currentSpeed; // The current speed of the train metres per second
    private double distMoved; // The last distance moved in pixels used by stock
    private double degDone = 0; // The degrees the train is through the curve

    // Drawing fields
    private Image trainImage; // Image of the train
    private ImageView trainImageView; // Image view of the train
    private double curRotation = 90;  // The current rotation of the train image
    private Point currentLocation; // Current location of the train
    private SnapshotParameters params; // Params of the train image
    private Circle connection;

    private double engineForce = 494000;
    private double maxEngineForce = 700000;
    private double minEngineForce = 220000;


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
        this.connection = new Circle();
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
        setConnectionLocation();
    }

    public double getEngineForce(){
        return this.engineForce;
    }

    /**
     * Sets up the image fields for the drawable train
     * */
    public void setUpImage(){
        this.trainImage= new Image("file:src/res/train.gif", train.getWidth() * Simulation.METER_MULTIPLIER, train.getLength() * Simulation.METER_MULTIPLIER, false, false);
        this.trainImageView = new ImageView(trainImage);
        this.params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
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

        double conX = this.getCurrentLocation().getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(this.getCurRotation()-90+180))));
        double conY = this.getCurrentLocation().getY() + ((getLengthPixels()/2) * (Math.sin(Math.toRadians(this.getCurRotation()-90+180))));

        g.setFill(Color.GREEN);
        g.fillRect(conX - 5, conY - 5, 10, 10);
    }


    public double  getAcceleration(){
        // Normal force needed for friction mass * gravity since it's always horizontal
        // Force = train power -  max static friction which is co efficient of static friction * Normal forcr which is mass * gravity
        // Force = train power - (coStaticFriction * (mass*gravity)
        // if train is moving use kinetic friction else use the static friction

        double friction = 0;
        if(currentSpeed > 0){
            friction = DefaultTrack.KINETIC_FRICTION;
        }
        else {
            friction = DefaultTrack.STATIC_FRICTRION;
        }

        double netForce = 0;
        if(engineForce < 0){
            netForce = engineForce + (friction * (train.getWeight() * 9.88) );

        }
        else {
            netForce = engineForce - (friction * ((train.getWeight()+getRollingstockWeights()) * 9.88) );
            netForce = netForce - airResistance();
            System.out.println(netForce);
        }

        // acceleration = force / mass
        return netForce / (train.getWeight() + getRollingstockWeights());
    }

    public double airResistance(){
        double airDensity = 1.225;// kg/m3
        double velocity = currentSpeed; // should be ms
        double dragCoefficient = 0.525;// Drag coefficient
        double area = 2.769 * 3.861;//TODO from the British_Rail_Class_25 wiki page w*h

        return ((airDensity * dragCoefficient * area )/2) * velocity;
    }

    public double getRollingstockWeights(){
        if(rollingStockConnected != null){
            return rollingStockConnected.getRollingStocConnectedkWeight();
        }
        return 0;
    }

    public void setConnectionLocation(){
        connection.setCenterX(this.getCurrentLocation().getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(this.getCurRotation()-90+180)))));
        connection.setCenterY(this.getCurrentLocation().getY() + ((getLengthPixels() / 2) * (Math.sin(Math.toRadians(this.getCurRotation() - 90 + 180)))));
        connection.setRadius(10);
    }

    /**
     * Updates the location of the train
     * */
    public void update(){
        if(crashed)return;

        setConnectionLocation();


        if(lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
        long curTime = System.currentTimeMillis();

        //new movement code
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;//milli second

        double acceleration = getAcceleration();// Metres per second per second
        System.out.println("acceleration " + acceleration);

        if(train.getDirection() == true){
            if(currentSpeed + (acceleration* (timeChanged/1000.0)) < 0){
                currentSpeed = 0;
            }
            else {
                currentSpeed += acceleration * (timeChanged/1000.0);// Convert Millisecond to second
            }
        }



        if(currentSpeed > train.getTargetSpeed()){
            if(engineForce - 1000 >= 0){
                engineForce -= 1000;
            }
        }

        if(currentSpeed < train.getTargetSpeed()){
            if(acceleration < 0.25){
                engineForce += 1000;
            }

        }


        int i = 0;
        int j = 0;

        i += 1;//3
        j = j + 1;// 3


        double pixelsToMove = ((timeChanged/1000.0)* (currentSpeed * Simulation.METER_MULTIPLIER));// Converts from metres to pixels
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
    }


    /**
     * Returns if the point at x,y is on the train
     *
     * @param x the x location to check
     *
     * @param y the y location to check
     * */
    public boolean containsPoint(double x, double y){
        double startX = currentLocation.getX() - getLengthPixels()/2;// Might be width
        double startY = currentLocation.getY() - getLengthPixels()/2;


        if(x >= startX && x <= startX + getLengthPixels()   && y > startY && y < startY + getLengthPixels())return true;//TODO make more accurate

        // The point is not on the train or any of it's rolling stock
        return false;
    }

    /**
     * Returns the circle representing the area of the connection point
     * */
    public Circle getConnection(){
        return connection;
    }

    /**
     * Returns how many degrees it is through the current curve
     * */
    public double getDegDone(){
        return this.degDone;
    }

    /**
     * Sets the amount the train is through the curve
     * */
    public void setDegDone(double done){
        this.degDone = done;
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

    /**
     * Returns the current speed of the train
     * */
    public double getCurrentSpeed(){
        return this.currentSpeed;
    }

    /**
     * Returns the length of the train in pixels
     * */
    public double getLengthPixels(){
        return train.getLength() * Simulation.METER_MULTIPLIER;//TODO need to decide on where to store full length including rolling stock or not to
    }

    /**
     * Returns the length of the train im metres
     * */
    public double getLengthMetres(){
        return train.getLength();
    }

    /**
     * Returns the last distance moved in pixels of the train
     * */
    public double getForce(){
        return distMoved;
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
        degDone = 0;
        this.juncTrack = juncTrack;
    }

    @Override
    public DefaultTrack getCurTrack(){
        return this.curTrack;
    }

    @Override
    public void setCurTrack(DefaultTrack track){
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
        return this.currentLocation;
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
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (lastDirection ? 1 : 0);
        result = 31 * result + (crashed ? 1 : 0);
        result = 31 * result + currentLocation.hashCode();
        temp = Double.doubleToLongBits(currentSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
