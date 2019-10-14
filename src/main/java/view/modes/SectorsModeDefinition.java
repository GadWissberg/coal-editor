package view.modes;

import data.handlers.LevelElementsManager;
import elements.sectors.Sector;
import view.LevelView;
import view.dialogs.SectorDialog;

import java.awt.event.MouseEvent;
import java.awt.geom.Area;

public class SectorsModeDefinition implements ModeDefinition {
    private static LevelView.Comparison<Sector> sectorsCmp = (element, e) -> {
        Area area = element.getArea();
        return area.contains(e.getX(), e.getY());
    };

    @Override
    public String getDisplayName() {
        return "Sectors Mode";
    }

    @Override
    public boolean onMouseMoved(LevelView levelView, MouseEvent e) {
        LevelElementsManager levelElementsManager = levelView.getLevelElementsManager();
        boolean answer = levelView.highlightElementBehindCursor(e, levelElementsManager.getSectors(), sectorsCmp);
        levelView.repaint();
        return answer;
    }

    @Override
    public boolean onMouseRightPressed(LevelView levelView, MouseEvent e) {
        levelView.openNewDialog(new SectorDialog(levelView));
        levelView.getSelectedElements().clear();
        return true;
    }

    @Override
    public boolean isHighlightingElements() {
        return true;
    }
}
