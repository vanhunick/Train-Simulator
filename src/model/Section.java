package model;

import view.Drawable.section_types.DefaultTrack;
import view.Drawable.section_types.JunctionTrack;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Section {

    // Length of the section
    private double length;

    // Section it came from
    private int from;

    // Section it goes to
    private int to;

    private int toId;
    private int fromId;
    int toJuncSectionID;

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
    public int getToID(){
        if(!containJunction)return toId;

        JunctionTrack jt;
        if(tracks[tracks.length-1] instanceof JunctionTrack){
            jt = (JunctionTrack)tracks[tracks.length-1];
        }
        else {
            jt = (JunctionTrack)tracks[0];
        }


        if(jt.getThrown() && !jt.inBound()){
            // The track is thrown so return to id track trown
            return toJuncSectionID;
        }

        return toId; // Error
    }

    public int getFromID(){
        if(!containJunction)return  fromId;

        JunctionTrack jt;
        if(tracks[tracks.length-1] instanceof JunctionTrack){
            jt = (JunctionTrack)tracks[tracks.length-1];
        }
        else {
            jt = (JunctionTrack)tracks[0];
        }


        if(jt.getThrown() && jt.inBound()){
            return toJuncSectionID;
        }
        return fromId;
    }

    public void setToId(int toId){
        this.toId = toId;
    }

    public void setFromId(int fromId){
        this.fromId = fromId;
    }

    public void setToJuncSectionID(int juncID){
        this.toJuncSectionID = juncID;
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


}
