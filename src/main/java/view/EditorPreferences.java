package view;

import com.google.gson.Gson;
import data.utils.C;
import data.utils.C.EditorPreferencesKeys;

import java.io.*;
import java.util.HashMap;

public class EditorPreferences {
    private static HashMap<String, Object> preferences;

    public static void setDefaultLoadFileDirectory(String path) {
        preferences.put(EditorPreferencesKeys.DEFAULT_LOAD_FILE_DIR, path);
    }

    public static String getDefaultLoadFileDirectory() {
        String defaultValue = System.getProperty("user.dir");
        return (String) preferences.getOrDefault(EditorPreferencesKeys.DEFAULT_LOAD_FILE_DIR, defaultValue);
    }

    public static void flush() throws FileNotFoundException {
        Gson gson = new Gson();
        String json = gson.toJson(preferences, HashMap.class);
        try (PrintWriter out = new PrintWriter(C.EditorPreferencesKeys.PREFS_FILE_NAME)) {
            out.print(json);
        }
    }

    public static void initialize() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(EditorPreferencesKeys.PREFS_FILE_NAME)) {
            preferences = gson.fromJson(reader, HashMap.class);
        } catch (IOException e) {
            preferences = new HashMap<>();
        }
    }
}
