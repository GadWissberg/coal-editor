package elements.sectors;

import data.handlers.IdsManager;
import data.utils.C;
import elements.Element;
import elements.SubSector;
import elements.texture.*;

import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;

public class Sector extends Element implements SectorElement, Serializable {
    private transient SectorAreaData sectorAreaData;
    private float floorAltitude;
    private float ceilAltitude = 5;
    private ArrayList<SubSector> subSectors;
    private TextureDefinition floorTexture = new TextureDefinition("wall3");
    private TextureDefinition ceilingTexture = new TextureDefinition("wall3");

    public Sector(Area area, long id) {
        super();
        this.sectorAreaData = new SectorAreaData(area);
        this.id = id;
    }

    @Override
    public void setCeilingAltitude(float i) {
        ceilAltitude = i;
    }

    @Override
    public float getCeilingAltitude() {
        return ceilAltitude;
    }

    @Override
    public void setFloorAltitude(float i) {
        floorAltitude = i;
    }

    @Override
    public float getFloorAltitude() {
        return floorAltitude;
    }

    @Override
    public TextureDefinition getFloorTexture() {
        return floorTexture;
    }

    @Override
    public void setFloorTexture(TextureDefinition s) {
        floorTexture = s;
    }

    @Override
    public TextureDefinition getCeilingTexture() {
        return ceilingTexture;
    }

    @Override
    public void setCeilingTexture(TextureDefinition s) {
        ceilingTexture = s;
    }

    public Area getArea() {
        return sectorAreaData.getArea();
    }

    public Sector updateBounds() {
        sectorAreaData.updateBounds();
        Rectangle2D bounds = sectorAreaData.getBounds();
        setWidth((float) bounds.getWidth());
        setHeight((float) bounds.getHeight());
        setX((float) bounds.getMinX());
        setY((float) (C.Data.LEVEL_SIZE_PIXELS - bounds.getMaxY()));
        return this;
    }


    public Sector polishSector() {
        updateBounds();
        if (id == C.Data.SKETCH_ID) setId(IdsManager.obtainSectorId());
        return this;
    }

    public Rectangle2D getBounds() {
        return sectorAreaData.getBounds();
    }

    public void generateSubSectors() {
        PathIterator it = sectorAreaData.getArea().getPathIterator(null);
        float[] coords = new float[8];
        subSectors = new ArrayList<>();
        while (!it.isDone()) {
            takeStepInSubSectorBuilding(it, coords);
        }
    }

    private void takeStepInSubSectorBuilding(PathIterator it, float[] coords) {
        int step = it.currentSegment(coords);
        float x = coords[0];
        float dataY = C.Data.LEVEL_SIZE_PIXELS - coords[1];
        if (step == PathIterator.SEG_MOVETO) startSubSector(coords, x, dataY);
        else if (step == PathIterator.SEG_LINETO) subSectors.get(subSectors.size() - 1).addPoint(x, dataY);
        else if (step == PathIterator.SEG_CLOSE) closeSubSector(coords, x, dataY);
        it.next();
    }

    private void closeSubSector(float[] coords, float x, float dataY) {
        float firstX = coords[6];
        float firstDataY = coords[7];
        SubSector subSector = subSectors.get(subSectors.size() - 1);
        if (x != firstX || dataY != firstDataY) {
            subSector.addPoint(x, dataY);
        }
        subSector.generateArea();
        checkIfSubSectorIsInternalOrExternal(subSector);
    }

    private void checkIfSubSectorIsInternalOrExternal(SubSector subSector) {
        for (SubSector other : subSectors) {
            if (subSector == other) continue;
            if (checkIfSubSectorIsInsideOther(subSector, other)) {
                subSector.setContainerId(subSectors.indexOf(other));
            } else if (checkIfSubSectorIsInsideOther(other, subSector)) {
                other.setContainerId(subSectors.indexOf(subSector));
            }
        }
    }

    private boolean checkIfSubSectorIsInsideOther(SubSector contained, SubSector container) {
        Area test = new Area(contained.getArea());
        test.subtract(container.getArea());
        return test.isEmpty();
    }

    private void startSubSector(float[] coords, float x, float dataY) {
        SubSector currentSubSector;
        currentSubSector = new SubSector();
        currentSubSector.addPoint(x, dataY);
        coords[6] = x;
        coords[7] = dataY;
        subSectors.add(currentSubSector);
    }

}
