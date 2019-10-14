package view;

import data.utils.C.Views.LevelViewSection.Border;

import javax.swing.*;

public class CameraView extends JScrollPane {
    private final LevelView levelView;

    public CameraView(LevelView levelView) {
        this.levelView = levelView;
        JPanel border = createBorder();
        border.add(levelView);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private JPanel createBorder() {
        JPanel border = new JPanel();
        border.setBackground(Border.BORDER_COLOR);
        border.setBorder(BorderFactory.createEmptyBorder(Border.BORDER_SIZE, Border.BORDER_SIZE,
                Border.BORDER_SIZE, Border.BORDER_SIZE));
        getViewport().add(border);
        return border;
    }

    public LevelView getLevelView() {
        return levelView;
    }

}
