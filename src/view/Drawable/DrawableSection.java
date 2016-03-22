package view.Drawable;

import javafx.scene.canvas.GraphicsContext;
import model.Section;

/**
 * Created by Nicky van Hulst on 22/03/16.
 */
public class DrawableSection implements Drawable{
    private int curX;
    private int curY;
    private int length;//TODO might just use the train length
    private double angle;
    private Section section;

    public DrawableSection(Section section, int startX, int startY, double angle){
        this.section = section;
        this.curX = startX;
        this.curY = startY;
        this.angle = angle;
    }



    @Override
    public void draw(GraphicsContext g){

    }
}
