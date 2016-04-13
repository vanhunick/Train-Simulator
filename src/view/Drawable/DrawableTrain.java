package view.Drawable;


import java.awt.*;
import java.util.List;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import model.Section;
import model.Train;
import view.Drawable.track_types.*;



/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Drawable{
    private double width = 40;

    private double curX;
    private double curY;
    private Train train;
    private long lastUpdate;
    private DefSection curSection;
    private Image trainImage;

    private ImageView trainImageView;
    private double curRotation = 90;

    private Point curentLocation;

    //Drawing along curve fields
    public int lastPointOnCurve = 0;

    private SnapshotParameters params;

    public DrawableTrain(Train train, DefSection curSection){
        this.train = train;
        this.curSection = curSection;
        this.trainImage= new Image("file:src/res/train.gif", 40, 143, false, false);
        this.trainImageView = new ImageView(trainImage);
        this.params = new SnapshotParameters();//TODO make more efficient later
        params.setFill(Color.TRANSPARENT);
        this.curRotation = 90;
        this.curX = curSection.getInitialX(width);
        this.curY = curSection.getInitialY(width);

        this.curentLocation = new Point((int)curSection.getInitialX(width),(int)curSection.getInitialY(width));
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

        this.curentLocation = curSection.getNextPoint(curentLocation, lastPointOnCurve,pixelsToMove);

        curRotation = curSection.getNextRotation(curRotation,pixelsToMove);
        lastPointOnCurve++;
    }


    public Train getTrain(){
        return this.train;
    }

    public DefSection getCurSection(){
        return this.curSection;
    }

    public void setCurSection(DefSection section){
        lastPointOnCurve = 0;
        this.curSection = section;
    }

    public double getX(){
        return this.curentLocation.getX();
    }
    public double getY(){
        return  this.curentLocation.getY();
    }

    public Point getCurPoint(){return this.curentLocation;}

    public Point getCurentLocation(){
        return  this.curentLocation;
    }
}
