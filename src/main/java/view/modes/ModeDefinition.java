package view.modes;

import view.LevelView;

import java.awt.event.MouseEvent;

public interface ModeDefinition {
    String getDisplayName();

    boolean onMouseMoved(LevelView levelView, MouseEvent e);

    boolean onMouseRightPressed(LevelView levelView, MouseEvent e);

    boolean isHighlightingElements();
}
