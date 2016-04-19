package Util;

/**
 * Created by vanhunick on 19/04/16.
 */
public class DrawUtil {





    public double lengthOfQuater(double length, double trackwidth){
        double radius = (length-trackwidth/2)/2;
        double circumference = 2 * Math.PI * radius;
        return circumference/4;
    }

}
