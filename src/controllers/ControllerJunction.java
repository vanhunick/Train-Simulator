package controllers;

/**
 * Created by User on 31/07/2016.
 */
public class ControllerJunction {

    boolean thrown;
    final boolean inbound;
    final int id;

    /**
     * Constructs a representation of a junction for the controller
     *
     * @param id the id of the junction
     *
     * @param inbound if the junction is inbound or outbound
     *
     * @param thrown if the junction is thrown or not
     * */
    public ControllerJunction(int id, boolean inbound, boolean thrown){
        this.id = id;
        this.inbound = inbound;
        this.thrown = thrown;
    }
}
