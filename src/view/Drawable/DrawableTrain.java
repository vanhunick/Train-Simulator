package view.Drawable;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;
import model.Train;
import view.Drawable.track_types.DefSection;


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

    public DrawableTrain(Train train, DefSection curSection){
        this.train = train;
        this.curSection = curSection;
        this.curX = curSection.getStartX();
        this.curY = curSection.getStartY();
    }

    @Override
    public void draw(GraphicsContext g){
        g.setFill(Color.RED);
        g.fillRect(curX, curY, width, width);
    }

    public void update(){

        long curTime = System.currentTimeMillis();
        if(curTime - lastUpdate > 100){
            lastUpdate = curTime;

            this.curX = curSection.getNextX(curX,8);
            this.curY = curSection.getNextY(curY,8);
        }
        train.getSpeed();
    }


    public Train getTrain(){
        return this.train;
    }

    public DefSection getCurSection(){
        return this.curSection;
    }

    public void setCurSection(DefSection section){
        this.curSection = section;
    }

    public double getX(){
        return this.curX;
    }

    public double getY(){
        return  this.curY;
    }

}
