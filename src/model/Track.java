package model;

/**
 * Created by vanhunick on 22/03/16.
 */
public class Track {

    private int id;
    private boolean status;

    public Track(int id, boolean status){
        this.id = id;
        this.status = status;
    }

    public int getId(){
        return id;
    }

    public boolean getStatus(){
        return status;
    }

    public void toggleStatus(){
        status = !status;
    }
}
