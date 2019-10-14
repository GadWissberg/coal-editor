package view.modes;

import data.handlers.LevelElementsManager;
import data.utils.C;
import elements.Actor;
import view.LevelView;
import view.dialogs.ActorDialog;

import java.awt.event.MouseEvent;

public class ActorsModeDefinition implements ModeDefinition {
    private static LevelView.Comparison<Actor> actorsCmp = (element, e) -> {
        float xDiff = element.getX() - e.getX();
        float yDiff = element.getY() - (C.Data.LEVEL_SIZE_PIXELS - e.getY());
        return Math.hypot(xDiff, yDiff) <= C.Views.LevelViewSection.ActorDisplay.RADIUS;
    };

    @Override
    public String getDisplayName() {
        return "Actors Mode";
    }

    @Override
    public boolean onMouseMoved(LevelView levelView, MouseEvent e) {
        LevelElementsManager levelElementsManager = levelView.getLevelElementsManager();
        boolean answer = levelView.highlightElementBehindCursor(e, levelElementsManager.getActors(), actorsCmp);
        levelView.repaint();
        return answer;
    }

    @Override
    public boolean onMouseRightPressed(LevelView levelView, MouseEvent e) {
        levelView.openNewDialog(new ActorDialog(levelView.getSelectedElements(), levelView.getLevelElementsManager()));
        return true;
    }

    @Override
    public boolean isHighlightingElements() {
        return true;
    }
}
