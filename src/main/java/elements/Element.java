package elements;

import java.io.Serializable;

public abstract class Element implements LevelElement, Serializable {

    protected long id;
    private float width;
    private float height;
    private float x;
    private float y;

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setId(long l) {
        this.id = l;
    }

    public long getId() {
        return id;
    }


}
