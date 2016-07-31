package controllers;

/**
 * Created by User on 31/07/2016.
 */
public class ControllerJunction {

    boolean thrown;
    boolean inbound;
    int id;

    public ControllerJunction(int id, boolean inbound, boolean thrown){
        this.id = id;
        this.inbound = inbound;
        this.thrown = thrown;
    }


}
