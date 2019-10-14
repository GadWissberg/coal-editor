package view.dialogs;

import data.utils.C;
import data.utils.C.Views.TopMenu;
import view.LevelView;
import view.dialogs.components.ExtendedSpinner;

import javax.swing.*;

public class SetGridSizeDialog extends EditorDialog {
    private final ExtendedSpinner spinner;
    private final LevelView levelView;

    public SetGridSizeDialog(LevelView levelView) {
        super();
        this.levelView = levelView;
        spinner = createSpinner(Integer.MAX_VALUE, TopMenu.View.SetGridDialog.SPINNER_GRID_SIZE, 0, 1);
        spinner.setFractionDigits(0, 0);
        initializeSpinner(spinner, LevelView.getGridSize());
        addLineWithLabelAndComponent(TopMenu.View.SetGridDialog.LABEL, spinner);
        JButton reset = new JButton(TopMenu.View.SetGridDialog.BUTTON_RESET_LABEL);
        reset.addActionListener(e -> spinner.setValue((double) C.Views.LevelViewSection.DEFAULT_GRID_SIZE));
        addLineWithComponent(reset);
    }

    @Override
    protected void saveData() throws InvalidValueException {
        super.saveData();
        LevelView.setGridSize(((Double) spinner.getValue()).intValue());
        levelView.repaint();
    }

    @Override
    public String getDialogTitle() {
        return TopMenu.View.GRID_SIZE;
    }
}
