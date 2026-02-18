package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.FresnelReflector;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Mirror;
import org.concord.energy3d.model.ParabolicDish;
import org.concord.energy3d.model.ParabolicTrough;
import org.concord.energy3d.model.PartGroup;
import org.concord.energy3d.model.Rack;
import org.concord.energy3d.model.Roof;
import org.concord.energy3d.model.SolarPanel;
import org.concord.energy3d.model.Wall;
import org.concord.energy3d.model.Window;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 */
class GroupSelector {

    static final String[] types = {
            I18n.get("group.type.solar_panel"),
            I18n.get("group.type.solar_panel_rack"),
            I18n.get("group.type.mirror"),
            I18n.get("group.type.parabolic_trough"),
            I18n.get("group.type.parabolic_dish"),
            I18n.get("group.type.fresnel_reflector"),
            I18n.get("group.type.window"),
            I18n.get("group.type.wall"),
            I18n.get("group.type.roof"),
            I18n.get("group.type.foundation"),
            I18n.get("group.type.foundation_mean")
    };
    private String currentGroupType = I18n.get("group.type.solar_panel");

    private ArrayList<Long> getIdArray(final Class<?> c) {
        final ArrayList<Long> idArray = new ArrayList<>();
        for (final HousePart p : Scene.getInstance().getParts()) {
            if (c.isInstance(p)) {
                idArray.add(p.getId());
            }
        }
        Collections.sort(idArray);
        return idArray;
    }

    private Class<?> getCurrentGroupClass() {
        Class<?> c = null;
        if (I18n.get("group.type.wall").equals(currentGroupType)) {
            c = Wall.class;
        } else if (I18n.get("group.type.window").equals(currentGroupType)) {
            c = Window.class;
        } else if (I18n.get("group.type.roof").equals(currentGroupType)) {
            c = Roof.class;
        } else if (I18n.get("group.type.solar_panel").equals(currentGroupType)) {
            c = SolarPanel.class;
        } else if (I18n.get("group.type.solar_panel_rack").equals(currentGroupType)) {
            c = Rack.class;
        } else if (I18n.get("group.type.mirror").equals(currentGroupType)) {
            c = Mirror.class;
        } else if (I18n.get("group.type.parabolic_trough").equals(currentGroupType)) {
            c = ParabolicTrough.class;
        } else if (I18n.get("group.type.parabolic_dish").equals(currentGroupType)) {
            c = ParabolicDish.class;
        } else if (I18n.get("group.type.fresnel_reflector").equals(currentGroupType)) {
            c = FresnelReflector.class;
        } else if (I18n.get("group.type.foundation").equals(currentGroupType) || currentGroupType.startsWith(I18n.get("group.type.foundation"))) {
            c = Foundation.class;
        }
        return c;
    }

    void setCurrentGroupType(final String currentGroupType) {
        this.currentGroupType = currentGroupType;
    }

    PartGroup select() {
        final JPanel gui = new JPanel(new BorderLayout(5, 5));
        gui.setBorder(BorderFactory.createTitledBorder(I18n.get("dialog.types_and_ids")));
        final DefaultListModel<Long> idListModel = new DefaultListModel<>();
        final JComboBox<String> typeComboBox = new JComboBox<>(types);
        if (currentGroupType != null) {
            typeComboBox.setSelectedItem(currentGroupType);
        }
        typeComboBox.addItemListener(e -> {
            idListModel.clear();
            currentGroupType = (String) typeComboBox.getSelectedItem();
            final Class<?> c = getCurrentGroupClass();
            if (c != null) {
                final ArrayList<Long> idArray = getIdArray(c);
                for (final Long id : idArray) {
                    idListModel.addElement(id);
                }
            }
        });
        final Class<?> c = getCurrentGroupClass();
        if (c != null) {
            final ArrayList<Long> idArray = getIdArray(c);
            for (final Long id : idArray) {
                idListModel.addElement(id);
            }
        }
        final JList<Long> idList = new JList<>(idListModel);
        idList.addListSelectionListener(e -> {
            SceneManager.getInstance().hideAllEditPoints();
            final List<Long> selectedValues = idList.getSelectedValuesList();
            for (final Long i : selectedValues) {
                final HousePart p = Scene.getInstance().getPart(i);
                p.setEditPointsVisible(true);
                p.draw();
            }
        });
        gui.add(typeComboBox, BorderLayout.NORTH);
        gui.add(new JScrollPane(idList), BorderLayout.CENTER);
        if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), gui, I18n.get("dialog.select_group"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
            return null;
        }
        final List<Long> selectedIds = idList.getSelectedValuesList();
        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.must_select_group"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new PartGroup((String) typeComboBox.getSelectedItem(), selectedIds);
    }

}