package org.concord.energy3d.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Mirror;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.HeliostatAnnualAnalysis;
import org.concord.energy3d.simulation.HeliostatDailyAnalysis;
import org.concord.energy3d.undo.ChangeAzimuthCommand;
import org.concord.energy3d.undo.ChangeAzimuthForAllHeliostatsCommand;
import org.concord.energy3d.undo.ChangeFoundationHeliostatAzimuthCommand;
import org.concord.energy3d.undo.ChangeFoundationHeliostatTargetCommand;
import org.concord.energy3d.undo.ChangeFoundationHeliostatTiltAngleCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarCollectorPoleHeightCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarReflectorOpticalEfficiencyCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarReflectorReflectanceCommand;
import org.concord.energy3d.undo.ChangeHeliostatTargetCommand;
import org.concord.energy3d.undo.ChangeHeliostatTextureCommand;
import org.concord.energy3d.undo.ChangeOpticalEfficiencyForAllSolarReflectorsCommand;
import org.concord.energy3d.undo.ChangePoleHeightCommand;
import org.concord.energy3d.undo.ChangePoleHeightForAllSolarCollectorsCommand;
import org.concord.energy3d.undo.ChangeReflectanceForAllSolarReflectorsCommand;
import org.concord.energy3d.undo.ChangeSolarReceiverEfficiencyCommand;
import org.concord.energy3d.undo.ChangeSolarReflectorOpticalEfficiencyCommand;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.undo.ChangeSolarReflectorReflectanceCommand;
import org.concord.energy3d.undo.ChangeTargetForAllHeliostatsCommand;
import org.concord.energy3d.undo.ChangeTiltAngleCommand;
import org.concord.energy3d.undo.ChangeTiltAngleForAllHeliostatsCommand;
import org.concord.energy3d.undo.LockEditPointsCommand;
import org.concord.energy3d.undo.LockEditPointsForClassCommand;
import org.concord.energy3d.undo.LockEditPointsOnFoundationCommand;
import org.concord.energy3d.undo.SetHeliostatLabelCommand;
import org.concord.energy3d.undo.SetPartSizeCommand;
import org.concord.energy3d.undo.SetSizeForAllHeliostatsCommand;
import org.concord.energy3d.undo.SetSizeForHeliostatsOnFoundationCommand;
import org.concord.energy3d.undo.ShowSunBeamCommand;
import org.concord.energy3d.util.Util;

class PopupMenuForHeliostat extends PopupMenuFactory {

    private static JPopupMenu popupMenuForHeliostat;

    static JPopupMenu getPopupMenu() {

        if (popupMenuForHeliostat == null) {

            final JCheckBoxMenuItem cbmiDisableEditPoint = new JCheckBoxMenuItem(I18n.get("menu.disable_edit_points"));
            cbmiDisableEditPoint.addItemListener(new ItemListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void itemStateChanged(final ItemEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final boolean disabled = cbmiDisableEditPoint.isSelected();
                    final Mirror m = (Mirror) selectedPart;
                    final String partInfo = m.toString().substring(0, m.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(0, 20));
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.SOUTH);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }

                    final String title = "<html>" + I18n.get(disabled ? "title.disable_edit_point" : "title.enable_edit_point", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.disable_edit_point_heliostat") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[0]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get(disabled ? "dialog.disable_edit_point" : "dialog.enable_edit_point"));
                    dialog.setVisible(true);
                    if (optionPane.getValue() == options[0]) {
                        if (rb1.isSelected()) {
                            final LockEditPointsCommand c = new LockEditPointsCommand(m);
                            m.setLockEdit(disabled);
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 0;
                        } else if (rb2.isSelected()) {
                            final Foundation foundation = m.getTopContainer();
                            final LockEditPointsOnFoundationCommand c = new LockEditPointsOnFoundationCommand(foundation, m.getClass());
                            foundation.setLockEditForClass(disabled, m.getClass());
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 1;
                        } else if (rb3.isSelected()) {
                            final LockEditPointsForClassCommand c = new LockEditPointsForClassCommand(m);
                            Scene.getInstance().setLockEditForClass(disabled, m.getClass());
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 2;
                        }
                        SceneManager.getInstance().refresh();
                        Scene.getInstance().setEdited(true);
                    }
                }

            });

            final JCheckBoxMenuItem cbmiDrawSunBeam = new JCheckBoxMenuItem(I18n.get("menu.draw_sun_beam"));
            cbmiDrawSunBeam.addItemListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Mirror)) {
                    return;
                }
                final Mirror m = (Mirror) selectedPart;
                final ShowSunBeamCommand c = new ShowSunBeamCommand(m);
                m.setSunBeamVisible(cbmiDrawSunBeam.isSelected());
                SceneManager.getTaskManager().update(() -> {
                    m.draw();
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
                Scene.getInstance().setEdited(true);
            });

            final JMenuItem miSetHeliostat = new JMenuItem(I18n.get("menu.set_target_tower"));
            miSetHeliostat.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final Mirror m = (Mirror) selectedPart;
                    final String partInfo = m.toString().substring(0, m.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(0, 20));
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.SOUTH);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }

                    final List<Foundation> foundations = Scene.getInstance().getAllFoundations();
                    final JComboBox<String> comboBox = new JComboBox<String>();
                    comboBox.addItemListener(event -> {
                        // TODO
                    });
                    comboBox.addItem(I18n.get("label.none"));
                    for (final Foundation x : foundations) {
                        if (!x.getChildren().isEmpty()) {
                            comboBox.addItem(x.getId() + "");
                        }
                    }
                    if (m.getReceiver() != null) {
                        comboBox.setSelectedItem(m.getReceiver().getId() + "");
                    }
                    gui.add(comboBox, BorderLayout.CENTER);

                    final String title = "<html>" + I18n.get("title.select_target_tower_id", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.heliostat_target") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.heliostat_target"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            Foundation target = null;
                            if (comboBox.getSelectedIndex() > 0) {
                                boolean ok = true;
                                int id = -1;
                                try {
                                    id = Integer.parseInt((String) comboBox.getSelectedItem());
                                } catch (final NumberFormatException exception) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", comboBox.getSelectedItem()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    final HousePart p = Scene.getInstance().getPart(id);
                                    if (p instanceof Foundation) {
                                        target = (Foundation) p;
                                    } else {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.id_must_be_foundation"), I18n.get("msg.id_error"), JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                            boolean changed = target != m.getReceiver();
                            final Foundation target2 = target;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final Foundation oldTarget = m.getReceiver();
                                    final ChangeHeliostatTargetCommand c = new ChangeHeliostatTargetCommand(m);
                                    m.setReceiver(target);
                                    SceneManager.getTaskManager().update(() -> {
                                        m.draw();
                                        if (oldTarget != null) {
                                            oldTarget.drawSolarReceiver();
                                        }
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = m.getTopContainer();
                                if (!changed) {
                                    for (final Mirror x : foundation.getHeliostats()) {
                                        if (target != x.getReceiver()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeFoundationHeliostatTargetCommand c = new ChangeFoundationHeliostatTargetCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setTargetForHeliostats(target2);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                        if (target != x.getReceiver()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeTargetForAllHeliostatsCommand c = new ChangeTargetForAllHeliostatsCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setTargetForAllHeliostats(target2);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 2;
                            }
                            if (changed) {
                                if (target2 != null) {
                                    SceneManager.getTaskManager().update(() -> {
                                        target2.drawSolarReceiver();
                                        return null;
                                    });
                                }
                                updateAfterEdit();
                            }
                            if (choice == options[0]) {
                                break;
                            }
                        }
                    }
                }
            });

            final JMenuItem miZenith = new JMenuItem(I18n.get("menu.tilt_angle"));
            miZenith.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final Mirror m = (Mirror) selectedPart;
                    final String partInfo = m.toString().substring(0, m.toString().indexOf(')') + 1);
                    final String title = "<html>" + I18n.get("title.tilt_angle_of", partInfo) + " (&deg;)</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.heliostat_tilt_angle") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(m.getTiltAngle()));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.heliostat_mirror_tilt_angle"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < -90 || val > 90) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_tilt_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    if (Util.isZero(val - 90)) {
                                        val = 89.999;
                                    } else if (Util.isZero(val + 90)) {
                                        val = -89.999;
                                    }
                                    boolean changed = Math.abs(val - m.getTiltAngle()) > 0.000001;
                                    final double tiltAngle = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeTiltAngleCommand c = new ChangeTiltAngleCommand(m);
                                            SceneManager.getTaskManager().update(() -> {
                                                m.setTiltAngle(tiltAngle);
                                                m.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = m.getTopContainer();
                                        if (!changed) {
                                            for (final Mirror x : foundation.getHeliostats()) {
                                                if (Math.abs(val - x.getTiltAngle()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationHeliostatTiltAngleCommand c = new ChangeFoundationHeliostatTiltAngleCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setTiltAngleForHeliostats(tiltAngle);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                                if (Math.abs(val - x.getTiltAngle()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeTiltAngleForAllHeliostatsCommand c = new ChangeTiltAngleForAllHeliostatsCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setTiltAngleForAllHeliostats(tiltAngle);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 2;
                                    }
                                    if (changed) {
                                        updateAfterEdit();
                                    }
                                    if (choice == options[0]) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            final JMenuItem miAzimuth = new JMenuItem(I18n.get("menu.azimuth"));
            miAzimuth.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final Mirror mirror = (Mirror) selectedPart;
                    final Foundation foundation = mirror.getTopContainer();
                    final String title = "<html>" + I18n.get("title.azimuth_angle_of", partInfo) + " (&deg;)</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.azimuth_angle") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    double a = mirror.getRelativeAzimuth() + foundation.getAzimuth();
                    if (a > 360) {
                        a -= 360;
                    }
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(a));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.heliostat_azimuth"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                a = val - foundation.getAzimuth();
                                if (a < 0) {
                                    a += 360;
                                }
                                boolean changed = Math.abs(a - mirror.getRelativeAzimuth()) > 0.000001;
                                final double azimuth = a;
                                if (rb1.isSelected()) {
                                    if (changed) {
                                        final ChangeAzimuthCommand c = new ChangeAzimuthCommand(mirror);
                                        SceneManager.getTaskManager().update(() -> {
                                            mirror.setRelativeAzimuth(azimuth);
                                            mirror.draw();
                                            SceneManager.getInstance().refresh();
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(c);
                                    }
                                    selectedScopeIndex = 0;
                                } else if (rb2.isSelected()) {
                                    if (!changed) {
                                        for (final Mirror x : foundation.getHeliostats()) {
                                            if (Math.abs(a - x.getRelativeAzimuth()) > 0.000001) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (changed) {
                                        final ChangeFoundationHeliostatAzimuthCommand c = new ChangeFoundationHeliostatAzimuthCommand(foundation);
                                        SceneManager.getTaskManager().update(() -> {
                                            foundation.setAzimuthForHeliostats(azimuth);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(c);
                                    }
                                    selectedScopeIndex = 1;
                                } else if (rb3.isSelected()) {
                                    if (!changed) {
                                        for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                            if (Math.abs(a - x.getRelativeAzimuth()) > 0.000001) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (changed) {
                                        final ChangeAzimuthForAllHeliostatsCommand c = new ChangeAzimuthForAllHeliostatsCommand();
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setAzimuthForAllHeliostats(azimuth);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(c);
                                    }
                                    selectedScopeIndex = 2;
                                }
                                if (changed) {
                                    updateAfterEdit();
                                }
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            final JMenuItem miSize = new JMenuItem(I18n.get("menu.size"));
            miSize.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final Mirror m = (Mirror) selectedPart;
                    final Foundation foundation = m.getTopContainer();
                    final String partInfo = m.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.add(new JLabel(I18n.get("label.width")));
                    final JTextField widthField = new JTextField(threeDecimalsFormat.format(m.getApertureWidth()));
                    inputPanel.add(widthField);
                    inputPanel.add(new JLabel(I18n.get("label.length")));
                    final JTextField heightField = new JTextField(threeDecimalsFormat.format(m.getApertureHeight()));
                    inputPanel.add(heightField);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    scopePanel.add(rb1);
                    scopePanel.add(rb2);
                    scopePanel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    gui.add(scopePanel, BorderLayout.NORTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_size_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.heliostat_size"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double w = 0, h = 0;
                            boolean ok = true;
                            try {
                                w = Double.parseDouble(widthField.getText());
                                h = Double.parseDouble(heightField.getText());
                            } catch (final NumberFormatException x) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (w < 1 || w > 50) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_width_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else if (h < 1 || h > 50) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(w - m.getApertureWidth()) > 0.000001 || Math.abs(h - m.getApertureHeight()) > 0.000001;
                                    final double w2 = w;
                                    final double h2 = h;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetPartSizeCommand c = new SetPartSizeCommand(m);
                                            SceneManager.getTaskManager().update(() -> {
                                                m.setApertureWidth(w2);
                                                m.seApertureHeight(h2);
                                                m.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : foundation.getHeliostats()) {
                                                if (Math.abs(w - x.getApertureWidth()) > 0.000001 || Math.abs(h - x.getApertureHeight()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSizeForHeliostatsOnFoundationCommand c = new SetSizeForHeliostatsOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setSizeForHeliostats(w2, h2);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                                if (Math.abs(w - x.getApertureWidth()) > 0.000001 || Math.abs(h - x.getApertureHeight()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSizeForAllHeliostatsCommand c = new SetSizeForAllHeliostatsCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setSizeForAllHeliostats(w2, h2);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 2;
                                    }
                                    if (changed) {
                                        updateAfterEdit();
                                    }
                                    if (choice == options[0]) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            final JMenuItem miPoleHeight = new JMenuItem(I18n.get("menu.pole_height"));
            miPoleHeight.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final Mirror m = (Mirror) selectedPart;
                    final Foundation foundation = m.getTopContainer();
                    final String title = "<html>" + I18n.get("title.pole_height_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(m.getPoleHeight() * Scene.getInstance().getScale()));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.heliostat_pole_height"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText()) / Scene.getInstance().getScale();
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < 0 || val * Scene.getInstance().getScale() > 10) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val - m.getPoleHeight()) > 0.000001;
                                    final double poleHeight = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangePoleHeightCommand c = new ChangePoleHeightCommand(m);
                                            SceneManager.getTaskManager().update(() -> {
                                                m.setPoleHeight(poleHeight);
                                                m.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : foundation.getHeliostats()) {
                                                if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarCollectorPoleHeightCommand c = new ChangeFoundationSolarCollectorPoleHeightCommand(foundation, m.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setPoleHeightForHeliostats(poleHeight);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                                if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleHeightForAllSolarCollectorsCommand c = new ChangePoleHeightForAllSolarCollectorsCommand(m.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setPoleHeightForAllHeliostats(poleHeight);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 2;
                                    }
                                    if (changed) {
                                        updateAfterEdit();
                                    }
                                    if (choice == options[0]) {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                }
            });

            final JMenu labelMenu = new JMenu(I18n.get("menu.label"));

            final JCheckBoxMenuItem miLabelNone = new JCheckBoxMenuItem(I18n.get("label.none"), true);
            miLabelNone.addActionListener(e -> {
                if (miLabelNone.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Mirror) {
                        final Mirror m = (Mirror) selectedPart;
                        final SetHeliostatLabelCommand c = new SetHeliostatLabelCommand(m);
                        m.clearLabels();
                        SceneManager.getTaskManager().update(() -> {
                            m.draw();
                            SceneManager.getInstance().refresh();
                            return null;
                        });
                        SceneManager.getInstance().getUndoManager().addEdit(c);
                        Scene.getInstance().setEdited(true);
                    }
                }
            });
            labelMenu.add(miLabelNone);

            final JCheckBoxMenuItem miLabelCustom = new JCheckBoxMenuItem(I18n.get("label.custom"));
            miLabelCustom.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Mirror) {
                    final Mirror m = (Mirror) selectedPart;
                    final SetHeliostatLabelCommand c = new SetHeliostatLabelCommand(m);
                    m.setLabelCustom(miLabelCustom.isSelected());
                    if (m.getLabelCustom()) {
                        m.setLabelCustomText(JOptionPane.showInputDialog(MainFrame.getInstance(), I18n.get("dialog.custom_text"), m.getLabelCustomText()));
                    }
                    SceneManager.getTaskManager().update(() -> {
                        m.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelCustom);

            final JCheckBoxMenuItem miLabelId = new JCheckBoxMenuItem(I18n.get("label.id"));
            miLabelId.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Mirror) {
                    final Mirror m = (Mirror) selectedPart;
                    final SetHeliostatLabelCommand c = new SetHeliostatLabelCommand(m);
                    m.setLabelId(miLabelId.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        m.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelId);

            final JCheckBoxMenuItem miLabelEnergyOutput = new JCheckBoxMenuItem(I18n.get("label.energy_output"));
            miLabelEnergyOutput.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Mirror) {
                    final Mirror m = (Mirror) selectedPart;
                    final SetHeliostatLabelCommand c = new SetHeliostatLabelCommand(m);
                    m.setLabelEnergyOutput(miLabelEnergyOutput.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        m.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelEnergyOutput);

            final JMenu textureMenu = new JMenu(I18n.get("menu.texture"));

            final ButtonGroup textureButtonGroup = new ButtonGroup();

            final JRadioButtonMenuItem texture1MenuItem = new JRadioButtonMenuItem(I18n.get("texture.whole_mirror"));
            texture1MenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeHeliostatTextureCommand c = new ChangeHeliostatTextureCommand();
                    Scene.getInstance().setHeliostatTextureType(Mirror.TEXTURE_ONE_MIRROR);
                    Scene.getInstance().setEdited(true);
                    if (MainPanel.getInstance().getEnergyButton().isSelected()) {
                        MainPanel.getInstance().getEnergyButton().setSelected(false);
                    }
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().redrawAll();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            textureButtonGroup.add(texture1MenuItem);
            textureMenu.add(texture1MenuItem);

            final JRadioButtonMenuItem texture2MenuItem = new JRadioButtonMenuItem(I18n.get("texture.2x1_mirrors"));
            texture2MenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeHeliostatTextureCommand c = new ChangeHeliostatTextureCommand();
                    Scene.getInstance().setHeliostatTextureType(Mirror.TEXTURE_2X1_MIRRORS);
                    Scene.getInstance().setEdited(true);
                    if (MainPanel.getInstance().getEnergyButton().isSelected()) {
                        MainPanel.getInstance().getEnergyButton().setSelected(false);
                    }
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().redrawAll();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            textureButtonGroup.add(texture2MenuItem);
            textureMenu.add(texture2MenuItem);

            final JRadioButtonMenuItem texture3MenuItem = new JRadioButtonMenuItem(I18n.get("texture.1x2_mirrors"));
            texture3MenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeHeliostatTextureCommand c = new ChangeHeliostatTextureCommand();
                    Scene.getInstance().setHeliostatTextureType(Mirror.TEXTURE_1X2_MIRRORS);
                    Scene.getInstance().setEdited(true);
                    if (MainPanel.getInstance().getEnergyButton().isSelected()) {
                        MainPanel.getInstance().getEnergyButton().setSelected(false);
                    }
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().redrawAll();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            textureButtonGroup.add(texture3MenuItem);
            textureMenu.add(texture3MenuItem);

            final JRadioButtonMenuItem texture4MenuItem = new JRadioButtonMenuItem(I18n.get("texture.7x5_mirrors"));
            texture4MenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeHeliostatTextureCommand c = new ChangeHeliostatTextureCommand();
                    Scene.getInstance().setHeliostatTextureType(Mirror.TEXTURE_7X5_MIRRORS);
                    Scene.getInstance().setEdited(true);
                    if (MainPanel.getInstance().getEnergyButton().isSelected()) {
                        MainPanel.getInstance().getEnergyButton().setSelected(false);
                    }
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().redrawAll();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            textureButtonGroup.add(texture4MenuItem);
            textureMenu.add(texture4MenuItem);

            textureMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    textureButtonGroup.clearSelection();
                    switch (Scene.getInstance().getHeliostatTextureType()) {
                        default:
                            Util.selectSilently(texture1MenuItem, true);
                            break;
                        case Mirror.TEXTURE_2X1_MIRRORS:
                            Util.selectSilently(texture2MenuItem, true);
                            break;
                        case Mirror.TEXTURE_1X2_MIRRORS:
                            Util.selectSilently(texture3MenuItem, true);
                            break;
                        case Mirror.TEXTURE_7X5_MIRRORS:
                            Util.selectSilently(texture4MenuItem, true);
                            break;
                    }
                }
            });

            popupMenuForHeliostat = createPopupMenu(true, true, () -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Mirror)) {
                    return;
                }
                final Mirror m = (Mirror) selectedPart;
                if (m.getReceiver() == null) {
                    miZenith.setEnabled(true);
                    miAzimuth.setEnabled(true);
                } else {
                    miZenith.setEnabled(false);
                    miAzimuth.setEnabled(false);
                }
                Util.selectSilently(cbmiDisableEditPoint, m.getLockEdit());
                Util.selectSilently(cbmiDrawSunBeam, m.isSunBeamVisible());
                Util.selectSilently(miLabelNone, !m.isLabelVisible());
                Util.selectSilently(miLabelCustom, m.getLabelCustom());
                Util.selectSilently(miLabelId, m.getLabelId());
                Util.selectSilently(miLabelEnergyOutput, m.getLabelEnergyOutput());
            });

            final JMenuItem miReflectance = new JMenuItem(I18n.get("menu.reflectance"));
            miReflectance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final Mirror m = (Mirror) selectedPart;
                    final String title = "<html>" + I18n.get("title.reflectance_percent_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.reflectance") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(m.getReflectance() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.heliostat_mirror_reflectance"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < 50 || val > 99) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_reflectance_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - m.getReflectance()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarReflectorReflectanceCommand c = new ChangeSolarReflectorReflectanceCommand(m);
                                            m.setReflectance(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = m.getTopContainer();
                                        if (!changed) {
                                            for (final Mirror x : foundation.getHeliostats()) {
                                                if (Math.abs(val * 0.01 - x.getReflectance()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarReflectorReflectanceCommand c = new ChangeFoundationSolarReflectorReflectanceCommand(foundation, m.getClass());
                                            foundation.setReflectanceForHeliostatMirrors(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                                if (Math.abs(val * 0.01 - x.getReflectance()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeReflectanceForAllSolarReflectorsCommand c = new ChangeReflectanceForAllSolarReflectorsCommand(m.getClass());
                                            Scene.getInstance().setReflectanceForAllSolarReflectors(val * 0.01, m.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 2;
                                    }
                                    if (changed) {
                                        updateAfterEdit();
                                    }
                                    if (choice == options[0]) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            final JMenuItem miApertureRatio = new JMenuItem(I18n.get("menu.aperture_ratio"));
            miApertureRatio.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Mirror)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final Mirror m = (Mirror) selectedPart;
                    final String title = "<html>" + I18n.get("title.aperture_percentage_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.aperture_percentage") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_heliostat"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_heliostats_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_heliostats"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(m.getOpticalEfficiency() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.aperture_percentage_heliostat"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < 70 || val > 100) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_aperture_percentage_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - m.getOpticalEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarReflectorOpticalEfficiencyCommand c = new ChangeSolarReflectorOpticalEfficiencyCommand(m);
                                            m.setOpticalEfficiency(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = m.getTopContainer();
                                        if (!changed) {
                                            for (final Mirror x : foundation.getHeliostats()) {
                                                if (Math.abs(val * 0.01 - x.getOpticalEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarReflectorOpticalEfficiencyCommand c = new ChangeFoundationSolarReflectorOpticalEfficiencyCommand(foundation, m.getClass());
                                            foundation.setOpticalEfficiencyForSolarReflectors(val * 0.01, m.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Mirror x : Scene.getInstance().getAllHeliostats()) {
                                                if (Math.abs(val * 0.01 - x.getOpticalEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeOpticalEfficiencyForAllSolarReflectorsCommand c = new ChangeOpticalEfficiencyForAllSolarReflectorsCommand(m.getClass());
                                            Scene.getInstance().setOpticalEfficiencyForAllSolarReflectors(val * 0.01, m.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 2;
                                    }
                                    if (changed) {
                                        updateAfterEdit();
                                    }
                                    if (choice == options[0]) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            final JMenuItem miConversionEfficiency = new JMenuItem(I18n.get("menu.central_receiver_conversion_efficiency"));
            miConversionEfficiency.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Mirror)) {
                    return;
                }
                final Mirror m = (Mirror) selectedPart;
                final Foundation receiver = m.getReceiver();
                if (receiver == null) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_no_receiver"), I18n.get("msg.no_receiver"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                final String title = "<html>" + I18n.get("title.receiver_conversion_efficiency_percent", partInfo) + "</html>";
                final String footnote = "<html><hr><font size=2><hr></html>";
                final JPanel gui = new JPanel(new BorderLayout());
                final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(receiver.getSolarReceiverEfficiency() * 100));
                gui.add(inputField, BorderLayout.SOUTH);

                final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.receiver_conversion_efficiency"));

                while (true) {
                    inputField.selectAll();
                    inputField.requestFocusInWindow();
                    dialog.setVisible(true);
                    final Object choice = optionPane.getValue();
                    if (choice == options[1] || choice == null) {
                        break;
                    } else {
                        double val = 0;
                        boolean ok = true;
                        try {
                            val = Double.parseDouble(inputField.getText());
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            ok = false;
                        }
                        if (ok) {
                            if (val < 5 || val > 50) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.receiver_conversion_efficiency_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                final boolean changed = Math.abs(val * 0.01 - receiver.getSolarReceiverEfficiency()) > 0.000001;
                                if (changed) {
                                    final ChangeSolarReceiverEfficiencyCommand c = new ChangeSolarReceiverEfficiencyCommand(receiver);
                                    receiver.setSolarReceiverEfficiency(val * 0.01);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                    updateAfterEdit();
                                }
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }
                    }
                }
            });

            popupMenuForHeliostat.addSeparator();
            popupMenuForHeliostat.add(miSetHeliostat);
            popupMenuForHeliostat.add(miZenith);
            popupMenuForHeliostat.add(miAzimuth);
            popupMenuForHeliostat.add(miSize);
            popupMenuForHeliostat.add(miPoleHeight);
            popupMenuForHeliostat.addSeparator();
            popupMenuForHeliostat.add(miReflectance);
            popupMenuForHeliostat.add(miApertureRatio);
            popupMenuForHeliostat.add(miConversionEfficiency);
            popupMenuForHeliostat.addSeparator();
            popupMenuForHeliostat.add(cbmiDisableEditPoint);
            popupMenuForHeliostat.add(cbmiDrawSunBeam);
            popupMenuForHeliostat.add(labelMenu);
            popupMenuForHeliostat.add(textureMenu);
            popupMenuForHeliostat.addSeparator();

            JMenuItem mi = new JMenuItem(I18n.get("menu.daily_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof Mirror) {
                    new HeliostatDailyAnalysis().show();
                }
            });
            popupMenuForHeliostat.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof Mirror) {
                    new HeliostatAnnualAnalysis().show();
                }
            });
            popupMenuForHeliostat.add(mi);

        }

        return popupMenuForHeliostat;

    }

}