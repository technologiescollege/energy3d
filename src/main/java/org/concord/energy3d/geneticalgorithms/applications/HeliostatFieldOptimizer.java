package org.concord.energy3d.geneticalgorithms.applications;

import java.awt.EventQueue;
import java.util.Calendar;

import org.concord.energy3d.geneticalgorithms.Individual;
import org.concord.energy3d.geneticalgorithms.ObjectiveFunction;
import org.concord.energy3d.gui.CspProjectDailyEnergyGraph;
import org.concord.energy3d.gui.EnergyPanel;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.shapes.Heliodon;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 */
public abstract class HeliostatFieldOptimizer extends SolarOutputOptimizer {

    double minimumApertureWidth = 2;
    double maximumApertureWidth = 10;
    double minimumApertureHeight = 1;
    double maximumApertureHeight = 2;

    double minimumRadialExpansion = 0;
    double maximumRadialExpansion = 0.01;

    boolean outputPerApertureSquareMeter;
    boolean netProfit;
    double pricePerKWh = 0.225;
    double dailyCostPerApertureSquareMeter = 0.1;

    HeliostatFieldOptimizer(final int populationSize, final int chromosomeLength, final int discretizationSteps) {
        super(populationSize, chromosomeLength, discretizationSteps);
    }

    void setPricePerKWh(final double x) {
        pricePerKWh = x;
    }

    void setDailyCostPerApertureSquareMeter(final double x) {
        dailyCostPerApertureSquareMeter = x;
    }

    void setOutputPerApertureSquareMeter(final boolean b) {
        outputPerApertureSquareMeter = b;
    }

    void setNetProfit(final boolean b) {
        netProfit = b;
    }

    void setMinimumApertureWidth(final double minimumApertureWidth) {
        this.minimumApertureWidth = minimumApertureWidth;
    }

    void setMaximumApertureWidth(final double maximumApertureWidth) {
        this.maximumApertureWidth = maximumApertureWidth;
    }

    void setMinimumApertureHeight(final double minimumApertureHeight) {
        this.minimumApertureHeight = minimumApertureHeight;
    }

    void setMaximumApertureHeight(final double maximumApertureHeight) {
        this.maximumApertureHeight = maximumApertureHeight;
    }

    void setMinimumRadialExpansion(final double minimumRadialExpansion) {
        this.minimumRadialExpansion = minimumRadialExpansion;
    }

    void setMaximumRadialExpansion(final double maximumRadialExpansion) {
        this.maximumRadialExpansion = maximumRadialExpansion;
    }

    @Override
    public void displayFittest() {
        final Individual best = population.getIndividual(0);
        final Foundation receiver = foundation.getHeliostats().get(0).getReceiver();
        if (receiver != null) {
            String s = null;
            switch (objectiveFunction.getType()) {
                case ObjectiveFunction.DAILY:
                    if (netProfit) {
                        s = I18n.get("label.daily_profit");
                    } else if (outputPerApertureSquareMeter) {
                        s = I18n.get("label.daily_output_per_aperture_square_meter");
                    } else {
                        s = I18n.get("label.total_daily_output");
                    }
                    s += ": " + EnergyPanel.TWO_DECIMALS.format(best.getFitness());
                    break;
                case ObjectiveFunction.ANNUAL:
                    if (netProfit) {
                        s = I18n.get("label.annual_profit");
                    } else if (outputPerApertureSquareMeter) {
                        s = I18n.get("label.annual_output_per_aperture_square_meter");
                    } else {
                        s = I18n.get("label.total_annual_output");
                    }
                    s += ": " + EnergyPanel.ONE_DECIMAL.format(best.getFitness() * 365.0 / 12.0);
                    break;
            }
            receiver.setLabelCustomText(s);
            receiver.draw();
        }
        SceneManager.getInstance().refresh();
        super.displayFittest();
    }

    @Override
    void updateInfo(final Individual individual) {
        final Individual best = population.getIndividual(0);
        final Foundation receiver = foundation.getHeliostats().get(0).getReceiver();
        if (receiver != null) {
            String s = null;
            switch (objectiveFunction.getType()) {
                case ObjectiveFunction.DAILY:
                    if (netProfit) {
                        s = I18n.get("label.daily_profit");
                    } else if (outputPerApertureSquareMeter) {
                        s = I18n.get("label.daily_output_per_aperture_square_meter");
                    } else {
                        s = I18n.get("label.total_daily_output");
                    }
                    s += "\n" + I18n.get("label.current") + ": " + EnergyPanel.TWO_DECIMALS.format(individual.getFitness()) + ", " + I18n.get("label.top") + ": " + EnergyPanel.TWO_DECIMALS.format(best.getFitness());
                    break;
                case ObjectiveFunction.ANNUAL:
                    if (netProfit) {
                        s = I18n.get("label.annual_profit");
                    } else if (outputPerApertureSquareMeter) {
                        s = I18n.get("label.annual_output_per_aperture_square_meter");
                    } else {
                        s = I18n.get("label.total_annual_output");
                    }
                    s += "\n" + I18n.get("label.current") + ": " + EnergyPanel.ONE_DECIMAL.format(individual.getFitness() * 365.0 / 12.0) + ", " + I18n.get("label.top") + ": " + EnergyPanel.ONE_DECIMAL.format(best.getFitness() * 365.0 / 12.0);
                    break;
            }
            receiver.setLabelCustomText(s);
            receiver.draw();
        }
        EventQueue.invokeLater(() -> {
            final Calendar today = Heliodon.getInstance().getCalendar();
            EnergyPanel.getInstance().getDateSpinner().setValue(today.getTime());
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart instanceof Foundation) {
                final CspProjectDailyEnergyGraph g = EnergyPanel.getInstance().getCspProjectDailyEnergyGraph();
                g.setCalendar(today);
                EnergyPanel.getInstance().getCspProjectTabbedPane().setSelectedComponent(g);
                if (g.hasGraph()) {
                    g.updateGraph();
                } else {
                    g.addGraph((Foundation) selectedPart);
                }
            }
        });
    }

}