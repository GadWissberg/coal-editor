package view.dialogs;

import data.handlers.LevelElementsManager;
import data.utils.C.Views.LevelViewSection;
import elements.Actor;
import elements.Element;
import elements.actor.*;
import view.dialogs.components.ExtendedSpinner;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ActorDialog extends LevelElementDialog {

    private final LevelElementsManager levelElementsManager;
    private ExtendedSpinner directionSpinner;
    private JTree tree;

    public ActorDialog(ArrayList<Element> selectedActors, LevelElementsManager levelElementsManager) {
        super(selectedActors);
        this.levelElementsManager = levelElementsManager;
    }


    @Override
    public void init(JDialog dialog) {
        super.init(dialog);
        createDirectionSpinner();
        JPanel typeSection = createSection(LevelViewSection.LevelElementDialog.Actor.TYPE, new FlowLayout());
        initializeTree(createActorsTree(typeSection));
        propertiesPanel.add(typeSection, 0);
    }

    private void createDirectionSpinner() {
        directionSpinner = createSpinner(LevelViewSection.LevelElementDialog.Actor.MAX_DEGREES,
                LevelViewSection.LevelElementDialog.Actor.NAME_DIRECTION_SPINNER, 0, 1);
        addLineWithLabelAndComponent(
                LevelViewSection.LevelElementDialog.Actor.DIRECTION,
                directionSpinner,
                settingsSection);
        GetElementAttributeValue<Actor, Float> getDirection = Actor::getDirection;
        initializeSpinner(directionSpinner, ((Actor) selectedElements.get(0)).getDirection(), getDirection);
    }

    private TreePath find(ActorNode root, Type type) {
        Enumeration<DefaultMutableTreeNode> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = e.nextElement();
            if (node.getUserObject() == type) {
                return new TreePath(node.getPath());
            }
        }
        return null;
    }

    private void initializeTree(ActorNode root) {
        Actor actor = (Actor) selectedElements.get(0);
        TreePath path = find(root, actor.getType());
        tree.setSelectionPath(path);
    }

    private ActorNode createActorsTree(JPanel typeSection) {
        ActorNode top = new ActorNode(LevelViewSection.LevelElementDialog.Actor.ACTORS);
        tree = new JTree(top);
        tree.expandRow(0);
        for (Type type : Type.values()) if (type.isPlaceAble()) top.add(new DefaultMutableTreeNode(type));
        typeSection.add(tree);
        addListenersToTree();
        return top;
    }

    private void addListenersToTree() {
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {

            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                tree.expandRow(0);
            }
        });
        tree.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (node == null) return;
                    Type nodeInfo = (Type) node.getUserObject();
                    directionSpinner.setEnabled(nodeInfo.hasDirection());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }


    @Override
    protected void saveData() throws InvalidValueException {
        super.saveData();
        for (Element element : selectedElements) {
            Actor actor = (Actor) element;
            actor.setDirection(((SpinnerNumberModel) directionSpinner.getModel()).getNumber().floatValue());
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            actor.setType((Type) selected.getUserObject());
        }
    }

    private void handleUnique() {
        Actor actor = (Actor) selectedElements.get(0);
        if (selectedElements.size() != 1 || !actor.getType().isUnique()) return;
        List<LevelElement> currentActorsByType = levelElementsManager.getActorsByType(actor.getType());
        if (actor.getType().isUnique() && currentActorsByType.size() >= 2)
            for (LevelElement currentActor : currentActorsByType) {
                if (currentActor != actor) levelElementsManager.removeActor((Actor) currentActor);
            }
    }

    @Override
    public String getDialogTitle() {
        String single = String.format(LevelViewSection.LevelElementDialog.Actor.TITLE, selectedElements.get(0).getId());
        return selectedElements.size() == 1 ? single : LevelViewSection.LevelElementDialog.Actor.TITLE_MULTIPLE;
    }

    public void onClose() {
        handleUnique();
    }

    private static class ActorNode extends DefaultMutableTreeNode {

        private final Type type;

        ActorNode(String displayName) {
            super(displayName);
            type = null;
        }

        public ActorNode(Type type) {
            super(type.getDisplayName());
            this.type = type;
        }

    }
}
