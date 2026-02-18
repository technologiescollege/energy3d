package org.concord.energy3d.simulation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Human;
import org.concord.energy3d.model.ParabolicDish;
import org.concord.energy3d.model.Tree;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.util.BugReporter;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 */
public class ParabolicDishDailyAnalysis extends DailyAnalysis {

    public ParabolicDishDailyAnalysis() {
        super();
        graph = new PartEnergyDailyGraph();
        graph.setPreferredSize(new Dimension(600, 400));
        graph.setBackground(Color.WHITE);
    }

    @Override
    void runAnalysis(final JDialog parent) {
        graph.info = I18n.get("msg.calculating");
        graph.repaint();
        onStart();
        SceneManager.getTaskManager().update(() -> {
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart instanceof Tree || selectedPart instanceof Human) { // make sure that we deselect trees or humans, which cannot be attributed to a foundation
                SceneManager.getInstance().setSelectedPart(null);
            }
            final Throwable t = compute();
            if (t != null) {
                EventQueue.invokeLater(() -> BugReporter.report(t));
            }
            EventQueue.invokeLater(() -> {
                onCompletion();
                final String current = Graph.TWO_DECIMALS.format(getResult("Solar"));
                final Map<String, Double> recordedResults = getRecordedResults("Solar");
                final int n = recordedResults.size();
                if (n > 0) {
                    String previousRuns = "";
                    final Object[] keys = recordedResults.keySet().toArray();
                    for (int i = n - 1; i >= 0; i--) {
                        previousRuns += keys[i] + " : " + Graph.TWO_DECIMALS.format(recordedResults.get(keys[i])) + " kWh<br>";
                    }
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("common.copy_data")};
                    final String msg = "<html>" + I18n.get("msg.calculated_daily_output", current) + "<br><hr>" + I18n.get("msg.results_from_previously_recorded_tests") + "<br>" + previousRuns + "</html>";
                    final JOptionPane optionPane = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("title.daily_output"));
                    dialog.setVisible(true);
                    final Object choice = optionPane.getValue();
                    if (choice == options[1]) {
                        String output = "";
                        for (int i = 0; i < n; i++) {
                            output += Graph.TWO_DECIMALS.format(recordedResults.get(keys[i])) + "\n";
                        }
                        output += current;
                        final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clpbrd.setContents(new StringSelection(output), null);
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>" + I18n.get("msg.data_points_copied", Integer.toString(n + 1)) + "<br><hr>" + output + "</html>", I18n.get("dialog.confirm"), JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "<html>" + I18n.get("msg.calculated_daily_output", current) + "</html>", I18n.get("title.daily_output"), JOptionPane.INFORMATION_MESSAGE);
                }
            });
            return null;
        });
    }

    @Override
    public void updateGraph() {
        for (int i = 0; i < 24; i++) {
            SolarRadiation.getInstance().computeEnergyAtHour(i);
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart != null) {
                if (selectedPart instanceof ParabolicDish) {
                    final ParabolicDish d = (ParabolicDish) selectedPart;
                    graph.addData("Solar", d.getSolarPotentialNow() * d.getSystemEfficiency());
                } else if (selectedPart instanceof Foundation) {
                    double output = 0;
                    for (final HousePart p : Scene.getInstance().getParts()) {
                        if (p instanceof ParabolicDish && p.getTopContainer() == selectedPart) {
                            final ParabolicDish d = (ParabolicDish) p;
                            output += d.getSolarPotentialNow() * d.getSystemEfficiency();
                        }
                    }
                    graph.addData("Solar", output);
                } else if (selectedPart.getTopContainer() != null) {
                    double output = 0;
                    for (final HousePart p : Scene.getInstance().getParts()) {
                        if (p instanceof ParabolicDish && p.getTopContainer() == selectedPart.getTopContainer()) {
                            final ParabolicDish d = (ParabolicDish) p;
                            output += d.getSolarPotentialNow() * d.getSystemEfficiency();
                        }
                    }
                    graph.addData("Solar", output);
                }
            } else {
                double output = 0;
                for (final HousePart p : Scene.getInstance().getParts()) {
                    if (p instanceof ParabolicDish) {
                        final ParabolicDish d = (ParabolicDish) p;
                        output += d.getSolarPotentialNow() * d.getSystemEfficiency();
                    }
                }
                graph.addData("Solar", output);
            }
        }
        graph.repaint();
    }

    public void show() {
        final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
        String s = null;
        int cost = -1;
        String title = I18n.get("title.daily_yield_all_parabolic_dishes", Integer.toString(Scene.getInstance().countParts(ParabolicDish.class)));
        if (selectedPart != null) {
            if (selectedPart instanceof ParabolicDish) {
                cost = (int) CspProjectCost.getPartCost(selectedPart);
                s = selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                title = I18n.get("title.daily_yield");
            } else if (selectedPart instanceof Foundation) {
                title = I18n.get("title.daily_yield_of_foundation", Integer.toString(((Foundation) selectedPart).countParts(ParabolicDish.class)), I18n.get("part.parabolic_dishes"));
            } else if (selectedPart.getTopContainer() != null) {
                title = I18n.get("title.daily_yield_of_foundation", Integer.toString(selectedPart.getTopContainer().countParts(ParabolicDish.class)), I18n.get("part.parabolic_dishes"));
            }
        }
        final JDialog dialog = createDialog(s == null ? title : title + ": " + s + I18n.get("msg.cost_suffix", String.valueOf(cost)));
        final JMenuBar menuBar = new JMenuBar();
        dialog.setJMenuBar(menuBar);
        menuBar.add(createOptionsMenu(dialog, null, true));
        menuBar.add(createRunsMenu());
        dialog.setVisible(true);
    }

    @Override
    public String toJson() {
        String s = "{";
        final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
        if (selectedPart != null) {
            if (selectedPart instanceof ParabolicDish) {
                s += "\"Parabolic Dish\": \"" + selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1) + "\"";
            } else if (selectedPart instanceof Foundation) {
                s += "\"Foundation\": \"" + selectedPart.toString().substring(0, selectedPart.toString().indexOf(')') + 1) + "\"";
            } else if (selectedPart.getTopContainer() != null) {
                s += "\"Foundation\": \"" + selectedPart.getTopContainer().toString().substring(0, selectedPart.getTopContainer().toString().indexOf(')') + 1) + "\"";
            }
        } else {
            s += "\"Parabolic Dish\": \"All\"";
        }
        final String name = "Solar";
        final List<Double> data = graph.getData(name);
        s += ", \"" + name + "\": {";
        s += "\"Hourly\": [";
        for (final Double x : data) {
            s += Graph.FIVE_DECIMALS.format(x) + ",";
        }
        s = s.substring(0, s.length() - 1);
        s += "]\n";
        s += ", \"Total\": " + Graph.ENERGY_FORMAT.format(getResult(name));
        s += "}";
        s += "}";
        return s;
    }

}