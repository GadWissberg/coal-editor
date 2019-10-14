package view;

import com.vividsolutions.jts.geom.Envelope;
import data.AssetsManager;
import data.FileManager;
import data.handlers.DrawingHandler;
import data.handlers.LevelElementsManager;
import data.handlers.ModeChangedListener;
import data.handlers.ModeHandler;
import data.utils.C;
import data.utils.C.Views.LevelViewSection;
import data.utils.C.Views.LevelViewSection.ActorDisplay;
import data.utils.C.Views.LevelViewSection.ElementColor;
import data.utils.C.Views.LevelViewSection.LineDisplay;
import data.utils.C.Views.LevelViewSection.VertexDisplay;
import data.utils.Utils;
import elements.Actor;
import elements.Element;
import elements.Line;
import elements.Vertex;
import elements.sectors.Sector;
import utils.*;
import view.dialogs.ActorDialog;
import view.dialogs.LevelElementDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LevelView extends JPanel implements ModeChangedListener, MapLoadSubscriber {
    private static boolean SNAP_TO_GRID = true;
    private static int gridSize = LevelViewSection.DEFAULT_GRID_SIZE;
    private static String currentLevel;

    private final ModeHandler modehandler;
    private final DrawingHandler drawingHandler;
    private final LevelElementsManager levelElementsManager;
    private Envelope auxEnvelope = new Envelope();
    private Element highlighted;
    private ArrayList<Element> selectedElements = new ArrayList<>();
    private Point drawingMousePosition = new Point();
    private AffineTransform transAT = new AffineTransform();
    private Stroke selectedLineStroke = new BasicStroke(2);
    private Stroke regularLineStroke = new BasicStroke(1);
    private Rectangle currentGridCell = new Rectangle();
    private FileManager fileManager;

    public LevelView(Dimension size, ModeHandler modeHandler) {
        this.modehandler = modeHandler;
        initializeView(size);
        initializeInput();
        levelElementsManager = new LevelElementsManager();
        drawingHandler = new DrawingHandler(levelElementsManager);
        fileManager = new FileManager(levelElementsManager);
        fileManager.subscribeForMapLoad(this);
    }

    public static boolean isSnapToGrid() {
        return SNAP_TO_GRID;
    }

    public static void setSnapToGrid(boolean value) {
        SNAP_TO_GRID = value;
    }

    public static float getGridSize() {
        return gridSize;
    }

    private void initializeInput() {
        LevelViewMouseEventsHandler mouseListener = new LevelViewMouseEventsHandler();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    private void initializeView(Dimension size) {
        setPreferredSize(size);
        setLayout(new BorderLayout(0, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= getWidth(); i += gridSize) g.drawLine(i, 0, i, getHeight());
        for (int i = 0; i <= getHeight(); i += gridSize) g.drawLine(0, i, getWidth(), i);
        drawLevelElements((Graphics2D) g);
        drawSketch((Graphics2D) g);
    }

    private void drawLevelElements(Graphics2D g) {
        Collection<LevelElement> lines = levelElementsManager.getLines().values();
        Collection<LevelElement> sectors = levelElementsManager.getSectors().values();
        drawSectors(g, sectors);
        drawLines(g, lines);
        drawVertices(g);
        drawActors(g);
    }

    private void drawActors(Graphics2D g) {
        Collection<LevelElement> actors = levelElementsManager.getActors().values();
        for (LevelElement element : actors) {
            Actor actor = (Actor) element;
            try {
                ElementColor elementColor = ElementColor.valueOf(actor.getType().name());
                Color newCol = getElementColor(actor, elementColor);
                BufferedImage img = AssetsManager.getImageColorized(ActorDisplay.ACTOR_IMAGE, newCol);
                drawActorIcon(g, actor, img);
                if (newCol == elementColor.getSelected() || newCol == elementColor.getBoth())
                    drawActorOutline(g, actor, img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawActorIcon(Graphics2D g, Actor actor, BufferedImage img) throws IOException {
        int x = (int) (actor.getX() - img.getWidth() / 2);
        int y = (int) (actor.getDisplayY() - img.getHeight() / 2);
        g.drawImage(img, null, x, y);
        drawActorDirectionImage(g, actor, x, y);
    }

    private void drawActorDirectionImage(Graphics2D g, Actor actor, int x, int y) throws IOException {
        String image = actor.getType().hasDirection() ? ActorDisplay.ACTOR_DIR_IMAGE : ActorDisplay.ACTOR_NO_DIR_IMAGE;
        BufferedImage dirImage = AssetsManager.getImage(image);
        transAT.setTransform(g.getTransform());
        transAT.setToTranslation(x, y);
        int pivotX = dirImage.getWidth() >> 1;
        int pivotY = dirImage.getHeight() >> 1;
        transAT.rotate(Math.toRadians(360 - actor.getDirection()), pivotX, pivotY);
        g.drawImage(dirImage, transAT, this);
    }

    private void drawVertices(Graphics2D g) {
        Collection<LevelElement> vertices = levelElementsManager.getVertices().values();
        for (LevelElement vertex : vertices) {
            Color color = getElementColor(vertex, ElementColor.VERTEX);
            boolean outline = color == ElementColor.VERTEX.getSelected() || color == ElementColor.VERTEX.getBoth();
            drawVertex(g, (Vertex) vertex, color, outline);
        }
    }

    private void drawLines(Graphics2D g, Collection<LevelElement> lines) {
        for (LevelElement element : lines) {
            Line line = (Line) element;
            Color regular = line.isSolid() ? LineDisplay.Colors.SOLID : LineDisplay.Colors.PASSABLE;
            Color color = getElementColor(line, ElementColor.LINE, regular);
            drawLine(g, line, color, color == regular ? regularLineStroke : selectedLineStroke);
            drawLineNormal(g, line, color);
        }
    }

    private void drawLineNormal(Graphics2D g, Line line, Color lineColor) {
        g.setColor(lineColor);
        float normalDirection = line.getNormalDirection();
        double sin = Math.sin(Math.toRadians(normalDirection));
        double cos = Math.cos(Math.toRadians(normalDirection));
        int centerX = (int) (line.getX() + line.getWidth() / 2);
        int centerY = (int) (C.Data.LEVEL_SIZE_PIXELS - (line.getY() + line.getHeight() / 2));
        g.drawLine(centerX, centerY,
                (int) (centerX + cos * LineDisplay.NORMAL_LENGTH), (int) (centerY - sin * LineDisplay.NORMAL_LENGTH));
    }

    private void drawSectors(Graphics2D g, Collection<LevelElement> sectors) {
        if (ModeHandler.getCurrentMode() != view.modes.Mode.SECTORS) return;
        for (LevelElement element : sectors) {
            Color sectorColor = getElementColor(element, ElementColor.SECTOR);
            if (sectorColor != null) {
                g.setColor(sectorColor);
                g.fill(((Sector) element).getArea());
            }
        }
    }

    private Color getElementColor(LevelElement element, ElementColor elementColor) {
        return getElementColor(element, elementColor, elementColor.getRegular());
    }

    private Color getElementColor(LevelElement element, ElementColor elementColor, Color regular) {
        boolean isHighlighted = highlighted == element;
        boolean isSelected = selectedElements.contains(element);
        Color result = regular;
        if (isHighlighted && isSelected) result = elementColor.getBoth();
        else if (isHighlighted) result = elementColor.getHighlight();
        else if (isSelected) result = elementColor.getSelected();
        return result;
    }

    private void drawSketch(Graphics2D g) {
        if (ModeHandler.getCurrentMode() == view.modes.Mode.DRAWING) {
            drawSketchVertices(g);
            drawSketchLines(g);
            if (drawingHandler.getSketchVertices().size() != 0) {
                drawSketchPlannedLine(g);
            }
        }
    }

    private void drawSketchPlannedLine(Graphics2D g) {
        Vertex lastVertex = drawingHandler.getPath().get(drawingHandler.getPath().size() - 1);
        int drawingMousePositionX = (int) drawingMousePosition.getX();
        int drawingMousePositionY = (int) drawingMousePosition.getY();
        g.drawLine((int) lastVertex.getX(), (int) (lastVertex.getDisplayY()),
                drawingMousePositionX, drawingMousePositionY);
        drawSketchPlannedLineSizeString(g, drawingMousePositionX, drawingMousePositionY, lastVertex);
    }

    public void magnetMouse(float x, float displayY) {
        findCurrentGridCell(x, displayY);
        List<Vertex> nearbyVertices = levelElementsManager.getVerticesInArea(currentGridCell);
        List<Line> nearbyLines = levelElementsManager.getLinesInArea(currentGridCell);
        nearbyVertices.addAll(drawingHandler.getSketchVertices());
        nearbyLines.addAll(drawingHandler.getSketchLines());
        double minDistance = Double.MAX_VALUE;
        double y = C.Data.LEVEL_SIZE_PIXELS - displayY;
        minDistance = magnetToGridPoint(x, minDistance, y, currentGridCell.getMaxX(), currentGridCell.getMaxY());
        minDistance = magnetToGridPoint(x, minDistance, y, currentGridCell.getMinX(), currentGridCell.getMaxY());
        minDistance = magnetToGridPoint(x, minDistance, y, currentGridCell.getMinX(), currentGridCell.getMinY());
        minDistance = magnetToGridPoint(x, minDistance, y, currentGridCell.getMaxX(), currentGridCell.getMinY());
        for (Vertex vertex : nearbyVertices) {
            if (currentGridCell.contains(vertex.getX(), vertex.getY())) {
                minDistance = magnetToGridPoint(x, minDistance, y, vertex.getX(), vertex.getY());
            }
        }
        for (Line line : nearbyLines) {
            VertexElement src = line.getSrc();
            VertexElement dst = line.getDst();
            if (currentGridCell.intersects(line.getX(), line.getY(), line.getWidth(), line.getHeight())) {
                float slope = (src.getY() - dst.getY()) / (src.getX() - dst.getX());
                float b = src.getY() - slope * src.getX();
                double yTop = currentGridCell.getMaxY();
                if (line.getY() <= yTop && line.getY() + line.getHeight() >= yTop) {
                    minDistance = magnetToGridPoint(x, minDistance, y, ((yTop - b) / slope), yTop);
                }

                double xRight = currentGridCell.getMaxX();
                if (line.getX() <= xRight && line.getX() + line.getWidth() >= xRight) {
                    minDistance = magnetToGridPoint(x, minDistance, y, xRight, slope * xRight + b);
                }
            }
        }
    }

    private double magnetToGridPoint(float x, double minDistance, double y, double candidateX, double candidateY) {
        double diffX = x - candidateX;
        double diffY = y - candidateY;
        double distance = Math.hypot(diffX, diffY);
        if (distance < minDistance) {
            minDistance = distance;
            drawingMousePosition.setLocation(candidateX, C.Data.LEVEL_SIZE_PIXELS - candidateY);
        }
        return minDistance;
    }

    private void findCurrentGridCell(float x, float displayY) {
        int closestX = SharedUtils.roundToClosestSide(x, gridSize);
        int closestY = SharedUtils.roundToClosestSide(displayY, gridSize);
        int cellX = x >= closestX ? closestX : closestX - gridSize;
        int cellY = displayY >= closestY ? closestY : closestY - gridSize;
        currentGridCell.setRect(cellX, cellY, gridSize, gridSize);
        currentGridCell.y = C.Data.LEVEL_SIZE_PIXELS - currentGridCell.y;
        currentGridCell.y -= gridSize;
    }


    private boolean magnetToLineIfCloseEnough(float x, float dataY, Line nearbyLine) {
        VertexElement src = nearbyLine.getSrc();
        VertexElement dst = nearbyLine.getDst();
        if (Line2D.ptSegDist(src.getX(), src.getY(), dst.getX(), dst.getY(), x, dataY) < LevelViewSection.MAGNET_DIST) {
            Utils.getClosestPointOnSegment(src.getX(), src.getY(), dst.getX(), dst.getY(), x, dataY, drawingMousePosition, gridSize);
            drawingMousePosition.setLocation(drawingMousePosition.getX(), C.Data.LEVEL_SIZE_PIXELS - drawingMousePosition.getY());
            return true;
        }
        return false;
    }

    private boolean magnetToNearestVertex(float correctMouseX, float dataY, List<Vertex> nearbyVertices) {
        for (Vertex nearbyVertex : nearbyVertices) {
            if (nearbyVertex.equals(correctMouseX, dataY, LevelViewSection.MAGNET_DIST)) {
                drawingMousePosition.setLocation(nearbyVertex.getX(), nearbyVertex.getDisplayY());
                return true;
            }
        }
        return false;
    }

    private void drawSketchPlannedLineSizeString(Graphics2D g, int fixedMouseX, int fixedMouseY, Vertex lastVertex) {
        double lineSizeLabelX = lastVertex.getX() + (fixedMouseX - lastVertex.getX()) / 2;
        double lineSizeLabelY = lastVertex.getDisplayY() + (fixedMouseY - lastVertex.getDisplayY()) / 2;
        double pit = Math.pow(fixedMouseX - lastVertex.getX(), 2) + Math.pow(fixedMouseY - lastVertex.getDisplayY(), 2);
        int size = (int) Math.sqrt(pit);
        g.drawString(String.valueOf(size), (float) lineSizeLabelX, (float) lineSizeLabelY);
    }

    private void drawSketchLines(Graphics2D g) {
        List<Line> lines = drawingHandler.getSketchLines();
        for (Line line : lines) {
            drawLine(g, line, LevelViewSection.DRAWING_COLOR, regularLineStroke);
        }
    }

    private void drawLine(Graphics2D g, Line line, Color color, Stroke stroke) {
        g.setColor(color);
        Vertex src = (Vertex) line.getSrc();
        Vertex dst = (Vertex) line.getDst();
        g.setStroke(stroke);
        g.drawLine((int) src.getX(), (int) src.getDisplayY(), (int) dst.getX(), (int) dst.getDisplayY());
        g.setStroke(regularLineStroke);
    }

    private void drawSketchVertices(Graphics2D g) {
        List<Vertex> all = drawingHandler.getSketchVertices();
        for (Vertex vertex : all) {
            drawVertex(g, vertex, LevelViewSection.DRAWING_COLOR, false);
        }
    }

    private void drawVertex(Graphics2D g, Vertex vertex, Color color, boolean outline) {
        g.setColor(color);
        Ellipse2D.Double circle = new Ellipse2D.Double(
                vertex.getX() - (VertexDisplay.VERTEX_DIAMETER >> 1),
                vertex.getDisplayY() - (VertexDisplay.VERTEX_DIAMETER >> 1),
                VertexDisplay.VERTEX_DIAMETER, VertexDisplay.VERTEX_DIAMETER);
        g.fill(circle);
        if (outline) drawVertexOutline(g, vertex);
    }

    private void drawVertexOutline(Graphics2D g, Vertex vertex) {
        g.setStroke(selectedLineStroke);
        g.setColor(C.Views.LevelViewSection.OUTLINE_COLOR);
        g.drawOval((int) (vertex.getX() - (VertexDisplay.VERTEX_DIAMETER >> 1)),
                (int) (vertex.getDisplayY() - (VertexDisplay.VERTEX_DIAMETER >> 1)),
                VertexDisplay.VERTEX_DIAMETER, VertexDisplay.VERTEX_DIAMETER);
    }

    private void drawActorOutline(Graphics2D g, Actor ac, BufferedImage img) {
        g.setStroke(selectedLineStroke);
        g.setColor(C.Views.LevelViewSection.OUTLINE_COLOR);
        int width = img.getWidth();
        g.drawOval((int) (ac.getX() - (width >> 1)), (int) (ac.getDisplayY() - (width >> 1)), width, img.getHeight());
    }

    public void deleteSelectedItems() {
        if (ModeHandler.getCurrentMode() == view.modes.Mode.SECTORS) {
            deleteSelectedSectors();
        }
    }

    private void deleteSelectedSectors() {
        ArrayList<Line> linesToDelete = new ArrayList<Line>();
        for (Element element : selectedElements) {
            deleteSector(linesToDelete, (Sector) element);
        }
        selectedElements.clear();
        repaint();
    }

    private void deleteSector(ArrayList<Line> linesToDelete, Sector element) {
        Sector sector = element;
        Rectangle area = new Rectangle((int) sector.getX(), (int) sector.getY(),
                (int) sector.getWidth(), (int) sector.getHeight());
        List<Line> nearbyLines = levelElementsManager.getLinesInArea(area);
        for (Line nearbyLine : nearbyLines) updateLineOfDeletedSector(linesToDelete, sector, nearbyLine);
        for (Line line : linesToDelete) levelElementsManager.removeLine(line);
        levelElementsManager.removeSector(sector);
        linesToDelete.clear();
    }

    private void updateLineOfDeletedSector(ArrayList<Line> linesToDelete, Sector sector, Line nearbyLine) {
        long frontSectorId = nearbyLine.getFrontSectorId();
        long backSectorId = nearbyLine.getBackSectorId();
        if (frontSectorId == sector.getId() || backSectorId == sector.getId())
            if (frontSectorId == C.Data.SKETCH_ID || backSectorId == C.Data.SKETCH_ID) {
                linesToDelete.add(nearbyLine);
                return;
            }
        updateLineSidesOfDeletedSector(nearbyLine, sector.getId(), frontSectorId, backSectorId);
    }

    private void updateLineSidesOfDeletedSector(Line nearbyLine, long sectorId, long frontSectorId, long backSectorId) {
        if (frontSectorId == sectorId) {
            updateLineAttributesOfDeletedSector(nearbyLine, backSectorId, C.Data.SKETCH_ID);
        } else if (backSectorId == sectorId) {
            updateLineAttributesOfDeletedSector(nearbyLine, frontSectorId, C.Data.SKETCH_ID);
        }
    }

    private void updateLineAttributesOfDeletedSector(Line nearbyLine, long newFrontSectorId, long newBackSectorId) {
        if (nearbyLine.getFrontSectorId() != newFrontSectorId)
            nearbyLine.flipVertices();
        nearbyLine.setBackSectorId(newBackSectorId);
        nearbyLine.setFrontSectorId(newFrontSectorId);
        nearbyLine.setSolid(newFrontSectorId < 0 || newBackSectorId < 0);
    }

    public ArrayList<Element> getSelectedElements() {
        return selectedElements;
    }

    void beginDrawing() {
        selectedElements.clear();
        float x = (float) drawingMousePosition.getX();
        float dataY = (float) (C.Data.LEVEL_SIZE_PIXELS - drawingMousePosition.getY());
        Element element = drawingHandler.begin(x, dataY, modehandler);
        if (ModeHandler.getCurrentMode() == view.modes.Mode.ACTORS) {
            handleActorAdded(element);
        }
        repaint();
    }

    private void handleActorAdded(Element element) {
        Actor a = (Actor) element;
        selectedElements.add(element);
        calculateValuesForActor(a);
        ActorDialog pane = new ActorDialog(selectedElements, getLevelElementsManager());
        openNewDialog(pane, new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                pane.onClose();
            }
        });
    }

    public void openNewDialog(LevelElementDialog pane) {
        openNewDialog(pane, null);
    }

    void openNewDialog(LevelElementDialog pane, WindowListener windowListener) {
        MainEditorWindow root = (MainEditorWindow) SwingUtilities.getWindowAncestor(this);
        JDialog window = root.createNewDialog(pane, new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                highlighted = null;
                repaint();
            }
        });
        window.addWindowListener(windowListener);
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.setVisible(true);
    }

    public void calculateValuesForActor(Actor a) {
        calculateActorAltitudes(a);
        a.setCurrentSectorId(findContainingSector(a));
    }

    private void calculateActorAltitudes(Actor a) {
        float floorAlt = calculateActorCurrentSurfaceAltitude(a, (current, s) -> Math.max(current, s.getFloorAltitude()), 0);
        a.setCurrentFloorAltitude(floorAlt);
        float ceilAlt = calculateActorCurrentSurfaceAltitude(a, (current, s) -> Math.min(current, s.getCeilingAltitude()),
                Float.MAX_VALUE);
        a.setCurrentCeilingAltitude(ceilAlt);
    }

    private long findContainingSector(Actor a) {
        Rectangle2D.Float bounds2D = new Rectangle2D.Float(a.getX(), a.getY(), 1, 1);
        List<Sector> nearbySectors = levelElementsManager.getSectorsInArea(bounds2D, false);
        for (Sector sector : nearbySectors) {
            if (sector.getArea().contains(a.getX(), a.getDisplayY())) {
                return sector.getId();
            }
        }
        return -1;
    }

    private float calculateActorCurrentSurfaceAltitude(Actor actor, ExtremeValueCalculation extremeValueCalc,
                                                       float beginExtreme) {
        Rectangle2D.Float zone = new Rectangle2D.Float(actor.getX() - actor.getWidth() / 2,
                actor.getY() - actor.getHeight() / 2, actor.getWidth(), actor.getHeight());
        List<Sector> nearbySectors = levelElementsManager.getSectorsInArea(zone, false);
        float extremeValue = beginExtreme;
        zone.setRect(zone.getMinX(), C.Data.LEVEL_SIZE_PIXELS - zone.getMaxY(), zone.getWidth(), zone.getHeight());
        for (Sector s : nearbySectors)
            extremeValue = (s.getArea().intersects(zone)) ? extremeValueCalc.calculate(extremeValue, s) : extremeValue;
        return extremeValue;
    }


    public Element getHighlight() {
        return highlighted;
    }

    public Point getDrawingMousePosition() {
        return drawingMousePosition;
    }

    public DrawingHandler getDrawingHandler() {
        return drawingHandler;
    }

    public void refreshDrawingMousePosition() {
        Point locationOnScreen = getLocationOnScreen();
        double x = MouseInfo.getPointerInfo().getLocation().getX() - locationOnScreen.getX();
        double y = MouseInfo.getPointerInfo().getLocation().getY() - locationOnScreen.getY();
        getDrawingMousePosition().setLocation(x, y);
    }

    public static void setGridSize(int value) {
        gridSize = value;
    }

    @Override
    public void modeChanged(view.modes.Mode selectedMode) {
        selectedElements.clear();
        if (drawingHandler.isDrawing()) drawingHandler.reset();
        repaint();
    }


    public FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public void onMapLoad(String name) {
        repaint();
    }

    private class LevelViewMouseEventsHandler implements MouseListener, MouseMotionListener {
        private static final String SECTOR_CREATION_FAILURE = "Failed to spawn sector from the sketch!";

        @Override
        public void mouseClicked(MouseEvent e) {
            if (ModeHandler.getCurrentMode() != view.modes.Mode.DRAWING)
                if (highlighted != null) {
                    if (selectedElements.contains(highlighted)) {
                        selectedElements.remove(highlighted);
                    } else {
                        selectedElements.add(highlighted);
                    }
                    repaint();
                }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                handleRightButtonPress(e);
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                takeStepInDrawing();
            }
        }

        private void handleRightButtonPress(MouseEvent e) {
            if (highlighted == null) {
                handleDrawingForRightClick(e);
            } else {
                selectedElements.add(highlighted);
                ModeHandler.getCurrentMode().onMouseRightPressed(LevelView.this, e);
            }
        }

        private void handleDrawingForRightClick(MouseEvent e) {
            if (drawingHandler.isDrawing()) {
                finishDrawing();
            } else {
                view.modes.Mode.DRAWING.onMouseMoved(LevelView.this, e);
                beginDrawing();
            }
        }

        private void takeStepInDrawing() {
            if (ModeHandler.getCurrentMode() == view.modes.Mode.DRAWING && drawingHandler.isDrawing()) {
                float x = (float) drawingMousePosition.getX();
                float dataY = (float) (C.Data.LEVEL_SIZE_PIXELS - drawingMousePosition.getY());
                if (drawingHandler.takeStep(x, dataY)) {
                    finishDrawing();
                }
                repaint();
            }
        }

        private void finishDrawing() {
            try {
                drawingHandler.spawnSectorFromSketch();
                repaint();
            } catch (Line.NegligibleLineException e1) {
                e1.printStackTrace();
                System.out.println(SECTOR_CREATION_FAILURE);
            }
            modehandler.setCurrentMode(ModeHandler.getPrevMode());
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
            view.modes.Mode currentMode = ModeHandler.getCurrentMode();
            if (!currentMode.onMouseMoved(LevelView.this, e) && (currentMode.isHighlightingElements())) {
                setHighlightedElement(null);
            }
        }

    }

    public boolean highlightElementBehindCursor(MouseEvent e, LevelElementDataStructure elements, Comparison cmp) {
        int x1 = e.getX() - LevelViewSection.CURSOR_QUAD_MAP_RADIUS;
        int x2 = e.getX() + LevelViewSection.CURSOR_QUAD_MAP_RADIUS;
        int y1 = C.Data.LEVEL_SIZE_PIXELS - (e.getY() + LevelViewSection.CURSOR_QUAD_MAP_RADIUS);
        int y2 = C.Data.LEVEL_SIZE_PIXELS - (e.getY() - LevelViewSection.CURSOR_QUAD_MAP_RADIUS);
        auxEnvelope.init(x1, x2, y1, y2);
        List<Element> nearbyElements = elements.getQuadTree().query(auxEnvelope);
        for (Element element : nearbyElements)
            if (cmp.compare(element, e)) {
                setHighlightedElement(element);
                return true;
            }
        return false;
    }

    public LevelElementsManager getLevelElementsManager() {
        return levelElementsManager;
    }

    public interface Comparison<T> {
        boolean compare(T element, MouseEvent e);
    }

    private void setHighlightedElement(Element element) {
        this.highlighted = element;
    }

    private interface ExtremeValueCalculation {
        float calculate(float currentExtremeValue, Sector sector);
    }

    private class GridSegmentSide {
    }
}
