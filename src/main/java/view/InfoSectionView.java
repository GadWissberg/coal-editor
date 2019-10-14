package view;

import data.handlers.ModeChangedListener;
import data.handlers.ModeHandler;
import data.utils.C;
import view.modes.Mode;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class InfoSectionView extends JToolBar implements ModeChangedListener {
    private JLabel modeLabel;

    public InfoSectionView(String name) {
        super(name);
        setLayout(new BorderLayout());
        setFloatable(false);
        setPreferredSize(new Dimension(C.Views.MainWindow.WINDOW_WIDTH, C.Views.InfoSection.HEIGHT));
        createModeLabel();
    }

    private void createModeLabel() {
        modeLabel = new JLabel(ModeHandler.getCurrentMode().getDisplayName());
        modeLabel.setFont(new Font(C.Views.FONT, Font.PLAIN, C.Views.InfoSection.ModeLabel.SIZE));
        modeLabel.setVerticalAlignment(SwingConstants.TOP);
        Border paddingBorder = BorderFactory.createEmptyBorder(C.Views.InfoSection.ModeLabel.PADDING_TOP_LEFT,
                C.Views.InfoSection.ModeLabel.PADDING_TOP_LEFT, 0, 0);
        modeLabel.setBorder(BorderFactory.createCompoundBorder(null, paddingBorder));
        add(modeLabel);
    }

    private void setLabelText(String displayName) {
        modeLabel.setText(displayName);
    }

    @Override
    public void modeChanged(Mode selectedMode) {
        setLabelText(selectedMode.getDisplayName());
    }
}
