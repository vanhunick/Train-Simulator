package view.Drawable;


import java.awt.*;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import model.Train;
import view.Drawable.section_types.*;



/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Drawable{
    private double width = 40;


    private Train train;

    private long lastUpdate;

    // The current track it is on
    private DefaultTrack curTrack;

    // The current section it is on
    private DrawableSection curSection;

    // Drawing fields
    private Image trainImage;
    private ImageView trainImageView;
    private double curRotation = 90;
    private Point currentLocation;
    public int lastPointOnCurve = 0;
    private SnapshotParameters params;


    /**
     * Creates a new drawable train object
     * */
    public DrawableTrain(Train train,DrawableSection curSection, DefaultTrack curTrack){
        this.curSection = curSection;
        this.train = train;
        this.curTrack = curTrack;
        this.currentLocation = new Point((int) curTrack.getInitialX(width),(int) curTrack.getInitialY(width));

        //Image setup
        this.trainImage= new Image("file:src/res/train.gif", 40, 143, false, false);
        this.trainImageView = new ImageView(trainImage);
        this.params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        if(train.getOrientation() || !train.getDirection()){
            this.curRotation = 90;
        }
        else{
            this.curRotation = 270;
        }
    }

    @Override
    public void draw(GraphicsContext g){
        trainImageView.setRotate(curRotation);
        Image rotatedImage = trainImageView.snapshot(params, null);
        trainImage = rotatedImage;

        g.drawImage(trainImage, currentLocation.getX() - trainImage.getWidth()/2, currentLocation.getY() - trainImage.getHeight()/2);
    }

    public void update(){
        if(lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }
        long curTime = System.currentTimeMillis();

        double speed = train.getSpeed();//Speed in pixels per second
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*speed;

        this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,pixelsToMove, train.getOrientation(),train.getDirection());

        curRotation = curTrack.getNextRotation(curRotation,pixelsToMove, train.getOrientation(),train.getDirection());
        lastPointOnCurve++;
    }


    public Train getTrain(){
        return this.train;
    }

    public DefaultTrack getCurTrack(){
        return this.curTrack;
    }

    public void setCurTrack(DefaultTrack section){
        lastPointOnCurve = 0;
        this.curTrack = section;
    }

    public void setCurSection(DrawableSection curSection){
        this.curSection = curSection;
    }

    public DrawableSection getCurSection(){
        return  this.curSection;
    }

    public double getX(){
        return this.currentLocation.getX();
    }
    public double getY(){
        return  this.currentLocation.getY();
    }

    public Point getCurPoint(){return this.currentLocation;}

    public DrawableSection getDrawableSection(){return this.curSection;}

    public Point getCurrentLocation(){
        return  this.currentLocation;
    }
}
