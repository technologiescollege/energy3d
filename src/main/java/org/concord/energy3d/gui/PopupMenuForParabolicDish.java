package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import javax.swing.JTextField;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.ParabolicDish;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.ParabolicDishAnnualAnalysis;
import org.concord.energy3d.simulation.ParabolicDishDailyAnalysis;
import org.concord.energy3d.undo.ChangeAbsorptanceForAllSolarReflectorsCommand;
import org.concord.energy3d.undo.ChangeFoundationParabolicDishStructureTypeCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarCollectorPoleHeightCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarReflectorAbsorptanceCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarReflectorOpticalEfficiencyCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarReflectorReflectanceCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarReflectorThermalEfficiencyCommand;
import org.concord.energy3d.undo.ChangeOpticalEfficiencyForAllSolarReflectorsCommand;
import org.concord.energy3d.undo.ChangePoleHeightCommand;
import org.concord.energy3d.undo.ChangePoleHeightForAllSolarCollectorsCommand;
import org.concord.energy3d.undo.ChangeReflectanceForAllSolarReflectorsCommand;
import org.concord.energy3d.undo.ChangeSolarReflectorAbsorptanceCommand;
import org.concord.energy3d.undo.ChangeSolarReflectorOpticalEfficiencyCommand;
import org.concord.energy3d.undo.ChangeSolarReflectorReflectanceCommand;
import org.concord.energy3d.undo.ChangeSolarReflectorThermalEfficiencyCommand;
import org.concord.energy3d.undo.ChangeStructureTypeForAllParabolicDishesCommand;
import org.concord.energy3d.undo.ChangeThermalEfficiencyForAllSolarReflectorsCommand;
import org.concord.energy3d.undo.LockEditPointsCommand;
import org.concord.energy3d.undo.LockEditPointsForClassCommand;
import org.concord.energy3d.undo.LockEditPointsOnFoundationCommand;
import org.concord.energy3d.undo.SetFocalLengthForAllParabolicDishesCommand;
import org.concord.energy3d.undo.SetFocalLengthForParabolicDishesOnFoundationCommand;
import org.concord.energy3d.undo.SetParabolicDishFocalLengthCommand;
import org.concord.energy3d.undo.SetParabolicDishLabelCommand;
import org.concord.energy3d.undo.SetParabolicDishRibsCommand;
import org.concord.energy3d.undo.SetParabolicDishStructureTypeCommand;
import org.concord.energy3d.undo.SetPartSizeCommand;
import org.concord.energy3d.undo.SetRibsForAllParabolicDishesCommand;
import org.concord.energy3d.undo.SetRibsForParabolicDishesOnFoundationCommand;
import org.concord.energy3d.undo.SetRimRadiusForAllParabolicDishesCommand;
import org.concord.energy3d.undo.SetRimRadiusForParabolicDishesOnFoundationCommand;
import org.concord.energy3d.undo.ShowSunBeamCommand;
import org.concord.energy3d.util.Util;
import org.concord.energy3d.util.I18n;

class PopupMenuForParabolicDish extends PopupMenuFactory {

    private static JPopupMenu popupMenuForParabolicDish;

    static JPopupMenu getPopupMenu() {

        if (popupMenuForParabolicDish == null) {

            final JMenuItem miMesh = new JMenuItem(I18n.get("menu.mesh"));
            miMesh.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final Foundation foundation = d.getTopContainer();
                    final String partInfo = d.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.add(new JLabel(I18n.get("label.radial_direction")));
                    final JTextField nRadialField = new JTextField("" + d.getNRadialSections());
                    inputPanel.add(nRadialField);
                    inputPanel.add(new JLabel(I18n.get("label.axial_direction")));
                    final JTextField nAxialField = new JTextField("" + d.getNAxialSections());
                    inputPanel.add(nAxialField);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_mesh_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_mesh"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            int nRadialSections = 0, nAxialSections = 0;
                            boolean ok = true;
                            try {
                                nRadialSections = Integer.parseInt(nRadialField.getText());
                                nAxialSections = Integer.parseInt(nAxialField.getText());
                            } catch (final NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (nRadialSections < 4) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_sections_min"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else if (nAxialSections < 4) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.axial_sections_mesh_min"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else if (!Util.isPowerOfTwo(nRadialSections) || !Util.isPowerOfTwo(nAxialSections)) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.parabolic_dish_mesh_power_of_two"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    final int nRadialSections2 = nRadialSections;
                                    final int nAxialSections2 = nAxialSections;
                                    if (rb1.isSelected()) {
                                        // final SetPartSizeCommand c = new SetPartSizeCommand(t);
                                        SceneManager.getTaskManager().update(() -> {
                                            d.setNRadialSections(nRadialSections2);
                                            d.setNAxialSections(nAxialSections2);
                                            d.draw();
                                            SceneManager.getInstance().refresh();
                                            return null;
                                        });
                                        // SceneManager.getInstance().getUndoManager().addEdit(c);
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        // final SetShapeForParabolicTroughsOnFoundationCommand c = new SetShapeForParabolicTroughsOnFoundationCommand(foundation);
                                        SceneManager.getTaskManager().update(() -> {
                                            foundation.setSectionsForParabolicDishes(nRadialSections2, nAxialSections2);
                                            return null;
                                        });
                                        // SceneManager.getInstance().getUndoManager().addEdit(c);
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        // final SetShapeForAllParabolicTroughsCommand c = new SetShapeForAllParabolicTroughsCommand();
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setSectionsForAllParabolicDishes(nRadialSections2, nAxialSections2);
                                            return null;
                                        });
                                        // SceneManager.getInstance().getUndoManager().addEdit(c);
                                        selectedScopeIndex = 2;
                                    }
                                    updateAfterEdit();
                                    if (choice == options[0]) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            });

            final JMenuItem miRib = new JMenuItem(I18n.get("menu.ribs"));
            miRib.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final Foundation foundation = d.getTopContainer();
                    final String partInfo = d.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.add(new JLabel(I18n.get("label.rib_lines")));
                    final JTextField nribField = new JTextField("" + d.getNumberOfRibs());
                    inputPanel.add(nribField);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_rib_lines_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_ribs"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            int nrib = 0;
                            boolean ok = true;
                            try {
                                nrib = Integer.parseInt(nribField.getText());
                            } catch (final NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (nrib < 0) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.ribs_cannot_be_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = nrib != d.getNumberOfRibs();
                                    final int nrib2 = nrib;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetParabolicDishRibsCommand c = new SetParabolicDishRibsCommand(d);
                                            SceneManager.getTaskManager().update(() -> {
                                                d.setNumberOfRibs(nrib2);
                                                d.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (x.getNumberOfRibs() != nrib) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetRibsForParabolicDishesOnFoundationCommand c = new SetRibsForParabolicDishesOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setNumberOfRibsForParabolicDishes(nrib2);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (x.getNumberOfRibs() != nrib) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetRibsForAllParabolicDishesCommand c = new SetRibsForAllParabolicDishesCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setNumberOfRibsForAllParabolicDishes(nrib2);
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

            final JCheckBoxMenuItem cbmiDisableEditPoint = new JCheckBoxMenuItem(I18n.get("menu.disable_edit_points"));
            cbmiDisableEditPoint.addItemListener(new ItemListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void itemStateChanged(final ItemEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final boolean disabled = cbmiDisableEditPoint.isSelected();
                    final ParabolicDish dish = (ParabolicDish) selectedPart;
                    final String partInfo = dish.toString().substring(0, dish.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(0, 20));
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.SOUTH);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.disable_edit_point_parabolic_dish") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[0]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get(disabled ? "dialog.disable_edit_point" : "dialog.enable_edit_point"));
                    dialog.setVisible(true);
                    if (optionPane.getValue() == options[0]) {
                        if (rb1.isSelected()) {
                            final LockEditPointsCommand c = new LockEditPointsCommand(dish);
                            dish.setLockEdit(disabled);
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 0;
                        } else if (rb2.isSelected()) {
                            final Foundation foundation = dish.getTopContainer();
                            final LockEditPointsOnFoundationCommand c = new LockEditPointsOnFoundationCommand(foundation, dish.getClass());
                            foundation.setLockEditForClass(disabled, dish.getClass());
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 1;
                        } else if (rb3.isSelected()) {
                            final LockEditPointsForClassCommand c = new LockEditPointsForClassCommand(dish);
                            Scene.getInstance().setLockEditForClass(disabled, dish.getClass());
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 2;
                        }
                        SceneManager.getInstance().refresh();
                        Scene.getInstance().setEdited(true);
                    }
                }

            });

            final JCheckBoxMenuItem cbmiDrawSunBeams = new JCheckBoxMenuItem(I18n.get("menu.draw_sun_beams"));
            cbmiDrawSunBeams.addItemListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof ParabolicDish)) {
                    return;
                }
                final ParabolicDish d = (ParabolicDish) selectedPart;
                final ShowSunBeamCommand c = new ShowSunBeamCommand(d);
                d.setSunBeamVisible(cbmiDrawSunBeams.isSelected());
                SceneManager.getTaskManager().update(() -> {
                    d.drawSunBeam();
                    d.draw();
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
                Scene.getInstance().setEdited(true);
            });

            final JMenuItem miRimRadius = new JMenuItem(I18n.get("menu.rim_radius"));
            miRimRadius.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final Foundation foundation = d.getTopContainer();
                    final String partInfo = d.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.add(new JLabel(I18n.get("label.rim_radius_m")));
                    final JTextField apertureRadiusField = new JTextField(threeDecimalsFormat.format(d.getRimRadius()));
                    inputPanel.add(apertureRadiusField);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_rim_radius_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_rim_radius"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double r = 0;
                            boolean ok = true;
                            try {
                                r = Double.parseDouble(apertureRadiusField.getText());
                            } catch (final NumberFormatException x) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (r < 1 || r > 10) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.parabolic_dish_rim_radius_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(r - d.getRimRadius()) > 0.000001;
                                    final double rimRadius = r;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetPartSizeCommand c = new SetPartSizeCommand(d);
                                            SceneManager.getTaskManager().update(() -> {
                                                d.setRimRadius(rimRadius);
                                                d.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(r - x.getRimRadius()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetRimRadiusForParabolicDishesOnFoundationCommand c = new SetRimRadiusForParabolicDishesOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setRimRadiusForParabolicDishes(rimRadius);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(r - x.getRimRadius()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetRimRadiusForAllParabolicDishesCommand c = new SetRimRadiusForAllParabolicDishesCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setRimRadiusForAllParabolicDishes(rimRadius);
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

            final JMenuItem miFocalLength = new JMenuItem(I18n.get("menu.focal_length"));
            miFocalLength.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final Foundation foundation = d.getTopContainer();
                    final String partInfo = d.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new GridLayout(1, 2, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.add(new JLabel(I18n.get("label.focal_length_m")));
                    final JTextField focalLengthField = new JTextField(threeDecimalsFormat.format(d.getFocalLength()));
                    inputPanel.add(focalLengthField);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_focal_length_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.focal_length"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double fl = 0;
                            boolean ok = true;
                            try {
                                fl = Double.parseDouble(focalLengthField.getText());
                            } catch (final NumberFormatException nfe) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (fl < 0.5 || fl > 10) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.focal_length_range_dish"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(fl - d.getFocalLength()) > 0.000001;
                                    final double fl2 = fl;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetParabolicDishFocalLengthCommand c = new SetParabolicDishFocalLengthCommand(d);
                                            SceneManager.getTaskManager().update(() -> {
                                                d.setFocalLength(fl2);
                                                d.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(fl - x.getFocalLength()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetFocalLengthForParabolicDishesOnFoundationCommand c = new SetFocalLengthForParabolicDishesOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setFocalLengthForParabolicDishes(fl2);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(fl - x.getFocalLength()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetFocalLengthForAllParabolicDishesCommand c = new SetFocalLengthForAllParabolicDishesCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setFocalLengthForAllParabolicDishes(fl2);
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
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final Foundation foundation = d.getTopContainer();
                    final String title = "<html>" + I18n.get("title.pole_height_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(d.getPoleHeight() * Scene.getInstance().getScale()));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_pole_height"));

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
                                    boolean changed = Math.abs(val - d.getPoleHeight()) > 0.000001;
                                    final double poleHeight = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangePoleHeightCommand c = new ChangePoleHeightCommand(d);
                                            SceneManager.getTaskManager().update(() -> {
                                                d.setPoleHeight(poleHeight);
                                                d.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarCollectorPoleHeightCommand c = new ChangeFoundationSolarCollectorPoleHeightCommand(foundation, d.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setPoleHeightForParabolicDishes(poleHeight);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleHeightForAllSolarCollectorsCommand c = new ChangePoleHeightForAllSolarCollectorsCommand(d.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setPoleHeightForAllParabolicDishes(poleHeight);
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

            final JMenuItem miStructureType = new JMenuItem(I18n.get("menu.structure_type"));
            miStructureType.addActionListener(new ActionListener() {
                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final Foundation foundation = d.getTopContainer();
                    final String title = "<html>" + I18n.get("title.structure_type_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JComboBox<String> comboBox = new JComboBox<String>(new String[]{I18n.get("structure_type.central_pole"), I18n.get("structure_type.tripod")});
                    comboBox.setSelectedIndex(d.getStructureType());
                    gui.add(comboBox, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.structure_type"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            final int structureType = comboBox.getSelectedIndex();
                            boolean changed = structureType != d.getStructureType();
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetParabolicDishStructureTypeCommand c = new SetParabolicDishStructureTypeCommand(d);
                                    SceneManager.getTaskManager().update(() -> {
                                        d.setStructureType(structureType);
                                        d.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                        if (structureType != x.getStructureType()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeFoundationParabolicDishStructureTypeCommand c = new ChangeFoundationParabolicDishStructureTypeCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setStructureTypeForParabolicDishes(structureType);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                        if (structureType != x.getStructureType()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeStructureTypeForAllParabolicDishesCommand c = new ChangeStructureTypeForAllParabolicDishesCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setStructureTypeForAllParabolicDishes(structureType);
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
            });

            final JMenu labelMenu = new JMenu(I18n.get("menu.label"));

            final JCheckBoxMenuItem miLabelNone = new JCheckBoxMenuItem(I18n.get("label.none"), true);
            miLabelNone.addActionListener(e -> {
                if (miLabelNone.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof ParabolicDish) {
                        final ParabolicDish d = (ParabolicDish) selectedPart;
                        final SetParabolicDishLabelCommand c = new SetParabolicDishLabelCommand(d);
                        d.clearLabels();
                        SceneManager.getTaskManager().update(() -> {
                            d.draw();
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
                if (selectedPart instanceof ParabolicDish) {
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final SetParabolicDishLabelCommand c = new SetParabolicDishLabelCommand(d);
                    d.setLabelCustom(miLabelCustom.isSelected());
                    if (d.getLabelCustom()) {
                        d.setLabelCustomText(JOptionPane.showInputDialog(MainFrame.getInstance(), I18n.get("dialog.custom_text"), d.getLabelCustomText()));
                    }
                    SceneManager.getTaskManager().update(() -> {
                        d.draw();
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
                if (selectedPart instanceof ParabolicDish) {
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final SetParabolicDishLabelCommand c = new SetParabolicDishLabelCommand(d);
                    d.setLabelId(miLabelId.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        d.draw();
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
                if (selectedPart instanceof ParabolicDish) {
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final SetParabolicDishLabelCommand c = new SetParabolicDishLabelCommand(d);
                    d.setLabelEnergyOutput(miLabelEnergyOutput.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        d.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelEnergyOutput);

            popupMenuForParabolicDish = createPopupMenu(true, true, () -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof ParabolicDish)) {
                    return;
                }
                final ParabolicDish d = (ParabolicDish) selectedPart;
                Util.selectSilently(miLabelNone, !d.isLabelVisible());
                Util.selectSilently(miLabelCustom, d.getLabelCustom());
                Util.selectSilently(miLabelId, d.getLabelId());
                Util.selectSilently(miLabelEnergyOutput, d.getLabelEnergyOutput());
                Util.selectSilently(cbmiDrawSunBeams, d.isSunBeamVisible());
                Util.selectSilently(cbmiDisableEditPoint, d.getLockEdit());
            });

            final JMenuItem miReflectance = new JMenuItem(I18n.get("menu.mirror_reflectance"));
            miReflectance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final String title = "<html>" + I18n.get("title.reflectance_percent_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.reflectance") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(d.getReflectance() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_mirror_reflectance"));

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
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.parabolic_dish_reflectance_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - d.getReflectance()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarReflectorReflectanceCommand c = new ChangeSolarReflectorReflectanceCommand(d);
                                            d.setReflectance(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = d.getTopContainer();
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getReflectance()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarReflectorReflectanceCommand c = new ChangeFoundationSolarReflectorReflectanceCommand(foundation, d.getClass());
                                            foundation.setReflectanceForSolarReflectors(val * 0.01, d.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getReflectance()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeReflectanceForAllSolarReflectorsCommand c = new ChangeReflectanceForAllSolarReflectorsCommand(d.getClass());
                                            Scene.getInstance().setReflectanceForAllSolarReflectors(val * 0.01, d.getClass());
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

            final JMenuItem miAbsorptance = new JMenuItem(I18n.get("menu.receiver_absorptance"));
            miAbsorptance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final String title = "<html>" + I18n.get("title.absorptance_percent_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2><hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(d.getAbsorptance() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_receiver_absorptance"));

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
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.parabolic_dish_absorptance_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - d.getAbsorptance()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarReflectorAbsorptanceCommand c = new ChangeSolarReflectorAbsorptanceCommand(d);
                                            d.setAbsorptance(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = d.getTopContainer();
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getAbsorptance()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarReflectorAbsorptanceCommand c = new ChangeFoundationSolarReflectorAbsorptanceCommand(foundation, d.getClass());
                                            foundation.setAbsorptanceForSolarReflectors(val * 0.01, d.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getAbsorptance()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeAbsorptanceForAllSolarReflectorsCommand c = new ChangeAbsorptanceForAllSolarReflectorsCommand(d.getClass());
                                            Scene.getInstance().setAbsorptanceForAllSolarReflectors(val * 0.01, d.getClass());
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

            final JMenuItem miOpticalEfficiency = new JMenuItem(I18n.get("menu.optical_efficiency"));
            miOpticalEfficiency.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final String title = "<html>" + I18n.get("title.optical_efficiency_percent_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.optical_efficiency") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(d.getOpticalEfficiency() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_optical_efficiency"));

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
                                if (val < 50 || val > 100) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.parabolic_dish_optical_efficiency_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - d.getOpticalEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarReflectorOpticalEfficiencyCommand c = new ChangeSolarReflectorOpticalEfficiencyCommand(d);
                                            d.setOpticalEfficiency(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = d.getTopContainer();
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getOpticalEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarReflectorOpticalEfficiencyCommand c = new ChangeFoundationSolarReflectorOpticalEfficiencyCommand(foundation, d.getClass());
                                            foundation.setOpticalEfficiencyForSolarReflectors(val * 0.01, d.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getOpticalEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeOpticalEfficiencyForAllSolarReflectorsCommand c = new ChangeOpticalEfficiencyForAllSolarReflectorsCommand(d.getClass());
                                            Scene.getInstance().setOpticalEfficiencyForAllSolarReflectors(val * 0.01, d.getClass());
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

            final JMenuItem miThermalEfficiency = new JMenuItem(I18n.get("menu.thermal_efficiency"));
            miThermalEfficiency.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof ParabolicDish)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    final String title = "<html>" + I18n.get("title.thermal_efficiency_percent_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2><hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_parabolic_dish"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_parabolic_dishes_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_parabolic_dishes"));
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
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(d.getThermalEfficiency() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.parabolic_dish_thermal_efficiency"));

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
                                if (val < 5 || val > 80) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.parabolic_dish_thermal_efficiency_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - d.getThermalEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarReflectorThermalEfficiencyCommand c = new ChangeSolarReflectorThermalEfficiencyCommand(d);
                                            d.setThermalEfficiency(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = d.getTopContainer();
                                        if (!changed) {
                                            for (final ParabolicDish x : foundation.getParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getThermalEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarReflectorThermalEfficiencyCommand c = new ChangeFoundationSolarReflectorThermalEfficiencyCommand(foundation, d.getClass());
                                            foundation.setThermalEfficiencyForSolarReflectors(val * 0.01, d.getClass());
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final ParabolicDish x : Scene.getInstance().getAllParabolicDishes()) {
                                                if (Math.abs(val * 0.01 - x.getThermalEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeThermalEfficiencyForAllSolarReflectorsCommand c = new ChangeThermalEfficiencyForAllSolarReflectorsCommand(d.getClass());
                                            Scene.getInstance().setThermalEfficiencyForAllSolarReflectors(val * 0.01, d.getClass());
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

            popupMenuForParabolicDish.addSeparator();
            popupMenuForParabolicDish.add(cbmiDisableEditPoint);
            popupMenuForParabolicDish.add(cbmiDrawSunBeams);
            popupMenuForParabolicDish.add(labelMenu);
            popupMenuForParabolicDish.addSeparator();
            popupMenuForParabolicDish.add(miRimRadius);
            popupMenuForParabolicDish.add(miFocalLength);
            popupMenuForParabolicDish.add(miPoleHeight);
            popupMenuForParabolicDish.add(miStructureType);
            popupMenuForParabolicDish.addSeparator();
            popupMenuForParabolicDish.add(miReflectance);
            popupMenuForParabolicDish.add(miAbsorptance);
            popupMenuForParabolicDish.add(miOpticalEfficiency);
            popupMenuForParabolicDish.add(miThermalEfficiency);
            popupMenuForParabolicDish.addSeparator();
            popupMenuForParabolicDish.add(miMesh);
            popupMenuForParabolicDish.add(miRib);
            popupMenuForParabolicDish.addSeparator();

            JMenuItem mi = new JMenuItem(I18n.get("menu.daily_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof ParabolicDish) {
                    new ParabolicDishDailyAnalysis().show();
                }
            });
            popupMenuForParabolicDish.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof ParabolicDish) {
                    new ParabolicDishAnnualAnalysis().show();
                }
            });
            popupMenuForParabolicDish.add(mi);

        }

        return popupMenuForParabolicDish;

    }

}