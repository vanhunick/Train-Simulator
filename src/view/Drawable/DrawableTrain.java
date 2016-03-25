package view.Drawable;

import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Train;
import view.Drawable.track_types.DefSection;


/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain implements Drawable{
    private double width = 40;
    private double length = 80;

    private int curX;
    private int curY;
    private Train train;
    private long lastUpdate;
    private List<DefSection> sections;

    public DrawableTrain(Train train, int startX, int startY, List<DefSection> sections){
        this.train = train;
        this.curX = startX;
        this.curY = startY;
        this.sections = sections;
    }

    @Override
    public void draw(GraphicsContext g){
        g.setFill(Color.WHITE);
        g.fillRect(curX, curY, width, length);
    }

    public void update(){
        long curTime = System.currentTimeMillis();
        lastUpdate = curTime;

        for(DefSection s : sections){

        }

    }
}
