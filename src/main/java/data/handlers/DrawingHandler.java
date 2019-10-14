package data.handlers;

import data.utils.C.Data;
import data.utils.Utils;
import elements.Element;
import elements.Line;
import elements.Vertex;
import elements.sectors.Sector;
import elements.texture.*;
import view.modes.Mode;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class DrawingHandler {

    private final LevelElementsManager levelElementsManager;
    private ArrayList<Vertex> sketchVertices = new ArrayList<>();
    private ArrayList<Vertex> path = new ArrayList<>();
    private List<Line> lines = new ArrayList<>();
    private ArrayList<Sector> sectorsToModify;
    private long latestLineId;

    /**
     * Generates level elements.
     *
     * @param levelElementsManager Uses and updates this map's elemets data.
     */
    public DrawingHandler(LevelElementsManager levelElementsManager) {
        this.levelElementsManager = levelElementsManager;
    }

    /**
     * Begins a drawing process according to current mode.
     *
     * @param x           X coordinate of the first element to create.
     * @param y           Y coordinate of the first element to create.
     * @param modehandler Uses and updates this map's modes handler.
     * @return The first created element in the drawing process.
     */
    public Element begin(float x, float y, ModeHandler modehandler) {
        if (ModeHandler.getCurrentMode() == Mode.ACTORS) {
            return generateActor(x, y);
        } else {
            modehandler.setCurrentMode(Mode.DRAWING);
            Vertex begin = obtainNewVertex(x, y);
            path.add(begin);
            return begin;
        }
    }

    private Element generateActor(float x, float y) {
        return levelElementsManager.generateNewActor(x, y);
    }

    private Vertex obtainNewVertex(float x, float y) {
        Vertex vertex = checkIfVertexExistsInPosition(x, y);
        if (vertex == null) {
            vertex = new Vertex(x, y, Data.SKETCH_ID);
            sketchVertices.add(vertex);
        }
        return vertex;
    }

    private Line obtainNewLine(Vertex v1, Vertex v2) throws Line.NegligibleLineException {
        Line line = checkIfLineExists(v1, v2);
        if (line == null) {
            line = new Line(v1, v2, Data.SKETCH_ID, true);
            lines.add(line);
        }
        return line;
    }

    private Line checkIfLineExists(Vertex v1, Vertex v2) {
        Line line = null;
        for (Line other : lines) {
            if (other.compareTo(v1, v2)) {
                line = other;
                break;
            }
        }
        return line;
    }

    private Vertex checkIfVertexExistsInPosition(float x, float y) {
        Vertex answer = null;
        for (Vertex other : sketchVertices)
            if (other.equals(x, y)) {
                answer = other;
                break;
            }
        return answer;
    }

    /**
     * The current's drawing process vertices (always empty if Actors mode is selected).
     *
     * @return A list of all sketch vertices.
     */
    public List<Vertex> getSketchVertices() {
        return sketchVertices;
    }

    /**
     * Takes a single step in the current drawing process.
     *
     * @return Whether the new step has finished the current drawing process.
     */
    public boolean takeStep(float x, float y) {
        Vertex lastVertex = path.get(path.size() - 1);
        Vertex newVertex = obtainNewVertex(x, y);
        boolean answer = false;
        try {
            obtainNewLine(lastVertex, newVertex);
            path.add(newVertex);
            answer = path.get(0).equals(newVertex);
        } catch (Line.NegligibleLineException e) {
            System.out.println(e.toString());
        }
        return answer;
    }

    public void spawnSectorFromSketch() throws Line.NegligibleLineException {
        ArrayList<Vertex> tempPath = new ArrayList<>(path);
        int lastSize = tempPath.size();
        while (tempPath.size() > 3) {
            findPolygonInPathAndSectorize(tempPath);
            if (tempPath.size() == lastSize) break;
        }
        reset();
    }

    private void findPolygonInPathAndSectorize(ArrayList<Vertex> tempPath) throws Line.NegligibleLineException {
        for (int i = 2; i < tempPath.size(); i++) {
            for (int j = 0; j < i; j++)
                if (tempPath.get(i).equals(tempPath.get(j))) {
                    spawnSectorFromSketch(tempPath, j, i);
                    Utils.removeRangeFromArray(tempPath, j + 1, i);
                    return;
                }
        }
    }

    private void spawnSectorFromSketch(ArrayList<Vertex> tempPath, int beginIndex, int endIndex)
            throws Line.NegligibleLineException {
        latestLineId = IdsManager.getLastLineId();
        Area area = generateAreaFromSketch(beginIndex, endIndex, tempPath);
        sectorsToModify = updateCollidingSectorsAreas(area);
        cullExistingSectors(sectorsToModify);
        Sector newSector = spawnNewSectorFromSketch(area);
        updateCollidingSectors(sectorsToModify);
        correctModifiedSectorsLinesSidesAndSolidness(sectorsToModify, newSector);
    }

    private void correctModifiedSectorsLinesSidesAndSolidness(ArrayList<Sector> sectorsToModify, Sector newSector) {
        correctSectorLinesSidesAndSolidness(newSector);
        if (sectorsToModify != null) {
            for (Sector sector : sectorsToModify) {
                correctSectorLinesSidesAndSolidness(sector);
            }
        }
    }

    private void correctSectorLinesSidesAndSolidness(Sector sector) {
        if (sector == null) return;
        PathIterator it = sector.getArea().getPathIterator(null);
        float[] coords = new float[8];
        Vertex prevVertex = new Vertex(0, 0, Data.SKETCH_ID);
        Vertex nextVertex = new Vertex(0, 0, Data.SKETCH_ID);
        while (!it.isDone()) {
            takeStepInCorrectingLinesSidesAndSolid(sector, it, coords, prevVertex, nextVertex);
        }
    }

    private void takeStepInCorrectingLinesSidesAndSolid(Sector sector, PathIterator it, float[] coords, Vertex prevVertex,
                                                        Vertex nextVertex) {
        int step = it.currentSegment(coords);
        float currentX = coords[0];
        float currentDataY = Data.LEVEL_SIZE_PIXELS - coords[1];
        if (step == PathIterator.SEG_MOVETO) {
            prevVertex.setPosition(currentX, currentDataY);
            coords[6] = currentX;
            coords[7] = currentDataY;
        } else if (step == PathIterator.SEG_LINETO) {
            nextVertex.setPosition(currentX, currentDataY);
            if (!nextVertex.equals(prevVertex, 0.1f)) {
                correctLineSidesAndSolidness(sector, prevVertex, nextVertex);
                prevVertex.setPosition(currentX, currentDataY);
            }
        } else if (step == PathIterator.SEG_CLOSE) {
            float firstX = coords[6];
            float firstDataY = coords[7];
            if (currentX != firstX || currentDataY != firstDataY) {
                prevVertex.setPosition(firstX, firstDataY);
            }
            correctLineSidesAndSolidness(sector, prevVertex, nextVertex);
        }
        it.next();
    }

    private void correctLineSidesAndSolidness(Sector sector, Vertex prevVertex, Vertex nextVertex) {
        Vertex nextVertexInPath = getClosestVertexInPath(prevVertex, nextVertex);
        applySectorToLineSideAndUpdateSolid(prevVertex, nextVertexInPath, sector);
        if (!nextVertexInPath.equals(nextVertex)) {
            correctLineSidesAndSolidness(sector, nextVertexInPath, nextVertex);
        }
    }

    private void applySectorToLineSideAndUpdateSolid(Vertex prevVertex, Vertex nextVertex, Sector sector) {
        Line line = levelElementsManager.checkIfThereIsLineInPosition(prevVertex, nextVertex);
        if (line != null) {
            long sectorId = sector.getId();
            if (line.getFrontSectorId() == Data.SKETCH_ID) {
                line.setFrontSectorId(sectorId);
            } else {
                double x = line.getX() + line.getWidth() / 2 + Math.cos(Math.toRadians(line.getNormalDirection()));
                double y = line.getY() + line.getHeight() / 2 + Math.sin(Math.toRadians(line.getNormalDirection()));
                if (sector.getArea().contains(x, Data.LEVEL_SIZE_PIXELS - y)) {
                    line.setFrontSectorId(sectorId);
                } else {
                    x = line.getX() + line.getWidth() / 2 - Math.cos(Math.toRadians(line.getNormalDirection()));
                    y = line.getY() + line.getHeight() / 2 - Math.sin(Math.toRadians(line.getNormalDirection()));
                    if (sector.getArea().contains(x, Data.LEVEL_SIZE_PIXELS - y)) {
                        line.setBackSectorId(sectorId);
                    }
                }
            }
            if (line.getBackSectorId() != Data.SKETCH_ID && (line.getId() > latestLineId || sector.getId() == IdsManager.getLastSectorId())) {
                line.setSolid(false);
            }
        }
    }

    private Sector spawnNewSectorFromSketch(Area area) throws Line.NegligibleLineException {
        Sector newSector = new Sector(area, Data.SKETCH_ID);
        if (sectorsToModify != null && sectorsToModify.size() > 0) {
            applySectorAttributesToAnotherSector(sectorsToModify.get(0), newSector);
        }
        newSector = checkIfThereIsSimilarSector(newSector);
        if (newSector.getId() == Data.SKETCH_ID) applyAreaToSector(area, newSector);
        return newSector;
    }

    private void updateCollidingSectors(ArrayList<Sector> sectorsToModify) throws Line.NegligibleLineException {
        if (sectorsToModify != null)
            for (Sector sectorToModify : sectorsToModify)
                applyAreaToSector(sectorToModify.getArea(), sectorToModify);
    }

    private void cullExistingSectors(ArrayList<Sector> sectors) {
        if (sectors == null) return;
        ArrayList<Sector> sectorsToCull = new ArrayList<>();
        for (Sector sector : sectors) {
            if (checkIfThereIsSimilarSector(sector) != sector) {
                sectorsToCull.add(sector);
            }
        }
        sectors.removeAll(sectorsToCull);
    }

    private Sector checkIfThereIsSimilarSector(Sector sector) {
        List<Sector> nearbySectors = levelElementsManager.getSectorsInArea(sector.getBounds(), true);
        for (Sector nearbySector : nearbySectors) {
            if (nearbySector.equals(sector.getArea())) return nearbySector;
        }
        return sector;
    }

    private ArrayList<Sector> updateCollidingSectorsAreas(Area area) {
        if (area.isEmpty()) return null;
        List<Sector> nearbySectors = levelElementsManager.getSectorsInArea(area.getBounds2D(), true);
        if (nearbySectors.size() == 0) return null;
        ArrayList<Sector> sectorsToModify = new ArrayList<>();
        for (Sector nearbySector : nearbySectors) {
            updateCollidingSector(area, sectorsToModify, nearbySector);
        }
        return sectorsToModify;
    }

    private void updateCollidingSector(Area newArea, ArrayList<Sector> sectorsToModify, Sector nearbySector) {
        Area otherSectorArea = nearbySector.getArea();
        Area mutualArea = new Area(newArea);
        mutualArea.intersect(otherSectorArea);
        if (!mutualArea.isEmpty()) {
            if (!isAreaTooSmall(mutualArea)) {
                Area area = new Area(newArea);
                area.subtract(otherSectorArea);
                boolean nearbyContainsNew = area.isEmpty();
                area = new Area(otherSectorArea);
                area.subtract(newArea);
                boolean newContainsNearby = area.isEmpty();
                if (nearbyContainsNew && newContainsNearby) {
                    newArea.subtract(newArea);
                    return;
                }
                if (!nearbyContainsNew) {
                    newArea.subtract(mutualArea);
                }
                if (!newContainsNearby) {
                    otherSectorArea.subtract(mutualArea);
                }
                if (!nearbyContainsNew && !newContainsNearby) {
                    Sector sectorFromMutual = new Sector(mutualArea, Data.SKETCH_ID);
                    applySectorAttributesToAnotherSector(nearbySector, sectorFromMutual);
                    sectorsToModify.add(sectorFromMutual);
                }
                sectorsToModify.add(nearbySector);
            }
        }
    }

    private boolean isAreaTooSmall(Area mutualArea) {
        PathIterator it = mutualArea.getPathIterator(null);
        float[] coords = new float[8];
        float currentX;
        float currentY;
        float prevX = 0;
        float prevY = 0;
        while (!it.isDone()) {
            int currentSegment = it.currentSegment(coords);
            if (currentSegment == PathIterator.SEG_MOVETO) {
                prevX = coords[0];
                prevY = coords[1];
            } else if (currentSegment == PathIterator.SEG_LINETO) {
                currentX = coords[0];
                currentY = coords[1];
                if (currentX != prevX && currentY != prevY && Math.hypot(currentX - prevX, currentY - prevY) < 0.5f) {
                    return true;
                }
                prevX = currentX;
                prevY = currentY;
            } else {
                break;
            }
            it.next();
        }
        return false;
    }

    private void applySectorAttributesToAnotherSector(Sector src, Sector dst) {
        dst.setCeilingAltitude(src.getCeilingAltitude());
        dst.setFloorAltitude(src.getFloorAltitude());
        dst.setFloorTexture(new TextureDefinition(src.getFloorTexture()));
        dst.setCeilingTexture(new TextureDefinition(src.getCeilingTexture()));
    }

    private void applyAreaToSector(Area area, Sector sector) throws Line.NegligibleLineException {
        PathIterator pathIterator = area.getPathIterator(null);
        float[] coords = new float[8];
        Vertex prevVertex = null;
        ArrayList<Line> subSectorLines = new ArrayList<Line>();
        while (!pathIterator.isDone()) {
            prevVertex = takeStepInPathIterator(pathIterator, coords, prevVertex, subSectorLines);
            pathIterator.next();
        }
        levelElementsManager.addSector(sector.polishSector());
    }

    private Vertex takeStepInPathIterator(PathIterator pathIterator, float[] coords, Vertex prevVertex,
                                          ArrayList<Line> subSectorLines)
            throws Line.NegligibleLineException {
        int step = pathIterator.currentSegment(coords);
        Vertex answer = takeStepAccordingToSegment(coords, prevVertex, step, subSectorLines);
        return answer;
    }

    private Vertex takeStepAccordingToSegment(float[] coords, Vertex prevVertex, int step, ArrayList<Line> subSectorLines)
            throws Line.NegligibleLineException {
        Vertex answer = null;
        if (step == PathIterator.SEG_MOVETO) {
            subSectorLines.clear();
            long currentVertexId = IdsManager.getLastVertexId();
            answer = levelElementsManager.generateNewVertex(coords[0], Data.LEVEL_SIZE_PIXELS - coords[1]);
            if (currentVertexId < answer.getId()) {
                handleLinesSplitting(answer);
            }
            coords[6] = answer.getId();
        } else if (step == PathIterator.SEG_LINETO) {
            if (!prevVertex.equals(coords[0], Data.LEVEL_SIZE_PIXELS - coords[1], 0.1f)) {
                answer = addLineAndVertexInPathIteration(coords, prevVertex, subSectorLines);
            } else {
                answer = prevVertex;
            }
        } else if (step == PathIterator.SEG_CLOSE) answer = finishSubPath(coords, prevVertex, subSectorLines);
        return answer;
    }

    private Vertex finishSubPath(float[] coords, Vertex prevVertex, ArrayList<Line> subSectorLines)
            throws Line.NegligibleLineException {
        Vertex answer;
        if (coords[6] == prevVertex.getId()) {
            answer = null;
        } else {
            answer = forceFinishSubPath(coords, prevVertex, subSectorLines);
        }
        return answer;
    }

    private Vertex forceFinishSubPath(float[] coords, Vertex prevVertex, ArrayList<Line> subSectorLines
    ) throws Line.NegligibleLineException {
        //This is for unexplained behaviour where I draw a simple rectangle and it kept missing the
        // last iteration...
        Vertex vertex = levelElementsManager.getVertex((long) coords[6]);
        coords[0] = vertex.getX();
        coords[1] = vertex.getDisplayY();
        Vertex answer = addLineAndVertexInPathIteration(coords, prevVertex, subSectorLines);
        return answer;
    }

    private Vertex addLineAndVertexInPathIteration(float[] coords, Vertex prevVertex,
                                                   ArrayList<Line> subSectorLines) throws Line.NegligibleLineException {
        float dataY = Data.LEVEL_SIZE_PIXELS - coords[1];
        Vertex dst;
        if (!prevVertex.equals(coords[0], dataY)) {
            long currentVertexId = IdsManager.getLastVertexId();
            dst = levelElementsManager.generateNewVertex(coords[0], dataY);
            if (currentVertexId < dst.getId()) {
                handleLinesSplitting(dst);
            }
            addLineInPathIteration(prevVertex, subSectorLines, dst);
        } else dst = prevVertex;
        return dst;
    }

    private void handleLinesSplitting(Vertex vertex) {
        Rectangle bounds = new Rectangle((int) vertex.getX(), (int) vertex.getY(), 1, 1);
        List<Line> nearbyLines = levelElementsManager.getLinesInArea(bounds);
        for (Line line : nearbyLines) {
            if (Utils.isPointOnLine(line, vertex)) {
                splitLine(vertex, line);
            }
        }
    }

    private void splitLine(Vertex middle, Line line) {
        try {
            Line subLine1 = levelElementsManager.generateNewLine((Vertex) line.getSrc(), middle);
            Line subLine2 = levelElementsManager.generateNewLine(middle, (Vertex) line.getDst());
            levelElementsManager.removeLine(line);
            applyCertainLineAttributesToGivenLines(line, subLine1, subLine2);
            addSectorToModifiedList(line.getFrontSectorId());
            addSectorToModifiedList(line.getBackSectorId());
        } catch (Line.NegligibleLineException e) {
            e.printStackTrace();
        }
    }

    private void applyCertainLineAttributesToGivenLines(Line line, Line... otherLines) {
        for (Line otherLine : otherLines) {
            otherLine.setFrontSectorId(line.getFrontSectorId());
            otherLine.setBackSectorId(line.getBackSectorId());
        }
    }

    private void addSectorToModifiedList(long id) {
        if (id != Data.SKETCH_ID) {
            Sector sector = levelElementsManager.getSector(id);
            if (!sectorsToModify.contains(sector)) sectorsToModify.add(sector);
        }
    }

    private void addLineInPathIteration(Vertex prevVertex, ArrayList<Line> subSectorLines, Vertex dst)
            throws Line.NegligibleLineException {
        Vertex nextVertexInPath = getClosestVertexInPath(prevVertex, dst);
        Line newLine = levelElementsManager.generateNewLine(prevVertex, nextVertexInPath);
        subSectorLines.add(newLine);
        if (nextVertexInPath != dst) {
            addLineInPathIteration(nextVertexInPath, subSectorLines, dst);
        }
    }

    private Vertex getClosestVertexInPath(Vertex src, Vertex dst) {
        float srcX = src.getX();
        float srcY = src.getY();
        float dstX = dst.getX();
        float dstY = dst.getY();
        float minX = Math.min(srcX, dstX);
        float minY = Math.min(srcY, dstY);
        float width = Math.max(srcX, dstX) - minX;
        float height = Math.max(srcY, dstY) - minY;
        Rectangle area = new Rectangle((int) minX, (int) minY, (int) width, (int) height);
        List<Vertex> nearbyVertices = levelElementsManager.getVerticesInArea(area);
        Vertex result = dst;
        double smallestDistance = Line2D.ptSegDist(srcX, srcY, srcX, srcY, dstX, dstY);
        for (Vertex vertex : nearbyVertices) {
            float x = vertex.getX();
            float y = vertex.getY();
            boolean onPath = Line2D.ptSegDist(srcX, srcY, dstX, dstY, x, y) < Data.EPSILON;
            if (vertex.equals(src) || vertex.equals(dst) || !onPath) continue;
            double currentDistance = Line2D.ptSegDist(srcX, srcY, srcX, srcY, x, y);
            if (currentDistance < smallestDistance) {
                smallestDistance = currentDistance;
                result = vertex;
            }
        }
        return result;
    }

    private Area generateAreaFromSketch(int beginIndex, int endIndex, ArrayList<Vertex> path) {
        int size = endIndex - beginIndex;
        int[] xValues = new int[size], yValues = new int[size];
        for (int i = 0; i < size; i++) {
            Vertex vertex = path.get(i + beginIndex);
            xValues[i] = (int) vertex.getX();
            yValues[i] = (int) (vertex.getDisplayY());
        }
        return new Area(new Polygon(xValues, yValues, size));
    }

    public void reset() {
        sketchVertices.clear();
        lines.clear();
        path.clear();
    }

    public boolean isDrawing() {
        return sketchVertices.size() > 0;
    }

    public ArrayList<Vertex> getPath() {
        return path;
    }

    public List<Line> getSketchLines() {
        return lines;
    }
}
