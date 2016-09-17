package simulation.model;

/**
 * Modified by vanhunick on 17/08/16.
 *
 * original from https://github.com/ElvisResearchGroup/ModelRailway/blob/master/src/modelrailway/core/Event.java
 */
public interface Event {

    interface  Listener {
        void notify(Event e);
    }

    // Event for the railway being turned on or off
    final class PowerChanged implements Event {

        private final boolean powerOn;

        public PowerChanged(boolean powerOn) {
            this.powerOn = powerOn;
        }

        public boolean isPowerOn() {
            return powerOn;
        }

        public String toString() {return powerOn ? "Railway was powered on." : "Railway was powered off.";}
    }

    // Event for a section changed event
    final class SectionChanged implements Event {

        private final int section;// The id of the section that generated the event

        private final boolean into; // True is entry false is exit

        public SectionChanged(int section, boolean into) {
            this.section = section;
            this.into = into;
        }

        public boolean getInto() {
            return into;
        }

        public int getSection() {
            return section;
        }

        public String toString() {return into ? "Locomotive moved into Section " + section + "." : "Locomotive moved out of Section " + section + ".";}
    }

    // Event for the speed a train changing
    final class SpeedChanged implements Event {

        private final int locomotive; // The id of the train

        private final float speed; // The speed in percentage

        public SpeedChanged(int locomotive, float speed) {
            this.locomotive = locomotive;
            this.speed = speed;
        }

        public int getLocomotive() {
            return locomotive;
        }

        public float getSpeed() {
            return speed;
        }

        public String toString() {
            int percent = (int) (speed * 100f);
            return "Locomotive " + locomotive + " now moving at speed "
                    + percent + "%.";
        }
    }

    // Event for direction of a train changing
    final class DirectionChanged implements Event {

        private final int locomotive; // The id of the train

        private final boolean direction; // The new direction of the train

        public DirectionChanged(int locomotive, boolean direction) {
            this.locomotive = locomotive;
            this.direction = direction;
        }

        public int getLocomotive() {
            return locomotive;
        }

        public boolean getDirection() {
            return direction;
        }

        public String toString() {
            return direction ? "Locomotive " + locomotive + " now going forwards." : "Locomotive " + locomotive
                    + " going backwards at speed now going backwards.";
        }
    }

    // Event which represents train performing emergency stop
    class EmergencyStop implements Event {

        private final int locomotive; // The trains whos throttle was changed

        public EmergencyStop(int locomotive) {
            this.locomotive = locomotive;
        }

        public int getLocomotive() {
            return locomotive;
        }

        public String toString() {
            return "Locomotive " + locomotive + " performing emergency stop.";
        }
    }

    // Event for toggling a junction
    final class TurnoutChanged implements Event {

        private final int turnout; // The id of the turnout that was changed

        private final boolean thrown; // if the junction is thrown or not

        public TurnoutChanged(int turnout, boolean thrown) {
            this.turnout = turnout;
            this.thrown = thrown;
        }

        public int getTurnout() {
            return turnout;
        }

        public boolean getThrown() {
            return thrown;
        }

        public String toString() {
            return thrown ? "Turnout " + turnout + " thrown." : "Turnout " + turnout + " closed.";
        }
    }
}
