package view;

import java.awt.event.ActionListener;

public class ButtonDefinition {
    private final String toolTipText;
    private final String iconLocation;
    private final String name;
    private final ActionListener action;

    public ButtonDefinition(String toolTipText, String iconLocation, String name, ActionListener action) {
        this.toolTipText = toolTipText;
        this.iconLocation = iconLocation;
        this.name = name;
        this.action = action;
    }

    public String getToolTipText() {
        return toolTipText;
    }

    public String getIconLocation() {
        return iconLocation;
    }

    public String getName() {
        return name;
    }

    public ActionListener getAction() {
        return action;
    }
}
