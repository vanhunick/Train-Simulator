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
import view.Drawable.Drawable;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.*;
import view.Panes.TrainDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Visualisation implements MouseEvents {

    private ModelTrack modelTrack;
    private boolean started;

    private List<DrawableTrain> trains;
    private List<DefSection> railway;

    private VBox vBox;

    public Visualisation(){
        this.vBox = getBuilderButtons();
        //Just the default track
        this.trains = new ArrayList<>();
        this.railway = createBasicTrack();
    }

    public void update(){
        for(DrawableTrain t : trains){
            t.update();
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
        Button clear = new Button("Clear Track");

        //Starts the simulation
        sim.setOnAction(e -> startSimulation());
        //TODO add some buttons later
        vBox.getChildren().addAll(sim);

        return vBox;
    }

    public void startSimulation(){
        testMovement();//Just creates some trains

        this.modelTrack = new ModelTrack(getTrains(), new TrackBuilder(null).linkUpSections(railway));



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
            if(ds.getDrawID() == 1){
                //Create the train
                Train train = new Train(1,50,0,1,true);

                // Create the drawable train
                DrawableTrain drawableTrain = new DrawableTrain(train, ds);
                trains.add(drawableTrain);
            }
        }
    }



    public ArrayList<DefSection> createBasicTrack(){
        ArrayList<DefSection> sections = new ArrayList<>();

        DefSection ds1 = new StraightHoriz(new Section(2, 100, null, null, null), 200, 100, 100,0, "RIGHT");
        DefSection ds2 = new StraightHoriz(new Section(2, 100, null, null, null), 100,0);
        ds2.setStart(ds1);

        DefSection ds3 = new Quart2(new Section(2, 100, null, null, null), 200,2);
        ds3.setStart(ds2);

        DefSection ds4 = new StraightVert(new Section(2, 100, null, null, null), 100,5);
        ds4.setStart(ds3);

        DefSection ds5 = new Quart3(new Section(2, 100, null, null, null), 200,3);
        ds5.setStart(ds4);

        DefSection ds6 = new StraightHoriz(new Section(2, 100, null, null, null), 100,0);
        ds6.setStart(ds5);

        DefSection ds7 = new StraightHoriz(new Section(2, 100, null, null, null), 100,0);
        ds7.setStart(ds6);

        DefSection ds8 = new Quart4(new Section(2, 100, null, null, null), 200,4);
        ds8.setStart(ds7);

        DefSection ds9 = new StraightVert(new Section(2, 100, null, null, null), 100,5);
        ds9.setStart(ds8);

        DefSection ds10 = new Quart1(new Section(2, 100, null, null, null), 200,1);
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
