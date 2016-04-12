package model;

import java.util.List;

/**
 * Created by vanhunick on 12/04/16.
 */
public class RailwayController {

    private List<Train> trains;
    private List<Section> tracks;

    private ModelTrack model;

    public RailwayController(List<Train> trains, List<Section> tracks, ModelTrack model){
        this.trains = trains;
        this.tracks = tracks;
        this.model = model;
    }

    public void sectionChangedEvent(int sectionID){
        for(Train t : trains){
            if(t.getPosition() == sectionID){

                // Grab the section
                Section s = getSection(sectionID);

                if(s.getTrainOn()){//TODO not sure if should be after it has changed or before
                    // Not there is a train
                }
            }
        }
    }



    public Section getSection(int id){
        for(Section s : tracks){
            if(s.getID() == id){
                return s;
            }
        }
        return null;//Throw error
    }
}

