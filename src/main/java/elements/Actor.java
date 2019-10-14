package elements;

import data.utils.C.Data;
import elements.actor.ActorElement;
import elements.actor.Type;
import utils.SharedC;
import utils.SharedUtils;

import java.io.Serializable;

public class Actor extends Element implements ActorElement, Serializable {
    private float direction;
    private long currentSectorId;
    private float currentFloorAltitude;
    private float currentCeilingAltitude;
    private Type type;
    private float z;

    public Actor(float x, float y, long id) {
        setX(x);
        setY(y);
        setWidth(SharedC.WORLD_UNIT);
        setHeight(SharedC.WORLD_UNIT);
        this.id = id;
    }

    @Override
    public boolean compareTo(ActorElement actorElement) {
        return compareTo(actorElement.getX(), actorElement.getY());
    }

    @Override
    public void setDirection(float v) {
        direction = v;
    }

    public Type getType() {
        return type;
    }

    @Override
    public float getDirection() {
        return direction;
    }

    public boolean compareTo(float x, float y) {
        return SharedUtils.compareApprox(getX(), x, Data.EPSILON) && SharedUtils.compareApprox(getY(), y, Data.EPSILON);
    }


    public float getDisplayY() {
        return Data.LEVEL_SIZE_PIXELS - getY();
    }

    public void setCurrentFloorAltitude(float v) {
        currentFloorAltitude = v;
    }

    public float getCurrentFloorAltitude() {
        return currentFloorAltitude;
    }

    public void setCurrentCeilingAltitude(float v) {
        currentCeilingAltitude = v;
    }

    public float getCurrentCeilingAltitude() {
        return currentCeilingAltitude;
    }

    public void setType(Type player) {
        type = player;
    }

    public void setCurrentSectorId(long currentSectorId) {
        this.currentSectorId = currentSectorId;
    }

    public float getCurrentSectorId() {
        return currentSectorId;
    }

}
