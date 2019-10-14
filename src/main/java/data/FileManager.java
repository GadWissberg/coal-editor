package data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import data.handlers.IdsManager;
import data.handlers.LevelElementsManager;
import data.utils.C;
import elements.Line;
import view.EditorPreferences;
import view.MapLoadSubscriber;
import view.ToolBarView;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {
    private final LevelElementsManager levelElementsManager;
    private ArrayList<MapLoadSubscriber> mapLoadSubscribers = new ArrayList<MapLoadSubscriber>();

    public FileManager(LevelElementsManager levelElementsManager) {
        this.levelElementsManager = levelElementsManager;
    }

    public void saveMap() {
        try (Writer writer = new FileWriter("saved_maps/test.json")) {
            Gson gson = new Gson();
            HashMap<String, Object> output = new HashMap<>();
            levelElementsManager.initializeDataForSaving();
            output.put(C.Data.JsonKeys.ELEMENTS, levelElementsManager);
            saveLevelProperties(output);
            gson.toJson(output, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveLevelProperties(HashMap<String, Object> output) {
        JsonObject properties = new JsonObject();
        JsonObject elementsProperties = new JsonObject();
        elementsProperties.addProperty(C.Data.JsonKeys.LATEST_ACTOR_ID, IdsManager.getLastLineId());
        elementsProperties.addProperty(C.Data.JsonKeys.LATEST_VERTEX_ID, IdsManager.getLastVertexId());
        elementsProperties.addProperty(C.Data.JsonKeys.LATEST_LINE_ID, IdsManager.getLastLineId());
        elementsProperties.addProperty(C.Data.JsonKeys.LATEST_SECTOR_ID, IdsManager.getLastSectorId());
        properties.add(C.Data.JsonKeys.ELEMENTS, elementsProperties);
        output.put(C.Data.JsonKeys.PROPERTIES, properties);
    }

    public void loadMap(ToolBarView toolBarView) throws IOException, Line.NegligibleLineException {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON File", "json");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(toolBarView);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String path = selectedFile.getPath();
            EditorPreferences.setDefaultLoadFileDirectory(path);
            try (Reader reader = new FileReader(path)) {
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
                levelElementsManager.inflate(jsonObject);
                mapLoadSubscribers.forEach(sub -> sub.onMapLoad(selectedFile.getName()));
            } catch (IOException e) {
                throw e;
            }
        }
    }

    public void subscribeForMapLoad(MapLoadSubscriber sub) {
        if (mapLoadSubscribers.contains(sub)) return;
        mapLoadSubscribers.add(sub);
    }
}
