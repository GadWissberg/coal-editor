package view.dialogs;

import data.utils.C;
import data.utils.C.Views.LevelViewSection;
import elements.Element;
import elements.Line;
import elements.texture.*;
import view.dialogs.components.ExtendedCheckBox;
import view.dialogs.components.ExtendedSpinner;
import view.dialogs.components.ExtendedTextField;
import view.dialogs.components.TextureAttributesSection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Predicate;

public class LineDialog extends LevelElementDialog {

    private ExtendedCheckBox solidCheckBox;
    private TextureAttributesSection backMidTexPan;
    private TextureAttributesSection backBotTexPan;
    private TextureAttributesSection backTopTexPan;
    private TextureAttributesSection frontMidTexPan;
    private TextureAttributesSection frontBotTexPan;
    private TextureAttributesSection frontTopTexPan;

    public LineDialog(ArrayList<Element> selectedLines) {
        super(selectedLines);
    }


    @Override
    public void init(JDialog dialog) {
        super.init(dialog);
        createCheckBoxes();
        createInfo();
        createTextureRelated();
    }

    private void createTextureRelated() {
        JPanel texturesSection = addTexturesTab(2);
        JPanel frontSection = createSection(C.Views.LevelViewSection.LevelElementDialog.Line.FRONT_TEXTURES);
        JPanel backSection = createSection(LevelViewSection.LevelElementDialog.Line.BACK_TEXTURES);
        texturesSection.add(frontSection);
        texturesSection.add(backSection);
        createFrontTextureFields(frontSection);
        createBackTextureFields(backSection);
    }

    private void createBackTextureFields(JPanel texturesSection) {
        backTopTexPan = createTextureField(texturesSection, checkedLine -> checkedLine.getBackTexture().getTop(),
                LevelViewSection.LevelElementDialog.Line.NAME_BACK_TOP_TEXTURE);
        backMidTexPan = createTextureField(texturesSection, checkedLine -> checkedLine.getBackTexture().getMiddle(),
                LevelViewSection.LevelElementDialog.Line.NAME_BACK_MIDDLE_TEXTURE, true);
        backBotTexPan = createTextureField(texturesSection, checkedLine -> checkedLine.getBackTexture().getBottom(),
                LevelViewSection.LevelElementDialog.Line.NAME_BACK_BOTTOM_TEXTURE);
    }

    private void createFrontTextureFields(JPanel texturesSection) {
        frontTopTexPan = createTextureField(texturesSection, checkedLine -> checkedLine.getFrontTexture().getTop(),
                LevelViewSection.LevelElementDialog.Line.NAME_FRONT_TOP_TEXTURE);
        frontMidTexPan = createTextureField(texturesSection, checkedLine -> checkedLine.getFrontTexture().getMiddle(),
                LevelViewSection.LevelElementDialog.Line.NAME_FRONT_MIDDLE_TEXTURE, true);
        frontBotTexPan = createTextureField(texturesSection, checkedLine -> checkedLine.getFrontTexture().getBottom(),
                LevelViewSection.LevelElementDialog.Line.NAME_FRONT_BOTTOM_TEXTURE);
    }

    private TextureAttributesSection createTextureField(JPanel texturesSection,
                                                        GetElementAttributeValue<Line, TextureDefinition> specificTexture, String label) {
        return createTextureField(texturesSection, specificTexture, label, false);
    }

    private TextureAttributesSection createTextureField(JPanel texturesSection,
                                                        GetElementAttributeValue<Line, TextureDefinition> specificTexture, String label,
                                                        boolean blend) {
        return createAndAddWallTexturePanel(specificTexture, texturesSection, label, blend);
    }

    private TextureAttributesSection createAndAddWallTexturePanel(
            GetElementAttributeValue<Line, TextureDefinition> specificTexture, JPanel texturesSection, String label,
            boolean blend) {
        TextureAttributesSection panel = createTextureAttributesSection(specificTexture, label, blend);
        texturesSection.add(panel);
        return panel;
    }

    private void createInfo() {
        Line line = (Line) selectedElements.get(0);
        createSectorsIdsLabels(line);
        createVerticesIdsLabels(line);
    }

    private void createVerticesIdsLabels(Line line) {
        createRelativeElementIdLabel(LevelViewSection.LevelElementDialog.Line.SOURCE_VERTEX_ID,
                line.getSrc().getId());
        createRelativeElementIdLabel(LevelViewSection.LevelElementDialog.Line.DESTINATION_VERTEX_ID,
                line.getDst().getId());
    }

    private void createSectorsIdsLabels(Line line) {
        createRelativeElementIdLabel(LevelViewSection.LevelElementDialog.Line.FRONT_SECTOR_ID,
                line.getFrontSectorId());
        createRelativeElementIdLabel(LevelViewSection.LevelElementDialog.Line.BACK_SECTOR_ID,
                line.getBackSectorId());
    }

    private JLabel createRelativeElementIdLabel(String labelText, long id) {
        JLabel label = createElementIdLabel();
        initializeElementIdLabel(() -> label.setText(String.format(labelText, Long.toString(id))),
                () -> label.setText(String.format(labelText, LevelViewSection.LevelElementDialog.Line.NO_ELEMENT)));
        return label;
    }

    private JLabel createElementIdLabel() {
        JPanel uiLine = new JPanel(new BorderLayout(0, 0));
        JLabel label = new JLabel();
        uiLine.add(label);
        settingsSection.add(uiLine);
        return label;
    }

    private void initializeElementIdLabel(Runnable onSingleElementSelected, Runnable onMultipleElementsSelected) {
        if (selectedElements.size() == 1) {
            onSingleElementSelected.run();
        } else onMultipleElementsSelected.run();
    }

    private void createCheckBoxes() {
        solidCheckBox = createAttributeCheckBox(LevelViewSection.LevelElementDialog.Line.SOLID, Line::isSolid,
                checkLine -> checkLine.getBackSectorId() < 0 || checkLine.getFrontSectorId() < 0);
    }

    private ExtendedCheckBox createAttributeCheckBox(String label, GetElementAttributeValue<Line, Boolean> attribute,
                                                     Predicate<Line> additionalCheck) {
        JPanel line = new JPanel(new BorderLayout(0, 0));
        ExtendedCheckBox checkBox = new ExtendedCheckBox();
        initializeCheckBox(attribute, checkBox, additionalCheck);
        line.add(new JLabel(label), BorderLayout.LINE_START);
        line.add(checkBox);
        settingsSection.add(line);
        return checkBox;
    }

    private void initializeCheckBox(GetElementAttributeValue<Line, Boolean> attributeCheck,
                                    ExtendedCheckBox checkBox, Predicate<Line> additionalCheck) {
        Line first = (Line) selectedElements.get(0);
        if (selectedElements.size() == 1 && (additionalCheck.test(first))) {
            checkBox.setSelected(true);
            checkBox.setEnabled(false);
        } else {
            initializeCheckBoxForMultipleElements(checkBox, attributeCheck);
        }
    }

    private void initializeCheckBoxForMultipleElements(ExtendedCheckBox checkBox,
                                                       GetElementAttributeValue<Line, Boolean> attributeCheck) {
        checkBox.setSelected(attributeCheck.getValue((Line) selectedElements.get(0)));
        if (!checkIfAllSelectedHaveSameValue(attributeCheck)) {
            checkBox.setSelected(false);
            checkBox.setNeutral(true);
        }
    }

    @Override
    protected void saveData() throws InvalidValueException {
        super.saveData();
        for (Element element : selectedElements) {
            saveLineChanges((Line) element);
        }
    }

    private void saveLineChanges(Line line) {
        if (line.getBackSectorId() >= 0 && line.getFrontSectorId() >= 0) line.setSolid(solidCheckBox.isSelected());
        else line.setSolid(true);
        saveTextureData(line);
    }

    private void saveTextureData(Line line) {
        saveTextureNames(line);
        saveTextureOffsets(line);
        saveTextureBlending(line);
    }

    private void saveTextureBlending(Line line) {
        TextureDefinition frontMiddle = line.getFrontTexture().getMiddle();
        frontMiddle.setOpacity(((Number) frontMidTexPan.getOpacitySpinner().getValue()).floatValue());
        TextureDefinition backMiddle = line.getBackTexture().getMiddle();
        backMiddle.setOpacity(((Number) backMidTexPan.getOpacitySpinner().getValue()).floatValue());
    }

    private void saveTextureOffsets(Line line) {
        WallTextureDefinition frontTexture = line.getFrontTexture();
        WallTextureDefinition backTexture = line.getBackTexture();
        saveTextureOffsets(frontTexture.getTop(), frontTopTexPan);
        saveTextureOffsets(frontTexture.getMiddle(), frontMidTexPan);
        saveTextureOffsets(frontTexture.getBottom(), frontBotTexPan);
        saveTextureOffsets(backTexture.getTop(), backTopTexPan);
        saveTextureOffsets(backTexture.getMiddle(), backMidTexPan);
        saveTextureOffsets(backTexture.getBottom(), backBotTexPan);
    }

    private void saveTextureOffsets(TextureDefinition textureDefinition, TextureAttributesSection textureAttributesSection) {
        ExtendedSpinner horSpinner = textureAttributesSection.getHorSpinner();
        if (horSpinner.shouldValueBeUsed())
            textureDefinition.setHorizontalOffset(((Number) horSpinner.getValue()).floatValue());
        ExtendedSpinner verSpinner = textureAttributesSection.getVerSpinner();
        if (horSpinner.shouldValueBeUsed())
            textureDefinition.setVerticalOffset(((Number) verSpinner.getValue()).floatValue());
    }

    private void saveTextureNames(Line line) {
        WallTextureDefinition frontTexture = line.getFrontTexture();
        WallTextureDefinition backTexture = line.getBackTexture();
        saveTextureName(frontTopTexPan, frontTexture.getTop());
        saveTextureName(frontMidTexPan, frontTexture.getMiddle());
        saveTextureName(frontBotTexPan, frontTexture.getBottom());
        saveTextureName(backTopTexPan, backTexture.getTop());
        saveTextureName(backMidTexPan, backTexture.getMiddle());
        saveTextureName(backBotTexPan, backTexture.getBottom());
    }

    private void saveTextureName(TextureAttributesSection texturePanel, TextureDefinition wallTextureDefinition) {
        ExtendedTextField textureField = texturePanel.getTextureField();
        if (textureField.shouldValueBeUsed())
            wallTextureDefinition.setName(textureField.getText());
    }

    @Override
    public String getDialogTitle() {
        String single = String.format(LevelViewSection.LevelElementDialog.Line.TITLE, selectedElements.get(0).getId());
        return selectedElements.size() == 1 ? single : LevelViewSection.LevelElementDialog.Line.TITLE_MULTIPLE;
    }

}
