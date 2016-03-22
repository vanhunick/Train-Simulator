package view.Drawable;

import javafx.scene.canvas.GraphicsContext;
import model.Train;

/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableTrain {

    private int curX;
    private int curY;
    private int length;//TODO might just use the train length
    private Train train;

    public DrawableTrain(Train train, int startX, int startY){
        this.train = train;
        this.curX = startX;
        this.curY = startY;
    }

    public void draw(GraphicsContext g){

    }
}
