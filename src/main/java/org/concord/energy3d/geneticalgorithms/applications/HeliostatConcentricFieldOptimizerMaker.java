package org.concord.energy3d.geneticalgorithms.applications;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.concord.energy3d.geneticalgorithms.ObjectiveFunction;
import org.concord.energy3d.gui.EnergyPanel;
import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.Mirror;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

/**
 * @author Charles Xie
 */
public class HeliostatConcentricFieldOptimizerMaker extends OptimizerMaker {

    private double minimumApertureWidth = 1;
    private double maximumApertureWidth = 10;
    private double minimumApertureHeight = 1;
    private double maximumApertureHeight = 2;
    private double minimumAzimuthalSpacing = 0;
    private double maximumAzimuthalSpacing = 10;
    private double minimumRadialSpacing = 0;
    private double maximumRadialSpacing = 5;
    private double minimumRadialExpansion = 0;
    private double maximumRadialExpansion = 0.01;

    private double pricePerKWh = 0.225;
    private double dailyCostPerApertureSquareMeter = 0.1;

    @Override
    public void make(final Foundation foundation) {

        final List<Mirror> heliostats = foundation.getHeliostats();
        if (heliostats.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_heliostat_on_foundation"), I18n.get("title.information"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        final JPanel panel = new JPanel(new SpringLayout());
        panel.add(new JLabel(I18n.get("label.solution")));
        final JComboBox<String> solutionComboBox = new JComboBox<>(new String[]{I18n.get("combo.field_pattern")});
        panel.add(solutionComboBox);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.objective")));
        final JComboBox<String> objectiveComboBox = new JComboBox<>
                (new String[]{I18n.get("combo.daily_total_output"), I18n.get("combo.annual_total_output"), I18n.get("combo.daily_average_output"), I18n.get("combo.annual_average_output"), I18n.get("combo.daily_profit"), I18n.get("combo.annual_profit")});
        objectiveComboBox.setSelectedIndex(selectedObjectiveFunction);
        panel.add(objectiveComboBox);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.electricity_price")));
        final JTextField priceField = new JTextField(pricePerKWh + "");
        panel.add(priceField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.dollar_per_kwh") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.cost")));
        final JTextField dailyCostField = new JTextField(dailyCostPerApertureSquareMeter + "");
        panel.add(dailyCostField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.dollar_per_day_per_m2") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.minimum_heliostat_aperture_width")));
        final JTextField minimumApertureWidthField = new JTextField(EnergyPanel.TWO_DECIMALS.format(minimumApertureWidth));
        panel.add(minimumApertureWidthField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.maximum_heliostat_aperture_width")));
        final JTextField maximumApertureWidthField = new JTextField(EnergyPanel.TWO_DECIMALS.format(maximumApertureWidth));
        panel.add(maximumApertureWidthField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.minimum_heliostat_aperture_height")));
        final JTextField minimumApertureHeightField = new JTextField(EnergyPanel.TWO_DECIMALS.format(minimumApertureHeight));
        panel.add(minimumApertureHeightField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.maximum_heliostat_aperture_height")));
        final JTextField maximumApertureHeightField = new JTextField(EnergyPanel.TWO_DECIMALS.format(maximumApertureHeight));
        panel.add(maximumApertureHeightField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.minimum_azimuthal_spacing")));
        final JTextField minimumAzimuthalSpacingField = new JTextField(EnergyPanel.TWO_DECIMALS.format(minimumAzimuthalSpacing));
        panel.add(minimumAzimuthalSpacingField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.maximum_azimuthal_spacing")));
        final JTextField maximumAzimuthalSpacingField = new JTextField(EnergyPanel.TWO_DECIMALS.format(maximumAzimuthalSpacing));
        panel.add(maximumAzimuthalSpacingField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.minimum_radial_spacing")));
        final JTextField minimumRadialSpacingField = new JTextField(EnergyPanel.TWO_DECIMALS.format(minimumRadialSpacing));
        panel.add(minimumRadialSpacingField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.maximum_radial_spacing")));
        final JTextField maximumRadialSpacingField = new JTextField(EnergyPanel.TWO_DECIMALS.format(maximumRadialSpacing));
        panel.add(maximumRadialSpacingField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.meters") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.minimum_radial_expansion")));
        final JTextField minimumRadialExpansionField = new JTextField(EnergyPanel.FIVE_DECIMALS.format(minimumRadialExpansion));
        panel.add(minimumRadialExpansionField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.dimensionless") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.maximum_radial_expansion")));
        final JTextField maximumRadialExpansionField = new JTextField(EnergyPanel.FIVE_DECIMALS.format(maximumRadialExpansion));
        panel.add(maximumRadialExpansionField);
        panel.add(new JLabel("<html><font size=2>" + I18n.get("unit.dimensionless") + "</font></html>"));

        panel.add(new JLabel(I18n.get("label.type")));
        final JComboBox<String> typeComboBox = new JComboBox<>(new String[]{I18n.get("combo.continuous")});
        panel.add(typeComboBox);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.selection")));
        final JComboBox<String> selectionComboBox = new JComboBox<>(new String[]{I18n.get("combo.roulette_wheel"), I18n.get("combo.tournament")});
        selectionComboBox.setSelectedIndex(selectedSelectionMethod);
        panel.add(selectionComboBox);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.population_size")));
        final JTextField populationField = new JTextField(populationSize + "");
        panel.add(populationField);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.maximum_generations")));
        final JTextField generationField = new JTextField(maximumGenerations + "");
        panel.add(generationField);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.mutation_rate")));
        final JTextField mutationRateField = new JTextField(EnergyPanel.FIVE_DECIMALS.format(mutationRate));
        panel.add(mutationRateField);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.convergence_criterion")));
        final JComboBox<String> convergenceCriterionComboBox = new JComboBox<>(new String[]{I18n.get("combo.bitwise_nominal")});
        panel.add(convergenceCriterionComboBox);
        panel.add(new JLabel());

        panel.add(new JLabel(I18n.get("label.convergence_threshold")));
        final JTextField convergenceThresholdField = new JTextField(EnergyPanel.FIVE_DECIMALS.format(convergenceThreshold));
        panel.add(convergenceThresholdField);
        panel.add(new JLabel());

        SpringUtilities.makeCompactGrid(panel, 21, 3, 6, 6, 6, 6);

        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION, null, options, options[0]);
        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.genetic_algorithm_options_heliostat_concentric"));

        while (true) {
            dialog.setVisible(true);
            final Object choice = optionPane.getValue();
            if (choice == options[1] || choice == null) {
                break;
            } else {
                boolean ok = true;
                try {
                    pricePerKWh = Double.parseDouble(priceField.getText());
                    dailyCostPerApertureSquareMeter = Double.parseDouble(dailyCostField.getText());
                    minimumAzimuthalSpacing = Double.parseDouble(minimumAzimuthalSpacingField.getText());
                    maximumAzimuthalSpacing = Double.parseDouble(maximumAzimuthalSpacingField.getText());
                    minimumRadialSpacing = Double.parseDouble(minimumRadialSpacingField.getText());
                    maximumRadialSpacing = Double.parseDouble(maximumRadialSpacingField.getText());
                    minimumRadialExpansion = Double.parseDouble(minimumRadialExpansionField.getText());
                    maximumRadialExpansion = Double.parseDouble(maximumRadialExpansionField.getText());
                    minimumApertureWidth = Double.parseDouble(minimumApertureWidthField.getText());
                    maximumApertureWidth = Double.parseDouble(maximumApertureWidthField.getText());
                    minimumApertureHeight = Double.parseDouble(minimumApertureHeightField.getText());
                    maximumApertureHeight = Double.parseDouble(maximumApertureHeightField.getText());
                    populationSize = Integer.parseInt(populationField.getText());
                    maximumGenerations = Integer.parseInt(generationField.getText());
                    convergenceThreshold = Double.parseDouble(convergenceThresholdField.getText());
                    mutationRate = Double.parseDouble(mutationRateField.getText());
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
                    } else if (minimumAzimuthalSpacing < 0 || maximumAzimuthalSpacing < 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.azimuthal_spacing_not_negative"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumAzimuthalSpacing >= maximumAzimuthalSpacing) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.azimuthal_spacing_max_gt_min"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumRadialSpacing < 0 || maximumRadialSpacing < 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_spacing_not_negative"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumRadialSpacing >= maximumRadialSpacing) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_spacing_max_gt_min"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumRadialExpansion < 0 || maximumRadialExpansion < 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_expansion_not_negative"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumRadialExpansion >= maximumRadialExpansion) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_expansion_max_gt_min"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumApertureWidth < 0 || maximumApertureWidth < 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_width_not_negative"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumApertureWidth >= maximumApertureWidth) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_width_max_gt_min"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumApertureHeight < 0 || maximumApertureHeight < 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_height_not_negative"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else if (minimumApertureHeight >= maximumApertureHeight) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_height_max_gt_min"), I18n.get("title.range_error"), JOptionPane.ERROR_MESSAGE);
                    } else {
                        selectedObjectiveFunction = objectiveComboBox.getSelectedIndex();
                        selectedSelectionMethod = selectionComboBox.getSelectedIndex();
                        op = new HeliostatConcentricFieldOptimizer(populationSize, 5, 0);
                        final HeliostatConcentricFieldOptimizer op1 = (HeliostatConcentricFieldOptimizer) op;
                        op.setSelectionMethod(selectedSelectionMethod);
                        op.setConvergenceThreshold(convergenceThreshold);
                        op1.setMinimumAzimuthalSpacing(minimumAzimuthalSpacing);
                        op1.setMaximumAzimuthalSpacing(maximumAzimuthalSpacing);
                        op1.setMinimumRadialSpacing(minimumRadialSpacing);
                        op1.setMaximumRadialSpacing(maximumRadialSpacing);
                        op1.setMinimumRadialExpansion(minimumRadialExpansion);
                        op1.setMaximumRadialExpansion(maximumRadialExpansion);
                        op1.setMinimumApertureWidth(minimumApertureWidth);
                        op1.setMaximumApertureWidth(maximumApertureWidth);
                        op1.setMinimumApertureHeight(minimumApertureHeight);
                        op1.setMaximumApertureHeight(maximumApertureHeight);
                        op.setMaximumGenerations(maximumGenerations);
                        op.setMutationRate(mutationRate);
                        op1.setDailyCostPerApertureSquareMeter(dailyCostPerApertureSquareMeter);
                        op1.setPricePerKWh(pricePerKWh);
                        switch (selectedObjectiveFunction) {
                            case 0:
                                op.setOjectiveFunction(ObjectiveFunction.DAILY);
                                break;
                            case 1:
                                op.setOjectiveFunction(ObjectiveFunction.ANNUAL);
                                break;
                            case 2:
                                op.setOjectiveFunction(ObjectiveFunction.DAILY);
                                op1.setOutputPerApertureSquareMeter(true);
                                break;
                            case 3:
                                op.setOjectiveFunction(ObjectiveFunction.ANNUAL);
                                op1.setOutputPerApertureSquareMeter(true);
                                break;
                            case 4:
                                op.setOjectiveFunction(ObjectiveFunction.DAILY);
                                op1.setNetProfit(true);
                                break;
                            case 5:
                                op.setOjectiveFunction(ObjectiveFunction.ANNUAL);
                                op1.setNetProfit(true);
                                break;
                        }
                        op.setFoundation(foundation);
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