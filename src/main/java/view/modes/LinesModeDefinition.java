package view.modes;

import data.handlers.LevelElementsManager;
import data.utils.C;
import elements.Line;
import view.LevelView;
import view.dialogs.LineDialog;

import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

public class LinesModeDefinition implements ModeDefinition {
    private static LevelView.Comparison<Line> linesCmp = (element, e) -> Line2D.ptSegDist(element.getSrc().getX(),
            element.getSrc().getY(), element.getDst().getX(), element.getDst().getY(),
            e.getX(), C.Data.LEVEL_SIZE_PIXELS - e.getY()) < C.Views.LevelViewSection.CURSOR_RADIUS;

    @Override
    public String getDisplayName() {
        return "Lines Mode";
    }

    @Override
    public boolean onMouseMoved(LevelView levelView, MouseEvent e) {
        LevelElementsManager levelElementsManager = levelView.getLevelElementsManager();
        boolean answer = levelView.highlightElementBehindCursor(e, levelElementsManager.getLines(),
                linesCmp);
        levelView.repaint();
        return answer;
    }

    @Override
    public boolean onMouseRightPressed(LevelView levelView, MouseEvent e) {
        levelView.openNewDialog(new LineDialog(levelView.getSelectedElements()));
        levelView.getSelectedElements().clear();
        return true;
    }

    @Override
    public boolean isHighlightingElements() {
        return true;
    }
}
