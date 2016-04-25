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

    private RollingStock rollingStock;
    private DrawableTrain connectedToTrain;
    private DefaultTrack curTrack;

    private DrawableRollingStock conToThis;// A rolling stock connected to us

    // Drawing fields
    private Image rollingStockImage;
    private ImageView trainImageView;
    private double curRotation = 90;
    private Point currentLocation;
    public int lastPointOnCurve = 0;
    private SnapshotParameters params;

    private boolean direction;

    public DrawableRollingStock(RollingStock rollingStock, DrawableTrain connectedToTrain, boolean direction){
        this.rollingStock = rollingStock;
        this.connectedToTrain = connectedToTrain;
        this.direction = direction;


        //Image setup
        this.rollingStockImage = new Image("file:src/res/rolling_stock.png", 30, 100, false, false);
        this.trainImageView = new ImageView(rollingStockImage);
        this.params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
    }

    public void setStart(Point startPointOfConnection, Visualisation vis){
        // Reverse direction so we can reverse it away from the train
        this.direction = !direction;

        // the middle of the train it is connected to
        this.currentLocation = new Point((int)startPointOfConnection.getX(),(int)startPointOfConnection.getY());

        double len = rollingStock.getLength() + 40;
        double increment = len/10;

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

        lastPointOnCurve = curTrack.getNumberOfPoints(increment) - lastPointOnCurve;

        this.direction = !this.direction;
    }

    public boolean getDirection(){
        return this.direction;
    }


    public void update(double pixels){
        this.currentLocation = curTrack.getNextPoint(currentLocation, lastPointOnCurve,pixels, connectedToTrain.getTrain().getOrientation(),this.getDirection());
        curRotation = curTrack.getNextRotation(curRotation,pixels, connectedToTrain.getTrain().getOrientation(),this.getDirection());
        lastPointOnCurve++;
    }

    public void refresh(GraphicsContext g){
        trainImageView.setRotate(curRotation);
        Image rotatedImage = trainImageView.snapshot(params, null);
        rollingStockImage = rotatedImage;

        g.drawImage(rollingStockImage, currentLocation.getX() - rollingStockImage.getWidth()/2, currentLocation.getY() - rollingStockImage.getHeight()/2);
    }

    public DrawableTrain getConnectedToTrain(){
        return  this.connectedToTrain;
    }

    public Point getCurrentLocation(){return  this.currentLocation;}

    public DefaultTrack getCurTrack(){return this.curTrack;}

    public void setCurTrack(DefaultTrack curTrack){
        lastPointOnCurve = 0;
        this.curTrack = curTrack;
    }

    public DrawableRollingStock getConToThis(){
        return this.conToThis;
    }
}
