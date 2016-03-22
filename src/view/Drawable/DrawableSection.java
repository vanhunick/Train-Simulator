package view.Drawable;

import javafx.scene.canvas.GraphicsContext;
import model.Section;

/**
 * Created by vanhunick on 22/03/16.
 */
public class DrawableSection {
    private int curX;
    private int curY;
    private int length;//TODO might just use the train length
    private Section trainSection;

    public DrawableSection(Section section, int startX, int startY){
        this.trainSection = section;
        this.curX = startX;
        this.curY = startY;
    }

    public void draw(GraphicsContext g){

    }
}
