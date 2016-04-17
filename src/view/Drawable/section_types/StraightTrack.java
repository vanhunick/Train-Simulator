package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import view.Drawable.track_types.Track;

/**
 * Created by vanhunick on 22/03/16.
 */
public class StraightTrack implements Track {

    private int id;
    private double length;
    private Track from;
    private Track to;

    public StraightTrack(int id, double length, Track from, Track to){
        this.id = id;
        this.length = length;
        this.from = from;
        this.to = to;
    }

    public StraightTrack(int id, double length){
        this.id = id;
        this.length = length;
    }



    public int getId(){
        return id;
    }
//
//    public boolean getStatus(){
//        return status;
//    }
//
//    public void toggleStatus(){
//        status = !status;
//    }

    @Override
    public double getLength(){return this.length;}


    @Override
    public Track getFrom() {
        return from;
    }

    @Override
    public Track getTo() {
        return to;
    }

    @Override
    public void draw(GraphicsContext gc) {

    }

    public void setFrom(Track from){
        this.from = from;
    }

    @Override
    public int getID() {
        return this.id;
    }

    public void setTo(Track to){
        this.to = to;
    }


}
