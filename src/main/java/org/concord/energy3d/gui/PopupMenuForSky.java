package org.concord.energy3d.gui;

import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.AnnualEnvironmentalTemperature;
import org.concord.energy3d.simulation.AnnualGraph;
import org.concord.energy3d.simulation.DailyEnvironmentalTemperature;
import org.concord.energy3d.simulation.MonthlySunshineHours;
import org.concord.energy3d.undo.ChangeAtmosphericDustLossCommand;
import org.concord.energy3d.undo.ChangeEnvironmentCommand;
import org.concord.energy3d.util.Config;
import org.concord.energy3d.util.SpringUtilities;
import org.concord.energy3d.util.Util;
import org.concord.energy3d.util.I18n;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ItemEvent;

class PopupMenuForSky extends PopupMenuFactory {

    private static JPopupMenu popupMenuForSky;

    static JPopupMenu getPopupMenu() {

        if (popupMenuForSky == null) {

            final JMenuItem miInfo = new JMenuItem(I18n.get("menu.sky"));
            miInfo.setEnabled(false);
            miInfo.setOpaque(true);
            miInfo.setBackground(Config.isMac() ? Color.DARK_GRAY : Color.GRAY);
            miInfo.setForeground(Color.WHITE);

            final JCheckBoxMenuItem miHeliodon = new JCheckBoxMenuItem(I18n.get("menu.heliodon"));
            miHeliodon.addItemListener(e -> MainPanel.getInstance().getHeliodonButton().doClick());

            final JMenu weatherMenu = new JMenu(I18n.get("menu.weather"));
            JMenuItem mi = new JMenuItem(I18n.get("menu.monthly_sunshine_hours"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    new MonthlySunshineHours().showDialog();
                }
            });
            weatherMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_environmental_temperature"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    new AnnualEnvironmentalTemperature().showDialog();
                }
            });
            weatherMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.daily_environmental_temperature"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    new DailyEnvironmentalTemperature().showDialog();
                }
            });
            weatherMenu.add(mi);

            final JMenu environmentMenu = new JMenu(I18n.get("menu.environment"));
            final ButtonGroup environmentButtonGroup = new ButtonGroup();

            final JRadioButtonMenuItem miDefault = new JRadioButtonMenuItem(I18n.get("environment.default"));
            miDefault.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.DEFAULT_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            environmentButtonGroup.add(miDefault);
            environmentMenu.add(miDefault);

            final JRadioButtonMenuItem miDesert = new JRadioButtonMenuItem(I18n.get("environment.desert"));
            miDesert.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.DESERT_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            environmentButtonGroup.add(miDesert);
            environmentMenu.add(miDesert);

            final JRadioButtonMenuItem miGrassland = new JRadioButtonMenuItem(I18n.get("environment.grassland"));
            miGrassland.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.GRASSLAND_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            environmentButtonGroup.add(miGrassland);
            environmentMenu.add(miGrassland);

            final JRadioButtonMenuItem miForest = new JRadioButtonMenuItem(I18n.get("environment.forest"));
            miForest.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.FOREST_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            environmentButtonGroup.add(miForest);
            environmentMenu.add(miForest);

            environmentMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    Util.selectSilently(miDefault, Scene.getInstance().getEnvironment() == Scene.DEFAULT_THEME);
                    Util.selectSilently(miDesert, Scene.getInstance().getEnvironment() == Scene.DESERT_THEME);
                    Util.selectSilently(miGrassland, Scene.getInstance().getEnvironment() == Scene.GRASSLAND_THEME);
                    Util.selectSilently(miForest, Scene.getInstance().getEnvironment() == Scene.FOREST_THEME);
                }
            });

            final JMenuItem miDustLoss = new JMenuItem(I18n.get("menu.dust_pollen"));
            miDustLoss.addActionListener(e -> {
                final JPanel gui = new JPanel(new BorderLayout());
                final String title = "<html>" + I18n.get("title.soiling_loss_factor") + "</html>";
                gui.add(new JLabel(title), BorderLayout.NORTH);
                final JPanel inputPanel = new JPanel(new SpringLayout());
                inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                gui.add(inputPanel, BorderLayout.CENTER);
                final JTextField[] fields = new JTextField[12];
                for (int i = 0; i < 12; i++) {
                    final JLabel l = new JLabel(AnnualGraph.getThreeLetterMonth()[i] + ": ", JLabel.LEFT);
                    inputPanel.add(l);
                    fields[i] = new JTextField(threeDecimalsFormat.format(Scene.getInstance().getAtmosphere().getDustLoss(i)), 5);
                    l.setLabelFor(fields[i]);
                    inputPanel.add(fields[i]);
                }
                SpringUtilities.makeCompactGrid(inputPanel, 12, 2, 6, 6, 6, 6);
                while (true) {
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), gui, I18n.get("dialog.dust_pollen_loss"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                        break;
                    }
                    boolean pass = true;
                    final double[] val = new double[12];
                    for (int i = 0; i < 12; i++) {
                        try {
                            val[i] = Double.parseDouble(fields[i].getText());
                            if (val[i] < 0 || val[i] > 1) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.dust_pollen_loss_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                pass = false;
                            }
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", fields[i].getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            pass = false;
                        }
                    }
                    if (pass) {
                        boolean changed = false;
                        for (int i = 0; i < 12; i++) {
                            if (Math.abs(Scene.getInstance().getAtmosphere().getDustLoss(i) - val[i]) > 0.000001) {
                                changed = true;
                                break;
                            }
                        }
                        if (changed) {
                            final ChangeAtmosphericDustLossCommand c = new ChangeAtmosphericDustLossCommand();
                            for (int i = 0; i < 12; i++) {
                                Scene.getInstance().getAtmosphere().setDustLoss(val[i], i);
                            }
                            updateAfterEdit();
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        break;
                    }
                }
            });

            popupMenuForSky = new JPopupMenu();
            popupMenuForSky.setInvoker(MainPanel.getInstance().getCanvasPanel());
            popupMenuForSky.addPopupMenuListener(new PopupMenuListener() {

                @Override
                public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                    Util.selectSilently(miHeliodon, MainPanel.getInstance().getHeliodonButton().isSelected());
                }

                @Override
                public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                }

                @Override
                public void popupMenuCanceled(final PopupMenuEvent e) {
                }

            });

            popupMenuForSky.add(miInfo);
            popupMenuForSky.add(miDustLoss);
            popupMenuForSky.add(miHeliodon);
            popupMenuForSky.addSeparator();
            popupMenuForSky.add(weatherMenu);
            popupMenuForSky.add(environmentMenu);

        }

        return popupMenuForSky;

    }

}