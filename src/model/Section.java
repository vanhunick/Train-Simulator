package model;

import view.Drawable.section_types.DefaultTrack;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Section {

    // Length of the section
    private double length;

    // Section it came from
    private Section from;

    // Section it goes to
    private Section to;

    // Tracks in the section
    private DefaultTrack[] tracks;

    // The id of the section
    private int id;

    // If there is a train on the track or not
    private boolean trainOn;

    // If it can detect trains or not
    private boolean canDetect;

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
    public Section(int id, double length, Section from, Section to, DefaultTrack[] tracks){
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
    public void setFrom(Section from){this.from = from;}

    /**
     * Set where the section goes to
     *
     * @param to the section it goes to
     * */
    public void setTo(Section to){this.to = to;}

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
    public Section getFrom() {return from;}

    /**
     * Returns the section it goes to
     *
     * @return to section
     * */
    public Section getTo() {return to;}

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
        return "Length " + length + " From " + from.getID() + " To " + to.getID() + " Can Detect " + canDetect;
    }
}
