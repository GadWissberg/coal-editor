package elements;

import java.awt.*;
import java.awt.geom.Area;
import java.io.Serializable;
import java.util.ArrayList;

public class SubSector implements Serializable {
    private transient Area area;
    private ArrayList<Float> points = new ArrayList();
    private int containerId = -1;

    public void addPoint(float x, float y) {
        points.add(x);
        points.add(y);
    }

    public ArrayList getPoints() {
        return points;
    }

    public void generateArea() {
        int numberOfPoints = points.size() / 2;
        int[] xCoords = new int[numberOfPoints];
        int[] yCoords = new int[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            xCoords[i] = Math.round(points.get(i * 2));
            yCoords[i] = Math.round(points.get(i * 2 + 1));
        }
        area = new Area(new Polygon(xCoords, yCoords, numberOfPoints));
    }

    public Area getArea() {
        return area;
    }

    public void setContainerId(int id) {
        containerId = id;
    }

    public void setPoints(ArrayList<Float> points) {
        this.points = points;
    }

    public int getContainerId() {
        return containerId;
    }
}
