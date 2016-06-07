package save;

import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.DrawableSection;

import java.util.List;

/**
 * Created by vanhunick on 7/06/16.
 */
public class LoadedRailway{
    public DrawableSection[] sections;
    public DefaultTrack[] tracks;
    public List<DrawableTrain> trains;
    public List<DrawableRollingStock> stocks;

    public LoadedRailway(DrawableSection[] sections, DefaultTrack[] tracks, List<DrawableTrain> trains, List<DrawableRollingStock> stocks){
        this.sections = sections;
        this.tracks = tracks;
        this.trains = trains;
        this.stocks = stocks;
    }
}
