package model;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Section {

    private double length;
    private Section from;
    private Section to;
    private Track[] tracks;
    private int id;

    public Section(int id, double length, Section from, Section to, Track[] tracks){
        this.id = id;
        this.length = length;
        this.from = from;
        this.to = to;
        this.tracks = tracks;
    }

    /**
     * Returns the given section matching the id
     * null if it does not have it
     * */
    public Track getSection(int id){
        for(Track s : tracks){
            if(s.getId() == id){
                return s;
            }
        }
        return null;
    }

    /**
     * Returns if a section is contained within this track
     * */
    public boolean containsSection(int id){
        for(Track s : tracks){
            if(s.getId() == id){
                return true;
            }
        }
        return false;
    }


    public void setFrom(Section from){this.from = from;}

    public void setTo(Section to){this.to = to;}

    public double getLength() {return length;}

    public Section getFrom() {return from;}

    public Section getTo() {return to;}

    public int getID() {return id;}

    public String toString(){
        return "Length " + length + " From " + from.getID() + " To " + to.getID();
    }
}
