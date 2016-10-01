package simulation.Drawable;

import util.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import simulation.model.Train;
import simulation.Drawable.tracks.*;
import simulation.Simulation;


/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Movable{

    private Train train;// The train to draw

    private DrawableRollingStock rollingStockConnected;// The rolling stock connected if any
    private DefaultTrack curTrack; // The current track it is on
    private DefaultTrack juncTrack; // Only used to work out which track it is on inside a junction track
    private DrawableSection curSection; // The current section it is on
    private boolean currentDirection; // The previous direction
    private boolean targetDirection;// The direction the controller or user wants the train to move
    private boolean crashed; // If the train is crashed or not
    private double currentSpeed; // The current speed of the train metres per second
    private boolean braking;// Applied when changing direction or slowing down
    private double engineForce = 494000; // The engine force (could change to vary)
    public int brakePower = 800000; // The power when braking

    // Drawing fields
    private Image trainImage; // Image of the train
    private ImageView trainImageView; // Image simulation of the train
    private double curRotation = 90;  // The current rotation of the train image
    private Point2D currentLocation; // Current location of the train
    private SnapshotParameters params; // Params of the train image
    private Circle connection;
    private double distMoved; // The last distance moved in pixels used by stock
    private double degDone = 0; // The degrees the train is through the curve
    private double width; // The width of the train

    private int timeChanged = 20; // Time between updates

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
        this.currentLocation = new Point2D((int) curTrack.getInitialX(width),(int) curTrack.getInitialY(width));
        this.currentDirection = train.getDirection();
        this.targetDirection = train.getDirection();
        this.currentSpeed = 0;
        this.curRotation = 270;// Not nat orientation
        this.width = (train.getLength()/4)*Simulation.METER_MULTIPLIER;

        if(curTrack.getDirection().equals("RIGHT")){
            if(train.getOrientation()){
                this.curRotation = 90;// Nat orientation
            } else {
                this.curRotation = 270;// Nat orientation
            }
        }

        if(curTrack.getDirection().equals("LEFT")){
            if(train.getOrientation()) {
                this.curRotation = 270;// Not nat orientation
            } else {
                this.curRotation = 90;
            }
        }
        setConnectionLocation(); // Sets up connection location based on position
    }

    /**
     * Sets up the image fields for the drawable train
     * */
    public void setUpImage(){
        String col = new String[]{"_blue","_orange","_yellow",""}[((int)(Math.random()*4))];
        this.trainImage= new Image("file:src/res/train"+col+".png", width, train.getLength() * Simulation.METER_MULTIPLIER, false, false);
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
        // Set the image rotation and draw the image
        trainImageView.setRotate(curRotation);
        trainImage = trainImageView.snapshot(params, null);
        g.drawImage(trainImage, currentLocation.getX() - trainImage.getWidth()/2, currentLocation.getY() - trainImage.getHeight()/2);
    }

    /**
     * Returns the acceleration of the train
     * */
    public double  getAcceleration(){
        // Set friction co-efficient based on whether it is moving or not
        double  friction = currentSpeed > 0 ? DefaultTrack.KINETIC_FRICTION : DefaultTrack.STATIC_FRICTION;

        if(currentSpeed <= 0 && engineForce == 0)return 0;

        double netForce = engineForce - (friction * ((train.getWeight()+getRollingstockWeights()) * 9.88));



        // Apply air resistance when moving
        if(currentSpeed > 0)netForce = netForce - airResistance();

        if(currentSpeed <= 0){
            netForce = Math.max(0,netForce);
        }

        // Apply brake power
        if(braking){ netForce = netForce - brakePower; }

        // acceleration = force / mass
        return netForce / (train.getWeight() + getRollingstockWeights());
    }

    /**
     * Returns the air resistance on the train
     * */
    public double airResistance(){
        double airDensity = 1.225;// kg/m3
        double velocity = currentSpeed; // should be ms
        double dragCoefficient = 0.525;// Drag coefficient
        double area = 2.769 * 3.861;//from the British_Rail_Class_25 wiki page w*h

        return ((airDensity * dragCoefficient * area )/2) * velocity;
    }

    /**
     * Returns the combined weight of the rolling stock connected
     * */
    public double getRollingstockWeights(){
        if(rollingStockConnected != null){
            return rollingStockConnected.getRollingStocConnectedkWeight();
        }
        return 0;
    }

    /**
     * Sets the bounding box for the location where rolling stock can connect to
     * */
    public void setConnectionLocation(){
        connection.setCenterX(this.getCurrentLocation().getX() + ((getLengthPixels()/2) * (Math.cos(Math.toRadians(this.getCurRotation()-90+180)))));
        connection.setCenterY(this.getCurrentLocation().getY() + ((width/ 2) * (Math.sin(Math.toRadians(this.getCurRotation() - 90 + 180)))));
        connection.setRadius(10);
    }

    /**
     * Applies braking when direction is changed and sets engine force to 0
     * */


    boolean changingDirection = false;

    /**
     * Updates the location of the train
     * */
    public void update(){
        setConnectionLocation(); // Updates the connection location based on the new position
        if(crashed){
            distMoved = 0;
            currentSpeed = 0;
            return;
        }

        // Check if direction has changed
        if(targetDirection != train.getDirection()) {
            targetDirection = train.getDirection();
            changingDirection = true;
        }

        // Trying to go backwards then needing to go forward again // Trying to go forwards then needing to go backward again
        if(targetDirection == currentDirection && changingDirection){
            changingDirection = false;
            braking = false;
            engineForce = 494000;
        }
        else if(changingDirection){
            engineForce = 0;// TODO not sure
            braking = true;
        }

        if(changingDirection && currentSpeed <= 0){
            changingDirection = false;
            braking = false;
            degDone = Math.abs(90 - degDone);
            currentDirection = targetDirection;
            engineForce = 494000;

            if(rollingStockConnected != null){
                rollingStockConnected.setDirection(targetDirection);
            }
        }

        if(train.getTargetSpeed() == 0 && currentSpeed > 0){ // MIGHT BE WRONG
            braking = true;
            engineForce = 0;
        }
        if(train.getTargetSpeed() == 0 && currentSpeed <= 0){
            braking = false;
        }


        if(currentSpeed < 0 )currentSpeed = 0;// TODO find a better way to fix

        double acceleration = getAcceleration();// Metres per second per second

        currentSpeed += acceleration * (timeChanged/1000.0);// Convert Millisecond to second

        if(currentSpeed > train.getTargetSpeed() && !braking && engineForce - 1000 >= 0){
            engineForce = Math.max(engineForce -= 1000,0);
        }


        if(currentSpeed < train.getTargetSpeed() && !braking && acceleration < 0.25){
//            engineForce = Math.max(494000, engineForce);
            engineForce += 1000;
        }

        distMoved = ((timeChanged/1000.0)* (currentSpeed * Simulation.METER_MULTIPLIER)); // Work out the distance to move in pixels

        // Get the rotation from a normal track or junction track
        this.curRotation = curTrack instanceof JunctionTrack ? ((JunctionTrack)curTrack).getNextPoint(this,distMoved) : curTrack.getNextPoint(currentLocation,curRotation, degDone,distMoved, this);

        // Should crash because going backwards on a junction that is thrown
        if(curTrack instanceof JunctionTrack){
            JunctionTrack j = ((JunctionTrack)curTrack);
            crashed = j.checkThrownCrash(this);
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

        if((x >= startX && x <= startX + getLengthPixels()   && y > startY && y < startY + getLengthPixels())){
            return containPointAccurate(x,y);// Check the more expensive way
        }
        return false;
    }

    public boolean containPointAccurate(double x, double y){

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

        double rectArea = width * getLengthPixels(); //TODO should be the width of the image instead of 21

        // if area is bigger point outside the rectangle
        if(t1 + t2 + t3 + t4 > rectArea){
            return false;
        }
        else {
            return true;
        }
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
     * Returns the current force the engine outputs
     * */
    public double getEngineForce(){
        return this.engineForce;
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
     * Returns the current speed of the train
     * */
    public double getCurrentSpeed(){
        return this.currentSpeed;
    }

    @Override
    public double getDistanceMoved() {
        return this.distMoved;
    }

    /**
     * Returns the length of the train in pixels
     * */
    public double getLengthPixels(){
        return train.getLength() * Simulation.METER_MULTIPLIER;
    }

    /**
     * Returns the length of the train im metres
     * */
    public double getLengthMetres(){
        return train.getLength();
    }


    /**
     * Used to connect rolling stock to train before starting simulation
     * */
    public void setPixelsMoved(int force){
        this.distMoved = force;
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
    public boolean getDirection() {return currentDirection;}

    @Override
    public Point2D getCurrentLocation(){
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
        if (currentDirection != that.currentDirection) return false;
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
        result = 31 * result + (currentDirection ? 1 : 0);
        result = 31 * result + (crashed ? 1 : 0);
        result = 31 * result + currentLocation.hashCode();
        temp = Double.doubleToLongBits(currentSpeed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
