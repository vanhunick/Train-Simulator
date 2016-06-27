package view;

import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import model.RollingStock;
import model.Section;
import model.Train;
import save.LoadedRailway;
import save.Save;
import view.Drawable.DrawableRollingStock;
import view.Drawable.DrawableTrain;
import view.Drawable.section_types.*;
import view.Panes.ErrorDialog;
import view.Panes.TrackMenu;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/03/2016.
 */
public class TrackBuilder implements MouseEvents{

    public static final int WIDTH = 150;

    // The number of possible different track pieces you can create
    public static final int NUMB_PIECES = 8;

    // Represents the currently selected track piece
    private int selectedBox;

    // The size of the box to draw
    private double boxSize;

    // The location and size of the piece to place on the canvas
    private double pieceSize = 100;

    // Start of the piece selection panel
    private double shownPanelStartX;

    // Screen dimensions
    private double screenWidth;
    private double screenHeight;

    // If the user is selecting sections
    private boolean sectionMode = false;

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

    // List of stocks on the track
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

    // If the next section should detect or not
    private boolean shouldDetect = true;

    // The track being dragged by the mouse
    private DefaultTrack mouseSelectedPeice;

    // The current location of the mouse
    private Point mouseLocation;


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
        this.mouseLocation = new Point(0,0);
    }


    /**
     * Called when the screen size changes to make sure all the elements still fit
     * */
    public void updateSize(){
        this.screenWidth = controller.getCanvasWidth();
        this.screenHeight = controller.getCanvasHeight();
        this.shownPanelStartX = screenWidth - boxSize - boxGap*2;
        shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.exampleTracks = setUpDrawPieces();
    }

    /**
     * Called to redraw all of the elements on the screen
     *
     * @param gc the graphics context to draw on
     * */
    public void refresh(GraphicsContext gc){
        //Draw the currently created track
        for(DefaultTrack d : allTracks){
            d.draw(gc);
        }

        // Set the sizes
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2));

        // Draw the piece selection panel
        drawShownPanel(gc);

        // Draw the example tracks
        for(DefaultTrack t : exampleTracks){
            t.draw(gc);
        }

        // Draw the trains
        for(DrawableTrain dt : trains){
            dt.draw(gc);
        }

        // Show the direction of the piece being dragged
        if(mouseSelectedPeice != null){
            gc.strokeText("Current Direction = " + mouseSelectedPeice.getDirection(), 50, 80);
        }

        // Indicate the id of the current section
        if(sectionMode){
            gc.strokeText("Current Section = " + curSectionID, 50, 40);
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
        for(DefaultTrack ds : exampleTracks){
            ds.draw(gc);
        }
        gc.setLineWidth(1);
    }



    public void save(){
        // Check if there are tracks that have not been added to a section yet
        if(tracksInSection.size() > 0){
            newSection();// Act like the next section button is clicked
        }

        // Sections
        DrawableSection[] sections = new DrawableSection[sectionsForTrack.size()];
        sectionsForTrack.toArray(sections);

        // Tracks
        DefaultTrack[] tracks = new DefaultTrack[allTracks.size()];
        allTracks.toArray(tracks);

        tracks[0].setFrom(tracks.length-1);

        LoadedRailway railway = new LoadedRailway(sections,tracks,trains,stocks);

        // Get user to enter a file location to save to
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Railway");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
                Save s = new Save();
                s.save(railway,file.getAbsolutePath());
        }
    }


    /**
     * Called when the user clicks the new section button
     * Indicates they want to start a new section or start the
     * section creation part of the builder.
     * */
    public void newSection(){
        // User clicked create sections for the first time
        if(!sectionMode){
            sectionMode = true;
            // link up the unconnected tracks
            connectDestinations();
            return;
        }
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

        // Set the Section to be from the one before
        s.setFrom(sectionsForTrack.size()-1);
        DrawableSection ds = new DrawableSection(s);

        if(alternate){
            ds.getSection().setCandetect(shouldDetect);
            shouldDetect = !shouldDetect;
        }

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
        if(allTracks.size() == 0 || trains.size() == 0){
            new ErrorDialog("No track or trains to simulate", "Error");
            return;
        }
    }

    /**
     * Undoes and addition of a track piece
     * */
    public void undo(){
        if(tracksInSection.size() > 0){
            this.tracksInSection.remove(tracksInSection.size()-1);
        }
        if(allTracks.size() > 0){
            this.allTracks.remove(allTracks.size()-1);
        }
    }


    /**
     * Removes all added tracks from the list
     * */
    public void clear(){
        tracksInSection.clear();
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
                Train train1 = new Train(getNextTrainID(), menu.getLength(), 500, true,true,71000);
                DrawableTrain drawableTrain1 = new DrawableTrain(train1, getSection(dt),dt);
                drawableTrain1.setUpImage();
                trains.add(drawableTrain1);
            }
            else if(selectedTrain.equals("British Rail Class 108 (DMU)")){

            }
            else if(selectedTrain.equals("British Rail Class 101 (DMU)")){

            }
        }

        // Checks if a rolling stock should be added to the track
        if(menu.addRollingStocl()){
            RollingStock rollingStock = new RollingStock(80,100,51000);
            DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock, null, true);
            drawableRollingStock.setStartNotConnected(dt);
            stocks.add(drawableRollingStock);
        }
    }

    /**
     * Returns the section the tracks belongs to null if none
     * */
    public DrawableSection getSection(DefaultTrack track){
        for(DrawableSection s : sectionsForTrack){
            for(DefaultTrack t : s.getTracks()){
                if(t.equals(track))return s;
            }
        }
        return null;
    }

    /**
     * Returns the next valid train id
     * */
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

    /**
     * Returns the track at the point x,y if there is one
     * else returns null
     * */
    public DefaultTrack getTrack(double x, double y){
        for(DefaultTrack s : allTracks){
            if(s.containsPoint(x,y)){
                return s;
            }
        }
        return null;
    }

    @Override
    public void mousePressed(double x, double y, MouseEvent e) {
        if(e.getButton().equals(MouseButton.PRIMARY)){

            // Check if the click is on the track panel
            if(onTrackPanel(x)){
                if(e.getButton().equals(MouseButton.PRIMARY)){
                    // Set the selected box
                    selectPiece(y);

                    // Check is mouse pressed on a box
                    if(selectedBox !=-1){
                        DefaultTrack t = getSelectedTrackFromPanel((int)x,(int)y);
                        curId++;

                        allTracks.add(t);
                        mouseSelectedPeice = t;
                        mouseSelectedPeice.setSelected(true);
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(double x, double y, MouseEvent e) {
        int numbSections = allTracks.size();

        // Check if there is a piece to put down
        if(mouseSelectedPeice != null){
            mouseSelectedPeice.setSelected(false);
            placeTrack(mouseSelectedPeice);
        }

        // The mouse was released but the location for the track was not valid
        if(allTracks.size() < numbSections){
            curId--;//StraightTrack was removed can free vup ID
        }
    }

    /**
     * Places a track called when the user released the mouse in a valid
     * location for the track
     * */
    public boolean placeTrack(DefaultTrack track){

        // The first track can be placed anywhere
        if(allTracks.size() == 1){
            return true;
        }

        // Not the first track must connect to another track
        for(int i = 0; i < allTracks.size()-1; i++){// -1 because the one being added is at the top
            // The track to try connect to
            DefaultTrack t = allTracks.get(i);

            if(t instanceof JunctionTrack){
                JunctionTrack j = (JunctionTrack)t;
                if(j.canConnectThrown(track)){
                    track.setStart(j.getTrackThrown());
                    track.setFrom(i);
                    j.setOutboundToThrown(allTracks.size()-1);
                    return true;
                }
                if(j.canConnect(track)){
                    track.setStart(j.getStraightTrack());
                    track.setFrom(i);
                    j.setTo(allTracks.size()-1);
                    return true;
                }
            }
            else {
                if(t.canConnect(track)){
                    t.setTo(allTracks.size()-1);
//                    t.setFrom(i);
                    track.setStart(t);
                    track.setFrom(i);

                    mouseSelectedPeice = null;
                    return true;
                }
            }
        }

        // User did not place it so remove it
        allTracks.remove(allTracks.size()-1);
        mouseSelectedPeice = null;
        return false;
    }

    public void connectDestinations(){
        for(int i = 0; i < allTracks.size(); i++){
            DefaultTrack t = allTracks.get(i);
            if(t instanceof JunctionTrack){
                if(t.getTo() == 0){
                    findTrackForConnection(t);
                }
            }
        }

        // Connect the start piece
        for(DefaultTrack t : allTracks){
            if(t.canConnect(allTracks.get(0))){
                allTracks.get(0).setFrom(getTrackIndex(t));
                //TODO to should already be set
            }
        }
    }

    public DefaultTrack findTrackForConnection(DefaultTrack trackWithoutTo){
        for(int i = 0; i < allTracks.size(); i++){
            if(allTracks.get(i) instanceof JunctionTrack){
                if (trackWithoutTo.canConnect(allTracks.get(i))) {//TODO not done
                    trackWithoutTo.setTo(i);
                    allTracks.get(i).setFrom(getTrackIndex(trackWithoutTo));
                }
            }
            else {
                if (trackWithoutTo.canConnect(allTracks.get(i))) {
                    trackWithoutTo.setTo(i);
                    allTracks.get(i).setFrom(getTrackIndex(trackWithoutTo));
                }
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(double x, double y, MouseEvent e){
        if(e.getButton().equals(MouseButton.SECONDARY)){
            DefaultTrack s = getTrack(x,y);
            if(e.getClickCount() == 2 && s!= null){
                showTrackMenu(s);
            }
        }

        if(sectionMode){
            DefaultTrack s = getTrack(x,y);
            if(s!=null){
                s.setSelected(true);
                tracksInSection.add(s);
            }
        }
    }



    @Override
    public void mouseMoved(double x, double y, MouseEvent e){
        for(DefaultTrack d : allTracks){
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
        this.mouseLocation.setLocation(x,y);// Update mouse location

        // Highlights the selected piece red or green based on if it is valid to place
        if(mouseSelectedPeice != null){
            mouseSelectedPeice.setMid(x,y);

            if(allTracks.size() == 1){
                DefaultTrack.SELECTED_COLOR = Color.GREEN;
                return;
            }
            DefaultTrack.SELECTED_COLOR = Color.RED;
            for(int i = 0; i < allTracks.size(); i++){
                DefaultTrack t = allTracks.get(i);

                if(t instanceof JunctionTrack){
                    JunctionTrack j = (JunctionTrack)t;
                    if(j.canConnectThrown(mouseSelectedPeice)){
                        DefaultTrack.SELECTED_COLOR = Color.GREEN;
                    }
                    if(j.canConnect(mouseSelectedPeice)){
                        DefaultTrack.SELECTED_COLOR = Color.GREEN;
                    }
                }
                else {
                    if(t.canConnect(mouseSelectedPeice)){
                        DefaultTrack.SELECTED_COLOR = Color.GREEN;
                    }
                }
            }
        }
    }

    /**
     * Returns a new track of the type selected in the track panel
     * */
    public DefaultTrack getSelectedTrackFromPanel(int x, int y){
        DefaultTrack[] trackChoices = new DefaultTrack[]{
                new StraightHoriz(x,y, (int)pieceSize,0,curId, "RIGHT"),
                new Quart1(x,y, (int)pieceSize*2,1, "RIGHT", curId),
                new Quart2(x,y, (int)pieceSize*2,2, "DOWN", curId),
                new Quart3(x,y, (int)pieceSize*2,3, "LEFT", curId),
                new Quart4(x,y, (int)pieceSize*2,4, "UP", curId),
                new StraightVert(x,y, (int)pieceSize,5, "DOWN",curId),
                new JunctionTrack(x,y, (int)pieceSize, 6,curId, "RIGHT",false,true),
                new JunctionTrack(x,y, (int)pieceSize, 6,curId, "RIGHT",false,false)
        };

        return trackChoices[selectedBox];
    }

    /**
     * Returns the index of the track in the tracks array -1
     * if it does not exist
     * */
    public int getTrackIndex(DefaultTrack track){
        for(int i = 0; i < allTracks.size(); i++){
            if(track.equals(allTracks.get(i)))return i;
        }
        return -1;
    }

    /**
     * Selects the box on the screen using the y location
     * */
    public void selectPiece(double y){
        int selected = 0;
        for(double ty = 20+ boxGap; ty < (((boxSize+boxGap)*NUMB_PIECES)+boxGap); ty+=boxSize+boxGap){
            if(y > ty && y < ty + boxSize){
                this.selectedBox = selected;
                return;
            }
            selected++;
        }
        this.selectedBox = -1;//None selected
    }

    /**
     * Returns if the x point is on the track panel
     * */
    public boolean onTrackPanel(double x){
        if(x > shownPanelStartX)return true;
        return false;
    }

    /**
     * A list of tracks to show to the user to choose from on the UI
     * */
    public List<DefaultTrack> setUpDrawPieces(){
        List<DefaultTrack> sections = new ArrayList<>();

        double x = (screenWidth - boxSize - boxGap) + (boxSize/2);//Start of the box to draw in
        double y = 10 + boxGap + (boxSize/2) - DefaultTrack.TRACK_WIDTH/2;
        double size = boxSize - (boxGap);

        DefaultTrack[] trackChoices = new DefaultTrack[]{
                new StraightHoriz((int)x,(int)y, (int)size,0,0, "RIGHT"),
                new Quart1((int)x,(int)y, (int)size,1,"RIGHT",0 ),
                new Quart2((int)x,(int)y, (int)size,2,"RIGHT",0 ),
                new Quart3((int)x,(int)y, (int)size,3,"RIGHT",0 ),
                new Quart4((int)x,(int)y, (int)size,4,"RIGHT",0 ),
                new StraightVert((int)x,(int)y, (int)size,5,"RIGHT",0),
                new JunctionTrack((int)x,(int)y, (int)size/2,7,0,"RIGHT",false,true),
                new JunctionTrack((int)x,(int)y, (int)size/2,7,0,"RIGHT",false,false)
        };

        for(DefaultTrack track : trackChoices){
            track.setMid(x,y);
            y+=boxSize + boxGap;
            sections.add(track);
        }

        return sections;
    }

    /**
     * Called when a key is pressed
     * */
    public void keyPressed(String code){
        // Use E to change the direction of a track piece
        if(code.equals("E")){
            if(mouseSelectedPeice != null){
                mouseSelectedPeice.toggleDirection();
            }
        }
        // Use R to rotate through the selectable pieces
        if(code.equals("R")){
            if(mouseSelectedPeice != null){
                if(selectedBox == 7)selectedBox = 0;
                else selectedBox++;
                allTracks.remove(allTracks.size()-1);
                DefaultTrack t = getSelectedTrackFromPanel((int)mouseLocation.getX(),(int)mouseLocation.getY());//TODO possibly use cur mouse loc
                t.setMid(mouseLocation.getX(),mouseLocation.getY());
                allTracks.add(t);
                mouseSelectedPeice = t;
            }
        }
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
        vBox.setPadding(new Insets(5, 5, 5, 5));

        CheckBox alternate = new CheckBox("Alternate");
        alternate.setSelected(true);
        alternate.setOnAction(e -> alternateCheckBoxEvent(alternate));

        vBox.getChildren().addAll(alternate);
        vBox.setPrefWidth(WIDTH);
        return vBox;
    }

    public void update(){}
}

