package org.concord.energy3d.gui;

import org.concord.energy3d.model.*;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.PvAnnualAnalysis;
import org.concord.energy3d.simulation.PvDailyAnalysis;
import org.concord.energy3d.simulation.PvModuleSpecs;
import org.concord.energy3d.simulation.PvModulesData;
import org.concord.energy3d.undo.*;
import org.concord.energy3d.util.SpringUtilities;
import org.concord.energy3d.util.Util;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.I18n;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Map;

class PopupMenuForSolarPanel extends PopupMenuFactory {

    private static JPopupMenu popupMenuForSolarPanel;

    static JPopupMenu getPopupMenu() {

        if (popupMenuForSolarPanel == null) {

            final JMenu trackerMenu = new JMenu(I18n.get("menu.tracker"));
            final JMenu shadeToleranceMenu = new JMenu(I18n.get("menu.shade_tolerance"));

            final ButtonGroup shadeToleranceButtonGroup = new ButtonGroup();

            final JRadioButtonMenuItem miHighTolerance = new JRadioButtonMenuItem(I18n.get("menu.high_tolerance"));
            shadeToleranceButtonGroup.add(miHighTolerance);
            miHighTolerance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final String title = "<html>" + I18n.get("title.choose_shade_tolerance", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.high_shade_tolerance") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.high_shade_tolerance"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getShadeTolerance() != SolarPanel.HIGH_SHADE_TOLERANCE;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetShadeToleranceCommand c = new SetShadeToleranceCommand(sp);
                                    sp.setShadeTolerance(SolarPanel.HIGH_SHADE_TOLERANCE);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getShadeTolerance() != SolarPanel.HIGH_SHADE_TOLERANCE) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetShadeToleranceForSolarPanelsOnFoundationCommand c = new SetShadeToleranceForSolarPanelsOnFoundationCommand(foundation);
                                    foundation.setShadeToleranceForSolarPanels(SolarPanel.HIGH_SHADE_TOLERANCE);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getShadeTolerance() != SolarPanel.HIGH_SHADE_TOLERANCE) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetShadeToleranceForAllSolarPanelsCommand c = new SetShadeToleranceForAllSolarPanelsCommand();
                                    Scene.getInstance().setShadeToleranceForAllSolarPanels(SolarPanel.HIGH_SHADE_TOLERANCE);
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

            final JRadioButtonMenuItem miPartialTolerance = new JRadioButtonMenuItem(I18n.get("menu.partial_tolerance"), true);
            shadeToleranceButtonGroup.add(miPartialTolerance);
            miPartialTolerance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final String title = "<html>" + I18n.get("title.choose_shade_tolerance", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.partial_shade_tolerance") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.partial_shade_tolerance"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getShadeTolerance() != SolarPanel.PARTIAL_SHADE_TOLERANCE;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetShadeToleranceCommand c = new SetShadeToleranceCommand(sp);
                                    sp.setShadeTolerance(SolarPanel.PARTIAL_SHADE_TOLERANCE);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getShadeTolerance() != SolarPanel.PARTIAL_SHADE_TOLERANCE) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetShadeToleranceForSolarPanelsOnFoundationCommand c = new SetShadeToleranceForSolarPanelsOnFoundationCommand(foundation);
                                    foundation.setShadeToleranceForSolarPanels(SolarPanel.PARTIAL_SHADE_TOLERANCE);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getShadeTolerance() != SolarPanel.PARTIAL_SHADE_TOLERANCE) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetShadeToleranceForAllSolarPanelsCommand c = new SetShadeToleranceForAllSolarPanelsCommand();
                                    Scene.getInstance().setShadeToleranceForAllSolarPanels(SolarPanel.PARTIAL_SHADE_TOLERANCE);
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

            final JRadioButtonMenuItem miNoTolerance = new JRadioButtonMenuItem(I18n.get("menu.no_tolerance"));
            shadeToleranceButtonGroup.add(miNoTolerance);
            miNoTolerance.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final String title = "<html>" + I18n.get("title.choose_shade_tolerance", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.no_shade_tolerance") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.no_shade_tolerance"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getShadeTolerance() != SolarPanel.NO_SHADE_TOLERANCE;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetShadeToleranceCommand c = new SetShadeToleranceCommand(sp);
                                    sp.setShadeTolerance(SolarPanel.NO_SHADE_TOLERANCE);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getShadeTolerance() != SolarPanel.NO_SHADE_TOLERANCE) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetShadeToleranceForSolarPanelsOnFoundationCommand c = new SetShadeToleranceForSolarPanelsOnFoundationCommand(foundation);
                                    foundation.setShadeToleranceForSolarPanels(SolarPanel.NO_SHADE_TOLERANCE);
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getShadeTolerance() != SolarPanel.NO_SHADE_TOLERANCE) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetShadeToleranceForAllSolarPanelsCommand c = new SetShadeToleranceForAllSolarPanelsCommand();
                                    Scene.getInstance().setShadeToleranceForAllSolarPanels(SolarPanel.NO_SHADE_TOLERANCE);
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

            final ButtonGroup trackerButtonGroup = new ButtonGroup();

            final JRadioButtonMenuItem miNoTracker = new JRadioButtonMenuItem(I18n.get("menu.no_tracker"), true);
            trackerButtonGroup.add(miNoTracker);
            miNoTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final String title = "<html>" + I18n.get("title.remove_tracker", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.no_tracker") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.no_tracker"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getTracker() != Trackable.NO_TRACKER;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarTrackerCommand c = new SetSolarTrackerCommand(sp, I18n.get("tracker.no_tracker"));
                                    SceneManager.getTaskManager().update(() -> {
                                        sp.setTracker(Trackable.NO_TRACKER);
                                        sp.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getTracker() != Trackable.NO_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, sp,
                                            I18n.get("tracker.no_tracker_all_on_foundation"));
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setTrackerForSolarPanels(Trackable.NO_TRACKER);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getTracker() != Trackable.NO_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(sp, I18n.get("tracker.no_tracker_all"));
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setTrackerForAllSolarPanels(Trackable.NO_TRACKER);
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

            final JRadioButtonMenuItem miHorizontalSingleAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.horizontal_single_axis_tracker"));
            trackerButtonGroup.add(miHorizontalSingleAxisTracker);
            miHorizontalSingleAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.horizontal_single_axis_tracker"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getTracker() != Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarTrackerCommand c = new SetSolarTrackerCommand(sp, I18n.get("tracker.horizontal_single_axis"));
                                    SceneManager.getTaskManager().update(() -> {
                                        sp.setTracker(SolarPanel.HORIZONTAL_SINGLE_AXIS_TRACKER);
                                        sp.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getTracker() != Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, sp,
                                            I18n.get("tracker.horizontal_single_axis_for_all_on_foundation"));
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setTrackerForSolarPanels(SolarPanel.HORIZONTAL_SINGLE_AXIS_TRACKER);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getTracker() != Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(sp, I18n.get("tracker.horizontal_single_axis_for_all"));
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setTrackerForAllSolarPanels(SolarPanel.HORIZONTAL_SINGLE_AXIS_TRACKER);
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

            final JRadioButtonMenuItem miVerticalSingleAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.vertical_single_axis_tracker"));
            trackerButtonGroup.add(miVerticalSingleAxisTracker);
            miVerticalSingleAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.vertical_single_axis_tracker"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarTrackerCommand c = new SetSolarTrackerCommand(sp, I18n.get("tracker.vertical_single_axis"));
                                    SceneManager.getTaskManager().update(() -> {
                                        sp.setTracker(SolarPanel.VERTICAL_SINGLE_AXIS_TRACKER);
                                        sp.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, sp,
                                            I18n.get("tracker.vertical_single_axis_for_all_on_foundation"));
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setTrackerForSolarPanels(SolarPanel.VERTICAL_SINGLE_AXIS_TRACKER);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(sp, I18n.get("tracker.vertical_single_axis_for_all"));
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setTrackerForAllSolarPanels(SolarPanel.VERTICAL_SINGLE_AXIS_TRACKER);
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

            final JRadioButtonMenuItem miAltazimuthDualAxisTracker = new JRadioButtonMenuItem(I18n.get("menu.altazimuth_dual_axis_tracker"));
            trackerButtonGroup.add(miAltazimuthDualAxisTracker);
            miAltazimuthDualAxisTracker.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, panel}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.altazimuth_dual_axis_tracker"));
                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            boolean changed = sp.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER;
                            if (rb1.isSelected()) {
                                if (changed) {
                                    final SetSolarTrackerCommand c = new SetSolarTrackerCommand(sp, I18n.get("tracker.dual_axis"));
                                    SceneManager.getTaskManager().update(() -> {
                                        sp.setTracker(SolarPanel.ALTAZIMUTH_DUAL_AXIS_TRACKER);
                                        sp.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final Foundation foundation = sp.getTopContainer();
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (x.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersOnFoundationCommand c = new SetSolarTrackersOnFoundationCommand(foundation, sp,
                                            I18n.get("tracker.dual_axis_for_all_on_foundation"));
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setTrackerForSolarPanels(SolarPanel.ALTAZIMUTH_DUAL_AXIS_TRACKER);
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (x.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final SetSolarTrackersForAllCommand c = new SetSolarTrackersForAllCommand(sp, I18n.get("tracker.dual_axis_for_all"));
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setTrackerForAllSolarPanels(SolarPanel.ALTAZIMUTH_DUAL_AXIS_TRACKER);
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

            final JMenu orientationMenu = new JMenu(I18n.get("menu.orientation"));
            final ButtonGroup orientationGroup = new ButtonGroup();

            final JRadioButtonMenuItem rbmiLandscape = new JRadioButtonMenuItem(I18n.get("orientation.landscape"));
            rbmiLandscape.addActionListener(e -> {
                if (rbmiLandscape.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel s = (SolarPanel) selectedPart;
                    if (!s.isRotated()) {
                        final RotateSolarPanelCommand c = new RotateSolarPanelCommand(s);
                        SceneManager.getTaskManager().update(() -> {
                            s.setRotated(true);
                            s.draw();
                            SceneManager.getInstance().refresh();
                            return null;
                        });
                        SceneManager.getInstance().getUndoManager().addEdit(c);
                        updateAfterEdit();
                    }
                }
            });
            orientationMenu.add(rbmiLandscape);
            orientationGroup.add(rbmiLandscape);

            final JRadioButtonMenuItem rbmiPortrait = new JRadioButtonMenuItem(I18n.get("orientation.portrait"), true);
            rbmiPortrait.addActionListener(e -> {
                if (rbmiPortrait.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel s = (SolarPanel) selectedPart;
                    if (s.isRotated()) {
                        final RotateSolarPanelCommand c = new RotateSolarPanelCommand(s);
                        SceneManager.getTaskManager().update(() -> {
                            s.setRotated(false);
                            s.draw();
                            SceneManager.getInstance().refresh();
                            return null;
                        });
                        SceneManager.getInstance().getUndoManager().addEdit(c);
                        updateAfterEdit();
                    }
                }
            });
            orientationMenu.add(rbmiPortrait);
            orientationGroup.add(rbmiPortrait);

            final JMenu labelMenu = new JMenu(I18n.get("menu.label"));

            final JCheckBoxMenuItem miLabelNone = new JCheckBoxMenuItem(I18n.get("label.none"), true);
            miLabelNone.addActionListener(e -> {
                if (miLabelNone.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof SolarPanel) {
                        final SolarPanel s = (SolarPanel) selectedPart;
                        final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                        s.clearLabels();
                        SceneManager.getTaskManager().update(() -> {
                            s.draw();
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
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelCustom(miLabelCustom.isSelected());
                    if (s.getLabelCustom()) {
                        s.setLabelCustomText(JOptionPane.showInputDialog(MainFrame.getInstance(), I18n.get("dialog.custom_text"), s.getLabelCustomText()));
                    }
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
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
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelId(miLabelId.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelId);

            final JCheckBoxMenuItem miLabelModelName = new JCheckBoxMenuItem(I18n.get("label.model"));
            miLabelModelName.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelModelName(miLabelModelName.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelModelName);

            final JCheckBoxMenuItem miLabelCellEfficiency = new JCheckBoxMenuItem(I18n.get("label.cell_efficiency"));
            miLabelCellEfficiency.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelCellEfficiency(miLabelCellEfficiency.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
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
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelTiltAngle(miLabelTiltAngle.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
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
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelTracker(miLabelTracker.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
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
                if (selectedPart instanceof SolarPanel) {
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final SetSolarPanelLabelCommand c = new SetSolarPanelLabelCommand(s);
                    s.setLabelEnergyOutput(miLabelEnergyOutput.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        s.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelEnergyOutput);

            final JMenuItem miTiltAngle = new JMenuItem(I18n.get("menu.tilt_angle"));
            miTiltAngle.addActionListener(e -> SolarPanelTiltAngleChanger.getInstance().change());
            final JMenuItem miAzimuth = new JMenuItem(I18n.get("menu.azimuth"));
            miAzimuth.addActionListener(e -> SolarPanelAzimuthChanger.getInstance().change());

            final JMenuItem miSize = new JMenuItem(I18n.get("menu.size"));
            miSize.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof SolarPanel)) {
                    return;
                }
                final SolarPanel s = (SolarPanel) selectedPart;
                final String partInfo = s.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                final JPanel gui = new JPanel(new BorderLayout(5, 5));
                gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.choose_size_for", partInfo)));
                final JComboBox<String> sizeComboBox = new JComboBox<>(solarPanelNominalSize.getStrings());
                final int nItems = sizeComboBox.getItemCount();
                for (int i = 0; i < nItems; i++) {
                    if (Util.isZero(s.getPanelHeight() - solarPanelNominalSize.getNominalHeights()[i]) && Util.isZero(s.getPanelWidth() - solarPanelNominalSize.getNominalWidths()[i])) {
                        sizeComboBox.setSelectedIndex(i);
                    }
                }
                gui.add(sizeComboBox, BorderLayout.NORTH);
                if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), gui, I18n.get("dialog.set_size"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                final int i = sizeComboBox.getSelectedIndex();
                boolean changed = s.getNumberOfCellsInX() != solarPanelNominalSize.getCellNx()[i] || s.getNumberOfCellsInY() != solarPanelNominalSize.getCellNy()[i];
                if (!changed) {
                    if (Math.abs(s.getPanelWidth() - solarPanelNominalSize.getNominalWidths()[i]) > 0.000001 || Math.abs(s.getPanelHeight() - solarPanelNominalSize.getNominalHeights()[i]) > 0.000001) {
                        changed = true;
                    }
                }
                if (changed) {
                    final ChooseSolarPanelSizeCommand c = new ChooseSolarPanelSizeCommand(s);
                    SceneManager.getTaskManager().update(() -> {
                        s.setPanelWidth(solarPanelNominalSize.getNominalWidths()[i]);
                        s.setPanelHeight(solarPanelNominalSize.getNominalHeights()[i]);
                        s.setNumberOfCellsInX(solarPanelNominalSize.getCellNx()[i]);
                        s.setNumberOfCellsInY(solarPanelNominalSize.getCellNy()[i]);
                        s.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    updateAfterEdit();
                }
            });

            final JMenuItem miPoleHeight = new JMenuItem(I18n.get("menu.pole_height"));
            miPoleHeight.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final Foundation foundation = sp.getTopContainer();
                    final String title = "<html>" + I18n.get("title.pole_height_m", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.only_this_row"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb4 = new JRadioButton(I18n.get("scope.all_solar_panels"));
                    panel.add(rb1);
                    panel.add(rb2);
                    panel.add(rb3);
                    panel.add(rb4);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    bg.add(rb4);
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
                        case 3:
                            rb4.setSelected(true);
                            break;
                    }
                    gui.add(panel, BorderLayout.CENTER);
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(sp.getPoleHeight() * Scene.getInstance().getScale()));
                    gui.add(inputField, BorderLayout.SOUTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_pole_height"));

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
                                    boolean changed = Math.abs(val - sp.getPoleHeight()) > 0.000001;
                                    final double poleHeight = val;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangePoleHeightCommand c = new ChangePoleHeightCommand(sp);
                                            SceneManager.getTaskManager().update(() -> {
                                                sp.setPoleHeight(poleHeight);
                                                sp.draw();
                                                if (sp.checkContainerIntersection()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                                                I18n.get("msg.illegal_pole_height_single"),
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
                                        final List<SolarPanel> row = sp.getRow();
                                        for (final SolarPanel x : row) {
                                            if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleHeightForSolarPanelRowCommand c = new ChangePoleHeightForSolarPanelRowCommand(row);
                                            SceneManager.getTaskManager().update(() -> {
                                                boolean intersected = false;
                                                for (final SolarPanel x : row) {
                                                    x.setPoleHeight(poleHeight);
                                                    x.draw();
                                                    if (x.checkContainerIntersection()) {
                                                        intersected = true;
                                                        break;
                                                    }
                                                }
                                                if (intersected) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                                                I18n.get("msg.illegal_pole_height_row"),
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
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        for (final SolarPanel x : foundation.getSolarPanels()) {
                                            if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarCollectorPoleHeightCommand c = new ChangeFoundationSolarCollectorPoleHeightCommand(foundation, sp.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                foundation.setPoleHeightForSolarPanels(poleHeight);
                                                if (foundation.checkContainerIntersectionForSolarPanels()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                                                I18n.get("msg.illegal_pole_height_multiple"),
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
                                    } else if (rb4.isSelected()) {
                                        for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                            if (Math.abs(val - x.getPoleHeight()) > 0.000001) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                        if (changed) {
                                            final ChangePoleHeightForAllSolarCollectorsCommand c = new ChangePoleHeightForAllSolarCollectorsCommand(sp.getClass());
                                            SceneManager.getTaskManager().update(() -> {
                                                Scene.getInstance().setPoleHeightForAllSolarPanels(poleHeight);
                                                if (Scene.getInstance().checkContainerIntersectionForAllSolarPanels()) {
                                                    EventQueue.invokeLater(() -> {
                                                        JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                                                I18n.get("msg.illegal_pole_height_multiple"),
                                                                I18n.get("msg.illegal_pole_height"), JOptionPane.ERROR_MESSAGE);
                                                        c.undo();
                                                    });
                                                } else {
                                                    EventQueue.invokeLater(() -> SceneManager.getInstance().getUndoManager().addEdit(c));
                                                }
                                                return null;
                                            });
                                        }
                                        selectedScopeIndex = 3;
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
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final boolean disabled = cbmiDisableEditPoint.isSelected();
                    final SolarPanel sp = (SolarPanel) selectedPart;
                    final String partInfo = sp.toString().substring(0, sp.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout(0, 20));
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.SOUTH);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.disable_edit_point") + "<hr></html>";
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[0]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get(disabled ? "dialog.disable_edit_point" : "dialog.enable_edit_point"));
                    dialog.setVisible(true);
                    if (optionPane.getValue() == options[0]) {
                        if (rb1.isSelected()) {
                            final LockEditPointsCommand c = new LockEditPointsCommand(sp);
                            SceneManager.getTaskManager().update(() -> {
                                sp.setLockEdit(disabled);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 0;
                        } else if (rb2.isSelected()) {
                            final Foundation foundation = sp.getTopContainer();
                            final LockEditPointsOnFoundationCommand c = new LockEditPointsOnFoundationCommand(foundation, sp.getClass());
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setLockEditForClass(disabled, sp.getClass());
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                            selectedScopeIndex = 1;
                        } else if (rb3.isSelected()) {
                            final LockEditPointsForClassCommand c = new LockEditPointsForClassCommand(sp);
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setLockEditForClass(disabled, sp.getClass());
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
                if (!(selectedPart instanceof SolarPanel)) {
                    return;
                }
                final SolarPanel sp = (SolarPanel) selectedPart;
                final ShowSunBeamCommand c = new ShowSunBeamCommand(sp);
                sp.setSunBeamVisible(cbmiDrawSunBeam.isSelected());
                SceneManager.getTaskManager().update(() -> {
                    sp.drawSunBeam();
                    sp.draw();
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
                Scene.getInstance().setEdited(true);
            });

            final JMenuItem miCells = new JMenuItem(I18n.get("menu.solar_cells"));
            miCells.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel solarPanel = (SolarPanel) selectedPart;
                    final String title = "<html>" + I18n.get("title.solar_cell_properties_of", selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1)) + "</html>";
                    String footnote = "<html><hr><font size=2><b>" + I18n.get("footnote.solar_cell_efficiency_question") + "</b><br>" + I18n.get("footnote.solar_cell_efficiency_details", SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE) + "<hr>";
                    footnote += "<font size=2>" + I18n.get("footnote.monocrystalline_description");
                    footnote += "<br><font size=2>" + I18n.get("footnote.polycrystalline_description") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.SOUTH);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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

                    final JPanel inputPanel = new JPanel(new SpringLayout());
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    gui.add(inputPanel, BorderLayout.CENTER);
                    JLabel label = new JLabel(I18n.get("label.type") + ": ", JLabel.LEFT);
                    inputPanel.add(label);
                    final JComboBox<String> typeComboBox = new JComboBox<>(new String[]{I18n.get("cell_type.polycrystalline"), I18n.get("cell_type.monocrystalline"), I18n.get("cell_type.thin_film")});
                    typeComboBox.setSelectedIndex(solarPanel.getCellType());
                    label.setLabelFor(typeComboBox);
                    inputPanel.add(typeComboBox);
                    label = new JLabel(I18n.get("label.color") + ": ", JLabel.LEFT);
                    inputPanel.add(label);
                    final JComboBox<String> colorComboBox = new JComboBox<>(new String[]{I18n.get("color.blue"), I18n.get("color.black"), I18n.get("color.gray")});
                    colorComboBox.setSelectedIndex(solarPanel.getColorOption());
                    label.setLabelFor(colorComboBox);
                    inputPanel.add(colorComboBox);
                    label = new JLabel(I18n.get("label.efficiency_percent") + ": ", JLabel.LEFT);
                    inputPanel.add(label);
                    final JTextField efficiencyField = new JTextField(EnergyPanel.TWO_DECIMALS.format(solarPanel.getCellEfficiency() * 100));
                    label.setLabelFor(efficiencyField);
                    inputPanel.add(efficiencyField);
                    SpringUtilities.makeCompactGrid(inputPanel, 3, 2, 6, 6, 6, 6);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_cell_properties"));

                    while (true) {
                        efficiencyField.selectAll();
                        efficiencyField.requestFocusInWindow();
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(efficiencyField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", efficiencyField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE || val > SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_cell_efficiency_range", SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    final int cellType = typeComboBox.getSelectedIndex();
                                    final int colorOption = colorComboBox.getSelectedIndex();
                                    boolean changed = cellType != solarPanel.getCellType() || colorOption != solarPanel.getColorOption() || Math.abs(val * 0.01 - solarPanel.getCellEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeSolarCellPropertiesCommand c = new ChangeSolarCellPropertiesCommand(solarPanel);
                                            solarPanel.setCellEfficiency(val * 0.01);
                                            solarPanel.setCellType(cellType);
                                            solarPanel.setColorOption(colorOption);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = solarPanel.getTopContainer();
                                        if (!changed) {
                                            for (final SolarPanel x : foundation.getSolarPanels()) {
                                                if (cellType != x.getCellType() || colorOption != x.getColorOption() || Math.abs(val * 0.01 - x.getCellEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationSolarCellPropertiesCommand c = new ChangeFoundationSolarCellPropertiesCommand(foundation);
                                            foundation.setSolarCellEfficiency(val * 0.01);
                                            foundation.setCellTypeForSolarPanels(cellType);
                                            foundation.setColorForSolarPanels(colorOption);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                                if (cellType != x.getCellType() || colorOption != x.getColorOption() || Math.abs(val * 0.01 - x.getCellEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeSolarCellPropertiesForAllCommand c = new ChangeSolarCellPropertiesForAllCommand();
                                            Scene.getInstance().setSolarCellEfficiencyForAll(val * 0.01);
                                            Scene.getInstance().setCellTypeForAllSolarPanels(cellType);
                                            Scene.getInstance().setColorForAllSolarPanels(colorOption);
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

            final JMenuItem miTemperatureEffects = new JMenuItem(I18n.get("menu.temperature_effects"));
            miTemperatureEffects.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final SolarPanel solarPanel = (SolarPanel) selectedPart;
                    final String title = "<html>" + I18n.get("title.temperature_effects_of", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.temperature_effects") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    gui.add(panel, BorderLayout.SOUTH);

                    final JPanel inputPanel = new JPanel(new SpringLayout());
                    gui.add(inputPanel, BorderLayout.CENTER);
                    inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

                    JLabel label = new JLabel("<html>" + I18n.get("label.nominal_operating_cell_temperature") + " (&deg;C): ", JLabel.LEFT);
                    inputPanel.add(label);
                    final JTextField noctField = new JTextField(EnergyPanel.TWO_DECIMALS.format(solarPanel.getNominalOperatingCellTemperature()));
                    label.setLabelFor(noctField);
                    inputPanel.add(noctField);
                    label = new JLabel("<html>" + I18n.get("label.temperature_coefficient_pmax") + " (%/&deg;C): ", JLabel.LEFT);
                    inputPanel.add(label);
                    final JTextField pmaxField = new JTextField(EnergyPanel.TWO_DECIMALS.format(solarPanel.getTemperatureCoefficientPmax() * 100));
                    label.setLabelFor(pmaxField);
                    inputPanel.add(pmaxField);
                    SpringUtilities.makeCompactGrid(inputPanel, 2, 2, 6, 6, 6, 6);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.temperature_effects"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            double noct = 0;
                            double pmax = 0;
                            boolean ok = true;
                            try {
                                noct = Double.parseDouble(noctField.getText());
                                pmax = Double.parseDouble(pmaxField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (noct < 33 || noct > 58) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.noct_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else if (pmax < -1 || pmax > 0) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.temperature_coefficient_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(noct - solarPanel.getNominalOperatingCellTemperature()) > 0.000001 || Math.abs(pmax * 0.01 - solarPanel.getTemperatureCoefficientPmax()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final SetTemperatureEffectsCommand c = new SetTemperatureEffectsCommand(solarPanel);
                                            solarPanel.setTemperatureCoefficientPmax(pmax * 0.01);
                                            solarPanel.setNominalOperatingCellTemperature(noct);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = solarPanel.getTopContainer();
                                        if (!changed) {
                                            for (final SolarPanel x : foundation.getSolarPanels()) {
                                                if (Math.abs(noct - x.getNominalOperatingCellTemperature()) > 0.000001 || Math.abs(pmax * 0.01 - x.getTemperatureCoefficientPmax()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetFoundationTemperatureEffectsCommand c = new SetFoundationTemperatureEffectsCommand(foundation);
                                            foundation.setTemperatureCoefficientPmax(pmax * 0.01);
                                            foundation.setNominalOperatingCellTemperature(noct);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                                if (Math.abs(noct - x.getNominalOperatingCellTemperature()) > 0.000001 || Math.abs(pmax * 0.01 - x.getTemperatureCoefficientPmax()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final SetTemperatrureEffectsForAllCommand c = new SetTemperatrureEffectsForAllCommand();
                                            Scene.getInstance().setTemperatureCoefficientPmaxForAll(pmax * 0.01);
                                            Scene.getInstance().setNominalOperatingCellTemperatureForAll(noct);
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

            final JMenuItem miInverterEff = new JMenuItem(I18n.get("menu.inverter_efficiency"));
            miInverterEff.addActionListener(new ActionListener() {

                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final SolarPanel solarPanel = (SolarPanel) selectedPart;
                    final String title = "<html>" + I18n.get("title.inverter_efficiency_percent", partInfo) + "</html>";
                    final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.inverter_efficiency") + "<hr></html>";
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel panel = new JPanel();
                    gui.add(panel, BorderLayout.CENTER);
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                    gui.add(panel, BorderLayout.CENTER);
                    final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(solarPanel.getInverterEfficiency() * 100));
                    gui.add(inputField, BorderLayout.SOUTH);

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
                            double val = 0;
                            boolean ok = true;
                            try {
                                val = Double.parseDouble(inputField.getText());
                            } catch (final NumberFormatException exception) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                ok = false;
                            }
                            if (ok) {
                                if (val < SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE || val >= SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inverter_efficiency_range", SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                } else {
                                    boolean changed = Math.abs(val * 0.01 - solarPanel.getInverterEfficiency()) > 0.000001;
                                    if (rb1.isSelected()) {
                                        if (changed) {
                                            final ChangeInverterEfficiencyCommand c = new ChangeInverterEfficiencyCommand(solarPanel);
                                            solarPanel.setInverterEfficiency(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 0;
                                    } else if (rb2.isSelected()) {
                                        final Foundation foundation = solarPanel.getTopContainer();
                                        if (!changed) {
                                            for (final SolarPanel x : foundation.getSolarPanels()) {
                                                if (Math.abs(val * 0.01 - x.getInverterEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeFoundationInverterEfficiencyCommand c = new ChangeFoundationInverterEfficiencyCommand(foundation);
                                            foundation.setSolarPanelInverterEfficiency(val * 0.01);
                                            SceneManager.getInstance().getUndoManager().addEdit(c);
                                        }
                                        selectedScopeIndex = 1;
                                    } else if (rb3.isSelected()) {
                                        if (!changed) {
                                            for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                                if (Math.abs(val * 0.01 - x.getInverterEfficiency()) > 0.000001) {
                                                    changed = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (changed) {
                                            final ChangeInverterEfficiencyForAllCommand c = new ChangeInverterEfficiencyForAllCommand();
                                            Scene.getInstance().setSolarPanelInverterEfficiencyForAll(val * 0.01);
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

            trackerMenu.add(miNoTracker);
            trackerMenu.add(miHorizontalSingleAxisTracker);
            trackerMenu.add(miVerticalSingleAxisTracker);
            trackerMenu.add(miAltazimuthDualAxisTracker);

            shadeToleranceMenu.add(miNoTolerance);
            shadeToleranceMenu.add(miPartialTolerance);
            shadeToleranceMenu.add(miHighTolerance);

            final JMenuItem miModel = new JMenuItem(I18n.get("menu.model"));
            miModel.addActionListener(new ActionListener() {

                private String modelName;
                private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof SolarPanel)) {
                        return;
                    }
                    final SolarPanel s = (SolarPanel) selectedPart;
                    final Foundation foundation = s.getTopContainer();
                    final String partInfo = s.toString().substring(0, s.toString().indexOf(')') + 1);
                    final Map<String, PvModuleSpecs> modules = PvModulesData.getInstance().getModules();
                    final String[] models = new String[modules.size() + 1];
                    int i = 0;
                    models[i] = I18n.get("model.custom");
                    for (final String key : modules.keySet()) {
                        models[++i] = key;
                    }
                    final PvModuleSpecs specs = s.getPvModuleSpecs();
                    modelName = specs.getModel();
                    final JPanel gui = new JPanel(new BorderLayout(5, 5));
                    gui.setBorder(BorderFactory.createTitledBorder(I18n.get("title.model_for", partInfo)));
                    final JComboBox<String> typeComboBox = new JComboBox<>(models);
                    typeComboBox.setSelectedItem(specs.getModel());
                    typeComboBox.addItemListener(e1 -> {
                        if (e1.getStateChange() == ItemEvent.SELECTED) {
                            modelName = (String) typeComboBox.getSelectedItem();
                        }
                    });
                    gui.add(typeComboBox, BorderLayout.NORTH);
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_solar_panel"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_solar_panels_on_foundation"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_solar_panels"));
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
                                    final ChangeSolarPanelModelCommand c = new ChangeSolarPanelModelCommand(s);
                                    s.setPvModuleSpecs(PvModulesData.getInstance().getModuleSpecs(modelName));
                                    SceneManager.getTaskManager().update(() -> {
                                        s.draw();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : foundation.getSolarPanels()) {
                                        if (!modelName.equals(x.getModelName())) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeFoundationSolarPanelModelCommand c = new ChangeFoundationSolarPanelModelCommand(foundation);
                                    SceneManager.getTaskManager().update(() -> {
                                        foundation.setModelForSolarPanels(PvModulesData.getInstance().getModuleSpecs(modelName));
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                if (!changed) {
                                    for (final SolarPanel x : Scene.getInstance().getAllSolarPanels()) {
                                        if (!modelName.equals(x.getModelName())) {
                                            changed = true;
                                            break;
                                        }
                                    }
                                }
                                if (changed) {
                                    final ChangeModelForAllSolarPanelsCommand c = new ChangeModelForAllSolarPanelsCommand();
                                    SceneManager.getTaskManager().update(() -> {
                                        Scene.getInstance().setModelForAllSolarPanels(PvModulesData.getInstance().getModuleSpecs(modelName));
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

            popupMenuForSolarPanel = createPopupMenu(true, true, () -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof SolarPanel)) {
                    return;
                }
                final SolarPanel sp = (SolarPanel) selectedPart;
                switch (sp.getShadeTolerance()) {
                    case SolarPanel.HIGH_SHADE_TOLERANCE:
                        Util.selectSilently(miHighTolerance, true);
                        break;
                    case SolarPanel.PARTIAL_SHADE_TOLERANCE:
                        Util.selectSilently(miPartialTolerance, true);
                        break;
                    case SolarPanel.NO_SHADE_TOLERANCE:
                        Util.selectSilently(miNoTolerance, true);
                        break;
                }
                Util.selectSilently(cbmiDrawSunBeam, sp.isSunBeamVisible());
                Util.selectSilently(cbmiDisableEditPoint, sp.getLockEdit());
                Util.selectSilently(rbmiLandscape, sp.isRotated());
                Util.selectSilently(rbmiPortrait, !sp.isRotated());
                Util.selectSilently(miLabelNone, !sp.isLabelVisible());
                Util.selectSilently(miLabelCustom, sp.getLabelCustom());
                Util.selectSilently(miLabelId, sp.getLabelId());
                Util.selectSilently(miLabelModelName, sp.getLabelModelName());
                Util.selectSilently(miLabelCellEfficiency, sp.getLabelCellEfficiency());
                Util.selectSilently(miLabelTiltAngle, sp.getLabelTiltAngle());
                Util.selectSilently(miLabelTracker, sp.getLabelTracker());
                Util.selectSilently(miLabelEnergyOutput, sp.getLabelEnergyOutput());

                final PvModuleSpecs pms = sp.getPvModuleSpecs();
                final boolean isCustom = I18n.get("model.custom").equals(pms.getModel());
                miCells.setEnabled(isCustom);
                miSize.setEnabled(isCustom);
                miTemperatureEffects.setEnabled(isCustom);
                shadeToleranceMenu.setEnabled(isCustom);

                switch (sp.getTracker()) {
                    case Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER:
                        Util.selectSilently(miAltazimuthDualAxisTracker, true);
                        break;
                    case Trackable.HORIZONTAL_SINGLE_AXIS_TRACKER:
                        Util.selectSilently(miHorizontalSingleAxisTracker, true);
                        break;
                    case Trackable.VERTICAL_SINGLE_AXIS_TRACKER:
                        Util.selectSilently(miVerticalSingleAxisTracker, true);
                        break;
                    case Trackable.NO_TRACKER:
                        Util.selectSilently(miNoTracker, true);
                        break;
                }
                miAltazimuthDualAxisTracker.setEnabled(true);
                miHorizontalSingleAxisTracker.setEnabled(true);
                miVerticalSingleAxisTracker.setEnabled(true);
                if (sp.getContainer() instanceof Roof) {
                    final Roof roof = (Roof) sp.getContainer();
                    final boolean flat = Util.isZero(roof.getHeight());
                    miAltazimuthDualAxisTracker.setEnabled(flat);
                    miHorizontalSingleAxisTracker.setEnabled(flat);
                    miVerticalSingleAxisTracker.setEnabled(flat);
                } else if (sp.getContainer() instanceof Wall || sp.getContainer() instanceof Rack) {
                    miAltazimuthDualAxisTracker.setEnabled(false);
                    miHorizontalSingleAxisTracker.setEnabled(false);
                    miVerticalSingleAxisTracker.setEnabled(false);
                }
                if (sp.getTracker() != Trackable.NO_TRACKER) {
                    miTiltAngle.setEnabled(sp.getTracker() == Trackable.VERTICAL_SINGLE_AXIS_TRACKER || sp.getTracker() == Trackable.TILTED_SINGLE_AXIS_TRACKER); // vertical and tilted single-axis trackers can adjust the tilt angle
                    miAzimuth.setEnabled(sp.getTracker() != Trackable.ALTAZIMUTH_DUAL_AXIS_TRACKER && sp.getTracker() != Trackable.VERTICAL_SINGLE_AXIS_TRACKER); // any tracker that will alter the azimuth angle should disable the menu item
                } else {
                    miTiltAngle.setEnabled(true);
                    miAzimuth.setEnabled(true);
                    miPoleHeight.setEnabled(true);
                    if (sp.getContainer() instanceof Roof) {
                        final Roof roof = (Roof) sp.getContainer();
                        if (roof.getHeight() > 0) {
                            miTiltAngle.setEnabled(false);
                            miAzimuth.setEnabled(false);
                            miPoleHeight.setEnabled(false);
                        }
                    } else if (sp.getContainer() instanceof Wall || sp.getContainer() instanceof Rack) {
                        miTiltAngle.setEnabled(false);
                        miAzimuth.setEnabled(false);
                        miPoleHeight.setEnabled(false);
                    }
                }
            });

            final JMenuItem miDeleteRow = new JMenuItem(I18n.get("menu.delete_row"));
            miDeleteRow.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof SolarPanel)) {
                    return;
                }
                Scene.getInstance().removeAllSolarPanels(((SolarPanel) selectedPart).getRow()); // already use Task Manager thread
            });
            popupMenuForSolarPanel.add(miDeleteRow);
            popupMenuForSolarPanel.addSeparator();
            popupMenuForSolarPanel.add(miModel);
            popupMenuForSolarPanel.add(miCells);
            popupMenuForSolarPanel.add(miSize);
            popupMenuForSolarPanel.add(miTemperatureEffects);
            popupMenuForSolarPanel.add(shadeToleranceMenu);
            popupMenuForSolarPanel.addSeparator();
            popupMenuForSolarPanel.add(miTiltAngle);
            popupMenuForSolarPanel.add(miAzimuth);
            popupMenuForSolarPanel.add(miPoleHeight);
            popupMenuForSolarPanel.add(miInverterEff);
            popupMenuForSolarPanel.add(orientationMenu);
            popupMenuForSolarPanel.add(trackerMenu);
            popupMenuForSolarPanel.addSeparator();
            popupMenuForSolarPanel.add(cbmiDisableEditPoint);
            popupMenuForSolarPanel.add(cbmiDrawSunBeam);
            popupMenuForSolarPanel.add(labelMenu);
            popupMenuForSolarPanel.addSeparator();

            JMenuItem mi = new JMenuItem(I18n.get("menu.daily_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof SolarPanel) {
                    new PvDailyAnalysis().show();
                }
            });
            popupMenuForSolarPanel.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_yield_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof SolarPanel) {
                    new PvAnnualAnalysis().show();
                }
            });
            popupMenuForSolarPanel.add(mi);

        }

        return popupMenuForSolarPanel;

    }

}