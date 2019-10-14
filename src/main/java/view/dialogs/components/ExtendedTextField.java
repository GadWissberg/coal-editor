package view.dialogs.components;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class ExtendedTextField extends JTextField implements ExtendedComponent {
    private String invalidMessage;
    private boolean valueValidity = true;
    private boolean neutralized;

    public ExtendedTextField() {
        super();
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setNeutral(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setNeutral(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setNeutral(false);
            }
        });
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
        setBackground(b ? Color.WHITE : Color.RED);
    }

    @Override
    public void setNeutral(boolean value) {
        if (isValueValid())
            if (value) {
                setText("");
                setInputComponentBackgroundColor(Color.YELLOW);
            } else {
                setInputComponentBackgroundColor(Color.WHITE);
            }
        neutralized = value;
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
