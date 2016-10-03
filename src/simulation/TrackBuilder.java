package simulation;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import simulation.model.RollingStock;
import simulation.model.Section;
import simulation.model.Train;
import util.save.LoadedRailway;
import util.save.Save;
import simulation.Drawable.DrawableRollingStock;
import simulation.Drawable.DrawableTrain;
import simulation.Drawable.tracks.*;
import simulation.ui.ErrorDialog;
import simulation.ui.AddTrainMenu;

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
    public static final int NUMB_PIECES = 10;

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

    // The user selected track length
    private int trackLength;

    // If the user is selecting sections
    private boolean sectionMode = false;

    // The exampleTracks drawn to select from
    private List<DefaultTrack> exampleTracks;

    // The section that represent the track
    private List<DefaultTrack> tracksInSection;

    // The added trains to the track
    private List<DrawableTrain> trains;

    // The exampleTracks the incapsulate the util.tracks
    private List<DrawableSection> sectionsForTrack;

    // All the util.tracks that have been created
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

    // The current ID of the util.tracks will be incremented with each piece added
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
        initLists();
        this.controller = controller;
        this.vBox = createBuilderButtons();
        this.mouseLocation = new Point(0,0);
    }

    /**
     * Track Builder Constructor for Testing
     * */
    public TrackBuilder(){
        initLists();
    }

    public void initLists(){
        this.tracksInSection = new ArrayList<>();
        this.trains = new ArrayList<>();
        this.stocks = new ArrayList<>();
        this.sectionsForTrack = new ArrayList<>();
        this.allTracks = new ArrayList<>();
    }

    /**
     * Called when the screen size changes to make sure all the elements still fit
     * */
    public void updateSize(){
        this.screenWidth = Main.SCREEN_WIDTH;
        this.screenHeight = Main.SCREEN_HEIGHT;
        this.boxSize = ((screenHeight - 80 - ((NUMB_PIECES*boxGap)+boxGap*2))/NUMB_PIECES);
        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2) + offsetFromExtraElements());
        this.exampleTracks = setUpDrawPieces();
    }

    public List<DefaultTrack> getAllTracks(){
        return this.allTracks;
    }

    /**
     * Returns the pixel offset from any elements on the screen next to the canvas
     * */
    public double offsetFromExtraElements(){
        return 0;
    }


    /**
     * Start the simulation by changing the mode in the controller and passing in
     * the created track and trains
     * */
    public LoadedRailway getLoadedRailway(){
        if(tracksInSection.size() > 0){
            newSection();// Act like the next section button is clicked
        }

        if(sectionsForTrack.size() <= 0){
            new ErrorDialog("No railway to simulate", "No need to simulate");
            return null;
        }

        allTracks.forEach(t -> t.setSelected(false));

        // Check if there are util.tracks that have not been added to a section yet

        // Looks at junctions
        finishConnectingUpSections();

        // Sections
        DrawableSection[] sections = new DrawableSection[sectionsForTrack.size()];
        sectionsForTrack.toArray(sections);

        // Tracks
        DefaultTrack[] tracks = new DefaultTrack[allTracks.size()];
        allTracks.toArray(tracks);
        
        LoadedRailway railway = new LoadedRailway(null,sections,tracks,trains,stocks); // null because not loaded from file
        return railway;
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
        this.boxSize = ((screenHeight - 80 - ((NUMB_PIECES*boxGap)+boxGap*2))/NUMB_PIECES);
        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2) + offsetFromExtraElements());

        // Draw the piece selection panel
        drawShownPanel(gc);

        // Draw the example util.tracks
        exampleTracks.forEach(t -> t.draw(gc));
        // Draw the trains
        trains.forEach(t -> t.draw(gc));

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
        LoadedRailway railway = getLoadedRailway();

        // Get user to enter a file location to util.save to
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
            // link up the unconnected util.tracks
            connectDestinations();
            return;
        }
        if(tracksInSection.size() == 0){
            new ErrorDialog("There are no util.tracks in this section", "Invalid Section");
            return;
        }

        DefaultTrack[] sectionTracks = new DefaultTrack[tracksInSection.size()];

        // Copy over the created util.tracks into to section
        for(int i = 0; i < tracksInSection.size(); i++){
            sectionTracks[i] = tracksInSection.get(i);
        }

        // Empty out the util.tracks
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
     * Sets the links for the sections
     * */
    public void finishConnectingUpSections(){
        for(DrawableSection s : sectionsForTrack){
            for(DefaultTrack t : s.getTracks()){
                if(t instanceof JunctionTrack){
                    s.getSection().setHasJunctionTrack(true);
                    JunctionTrack jt = (JunctionTrack)t;
                    if(jt.inBound()){
                        s.getSection().setJuncSectionIndex(findSectionForTrack(jt.getInboundFromThrown(),sectionsForTrack));
                    }
                    else {
                        s.getSection().setJuncSectionIndex(findSectionForTrack(jt.getOutboundToThrown(),sectionsForTrack));
                    }
                }
            }
        }
    }

    /**
     * Returns the id of the section that the track is contained in
     * */
    public int findSectionForTrack(int trackIndex, List<DrawableSection> sections){
        for(DrawableSection s : sections){
            if(s.containsTrack(allTracks.get(trackIndex))){
                return s.getSection().getID();
            }
        }
        return -1; //Error
    }


    /**
     * Undoes and addition of a track piece
     * */
    public void undo(){// TODO // FIXME: 14/08/2016 
        if(tracksInSection.size() > 0){
            this.tracksInSection.remove(tracksInSection.size()-1);
        }
        if(allTracks.size() > 0){
            this.allTracks.remove(allTracks.size()-1);
        }
    }


    /**
     * Removes all added util.tracks from the list
     * */
    public void clear(){
        allTracks.clear();
        trains.clear();
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
     * Pops up a menu where you can add util.tracks or trains to a track or modify attributes of the track
     *
     * @param dt the track to modify or add a train or stock to
     * */
    public void showTrackMenu(DefaultTrack dt){
        AddTrainMenu menu = new AddTrainMenu(dt, getNextTrainID());

        // Checks if a train should be added to the track
        if(menu.addTrain()){
            String selectedTrain = menu.getCurTrainSelection();
            if(selectedTrain.equals("British Rail Class 25")){
                Train train1 = new Train(getNextTrainID(), menu.getLength(), 500, true,true,71000,7);
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
            DrawableRollingStock drawableRollingStock = new DrawableRollingStock(rollingStock, null, true, true);
            drawableRollingStock.setStartNotConnected(dt);
            stocks.add(drawableRollingStock);
        }
    }

    /**
     * Returns the section the util.tracks belongs to null if none
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



    public void lengthChanged(int length){
        this.trackLength = length;
    }


    public boolean placeJunctionTrack(JunctionTrack jTrack){
        boolean placed = false;

        for(int i = 0; i < allTracks.size(); i++){
            DefaultTrack t = allTracks.get(i);

            // Check if can connect to both
            if(jTrack.inBound() && t.canConnect(jTrack.getInnerTrack())){
                jTrack.setInboundFromThrown(i);
                jTrack.setStart(allTracks.get(i));
                jTrack.moveToThrown();
                t.setTo(allTracks.size()-1);
                t.setJuncTo(allTracks.size()-1);
                return true;
            }

            // Check if connecting to straight piece
            if(t.canConnect(jTrack.getStraightTrack())){
                jTrack.setStart(allTracks.get(i));
                jTrack.setFrom(i);
                if(jTrack.inBound()){
                    jTrack.setFrom(i);
                }
                if(!jTrack.inBound()){
                    jTrack.setFrom(i);//TODO might be different
                }

                placed = true;
                t.setTo(allTracks.size()-1);
            }
        }

        if(!placed){
            // User did not place it so remove it
            allTracks.remove(allTracks.size()-1);
            mouseSelectedPeice = null;
        }
        return placed;
    }

    /**
     * Places a track called when the user released the mouse in a valid
     * location for the track
     * */
    public boolean placeTrack(DefaultTrack track){
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
                    if(!j.inBound()){
                        j.setTo(allTracks.size()-1);
                    }

                    if(j.inBound()){
                        j.setInboundTo(allTracks.size()-1);
                    }
                    return true;
                }
            }
            else {
                if(t.canConnect(track)){
                    t.setTo(allTracks.size()-1);
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
        // Go through all util.tracks checking if they have a destination -1 indicated it does not
        for(int i = 0; i < allTracks.size(); i++){
            DefaultTrack t = allTracks.get(i);
            if(t.getTo() == -1){
                findTrackForConnection(t);
            }
        }
    }



    /**
     * It searches the util.tracks in the util.tracks array for a track that matches up with the end
     * of the track passed in.
     * */
    public DefaultTrack findTrackForConnection(DefaultTrack trackWithoutTo){

        // Check if the track without a destination is a junction track
        if(trackWithoutTo instanceof JunctionTrack){
            // Try connect the trown bit
            JunctionTrack j = (JunctionTrack)trackWithoutTo;
            for(int i = 0; i < allTracks.size(); i++){
                if(j.canConnectThrown(allTracks.get(i))){
                    j.setOutboundToThrown(i);
                    allTracks.get(i).setFrom(getTrackIndex(j));// TODO not sure how to find if it's junc from or from
                }
            }
        }


        for(int i = 0; i < allTracks.size(); i++){
            if(allTracks.get(i) instanceof JunctionTrack){
                if (trackWithoutTo.canConnect(allTracks.get(i))) {//TODO not done
                    trackWithoutTo.setTo(i);
                    ((JunctionTrack) allTracks.get(i)).setInboundFromThrown(getTrackIndex(trackWithoutTo));//TODO THROWN OR NOT?
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
    public void mouseReleased(double x, double y, MouseEvent e) {
        int numbTracks = allTracks.size();

        // Nothing is selected so nothing to put down
        if(mouseSelectedPeice == null)return;

        // Check if there is a piece to put down
        mouseSelectedPeice.setSelected(false);

        if(!(allTracks.size() == 1)){
            // Check for special case of placing down junction track
            if(mouseSelectedPeice instanceof JunctionTrack){
                placeJunctionTrack((JunctionTrack) mouseSelectedPeice);
            }
            else {
                placeTrack(mouseSelectedPeice);
            }
        }


        // The mouse was released but the location for the track was not valid
        if(allTracks.size() < numbTracks){
            curId--;//StraightTrack was removed can free vup ID
        }
        mouseSelectedPeice = null;
    }

    @Override
    public void mouseClicked(double x, double y, MouseEvent e){
        if(e.getButton().equals(MouseButton.SECONDARY)){// Check is a user double clicks on a track if so show track menu
            DefaultTrack s = getTrack(x,y);
            if(e.getClickCount() == 2 && s!= null){
                if(!sectionMode){
                    new ErrorDialog("Sections must have been created before adding trains", "Cannot Place train");

                } else {
                    showTrackMenu(s);
                }
            }
        }

        // If the user is setting the sections set the section selected
        if(sectionMode && e.getButton().equals(MouseButton.PRIMARY)){
            DefaultTrack s = getTrack(x,y);
            if(s!=null){
                s.setSelected(true);
                tracksInSection.add(s);
                s.setColor(Color.YELLOW);
            }
        }
    }

    @Override
    public void mouseMoved(double x, double y, MouseEvent e){}

    @Override
    public void mouseDragged(double x, double y, MouseEvent e) {
        this.mouseLocation.setLocation(x,y);// Update mouse location

        // Highlights the selected piece red or green based on if it is valid to place
        if(mouseSelectedPeice != null){

            // Set where t he piece should be drawn
            mouseSelectedPeice.setMid(x,y);

            // The current track is the only track so any placement is valid
            if(allTracks.size() == 1){
                DefaultTrack.SELECTED_COLOR = Color.GREEN;
                return;
            }

            DefaultTrack.SELECTED_COLOR = Color.RED;
            for(int i = 0; i < allTracks.size(); i++){
                DefaultTrack t = allTracks.get(i);

                // Special case for junction track
                if(t instanceof JunctionTrack){
                    JunctionTrack j = (JunctionTrack)t;
                    if(j.canConnectThrown(mouseSelectedPeice) || j.canConnect(mouseSelectedPeice)){
                        DefaultTrack.SELECTED_COLOR = Color.GREEN;
                        return;
                    }
                }

                if(mouseSelectedPeice instanceof JunctionTrack){
                    JunctionTrack j = (JunctionTrack)mouseSelectedPeice;

                    if(j.inBound()){
                        DefaultTrack innnerTrack = j.getInnerTrack();
                        if(t.canConnect(innnerTrack)){
                            DefaultTrack.SELECTED_COLOR = Color.GREEN;
                            return;
                        }

                    }
                }

                if(t.canConnect(mouseSelectedPeice)){
                    DefaultTrack.SELECTED_COLOR = Color.GREEN;
                    return;
                }
            }
        }
    }

    /**
     * Returns a new track of the type selected in the track panel
     * */
    public DefaultTrack getSelectedTrackFromPanel(int x, int y){
        trackLength = 10;
        DefaultTrack[] trackChoices = new DefaultTrack[]{
                new StraightHoriz(x,y, trackLength,0,curId, "RIGHT"),
                new Quart1(x,y, trackLength*2,1, "RIGHT", curId),
                new Quart2(x,y, trackLength*2,2, "DOWN", curId),
                new Quart3(x,y, trackLength*2,3, "LEFT", curId),
                new Quart4(x,y, trackLength*2,4, "UP", curId),
                new StraightVert(x,y, trackLength,5, "DOWN",curId),
                new JunctionTrack(x,y, trackLength, 6,curId, "RIGHT",false,true, "UP"),
                new JunctionTrack(x,y, trackLength, 6,curId, "RIGHT",false,false, "UP"),
                new JunctionTrack(x,y, trackLength, 6,curId, "RIGHT",false,true, "DOWN"),
                new JunctionTrack(x,y, trackLength, 6,curId, "RIGHT",false,false, "DOWN")
        };

        return trackChoices[selectedBox];
    }

    /**
     * Returns the index of the track in the util.tracks array -1
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
        return  x > shownPanelStartX;
    }

    /**
     * A list of util.tracks to show to the user to choose from on the UI
     * */
    public List<DefaultTrack> setUpDrawPieces(){
        List<DefaultTrack> sections = new ArrayList<>();

        double x = (shownPanelStartX + (boxSize/2) + boxGap);//Start of the box to draw in
        double y = 10 + boxGap + (boxSize/2) - DefaultTrack.TRACK_WIDTH/2;
        double size = (boxSize - (boxGap)) / Simulation.METER_MULTIPLIER;

        DefaultTrack[] trackChoices = new DefaultTrack[]{
                new StraightHoriz((int)x,(int)y, (int)size,0,0, "RIGHT"),
                new Quart1((int)x,(int)y, (int)size,1,"RIGHT",0 ),
                new Quart2((int)x,(int)y, (int)size,2,"RIGHT",0 ),
                new Quart3((int)x,(int)y, (int)size,3,"RIGHT",0 ),
                new Quart4((int)x,(int)y, (int)size,4,"RIGHT",0 ),
                new StraightVert((int)x,(int)y, (int)size,5,"RIGHT",0),
                new JunctionTrack((int)x,(int)y, (int)size,6,0,"RIGHT",false,true, "UP"),
                new JunctionTrack((int)x,(int)y, (int)size,6,0,"RIGHT",false,false, "UP"),
                new JunctionTrack((int)x,(int)y, (int)size,6,0,"RIGHT",false,true, "DOWN"),
                new JunctionTrack((int)x,(int)y, (int)size,6,0,"RIGHT",false,false, "DOWN")
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
                mouseSelectedPeice = rotatePiece();
            }
        }
    }

    /**
     * Returns the next track based on the currently selected track
     *
     * @return the next track in the list
     * */
    public DefaultTrack rotatePiece(){
        selectedBox = (selectedBox == 9 ? selectedBox = 0 : selectedBox++);
        allTracks.remove(allTracks.size()-1);
        DefaultTrack t = getSelectedTrackFromPanel((int)mouseLocation.getX(),(int)mouseLocation.getY());
        t.setMid(mouseLocation.getX(),mouseLocation.getY());
        allTracks.add(t);
        mouseSelectedPeice = t;
        mouseSelectedPeice.setSelected(true);
        return t;
    }

    /**
     * Adds the appropriate UI elements for the builder to the border pane
     *
     * @param bp the border pane to add elements to
     * */
    public void addUIElementsToLayout(BorderPane bp){}

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
     *
     * @return The VBox holding the buttons
     * */
    private VBox createBuilderButtons(){
        VBox vBox = new VBox(8);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.setPrefWidth(WIDTH);
        return vBox;
    }

    public void update(){}
}

