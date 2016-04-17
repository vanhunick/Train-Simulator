package view.Drawable;


import java.awt.*;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import model.Section;
import model.Train;
import view.Drawable.section_types.*;



/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Drawable{
    private double width = 40;

    private double curX;
    private double curY;
    private Train train;
    private long lastUpdate;



    private DefaultTrack curTrack;

    private DrawableSection curSection;

    private Image trainImage;

    private ImageView trainImageView;
    private double curRotation = 90;

    private Point curentLocation;

    //Drawing along curve fields
    public int lastPointOnCurve = 0;

    private SnapshotParameters params;


    public DrawableTrain(Train train,DrawableSection curSection, DefaultTrack curTrack){
        this.curSection = curSection;
        this.train = train;
        this.curTrack = curTrack;

        this.curX = curTrack.getInitialX(width);
        this.curY = curTrack.getInitialY(width);
        this.curentLocation = new Point((int) curTrack.getInitialX(width),(int) curTrack.getInitialY(width));

        //Image setup
        this.trainImage= new Image("file:src/res/train.gif", 40, 143, false, false);
        this.trainImageView = new ImageView(trainImage);
        this.params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        this.curRotation = 90;
    }

    @Override
    public void draw(GraphicsContext g){
        trainImageView.setRotate(curRotation);
        Image rotatedImage = trainImageView.snapshot(params, null);
        trainImage = rotatedImage;

        g.drawImage(trainImage,curentLocation.getX() - trainImage.getWidth()/2,curentLocation.getY() - trainImage.getHeight()/2);
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

        this.curentLocation = curTrack.getNextPoint(curentLocation, lastPointOnCurve,pixelsToMove);

        curRotation = curTrack.getNextRotation(curRotation,pixelsToMove);
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
        return this.curentLocation.getX();
    }
    public double getY(){
        return  this.curentLocation.getY();
    }

    public Point getCurPoint(){return this.curentLocation;}

    public DrawableSection getDrawableSection(){return this.curSection;}

    public Point getCurentLocation(){
        return  this.curentLocation;
    }
}
