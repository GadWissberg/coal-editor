package view.modes;

import view.LevelView;

import java.awt.*;
import java.awt.event.MouseEvent;

public class DrawingModeDefinition implements ModeDefinition {
    @Override
    public String getDisplayName() {
        return "Drawing Mode";
    }

    @Override
    public boolean onMouseMoved(LevelView levelView, MouseEvent e) {
        levelView.refreshDrawingMousePosition();
        Point drawingMousePosition = levelView.getDrawingMousePosition();
        double x = drawingMousePosition.getX();
        double displayY = drawingMousePosition.getY();
        levelView.magnetMouse((float) x, (float) displayY);
        levelView.repaint();
        return true;
    }

    @Override
    public boolean onMouseRightPressed(LevelView levelView, MouseEvent e) {
        return true;
    }

    @Override
    public boolean isHighlightingElements() {
        return false;
    }
}
