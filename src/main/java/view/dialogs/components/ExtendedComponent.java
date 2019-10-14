package view.dialogs.components;

import java.awt.*;

public interface ExtendedComponent {

    default boolean shouldValueBeUsed() {
        return !isNeutral() && isValueValid();
    }

    String getInvalidMessage();

    void setInputComponentBackgroundColor(Color color);

    void setInvalidMessage(String invalidMessage);

    void setValueValidity(boolean b);

    void setNeutral(boolean value);

    boolean isNeutral();

    boolean isValueValid();
}
