package org.concord.energy3d.geneticalgorithms.applications;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.concord.energy3d.gui.EnergyPanel;
import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.Wall;
import org.concord.energy3d.model.Window;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

/**
 * @author Charles Xie
 */
public class WindowOptimizerMaker extends OptimizerMaker {

    private double minimumWidthRelative = 0.05;
    private double maximumWidthRelative = 0.15;
    private double minimumHeightRelative = 0.05;
    private double maximumHeightRelative = 0.4;
    private boolean optimizeIndividualWindows;

    @Override
    public void make(final Foundation foundation) {

        final List<Window> windows = foundation.getWindows();
        if (windows.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_window_on_foundation"), I18n.get("title.information"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final JPanel panel = new JPanel(new SpringLayout());
        panel.add(new JLabel(I18n.get("label.solution")));
        final JComboBox<String> solutionComboBox = new JComboBox<>(new String[]{I18n.get("combo.window_sizes")});
        panel.add(solutionComboBox);
        panel.add(new JLabel(I18n.get("label.objective")));
        final JComboBox<String> objectiveComboBox = new JComboBox<>(new String[]{I18n.get("combo.daily_energy_use"), I18n.get("combo.annual_energy_use")});
        objectiveComboBox.setSelectedIndex(selectedObjectiveFunction);
        panel.add(objectiveComboBox);
        panel.add(new JLabel(I18n.get("label.minimum_width")));
        final JTextField minimumWidthField = new JTextField(EnergyPanel.TWO_DECIMALS.format(minimumWidthRelative));
        panel.add(minimumWidthField);
        panel.add(new JLabel(I18n.get("label.maximum_width")));
        final JTextField maximumWidthField = new JTextField(EnergyPanel.TWO_DECIMALS.format(maximumWidthRelative));
        panel.add(maximumWidthField);
        panel.add(new JLabel(I18n.get("label.minimum_height")));
        final JTextField minimumHeightField = new JTextField(EnergyPanel.TWO_DECIMALS.format(minimumHeightRelative));
        panel.add(minimumHeightField);
        panel.add(new JLabel(I18n.get("label.maximum_height")));
        final JTextField maximumHeightField = new JTextField(EnergyPanel.TWO_DECIMALS.format(maximumHeightRelative));
        panel.add(maximumHeightField);
        panel.add(new JLabel(I18n.get("label.type")));
        final JComboBox<String> typeComboBox = new JComboBox<>(new String[]{I18n.get("combo.continuous")});
        panel.add(typeComboBox);
        panel.add(new JLabel(I18n.get("label.selection")));
        final JComboBox<String> selectionComboBox = new JComboBox<>(new String[]{I18n.get("combo.roulette_wheel"), I18n.get("combo.tournament")});
        selectionComboBox.setSelectedIndex(selectedSelectionMethod);
        panel.add(selectionComboBox);
        panel.add(new JLabel(I18n.get("label.population_size")));
        final JTextField populationField = new JTextField(populationSize + "");
        panel.add(populationField);
        panel.add(new JLabel(I18n.get("label.maximum_generations")));
        final JTextField generationField = new JTextField(maximumGenerations + "");
        panel.add(generationField);
        panel.add(new JLabel(I18n.get("label.mutation_rate")));
        final JTextField mutationRateField = new JTextField(EnergyPanel.FIVE_DECIMALS.format(mutationRate));
        panel.add(mutationRateField);
        panel.add(new JLabel(I18n.get("label.convergence_criterion")));
        final JComboBox<String> convergenceCriterionComboBox = new JComboBox<>(new String[]{I18n.get("combo.bitwise_nominal")});
        panel.add(convergenceCriterionComboBox);
        panel.add(new JLabel(I18n.get("label.convergence_threshold")));
        final JTextField convergenceThresholdField = new JTextField(EnergyPanel.FIVE_DECIMALS.format(convergenceThreshold));
        panel.add(convergenceThresholdField);
        panel.add(new JLabel(I18n.get("label.optimize_individual_windows")));
        final JComboBox<String> optimizeIndividualWindowsComboBox = new JComboBox<>(new String[]{I18n.get("combo.no"), I18n.get("combo.yes")});
        optimizeIndividualWindowsComboBox.setSelectedIndex(optimizeIndividualWindows ? 1 : 0);
        panel.add(optimizeIndividualWindowsComboBox);

        SpringUtilities.makeCompactGrid(panel, 14, 2, 6, 6, 6, 6);

        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);
        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.genetic_algorithm_options_window_sizes"));

        while (true) {
            dialog.setVisible(true);
            final Object choice = optionPane.getValue();
            if (choice == options[1] || choice == null) {
                break;
            } else {
                boolean ok = true;
                try {
                    populationSize = Integer.parseInt(populationField.getText());
                    maximumGenerations = Integer.parseInt(generationField.getText());
                    convergenceThreshold = Double.parseDouble(convergenceThresholdField.getText());
                    mutationRate = Double.parseDouble(mutationRateField.getText());
                    minimumWidthRelative = Double.parseDouble(minimumWidthField.getText());
                    maximumWidthRelative = Double.parseDouble(maximumWidthField.getText());
                    minimumHeightRelative = Double.parseDouble(minimumHeightField.getText());
                    maximumHeightRelative = Double.parseDouble(maximumHeightField.getText());
                } catch (final NumberFormatException exception) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value_short"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
                    ok = false;
                }
                if (ok) {
                    if (populationSize <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.population_size_positive"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (maximumGenerations <= 1) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.max_generations_positive"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (mutationRate < 0 || mutationRate > 1) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.mutation_rate_range"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (convergenceThreshold < 0 || convergenceThreshold > 0.1) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.convergence_threshold_range"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else {
                        selectedObjectiveFunction = objectiveComboBox.getSelectedIndex();
                        selectedSelectionMethod = selectionComboBox.getSelectedIndex();
                        optimizeIndividualWindows = optimizeIndividualWindowsComboBox.getSelectedIndex() == 1;
                        int chromesomeLength;
                        if (optimizeIndividualWindows) {
                            chromesomeLength = foundation.getWindows().size() * 2;
                        } else {
                            final List<Wall> walls = foundation.getWalls();
                            int numberOfWallsWithWindows = 0;
                            for (final Wall wall : walls) {
                                if (!wall.getWindows().isEmpty()) {
                                    numberOfWallsWithWindows++;
                                }
                            }
                            chromesomeLength = numberOfWallsWithWindows * 2;
                        }
                        op = new WindowOptimizer(populationSize, chromesomeLength, 20);
                        op.setSelectionMethod(selectedSelectionMethod);
                        op.setConvergenceThreshold(convergenceThreshold);
                        ((WindowOptimizer) op).setOptimizeIndividualWindows(optimizeIndividualWindows);
                        ((WindowOptimizer) op).setMinimumWidthRelative(minimumWidthRelative);
                        ((WindowOptimizer) op).setMaximumWidthRelative(maximumWidthRelative);
                        ((WindowOptimizer) op).setMinimumHeightRelative(minimumHeightRelative);
                        ((WindowOptimizer) op).setMaximumHeightRelative(maximumHeightRelative);
                        op.setMaximumGenerations(maximumGenerations);
                        op.setMutationRate(mutationRate);
                        op.setFoundation(foundation);
                        op.setOjectiveFunction(selectedObjectiveFunction);
                        op.evolve();
                        if (choice == options[0]) {
                            break;
                        }
                    }
                }
            }
        }

    }

}