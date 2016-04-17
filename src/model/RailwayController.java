package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanhunick on 12/04/16.
 */
public class RailwayController {

    private List<Train> trains;
    private List<ControlSection> tracks;

    private ModelTrack model;

    public RailwayController(List<Train> trains, List<Section> tracks, ModelTrack model){
        this.trains = trains;
        this.tracks = createControlTracks(tracks);
        this.model = model;
    }

    public void sectionChangedEvent(int sectionID){
        for(Train t : trains){
            if(t.getPosition() == sectionID){

                // Grab the section
                int nextSectionID = getNextSection(sectionID);



            }
        }
    }

    public int getNextSection(int curSection){
        for(ControlSection cs : tracks){
            if(cs.getId() == curSection)return cs.getTo();
        }
        return -1;// Should never get here
    }

    public boolean checkTrainOnSection(int sectionID){
        for(Train t : trains){
            if(t.getPosition() == sectionID)return true;
        }
        return false;
    }

    public List<ControlSection> createControlTracks(List<Section> track){
        List<ControlSection> sections = new ArrayList<>();

        for(Section s : track){
            sections.add(new ControlSection(s.getID(),s.getFrom().getID(),s.getTo().getID()));
        }
        return sections;
    }




    private class ControlSection{
        private int id;
        private int from;
        private int to;

        public ControlSection(int id, int from, int to){
            this.id = id;
            this.from = from;
            this.to = to;
        }

        public int getId(){return this.id;}
        public int getFrom(){return this.from;}
        public int getTo(){return this.to;}

    }
}

