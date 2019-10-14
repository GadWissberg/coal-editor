package view.dialogs;

import data.utils.C.Views.LevelViewSection;
import elements.Element;
import elements.Line;
import elements.texture.*;
import view.dialogs.components.ExtendedSpinner;
import view.dialogs.components.ExtendedTextField;
import view.dialogs.components.TextureAttributesSection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public abstract class LevelElementDialog extends EditorDialog {
    JPanel propertiesPanel;
    protected ArrayList<Element> selectedElements;
    protected JPanel settingsSection;
    protected JTabbedPane tabbedPane;

    public LevelElementDialog(ArrayList<Element> selectElements) {
        super();
        selectedElements = selectElements;
    }

    protected JPanel createPropertiesPanel() {
        propertiesPanel = createTabPanel();
        return propertiesPanel;
    }

    protected JPanel createTabPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return panel;
    }

    @Override
    public void init(JDialog dialog) {
        super.init(dialog);
        tabbedPane = new JTabbedPane();
        propertiesPanel = createPropertiesPanel();
        tabbedPane.addTab(LevelViewSection.LevelElementDialog.PROPERTIES_TAB_NAME, propertiesPanel);
        setMainContentPanel(add(tabbedPane));
        addSettingsSection(propertiesPanel);
    }


    private void addSettingsSection(JPanel propertiesPanel) {
        settingsSection = createSection(LevelViewSection.LevelElementDialog.SETTINGS);
        propertiesPanel.add(settingsSection);
    }

    JPanel createSection(String label) {
        return createSection(label, null);
    }

    JPanel createSection(String label, LayoutManager layoutManager) {
        JPanel section = new JPanel();
        section.setLayout(layoutManager == null ? new BoxLayout(section, BoxLayout.Y_AXIS) : layoutManager);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), label);
        int pad = LevelViewSection.LevelElementDialog.SECTION_PADDING;
        Border padding = BorderFactory.createEmptyBorder(pad, pad, pad, pad);
        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(padding, titledBorder);
        section.setBorder(BorderFactory.createCompoundBorder(compoundBorder, padding));
        return section;
    }

    void initializeSpinner(ExtendedSpinner spinner, float value,
                           GetElementAttributeValue<? extends Object, Float> wantedValue) {
        super.initializeSpinner(spinner, value);
        if (selectedElements.size() == 1 || checkIfAllSelectedHaveSameValue(wantedValue))
            spinner.setValue(value);
        else spinner.setNeutral(true);
    }


    protected <T> ExtendedTextField createAndAddTextureField(String name, TextureAttributesSection container,
                                                             GetElementAttributeValue<T, TextureDefinition> specificTexture) {
        ExtendedTextField textField = new ExtendedTextField();
        textField.setName(name);
        if (selectedElements.size() == 1 || checkIfAllSelectedHaveSameValue((GetElementAttributeValue<T, String>) element -> specificTexture.getValue(element).getName())) {
            textField.setText(specificTexture.getValue((T) selectedElements.get(0)).getName());
        } else textField.setNeutral(true);
        addLineWithLabelAndComponent(name + ":", textField, container);
        return textField;
    }

    protected <T, S> boolean checkIfAllSelectedHaveSameValue(GetElementAttributeValue<T, S> wantedValue) {
        S value = wantedValue.getValue((T) selectedElements.get(0));
        for (Element element : selectedElements) {
            T otherElement = (T) element;
            S otherElementValue = wantedValue.getValue(otherElement);
            if (!Objects.equals(otherElementValue, value))
                return false;
        }
        return true;
    }

    protected <T> ExtendedSpinner createAndAddOffsetSpinner(String name,
                                                            GetElementAttributeValue<T, Float> wantedValue,
                                                            TextureAttributesSection panel) {
        ExtendedSpinner spinner = createAndAddTextureRelatedSpinner(name, wantedValue, panel);
        spinner.allowNegativeValue(true);
        return spinner;
    }

    protected <T> ExtendedSpinner createAndAddTextureRelatedSpinner(String name,
                                                                    GetElementAttributeValue<T, Float> wantedValue,
                                                                    TextureAttributesSection panel) {
        ExtendedSpinner textureOffsetSpinner = createSpinner(Integer.MAX_VALUE, name);
        addLineWithLabelAndComponent(name + ":", textureOffsetSpinner, panel);
        if (selectedElements.size() == 1 || checkIfAllSelectedHaveSameValue(wantedValue)) {
            textureOffsetSpinner.setValue(wantedValue.getValue((T) selectedElements.get(0)));
        } else textureOffsetSpinner.setNeutral(true);
        return textureOffsetSpinner;
    }

    protected <T> TextureAttributesSection createTextureAttributesSection(
            GetElementAttributeValue<T, TextureDefinition> specificTexture, String label) {
        return createTextureAttributesSection(specificTexture, label, false);
    }

    protected <T> TextureAttributesSection createTextureAttributesSection(
            GetElementAttributeValue<T, TextureDefinition> specificTexture, String label, boolean allowOpacity) {
        TextureAttributesSection section = new TextureAttributesSection(label);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setTextureField(createAndAddTextureField(label, section, specificTexture));
        String horName = String.format(LevelViewSection.LevelElementDialog.Line.SPINNER_NAME_HOR_TEX, label);
        String verName = String.format(LevelViewSection.LevelElementDialog.Line.SPINNER_NAME_VER_TEX, label);
        section.setHorOffsetSpinner(createAndAddOffsetSpinner(horName, element -> specificTexture.getValue((T) element).getHorizontalOffset(), section));
        section.setVerOffsetSpinner(createAndAddOffsetSpinner(verName, element -> specificTexture.getValue((T) element).getVerticalOffset(), section));
        if (allowOpacity)
            section.setOpacityField(createAndAddTextureRelatedSpinner(LevelViewSection.LevelElementDialog.OPACITY_SPINNER,
                    (GetElementAttributeValue<Line, Float>) element -> specificTexture.getValue((T) element).getOpacity(), section));
        return section;
    }

    private JCheckBox createAndAddTextureTransparentCheckBox(TextureAttributesSection panel, GetElementAttributeValue func) {
        JCheckBox checkbox = new JCheckBox();
        if (selectedElements.size() == 1 || checkIfAllSelectedHaveSameValue(func)) {
            checkbox.setSelected((Boolean) func.getValue(selectedElements.get(0)));
        }
        addLineWithLabelAndComponent("Transparent:", checkbox, panel);
        return checkbox;
    }

    protected JPanel addTexturesTab(int numberOfCols) {
        JPanel textureTab = createTabPanel();
        GridLayout layoutManager = new GridLayout(1, numberOfCols);
        JPanel section = createSection(LevelViewSection.LevelElementDialog.TEXTURE_TAB, layoutManager);
        textureTab.add(section);
        tabbedPane.addTab(LevelViewSection.LevelElementDialog.TEXTURE_TAB, textureTab);
        return section;
    }

    protected interface GetElementAttributeValue<T, U> {
        U getValue(T element);
    }
}
