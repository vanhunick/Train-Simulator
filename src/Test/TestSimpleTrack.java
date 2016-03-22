package Test;

import model.Section;
import model.Track;
import model.Train;

/**
 * Created by vanhunick on 22/03/16.
 */
public class TestSimpleTrack {


    public Train  createTrain(){
        return  new Train(5, 20,10, 4, true);
    }

    /**
     * Creates a very basic railway with one track per section
     * */
    public Section[] createTrack(int trackPieces){
        Section[] railway = new Section[trackPieces];

        Section start = new Section(0,10,null,null,null);
        railway[0] = start;
        for(int i = 1; i < railway.length; i++){
            Section s = new Section(i,10,null,null,null);
            s.setFrom(railway[i-1]);
            railway[i] = s;
        }


        for(int i = 0; i < railway.length-1; i++){
            railway[i].setTo(railway[i+1]);
        }

        //link the last one to the start
        railway[railway.length-1].setTo(railway[0]);
        railway[0].setFrom(railway[railway.length-1]);

        return railway;
    }

    public Track createTrack(String type, int id){
        return null;
    }

    public static void main(String[] args){
        TestSimpleTrack t = new TestSimpleTrack();

        Section[] rail = t.createTrack(10);
        for(Section s : rail){
            System.out.println(s);
        }
    }

}
