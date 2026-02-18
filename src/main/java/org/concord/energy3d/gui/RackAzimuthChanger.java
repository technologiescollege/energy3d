package org.concord.energy3d.gui;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Rack;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.undo.*;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.Util;

import javax.swing.*;
import java.awt.*;

/**
 * @author Charles Xie
 */

public class RackAzimuthChanger {

    private final static RackAzimuthChanger instance = new RackAzimuthChanger();
    private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

    private RackAzimuthChanger() {
    }

    public static RackAzimuthChanger getInstance() {
        return instance;
    }

    public void change() {

        final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
        if (!(selectedPart instanceof Rack)) {
            return;
        }
        final String partInfo = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
        final Rack rack = (Rack) selectedPart;
        final Foundation foundation = rack.getTopContainer();
        final String title = "<html>" + I18n.get("title.azimuth_angle_of", partInfo) + " (&deg;)</html>";
        final String footnote = "<html><hr><font size=2>" + I18n.get("footnote.azimuth_angle") + "<hr></html>";
        final JPanel gui = new JPanel(new BorderLayout());
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
        gui.add(panel, BorderLayout.CENTER);
        double a = rack.getRelativeAzimuth() + foundation.getAzimuth();
        if (a > 360) {
            a -= 360;
        }
        final JTextField inputField = new JTextField(a + "");
        gui.add(inputField, BorderLayout.SOUTH);

        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
        final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.azimuth_angle"));

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
                    a = Double.parseDouble(inputField.getText()) - foundation.getAzimuth();
                } catch (final NumberFormatException exception) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                    ok = false;
                }
                if (ok) {
                    if (a < 0) {
                        a += 360;
                    }
                    boolean changed = a != rack.getRelativeAzimuth();
                    final double azimuth = a;
                    if (rb1.isSelected()) {
                        if (changed) {
                            final ChangeAzimuthCommand c = new ChangeAzimuthCommand(rack);
                            SceneManager.getTaskManager().update(() -> {
                                rack.setRelativeAzimuth(azimuth);
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
                                if (x.getRelativeAzimuth() != a) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final ChangeFoundationRackAzimuthCommand c = new ChangeFoundationRackAzimuthCommand(foundation);
                            SceneManager.getTaskManager().update(() -> {
                                foundation.setAzimuthForRacks(azimuth);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 1;
                    } else if (rb3.isSelected()) {
                        if (!changed) {
                            for (final Rack x : Scene.getInstance().getAllRacks()) {
                                if (x.getRelativeAzimuth() != a) {
                                    changed = true;
                                    break;
                                }
                            }
                        }
                        if (changed) {
                            final ChangeAzimuthForAllRacksCommand c = new ChangeAzimuthForAllRacksCommand();
                            SceneManager.getTaskManager().update(() -> {
                                Scene.getInstance().setAzimuthForAllRacks(azimuth);
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                        selectedScopeIndex = 2;
                    }
                    if (changed) {
                        PopupMenuFactory.updateAfterEdit();
                    }
                    if (choice == options[0]) {
                        break;
                    }
                }
            }
        }

    }

}