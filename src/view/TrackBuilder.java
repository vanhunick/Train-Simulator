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
import model.RollingStock;
import model.Section;
import model.Train;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.*;
import view.Panes.ErrorDialog;
import view.Panes.TrackMenu;

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

    // The exampleTracks drawn to select from
    private List<DefaultTrack> exampleTracks;

    // The section that represent the track
    private List<DefaultTrack> tracksInSection;

    // The added trains to the track
    private List<DrawableTrain> trains;

    // The exampleTracks the incapsulate the tracks
    private List<DrawableSection> sectionsForTrack;

    // All the tracks that have been created
    private List<DefaultTrack> allTracks;

    private DrawableSection curentSection;

    // List
    private List<DrawableRollingStock> stocks;

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

    // Set if every time you add new piece it alternates between detection and non detection exampleTracks
    private boolean alternate = true;

    /**
     * Constructs a new TrackBuilder object
     *
     * @param controller the controller of the program
     * */
    public TrackBuilder(ProgramController controller){
        this.controller = controller;
        this.vBox = createBuilderButtons();
        this.tracksInSection = new ArrayList<>();
        this.trains = new ArrayList<>();
        this.stocks = new ArrayList<>();
        this.exampleTracks = setUpDrawPieces();
        this.sectionsForTrack = new ArrayList<>();
        this.allTracks = new ArrayList<>();
    }

    public void updateSize(){
        this.screenWidth = controller.getCanvasWidth();
        this.screenHeight = controller.getCanvasHeight();
        this.shownPanelStartX = screenWidth - boxSize - boxGap*2;
        shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.exampleTracks = setUpDrawPieces();
    }

    public void update(){}

    /**
     * Called to redraw all of the elements on the screen
     *
     * @param gc the graphics context to draw on
     * */
    public void refresh(GraphicsContext gc){

        //Draw the currently created track
        for(DefaultTrack d : allTracks){
            gc.setStroke(Color.WHITE);
            d.draw(gc);
        }

        // Set the sizes
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2));

        // Draw the piece selection panel
        drawShownPanel(gc);

        for(DefaultTrack t : exampleTracks){
            t.setColor(Color.BLACK);
            t.draw(gc);
        }

        // Draw the trains
        for(DrawableTrain dt : trains){
            dt.draw(gc);
        }

        gc.setStroke(Color.GREEN);
        gc.strokeText("Current Section = " + curSectionID, 50, 40);
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
        for(DefaultTrack ds : exampleTracks){
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


        undo.setOnAction(e -> undo());
        sim.setOnAction(e -> simulateTrack());
        clear.setOnAction(e -> clear());
        completeSection.setOnAction(e -> newSection());

        vBox.getChildren().addAll(sim,clear,undo,addTrainMenu,alternate,completeSection);
        vBox.setPrefWidth(WIDTH);
        return vBox;
    }

    public void newSection(){
        if(tracksInSection.size() == 0){
            new ErrorDialog("There are no tracks in this section", "Invalid Section");
            return;
        }
        DefaultTrack[] sectionTracks = new DefaultTrack[tracksInSection.size()];

        // Copy over the created tracks into to section
        for(int i = 0; i < tracksInSection.size(); i++){
            sectionTracks[i] = tracksInSection.get(i);
        }

        // Empty out the tracks
        tracksInSection.clear();

        Section s = new Section(curSectionID,100,sectionTracks);//TODO do length later
        DrawableSection ds = new DrawableSection(s);

        sectionsForTrack.add(ds);
        curSectionID++;
    }




    /**
     * Toggle the checkbox for alternating exampleTracks
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
        if(tracksInSection.size() == 0 || trains.size() == 0){
            new ErrorDialog("No track or trains to simulate", "Error");
            return;
        }
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
        if(tracksInSection.size() > 0){
            this.tracksInSection.remove(tracksInSection.size()-1);
            this.shouldDetect = !shouldDetect;//reverse it
        }
    }


    /**
     * Removes all added tracks from the list
     * */
    public void clear(){
        tracksInSection.clear();
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
     * Pops up a menu where you can add tracks or trains to a track or modify attributes of the track
     *
     * @param dt the track to modify or add a train or stock to
     * */
    public void showTrackMenu(DefaultTrack dt){
        TrackMenu menu = new TrackMenu(dt);

        // Checks if a train should be added to the track
        if(menu.addTrain()){
            String selectedTrain = menu.getCurTrainSelection();
            if(selectedTrain.equals("British Rail Class 25")){
                Train train1 = new Train(getNextTrainID(), 80, 500, true,true, 0.2, 0.5);
                DrawableTrain drawableTrain1 = new DrawableTrain(train1, getSection(dt),dt);
                trains.add(drawableTrain1);
            }
            else if(selectedTrain.equals("British Rail Class 108 (DMU)")){

            }
            else if(selectedTrain.equals("British Rail Class 101 (DMU)")){

            }
        }

        // Checks if a rolling stock should be added to the track
        if(menu.addRollingStocl()){
            RollingStock rollingStock = new RollingStock(80,100,0.8);
            DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock, null, true);
            drawableRollingStock.setStartNotConnected(dt);
            stocks.add(drawableRollingStock);
        }
    }

    public DrawableSection getSection(DefaultTrack track){
        for(DrawableSection s : sectionsForTrack){
            for(DefaultTrack t : s.getTracks()){
                if(t.equals(track))return s;
            }
        }
        return null;
    }

    public int getNextTrainID(){
        int maxID = 0;
        for(DrawableTrain t : trains){
            if(t.getTrain().getId() > maxID){
                maxID = t.getTrain().getId();
            }
        }
        maxID++;
        return maxID;
    }


    public DefaultTrack getTrack(double x, double y){
        for(DefaultTrack s : tracksInSection){
            if(s.containsPoint(x,y)){
                return s;
            }
        }
        return null;
    }




    @Override
    public void mousePressed(double x, double y, MouseEvent e) {}

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {}

    @Override
    public void mouseClicked(double x, double y, MouseEvent e){
        int numbSections = tracksInSection.size();

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

        if(tracksInSection.size() < numbSections){
            curId--;//StraightTrack was removed can free vup ID
        }
        else if(tracksInSection.size() > numbSections){
            curId++;//Added a track need to increment the ID
        }
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e){
        for(DefaultTrack d : tracksInSection){
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

        allTracks.add(ds0);
        tracksInSection.add(ds0);
    }

    private boolean shouldDetect = true;


    public void nextSection(){


        if(alternate){
            curentSection.getSection().setCandetect(shouldDetect);
            shouldDetect = !shouldDetect;
        }
    }

    public void addPiece(){
        if(tracksInSection.size() == 0){
            addFirstPiece();
            return;
        }

        int length = 100;

        DefaultTrack ds1 = null;

        if(selectedBox == 0){
            ds1 = new StraightHoriz(length, 0, curId);
        }
        else if(selectedBox == 1){
            ds1 = new Quart1(length*2, 1, curId);
        }
        else if(selectedBox == 2){
            ds1 = new Quart2(length*2, 2, curId);
        }
        else if(selectedBox == 3){
            ds1 = new Quart3(length*2, 3, curId);
        }
        else if(selectedBox == 4){
            ds1 = new Quart4(length*2, 4, curId);
        }
        else if(selectedBox == 5){
            ds1 = new StraightVert(length, 5, curId);
        }
        else {
            return; // No box selected
        }

        ds1.setStart(allTracks.get(allTracks.size()-1));
        allTracks.add(ds1);
        tracksInSection.add(ds1);
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
        return tracksInSection;
    }


    /**
     * A list of tracks to show to the user to choose from on the UI
     * */
    public List<DefaultTrack> setUpDrawPieces(){
        List<DefaultTrack> sections = new ArrayList<>();

        double middleX = (screenWidth - boxSize - boxGap) + (boxSize/2);//Start of the box to draw in
        double middleY = 10 + boxGap + (boxSize/2);

        double size = boxSize - (boxGap);
        double y = middleY - DefaultTrack.TRACK_WIDTH/2;
        double x = middleX - (size/2);


        DefaultTrack ds0 = new StraightHoriz((int)x,(int)y, (int)size,0,0, "RIGHT");

        x = middleX - size/4;
        y = middleY + boxSize + boxGap - (size/2) + size/4;

        DefaultTrack ds1 = new Quart1((int)x,(int)y, (int)size,1,"RIGHT",0 );

        y += boxSize + boxGap;
        x = middleX - size/2 - size/4;

        DefaultTrack ds2 = new Quart2((int)x,(int)y, (int)size,2,"RIGHT",0 );

        x = middleX - size/2 - size/4;
        y += boxSize + boxGap - size/2;

        DefaultTrack ds3 = new Quart3((int)x,(int)y, (int)size,3,"RIGHT",0 );

        y += boxSize + boxGap;
        x = middleX - size/2 + size/4 ;

        DefaultTrack ds4 = new Quart4((int)x,(int)y, (int)size,4,"RIGHT",0 );

        x = middleX - DefaultTrack.TRACK_WIDTH /2 + size/4;
        y+= boxSize + boxGap + size/4;

        DefaultTrack ds5 = new StraightVert((int)x,(int)y, (int)size,5,"RIGHT",0);

        sections.add(ds0);
        sections.add(ds1);
        sections.add(ds2);
        sections.add(ds3);
        sections.add(ds4);
        sections.add(ds5);

        return sections;
    }
}

