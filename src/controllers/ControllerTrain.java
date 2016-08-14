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

        // The id of the section it is currently on
        int curSectionID;

        // Lock of the current section it is on
        int lockCur;

        // Lock of the next section it is trying to get to
        int lockNext;


        // The id of the section that is the destination
        int destinationID;

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
        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection, int destinationID){
            this.id = id;
            this.direction = direction;
            this.orientation = orientation;
            this.curSectionID = startingSection;
            this.destinationID = destinationID;
            this.srcSection = curSectionID;
            this.lockNext = -1;
            this.lockCur = -1;
        }
}
