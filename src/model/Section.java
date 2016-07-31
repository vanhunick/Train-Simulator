package model;

import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

import java.util.Arrays;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Section {

    // Length of the section
    private double length;

    //TODO indexes I think
    // Section it came from
    private int from;

    // Section it goes to
    private int to;

    // IDs
    private int toIndex;
    private int fromIndex;
    private int juncSectionIndex;

    // Tracks in the section
    private DefaultTrack[] tracks;

    // The id of the section
    private int id;

    // If there is a train on the track or not
    private boolean trainOn;

    // If it can detect trains or not
    private boolean canDetect;

    // If the section has a junction in it
    private boolean containJunction;



    /**
     * Contructs a Section object
     *
     * @param id the id of the section
     *
     * @param length the length of the section
     *
     * @param from the section it comes from
     *
     * @param to the section it goes to
     *
     * @param tracks the tracks inside the section
     * */
    public Section(int id, double length, int from, int to, DefaultTrack[] tracks){
        this.canDetect = false;
        this.id = id;
        this.length = length;
        this.from = from;
        this.to = to;
        this.tracks = tracks;
    }

    /**
     * Contructs a Section object
     *
     * @param id the id of the section
     *
     * @param length the length of the section
     *
     * @param tracks the tracks inside the section
     * */
    public Section(int id, double length, DefaultTrack[] tracks){
        this.canDetect = false;
        this.id = id;
        this.length = length;
        this.tracks = tracks;
    }

    public int getJuncSectionIndex(){
        return this.juncSectionIndex;
    }

    /**
     * Returns the tracks in the section
     *
     * @return the tracks
     * */
    public DefaultTrack[] getTracks(){return this.tracks;}

    /**
     * Set where the section comes from
     *
     * @param from the section it originates from
     * */
    public void setFrom(int from){this.from = from;}

    /**
     * Set where the section goes to
     *
     * @param to the section it goes to
     * */
    public void setTo(int to){this.to = to;}

    /**
     * Returns the length of the train
     *
     * @return length
     * */
    public double getLength() {return length;}

    /**
     * Returns the section it is from
     *
     * @return from section
     * */
    public int getFrom() {return from;}

    /**
     * Returns the section it goes to
     *
     * @return to section
     * */
    public int getTo() {return to;}


    /**
     *
     * */
    public int getToIndex(){
        if(!containJunction)return toIndex;

        JunctionTrack jt;
        if(tracks[tracks.length-1] instanceof JunctionTrack){
            jt = (JunctionTrack)tracks[tracks.length-1];
        }
        else {
            jt = (JunctionTrack)tracks[0];
        }


        if(jt.getThrown() && !jt.inBound()){
            // The track is thrown so return to id track trown
            return juncSectionIndex;
        }

        return toIndex; // Error
    }

    public int getFromIndex(){
        if(!containJunction)return fromIndex;

        JunctionTrack jt;
        if(tracks[tracks.length-1] instanceof JunctionTrack){
            jt = (JunctionTrack)tracks[tracks.length-1];
        }
        else {
            jt = (JunctionTrack)tracks[0];
        }


        if(jt.getThrown() && jt.inBound()){
            return juncSectionIndex;
        }

        return fromIndex;
    }

    public void setToIndex(int toIndex){
        this.toIndex = toIndex;
    }

    public void setFromIndex(int fromIndex){
        this.fromIndex = fromIndex;
    }

    public void setJuncSectionIndex(int juncID){
        this.juncSectionIndex = juncID;
    }

    public JunctionTrack getJunction(){
        JunctionTrack jt = null;
        if(tracks[tracks.length-1] instanceof JunctionTrack){
            jt = (JunctionTrack)tracks[tracks.length-1];
        }
        else {
            jt = (JunctionTrack)tracks[0];
        }
        return jt;
    }

    /**
     * If a section contains a junction track it means it can go to two different sections
     * depending on the state of the junction inside the section
     * */
    public void setHasJunctionTrack(boolean hasJunction){
        containJunction = hasJunction;
    }

    public boolean hasJunctionTrack(){
        return containJunction;
    }

    /**
     * Returns if a train is on the section or not
     *
     * @return train on
     * */
    public boolean getTrainOn(){
        return this.trainOn;
    }

    /**
     * Set if a train is on the section or not
     *
     * @param on train on the section
     * */
    public void setTrainOn(boolean on){
        this.trainOn = on;
    }

    /**
     * Returns the id of the section
     *
     * @return section id
     * */
    public int getID() {return id;}

    /**
     * Set if it can detect or not
     *
     * @param canDetect if it can detect
     * */
    public void setCandetect(boolean canDetect){
        this.canDetect = canDetect;
    }

    /**
     * Return weather it can or cant detect
     *
     * @return canDetect
     * */
    public boolean canDetect(){
        return this.canDetect;
    }

    public String toString(){
        return "Length " + length + " From " + from + " To " + to + " Can Detect " + canDetect;//TODO from and to should access array it is not the id but position in the array
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Section section = (Section) o;

        if (Double.compare(section.length, length) != 0) return false;
        if (from != section.from) return false;
        if (to != section.to) return false;
        if (toIndex != section.toIndex) return false;
        if (fromIndex != section.fromIndex) return false;
        if (juncSectionIndex != section.juncSectionIndex) return false;
        if (id != section.id) return false;
        if (trainOn != section.trainOn) return false;
        if (canDetect != section.canDetect) return false;
        if (containJunction != section.containJunction) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(tracks, section.tracks);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(length);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + from;
        result = 31 * result + to;
        result = 31 * result + toIndex;
        result = 31 * result + fromIndex;
        result = 31 * result + juncSectionIndex;
        result = 31 * result + Arrays.hashCode(tracks);
        result = 31 * result + id;
        result = 31 * result + (trainOn ? 1 : 0);
        result = 31 * result + (canDetect ? 1 : 0);
        result = 31 * result + (containJunction ? 1 : 0);
        return result;
    }
}
