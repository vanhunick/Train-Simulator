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
import view.Drawable.section_types.*;
import view.Panes.ErrorDialog;
import view.Panes.TrackMenu;
import view.Panes.TrainDialog;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/03/2016.
 */
public class TrackBuilder implements MouseEvents{

    public void keyPressed(String code){

    }

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
    private List<DefaultTrack> sections;

    // The section that represent the track
    private List<DefaultTrack> tracksInTrack;

    // The added trains to the track
    private List<DrawableTrain> trains;

    // The sections the incapsulate the tracks
    private List<DrawableSection> sectionsForTrack;

    // All the tracks that have been created
    private List<DefaultTrack> allTracks;

    private DrawableSection curentSection;

    // ID for section
    private  int curSectionID;

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
        this.tracksInTrack = new ArrayList<>();
        this.trains = new ArrayList<>();
//        this.sections = setUpDrawPieces();
    }


    public void updateSize(){
        this.screenWidth = controller.getCanvasWidth();
        this.screenHeight = controller.getCanvasHeight();
        this.shownPanelStartX = screenWidth - boxSize - boxGap*2;
        shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
//        this.sections = setUpDrawPieces();
    }

    public void update(){}

    /**
     * Called to redraw all of the elements on the screen
     *
     * @param gc the graphics context to draw on
     * */
    public void refresh(GraphicsContext gc){

        //Draw the currently created track
        for(DefaultTrack d : tracksInTrack){
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
        for(DefaultTrack ds : sections){
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

        Button sim = new Button("Simulate StraightTrack");
        Button clear = new Button("Clear StraightTrack");
        Button undo = new Button("Undo StraightTrack");
        Button addTrainMenu = new Button("Add Train");
        Button completeSection = new Button("New Section");

        CheckBox alternate = new CheckBox("Alternate");
        alternate.setSelected(true);
        alternate.setOnAction(e -> alternateCheckBoxEvent(alternate));

        addTrainMenu.setOnAction(e -> showAddTrainMenu());
        undo.setOnAction(e -> undo());
        sim.setOnAction(e -> simulateTrack());
        clear.setOnAction(e -> clear());
        completeSection.setOnAction(e -> newSection());

        vBox.getChildren().addAll(sim,clear,undo,addTrainMenu,alternate);
        vBox.setPrefWidth(WIDTH);
        return vBox;
    }


    public void newSection(){
        DefaultTrack[] defaultTracks = new DefaultTrack[tracksInTrack.size()];

        for(int i = 0; i < defaultTracks.length; i++){
            defaultTracks[i] = tracksInTrack.get(i);
        }

        sectionsForTrack.add(new DrawableSection(new Section(curSectionID,100.0,defaultTracks)));
        curSectionID++;
        tracksInTrack.clear();//new Section so clear the list
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
        if(tracksInTrack.size() == 0 || trains.size() == 0){
            new ErrorDialog("No track or trains to simulate", "Error");
            return;
        }

//        controller.setVisualisationMode(makeSections(tracksInTrack), trains);
    }

    public List<DrawableSection> makeSections(List<DefaultTrack> tracks){
        List<DrawableSection> sections = new ArrayList<>();

        //Could just make a new section for each track


        return sections;
    }


    /**
     * Undoes and addition of a track piece
     * */
    public void undo(){
        if(tracksInTrack.size() > 0){
            this.tracksInTrack.remove(tracksInTrack.size()-1);
            this.shouldDetect = !shouldDetect;//reverse it
        }
    }


    /**
     * Removes all added tracks from the list
     * */
    public void clear(){
        tracksInTrack.clear();
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
        for(DefaultTrack s : allTracks){
            if(s.getId() == trackStartID){
                for(DrawableTrain t : trains){
                    if(t.getCurTrack().getId() == trackStartID){
                        return false;// StraightTrack does exist but there is already a train on it
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


        for(DefaultTrack s : tracksInTrack){
            if(s.getId() == td.getStartId()){//section the train should start on

                //td.getStartId()

                //Create the train
                Train train = new Train(td.getId(),td.getLength(),80,true,false, 0.8,0.5);

                // Create the drawable train
//                DrawableTrain drawableTrain = new DrawableTrain(train, s,s.getSection());//TODO make sections first
//                trains.add(drawableTrain);
            }
        }
    }

    /**
     * Creates a very basic railway with one track per section
     * */
//    public Section[] linkUpSections(List<DefaultTrack> sections){
//        Section[] railway = new Section[sections.size()];
//
//        Section start = sections.get(0).getSection();
//        railway[0] = start;
//        for(int i = 1; i < sections.size(); i++){
//            Section s = sections.get(i).getSection();
//            s.setFrom(railway[i-1]);
//            railway[i] = s;
//        }
//
//        for(int i = 0; i < sections.size()-1; i++){
//            railway[i].setTo(railway[i+1]);
//        }
//
//        //link the last one to the start
//        railway[railway.length-1].setTo(railway[0]);
//        railway[0].setFrom(railway[sections.size()-1]);
//
//        for(Section s : railway){
//        }
//
//        return railway;
//    }



    public DefaultTrack getTrack(double x, double y){
        for(DefaultTrack s : tracksInTrack){
            if(s.containsPoint(x,y)){
                return s;
            }
        }
        return null;
    }

    public void showTrackMenu(DefaultTrack ds){
        TrackMenu tm = new TrackMenu(ds);

        ds.setLength(tm.getLength());
    }

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e){
        int numbSections = tracksInTrack.size();

        if(e.getButton().equals(MouseButton.PRIMARY)){
            if(e.getClickCount() == 2){
                DefaultTrack s = getTrack(x,y);
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

        if(tracksInTrack.size() < numbSections){
            curId--;//StraightTrack was removed can free vup ID
        }
        else if(tracksInTrack.size() > numbSections){
            curId++;//Added a track need to increment the ID
        }
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e){
        for(DefaultTrack d : tracksInTrack){
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

    public void linkUpDrawSections(List<DrawableSection> railway){

    }

    public void addFirstPiece(){

        DefaultTrack ds0 = null;
        if(selectedBox == 0){
            ds0 = new StraightHoriz((int)trackStartX,(int)trackStartY, (int)pieceSize,0,curId, "RIGHT");
        }
        else if(selectedBox == 1){
            ds0 = new Quart1((int)trackStartX,(int)trackStartY, (int)pieceSize*2,1, "RIGHT", curId);

        }
        else if(selectedBox == 2){
            ds0 = new Quart2((int)trackStartX,(int)trackStartY, (int)pieceSize*2,2, "DOWN", curId);
        }
        else if(selectedBox == 3){
            ds0 = new Quart3((int)trackStartX,(int)trackStartY, (int)pieceSize*2,3, "DOWN", curId);
        }
        else if(selectedBox == 4){
            ds0 = new Quart4((int)trackStartX,(int)trackStartY, (int)pieceSize*2,4, "UP", curId);
        }
        else if(selectedBox == 5){
            ds0 = new StraightVert((int)trackStartX,(int)trackStartY, (int)pieceSize,5, "DOWN",curId);
        }
        else{
            // No boxes were selected so don't add null
            return;
        }
        if(alternate){
            if(shouldDetect){
//                ds0.getSection().setCandetect(true);//TODO make into dection
            }
            else{
//                ds0.getSection().setCandetect(false);
            }
            shouldDetect = !shouldDetect;
        }

        tracksInTrack.add(ds0);
    }

    private boolean shouldDetect = true;


    public void addPiece(){
        if(tracksInTrack.size() == 0){
//            addFirstPiece();
            return;
        }

        DefaultTrack ds1 = null;

//        if(selectedBox == 0){
//            ds1 = new StraightHoriz(new Section(curId, 100, null, null, null), (int)pieceSize,0);
//
//        }
//        else if(selectedBox == 1){
//            ds1 = new Quart1(new Section(curId, 200, null, null, null), (int)pieceSize*2,1);
//        }
//        else if(selectedBox == 2){
//            ds1 = new Quart2(new Section(curId, 200, null, null, null), (int)pieceSize*2,2);
//        }
//        else if(selectedBox == 3){
//            ds1 = new Quart3(new Section(curId, 200, null, null, null), (int)pieceSize*2,3);
//        }
//        else if(selectedBox == 4){
//            ds1 = new Quart4(new Section(curId, 200, null, null, null), (int)pieceSize*2,4);
//        }
//        else if(selectedBox == 5){
//            ds1 = new StraightVert(new Section(curId, 100, null, null, null), (int)pieceSize,5);
//        }
//        else {
//            return; // No box selected
//        }

        if(alternate){
            if(shouldDetect){
//                ds1.getSection().setCandetect(true);
            }
            else{
//                ds1.getSection().setCandetect(false);
            }
            shouldDetect = !shouldDetect;
        }

//        ds1.setStart(tracksInTrack.get(tracksInTrack.size()-1));
        tracksInTrack.add(ds1);
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

    public List<DefaultTrack> getCreatedTrack(){
        return tracksInTrack;
    }

//    public List<DefaultTrack> setUpDrawPieces(){
//        List<DefaultTrack> sections = new ArrayList<>();
//
//        double middleX = (screenWidth - boxSize - boxGap) + (boxSize/2);//Start of the box to draw in
//        double middleY = 10 + boxGap + (boxSize/2);
//
//        double size = boxSize - (boxGap);
//        double y = middleY - DefaultTrack.TRACK_WIDTH/2;
//        double x = middleX - (size/2);
//
//        DefaultTrack ds0 = new StraightHoriz(new Section(2, 100, null, null, null),(int)x,(int)y, (int)size,0, "LEFT");
//
//        x = middleX - size/4;
//        y = middleY + boxSize + boxGap - (size/2) + size/4;
//
//        DefaultTrack ds1 = new Quart1(new Section(2, 100, null, null, null),(int)(x) ,(int)y, (int)size,1, "RIGHT");
//
//        y += boxSize + boxGap;
//        x = middleX - size/2 - size/4;
//
//        DefaultTrack ds2 = new Quart2(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),2, "RIGHT");
//
//        x = middleX - size/2 - size/4;
//        y += boxSize + boxGap - size/2;
//
//        DefaultTrack ds3 = new Quart3(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),3, "RIGHT");
//
//        y += boxSize + boxGap;
//        x = middleX - size/2 + size/4 ;
//
//        DefaultTrack ds4 = new Quart4(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),4, "RIGHT");
//
//        x = middleX - DefaultTrack.TRACK_WIDTH /2 + size/4;
//        y+= boxSize + boxGap + size/4;
//
//        DefaultTrack ds5 = new StraightVert(new Section(2, 100, null, null, null),(int)(x),(int)y, (int)(size),5, "RIGHT");
//
//        sections.add(ds0);
//        sections.add(ds1);
//        sections.add(ds2);
//        sections.add(ds3);
//        sections.add(ds4);
//        sections.add(ds5);
//
//        return sections;
//    }

}

