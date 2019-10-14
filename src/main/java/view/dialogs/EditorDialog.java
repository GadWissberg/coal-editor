package view.dialogs;

import data.utils.C;
import view.dialogs.components.ExtendedSpinner;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.util.ArrayList;

public abstract class EditorDialog extends JPanel {
    private ArrayList<ExtendedSpinner> validableComponents = new ArrayList<>();
    private JPanel mainContentPanel = new JPanel();

    public EditorDialog() {
        super();
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);
        setBorder(new EmptyBorder(C.Views.Dialogs.DIALOG_BORDER, C.Views.Dialogs.DIALOG_BORDER,
                C.Views.Dialogs.DIALOG_BORDER, C.Views.Dialogs.DIALOG_BORDER));
        add(mainContentPanel);
    }

    void setMainContentPanel(Component component) {
        mainContentPanel.add(component);
    }

    ExtendedSpinner createSpinner(int max, String name) {
        return createSpinner(max, name, Integer.MIN_VALUE, 0.1);
    }

    ArrayList<ExtendedSpinner> getValidableComponents() {
        return validableComponents;
    }

    JComponent addLineWithComponent(JComponent component) {
        JPanel line = createNewLine();
        line.add(component);
        mainContentPanel.add(line);
        return component;
    }

    private JPanel createNewLine() {
        JPanel line = new JPanel(new GridLayout(1, 2));
        return line;
    }

    JComponent addLineWithLabelAndComponent(String label, JComponent component) {
        return addLineWithLabelAndComponent(label, component, mainContentPanel);
    }

    JComponent addLineWithLabelAndComponent(String label, JComponent component, JComponent container) {
        return addLineWithLabelAndComponent(label, component, container, null);
    }

    JComponent addLineWithLabelAndComponent(String label, JComponent component, JComponent container,
                                            Border componentBorder) {
        JPanel line = createNewLine();
        line.add(new JLabel(label, JLabel.LEFT));
        if (component != null) {
            initializeLayoutForComponent(component, componentBorder, line);
        }
        container.add(line);
        return component;
    }

    private void initializeLayoutForComponent(JComponent component, Border componentBorder, JPanel line) {
        JPanel componentContainer = new JPanel();
        componentContainer.setLayout(new BoxLayout(componentContainer, BoxLayout.X_AXIS));
        componentContainer.add(component);
        if (componentBorder != null) {
            componentContainer.setPreferredSize(new Dimension(10, 10));
            componentContainer.setBorder(componentBorder);
        }
        line.add(componentContainer);
    }


    public abstract String getDialogTitle();

    public void init(JDialog dialog) {
        addOkButton(dialog);
    }

    private void addOkButton(JDialog dialog) {
        JButton okButton = new JButton(C.Views.LevelViewSection.LevelElementDialog.OK);
        okButton.addActionListener(e -> {
            try {
                saveData();
                dialog.dispose();
            } catch (InvalidValueException ex) {
                ex.printStackTrace();
            }
        });
        okButton.setSize(new Dimension(C.Views.Dialogs.OK_BUTTON_WIDTH, C.Views.Dialogs.OK_BUTTON_HEIGHT));
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(okButton);
    }

    protected void saveData() throws InvalidValueException {
        for (ExtendedSpinner spinner : getValidableComponents()) {
            if (!spinner.isValueValid()) {
                JOptionPane.showMessageDialog(this, spinner.getInvalidMessage());
                throw new InvalidValueException(spinner.getInvalidMessage());
            }
        }
    }

    void initializeSpinner(ExtendedSpinner spinner, float value) {
        spinner.setValue(value);
        validableComponents.add(spinner);
    }

    ExtendedSpinner createSpinner(int max, String name, int min, double stepSize) {
        ExtendedSpinner spinner = new ExtendedSpinner(new SpinnerNumberModel(0, min, max, stepSize));
        JFormattedTextField textField = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        spinner.setName(name);
        ((NumberFormatter) textField.getFormatter()).setAllowsInvalid(false);
        textField.setHorizontalAlignment(JTextField.CENTER);
        spinner.setFractionDigits(1, 1);
        return spinner;
    }

}
