package simulation;

import javafx.scene.input.MouseEvent;

/**
 * Created by vanhunick on 5/04/16.
 */
public interface MouseEvents {

    /**
     * Called when a key is pressed
     * */
    void keyPressed(String code);

    /**
     * Called when the mouse button mouse is pressed
     * */
    void mousePressed(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse is released
     * */
    void mouseReleased(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse is clicked
     * */
    void mouseClicked(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse have moved
     * */
    void mouseMoved(double x, double y, MouseEvent e );

    /**
     * Called when the mouse button mouse is dragged
     * */
    void mouseDragged(double x, double y, MouseEvent e );
}
