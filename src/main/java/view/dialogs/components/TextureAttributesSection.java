package view.dialogs.components;

import javax.swing.*;

public class TextureAttributesSection extends JPanel {
    private ExtendedTextField textureField;
    private ExtendedSpinner horSpinner;
    private ExtendedSpinner verSpinner;
    private ExtendedSpinner opacitySpinner;

    public TextureAttributesSection(String name) {
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), name));
    }

    public ExtendedTextField getTextureField() {
        return textureField;
    }

    public void setTextureField(ExtendedTextField field) {
        this.textureField = field;
    }

    public void setHorOffsetSpinner(ExtendedSpinner spinner) {
        this.horSpinner = spinner;
    }

    public void setVerOffsetSpinner(ExtendedSpinner spinner) {
        this.verSpinner = spinner;
    }

    public ExtendedSpinner getHorSpinner() {
        return horSpinner;
    }

    public ExtendedSpinner getVerSpinner() {
        return verSpinner;
    }

    public void setOpacityField(ExtendedSpinner spinner) {
        opacitySpinner = spinner;
    }

    public ExtendedSpinner getOpacitySpinner() {
        return opacitySpinner;
    }
}
