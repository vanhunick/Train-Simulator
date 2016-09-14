package simulation.Drawable.section_types;

import javafx.scene.canvas.GraphicsContext;
import model.Section;

/**
 * Created by vanhunick on 16/04/16.
 */
public class DrawableSection {

    private Section section; // Section the drawable section represents

    /**
     * Constructs DrawableSection object
     *
     * @param section Section to draw
     * */
    public DrawableSection(Section section){
        this.section = section;
    }

    /**
     * Draws the section on the graphics context
     * */
    public void draw(GraphicsContext g){
        for(DefaultTrack track : section.getTracks()){
            track.draw(g);
        }
    }

    /**
     * Returns the tracks in the section
     * */
    public DefaultTrack[] getTracks(){return section.getTracks();}

    /**
     * Returns the section
     * */
    public Section getSection(){return this.section;}

    /**
     * Returns true if the track is in the section false if not
     * */
    public boolean containsTrack(DefaultTrack track){
        for(DefaultTrack t : section.getTracks()){
            if(t.equals(track))return true;
        }
        return false;
    }

    /**
     * Returns the track in the section given the id.
     * null if it does not exist
     * */
    public DefaultTrack getTrackWithID(int id){
        for(DefaultTrack t : section.getTracks()){
            if (t.getId() == id){
                return t;
            }
        }
        return null;
    }

    /**
     * Sets the section for to draw
     * */
    public void setSection(Section section){
        this.section = section;
    }
}
