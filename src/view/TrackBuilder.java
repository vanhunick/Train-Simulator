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
    public static final int WIDTH = 150;

    // The number of possible different track pieces you can create
    public static final int NUMB_PIECES = 6;

    // Represents the currently selected track piece
    private int selectedBox;

    // The size of the box to draw
    private double boxSize;

    // The location and size of the piece to place on the canvas
    private double pieceSize = 100;
    private double trackStartX = 300;
    private double trackStartY = 80;

    // Start of the piece selection panel
    private double shownPanelStartX;

    // Screen dimensions
    private double screenWidth;
    private double screenHeight;

    // The sections drawn to select from
    private List<DefSection> sections;

    // The section that represent the track
    private List<DefSection> sectionsForTrack;

    // The added trains to the track
    private List<DrawableTrain> trains;

    // Space between pieces in selection panel
    private double boxGap = 10;

    // The buttons for the builder
    private VBox vBox;

    // The program controller
    private ProgramController controller;

    // The current ID of the tracks will be incremented with each piece added
    private int curId = 0;

    // Set if every time you add new piece it alternates between detection and non detection sections
    private boolean alternate = true;

    /**
     * Constructs a new TrackBuilder object
     *
     * @param controller the controller of the program
     * */
    public TrackBuilder(ProgramController controller){
        this.controller = controller;
        this.vBox = createBuilderButtons();
        this.sectionsForTrack = new ArrayList<>();
        this.trains = new ArrayList<>();
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

    public void update(){}

    /**
     * Called to redraw all of the elements on the screen
     *
     * @param gc the graphics context to draw on
     * */
    public void refresh(GraphicsContext gc){

        //Draw the currently created track
        for(DefSection d : sectionsForTrack){
            gc.setStroke(Color.WHITE);
            d.draw(gc);
        }

        // Set the sizes
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2));

        // Draw the piece selection panel
        drawShownPanel(gc);

        // Draw the trains
        for(DrawableTrain dt : trains){
            dt.draw(gc);
        }
    }

    /**
     * Draws the panel where you select pieces of track from
     *
     * @param gc the graphics context to draw on
     * */
    public void drawShownPanel(GraphicsContext gc){
        gc.setFill(Color.WHITE);

        gc.fillRect(shownPanelStartX, 10, screenWidth - shownPanelStartX, ((boxSize+boxGap)*NUMB_PIECES)+boxGap/2);
        gc.strokeRect(shownPanelStartX, 10, screenWidth - shownPanelStartX, ((boxSize + boxGap) * NUMB_PIECES) + boxGap/2);

        double curY = 10 + boxGap;
        for(int i = 0; i < NUMB_PIECES; i++){
            if(selectedBox == i){
                gc.setStroke(Color.BLUE);
                gc.setLineWidth(3);
            }
            gc.setStroke(Color.BLACK);
            gc.strokeRect(shownPanelStartX + boxGap, curY, boxSize-boxGap/2, boxSize - boxGap/2);
            gc.setLineWidth(1);

            curY+= boxGap + boxSize;
        }

        gc.setLineWidth(5);
        for(DefSection ds : sections){
            ds.draw(gc);
        }
        gc.setLineWidth(1);
    }

    /**
     * Adds the appropriate UI elements for the builder to the border pane
     *
     * @param bp the border pane to add elements to
     * */
    public void addUIElementsToLayout(BorderPane bp){
        bp.setLeft(vBox);
    }


    /**
     * Removes the appropriate UI elements for the builder to the border pane
     *
     * @param bp the border pane to add elements to
     * */
    public void removeUIElementsFromLayout(BorderPane bp){
        bp.getChildren().remove(vBox);
    }


    /**
     * Creates the buttons for the track builder and sets of action listeners for them
     * */
    private VBox createBuilderButtons(){
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(5,5,5,5));

        Button sim = new Button("Simulate Track");
        Button clear = new Button("Clear Track");
        Button undo = new Button("Undo Track");
        Button addTrainMenu = new Button("Add Train");

        CheckBox alternate = new CheckBox("Alternate");
        alternate.setSelected(true);
        alternate.setOnAction(e -> alternateCheckBoxEvent(alternate));

        addTrainMenu.setOnAction(e -> showAddTrainMenu());
        undo.setOnAction(e -> undo());
        sim.setOnAction(e -> simulateTrack());
        clear.setOnAction(e -> clear());

        vBox.getChildren().addAll(sim,clear,undo,addTrainMenu,alternate);
        vBox.setPrefWidth(WIDTH);
        return vBox;
    }


    /**
     * Toggle the checkbox for alternating sections
     * */
    public void alternateCheckBoxEvent(CheckBox alternate){
        alternate.setSelected(!this.alternate);
        this.alternate = !this.alternate;
    }


    /**
     * Start the simulation by changing the mode in the controller and passing in
     * the created track and trains
     * */
    public void simulateTrack(){
        if(sectionsForTrack.size() == 0 || trains.size() == 0){
            new ErrorDialog("No track or trains to simulate", "Error");
            return;
        }

        //must link of the sections inside the drawing sections for the simulation
        linkUpSections(sectionsForTrack);
        controller.setVisualisationMode(sectionsForTrack, trains);
    }


    /**
     * Undoes and addition of a track piece
     * */
    public void undo(){
        if(sectionsForTrack.size() > 0){
            this.sectionsForTrack.remove(sectionsForTrack.size()-1);
            this.shouldDetect = !shouldDetect;//reverse it
        }
    }


    /**
     * Removes all added tracks from the list
     * */
    public void clear(){
        sectionsForTrack.clear();
    }


    /**
     * Returns true if a train can be created with the id false otherwise
     *
     * @param id to check if exists
     * */
    public boolean validTrainID(int id){
        for(DrawableTrain t : trains){
            if(t.getTrain().getId() == id)return false;
        }
        return true;
    }

    /**
     * Returns weather adding a train to a certain location is valid or not
     *
     * @param trackStartID the id of the track to start on
     * */
    public boolean validTrainStartLocation(int trackStartID){
        for(DefSection s : sectionsForTrack){
            if(s.getSection().getID() == trackStartID){
                for(DrawableTrain t : trains){
                    if(t.getCurSection().getSection().getID() == trackStartID){
                        return false;// Track does exist but there is already a train on it
                    }
                    return true;
                }
            }
        }
        //no track with that ID exists
        return false;
    }

    /**
     *
     * */
    public void showAddTrainMenu(){
        TrainDialog td = new TrainDialog(this);


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

        ds.setLength(tm.getLength());


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

    public List<DefSection> setUpDrawPieces(){
        List<DefSection> sections = new ArrayList<>();

        double middleX = (screenWidth - boxSize - boxGap) + (boxSize/2);//Start of the box to refresh in
        double middleY = 10 + boxGap + (boxSize/2);

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

}

