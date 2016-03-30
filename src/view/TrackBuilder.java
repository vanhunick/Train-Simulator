package view;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import model.Section;
import view.Drawable.track_types.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 26/03/2016.
 */
public class TrackBuilder {
    public static final int NUMB_PIECES = 6;

    private boolean hidden;
    private int selectedBox;
    private double boxSize;

    private double hiddenPanelSze = 20;
    private double hiddenPanelStartX;
    private double hiddenPanelStartY;

    private double shownPanelStartX;
    private double shownPanelStartY;

    private double screenWidth;
    private double screenHeight;

    private List<DefSection> sections;
    private List<DefSection> sectionsForTrack;

    private double boxGap = 10;

    public TrackBuilder(List<DefSection> sections){
        this.hidden = true;
        this.sectionsForTrack = sections;

        //In constructor so the creation of pieces know where to start drawing
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//        screenWidth = primaryScreenBounds.getWidth();
//        screenHeight = primaryScreenBounds.getHeight();
        screenWidth = Main.SCREEN_WIDTH;
        screenHeight = Main.SCREEN_HEIGHT;

        this.hiddenPanelStartX = screenWidth - 20 - hiddenPanelSze;
        this.shownPanelStartX = screenWidth - boxSize - boxGap*2;
        this.shownPanelStartY = 20;
        this.hiddenPanelStartY = 20;

        hiddenPanelStartX = screenWidth - 20 - hiddenPanelSze;

        shownPanelStartX = screenWidth - (boxSize - (boxGap*2));
        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);
        this.sections = setUpDrawPieces();
    }

    public void draw(GraphicsContext gc){
//        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
//        screenWidth = primaryScreenBounds.getWidth();
//        screenHeight = primaryScreenBounds.getHeight();
//

        this.boxSize = ((screenHeight - 50 - ((NUMB_PIECES*boxGap)+boxGap))/NUMB_PIECES);

        this.shownPanelStartX = screenWidth - (boxSize + (boxGap*2));

        hiddenPanelStartX = screenWidth - 20 - hiddenPanelSze;

        if(hidden){
            drawHiddenPanel(gc);
        }
        else {
            drawAddButton(gc);
            drawShownPanel(gc);
        }
    }

    public void drawHiddenPanel(GraphicsContext gc){
        gc.setFill(Color.GREY);
        gc.setStroke(Color.WHITE);
        gc.fillRect(hiddenPanelStartX, hiddenPanelStartY, hiddenPanelSze + 10, hiddenPanelSze);
        gc.strokeRect(hiddenPanelStartX, hiddenPanelStartY, hiddenPanelSze+ 10, hiddenPanelSze);
    }

    public void drawShownPanel(GraphicsContext gc){
        gc.setFill(Color.WHITE);
//        gc.setGlobalAlpha(0.9);

        gc.fillRect(shownPanelStartX, 20, screenWidth - shownPanelStartX, ((boxSize+boxGap)*NUMB_PIECES)+boxGap);//TODO make less silly'
        gc.setStroke(Color.ORANGE);
        gc.strokeRect(shownPanelStartX, 20, screenWidth - shownPanelStartX, ((boxSize+boxGap)*NUMB_PIECES)+boxGap);

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

    private double addButtonWidth;
    private double addButtonHeight;
    private double startXAddButon;
    private double startYAddButon;


    public void drawAddButton(GraphicsContext gc){
        startYAddButon = 20;//just the end of the panel
        startXAddButon = shownPanelStartX - 80;
        addButtonHeight = 30;
        addButtonWidth = 70;

        gc.setStroke(Color.WHITE);
        gc.setFill(Color.GREY);
        gc.setStroke(Color.BLACK);
        gc.fillText("Add",startXAddButon + 15, startYAddButon + 10);
        gc.fillRect(startXAddButon, startYAddButon, addButtonWidth,addButtonHeight);
        gc.strokeRect(startXAddButon, startYAddButon, addButtonWidth,addButtonHeight);
    }

    public List<DefSection> setUpDrawPieces(){
        List<DefSection> sections = new ArrayList<>();

        double middleX = (screenWidth - boxSize - boxGap) + (boxSize/2);//Start of the box to draw in
        double middleY = 20 + boxGap + (boxSize/2);

        double size = boxSize - (boxGap);
        System.out.println(boxSize);
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

    public void mouseClicked(double x, double y){
        if(oniddenPanel(x,y)){
            hidden = !hidden;
            System.out.printf("Pressing on hidden button");
        }
        else if(!hidden && oneShownPanel(x,y)){
            selectPiece(x,y);
        }
        else if(!hidden && onAddButton(x,y)){
            System.out.println("Adding piece");
            addPiece();
        }
    }

    private double pieceSize = 100;
    private double trackStartX = 300;
    private double trackStartY = 80;


    public void addFirstPiece(){
        if(selectedBox == 0){
            DefSection ds0 = new StraightHoriz(new Section(2, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,0, "RIGHT");
            sectionsForTrack.add(ds0);
        }
        else if(selectedBox == 1){
            DefSection ds0 = new Quart1(new Section(2, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,1, "RIGHT");
            sectionsForTrack.add(ds0);
        }
        else if(selectedBox == 2){
            DefSection ds0 = new Quart2(new Section(2, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,2, "DOWN");
            sectionsForTrack.add(ds0);
        }
        else if(selectedBox == 3){
            DefSection ds0 = new Quart3(new Section(2, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,3, "LEFT");
            sectionsForTrack.add(ds0);
        }
        else if(selectedBox == 4){
            DefSection ds0 = new Quart4(new Section(2, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,4, "UP");
            sectionsForTrack.add(ds0);
        }
        else if(selectedBox == 5){
            DefSection ds0 = new StraightVert(new Section(2, 100, null, null, null),(int)trackStartX,(int)trackStartY, (int)pieceSize,5, "DOWN");
            sectionsForTrack.add(ds0);
        }
    }

    public void addPiece(){
        if(sectionsForTrack.size() == 0){
            addFirstPiece();
            return;
        }
        System.out.println(sectionsForTrack);

        if(selectedBox == 0){
            DefSection ds1 = new StraightHoriz(new Section(2, 100, null, null, null), (int)pieceSize,0);
            ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
            sectionsForTrack.add(ds1);
        }
        else if(selectedBox == 1){
            DefSection ds1 = new Quart1(new Section(2, 100, null, null, null), (int)pieceSize,1);
            ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
            sectionsForTrack.add(ds1);
        }
        else if(selectedBox == 2){
            DefSection ds1 = new Quart2(new Section(2, 100, null, null, null), (int)pieceSize,2);
            ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
            sectionsForTrack.add(ds1);
        }
        else if(selectedBox == 3){
            DefSection ds1 = new Quart3(new Section(2, 100, null, null, null), (int)pieceSize,3);
            ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
            sectionsForTrack.add(ds1);
        }
        else if(selectedBox == 4){
            DefSection ds1 = new Quart4(new Section(2, 100, null, null, null), (int)pieceSize,4);
            ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
            sectionsForTrack.add(ds1);
        }
        else if(selectedBox == 5){
            DefSection ds1 = new StraightVert(new Section(2, 100, null, null, null), (int)pieceSize,5);
            ds1.setStart(sectionsForTrack.get(sectionsForTrack.size()-1));
            sectionsForTrack.add(ds1);
        }
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
    }

    public boolean oniddenPanel(double x, double y){
        return x >= hiddenPanelStartX && x <= hiddenPanelStartX + hiddenPanelSze
                && y>= hiddenPanelStartY && y <= hiddenPanelStartY + hiddenPanelSze;
    }

    public void screenHeightChanged(double height){
        screenHeight = height;
    }


    public void screenWidthChanged(double width){
        screenWidth = width;
        setUpDrawPieces();
    }

    public boolean oneShownPanel(double x, double y){
        if(x > shownPanelStartX)return true;
        return false;
    }

    public boolean onAddButton(double x, double y){
        return x > startXAddButon && x < startXAddButon + addButtonWidth &&
                y > startYAddButon && y < startXAddButon + addButtonHeight;
    }
}
