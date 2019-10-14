package view.dialogs.components;

import data.utils.C;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ExtendedSpinner extends JSpinner implements ExtendedComponent {
    private static final ChangeListener negativeValidator = e -> {
        ExtendedSpinner self = (ExtendedSpinner) e.getSource();
        if (((Number) self.getValue()).doubleValue() < 0) {
            self.setValueValidity(false);
            String msg = String.format(C.Views.LevelViewSection.LevelElementDialog.ERROR_NEGATIVE_VALUE, self.getName());
            self.setInvalidMessage(msg);
        } else {
            self.setValueValidity(true);
        }
    };

    private boolean valueValidity = true;
    private String invalidMessage;
    private boolean neutralized;

    public ExtendedSpinner(SpinnerNumberModel spinnerNumberModel) {
        super(spinnerNumberModel);
        addChangeListener(negativeValidator);
        addChangeListener(e -> setNeutral(false));
    }

    public void setInputComponentBackgroundColor(Color color) {
        Component textField = getEditor().getComponent(0);
        textField.setBackground(color);
    }

    public void setFractionDigits(int minimumFractionDigits, int maximumFractionDigits) {
        DecimalFormat format = ((JSpinner.NumberEditor) getEditor()).getFormat();
        format.setMinimumFractionDigits(minimumFractionDigits);
        format.setMaximumFractionDigits(maximumFractionDigits);
    }

    public void allowNegativeValue(boolean b) {
        if (!b) {
            if (Arrays.stream(getChangeListeners()).noneMatch(changeListener -> changeListener == negativeValidator)) {
                addChangeListener(negativeValidator);
            }
        } else {
            removeChangeListener(negativeValidator);
            setValueValidity(true);
        }
    }

    @Override
    public String getInvalidMessage() {
        return invalidMessage;
    }

    @Override
    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }

    @Override
    public void setValueValidity(boolean b) {
        valueValidity = b;
        setInputComponentBackgroundColor(valueValidity ? Color.WHITE : Color.RED);
    }

    @Override
    public void setNeutral(boolean neutral) {
        if (isValueValid())
            if (neutral) {
                setValue(0);
                setInputComponentBackgroundColor(Color.YELLOW);
            } else {
                setInputComponentBackgroundColor(Color.WHITE);
            }
        neutralized = neutral;
    }

    @Override
    public boolean isNeutral() {
        return neutralized;
    }

    @Override
    public boolean isValueValid() {
        return valueValidity;
    }
}
