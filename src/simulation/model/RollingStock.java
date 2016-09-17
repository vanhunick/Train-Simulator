package simulation.model;

/**
 * Created by vanhunick on 21/04/16.
 */
public class RollingStock {

    // Length of rolling stock
    private double length;
    private double width = 4;
//    private double weight;

    // The id of the rolling stock
    private int rollID;

    private double weight;

    /**
     * Creates a new rolling stock object
     *
     * @param length the length of the rolling stock
     *
     * @param rollID the id of the rolling stock
     * */
    public RollingStock(double length, int rollID, double weight){
        this.length = length;
        this.rollID = rollID;
        this.weight = weight;
    }

    public double getWeight(){
        return this.weight;
    }

    /**
     * Returns the length of the rolling stock
     *
     * @return the length of the rolling stock
     * */
    public double getLength(){
        return this.length;
    }

    public double getWidth(){
        return this.width;
    }

    /**
     * Returns the length of the rolling stock
     *
     * @return the id of the rolling stock
     * */
    public int getRollID(){
        return this.rollID;
    }
}
