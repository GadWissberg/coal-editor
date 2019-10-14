package data.handlers;

import view.modes.Mode;

import java.util.ArrayList;

public class ModeHandler {
    private static Mode selectedMode = Mode.VERTICES;
    private static Mode prevMode = Mode.VERTICES;
    private ArrayList<ModeChangedListener> ModeChangedListeners = new ArrayList<>();

    public static Mode getCurrentMode() {
        return selectedMode;
    }

    public static Mode getPrevMode() {
        return prevMode;
    }

    public void setCurrentMode(Mode mode) {
        prevMode = selectedMode;
        selectedMode = mode;
        for (ModeChangedListener listener : ModeChangedListeners) {
            listener.modeChanged(mode);
        }
    }

    public void registerForModeChange(ModeChangedListener listener) {
        ModeChangedListeners.add(listener);
    }
}
