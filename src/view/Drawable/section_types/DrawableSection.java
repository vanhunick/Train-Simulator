package view.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import model.Section;
import view.Drawable.track_types.Track;

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

    public void setSection(Section section){
        this.section = section;
    }

}
