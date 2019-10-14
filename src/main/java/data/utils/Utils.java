package data.utils;

import elements.Line;
import elements.Vertex;
import utils.SharedUtils;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Utils {

    public static boolean isPointOnLine(Line line, Vertex vertex) {
        return Line2D.ptSegDist(line.getSrc().getX(), line.getSrc().getY(),
                line.getDst().getX(), line.getDst().getY(), vertex.getX(), vertex.getY()) < C.Data.EPSILON;
    }

    public static void removeRangeFromArray(ArrayList<Vertex> arrayList, int start, int end) {
        for (int k = end - start; k >= 0; k--) {
            arrayList.remove(start + k);
        }
    }

    public static void getClosestPointOnSegment(float sx1, float sy1, float sx2, float sy2,
                                                float px, float py, Point result, int forceDistance) {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;
        if ((xDelta == 0) && (yDelta == 0)) {
            throw new IllegalArgumentException("Segment start equals segment end");
        }

        double u = ((SharedUtils.roundToClosestSide(px - sx1, forceDistance)) * xDelta
                + (SharedUtils.roundToClosestSide(py - sy1, forceDistance)) * yDelta)
                / (xDelta * xDelta + yDelta * yDelta);

        if (u < 0) {
            result.setLocation(sx1, sy1);
        } else if (u > 1) {
            result.setLocation(sx2, sy2);
        } else {
            result.setLocation((int) Math.round(sx1 + u * xDelta), (int) Math.round(sy1 + u * yDelta));
        }

    }
}
