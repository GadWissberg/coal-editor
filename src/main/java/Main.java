import view.EditorPreferences;
import view.MainEditorWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        MainEditorWindow display = new MainEditorWindow();
        try {
            EditorPreferences.initialize();
            display.initialize();
            display.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
