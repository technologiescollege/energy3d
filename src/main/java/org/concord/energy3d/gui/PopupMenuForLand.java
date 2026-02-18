package org.concord.energy3d.gui;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Human;
import org.concord.energy3d.model.Tree;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.AnnualGraph;
import org.concord.energy3d.undo.ChangeBackgroundAlbedoCommand;
import org.concord.energy3d.undo.ChangeGroundThermalDiffusivityCommand;
import org.concord.energy3d.undo.ChangeSnowReflectionFactorCommand;
import org.concord.energy3d.util.Config;
import org.concord.energy3d.util.FileChooser;
import org.concord.energy3d.util.SpringUtilities;
import org.concord.energy3d.util.Util;
import org.concord.energy3d.util.I18n;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;

class PopupMenuForLand extends PopupMenuFactory {

    private static JPopupMenu popupMenuForLand;

    static JPopupMenu getPopupMenu(final MouseEvent mouseEvent) {

        if (mouseEvent.isShiftDown()) {
            SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().pasteToPickedLocationOnLand();
                return null;
            });
            Scene.getInstance().setEdited(true);
            return null;
        }

        if (popupMenuForLand == null) {

            final JMenuItem miInfo = new JMenuItem(I18n.get("menu.land"));
            miInfo.setEnabled(false);
            miInfo.setOpaque(true);
            miInfo.setBackground(Config.isMac() ? Color.DARK_GRAY : Color.GRAY);
            miInfo.setForeground(Color.WHITE);

            final JMenuItem miPaste = new JMenuItem(I18n.get("menu.paste"));
            miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            miPaste.addActionListener(e -> SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().pasteToPickedLocationOnLand();
                return null;
            }));

            final JMenuItem miRemoveAllTrees = new JMenuItem(I18n.get("menu.remove_all_trees"));
            miRemoveAllTrees.addActionListener(e -> Scene.getInstance().removeAllTrees());

            final JMenuItem miRemoveAllHumans = new JMenuItem(I18n.get("menu.remove_all_humans"));
            miRemoveAllHumans.addActionListener(e -> Scene.getInstance().removeAllHumans());

            final JMenuItem miRemoveAllBuildings = new JMenuItem(I18n.get("menu.remove_all_foundations"));
            miRemoveAllBuildings.addActionListener(e -> Scene.getInstance().removeAllFoundations());

            final JMenuItem miImportEnergy3D = new JMenuItem(I18n.get("menu.import"));
            miImportEnergy3D.setToolTipText(I18n.get("tooltip.import_energy3d"));
            miImportEnergy3D.addActionListener(e -> MainFrame.getInstance().importFile());

            final JMenuItem miImportCollada = new JMenuItem(I18n.get("menu.import_collada"));
            miImportCollada.setToolTipText(I18n.get("tooltip.import_collada"));
            miImportCollada.addActionListener(e -> MainFrame.getInstance().importColladaFile());

            final JMenu miImportPrefabMenu = new JMenu(I18n.get("menu.import_prefab"));
            addPrefabMenuItem(I18n.get("prefab.back_hip_roof_porch"), "prefabs/back-hip-roof-porch.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.balcony"), "prefabs/balcony1.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.bell_tower"), "prefabs/bell-tower.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.box"), "prefabs/box.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.chimney"), "prefabs/chimney.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.connecting_porch"), "prefabs/connecting-porch.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.cylinder_tower"), "prefabs/cylinder-tower.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.fence"), "prefabs/fence1.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.flat_top_porch"), "prefabs/flat-top-porch.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.fountain"), "prefabs/fountain.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.front_door_overhang"), "prefabs/front-door-overhang.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.gable_dormer"), "prefabs/gable-dormer.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.hexagonal_gazebo"), "prefabs/hexagonal-gazebo.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.hexagonal_tower"), "prefabs/hexagonal-tower.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.lighthouse"), "prefabs/lighthouse.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.octagonal_tower"), "prefabs/octagonal-tower.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.round_tower"), "prefabs/round-tower.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.shed_dormer"), "prefabs/shed-dormer.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.solarium"), "prefabs/solarium1.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.square_tower"), "prefabs/square-tower.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.stair"), "prefabs/stair1.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.tall_front_door_overhang"), "prefabs/tall-front-door-overhang.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.temple_front"), "prefabs/temple-front.ng3", miImportPrefabMenu);
            addPrefabMenuItem(I18n.get("prefab.waterfront_deck"), "prefabs/waterfront-deck.ng3", miImportPrefabMenu);

            final JMenuItem miAlbedo = new JMenuItem(I18n.get("menu.albedo"));
            miAlbedo.addActionListener(e -> {
                final String title = "<html>" + I18n.get("title.background_albedo") + "</html>";
                while (true) {
                    final String newValue = JOptionPane.showInputDialog(MainFrame.getInstance(), title, Scene.getInstance().getGround().getAlbedo());
                    if (newValue == null) {
                        break;
                    } else {
                        try {
                            final double val = Double.parseDouble(newValue);
                            if (val < 0 || val > 1) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.albedo_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                if (val != Scene.getInstance().getGround().getAlbedo()) {
                                    final ChangeBackgroundAlbedoCommand c = new ChangeBackgroundAlbedoCommand();
                                    Scene.getInstance().getGround().setAlbedo(val);
                                    updateAfterEdit();
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                break;
                            }
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", newValue), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            final JMenuItem miSnowReflection = new JMenuItem(I18n.get("menu.snow_reflection"));
            miSnowReflection.addActionListener(e -> {
                final JPanel gui = new JPanel(new BorderLayout());
                final String title = "<html>" + I18n.get("title.snow_reflection") + "</html>";
                gui.add(new JLabel(title), BorderLayout.NORTH);
                final JPanel inputPanel = new JPanel(new SpringLayout());
                inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                gui.add(inputPanel, BorderLayout.CENTER);
                final JTextField[] fields = new JTextField[12];
                for (int i = 0; i < 12; i++) {
                    final JLabel l = new JLabel(AnnualGraph.getThreeLetterMonth()[i] + ": ", JLabel.TRAILING);
                    inputPanel.add(l);
                    fields[i] = new JTextField(threeDecimalsFormat.format(Scene.getInstance().getGround().getSnowReflectionFactor(i)), 5);
                    l.setLabelFor(fields[i]);
                    inputPanel.add(fields[i]);
                }
                SpringUtilities.makeCompactGrid(inputPanel, 12, 2, 6, 6, 6, 6);
                while (true) {
                    if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), gui, I18n.get("dialog.snow_reflection_factor"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.CANCEL_OPTION) {
                        break;
                    }
                    boolean pass = true;
                    final double[] val = new double[12];
                    for (int i = 0; i < 12; i++) {
                        try {
                            val[i] = Double.parseDouble(fields[i].getText());
                            if (val[i] < 0 || val[i] > 0.2) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.snow_reflection_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                pass = false;
                            }
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", fields[i].getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            pass = false;
                        }
                    }
                    if (pass) {
                        final ChangeSnowReflectionFactorCommand c = new ChangeSnowReflectionFactorCommand();
                        for (int i = 0; i < 12; i++) {
                            Scene.getInstance().getGround().setSnowReflectionFactor(val[i], i);
                        }
                        updateAfterEdit();
                        SceneManager.getInstance().getUndoManager().addEdit(c);
                        break;
                    }
                }
            });

            final JMenuItem miThermalDiffusivity = new JMenuItem(I18n.get("menu.ground_thermal_diffusivity"));
            miThermalDiffusivity.addActionListener(e -> {
                final String title = "<html>" + I18n.get("title.ground_thermal_diffusivity") + "</html>";
                while (true) {
                    final String newValue = JOptionPane.showInputDialog(MainFrame.getInstance(), title, Scene.getInstance().getGround().getThermalDiffusivity());
                    if (newValue == null) {
                        break;
                    } else {
                        try {
                            final double val = Double.parseDouble(newValue);
                            if (val <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.ground_thermal_diffusivity_positive"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                if (val != Scene.getInstance().getGround().getThermalDiffusivity()) {
                                    final ChangeGroundThermalDiffusivityCommand c = new ChangeGroundThermalDiffusivityCommand();
                                    Scene.getInstance().getGround().setThermalDiffusivity(val);
                                    updateAfterEdit();
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                break;
                            }
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", newValue), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            final JMenuItem miClearImage = new JMenuItem(I18n.get("menu.clear_image"));
            final JMenuItem miRescaleImage = new JMenuItem(I18n.get("menu.rescale_image"));
            final JCheckBoxMenuItem miShowImage = new JCheckBoxMenuItem(I18n.get("menu.show_image"));

            final JMenu groundImageMenu = new JMenu(I18n.get("menu.ground_image"));
            groundImageMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                    miShowImage.setEnabled(true);
                    miClearImage.setEnabled(true);
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    miShowImage.setEnabled(true);
                    miClearImage.setEnabled(true);
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    final boolean hasGroundImage = Scene.getInstance().isGroundImageEnabled();
                    miShowImage.setEnabled(hasGroundImage);
                    miClearImage.setEnabled(hasGroundImage);
                    Util.selectSilently(miShowImage, SceneManager.getInstance().getGroundImageLand().isVisible());
                }
            });

            final JMenuItem miUseEarthView = new JMenuItem(I18n.get("menu.use_image_earth_view"));
            miUseEarthView.addActionListener(e -> new MapDialog(MainFrame.getInstance()).setVisible(true));
            groundImageMenu.add(miUseEarthView);

            final JMenuItem miUseImageFile = new JMenuItem(I18n.get("menu.use_image_file"));
            miUseImageFile.addActionListener(e -> {
                final File file = FileChooser.getInstance().showDialog(".png", FileChooser.pngFilter, false);
                if (file == null) {
                    return;
                }
                SceneManager.getTaskManager().update(() -> {
                    try {
                        Scene.getInstance().setGroundImage(ImageIO.read(file), 1);
                        Scene.getInstance().setGroundImageEarthView(false);
                    } catch (final Throwable t) {
                        t.printStackTrace();
                        EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.getInstance(), t.getMessage(), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE));
                    }
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
            groundImageMenu.add(miUseImageFile);
            groundImageMenu.addSeparator();

            miRescaleImage.addActionListener(e -> {
                final String title = I18n.get("title.scale_ground_image");
                while (true) {
                    final String newValue = JOptionPane.showInputDialog(MainFrame.getInstance(), title, Scene.getInstance().getGroundImageScale());
                    if (newValue == null) {
                        break;
                    } else {
                        try {
                            final double val = Double.parseDouble(newValue);
                            if (val <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.scaling_factor_positive"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                // final RescaleGroundImageCommand c = new RescaleGroundImageCommand();
                                SceneManager.getTaskManager().update(() -> {
                                    Scene.getInstance().setGroundImageScale(val);
                                    return null;
                                });
                                // SceneManager.getInstance().getUndoManager().addEdit(c);
                                break;
                            }
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", newValue), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                Scene.getInstance().setEdited(true);
            });
            groundImageMenu.add(miRescaleImage);

            miClearImage.addActionListener(e -> {
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setGroundImage(null, 1);
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
            groundImageMenu.add(miClearImage);

            miShowImage.addItemListener(e -> {
                final boolean b = miShowImage.isSelected();
                SceneManager.getInstance().getGroundImageLand().setVisible(b);
                Scene.getInstance().setShowGroundImage(b);
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().refresh();
            });
            groundImageMenu.add(miShowImage);

            popupMenuForLand = new JPopupMenu();
            popupMenuForLand.setInvoker(MainPanel.getInstance().getCanvasPanel());
            popupMenuForLand.addPopupMenuListener(new PopupMenuListener() {

                @Override
                public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                    final HousePart copyBuffer = Scene.getInstance().getCopyBuffer();
                    miPaste.setEnabled(copyBuffer instanceof Tree || copyBuffer instanceof Human || copyBuffer instanceof Foundation);
                }

                @Override
                public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                }

                @Override
                public void popupMenuCanceled(final PopupMenuEvent e) {
                }

            });

            popupMenuForLand.add(miInfo);
            // popupMenuForLand.addSeparator();
            popupMenuForLand.add(miPaste);
            popupMenuForLand.add(miRemoveAllTrees);
            popupMenuForLand.add(miRemoveAllHumans);
            popupMenuForLand.add(miRemoveAllBuildings);
            popupMenuForLand.addSeparator();
            popupMenuForLand.add(miImportEnergy3D);
            popupMenuForLand.add(miImportCollada);
            popupMenuForLand.add(miImportPrefabMenu);
            popupMenuForLand.addSeparator();
            popupMenuForLand.add(groundImageMenu);
            popupMenuForLand.add(colorAction);
            popupMenuForLand.add(miAlbedo);
            popupMenuForLand.add(miSnowReflection);
            popupMenuForLand.add(miThermalDiffusivity);

        }

        return popupMenuForLand;

    }

}