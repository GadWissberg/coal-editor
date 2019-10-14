package elements;

import data.utils.C;
import utils.SharedUtils;

import java.io.Serializable;

public class Vertex extends Element implements VertexElement, Serializable {
    private int numberOfLines;

    public Vertex(float x, float y, long id) {
        setX(x);
        setY(y);
        setId(id);
    }

    @Override
    public String toString() {
        return "X:" + getX() + ", Y:" + getY() + " ID:" + id;
    }

    public void incrementNumberOfLines() {
        numberOfLines++;
    }

    public void decrementNumberOfLines() {
        numberOfLines--;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }


    public Vertex setPosition(float x, float y) {
        setX(x);
        setY(y);
        return this;
    }


    @Override
    public boolean equals(VertexElement vertexElement) {
        return equals(vertexElement, C.Data.EPSILON);
    }

    public boolean equals(VertexElement vertexElement, float epsilon) {
        return equals(vertexElement.getX(), vertexElement.getY(), epsilon);
    }

    public boolean equals(float x, float y) {
        return equals(x, y, C.Data.EPSILON);
    }

    public boolean equals(float x, float y, float epsilon) {
        return SharedUtils.compareApprox(getX(), x, epsilon) && SharedUtils.compareApprox(getY(), y, epsilon);
    }

    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 1;
    }


    public float getDisplayY() {
        return C.Data.LEVEL_SIZE_PIXELS - getY();
    }

    public void setNumberOfLines(int number) {
        numberOfLines = number;
    }
}
