package data.handlers;

import com.google.gson.JsonObject;
import data.utils.C.Data.JsonKeys;
import elements.LevelElement;
import elements.LevelElementDataStructure;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class IdsManager {
    private static long currentActorId;
    private static long currentVertexId;
    private static long currentLineId;
    private static long currentSectorId;

    public static long obtainVertexId() {
        return currentVertexId++;
    }

    public static long obtainActorId() {
        return currentActorId++;
    }

    public static long obtainLineId() {
        return currentLineId++;
    }

    public static long obtainSectorId() {
        return currentSectorId++;
    }

    public static long getLastVertexId() {
        return currentVertexId - 1;
    }

    public static long getLastSectorId() {
        return currentSectorId - 1;
    }

    public static long getLastLineId() {
        return currentLineId - 1;
    }

    public static long getLastActorId() {
        return currentActorId - 1;
    }

    static void inflateIds(JsonObject elements, LevelElementsManager levelElementsManager) {
        currentActorId = inflateId(elements, JsonKeys.LATEST_ACTOR_ID, levelElementsManager.getActors());
        currentVertexId = inflateId(elements, JsonKeys.LATEST_VERTEX_ID, levelElementsManager.getVertices());
        currentLineId = inflateId(elements, JsonKeys.LATEST_LINE_ID, levelElementsManager.getLines());
        currentSectorId = inflateId(elements, JsonKeys.LATEST_SECTOR_ID, levelElementsManager.getSectors());
    }

    private static long inflateId(JsonObject elements, String jsonKey, LevelElementDataStructure elementDataStructure) {
        long result;
        if (elements != null && elements.has(jsonKey)) result = elements.get(jsonKey).getAsLong() + 1;
        else {
            Stream<LevelElement> stream = elementDataStructure.values().stream();
            Optional<LevelElement> element = stream.max(Comparator.comparingLong(LevelElement::getId));
            result = element.map(LevelElement::getId).orElse(-1L) + 1;
        }
        return result;
    }

}
