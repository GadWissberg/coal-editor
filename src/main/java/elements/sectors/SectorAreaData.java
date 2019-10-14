package elements.sectors;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

public class SectorAreaData {
    private transient Area area;
    private transient Rectangle2D bounds;

    public SectorAreaData(Area area) {
        this.area = area;
        this.bounds = area.getBounds2D();
    }

    public Area getArea() {
        return area;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void updateBounds() {
        bounds = area.getBounds2D();
    }
}
