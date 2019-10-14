package view.modes;

import view.LevelView;

import java.awt.event.MouseEvent;

public enum Mode {

    VERTICES(new VerticesModeDefinition()),
    LINES(new LinesModeDefinition()),
    SECTORS(new SectorsModeDefinition()),
    ACTORS(new ActorsModeDefinition()),
    DRAWING(new DrawingModeDefinition());

    private final ModeDefinition definition;

    Mode(ModeDefinition definition) {
        this.definition = definition;
    }

    public String getDisplayName() {
        return definition.getDisplayName();
    }

    public boolean onMouseMoved(LevelView levelView, MouseEvent e) {
        return definition.onMouseMoved(levelView, e);
    }

    public boolean onMouseRightPressed(LevelView levelView, MouseEvent e) {
        return definition.onMouseRightPressed(levelView, e);
    }

    public boolean isHighlightingElements() {
        return definition.isHighlightingElements();
    }

}
