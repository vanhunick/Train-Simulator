package util;

/**
 * Created by vanhunick on 12/07/16.
 */
public class Point2D {

    public double x;
    public double y;

    public Point2D(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public void setLocation(double x, double y){
        this.x = x;
        this.y = y;
    }

}
