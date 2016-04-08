package view;

import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.ModelTrack;
import model.Section;
import model.Train;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Visualisation implements MouseEvents {

    private ModelTrack modelTrack;
    private boolean started = false;

    private List<DrawableTrain> trains;
    private List<DefSection> railway;

    private VBox vBox;

    private long lastUpdate;

    public Visualisation(){
        this.vBox = getBuilderButtons();
        //Just the default track
        this.trains = new ArrayList<>();
        this.railway = createBasicTrack();
    }

    public void update(){
        if(started){
                for(DrawableTrain t : trains){
                    onSectionCheck(t);
                    t.update();
                }
        }
    }

    public void refresh(GraphicsContext g){
        g.setStroke(Color.WHITE);

        // Draw the track
        for(DefSection d : railway){
            d.draw(g);
        }

        //Draw the trains
        for(DrawableTrain t : trains){
            t.draw(g);
        }
    }

    public void onSectionCheck(DrawableTrain t){
        DefSection curSection = t.getCurSection();
        double speed = t.getTrain().getSpeed();

        if(lastUpdate == 0){
            lastUpdate = System.currentTimeMillis();
        }

        long curTime = System.currentTimeMillis();
        long timeChanged = curTime - lastUpdate;
        timeChanged = 20;
        double pixelsToMove = (timeChanged/1000.0)*speed;
        lastUpdate = System.currentTimeMillis();

        //TEMP CODE
        boolean q2 = true;
        if(curSection instanceof Quart2){
            Quart2 q = (Quart2)curSection;
            q2 = q.checkOnAfterUpdate(t.lastPointOnCurve,pixelsToMove);
        }

        if(curSection instanceof Quart3){
            Quart3 q = (Quart3)curSection;
            q2 = q.checkOnAfterUpdate(t.lastPointOnCurve,pixelsToMove);
        }

        if(curSection instanceof Quart4){
            Quart4 q = (Quart4)curSection;
            q2 = q.checkOnAfterUpdate(t.lastPointOnCurve,pixelsToMove);
        }

        if(curSection instanceof Quart1){
            Quart1 q = (Quart1)curSection;

            q2 = q.checkOnAfterUpdate(t.lastPointOnCurve,pixelsToMove);
        }


        if(!q2 || !curSection.checkOnSectionAfterMovement(t.getX(),t.getY(),pixelsToMove)){
            System.out.println("Changing drawplace");
            for(int i = 0; i < railway.size(); i++){

                // Check if the current track
                if(railway.get(i).getSection().getID() == curSection.getSection().getID()){
                    if(i == railway.size() -1)t.setCurSection(railway.get(0));// The cur section is the last in the track so set the next to be the start section
                    else {
                        t.setCurSection(railway.get(i+1));
                        //make the model generate an event
                    }
                    return;
                }
            }
        }

    }

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {

    }

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {

    }

    @Override
    public void mouseClicked(double x, double y, MouseEvent e) {

    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e) {

    }

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {

    }

    public void setRailway(List<DefSection> rail){
        this.railway = rail;
    }

    public void addTrain(DrawableTrain train){
        this.trains.add(train);
    }

    public void setTrains(List<DrawableTrain> trains){
        this.trains = trains;
    }

    private VBox getBuilderButtons(){
        VBox vBox = new VBox(8); // spacing = 8
        vBox.setPadding(new Insets(5,5,5,5));
        Button sim = new Button("Start Simulation");
        Button stop = new Button("Stop");
        Button pause = new Button("Pause");

        //Starts the simulation
        sim.setOnAction(e -> startSimulation());
        stop.setOnAction(e -> stopSimulation());
        pause.setOnAction(e -> pause());
        //TODO add some buttons later
        vBox.getChildren().addAll(sim,stop, pause);

        return vBox;
    }

    public void stopSimulation(){
        started = false;
    }

    public void pause(){
        started = false;
    }

    public void startSimulation(){
        started = true;

        this.modelTrack = new ModelTrack(getTrains(), new TrackBuilder(null).linkUpSections(railway));
        lastUpdate = System.currentTimeMillis();
    }

    public List<Train> getTrains(){
        List<Train> trains = new ArrayList<>();

        for(DrawableTrain dt : this.trains){
            trains.add(dt.getTrain());
        }
        return trains;
    }

    public void addUIElementsToLayout(BorderPane bp){
        bp.setLeft(vBox);
    }

    public void removeUIElementsFromLayout(BorderPane bp){
        bp.getChildren().remove(vBox);
    }



    public void testMovement(){

        // Add a train to the track
        for(DefSection ds : railway){
            if(ds.getDrawID() == 0){
                //Create the train
                Train train = new Train(1,50,120,1,true);

                // Create the drawable train
                DrawableTrain drawableTrain = new DrawableTrain(train, ds);
                trains.add(drawableTrain);
                return;
            }
        }
    }



    public ArrayList<DefSection> createBasicTrack(){
        ArrayList<DefSection> sections = new ArrayList<>();

        DefSection ds1 = new StraightHoriz(new Section(1, 100, null, null, null), 200, 100, 100,0, "RIGHT");
        DefSection ds2 = new StraightHoriz(new Section(2, 100, null, null, null), 100,0);
        ds2.setStart(ds1);

        DefSection ds3 = new Quart2(new Section(3, 100, null, null, null), 200,2);
        ds3.setStart(ds2);

        DefSection ds4 = new StraightVert(new Section(4, 100, null, null, null), 100,5);
        ds4.setStart(ds3);

        DefSection ds5 = new Quart3(new Section(5, 100, null, null, null), 200,3);
        ds5.setStart(ds4);

        DefSection ds6 = new StraightHoriz(new Section(6, 100, null, null, null), 100,0);
        ds6.setStart(ds5);

        DefSection ds7 = new StraightHoriz(new Section(7, 100, null, null, null), 100,0);
        ds7.setStart(ds6);

        DefSection ds8 = new Quart4(new Section(8, 100, null, null, null), 200,4);
        ds8.setStart(ds7);

        DefSection ds9 = new StraightVert(new Section(9, 100, null, null, null), 100,5);
        ds9.setStart(ds8);

        DefSection ds10 = new Quart1(new Section(10, 100, null, null, null), 200,1);
        ds10.setStart(ds9);

        sections.add(ds1);
        sections.add(ds2);
        sections.add(ds3);
        sections.add(ds4);
        sections.add(ds5);
        sections.add(ds6);
        sections.add(ds7);
        sections.add(ds8);
        sections.add(ds9);
        sections.add(ds10);

        return sections;
    }

}
