package data.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Envelope;
import data.utils.C;
import data.utils.C.Data.JsonKeys;
import elements.Actor;
import elements.Line;
import elements.SubSector;
import elements.Vertex;
import elements.actor.*;
import elements.sectors.Sector;
import elements.texture.*;
import utils.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class LevelElementsManager implements Serializable {
    private LevelElementDataStructure actors = new LevelElementDataStructure();
    private LevelElementDataStructure vertices = new LevelElementDataStructure();
    private LevelElementDataStructure lines = new LevelElementDataStructure();
    private LevelElementDataStructure sectors = new LevelElementDataStructure();
    private transient Envelope auxEnvelope = new Envelope();

    public Vertex generateNewVertex(float x, float y) {
        Vertex answer = getVertex(x, y);
        if (answer == null) {
            answer = new Vertex(x, y, IdsManager.obtainVertexId());
        }
        vertices.put(answer.getId(), answer);
        return answer;
    }

    public Actor generateNewActor(float x, float y) {
        Actor actor = getActor(x, y);
        if (actor == null) {
            actor = new Actor(x, y, IdsManager.obtainActorId());
            actor.setType(Type.PLAYER);
        }
        actors.put(actor.getId(), actor);
        return actor;
    }

    public Vertex getVertex(float x, float y) {
        auxEnvelope.init(x, x + 1, y, y + 1);
        List<Vertex> result = vertices.getQuadTree().query(auxEnvelope);
        for (Vertex vertex : result)
            if (vertex.equals(x, y)) {
                return vertex;
            }
        return null;
    }

    public Actor getActor(float x, float y) {
        auxEnvelope.init(x, x + 1, y, y + 1);
        List<Actor> result = actors.getQuadTree().query(auxEnvelope);
        for (Actor actor : result)
            if (actor.compareTo(x, y)) {
                return actor;
            }
        return null;
    }

    public Line generateNewLine(Vertex src, Vertex dst) throws Line.NegligibleLineException {
        Line answer = checkIfThereIsLineInPosition(src, dst);
        if (answer == null) {
            answer = new Line(src, dst, IdsManager.obtainLineId(), true);
        }
        lines.put(answer.getId(), answer);
        return answer;
    }

    public Line checkIfThereIsLineInPosition(Vertex prevVertex, Vertex nextVertex) {
        auxEnvelope.init(Math.min(prevVertex.getX(), nextVertex.getX()), Math.max(1, Math.max(prevVertex.getX(), nextVertex.getX())),
                Math.min(prevVertex.getY(), nextVertex.getY()), Math.max(1, Math.max(prevVertex.getY(), nextVertex.getY())));
        List<Line> result = lines.getQuadTree().query(auxEnvelope);
        for (Line line : result)
            if (line.compareTo(prevVertex, nextVertex)) {
                return line;
            }
        return null;
    }

    public void addSector(Sector sector) {
        sectors.put(sector.getId(), sector);
    }

    public LevelElementDataStructure getVertices() {
        return vertices;
    }

    public LevelElementDataStructure getLines() {
        return lines;
    }

    public Vertex getVertex(long id) {
        return (Vertex) vertices.get(id);
    }

    public List<Sector> getSectorsInArea(Rectangle2D bounds2D, boolean flipY) {
        double minY = bounds2D.getMinY();
        double maxY = bounds2D.getMaxY();
        if (flipY) {
            minY = C.Data.LEVEL_SIZE_PIXELS - bounds2D.getMaxY();
            maxY = C.Data.LEVEL_SIZE_PIXELS - bounds2D.getMinY();
        }
        auxEnvelope.init(bounds2D.getMinX(), bounds2D.getMaxX(), minY, maxY);
        return sectors.getQuadTree().query(auxEnvelope);
    }

    public Line getLine(long id) {
        return (Line) lines.get(id);
    }

    public void removeLine(Line line) {
        if (line == null) return;
        lines.remove(line);
        Vertex src = (Vertex) line.getSrc();
        src.decrementNumberOfLines();
        if (src.getNumberOfLines() == 0) removeVertex(src);
        Vertex dst = (Vertex) line.getDst();
        dst.decrementNumberOfLines();
        if (dst.getNumberOfLines() == 0) removeVertex(dst);
    }

    private void removeVertex(Vertex vertex) {
        vertices.remove(vertex);
    }

    public List<Line> getLinesInArea(Rectangle bounds) {
        return getLinesInArea(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    public List<Vertex> getVerticesInArea(Rectangle bounds) {
        return getVerticesInArea(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    public List<Vertex> getVerticesInArea(double x, double y, double width, double height) {
        auxEnvelope.init(x, x + width, y, y + height);
        return vertices.getQuadTree().query(auxEnvelope);
    }

    public List<Actor> getActorsInArea(double x, double y, double width, double height) {
        auxEnvelope.init(x, x + width, y, y + height);
        return actors.getQuadTree().query(auxEnvelope);
    }

    public List<Line> getLinesInArea(double x, double y, double width, double height) {
        auxEnvelope.init(x, x + width, y, y + height);
        return lines.getQuadTree().query(auxEnvelope);
    }

    public LevelElementDataStructure getSectors() {
        return sectors;
    }

    public Sector getSector(long sectorId) {
        return (Sector) sectors.get(sectorId);
    }

    public boolean hasSector(long id) {
        return sectors.contains(id);
    }

    public void removeSector(Sector sector) {
        sectors.remove(sector);
    }

    public LevelElementDataStructure getActors() {
        return actors;
    }

    public void initializeDataForSaving() {
        Collection<LevelElement> sectorsList = sectors.values();
        for (LevelElement element : sectorsList) {
            Sector sector = (Sector) element;
            sector.generateSubSectors();
        }
    }

    public List<LevelElement> getActorsByType(Type t) {
        return actors.values().stream().filter(element -> ((Actor) element).getType() == t).collect(Collectors.toList());
    }

    public boolean hasActorOfType(Type type) {
        return actors.values().stream().anyMatch(levelElement -> ((Actor) levelElement).getType() == type);
    }

    public void removeActor(Actor a) {
        actors.remove(a);
    }

    public void inflate(JsonObject levelJsonObject) throws Line.NegligibleLineException {
        JsonObject elements = levelJsonObject.getAsJsonObject(JsonKeys.ELEMENTS);
        actors = inflateElements(elements, JsonKeys.ACTORS, getActorInflationProcess());
        vertices = inflateElements(elements, JsonKeys.VERTICES, getVerticesInflationProcess());
        lines = inflateElements(elements, JsonKeys.LINES, getLinesInflationProcess());
        sectors = inflateElements(elements, JsonKeys.SECTORS, getSectorsInflationProcess());
        inflateLevelProperties(levelJsonObject);
    }

    private void inflateLevelProperties(JsonObject levelJsonObject) {
        JsonObject propertiesJsonObject = levelJsonObject.getAsJsonObject(JsonKeys.PROPERTIES);
        if (propertiesJsonObject != null && propertiesJsonObject.has(JsonKeys.ELEMENTS)) {
            IdsManager.inflateIds(levelJsonObject.getAsJsonObject(JsonKeys.ELEMENTS), this);
        } else {
            IdsManager.inflateIds(null, this);
        }
    }

    private ElementToInflateProcess getActorInflationProcess() {
        return new ElementToInflateProcess() {
            @Override
            public void inflate(JsonObject elementJsonObject, LevelElementDataStructure dataStructure) {
                float x = elementJsonObject.get(JsonKeys.X).getAsFloat();
                float y = elementJsonObject.get(JsonKeys.Y).getAsFloat();
                long id = elementJsonObject.get(JsonKeys.ID).getAsLong();
                Actor actor = new Actor(x, y, id);
                defineActor(elementJsonObject, actor);
                dataStructure.put(id, actor);
            }

            private void defineActor(JsonObject elementJsonObject, Actor actor) {
                actor.setDirection(elementJsonObject.get(JsonKeys.DIRECTION).getAsFloat());
                actor.setCurrentSectorId(elementJsonObject.get(JsonKeys.CURRENT_SECTOR_ID).getAsLong());
                actor.setCurrentFloorAltitude(elementJsonObject.get(JsonKeys.CURRENT_FLOOR_ALTITUDE).getAsFloat());
                actor.setCurrentCeilingAltitude(elementJsonObject.get(JsonKeys.CURRENT_CEILING_ALTITUDE).getAsFloat());
                actor.setType(Type.valueOf(elementJsonObject.get(JsonKeys.TYPE).getAsString()));
            }
        };
    }

    private ElementToInflateProcess getVerticesInflationProcess() {
        return (elementJsonObject, dataStructure) -> {
            float x = elementJsonObject.get(JsonKeys.X).getAsFloat();
            float y = elementJsonObject.get(JsonKeys.Y).getAsFloat();
            long id = elementJsonObject.get(JsonKeys.ID).getAsLong();
            Vertex vertex = new Vertex(x, y, id);
            vertex.setNumberOfLines(elementJsonObject.get(JsonKeys.NUMBER_OF_LINES).getAsInt());
            dataStructure.put(id, vertex);
        };
    }

    private ElementToInflateProcess getSectorsInflationProcess() {
        return new ElementToInflateProcess() {
            @Override
            public void inflate(JsonObject elementJsonObject, LevelElementDataStructure dataStructure) {
                long id = elementJsonObject.get(JsonKeys.ID).getAsLong();
                Sector sector = new Sector(constructAreaFromSubSectors(elementJsonObject), id);
                defineSector(elementJsonObject, sector);
                dataStructure.put(id, sector);
            }

            private void defineSector(JsonObject elementJsonObject, Sector sector) {
                sector.generateSubSectors();
                sector.setFloorAltitude(elementJsonObject.get(JsonKeys.FLOOR_ALTITUDE).getAsFloat());
                sector.setCeilingAltitude(elementJsonObject.get(JsonKeys.CEIL_ALTITUDE).getAsFloat());
                JsonObject floorTextureDefinition = elementJsonObject.get(JsonKeys.FLOOR_TEXTURE).getAsJsonObject();
                JsonObject ceilingTextureDefinition = elementJsonObject.get(JsonKeys.CEILING_TEXTURE).getAsJsonObject();
                sector.setFloorTexture(inflateTextureDefinition(floorTextureDefinition));
                sector.setCeilingTexture(inflateTextureDefinition(ceilingTextureDefinition));
                sector.updateBounds();
            }

            private Area constructAreaFromSubSectors(JsonObject elementJsonObject) {
                JsonArray subSectorsJsonArray = elementJsonObject.get(JsonKeys.SUB_SECTORS).getAsJsonArray();
                Iterator<JsonElement> it = subSectorsJsonArray.iterator();
                ArrayList<SubSector> subSectors = new ArrayList<>();
                while (it.hasNext()) inflateSubSector(it, subSectors);
                subSectors.forEach(subSector -> {
                    int containerId = subSector.getContainerId();
                    if (containerId >= 0) {
                        subSectors.get(containerId).getArea().subtract(subSector.getArea());
                    }
                });
                Area sectorArea = new Area();
                subSectors.forEach(subSector -> {
                    int containerId = subSector.getContainerId();
                    if (containerId < 0) {
                        sectorArea.add(subSector.getArea());
                    }
                });
                return sectorArea;
            }

            private void inflateSubSector(Iterator<JsonElement> it, ArrayList<SubSector> subSectors) {
                JsonObject currentSubSectorJsonObject = it.next().getAsJsonObject();
                ArrayList<Float> points = inflateListOfPoints(currentSubSectorJsonObject);
                SubSector subSector = new SubSector();
                subSector.setPoints(points);
                subSector.generateArea();
                subSector.setContainerId(currentSubSectorJsonObject.get(JsonKeys.CONTAINER_ID).getAsInt());
                subSectors.add(subSector);
            }

            private ArrayList<Float> inflateListOfPoints(JsonObject currentSubSectorJsonObject) {
                JsonArray pointsJsonArray = currentSubSectorJsonObject.get(JsonKeys.POINTS).getAsJsonArray();
                ArrayList<Float> points = new ArrayList<>();
                for (int i = 0; i < pointsJsonArray.size(); i += 2) {
                    points.add(pointsJsonArray.get(i).getAsFloat());
                    points.add(C.Data.LEVEL_SIZE_PIXELS - pointsJsonArray.get(i + 1).getAsFloat());
                }
                return points;
            }
        };
    }

    private ElementToInflateProcess getLinesInflationProcess() {
        return new ElementToInflateProcess() {
            @Override
            public void inflate(JsonObject elementJsonObject, LevelElementDataStructure dataStructure)
                    throws Line.NegligibleLineException {
                long id = elementJsonObject.get(JsonKeys.ID).getAsLong();
                JsonObject srcJsonObject = elementJsonObject.get(JsonKeys.SRC).getAsJsonObject();
                JsonObject dstJsonObject = elementJsonObject.get(JsonKeys.DST).getAsJsonObject();
                Vertex src = (Vertex) vertices.get(srcJsonObject.get(JsonKeys.ID).getAsLong());
                Vertex dst = (Vertex) vertices.get(dstJsonObject.get(JsonKeys.ID).getAsLong());
                Line line = new Line(src, dst, id, false);
                defineLine(elementJsonObject, line);
                dataStructure.put(id, line);
            }

            private void defineLine(JsonObject elementJsonObject, Line line) {
                line.setSolid(elementJsonObject.get(JsonKeys.SOLID).getAsBoolean());
                line.setFrontSectorId(elementJsonObject.get(JsonKeys.FRONT_SECTOR_ID).getAsLong());
                line.setBackSectorId(elementJsonObject.get(JsonKeys.BACK_SECTOR_ID).getAsLong());
                defineLineTextures(elementJsonObject, line);
            }

            private void defineLineTextures(JsonObject elementJsonObject, Line line) {
                JsonObject frontTextureJsonObject = elementJsonObject.get(JsonKeys.FRONT_TEXTURE).getAsJsonObject();
                JsonObject backTextureJsonObject = elementJsonObject.get(JsonKeys.BACK_TEXTURE).getAsJsonObject();
                WallTextureDefinition frontTextureDefinition = inflateWallTextureDefinition(frontTextureJsonObject);
                WallTextureDefinition backTextureDefinition = inflateWallTextureDefinition(backTextureJsonObject);
                line.setFrontTexture(frontTextureDefinition);
                line.setBackTexture(backTextureDefinition);
            }
        };
    }

    private WallTextureDefinition inflateWallTextureDefinition(JsonObject jsonObject) {
        WallTextureDefinition wallTextureDefinition = new WallTextureDefinition();
        wallTextureDefinition.setTop(inflateTextureDefinition(jsonObject.get(JsonKeys.TOP).getAsJsonObject()));
        wallTextureDefinition.setMiddle(inflateTextureDefinition(jsonObject.get(JsonKeys.MIDDLE).getAsJsonObject()));
        wallTextureDefinition.setBottom(inflateTextureDefinition(jsonObject.get(JsonKeys.BOTTOM).getAsJsonObject()));
        return wallTextureDefinition;
    }

    private TextureDefinition inflateTextureDefinition(JsonObject jsonObject) {
        TextureDefinition textureDefinition = new TextureDefinition();
        textureDefinition.setHorizontalOffset(SharedUtils.getFloatFromJson(jsonObject, JsonKeys.HORIZONTAL_OFFSET, 0));
        textureDefinition.setVerticalOffset(SharedUtils.getFloatFromJson(jsonObject, JsonKeys.VERTICAL_OFFSET, 0));
        textureDefinition.setOpacity(SharedUtils.getFloatFromJson(jsonObject, JsonKeys.OPACITY, 1));
        textureDefinition.setName(SharedUtils.getStringFromJson(jsonObject, JsonKeys.NAME, null));
        return textureDefinition;
    }

    private LevelElementDataStructure inflateElements(JsonObject elements, String key, ElementToInflateProcess process)
            throws Line.NegligibleLineException {
        LevelElementDataStructure dataStructure = new LevelElementDataStructure();
        JsonObject elementsJsonObject = elements.getAsJsonObject(key).getAsJsonObject("hashMap");
        Set<Map.Entry<String, JsonElement>> elementsJsonObjects = elementsJsonObject.entrySet();
        for (Map.Entry<String, JsonElement> elementEntry : elementsJsonObjects) {
            JsonObject elementJsonObject = elementEntry.getValue().getAsJsonObject();
            process.inflate(elementJsonObject, dataStructure);
        }
        return dataStructure;
    }

    private interface ElementToInflateProcess {
        void inflate(JsonObject elementJsonObject, LevelElementDataStructure dataStructure)
                throws Line.NegligibleLineException;
    }
}
