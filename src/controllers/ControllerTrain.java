package controllers;

/**
 * Created by User on 31/07/2016.
 */
public class ControllerTrain {

        // The id of the train
        int id;

        // The direction and orientation of the train
        boolean direction;
        boolean orientation;

        // The id of the train it is currently on
        int curSection;

        // Lock of the current section it is on
        int lockCur;

        // Lock of the next section it is trying to get to
        int lockNext;


        int destinationSection;

        int srcSection;


        /**
         * Creates a controller train
         *
         * @param id the id of the train
         *
         * @param direction the direction the train is going
         *
         * @param orientation if the train is going along the natural orientation or against
         * */
        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection, int destinationSection){
            this.id = id;
            this.direction = direction;
            this.orientation = orientation;
            this.curSection = startingSection;
            this.destinationSection = destinationSection;
            this.srcSection = curSection;
            this.lockNext = -1;
            this.lockCur = -1;
        }
}
