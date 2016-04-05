package view;

import javafx.scene.input.MouseEvent;

/**
 * Created by vanhunick on 5/04/16.
 */
public interface MouseEvents {

    /**
     * Called when the mouse button mouse is pressed
     * */
    public void mousePressed(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse is released
     * */
    public void mouseReleased(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse is clicked
     * */
    public void mouseClicked(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse have moved
     * */
    public void mouseMoved(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse is dragged
     * */
    public void mouseDragged(double x, double y, MouseEvent e );


}
