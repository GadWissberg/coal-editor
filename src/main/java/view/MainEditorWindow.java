package view;

import data.handlers.ModeHandler;
import data.utils.C;
import view.dialogs.EditorDialog;
import view.dialogs.SetGridSizeDialog;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;

public class MainEditorWindow extends JFrame implements WindowListener, MapLoadSubscriber {
    private InfoSectionView infoSection;
    private CameraView cameraView;
    private ModeHandler modeHandler = new ModeHandler();
    private JMenuBar topMenu;

    public MainEditorWindow() throws HeadlessException {
        addWindowListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                try {
                    EditorPreferences.flush();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void initialize() throws InterruptedException {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                    UnsupportedLookAndFeelException ignored) {
            }
            initializeWindow();
            createWindowContent();
            pack();
            doneInitialization();
        });
        synchronized (this) {
            wait();
        }
    }

    private void initializeWindow() {
        refreshTitle(C.Views.MainWindow.TITLE_NO_PROJECT);
        setVisible(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    private void refreshTitle(String projectName) {
        setTitle(C.Views.MainWindow.TITLE + " - " + projectName);
    }

    private CameraView initializeCameraView() {
        LevelView levelView = new LevelView(new Dimension(C.Data.LEVEL_SIZE_PIXELS + 1, C.Data.LEVEL_SIZE_PIXELS + 1), modeHandler);
        levelView.getFileManager().subscribeForMapLoad(this);
        modeHandler.registerForModeChange(levelView);
        cameraView = new CameraView(levelView);
        cameraView.getViewport().setPreferredSize(new Dimension(getWidth() / 2, C.Views.LevelViewSection.HEIGHT));
        return cameraView;
    }

    private void createWindowContent() {
        createTopMenu();
        createInfoSection();
        initializeCameraView();
        createToolBar();
        createSplitPane();
        createStatusBar();
    }

    private void createTopMenu() {
        topMenu = new JMenuBar();
        createViewTopMenu();
        createSectorsTopMenu();
        createWindowsTopMenu();
        setJMenuBar(topMenu);
    }

    private void createViewTopMenu() {
        JMenu viewMenu = addTopMenu(C.Views.TopMenu.View.LABEL);
        JMenuItem gridSize = new JMenuItem(C.Views.TopMenu.View.GRID_SIZE);
        gridSize.addActionListener(e -> {
            JDialog dialog = createNewDialog(new SetGridSizeDialog(cameraView.getLevelView()));
            dialog.setVisible(true);
        });
        viewMenu.add(gridSize);
    }

    private void initializeNewDialog(EditorDialog pane, JDialog dialog) {
        dialog.setTitle(pane.getDialogTitle());
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(pane);
        pane.init(dialog);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.pack();
    }

    public JDialog createNewDialog(EditorDialog pane) {
        return createNewDialog(pane, null);
    }

    public JDialog createNewDialog(EditorDialog pane, WindowAdapter windowListener) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this));
        initializeNewDialog(pane, dialog);
        if (windowListener != null) {
            dialog.addWindowListener(windowListener);
        }
        return dialog;
    }

    private void createSectorsTopMenu() {
        JMenu sectorsMenu = addTopMenu(C.Views.TopMenu.Sectors.LABEL);
        JMenuItem deleteMenu = new JMenuItem(C.Views.TopMenu.Sectors.DELETE_SECTOR);
        deleteMenu.addActionListener(e -> cameraView.getLevelView().deleteSelectedItems());
        sectorsMenu.add(deleteMenu);
    }

    private void createWindowsTopMenu() {
        JMenu windowsMenu = addTopMenu(C.Views.TopMenu.Windows.LABEL);
        JCheckBoxMenuItem info = new JCheckBoxMenuItem(C.Views.TopMenu.Windows.INFORMATION);
        info.setState(true);
        info.addActionListener(e -> infoSection.setVisible(info.getState()));
        windowsMenu.add(info);
    }

    private JMenu addTopMenu(String label) {
        JMenu menu = new JMenu(label);
        topMenu.add(menu);
        return menu;
    }

    private void createStatusBar() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        add(statusPanel, BorderLayout.SOUTH);
        statusPanel.setPreferredSize(new Dimension(getWidth() / 2, C.Views.StatusBar.HEIGHT));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        createMousePositionLabel(statusPanel);
    }

    private void createMousePositionLabel(JPanel statusPanel) {
        JLabel mousePositionLabel = new JLabel();
        mousePositionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(mousePositionLabel);
    }

    private void createSplitPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cameraView, infoSection);
        getContentPane().add(splitPane);
        splitPane.setEnabled(true);
    }

    private void createToolBar() {
        ToolBarView toolBar = new ToolBarView(C.Views.ToolBar.NAME, modeHandler, cameraView.getLevelView());
        add(toolBar, BorderLayout.PAGE_START);
        modeHandler.registerForModeChange(toolBar);
    }

    private void createInfoSection() {
        infoSection = new InfoSectionView(C.Views.InfoSection.NAME);
        modeHandler.registerForModeChange(infoSection);
    }

    private void doneInitialization() {
        JScrollBar verticalScrollBar = cameraView.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(C.Views.LevelViewSection.SCROLL_SPEED);
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void onMapLoad(String name) {
        refreshTitle(name);
    }
}
