package view;

import data.handlers.ModeChangedListener;
import data.handlers.ModeHandler;
import data.utils.C;
import data.utils.C.Views.ToolBar.CommandsNames;
import data.utils.C.Views.ToolBar.Icons;
import data.utils.C.Views.ToolBar.ToolTips;
import elements.Line;
import view.modes.Mode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import static data.utils.C.Views.ToolBar.ToolTips.*;

public class ToolBarView extends JToolBar implements ModeChangedListener {
    private final ModeHandler modehandler;
    private final LevelView levelView;
    private ButtonGroup modesButtonsGroup;

    public ToolBarView(String name, ModeHandler modeHandler, LevelView levelView) {
        super(name);
        this.modehandler = modeHandler;
        this.levelView = levelView;
        setFloatable(false);
        createButtons();
    }

    private void createButtons() {
        createToolBarSystemButtons();
        addSeparator();
        createToolBarModesGroup();
        addSeparator();
        JToggleButton button = createToolbarToggleButton(C.Views.ToolBar.TOGGLE_SNAP_NAME, TOGGLE_SNAP, Icons.TOGGLE_SNAP,
                e -> LevelView.setSnapToGrid(((JToggleButton) e.getSource()).isSelected()));
        button.setSelected(LevelView.isSnapToGrid());
    }

    private void createToolBarModesGroup() {
        modesButtonsGroup = new ButtonGroup();
        createToolBarModesButtons();
        modesButtonsGroup.setSelected(modesButtonsGroup.getElements().nextElement().getModel(), true);
    }

    private void setButtonIcon(String iconLocation, AbstractButton button) {
        try {
            button.setIcon(new ImageIcon(ImageIO.read(new File(iconLocation)), "temp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createToolBarModesButtons() {
        createLevelElementsModes();
        modesButtonsGroup.add(createToolbarToggleButton(Mode.DRAWING.getDisplayName(), SELECT_MODE_DRAWING,
                Icons.SELECT_MODE_DRAWING, e -> modehandler.setCurrentMode(Mode.DRAWING)));
    }

    private void createLevelElementsModes() {
        modesButtonsGroup.add(createToolbarToggleButton(Mode.VERTICES.getDisplayName(), ToolTips.SELECT_MODE_VERTICES,
                Icons.SELECT_MODE_VERTICES, e -> modehandler.setCurrentMode(Mode.VERTICES)));
        modesButtonsGroup.add(createToolbarToggleButton(Mode.LINES.getDisplayName(), SELECT_MODE_LINES,
                Icons.SELECT_MODE_LINES, e -> modehandler.setCurrentMode(Mode.LINES)));
        modesButtonsGroup.add(createToolbarToggleButton(Mode.SECTORS.getDisplayName(), SELECT_MODE_SECTORS,
                Icons.SELECT_MODE_SECTORS, e -> modehandler.setCurrentMode(Mode.SECTORS)));
        modesButtonsGroup.add(createToolbarToggleButton(Mode.ACTORS.getDisplayName(), SELECT_MODE_ACTORS,
                Icons.SELECT_MODE_ACTORS, e -> modehandler.setCurrentMode(Mode.ACTORS)));
    }

    private void createToolBarSystemButtons() {
        createToolbarButton(CommandsNames.SAVE, ToolTips.SAVE, Icons.SAVE, e -> levelView.getFileManager().saveMap());
        createToolbarButton(CommandsNames.LOAD, ToolTips.LOAD, Icons.LOAD, e -> {
            try {
                levelView.getFileManager().loadMap(this);
            } catch (IOException | Line.NegligibleLineException ex) {
                JOptionPane.showMessageDialog(ToolBarView.this, C.Views.ERROR_LOAD_MAP_FAIL);
                ex.printStackTrace();
            }
        });
        createToolbarButton(CommandsNames.UNDO, ToolTips.UNDO, Icons.UNDO, null);
        createToolbarButton(CommandsNames.REDO, ToolTips.REDO, Icons.REDO, null);
    }

    private JButton createToolbarButton(String name, String toolTipText, String iconLocation, ActionListener action) {
        JButton button = new JButton();
        ButtonDefinition def = new ButtonDefinition(toolTipText, iconLocation, name, action);
        defineToolBarButton(button, def);
        return button;
    }

    private JToggleButton createToolbarToggleButton(String name, String toolTipText, String iconLocation,
                                                    ActionListener action) {
        JToggleButton button = new JToggleButton();
        ButtonDefinition def = new ButtonDefinition(toolTipText, iconLocation, name, action);
        defineToolBarButton(button, def);
        return button;
    }

    private void defineToolBarButton(AbstractButton button, ButtonDefinition definition) {
        button.setName(definition.getName());
        button.setToolTipText(definition.getToolTipText());
        button.addActionListener(definition.getAction());
        setButtonIcon(definition.getIconLocation(), button);
        add(button);
    }

    public void refreshModeSelection() {
        Enumeration<AbstractButton> buttons = modesButtonsGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton currentButton = buttons.nextElement();
            if (currentButton.getName().equals(ModeHandler.getCurrentMode().getDisplayName())) {
                currentButton.setSelected(true);
            }
        }
    }

    @Override
    public void modeChanged(Mode selectedMode) {
        refreshModeSelection();
    }
}
