package view.Drawable;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
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

    //Drawing along curve fields
    public int lastPointOnCurve = 0;
    public double angleGone = 0;

    public DrawableTrain(Train train, DefSection curSection){
        this.train = train;
        this.curSection = curSection;

        this.curX = curSection.getInitialX(width);
        this.curY = curSection.getInitialY(width);
    }

    @Override
    public void draw(GraphicsContext g){
        g.setFill(Color.RED);
        g.fillRect(curX - width/2, curY - width/2, width, width);
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



        if(curSection instanceof Quart2 || curSection instanceof Quart3 || curSection instanceof Quart4 || curSection instanceof Quart1){
            this.curX = curSection.getNextPoint(curX,curY,lastPointOnCurve, pixelsToMove).getX();
            this.curY = curSection.getNextPoint(curX,curY,lastPointOnCurve, pixelsToMove).getY();
            lastPointOnCurve++;
        }
        else{
            this.curX = curSection.getNextX(curX,pixelsToMove);
            this.curY = curSection.getNextY(curY,pixelsToMove);
        }

        lastUpdate = curTime;
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
        return this.curX;
    }
    public double getY(){
        return  this.curY;
    }
}
