package elements;

import elements.texture.*;
import utils.*;

import java.io.Serializable;

public class Line extends Element implements LineElement, Serializable {
    private transient static final long NO_SECTOR = -1;

    private Vertex src;
    private Vertex dst;
    private boolean solid = true;
    private long frontSectorId = NO_SECTOR;
    private long backSectorId = NO_SECTOR;
    private WallTextureDefinition frontTexture = new WallTextureDefinition("wall3", "wall3", "wall3");
    private WallTextureDefinition backTexture = new WallTextureDefinition();

    public Line(Vertex src, Vertex dst, long id, boolean updateVertices) throws NegligibleLineException {
        if (src.equals(dst)) throw new NegligibleLineException(src, dst);
        this.src = src;
        this.dst = dst;
        this.id = id;
        if (updateVertices) {
            updateVertices(src, dst);
        }
        initializeDimensions(src, dst);
    }

    private void updateVertices(Vertex src, Vertex dst) {
        src.incrementNumberOfLines();
        dst.incrementNumberOfLines();
    }

    private void initializeDimensions(Vertex src, Vertex dst) {
        setX(Math.min(src.getX(), dst.getX()));
        setY(Math.min(src.getY(), dst.getY()));
        setWidth(Math.max(Math.max(src.getX(), dst.getX()) - getX(), 1));
        setHeight(Math.max(Math.max(src.getY(), dst.getY()) - getY(), 1));
    }

    @Override
    public boolean equals(LineElement lineElement) {
        VertexElement otherSrc = lineElement.getSrc();
        VertexElement otherDst = lineElement.getDst();
        return compareTo(otherSrc, otherDst);
    }

    @Override
    public VertexElement getSrc() {
        return src;
    }

    @Override
    public VertexElement getDst() {
        return dst;
    }

    @Override
    public boolean isSolid() {
        return solid;
    }

    @Override
    public void setSolid(boolean b) {
        solid = b;
    }

    @Override
    public long getFrontSectorId() {
        return frontSectorId;
    }

    @Override
    public long getBackSectorId() {
        return backSectorId;
    }

    @Override
    public void setFrontSectorId(long l) {
        frontSectorId = l;
    }

    @Override
    public void setBackSectorId(long l) {
        backSectorId = l;
    }

    @Override
    public WallTextureDefinition getFrontTexture() {
        return frontTexture;
    }

    @Override
    public void setFrontTexture(WallTextureDefinition s) {
        frontTexture = s;
    }

    @Override
    public WallTextureDefinition getBackTexture() {
        return backTexture;
    }

    @Override
    public void setBackTexture(WallTextureDefinition s) {
        backTexture = s;
    }


    public boolean compareTo(VertexElement v1, VertexElement v2) {
        return compareTo(v1, v2.getX(), v2.getY());
    }

    public boolean compareTo(VertexElement v1, float v2x, float v2y) {
        return (src.equals(v1) && dst.equals(v2x, v2y)) || (src.equals(v2x, v2y) && dst.equals(v1));
    }

    @Override
    public float getNormalDirection() {
        double rad = Math.atan2(dst.getY() - src.getY(), dst.getX() - src.getX()) + Math.PI / 2;
        float normalDirection = (float) Math.toDegrees(rad);
        return SharedUtils.clampAngle(normalDirection);
    }

    public void flipVertices() {
        Vertex temp = src;
        src = dst;
        dst = temp;
    }

    public class NegligibleLineException extends Exception {
        private static final String NEGLIGIBLE_LINE_EXCEPTION_MESSAGE = "The line's vertices cannot have the same " +
                "position! Source Vertex: %s, Destination Vertex : %s.";
        private final Vertex src;
        private final Vertex dst;

        public NegligibleLineException(Vertex src, Vertex dst) {
            this.src = src;
            this.dst = dst;
        }

        @Override
        public String getMessage() {
            return String.format(NEGLIGIBLE_LINE_EXCEPTION_MESSAGE, src.toString(), dst.toString());
        }
    }
}
