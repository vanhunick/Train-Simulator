package view;

import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Section;
import model.Train;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.*;
import view.Panes.TrainDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by vanhunick on 22/03/16.
 */
public class Visualisation {

    private ArrayList<DrawableTrain> trains;
    private List<DefSection> railway;

    private VBox vBox;
    private double widthOffset;
    private double heightOffset;

    TrackBuilder trackBuilder;//TODO change location later

    public Visualisation(TrackBuilder tb){
        this.trackBuilder = tb;
        this.vBox = getBuilderButtons();
        //Just the default track
        this.trains = new ArrayList<>();
        this.railway = createBasicTrack();
    }

    public void update(){
//        for(DrawableTrain t : trains){
//            t.update();
//        }
    }

    public void draw(GraphicsContext g){

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

    public void setOffesets(double width, double height){
        this.widthOffset = width;
        this.heightOffset = height;
    }

    public void mousePressed(double x, double y){

    }

    public void mouseReleased(double x, double y){

    }

    public void mouseClicked(double x, double y, MouseEvent e){
        trackBuilder.mouseClicked(x,y,e);
    }

    public void mouseMoved(double x, double y){
        System.out.println(widthOffset + "  " + heightOffset);
        trackBuilder.mouseMoved(x - widthOffset, y - heightOffset);
    }

    public void mouseDragged(double x, double y){

    }

    public void addTrakcBuilder(TrackBuilder tb){
        this.trackBuilder = tb;
    }

    public void setRailway(List<DefSection> rail){
        this.railway = rail;
    }

    public void addTrain(DrawableTrain train){
        this.trains.add(train);
    }

    private VBox getBuilderButtons(){
        VBox vBox = new VBox(8); // spacing = 8
        vBox.setPadding(new Insets(5,5,5,5));
        Button sim = new Button("Simulate Track");
        Button clear = new Button("Clear Track");
        Button addTrainMenu = new Button("Add Train");

        addTrainMenu.setOnAction(e -> showAddTrainMenu());


        vBox.getChildren().addAll(addTrainMenu);

        return vBox;
    }

    public void showAddTrainMenu(){
        TrainDialog td = new TrainDialog();


        Train t = new Train(td.getId(),td.getLength(),0,td.getStartId(),true);//TODO make some checks for same id
//        DrawableTrain dt = new DrawableTrain();

    }




    public void addUIElementsToLayout(BorderPane bp){
        bp.setLeft(vBox);
        widthOffset  = bp.getLeft().getLayoutBounds().getWidth();
        heightOffset = bp.getTop().getLayoutBounds().getHeight();
    }

    public void removeUIElementsFromLayout(BorderPane bp){
        bp.getChildren().remove(vBox);
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
