package util.save;

import simulation.Drawable.DrawableRollingStock;
import simulation.Drawable.DrawableTrain;
import simulation.Drawable.tracks.DefaultTrack;
import simulation.Drawable.tracks.DrawableSection;

import java.io.File;
import java.util.List;

/**
 * Created by vanhunick on 7/06/16.
 */
public class LoadedRailway{
    public File file;
    public DrawableSection[] sections;
    public DefaultTrack[] tracks;
    public List<DrawableTrain> trains;
    public List<DrawableRollingStock> stocks;

    public LoadedRailway(File file, DrawableSection[] sections, DefaultTrack[] tracks, List<DrawableTrain> trains, List<DrawableRollingStock> stocks){
        this.file = file;
        this.sections = sections;
        this.tracks = tracks;
        this.trains = trains;
        this.stocks = stocks;
    }
}
