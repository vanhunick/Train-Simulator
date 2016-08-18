package controllers;

import java.util.List;

/**
 * Created by User on 31/07/2016.
 */
public class ControllerTrain {

        final int id; // The id of the train

        // The direction and orientation of the train
        boolean direction;
        boolean orientation;

        int curSectionID; // The id of the section it is currently on

        int lockCur; // Lock of the current section it is on
        int lockNext; // Lock of the next section it is trying to get to

        int destinationID; // The id of the section that is the destination
        List<Integer> destinationIDs; // List of destination the train should go to
        int curDest; // The current goal for the train to get to

        int srcSection;

        /**
         * Constructor without a destination
         * */
        public ControllerTrain(int id, boolean direction, boolean orientation, int startingSection){
            this.id = id;
            this.direction = direction;
            this.orientation = orientation;
            this.curSectionID = startingSection;
        }

        public void setDestinationList(List<Integer> destinationIDs){
            this.destinationIDs = destinationIDs;
        }
}
