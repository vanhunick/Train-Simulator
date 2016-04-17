package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import model.Section;
import view.Drawable.track_types.Track;

/**
 * Created by vanhunick on 14/04/16.
 */
public class JunctionTrack extends DefaultTrack {

    private static  final int TRACK_WIDTH = 40;

    private DefaultTrack fromTrack;
    private DefaultTrack toThrownTrack;
    private DefaultTrack toNotThrownTrack;

    private boolean thrown;

    /**
     * Constructor for a piece that connects to another piece
     * */
    public JunctionTrack(int length, int drawID, int id,boolean thrown){
        super(length, drawID,id);
        this.thrown = thrown;
    }

    /**
     * Constructor for the starting piece
     * */
    public JunctionTrack(int startX, int startY, int length, int drawID,int id, String direction, boolean thrown){
        super(startX,startY,length,drawID,id, direction);
        this.thrown = thrown;
    }


    public double getLength() {
        return super.getLength();
    }

    public DefaultTrack getFromTrack() {
        return fromTrack;
    }

    public DefaultTrack getToThrownTrack() {
        return toThrownTrack;
    }


    @Override
    public void draw(GraphicsContext g){

    }


    public DefaultTrack getFrom() {
        return fromTrack;
    }


    public void setFrom(DefaultTrack from){
        //even though from can be from multiple pieces it does not matter from this tracks perspective
        this.fromTrack = from;
    }

    public DefaultTrack getTo() {
        //TODO not sure if it valid to do this in here
        if(thrown){
            return toThrownTrack;
        }
        else {
            return toNotThrownTrack;
        }
    }

    public DefaultTrack getToNotThrownTrack() {
        return toNotThrownTrack;
    }

    public void setToThrownTrack(DefaultTrack toThrownTrack) {
        this.toThrownTrack = toThrownTrack;
    }

    public void setToNotThrownTrack(DefaultTrack toNotThrownTrack) {
        this.toNotThrownTrack = toNotThrownTrack;
    }

    public void setFromTrack(DefaultTrack fromTrack) {
        this.fromTrack = fromTrack;
    }
}
