package view;

import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Section;
import model.Train;
import view.Drawable.DrawableTrain;
import view.Drawable.track_types.*;
import view.Panes.ErrorDialog;
import view.Panes.TrackMenu;
import view.Panes.TrainDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/03/2016.
 */
public class TrackBuilder implements MouseEvents{
    public static final int NUMB_PIECES = 6;

    private int selectedBox;
    private double boxSize;


    private double pieceSize = 100;
    private double trackStartX = 300;
    private double trackStartY = 80;


    private double shownPanelStartX;

    private double screenWidth;
    private double screenHeight;

    private List<DefSection> sections;
    private List<DefSection> sectionsForTrack;
    private List<DrawableTrain> trains;

    private double boxGap = 10;

    private VBox vBox;

    private ProgramController controller;
    private int curId = 0;

    private boolean alternate = true;

    public TrackBuilder(ProgramController controller){
        this.controller = controller;
        this.vBox = getBuilderButtons();

        this.sectionsForTrack = new ArrayList<>();
        this.trains = new ArrayList<>();

        this.shownPanelStartX = screenWidth - boxSize - boxGap*2;

        this.shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.sections = setUpDrawPieces();
    }

    public void updateSize(){
        this.screenWidth = controller.getCanvasWidth();
        this.screenHeight = controller.getCanvasHeight();

        this.shownPanelStartX = screenWidth - boxSize - boxGap*2;

        shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.sections = setUpDrawPieces();
    }

    public void update(){

    }

    public void refresh(GraphicsContext gc){

        //Draw the currently created track
        for(DefSection d : sectionsForTrack){
            gc.setStroke(Color.WHITE);
            d.draw(gc);
        }

        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2));

        drawShownPanel(gc);
        for(DrawableTrain dt : trains){
            dt.draw(gc);
        }
    }


    public void drawShownPanel(GraphicsContext gc){
        gc.setFill(Color.WHITE);

        gc.fillRect(shownPanelStartX, 20, screenWidth - shownPanelStartX, ((boxSize+boxGap)*NUMB_PIECES)+boxGap);
        gc.strokeRect(shownPanelStartX, 20, screenWidth - shownPanelStartX, ((boxSize + boxGap) * NUMB_PIECES) + boxGap);

        double curY = 20 + boxGap;
        for(int i = 0; i < NUMB_PIECES; i++){
            if(selectedBox == i){
                gc.setStroke(Color.BLUE);
                gc.setLineWidth(3);
            }
            gc.strokeRect(shownPanelStartX + boxGap, curY, boxSize-boxGap/2, boxSize - boxGap/2);
            gc.setLineWidth(1);
            gc.setStroke(Color.BLACK);
            curY+= boxGap + boxSize;
        }

        gc.setLineWidth(5);
        for(DefSection ds : sections){
            ds.draw(gc);
        }
        gc.setLineWidth(1);
    }

    public void setScreenHeightAndWidth(double width, double height){
        if(screenWidth != width || screenHeight != height){
            this.screenHeight = height;
            this.screenWidth = width;

            this.shownPanelStartX = screenWidth - boxSize - boxGap*2;

            shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
            this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
            this.sections = setUpDrawPieces();
        }
    }

    public void addUIElementsToLayout(BorderPane bp){
        bp.setLeft(vBox);
    }

    public void removeUIElementsFromLayout(BorderPane bp){
        bp.getChildren().remove(vBox);
    }


    private VBox getBuilderButtons(){
        VBox vBox = new VBox(8); // spacing = 8
        vBox.setPadding(new Insets(5,5,5,5));
        Button sim = new Button("Simulate Track");
        Button clear = new Button("Clear Track");
        Button undo = new Button("Undo Track");
        Button addTrainMenu = new Button("Add Train");

        CheckBox alternate = new CheckBox("Alternate");
        alternate.setSelected(true);
        alternate.setOnAction(e -> alternateCheckBoxEvent(alternate));
        alternate.setStyle("-fx-text-inner-color: white;");//TODO not working

        addTrainMenu.setOnAction(e -> showAddTrainMenu());
        undo.setOnAction(e -> undo());
        sim.setOnAction(e -> simulateTrack());
        clear.setOnAction(e -> clear());

        vBox.getChildren().addAll(sim,clear,undo,addTrainMenu,alternate);
        return vBox;
    }



    public void alternateCheckBoxEvent(CheckBox alternate){
        alternate.setSelected(!this.alternate);
        this.alternate = !this.alternate;
    }

    public void simulateTrack(){
        if(sectionsForTrack.size() == 0 || trains.size() == 0){
            new ErrorDialog("No track or trains to simulate", "Error");
            return;
        }

        linkUpSections(sectionsForTrack);//must link of the sections inside the drawing sections for the simulation
        controller.setVisualisationMode(sectionsForTrack, trains);
    }

    public void undo(){
        if(sectionsForTrack.size() > 0){
            this.sectionsForTrack.remove(sectionsForTrack.size()-1);
            this.shouldDetect = !shouldDetect;//reverse it
        }
    }

    public void clear(){
        sectionsForTrack.clear();
    }


    public void showAddTrainMenu(){
        TrainDialog td = new TrainDialog();
        //TODO do some checks


        for(DefSection s : sectionsForTrack){
            if(s.getSection().getID() == td.getStartId()){//section the train should start on

                //Create the train
                Train train = new Train(td.getId(),td.getLength(),80,td.getStartId(),true);

                // Create the drawable train
                DrawableTrain drawableTrain = new DrawableTrain(train, s);
                trains.add(drawableTrain);
            }
        }
    }

    /**
     * Creates a very basic railway with one track per section
     * */
    public Section[] linkUpSections(List<DefSection> sections){
        Section[] railway = new Section[sections.size()];

        Section start = sections.get(0).getSection();
        railway[0] = start;
        for(int i = 1; i < sections.size(); i++){
            Section s = sections.get(i).getSection();
            s.setFrom(railway[i-1]);
            railway[i] = s;
        }

        for(int i = 0; i < sections.size()-1; i++){
            railway[i].setTo(railway[i+1]);
        }

        //link the last one to the start
        railway[railway.length-1].setTo(railway[0]);
        railway[0].setFrom(railway[sections.size()-1]);

        for(Section s : railway){
        }

        return railway;
    }


    public List<DefSection> setUpDrawPieces(){
        List<DefSection> sections = new ArrayList<>();

        double middleX = (screenWidth - boxSize - boxGap) + (boxSize/2);//Start of the box to refresh in
        double middleY = 20 + boxGap + (boxSize/2);

        double size = boxSize - (boxGap);
        double y = middleY - DefSection.TRACK_WIDTH/2;
        double x = middleX - (size/2);

        DefSection ds0 = new StraightHoriz(new Section(2, 100, null, null, null),(int)x,(int)y, (int)size,0, "LEFT");

        x = middleX - size/4;
        y = middleY + boxSize + boxGap - (size/2) + size/4;

        DefSection ds1 = new Quart1(new Section(2, 100, null, null, null),(int)(x) ,(int)y, (int)size,1, "RIGHT");

        y += boxSize + boxGap;
        x = middleX - size/2 - size/4;

        DefSection ds2 = new Quart2(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),2, "RIGHT");

        x = middleX - size/2 - size/4;
        y += boxSize + boxGap - size/2;

        DefSection ds3 = new Quart3(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),3, "RIGHT");

        y += boxSize + boxGap;
        x = middleX - size/2 + size/4 ;

        DefSection ds4 = new Quart4(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),4, "RIGHT");

        x = middleX - DefSection.TRACK_WIDTH /2 + size/4;
        y+= boxSize + boxGap + size/4;

        DefSection ds5 = new StraightVert(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),5, "RIGHT");

        sections.add(ds0);
        sections.add(ds1);
        sections.add(ds2);
        sections.add(ds3);
        sections.add(ds4);
        sections.add(ds5);

        return sections;
    }

    public DefSection getTrack(double x, double y){
        for(DefSection s : sectionsForTrack){
            if(s.containsPoint(x,y)){
                return s;
            }
        }
        return null;
    }

    public void showTrackMenu(DefSection ds){
        TrackMenu tm = new TrackMenu(ds);


    }

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e){
        int numbSections = sectionsForTrack.size();

        if(e.getButton().equals(MouseButton.PRIMARY)){
            if(e.getClickCount() == 2){
                DefSection s = getTrack(x,y);
                if(s != null){
                    showTrackMenu(s);
                }
            }
        }

        if(oneShownPanel(x, y)){
            selectPiece(x, y);
            if(e.getButton().equals(MouseButton.PRIMARY)){
                if(e.getClickCount() == 2){
                        addPiece();
                }
            }
        }

        if(sectionsForTrack.size() < numbSections){
            curId--;//Track was removed can free vup ID
        }
        else if(sectionsForTrack.size() > numbSections){
            curId++;//Added a track need to increment the ID
        }
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e){
        for(DefSection d : sectionsForTrack){
            if(d.containsPoint(x,y)){
                d.setMouseOn(true);
            }
            else{
                d.setMouseOn(false);
            }
        }
    }

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {

    }



    public void addFirstPiece(){

        DefSection ds0 = null;
        if(selectedBox == 0){
            ds0 = new StraightHoriz(new Section(curId, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,0, "RIGHT");

        }
        else if(selectedBox == 1){
            ds0 = new Quart1(new Section(curId, 200, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize*2,1, "RIGHT");
        }
        else if(selectedBox == 2){
            ds0 = new Quart2(new Section(curId, 200, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize*2,2, "DOWN");
        }
        else if(selectedBox == 3){
            ds0 = new Quart3(new Section(curId, 200, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize*2,3, "LEFT");
        }
        else if(selectedBox == 4){
            ds0 = new Quart4(new Section(curId, 200, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize*2,4, "UP");
        }
        else if(selectedBox == 5){
            ds0 = new StraightVert(new Section(curId, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize*2,5, "DOWN");
        }
        else{
            // No boxes were selected so don't add null
            return;
        }
        if(alternate){
            if(shouldDetect){
                ds0.getSection().setCandetect(true);
            }
            else{
                ds0.getSection().setCandetect(false);
            }
            shouldDetect = !shouldDetect;
        }

        sectionsForTrack.add(ds0);
    }

    private boolean shouldDetect = true;


    public void addPiece(){
        if(sectionsForTrack.size() == 0){
            addFirstPiece();
            return;
        }

        DefSection ds1 = null;

        if(selectedBox == 0){
            ds1 = new StraightHoriz(new Section(curId, 100, null, null, null), (int)pieceSize,0);

        }
        else if(selectedBox == 1){
            ds1 = new Quart1(new Section(curId, 200, null, null, null), (int)pieceSize*2,1);
        }
        else if(selectedBox == 2){
            ds1 = new Quart2(new Section(curId, 200, null, null, null), (int)pieceSize*2,2);
        }
        else if(selectedBox == 3){
            ds1 = new Quart3(new Section(curId, 200, null, null, null), (int)pieceSize*2,3);
        }
        else if(selectedBox == 4){
            ds1 = new Quart4(new Section(curId, 200, null, null, null), (int)pieceSize*2,4);
        }
        else if(selectedBox == 5){
            ds1 = new StraightVert(new Section(curId, 100, null, null, null), (int)pieceSize,5);
        }
        else {
            return; // No box selected
        }

        if(alternate){
            if(shouldDetect){
                ds1.getSection().setCandetect(true);
            }
            else{
                ds1.getSection().setCandetect(false);
            }
            shouldDetect = !shouldDetect;
        }

        ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
        sectionsForTrack.add(ds1);
    }

    public void selectPiece(double x, double y){
        int selected = 0;
        for(double ty = 20+boxGap; ty < (((boxSize+boxGap)*NUMB_PIECES)+boxGap); ty+=boxSize+boxGap){
            if(y > ty && y < ty + boxSize){
                this.selectedBox = selected;
                return;
            }
            selected++;
        }
        this.selectedBox = -1;//None selected
    }

    public boolean oneShownPanel(double x, double y){
        if(x > shownPanelStartX)return true;
        return false;
    }

    public List<DefSection> getCreatedTrack(){
        return sectionsForTrack;
    }

}

