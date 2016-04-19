package model;

import view.Drawable.section_types.DefaultTrack;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Section {

    private double length;
    private Section from;
    private Section to;
    private DefaultTrack[] tracks;
    private int id;
    private boolean trainOn;

    private boolean canDetect;

    public Section(int id, double length, Section from, Section to, DefaultTrack[] tracks){
        this.canDetect = false;
        this.id = id;
        this.length = length;
        this.from = from;
        this.to = to;
        this.tracks = tracks;
    }

    public Section(int id, double length, DefaultTrack[] tracks){
        this.canDetect = false;
        this.id = id;
        this.length = length;
        this.tracks = tracks;
    }

    //TODO needed?
    /**
     * Returns the given section matching the id
     * null if it does not have it
     * */
    public DefaultTrack getTrack(DefaultTrack dt){
        for(DefaultTrack t : tracks){
            if(t.equals(dt)){
//                return
            }
        }
        return null;
    }

    /**
     * Returns if a section is contained within this track
     * */
    public boolean containsTrack(int id){
        for(DefaultTrack s : tracks){
            if(s.getId() == id){
                return true;
            }
        }
        return false;
    }

    public DefaultTrack[] getTracks(){return this.tracks;}

    public void setFrom(Section from){this.from = from;}

    public void setTo(Section to){this.to = to;}

    public double getLength() {return length;}

    public Section getFrom() {return from;}

    public Section getTo() {return to;}

    public boolean getTrainOn(){
        return this.trainOn;
    }

    public void setTrainOn(boolean on){
        this.trainOn = on;
    }

    public int getID() {return id;}

    public String toString(){
        return "Length " + length + " From " + from.getID() + " To " + to.getID() + " Can Detect " + canDetect;
    }

    public void setCandetect(boolean canDetect){
        this.canDetect = canDetect;
    }

    public boolean canDetect(){
        return this.canDetect;
    }
}
