package view.dialogs;

import data.utils.C.Views.LevelViewSection.LevelElementDialog.Vertex;
import elements.Element;

import javax.swing.*;
import java.util.ArrayList;

public class VertexDialog extends LevelElementDialog {

    public VertexDialog(ArrayList<Element> selectedVertices) {
        super(selectedVertices);
    }


    @Override
    public void init(JDialog dialog) {
        super.init(dialog);
        Element firstVertex = selectedElements.get(0);
        createCoordinateLabel(firstVertex.getX(), Vertex.LABEL_X);
        createCoordinateLabel(firstVertex.getY(), Vertex.LABEL_Y);
    }

    private void createCoordinateLabel(float value, String text) {
        JLabel label = new JLabel();
        if (selectedElements.size() == 1) {
            label.setText(String.valueOf(value));
        } else {
            label.setText(Vertex.MSG_MULTIPLE);
        }
        addLineWithLabelAndComponent(text, label, settingsSection);
    }

    @Override
    public String getDialogTitle() {
        String single = String.format(Vertex.TITLE, selectedElements.get(0).getId());
        return selectedElements.size() == 1 ? single : Vertex.TITLE_MULTIPLE;
    }

}
