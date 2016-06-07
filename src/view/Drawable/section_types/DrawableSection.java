package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Section;

/**
 * Created by vanhunick on 16/04/16.
 */
public class DrawableSection {
    private Section section;


    public DrawableSection(Section section){
        this.section = section;
    }


    public void draw(GraphicsContext g){
        for(DefaultTrack track : section.getTracks()){
            if(section.getTrainOn())track.setColor(Color.BLUE);
            else track.setColor(Color.WHITE);
            track.draw(g);
        }
    }

    public DefaultTrack[] getTracks(){return section.getTracks();}

    public Section getSection(){return this.section;}

    public double getInitialX(double width){
        return section.getTracks()[0].getInitialX(width);//Check with the first track in the section
    }

    public double getInitialY(double width){
        return section.getTracks()[0].getInitialY(width);//Check with the first track in the section
    }

    public boolean containsTrack(DefaultTrack track){
        for(DefaultTrack t : section.getTracks()){
            if(t.equals(track))return true;
        }
        return false;
    }

    public DefaultTrack getTrackWithID(int id){
        for(DefaultTrack t : section.getTracks()){
            if (t.getId() == id){
                return t;
            }
        }
        return null;
    }

    public void setSection(Section section){
        this.section = section;
    }

}
