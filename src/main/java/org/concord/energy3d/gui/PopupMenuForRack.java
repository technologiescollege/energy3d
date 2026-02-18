package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

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
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Rack;
import org.concord.energy3d.model.Roof;
import org.concord.energy3d.model.SolarPanel;
import org.concord.energy3d.model.Trackable;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.PvAnnualAnalysis;
import org.concord.energy3d.simulation.PvDailyAnalysis;
import org.concord.energy3d.simulation.PvModuleSpecs;
import org.concord.energy3d.simulation.PvModulesData;
import org.concord.energy3d.undo.ChangeAzimuthCommand;
import org.concord.energy3d.undo.ChangeAzimuthForAllRacksCommand;
import org.concord.energy3d.undo.ChangePoleHeightCommand;
import org.concord.energy3d.undo.ChangeFoundationRackAzimuthCommand;
import org.concord.energy3d.undo.ChangeFoundationSolarCollectorPoleHeightCommand;
import org.concord.energy3d.undo.ChangePoleHeightForAllSolarCollectorsCommand;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.undo.ChangePoleSettingsForAllRacksCommand;
import org.concord.energy3d.undo.ChangePoleSettingsForRacksOnFoundationCommand;
import org.concord.energy3d.undo.ChangeRackPoleSettingsCommand;
import org.concord.energy3d.undo.ChangeSolarPanelModelForAllRacksCommand;
import org.concord.energy3d.undo.ChangeSolarPanelModelForRackCommand;
import org.concord.energy3d.undo.ChangeSolarPanelModelForRacksOnFoundationCommand;
import org.concord.energy3d.undo.ChooseSolarPanelSizeForRackCommand;
import org.concord.energy3d.undo.LockEditPointsCommand;
import org.concord.energy3d.undo.LockEditPointsForClassCommand;
import org.concord.energy3d.undo.LockEditPointsOnFoundationCommand;
import org.concord.energy3d.undo.RotateSolarPanelsForAllRacksCommand;
import org.concord.energy3d.undo.RotateSolarPanelsForRackCommand;
import org.concord.energy3d.undo.RotateSolarPanelsForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetInverterEfficiencyForAllRacksCommand;
import org.concord.energy3d.undo.SetInverterEfficiencyForRackCommand;
import org.concord.energy3d.undo.SetInverterEfficiencyForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetNoctForAllRacksCommand;
import org.concord.energy3d.undo.SetNoctForRackCommand;
import org.concord.energy3d.undo.SetNoctForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetPartSizeCommand;
import org.concord.energy3d.undo.SetRackLabelCommand;
import org.concord.energy3d.undo.SetSizeForAllRacksCommand;
import org.concord.energy3d.undo.SetSizeForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetSolarCellEfficiencyForAllRacksCommand;
import org.concord.energy3d.undo.SetSolarCellEfficiencyForRackCommand;
import org.concord.energy3d.undo.SetSolarCellEfficiencyForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetSolarPanelArrayOnRackByModelCommand;
import org.concord.energy3d.undo.SetSolarPanelArrayOnRackCustomCommand;
import org.concord.energy3d.undo.SetSolarPanelCellTypeForAllRacksCommand;
import org.concord.energy3d.undo.SetSolarPanelCellTypeForRackCommand;
import org.concord.energy3d.undo.SetSolarPanelCellTypeForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetSolarPanelColorForAllRacksCommand;
import org.concord.energy3d.undo.SetSolarPanelColorForRackCommand;
import org.concord.energy3d.undo.SetSolarPanelColorForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetSolarPanelShadeToleranceForAllRacksCommand;
import org.concord.energy3d.undo.SetSolarPanelShadeToleranceForRackCommand;
import org.concord.energy3d.undo.SetSolarPanelShadeToleranceForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetSolarPanelSizeForAllRacksCommand;
import org.concord.energy3d.undo.SetSolarPanelSizeForRacksOnFoundationCommand;
import org.concord.energy3d.undo.SetSolarTrackerCommand;
import org.concord.energy3d.undo.SetSolarTrackersForAllCommand;
import org.concord.energy3d.undo.SetSolarTrackersOnFoundationCommand;
import org.concord.energy3d.undo.SetTemperatureCoefficientPmaxForAllRacksCommand;
import org.concord.energy3d.undo.SetTemperatureCoefficientPmaxForRackCommand;
import org.concord.energy3d.undo.SetTemperatureCoefficientPmaxForRacksOnFoundationCommand;
import org.concord.energy3d.undo.ShowSunBeamCommand;
import org.concord.energy3d.util.Config;
import org.concord.energy3d.util.SpringUtilities;
import org.concord.energy3d.util.Util;

class PopupMenuForRack extends PopupMenuFactory {

    private static JPopupMenu popupMenuForRack;

    static JPopupMenu getPopupMenu() {

        if (popupMenuForRack == null) {

            final JMenu trackerMenu = new JMenu(I18n.get("menu.tracker"));

            final JMenuItem miPaste = new JMenuItem(I18n.get("menu.paste"));
            miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            miPaste.addActionListener(e -> {
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().pasteToPickedLocationOnRack();
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });

            final JMenuItem miClear = new JMenuItem(I18n.get("menu.clear"));
            miClear.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Rack)) {
                    return;
                }
                final Rack rack = (Rack) selectedPart;
                SceneManager.getTaskManager().update(() -> {
                    if (rack.isMonolithic()) {
                        rack.setMonolithic(false);
                        rack.draw();
                    } else {
                        Scene.getInstance().removeAllSolarPanels(null);
                        EventQueue.invokeLater(() -> MainPanel.getInstance().getEnergyButton().setSelected(false));
                    }
                    return null;
                });
            });

            final JMenuItem miFixedTiltAngle = new JMenuItem(I18n.get("menu.fixed_tilt_angle"));
            miFixedTiltAngle.addActionListener(e -> RackFixedTiltAngleChanger.getInstance().change());

            final JMenuItem miSeasonalTiltAngle = new JMenuItem(I18n.get("menu.seasonally_adjusted_tilt_angles"));
            miSeasonalTiltAngle.addActionListener(e -> RackSeasonalTiltAnglesChanger.getInstance().change());

            final JMenuItem miAzimuth = new JMenuItem(I18n.get("menu.azimuth"));
            miAzimuth.addActionListener(e -> RackAzimuthChanger.getInstance().change());

            final JMenuItem miRotate = new JMenuItem(I18n.get("menu.rotate_90"));
            miRotate.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final Foundation foundation = rack.getTopContainer();
                    final String partInfo = rack.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.rotate_90_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.rotate_rack"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            if (rb1.isSelected()) {
                                final ChangeAzimuthCommand c = new ChangeAzimuthCommand(rack);
                                SceneManager.getTaskManager().update(() -> {
                                    double a = rack.getRelativeAzimuth() + 90;
                                    if (a > 360) {
                                        a -= 360;
                                    }
                                    rack.setRelativeAzimuth(a);
                                    rack.draw();
                                    SceneManager.getInstance().refresh();
                                    return null;
                                });
                                SceneManager.getInstance().getUndoManager().addEdit(c);
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final ChangeFoundationRackAzimuthCommand c = new ChangeFoundationRackAzimuthCommand(foundation);
                                SceneManager.getTaskManager().update(() -> {
                                    final List<Rack> racks = foundation.getRacks();
                                    for (final Rack r : racks) {
                                        double a = r.getRelativeAzimuth() + 90;
                                        if (a > 360) {
                                            a -= 360;
                                        }
                                        r.setRelativeAzimuth(a);
                                        r.draw();
                                    }
                                    SceneManager.getInstance().refresh();
                                    return null;
                                });
                                SceneManager.getInstance().getUndoManager().addEdit(c);
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                final ChangeAzimuthForAllRacksCommand c = new ChangeAzimuthForAllRacksCommand();
                                SceneManager.getTaskManager().update(() -> {
                                    final List<Rack> racks = Scene.getInstance().getAllRacks();
                                    for (final Rack r : racks) {
                                        double a = r.getRelativeAzimuth() + 90;
                                        if (a > 360) {
                                            a -= 360;
                                        }
                                        r.setRelativeAzimuth(a);
                                        r.draw();
                                    }
                                    SceneManager.getInstance().refresh();
                                    return null;
                                });
                                SceneManager.getInstance().getUndoManager().addEdit(c);
                                selectedScopeIndex = 2;
                            }
                            updateAfterEdit();
                            if (choice == options[0]) {
                                break;
                            }
                        }
                    }
                }
            });

            final JMenuItem miRackWidth = new JMenuItem(I18n.get("menu.width"));
            miRackWidth.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final Foundation foundation = rack.getTopContainer();
                    final String partInfo = rack.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new SpringLayout());
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    final JLabel label = new JLabel(I18n.get("label.width_m"), JLabel.TRAILING);
                    inputPanel.add(label);
                    final JTextField inputField = new JTextField(threeDecimalsFormat.format(rack.getRackHeight())); // rack uses width and height, which should have been named length and width
                    label.setLabelFor(inputField);
                    inputPanel.add(inputField);
                    SpringUtilities.makeCompactGrid(inputPanel, 1, 2, 6, 6, 6, 6);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_width_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.rack_width"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException x) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < 0.5 || val > 50) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.rack_width_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = val != rack.getRackHeight();
                                    final double val2 = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetPartSizeCommand c = new SetPartSizeCommand(rack);
                                            SceneManager.getTaskManager().update(() -> {
                                                rack.setRackHeight(val2);
                                                rack.ensureFullSolarPanels(false);
                                                rack.draw();
                                                if (rack.checkContainerIntersection()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.illegal_rack_width_single"),
                                                                I18n.get("msg.illegal_size"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    SceneManager.getInstance().refresh();
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (x.getRackHeight() != val) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSizeForRacksOnFoundationCommand c = new SetSizeForRacksOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setWidthForRacks(val2);
                                                if (foundation.checkContainerIntersectionForRacks()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.illegal_rack_width_multiple"),
                                                                I18n.get("msg.illegal_size"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (x.getRackHeight() != val) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSizeForAllRacksCommand c = new SetSizeForAllRacksCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setWidthForAllRacks(val2);
                                                if (Scene.getInstance().checkContainerIntersectionForAllRacks()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.illegal_rack_width_multiple"),
                                                                I18n.get("msg.illegal_size"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
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

            final JMenuItem miRackLength = new JMenuItem(I18n.get("menu.length"));
            miRackLength.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final Foundation foundation = rack.getTopContainer();
                    final String partInfo = rack.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new SpringLayout());
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    final JLabel label = new JLabel(I18n.get("label.length_m"), JLabel.TRAILING);
                    inputPanel.add(label);
                    final JTextField inputField = new JTextField(threeDecimalsFormat.format(rack.getRackWidth())); // rack uses width and height, which should have been named length and width
                    label.setLabelFor(inputField);
                    inputPanel.add(inputField);
                    SpringUtilities.makeCompactGrid(inputPanel, 1, 2, 6, 6, 6, 6);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_length_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.rack_length"));

                    while (true) {
                        dialog.setVisible(true);
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException x) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < 1 || val > 1000) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.rack_length_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = val != rack.getRackWidth();
                                    final double val2 = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetPartSizeCommand c = new SetPartSizeCommand(rack);
                                            SceneManager.getTaskManager().update(() -> {
                                                rack.setRackWidth(val2); // width = length, height = width
                                                rack.ensureFullSolarPanels(false);
                                                rack.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (x.getRackWidth() != val) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSizeForRacksOnFoundationCommand c = new SetSizeForRacksOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setLengthForRacks(val2);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (x.getRackWidth() != val) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSizeForAllRacksCommand c = new SetSizeForAllRacksCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setLengthForAllRacks(val2);
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
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final Rack rack = (Rack) selectedPart;
                    final Foundation foundation = rack.getTopContainer();
                    final String title = "<html>Pole Height (m) of " + partInfo + "</html>";
                    final String footnote = "<html><hr><font size=2></html>";
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final JPanel gui = new JPanel(new BorderLayout());
                    gui.add(panel, BorderLayout.CENTER);
                    final JTextField inputField = new JTextField(rack.getPoleHeight() * Scene.getInstance().getScale() + "");
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.pole_height"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            double val = 0;
                            try {
                                val = Double.parseDouble(inputField.getText()) / Scene.getInstance().getScale();
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < 0 || val * Scene.getInstance().getScale() > 10) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = val != rack.getPoleHeight();
                                    final double poleHeight = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangePoleHeightCommand c = new ChangePoleHeightCommand(rack);
                                            SceneManager.getTaskManager().update(() -> {
                                                rack.setPoleHeight(poleHeight);
                                                rack.draw();
                                                if (rack.checkContainerIntersection()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.illegal_rack_pole_height_single"),
                                                                I18n.get("msg.illegal_pole_height"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    SceneManager.getInstance().refresh();
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (x.getPoleHeight() != val) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarCollectorPoleHeightCommand c = new ChangeFoundationSolarCollectorPoleHeightCommand(foundation, rack.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setPoleHeightForRacks(poleHeight);
                                                if (foundation.checkContainerIntersectionForRacks()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.illegal_rack_pole_height_multiple"),
                                                                I18n.get("msg.illegal_pole_height"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (x.getPoleHeight() != val) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleHeightForAllSolarCollectorsCommand c = new ChangePoleHeightForAllSolarCollectorsCommand(rack.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setPoleHeightForAllRacks(poleHeight);
                                                if (Scene.getInstance().checkContainerIntersectionForAllRacks()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.illegal_rack_pole_height_multiple"),
                                                                I18n.get("msg.illegal_pole_height"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
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

            final JMenuItem miPoleSpacing = new JMenuItem(I18n.get("menu.pole_settings"));
            miPoleSpacing.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final String partInfo = rack.toString().substring(0, rack.toString().indexOf(')') + 1);
                    final String title = "<html>" + I18n.get("title.pole_settings_of", partInfo) + "</html>";

                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.add(new JLabel(I18n.get("label.distance_x_m")));
                    final JTextField dxField = new JTextField(threeDecimalsFormat.format(rack.getPoleDistanceX()));
                    inputPanel.add(dxField);
                    inputPanel.add(new JLabel(I18n.get("label.distance_y_m")));
                    final JTextField dyField = new JTextField(threeDecimalsFormat.format(rack.getPoleDistanceY()));
                    inputPanel.add(dyField);
                    inputPanel.add(new JLabel(I18n.get("label.visible")));
                    final JComboBox<String> visibleComboBox = new JComboBox<String>(new String[]{I18n.get("common.yes"), I18n.get("common.no")});
                    visibleComboBox.setSelectedIndex(rack.isPoleVisible() ? 0 : 1);
                    inputPanel.add(visibleComboBox);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.pole_settings"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            double dx = 0, dy = 0;
                            try {
                                dx = Double.parseDouble(dxField.getText());
                                dy = Double.parseDouble(dyField.getText());
                            } catch (final NumberFormatException x) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (dx < 1 || dx > 50) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_distance_x_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else if (dy < 1 || dy > 50) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_distance_y_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    final boolean visible = visibleComboBox.getSelectedIndex() == 0;
                                    boolean changed = dx != rack.getPoleDistanceX() || dy != rack.getPoleDistanceY();
                                    final double dx2 = dx;
                                    final double dy2 = dy;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeRackPoleSettingsCommand c = new ChangeRackPoleSettingsCommand(rack);
                                            SceneManager.getTaskManager().update(() -> {
                                                rack.setPoleDistanceX(dx2);
                                                rack.setPoleDistanceY(dy2);
                                                rack.setPoleVisible(visible);
                                                rack.draw();
                                                SceneManager.getInstance().refresh();
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = rack.getTopContainer();
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (x.getPoleDistanceX() != dx || x.getPoleDistanceY() != dy) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleSettingsForRacksOnFoundationCommand c = new ChangePoleSettingsForRacksOnFoundationCommand(foundation);
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setPoleSpacingForRacks(dx2, dy2, visible);
                                                return null;
                                            });
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (x.getPoleDistanceX() != dx || x.getPoleDistanceY() != dy) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleSettingsForAllRacksCommand c = new ChangePoleSettingsForAllRacksCommand();
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setPoleSpacingForAllRacks(dx2, dy2, visible);
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

            final JMenuItem miSolarPanels = new JMenuItem(I18n.get("menu.select_solar_panels"));
            miSolarPanels.addActionListener(new ActionListener() {

                private Rack rack;
                private JComboBox<String> modelComboBox;
                private JComboBox<String> sizeComboBox;
                private JComboBox<String> orientationComboBox;
                private JComboBox<String> cellTypeComboBox;
                private JComboBox<String> colorOptionComboBox;
                private JComboBox<String> shadeToleranceComboBox;
                private JTextField cellEfficiencyField;
                private JTextField noctField;
                private JTextField pmaxTcField;
                private double cellEfficiency;
                private double inverterEfficiency;
                private double pmax;
                private double noct;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    rack = (Rack) selectedPart;
                    final int n = rack.getChildren().size();
                    if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                            I18n.get("msg.confirm_remove_solar_panels_for_layout", n),
                            I18n.get("dialog.confirm"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    final SolarPanel solarPanel = rack.getSolarPanel();
                    final JPanel panel = new JPanel(new SpringLayout());

                    panel.add(new JLabel(I18n.get("label.model")));
                    modelComboBox = new JComboBox<String>();
                    modelComboBox.addItem(I18n.get("model.custom"));
                    final Map<String, PvModuleSpecs> modules = PvModulesData.getInstance().getModules();
                    for (final String key : modules.keySet()) {
                        modelComboBox.addItem(key);
                    }
                    if (solarPanel.getModelName() != null) {
                        modelComboBox.setSelectedItem(solarPanel.getModelName());
                    }
                    modelComboBox.addItemListener(e1 -> {
                        if (e1.getStateChange() == ItemEvent.SELECTED) {
                            final boolean isCustom = modelComboBox.getSelectedIndex() == 0;
                            sizeComboBox.setEnabled(isCustom);
                            cellTypeComboBox.setEnabled(isCustom);
                            colorOptionComboBox.setEnabled(isCustom);
                            shadeToleranceComboBox.setEnabled(isCustom);
                            cellEfficiencyField.setEnabled(isCustom);
                            noctField.setEnabled(isCustom);
                            pmaxTcField.setEnabled(isCustom);
                            if (!isCustom) {
                                final PvModuleSpecs specs = modules.get(modelComboBox.getSelectedItem());
                                cellTypeComboBox.setSelectedItem(specs.getCellType());
                                shadeToleranceComboBox.setSelectedItem(specs.getShadeTolerance());
                                cellEfficiencyField.setText(threeDecimalsFormat.format(specs.getCelLEfficiency() * 100));
                                noctField.setText(threeDecimalsFormat.format(specs.getNoct()));
                                pmaxTcField.setText(sixDecimalsFormat.format(specs.getPmaxTc()));
                                final String s = threeDecimalsFormat.format(specs.getNominalWidth()) + "m \u00D7 " + threeDecimalsFormat.format(specs.getNominalLength()) + "m (" + specs.getLayout().width + " \u00D7 " + specs.getLayout().height + " cells)";
                                sizeComboBox.setSelectedItem(s);
                                colorOptionComboBox.setSelectedItem(specs.getColor());
                            }
                        }
                    });
                    panel.add(modelComboBox);

                    // the following properties should be disabled when the model is not custom
                    panel.add(new JLabel(I18n.get("label.panel_size")));
                    sizeComboBox = new JComboBox<String>(solarPanelNominalSize.getStrings());
                    final PvModuleSpecs specs = solarPanel.getPvModuleSpecs();
                    final boolean isCustom = I18n.get("model.custom").equals(specs.getModel());
                    final double width = isCustom ? solarPanel.getPanelWidth() : specs.getNominalWidth();
                    final double height = isCustom ? solarPanel.getPanelHeight() : specs.getNominalLength();
                    final int nItems = sizeComboBox.getItemCount();
                    for (int i = 0; i < nItems; i++) {
                        if (Util.isZero(height - solarPanelNominalSize.getNominalHeights()[i]) && Util.isZero(width - solarPanelNominalSize.getNominalWidths()[i])) {
                            sizeComboBox.setSelectedIndex(i);
                        }
                    }
                    panel.add(sizeComboBox);
                    panel.add(new JLabel(I18n.get("label.cell_type")));
                    cellTypeComboBox = new JComboBox<String>(new String[]{I18n.get("cell_type.polycrystalline"), I18n.get("cell_type.monocrystalline"), I18n.get("cell_type.thin_film")});
                    cellTypeComboBox.setSelectedIndex(solarPanel.getCellType());
                    panel.add(cellTypeComboBox);
                    panel.add(new JLabel(I18n.get("label.color")));
                    colorOptionComboBox = new JComboBox<String>(new String[]{I18n.get("color.blue"), I18n.get("color.black"), I18n.get("color.gray")});
                    colorOptionComboBox.setSelectedIndex(solarPanel.getColorOption());
                    panel.add(colorOptionComboBox);
                    panel.add(new JLabel(I18n.get("label.solar_cell_efficiency_percent")));
                    cellEfficiencyField = new JTextField(threeDecimalsFormat.format(solarPanel.getCellEfficiency() * 100));
                    panel.add(cellEfficiencyField);
                    panel.add(new JLabel("<html>" + I18n.get("label.nominal_operating_cell_temperature") + " (&deg;C):"));
                    noctField = new JTextField(threeDecimalsFormat.format(solarPanel.getNominalOperatingCellTemperature()));
                    panel.add(noctField);
                    panel.add(new JLabel("<html>" + I18n.get("label.temperature_coefficient_pmax") + " (%/&deg;C):"));
                    pmaxTcField = new JTextField(sixDecimalsFormat.format(solarPanel.getTemperatureCoefficientPmax() * 100));
                    panel.add(pmaxTcField);
                    panel.add(new JLabel(I18n.get("label.shade_tolerance")));
                    shadeToleranceComboBox = new JComboBox<String>(new String[]{I18n.get("shade_tolerance.partial"), I18n.get("shade_tolerance.high"), I18n.get("shade_tolerance.none")});
                    shadeToleranceComboBox.setSelectedIndex(solarPanel.getShadeTolerance());
                    panel.add(shadeToleranceComboBox);

                    if (modelComboBox.getSelectedIndex() != 0) {
                        sizeComboBox.setEnabled(false);
                        cellTypeComboBox.setEnabled(false);
                        colorOptionComboBox.setEnabled(false);
                        shadeToleranceComboBox.setEnabled(false);
                        cellEfficiencyField.setEnabled(false);
                        noctField.setEnabled(false);
                        pmaxTcField.setEnabled(false);
                    }

                    // the following properties are not related to the model
                    panel.add(new JLabel(I18n.get("label.orientation")));
                    orientationComboBox = new JComboBox<String>(new String[]{I18n.get("orientation.portrait"), I18n.get("orientation.landscape")});
                    orientationComboBox.setSelectedIndex(solarPanel.isRotated() ? 1 : 0);
                    panel.add(orientationComboBox);
                    panel.add(new JLabel(I18n.get("label.inverter_efficiency_percent")));
                    final JTextField inverterEfficiencyField = new JTextField(threeDecimalsFormat.format(solarPanel.getInverterEfficiency() * 100));
                    panel.add(inverterEfficiencyField);
                    SpringUtilities.makeCompactGrid(panel, 10, 2, 6, 6, 6, 6);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panels_on_rack"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            if (modelComboBox.getSelectedIndex() == 0) {
                                try {
                                    cellEfficiency = Double.parseDouble(cellEfficiencyField.getText());
                                    pmax = Double.parseDouble(pmaxTcField.getText());
                                    noct = Double.parseDouble(noctField.getText());
                                    inverterEfficiency = Double.parseDouble(inverterEfficiencyField.getText());
                                } catch (final NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    if (cellEfficiency < SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE || cellEfficiency > SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_cell_efficiency_range", SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (inverterEfficiency < SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE || inverterEfficiency >= SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inverter_efficiency_range", SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (pmax < -1 || pmax > 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.temperature_coefficient_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (noct < 33 || noct > 58) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.noct_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        setCustomSolarPanels();
                                        if (choice == options[0]) {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                try {
                                    inverterEfficiency = Double.parseDouble(inverterEfficiencyField.getText());
                                } catch (final NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    if (inverterEfficiency < SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE || inverterEfficiency >= SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inverter_efficiency_range", SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        setBrandSolarPanels((String) modelComboBox.getSelectedItem());
                                        if (choice == options[0]) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                private void setBrandSolarPanels(final String modelName) {
                    final SolarPanel s = rack.getSolarPanel();
                    final boolean changed = !modelName.equals(s.getPvModuleSpecs().getModel()) || Math.abs(inverterEfficiency * 0.01 - s.getInverterEfficiency()) > 0.000001 || ((orientationComboBox.getSelectedIndex() == 1) ^ s.isRotated());
                    if (changed) {
                        final SetSolarPanelArrayOnRackByModelCommand command = rack.isMonolithic() ? new SetSolarPanelArrayOnRackByModelCommand(rack) : null;
                        s.setRotated(orientationComboBox.getSelectedIndex() == 1);
                        s.setInverterEfficiency(inverterEfficiency * 0.01);
                        s.setPvModuleSpecs(PvModulesData.getInstance().getModuleSpecs(modelName));
                        SceneManager.getTaskManager().update(() -> {
                            rack.addSolarPanels();
                            if (command != null) {
                                EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(command));
                            }
                            return null;
                        });
                        updateAfterEdit();
                    }
                }

                private void setCustomSolarPanels() {
                    final SolarPanel s = rack.getSolarPanel();
                    final int i = sizeComboBox.getSelectedIndex();
                    boolean changed = !I18n.get("model.custom").equals(s.getModelName());
                    if (s.getPanelWidth() != solarPanelNominalSize.getNominalWidths()[i]) {
                        changed = true;
                    } else if (s.getPanelHeight() != solarPanelNominalSize.getNominalHeights()[i]) {
                        changed = true;
                    } else if (s.getNumberOfCellsInX() != solarPanelNominalSize.getCellNx()[i]) {
                        changed = true;
                    } else if (s.getNumberOfCellsInY() != solarPanelNominalSize.getCellNy()[i]) {
                        changed = true;
                    } else if (s.isRotated() ^ orientationComboBox.getSelectedIndex() == 1) {
                        changed = true;
                    } else if (s.getCellType() != cellTypeComboBox.getSelectedIndex()) {
                        changed = true;
                    } else if (s.getColorOption() != colorOptionComboBox.getSelectedIndex()) {
                        changed = true;
                    } else if (Math.abs(s.getCellEfficiency() - cellEfficiency * 0.01) > 0.000001) {
                        changed = true;
                    } else if (Math.abs(s.getInverterEfficiency() - inverterEfficiency * 0.01) > 0.000001) {
                        changed = true;
                    } else if (Math.abs(s.getTemperatureCoefficientPmax() - pmax * 0.01) > 0.000001) {
                        changed = true;
                    } else if (Math.abs(s.getNominalOperatingCellTemperature() - noct) > 0.000001) {
                        changed = true;
                    } else if (s.getShadeTolerance() != shadeToleranceComboBox.getSelectedIndex()) {
                        changed = true;
                    }
                    if (changed) {
                        s.setModelName(I18n.get("model.custom"));
                        s.setBrandName(I18n.get("model.custom"));
                        final SetSolarPanelArrayOnRackCustomCommand command = rack.isMonolithic() ? new SetSolarPanelArrayOnRackCustomCommand(rack) : null;
                        s.setPanelWidth(solarPanelNominalSize.getNominalWidths()[i]);
                        s.setPanelHeight(solarPanelNominalSize.getNominalHeights()[i]);
                        s.setNumberOfCellsInX(solarPanelNominalSize.getCellNx()[i]);
                        s.setNumberOfCellsInY(solarPanelNominalSize.getCellNy()[i]);
                        s.setRotated(orientationComboBox.getSelectedIndex() == 1);
                        s.setCellType(cellTypeComboBox.getSelectedIndex());
                        s.setColorOption(colorOptionComboBox.getSelectedIndex());
                        s.setCellEfficiency(cellEfficiency * 0.01);
                        s.setInverterEfficiency(inverterEfficiency * 0.01);
                        s.setTemperatureCoefficientPmax(pmax * 0.01);
                        s.setNominalOperatingCellTemperature(noct);
                        s.setShadeTolerance(shadeToleranceComboBox.getSelectedIndex());
                        SceneManager.getTaskManager().update(() -> {
                            rack.addSolarPanels();
                            if (command != null) {
                                EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(command));
                            }
                            return null;
                        });
                        updateAfterEdit();
                    }
                }

            });

            final JMenu solarPanelMenu = new JMenu(I18n.get("menu.change_solar_panel_properties"));

            final JMenuItem miSolarPanelModel = new JMenuItem(I18n.get("menu.model") + "...");
            solarPanelMenu.add(miSolarPanelModel);
            miSolarPanelModel.addActionListener(new ActionListener() {

                private String modelName;
                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final Map<String, PvModuleSpecs> modules = PvModulesData.getInstance().getModules();
                    final String[] models = new String[modules.size() + 1];
                    int i = 0;
                    models[i] = I18n.get("label.custom");
                    for (final String key : modules.keySet()) {
                        models[++i] = key;
                    }
                    final PvModuleSpecs specs = s.getPvModuleSpecs();
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.solar_panel_model_for", partInfo)));
                    final JComboBox<String> typeComboBox = new JComboBox<String>(models);
                    typeComboBox.setSelectedItem(specs.getModel());
                    modelName = specs.getModel();
                    typeComboBox.addItemListener(e12 -> {
                        if (e12.getStateChange() == ItemEvent.SELECTED) {
                            modelName = (String) typeComboBox.getSelectedItem();
                        }
                    });
                    gui.add(typeComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(gui, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_model"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = !modelName.equals(s.getModelName());
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final ChangeSolarPanelModelForRackCommand c = new ChangeSolarPanelModelForRackCommand(r);
                                    SceneManager.getTaskManager().update(() -> {
                                        s.setPvModuleSpecs(PvModulesData.getInstance().getModuleSpecs(modelName));
                                        r.ensureFullSolarPanels(false);
                                        r.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : foundation.getRacks()) {
                                        if (!modelName.equals(x.getSolarPanel().getModelName())) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeSolarPanelModelForRacksOnFoundationCommand c = new ChangeSolarPanelModelForRacksOnFoundationCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setSolarPanelModelForRacks(PvModulesData.getInstance().getModuleSpecs(modelName));
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : Scene.getInstance().getAllRacks()) {
                                        if (!modelName.equals(x.getSolarPanel().getModelName())) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeSolarPanelModelForAllRacksCommand c = new ChangeSolarPanelModelForAllRacksCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setSolarPanelModelForAllRacks(PvModulesData.getInstance().getModuleSpecs(modelName));
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

            solarPanelMenu.addSeparator();

            final JMenuItem miSolarPanelSize = new JMenuItem(I18n.get("menu.size") + "...");
            solarPanelMenu.add(miSolarPanelSize);
            miSolarPanelSize.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.solar_panel_size_for", partInfo)));
                    final JComboBox<String> sizeComboBox = new JComboBox<String>(solarPanelNominalSize.getStrings());
                    final PvModuleSpecs specs = s.getPvModuleSpecs();
                    final boolean isCustom = I18n.get("model.custom").equals(specs.getModel());
                    final double width = isCustom ? s.getPanelWidth() : specs.getNominalWidth();
                    final double height = isCustom ? s.getPanelHeight() : specs.getNominalLength();
                    final int nItems = sizeComboBox.getItemCount();
                    for (int i = 0; i < nItems; i++) {
                        if (Util.isZero(height - solarPanelNominalSize.getNominalHeights()[i]) && Util.isZero(width - solarPanelNominalSize.getNominalWidths()[i])) {
                            sizeComboBox.setSelectedIndex(i);
                        }
                    }
                    gui.add(sizeComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(gui, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_size"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            final int i = sizeComboBox.getSelectedIndex();
                            final double w = solarPanelNominalSize.getNominalWidths()[i];
                            final double h = solarPanelNominalSize.getNominalHeights()[i];
                            final int numberOfCellsInX = solarPanelNominalSize.getCellNx()[i];
                            final int numberOfCellsInY = solarPanelNominalSize.getCellNy()[i];
                            boolean changed = numberOfCellsInX != s.getNumberOfCellsInX() || numberOfCellsInY != s.getNumberOfCellsInY();
                            if (Math.abs(s.getPanelWidth() - w) > 0.000001 || Math.abs(s.getPanelHeight() - h) > 0.000001) {
                                changed = true;
                            }
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final ChooseSolarPanelSizeForRackCommand c = new ChooseSolarPanelSizeForRackCommand(r);
                                    SceneManager.getTaskManager().update(() -> {
                                        s.setPanelWidth(w);
                                        s.setPanelHeight(h);
                                        s.setNumberOfCellsInX(numberOfCellsInX);
                                        s.setNumberOfCellsInY(numberOfCellsInY);
                                        r.ensureFullSolarPanels(false);
                                        r.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : foundation.getRacks()) {
                                        final SolarPanel p = x.getSolarPanel();
                                        if (numberOfCellsInX != p.getNumberOfCellsInX() || numberOfCellsInY != p.getNumberOfCellsInY()) {
                                            changed = true;
                                            break;
                                        }
                                        if (Math.abs(p.getPanelWidth() - w) > 0.000001 || Math.abs(p.getPanelHeight() - h) > 0.000001) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelSizeForRacksOnFoundationCommand c = new SetSolarPanelSizeForRacksOnFoundationCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setSolarPanelSizeForRacks(w, h, numberOfCellsInX, numberOfCellsInY);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : Scene.getInstance().getAllRacks()) {
                                        final SolarPanel p = x.getSolarPanel();
                                        if (numberOfCellsInX != p.getNumberOfCellsInX() || numberOfCellsInY != p.getNumberOfCellsInY()) {
                                            changed = true;
                                            break;
                                        }
                                        if (Math.abs(p.getPanelWidth() - w) > 0.000001 || Math.abs(p.getPanelHeight() - h) > 0.000001) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelSizeForAllRacksCommand c = new SetSolarPanelSizeForAllRacksCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setSolarPanelSizeForAllRacks(w, h, numberOfCellsInX, numberOfCellsInY);
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

            final JMenuItem miSolarPanelCellType = new JMenuItem(I18n.get("menu.cell_type") + "...");
            solarPanelMenu.add(miSolarPanelCellType);
            miSolarPanelCellType.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.choose_cell_type_for", partInfo)));
                    final JComboBox<String> cellTypeComboBox = new JComboBox<String>(new String[]{I18n.get("cell_type.polycrystalline"), I18n.get("cell_type.monocrystalline"), I18n.get("cell_type.thin_film")});
                    cellTypeComboBox.setSelectedIndex(s.getCellType());
                    gui.add(cellTypeComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(gui, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_cell_type"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            final int selectedIndex = cellTypeComboBox.getSelectedIndex();
                            boolean changed = selectedIndex != s.getCellType();
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarPanelCellTypeForRackCommand c = new SetSolarPanelCellTypeForRackCommand(r);
                                    SceneManager.getTaskManager().update(() -> {
                                        s.setCellType(selectedIndex);
                                        r.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : foundation.getRacks()) {
                                        if (x.getSolarPanel().getCellType() != selectedIndex) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelCellTypeForRacksOnFoundationCommand c = new SetSolarPanelCellTypeForRacksOnFoundationCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setSolarPanelCellTypeForRacks(selectedIndex);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : Scene.getInstance().getAllRacks()) {
                                        if (x.getSolarPanel().getCellType() != selectedIndex) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelCellTypeForAllRacksCommand c = new SetSolarPanelCellTypeForAllRacksCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setSolarPanelCellTypeForAllRacks(selectedIndex);
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

            final JMenuItem miSolarPanelColor = new JMenuItem(I18n.get("menu.color") + "...");
            solarPanelMenu.add(miSolarPanelColor);
            miSolarPanelColor.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.choose_color_for", partInfo)));
                    final JComboBox<String> colorComboBox = new JComboBox<String>(new String[]{I18n.get("color.blue"), I18n.get("color.black"), I18n.get("color.gray")});
                    colorComboBox.setSelectedIndex(s.getColorOption());
                    gui.add(colorComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(gui, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_color"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            final int selectedIndex = colorComboBox.getSelectedIndex();
                            boolean changed = selectedIndex != s.getColorOption();
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarPanelColorForRackCommand c = new SetSolarPanelColorForRackCommand(r);
                                    SceneManager.getTaskManager().update(() -> {
                                        s.setColorOption(selectedIndex);
                                        r.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : foundation.getRacks()) {
                                        if (x.getSolarPanel().getColorOption() != selectedIndex) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelColorForRacksOnFoundationCommand c = new SetSolarPanelColorForRacksOnFoundationCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setSolarPanelColorForRacks(selectedIndex);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : Scene.getInstance().getAllRacks()) {
                                        if (x.getSolarPanel().getColorOption() != selectedIndex) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelColorForAllRacksCommand c = new SetSolarPanelColorForAllRacksCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setSolarPanelColorForAllRacks(selectedIndex);
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

            final JMenuItem miSolarPanelCellEfficiency = new JMenuItem(I18n.get("menu.solar_cell_efficiency") + "...");
            solarPanelMenu.add(miSolarPanelCellEfficiency);
            miSolarPanelCellEfficiency.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final String title = I18n.get("title.set_solar_cell_efficiency_for", partInfo);
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.solar_cell_efficiency", Integer.toString(SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE)) + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    final JTextField inputField = new JTextField(threeDecimalsFormat.format(s.getCellEfficiency() * 100));
                    gui.add(inputField, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_cell_efficiency"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            try {
                                solarCellEfficiencyPercentage = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (solarCellEfficiencyPercentage < SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE || solarCellEfficiencyPercentage > SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_cell_efficiency_range", Integer.toString(SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE), Integer.toString(SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE)), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(solarCellEfficiencyPercentage * 0.01 - s.getCellEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetSolarCellEfficiencyForRackCommand c = new SetSolarCellEfficiencyForRackCommand(r);
                                            s.setCellEfficiency(solarCellEfficiencyPercentage * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (Math.abs(solarCellEfficiencyPercentage * 0.01 - x.getSolarPanel().getCellEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSolarCellEfficiencyForRacksOnFoundationCommand c = new SetSolarCellEfficiencyForRacksOnFoundationCommand(foundation);
                                            foundation.setSolarCellEfficiencyForRacks(solarCellEfficiencyPercentage * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (Math.abs(solarCellEfficiencyPercentage * 0.01 - x.getSolarPanel().getCellEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetSolarCellEfficiencyForAllRacksCommand c = new SetSolarCellEfficiencyForAllRacksCommand();
                                            Scene.getInstance().setSolarCellEfficiencyForAllRacks(solarCellEfficiencyPercentage * 0.01);
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

            final JMenuItem miSolarPanelNoct = new JMenuItem(I18n.get("menu.nominal_operating_cell_temperature") + "...");
            solarPanelMenu.add(miSolarPanelNoct);
            miSolarPanelNoct.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String title = "<html>" + I18n.get("title.nominal_operating_cell_temperature") + " (&deg;C) " + I18n.get("common.for_lowercase") + " " + r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final String footnote = "<html><hr><font size=2>" + I18n.get("msg.increased_temperature_reduces_efficiency") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    final JTextField inputField = new JTextField(threeDecimalsFormat.format(s.getNominalOperatingCellTemperature()));
                    gui.add(inputField, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.nominal_operating_cell_temperature"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            try {
                                solarPanelNominalOperatingCellTemperature = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (solarPanelNominalOperatingCellTemperature < 33 || solarPanelNominalOperatingCellTemperature > 58) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.noct_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(solarPanelNominalOperatingCellTemperature - s.getNominalOperatingCellTemperature()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetNoctForRackCommand c = new SetNoctForRackCommand(r);
                                            s.setNominalOperatingCellTemperature(solarPanelNominalOperatingCellTemperature);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (Math.abs(solarPanelNominalOperatingCellTemperature - x.getSolarPanel().getNominalOperatingCellTemperature()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetNoctForRacksOnFoundationCommand c = new SetNoctForRacksOnFoundationCommand(foundation);
                                            foundation.setNominalOperatingCellTemperatureForRacks(solarPanelNominalOperatingCellTemperature);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (Math.abs(solarPanelNominalOperatingCellTemperature - x.getSolarPanel().getNominalOperatingCellTemperature()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetNoctForAllRacksCommand c = new SetNoctForAllRacksCommand();
                                            Scene.getInstance().setNominalOperatingCellTemperatureForAllRacks(solarPanelNominalOperatingCellTemperature);
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

            final JMenuItem miSolarPanelPmaxTc = new JMenuItem(I18n.get("menu.temperature_coefficient_pmax") + "...");
            solarPanelMenu.add(miSolarPanelPmaxTc);
            miSolarPanelPmaxTc.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final String title = "<html>" + I18n.get("title.temperature_coefficient_pmax_for", partInfo) + " (%/&deg;C)</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.temperature_coefficient_pmax") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    final JTextField inputField = new JTextField(threeDecimalsFormat.format(s.getTemperatureCoefficientPmax() * 100));
                    gui.add(inputField, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.temperature_coefficient_pmax"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            try {
                                solarPanelTemperatureCoefficientPmaxPercentage = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (solarPanelTemperatureCoefficientPmaxPercentage < -1 || solarPanelTemperatureCoefficientPmaxPercentage > 0) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.temperature_coefficient_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(solarPanelTemperatureCoefficientPmaxPercentage * 0.01 - s.getTemperatureCoefficientPmax()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetTemperatureCoefficientPmaxForRackCommand c = new SetTemperatureCoefficientPmaxForRackCommand(r);
                                            s.setTemperatureCoefficientPmax(solarPanelTemperatureCoefficientPmaxPercentage * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (Math.abs(solarPanelTemperatureCoefficientPmaxPercentage * 0.01 - x.getSolarPanel().getTemperatureCoefficientPmax()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetTemperatureCoefficientPmaxForRacksOnFoundationCommand c = new SetTemperatureCoefficientPmaxForRacksOnFoundationCommand(foundation);
                                            foundation.setTemperatureCoefficientPmaxForRacks(solarPanelTemperatureCoefficientPmaxPercentage * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (Math.abs(solarPanelTemperatureCoefficientPmaxPercentage * 0.01 - x.getSolarPanel().getTemperatureCoefficientPmax()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetTemperatureCoefficientPmaxForAllRacksCommand c = new SetTemperatureCoefficientPmaxForAllRacksCommand();
                                            Scene.getInstance().setTemperatureCoefficientPmaxForAllRacks(solarPanelTemperatureCoefficientPmaxPercentage * 0.01);
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

            final JMenuItem miSolarPanelShadeTolerance = new JMenuItem(I18n.get("menu.shade_tolerance") + "...");
            solarPanelMenu.add(miSolarPanelShadeTolerance);
            miSolarPanelShadeTolerance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final String title = I18n.get("title.set_solar_panel_shade_tolerance_for", partInfo);
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.shade_tolerance") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    final JComboBox<String> toleranceComboBox = new JComboBox<String>(new String[]{I18n.get("shade_tolerance.partial"), I18n.get("shade_tolerance.high"), I18n.get("label.none")});
                    toleranceComboBox.setSelectedIndex(s.getShadeTolerance());
                    gui.add(toleranceComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_shade_tolerance"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            final int selectedIndex = toleranceComboBox.getSelectedIndex();
                            boolean changed = selectedIndex != s.getShadeTolerance();
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarPanelShadeToleranceForRackCommand c = new SetSolarPanelShadeToleranceForRackCommand(r);
                                    s.setShadeTolerance(selectedIndex);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : foundation.getRacks()) {
                                        if (selectedIndex != x.getSolarPanel().getShadeTolerance()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelShadeToleranceForRacksOnFoundationCommand c = new SetSolarPanelShadeToleranceForRacksOnFoundationCommand(foundation);
                                    foundation.setSolarPanelShadeToleranceForRacks(selectedIndex);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : Scene.getInstance().getAllRacks()) {
                                        if (selectedIndex != x.getSolarPanel().getShadeTolerance()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarPanelShadeToleranceForAllRacksCommand c = new SetSolarPanelShadeToleranceForAllRacksCommand();
                                    Scene.getInstance().setSolarPanelShadeToleranceForAllRacks(selectedIndex);
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

            solarPanelMenu.addSeparator();

            final JMenuItem miSolarPanelOrientation = new JMenuItem(I18n.get("menu.orientation") + "...");
            solarPanelMenu.add(miSolarPanelOrientation);
            miSolarPanelOrientation.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.solar_panel_orientation_for", partInfo)));
                    final JComboBox<String> orientationComboBox = new JComboBox<String>(new String[]{I18n.get("orientation.portrait"), I18n.get("orientation.landscape")});
                    orientationComboBox.setSelectedIndex(s.isRotated() ? 1 : 0);
                    gui.add(orientationComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(gui, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_orientation"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            final boolean b = orientationComboBox.getSelectedIndex() == 1;
                            boolean changed = b ^ s.isRotated();
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final RotateSolarPanelsForRackCommand c = new RotateSolarPanelsForRackCommand(r);
                                    SceneManager.getTaskManager().update(() -> {
                                        s.setRotated(orientationComboBox.getSelectedIndex() == 1);
                                        r.ensureFullSolarPanels(false);
                                        r.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : foundation.getRacks()) {
                                        if (b ^ x.getSolarPanel().isRotated()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final RotateSolarPanelsForRacksOnFoundationCommand c = new RotateSolarPanelsForRacksOnFoundationCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.rotateSolarPanelsOnRacks(orientationComboBox.getSelectedIndex() == 1);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final Rack x : Scene.getInstance().getAllRacks()) {
                                        if (b ^ x.getSolarPanel().isRotated()) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final RotateSolarPanelsForAllRacksCommand c = new RotateSolarPanelsForAllRacksCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().rotateSolarPanelsOnAllRacks(orientationComboBox.getSelectedIndex() == 1);
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

            final JMenuItem miInverterEfficiency = new JMenuItem(I18n.get("menu.inverter_efficiency") + "...");
            solarPanelMenu.add(miInverterEfficiency);
            miInverterEfficiency.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack r = (Rack) selectedPart;
                    final Foundation foundation = r.getTopContainer();
                    final SolarPanel s = r.getSolarPanel();
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final String title = I18n.get("title.set_inverter_efficiency_for", partInfo);
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.inverter_efficiency") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    final JTextField inputField = new JTextField(threeDecimalsFormat.format(s.getInverterEfficiency() * 100));
                    gui.add(inputField, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    gui.add(scopePanel, BorderLayout.CENTER);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.inverter_efficiency"));

                    while (true) {
                        inputField.selectAll();
                        inputField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean ok = true;
                            try {
                                inverterEfficiencyPercentage = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (inverterEfficiencyPercentage < SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE || inverterEfficiencyPercentage > SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inverter_efficiency_range", Integer.toString(SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE), Integer.toString(SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE)), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(inverterEfficiencyPercentage * 0.01 - s.getInverterEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetInverterEfficiencyForRackCommand c = new SetInverterEfficiencyForRackCommand(r);
                                            s.setInverterEfficiency(inverterEfficiencyPercentage * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : foundation.getRacks()) {
                                                if (Math.abs(inverterEfficiencyPercentage * 0.01 - x.getSolarPanel().getInverterEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetInverterEfficiencyForRacksOnFoundationCommand c = new SetInverterEfficiencyForRacksOnFoundationCommand(foundation);
                                            foundation.setInverterEfficiencyForRacks(inverterEfficiencyPercentage * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                                if (Math.abs(inverterEfficiencyPercentage * 0.01 - x.getSolarPanel().getInverterEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetInverterEfficiencyForAllRacksCommand c = new SetInverterEfficiencyForAllRacksCommand();
                                            Scene.getInstance().setInverterEfficiencyForAllRacks(inverterEfficiencyPercentage * 0.01);
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

            final ButtonGroup trackerButtonGroup = new ButtonGroup();

            final JRadioButtonMenuItem miNoTracker = new JRadioButtonMenuItem(I18n.get("menu.no_tracker"), true);
            trackerButtonGroup.add(miNoTracker);
            miNoTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final String partInfo = rack.toString().substring(0, rack.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final String title = "<html>" + I18n.get("title.remove_tracker_for", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.no_tracker") + "<hr></html>";
                    final Object[] params = {title, footnote, panel};
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), params, I18n.get("dialog.remove_solar_tracker"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    boolean changed = rack.getTracker() != Trackable.NO_TRACKER;
                    if (rb1.isSelected()) {
                        if (changed) {
                            final SetSolarTrackerCommand c = new SetSolarTrackerCommand(rack, I18n.get("tracker.no_tracker"));
                            SceneManager.getTaskManager().update(() -> {
                                rack.setTracker(Trackable.NO_TRACKER);
                                rack.draw();
                                SceneManager.getInstance().refresh();
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 0;
                    } else if (rb2.isSelected()) {
                        final Foundation foundation = rack.getTopContainer();
                        if (!changed) {
                            for (final Rack x : foundation.getRacks()) {
                                if (x.getTracker() != Trackable.NO_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, rack, I18n.get("tracker.no_tracker_all_racks_foundation"));
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setTrackerForRacks(Trackable.NO_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 1;
                    } else if (rb3.isSelected()) {
                        if (!changed) {
                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                if (x.getTracker() != Trackable.NO_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(rack, I18n.get("tracker.no_tracker_all_racks"));
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setTrackerForAllRacks(Trackable.NO_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 2;
                    }
                    if (changed) {
                        updateAfterEdit();
                    }
                }
            });

            final JRadioButtonMenuItem miHorizontalSingleAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.horizontal_single_axis_tracker"));
            trackerButtonGroup.add(miHorizontalSingleAxisTracker);
            miHorizontalSingleAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final String partInfo = rack.toString().substring(0, rack.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final String title = "<html>" + I18n.get("title.set_horizontal_single_axis_tracker", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.horizontal_single_axis_tracker") + "<hr></html>";
                    final Object[] params = {title, footnote, panel};
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), params, I18n.get("dialog.set_horizontal_single_axis_solar_tracker"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    boolean changed = rack.getTracker() != Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER;
                    if (rb1.isSelected()) {
                        if (changed) {
                            final SetSolarTrackerCommand c = new SetSolarTrackerCommand(rack, I18n.get("tracker.horizontal_single_axis"));
                            SceneManager.getTaskManager().update(() -> {
                                rack.setTracker(Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER);
                                rack.draw();
                                SceneManager.getInstance().refresh();
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 0;
                    } else if (rb2.isSelected()) {
                        final Foundation foundation = rack.getTopContainer();
                        if (!changed) {
                            for (final Rack x : foundation.getRacks()) {
                                if (x.getTracker() != Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, rack,
                                    I18n.get("tracker.horizontal_single_axis_for_all_on_foundation"));
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setTrackerForRacks(Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 1;
                    } else if (rb3.isSelected()) {
                        if (!changed) {
                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                if (x.getTracker() != Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(rack, I18n.get("tracker.horizontal_single_axis_for_all"));
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setTrackerForAllRacks(Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 2;
                    }
                    if (changed) {
                        updateAfterEdit();
                    }
                }
            });

            final JRadioButtonMenuItem miVerticalSingleAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.vertical_single_axis_tracker"));
            trackerButtonGroup.add(miVerticalSingleAxisTracker);
            miVerticalSingleAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final String partInfo = rack.toString().substring(0, rack.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final String title = "<html>" + I18n.get("title.set_vertical_single_axis_tracker", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.vertical_single_axis_tracker") + "<hr></html>";
                    final Object[] params = {title, footnote, panel};
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), params, I18n.get("dialog.set_vertical_single_axis_solar_tracker"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    boolean changed = rack.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER;
                    if (rb1.isSelected()) {
                        if (changed) {
                            final SetSolarTrackerCommand c = new SetSolarTrackerCommand(rack, I18n.get("tracker.vertical_single_axis"));
                            SceneManager.getTaskManager().update(() -> {
                                rack.setTracker(Trackable.VERTICAL_SINGLE_AXIS_TRACKER);
                                rack.draw();
                                SceneManager.getInstance().refresh();
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 0;
                    } else if (rb2.isSelected()) {
                        final Foundation foundation = rack.getTopContainer();
                        if (!changed) {
                            for (final Rack x : foundation.getRacks()) {
                                if (x.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, rack,
                                    I18n.get("tracker.vertical_single_axis_for_all_racks_on_foundation"));
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setTrackerForRacks(Trackable.VERTICAL_SINGLE_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 1;
                    } else if (rb3.isSelected()) {
                        if (!changed) {
                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                if (x.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(rack, I18n.get("tracker.vertical_single_axis_for_all_racks"));
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setTrackerForAllRacks(Trackable.VERTICAL_SINGLE_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 2;
                    }
                    if (changed) {
                        updateAfterEdit();
                    }
                }
            });

            final JRadioButtonMenuItem miTiltedSingleAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.tilted_single_axis_tracker"));
            miTiltedSingleAxisTracker.setEnabled(false);
            trackerButtonGroup.add(miTiltedSingleAxisTracker);
            miTiltedSingleAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final String partInfo = rack.toString().substring(0, rack.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final String title = "<html>" + I18n.get("title.set_tilted_single_axis_tracker", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.tilted_single_axis_tracker") + "<hr></html>";
                    final Object[] params = {title, footnote, panel};
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), params, I18n.get("dialog.set_tilted_single_axis_solar_tracker"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    boolean changed = rack.getTracker() != Trackable.TILTED_SINGLE_AXIS_TRACKER;
                    if (rb1.isSelected()) {
                        if (changed) {
                            final SetSolarTrackerCommand c = new SetSolarTrackerCommand(rack, I18n.get("tracker.tilted_single_axis"));
                            SceneManager.getTaskManager().update(() -> {
                                rack.setTracker(Trackable.TILTED_SINGLE_AXIS_TRACKER);
                                rack.draw();
                                SceneManager.getInstance().refresh();
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 0;
                    } else if (rb2.isSelected()) {
                        final Foundation foundation = rack.getTopContainer();
                        if (!changed) {
                            for (final Rack x : foundation.getRacks()) {
                                if (x.getTracker() != Trackable.TILTED_SINGLE_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, rack,
                                    I18n.get("tracker.tilted_single_axis_for_all_on_foundation"));
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setTrackerForRacks(Trackable.TILTED_SINGLE_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 1;
                    } else if (rb3.isSelected()) {
                        if (!changed) {
                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                if (x.getTracker() != Trackable.TILTED_SINGLE_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(rack, I18n.get("tracker.tilted_single_axis_for_all"));
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setTrackerForAllRacks(Trackable.TILTED_SINGLE_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 2;
                    }
                    if (changed) {
                        updateAfterEdit();
                    }
                }
            });

            final JRadioButtonMenuItem miAltazimuthDualAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.altazimuth_dual_axis_tracker"));
            trackerButtonGroup.add(miAltazimuthDualAxisTracker);
            miAltazimuthDualAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final Rack rack = (Rack) selectedPart;
                    final String partInfo = rack.toString().substring(0, rack.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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
                    final String title = "<html>" + I18n.get("title.set_altazimuth_dual_axis_tracker", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.altazimuth_dual_axis_tracker") + "<hr></html>";
                    final Object[] params = {title, footnote, panel};
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), params, I18n.get("dialog.set_altazimuth_dual_axis_solar_tracker"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    boolean changed = rack.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER;
                    if (rb1.isSelected()) {
                        if (changed) {
                            final SetSolarTrackerCommand c = new SetSolarTrackerCommand(rack, I18n.get("tracker.dual_axis"));
                            SceneManager.getTaskManager().update(() -> {
                                rack.setTracker(Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER);
                                rack.draw();
                                SceneManager.getInstance().refresh();
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 0;
                    } else if (rb2.isSelected()) {
                        final Foundation foundation = rack.getTopContainer();
                        if (!changed) {
                            for (final Rack x : foundation.getRacks()) {
                                if (x.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, rack,
                                    I18n.get("tracker.dual_axis_for_all_on_foundation"));
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setTrackerForRacks(Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 1;
                    } else if (rb3.isSelected()) {
                        if (!changed) {
                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                if (x.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(rack, I18n.get("tracker.dual_axis_for_all"));
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setTrackerForAllRacks(Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 2;
                    }
                    if (changed) {
                        updateAfterEdit();
                    }
                }
            });

            trackerMenu.add(miNoTracker);
            trackerMenu.add(miHorizontalSingleAxisTracker);
            trackerMenu.add(miVerticalSingleAxisTracker);
            trackerMenu.add(miTiltedSingleAxisTracker);
            trackerMenu.add(miAltazimuthDualAxisTracker);

            final JCheckBoxMenuItem cbmiDisableEditPoints = new JCheckBoxMenuItem(I18n.get("menu.disable_edit_points"));
            cbmiDisableEditPoints.addItemListener(new ItemListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void itemStateChanged(final ItemEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Rack)) {
                        return;
                    }
                    final boolean disabled = cbmiDisableEditPoints.isSelected();
                    final Rack r = (Rack) selectedPart;
                    final String partInfo = r.toString().substring(0, r.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(0, 20));
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.SOUTH);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_rack"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_racks_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_racks"));
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

                    final String title = "<html>" + I18n.get(disabled ? "title.disable_edit_points" : "title.enable_edit_points", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.disable_edit_points_rack") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[0]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get(disabled ? "dialog.disable_edit_points" : "dialog.enable_edit_points"));
                    dialog.setVisible(true);
                    if (optionPane.getValue() == options[0]) {
                        if (rb1.isSelected()) {
                            final LockEditPointsCommand c = new LockEditPointsCommand(r);
                            SceneManager.getTaskManager().update(() -> {
                                r.setLockEdit(disabled);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 0;
                        } else if (rb2.isSelected()) {
                            final Foundation foundation = r.getTopContainer();
                            final LockEditPointsOnFoundationCommand c = new LockEditPointsOnFoundationCommand(foundation, r.getClass());
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setLockEditForClass(disabled, r.getClass());
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 1;
                        } else if (rb3.isSelected()) {
                            final LockEditPointsForClassCommand c = new LockEditPointsForClassCommand(r);
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setLockEditForClass(disabled, r.getClass());
                                return null;
                            });
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
                if (!(selectedPart instanceof Rack)) {
                    return;
                }
                final Rack rack = (Rack) selectedPart;
                final ShowSunBeamCommand c = new ShowSunBeamCommand(rack);
                rack.setSunBeamVisible(cbmiDrawSunBeam.isSelected());
                SceneManager.getTaskManager().update(() -> {
                    rack.drawSunBeam();
                    rack.draw();
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
                Scene.getInstance().setEdited(true);
            });

            final JMenu labelMenu = new JMenu(I18n.get("menu.label"));

            final JCheckBoxMenuItem miLabelNone = new JCheckBoxMenuItem(I18n.get("label.none"), true);
            miLabelNone.addActionListener(e -> {
                if (miLabelNone.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Rack) {
                        final Rack r = (Rack) selectedPart;
                        final SetRackLabelCommand c = new SetRackLabelCommand(r);
                        r.clearLabels();
                        SceneManager.getTaskManager().update(() -> {
                            r.draw();
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
                if (selectedPart instanceof Rack) {
                    final Rack r = (Rack) selectedPart;
                    final SetRackLabelCommand c = new SetRackLabelCommand(r);
                    r.setLabelCustom(miLabelCustom.isSelected());
                    if (r.getLabelCustom()) {
                        r.setLabelCustomText(JOptionPane.showInputDialog(MainFrame.getInstance(), I18n.get("dialog.custom_text"), r.getLabelCustomText()));
                    }
                    SceneManager.getTaskManager().update(() -> {
                        r.draw();
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
                if (selectedPart instanceof Rack) {
                    final Rack r = (Rack) selectedPart;
                    final SetRackLabelCommand c = new SetRackLabelCommand(r);
                    r.setLabelId(miLabelId.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        r.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelId);

            final JCheckBoxMenuItem miLabelCellEfficiency = new JCheckBoxMenuItem(I18n.get("label.cell_efficiency"));
            miLabelCellEfficiency.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Rack) {
                    final Rack r = (Rack) selectedPart;
                    final SetRackLabelCommand c = new SetRackLabelCommand(r);
                    r.setLabelCellEfficiency(miLabelCellEfficiency.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        r.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelCellEfficiency);

            final JCheckBoxMenuItem miLabelTiltAngle = new JCheckBoxMenuItem(I18n.get("label.tilt_angle"));
            miLabelTiltAngle.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Rack) {
                    final Rack r = (Rack) selectedPart;
                    final SetRackLabelCommand c = new SetRackLabelCommand(r);
                    r.setLabelTiltAngle(miLabelTiltAngle.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        r.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelTiltAngle);

            final JCheckBoxMenuItem miLabelTracker = new JCheckBoxMenuItem(I18n.get("label.tracker"));
            miLabelTracker.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Rack) {
                    final Rack r = (Rack) selectedPart;
                    final SetRackLabelCommand c = new SetRackLabelCommand(r);
                    r.setLabelTracker(miLabelTracker.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        r.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelTracker);

            final JCheckBoxMenuItem miLabelEnergyOutput = new JCheckBoxMenuItem(I18n.get("label.energy_output"));
            miLabelEnergyOutput.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Rack) {
                    final Rack r = (Rack) selectedPart;
                    final SetRackLabelCommand c = new SetRackLabelCommand(r);
                    r.setLabelEnergyOutput(miLabelEnergyOutput.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        r.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelEnergyOutput);

            popupMenuForRack = createPopupMenu(true, true, () -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Rack)) {
                    return;
                }
                final HousePart copyBuffer = Scene.getInstance().getCopyBuffer();
                miPaste.setEnabled(copyBuffer instanceof SolarPanel);
                final Rack rack = (Rack) selectedPart;
                switch (rack.getTracker()) {
                    case Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER:
                        Util.selectSilently(miAltazimuthDualAxisTracker, true);
                        break;
                    case Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER:
                        Util.selectSilently(miHorizontalSingleAxisTracker, true);
                        break;
                    case Trackable.VERTICAL_SINGLE_AXIS_TRACKER:
                        Util.selectSilently(miVerticalSingleAxisTracker, true);
                        break;
                    case Trackable.TILTED_SINGLE_AXIS_TRACKER:
                        Util.selectSilently(miTiltedSingleAxisTracker, true);
                        break;
                    case Trackable.NO_TRACKER:
                        Util.selectSilently(miNoTracker, true);
                        break;
                }
                miAltazimuthDualAxisTracker.setEnabled(true);
                miHorizontalSingleAxisTracker.setEnabled(true);
                miVerticalSingleAxisTracker.setEnabled(true);
                if (rack.getContainer() instanceof Roof) {
                    final Roof roof = (Roof) rack.getContainer();
                    final boolean flat = Util.isZero(roof.getHeight());
                    miAltazimuthDualAxisTracker.setEnabled(flat);
                    miHorizontalSingleAxisTracker.setEnabled(flat);
                    miVerticalSingleAxisTracker.setEnabled(flat);
                }
                if (rack.getTracker() != Trackable.NO_TRACKER) {
                    miFixedTiltAngle.setEnabled(rack.getTracker() == Trackable.VERTICAL_SINGLE_AXIS_TRACKER || rack.getTracker() == Trackable.TILTED_SINGLE_AXIS_TRACKER); // vertical and tilted single-axis trackers can adjust the tilt angle
                    miSeasonalTiltAngle.setEnabled(miFixedTiltAngle.isEnabled());
                    miAzimuth.setEnabled(rack.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER && rack.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER); // any tracker that will alter the azimuth angle should disable the menu item
                    miRotate.setEnabled(miAzimuth.isEnabled());
                } else {
                    miFixedTiltAngle.setEnabled(true);
                    miSeasonalTiltAngle.setEnabled(true);
                    miAzimuth.setEnabled(true);
                    miRotate.setEnabled(true);
                    miPoleHeight.setEnabled(true);
                    miPoleSpacing.setEnabled(true);
                    if (rack.getContainer() instanceof Roof) {
                        final Roof roof = (Roof) rack.getContainer();
                        if (roof.getHeight() > 0) {
                            miFixedTiltAngle.setEnabled(false);
                            miSeasonalTiltAngle.setEnabled(false);
                            miAzimuth.setEnabled(false);
                            miPoleHeight.setEnabled(false);
                            miPoleSpacing.setEnabled(false);
                            miRotate.setEnabled(false);
                        }
                    }
                }
                Util.selectSilently(cbmiDisableEditPoints, rack.getLockEdit());
                Util.selectSilently(cbmiDrawSunBeam, rack.isSunBeamVisible());
                Util.selectSilently(miLabelNone, !rack.isLabelVisible());
                Util.selectSilently(miLabelCustom, rack.getLabelCustom());
                Util.selectSilently(miLabelId, rack.getLabelId());
                Util.selectSilently(miLabelCellEfficiency, rack.getLabelCellEfficiency());
                Util.selectSilently(miLabelTiltAngle, rack.getLabelTiltAngle());
                Util.selectSilently(miLabelTracker, rack.getLabelTracker());
                Util.selectSilently(miLabelEnergyOutput, rack.getLabelEnergyOutput());
                final boolean isCustom = I18n.get("model.custom").equals(rack.getSolarPanel().getModelName());
                miSolarPanelCellEfficiency.setEnabled(isCustom);
                miSolarPanelCellType.setEnabled(isCustom);
                miSolarPanelColor.setEnabled(isCustom);
                miSolarPanelSize.setEnabled(isCustom);
                miSolarPanelShadeTolerance.setEnabled(isCustom);
                miSolarPanelNoct.setEnabled(isCustom);
                miSolarPanelPmaxTc.setEnabled(isCustom);
            });

            popupMenuForRack.add(miPaste);
            popupMenuForRack.add(miClear);
            popupMenuForRack.addSeparator();
            popupMenuForRack.add(miSolarPanels);
            popupMenuForRack.add(solarPanelMenu);
            popupMenuForRack.addSeparator();
            popupMenuForRack.add(miFixedTiltAngle);
            popupMenuForRack.add(miSeasonalTiltAngle);
            popupMenuForRack.add(miAzimuth);
            popupMenuForRack.add(miRotate);
            popupMenuForRack.add(miRackWidth);
            popupMenuForRack.add(miRackLength);
            popupMenuForRack.add(miPoleHeight);
            popupMenuForRack.add(miPoleSpacing);
            popupMenuForRack.add(trackerMenu);
            popupMenuForRack.addSeparator();
            popupMenuForRack.add(cbmiDisableEditPoints);
            popupMenuForRack.add(cbmiDrawSunBeam);
            popupMenuForRack.add(labelMenu);
            popupMenuForRack.addSeparator();

            JMenuItem mi = new JMenuItem(I18n.get("menu.daily_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof Rack) {
                    new PvDailyAnalysis().show();
                }
            });
            popupMenuForRack.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof Rack) {
                    new PvAnnualAnalysis().show();
                }
            });
            popupMenuForRack.add(mi);

        }

        return popupMenuForRack;

    }

}