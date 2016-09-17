package tests;

import org.junit.Test;
import simulation.Drawable.tracks.*;

/**
 * Created by User on 23/07/2016.
 */
public class TrackConnectionTests {


    /**
     * Returns a new track of the type selected in the track panel
     */
    public DefaultTrack getTrackWithID(int id, String direction) {
        int x = 200;
        int y = 200;
        double pieceSize = 100;
        int curId = 1;
        DefaultTrack[] trackChoices = new DefaultTrack[]{
                new StraightHoriz(x, y, (int) pieceSize, 0, curId, direction),
                new Quart1(x, y, (int) pieceSize * 2, 1, direction, curId),
                new Quart2(x, y, (int) pieceSize * 2, 2, direction, curId),
                new Quart3(x, y, (int) pieceSize * 2, 3, direction, curId),
                new Quart4(x, y, (int) pieceSize * 2, 4, direction, curId),
                new StraightVert(x, y, (int) pieceSize, 5, direction, curId),
                new JunctionTrack(x, y, (int) pieceSize, 6, curId, direction, false, true, "UP"),
                new JunctionTrack(x, y, (int) pieceSize, 6, curId, direction, false, false, "UP"),
                new JunctionTrack(x, y, (int) pieceSize, 6, curId, direction, false, true, "DOWN"),
                new JunctionTrack(x, y, (int) pieceSize, 6, curId, direction, false, false, "DOWN")
        };

        return trackChoices[id];
    }

    @Test
    public void straightFromQ1Right() {
        DefaultTrack straightTrack = new StraightHoriz(200, 0, 1);
        DefaultTrack q1 = getTrackWithID(1, "RIGHT");
        straightTrack.setStart(q1);

        assert straightTrack.getStartX() == q1.getStartX() + q1.getLength() / 2;
        assert straightTrack.getStartY() == q1.getStartY();
    }

    @Test
    public void straightFromStraightRight() {
        DefaultTrack straightTrack = new StraightHoriz(200, 0, 1);
        DefaultTrack straightTrack1 = getTrackWithID(0, "RIGHT");
        straightTrack.setStart(straightTrack1);

        assert straightTrack.getStartX() == straightTrack1.getStartX() + straightTrack1.getLength();
        assert straightTrack.getStartY() == straightTrack1.getStartY();
    }

    @Test
    public void straightFromQ4Right() {
        DefaultTrack straightTrack = new StraightHoriz(200, 0, 1);
        DefaultTrack q4 = getTrackWithID(4, "RIGHT");
        straightTrack.setStart(q4);

        assert straightTrack.getStartX() == q4.getStartX() + q4.getLength() / 2;
        assert straightTrack.getStartY() == q4.getStartY() + q4.getLength() - DefaultTrack.TRACK_WIDTH;
    }


    @Test
    public void straightFromStraightLeft() {
        DefaultTrack straightTrack = new StraightHoriz(200, 0, 1);
        DefaultTrack st = getTrackWithID(0, "LEFT");
        straightTrack.setStart(st);

        assert straightTrack.getStartX() == st.getStartX() - straightTrack.getLength();
        assert straightTrack.getStartY() == st.getStartY();
    }

    @Test
    public void straightFromQ2Left() {
        DefaultTrack straightTrack = new StraightHoriz(200, 0, 1);
        DefaultTrack q2 = getTrackWithID(2, "LEFT");
        straightTrack.setStart(q2);

        assert straightTrack.getStartX() == (q2.getStartX() + q2.getLength() / 2) - straightTrack.getLength();
        assert straightTrack.getStartY() == q2.getStartY();
    }

    @Test
    public void straightFromQ3Left() {
        DefaultTrack straightTrack = new StraightHoriz(200, 0, 1);
        DefaultTrack q3 = getTrackWithID(3, "LEFT");
        straightTrack.setStart(q3);

        assert straightTrack.getStartX() == (q3.getStartX() + q3.getLength() / 2) - straightTrack.getLength();
        assert straightTrack.getStartY() == (q3.getStartY() + q3.getLength()) - DefaultTrack.TRACK_WIDTH;
    }
}