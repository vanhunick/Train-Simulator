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
//        g.fillRect(curX, curY, width, length);
        g.fillRect(curX, curY, width, train.getLength());
    }

    public void update(){
        long curTime = System.currentTimeMillis();
        if(lastUpdate - curTime > 1000){
//            for(DefSection s : sections){
////
//            }
        }

        lastUpdate = curTime;


        train.getSpeed();

    }


    public Train getTrain(){
        return this.train;
    }

}
