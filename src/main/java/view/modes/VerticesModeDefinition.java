package view.modes;

import data.handlers.LevelElementsManager;
import data.utils.C;
import elements.Vertex;
import view.LevelView;
import view.dialogs.VertexDialog;

import java.awt.event.MouseEvent;

public class VerticesModeDefinition implements ModeDefinition {
    private static LevelView.Comparison<Vertex> verticesCmp = (element, e) -> element.equals(e.getX(),
            C.Data.LEVEL_SIZE_PIXELS - e.getY(), C.Views.LevelViewSection.CURSOR_RADIUS);

    @Override
    public String getDisplayName() {
        return "Vertices Mode";
    }

    @Override
    public boolean onMouseMoved(LevelView levelView, MouseEvent e) {
        LevelElementsManager levelElementsManager = levelView.getLevelElementsManager();
        boolean answer = levelView.highlightElementBehindCursor(e, levelElementsManager.getVertices(), verticesCmp);
        levelView.repaint();
        return answer;
    }

    @Override
    public boolean onMouseRightPressed(LevelView levelView, MouseEvent e) {
        levelView.openNewDialog(new VertexDialog(levelView.getSelectedElements()));
        levelView.getSelectedElements().clear();
        return true;
    }

    @Override
    public boolean isHighlightingElements() {
        return true;
    }
}
