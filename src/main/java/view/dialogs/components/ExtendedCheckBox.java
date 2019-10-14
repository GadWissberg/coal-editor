package view.dialogs.components;

import javax.swing.*;
import java.awt.*;

public class ExtendedCheckBox extends JCheckBox implements ExtendedComponent {
    private String invalidMessage;
    private boolean valueValidity = true;
    private boolean neutral;

    public ExtendedCheckBox() {
        super();
        addActionListener(e -> setNeutral(false));
        setOpaque(true);
    }

    @Override
    public boolean shouldValueBeUsed() {
        return !isNeutral() && isValueValid();
    }

    @Override
    public String getInvalidMessage() {
        return invalidMessage;
    }

    @Override
    public void setInputComponentBackgroundColor(Color color) {
        setBackground(color);
    }

    @Override
    public void setInvalidMessage(String invalidMessage) {
        this.invalidMessage = invalidMessage;
    }

    @Override
    public void setValueValidity(boolean b) {
        valueValidity = b;
        setBackground(b ? getParent().getBackground() : Color.RED);
    }

    @Override
    public void setNeutral(boolean value) {
        if (isValueValid())
            if (value) {
                setText("");
                setInputComponentBackgroundColor(Color.YELLOW);
            } else {
                setInputComponentBackgroundColor(getParent().getBackground());
            }
        this.neutral = value;
    }

    @Override
    public boolean isNeutral() {
        return neutral;
    }

    @Override
    public boolean isValueValid() {
        return valueValidity;
    }
}
