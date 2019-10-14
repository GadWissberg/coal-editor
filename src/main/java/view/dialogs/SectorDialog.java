package view.dialogs;

import data.utils.C.Views.LevelViewSection;
import elements.Actor;
import elements.Element;
import elements.sectors.Sector;
import elements.texture.*;
import view.LevelView;
import view.dialogs.components.ExtendedSpinner;
import view.dialogs.components.ExtendedTextField;
import view.dialogs.components.TextureAttributesSection;

import javax.swing.*;
import java.util.List;

public class SectorDialog extends LevelElementDialog {

    private final LevelView levelView;
    private ExtendedSpinner floorAltSpinner;
    private ExtendedSpinner ceilAltSpinner;
    private TextureAttributesSection ceilingTextureSection;
    private TextureAttributesSection floorTextureSection;

    public SectorDialog(LevelView levelView) {
        super(levelView.getSelectedElements());
        this.levelView = levelView;
    }


    @Override
    public void init(JDialog dialog) {
        super.init(dialog);
        createFloorAltitudeSpinner();
        createCeilingAltitudeSpinner();
        createTextureRelated();
    }

    private void createTextureRelated() {
        JPanel texturesSection = addTexturesTab(1);
        JPanel container = new JPanel();
        floorTextureSection = createTextureSection(Sector::getFloorTexture, LevelViewSection.LevelElementDialog.Sector.NAME_TEX_FLOOR);
        ceilingTextureSection = createTextureSection(Sector::getCeilingTexture, LevelViewSection.LevelElementDialog.Sector.NAME_TEX_CEILING);
        container.add(floorTextureSection);
        container.add(ceilingTextureSection);
        texturesSection.add(container);
    }

    private TextureAttributesSection createTextureSection(GetElementAttributeValue<Sector, TextureDefinition> specificTexture, String label) {
        TextureAttributesSection section = createTextureAttributesSection(specificTexture, label);
        return section;
    }

    private void createCeilingAltitudeSpinner() {
        ceilAltSpinner = (ExtendedSpinner) addLineWithLabelAndComponent(
                LevelViewSection.LevelElementDialog.Sector.CEILING_ALTITUDE,
                createSpinner(Integer.MAX_VALUE, LevelViewSection.LevelElementDialog.Sector.NAME_CEILING_SPINNER),
                settingsSection, BorderFactory.createEmptyBorder(10, 150, 10, 150));
        GetElementAttributeValue<Sector, Float> getCeilingAltitude = Sector::getCeilingAltitude;
        initializeSpinner(ceilAltSpinner, ((Sector) selectedElements.get(0)).getCeilingAltitude(), getCeilingAltitude);
    }

    private void createFloorAltitudeSpinner() {
        floorAltSpinner = (ExtendedSpinner) addLineWithLabelAndComponent(
                LevelViewSection.LevelElementDialog.Sector.FLOOR_ALTITUDE,
                createSpinner(Integer.MAX_VALUE, LevelViewSection.LevelElementDialog.Sector.NAME_FLOOR_SPINNER),
                settingsSection, BorderFactory.createEmptyBorder(10, 150, 10, 150));
        GetElementAttributeValue<Sector, Float> getFloorAltitude = Sector::getFloorAltitude;
        initializeSpinner(floorAltSpinner, ((Sector) selectedElements.get(0)).getFloorAltitude(), getFloorAltitude);
    }


    @Override
    protected void saveData() throws InvalidValueException {
        super.saveData();
        float floorAltitude = ((SpinnerNumberModel) floorAltSpinner.getModel()).getNumber().floatValue();
        float ceilingAltitude = ((SpinnerNumberModel) ceilAltSpinner.getModel()).getNumber().floatValue();
        if (!floorAltSpinner.isNeutral() && !ceilAltSpinner.isNeutral() && ceilingAltitude <= floorAltitude) {
            JOptionPane.showMessageDialog(this, LevelViewSection.LevelElementDialog.Sector.ALERT_ALTITUDES);
            throw new InvalidValueException(LevelViewSection.LevelElementDialog.Sector.ALERT_ALTITUDES);
        }
        applyAttributesDataToLevel(floorAltitude, ceilingAltitude);
    }

    private void applyAttributesDataToLevel(float floorAltitude, float ceilingAltitude) {
        updateSelectedElements(floorAltitude, ceilingAltitude);
        recalculateAttributesForContainedActors();
    }

    private void updateSelectedElements(float floorAlt, float ceilingAlt) {
        for (Element element : selectedElements) {
            Sector s = (Sector) element;
            s.setFloorAltitude(floorAltSpinner.shouldValueBeUsed() ? floorAlt : s.getFloorAltitude());
            s.setCeilingAltitude(ceilAltSpinner.shouldValueBeUsed() ? ceilingAlt : s.getCeilingAltitude());
            saveTextureData(s);
        }
    }

    private void saveTextureData(Sector s) {
        ExtendedTextField floorTextureField = floorTextureSection.getTextureField();
        ExtendedTextField ceilingTextureField = ceilingTextureSection.getTextureField();
        if (floorTextureField.shouldValueBeUsed()) s.getFloorTexture().setName(floorTextureField.getText());
        if (ceilingTextureField.shouldValueBeUsed()) s.getCeilingTexture().setName(ceilingTextureField.getText());
        saveTextureOffsetsData(s);
    }

    private void saveTextureOffsetsData(Sector s) {
        saveFloorOffsets(s);
        saveCeilingOffsets(s);
    }

    private void saveFloorOffsets(Sector s) {
        ExtendedSpinner horSpinner = floorTextureSection.getHorSpinner();
        TextureDefinition floorTexture = s.getFloorTexture();
        if (horSpinner.shouldValueBeUsed())
            floorTexture.setHorizontalOffset(((Number) horSpinner.getValue()).floatValue());
        ExtendedSpinner verSpinner = floorTextureSection.getVerSpinner();
        if (verSpinner.shouldValueBeUsed())
            floorTexture.setVerticalOffset(((Number) verSpinner.getValue()).floatValue());
    }

    private void saveCeilingOffsets(Sector s) {
        ExtendedSpinner horSpinner = ceilingTextureSection.getHorSpinner();
        TextureDefinition ceilingTexture = s.getCeilingTexture();
        if (horSpinner.shouldValueBeUsed())
            ceilingTexture.setHorizontalOffset(((Number) horSpinner.getValue()).floatValue());
        ExtendedSpinner verSpinner = ceilingTextureSection.getVerSpinner();
        if (verSpinner.shouldValueBeUsed())
            ceilingTexture.setVerticalOffset(((Number) verSpinner.getValue()).floatValue());
    }

    private void recalculateAttributesForContainedActors() {
        for (Element element : selectedElements) {
            Sector sector = (Sector) element;
            List<Actor> actors = levelView.getLevelElementsManager().getActorsInArea(sector.getX(), sector.getY(),
                    sector.getWidth(), sector.getHeight());
            for (Actor actor : actors)
                if (sector.getArea().intersects(actor.getX(), actor.getDisplayY(), actor.getWidth(), actor.getHeight()))
                    levelView.calculateValuesForActor(actor);
        }
    }

    @Override
    public String getDialogTitle() {
        String single = String.format(LevelViewSection.LevelElementDialog.Sector.TITLE, selectedElements.get(0).getId());
        return selectedElements.size() == 1 ? single : LevelViewSection.LevelElementDialog.Sector.TITLE_MULTIPLE;
    }

}
