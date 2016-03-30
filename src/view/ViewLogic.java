package view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import view.Drawable.DrawableSection;
import view.Drawable.DrawableTrain;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vanhunick on 22/03/16.
 */
public class ViewLogic {

    ArrayList<DrawableTrain> trains;
    ArrayList<DrawableSection> railway;

    TrackBuilder trackBuilder;

    public ViewLogic(ArrayList<DrawableTrain> trains, ArrayList<DrawableSection> railWay){
        this.trains = trains;
        this.railway = railWay;
    }

    public void update(){
        for(DrawableTrain t : trains){
            t.update();
        }
    }

    public void refresh(GraphicsContext g){
        for(DrawableTrain t : trains){
            t.draw(g);
        }

        //g.setFill(Color.WHITE);
        //g.fillRect(400, 250, 100, 100);
    }


    public void mousePressed(double x, double y){

    }

    public void mouseReleased(double x, double y){

    }

    public void mouseClicked(double x, double y){
        trackBuilder.mouseClicked(x,y);
    }

    public void mouseMoved(double x, double y){

    }

    public void mouseDragged(double x, double y){

    }

    public void addTrakcBuilder(TrackBuilder tb){
        this.trackBuilder = tb;
    }

}
