package org.concord.energy3d.geneticalgorithms.applications;

import java.awt.EventQueue;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.concord.energy3d.geneticalgorithms.Individual;
import org.concord.energy3d.geneticalgorithms.ObjectiveFunction;
import org.concord.energy3d.gui.EnergyPanel;
import org.concord.energy3d.gui.PvProjectDailyEnergyGraph;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Rack;
import org.concord.energy3d.model.SolarPanel;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.shapes.Heliodon;
import org.concord.energy3d.util.I18n;

import com.ardor3d.math.Vector3;

/**
 * Chromosome of an individual is encoded as follows:
 * <p>
 * row spacing (d), tilt angle (a), panel row number on rack (r)
 * <p>
 * assuming the base height is fixed and the number of rows on each rack increases when the tilt angle decreases (otherwise the maximum inter-row spacing would always be preferred)
 *
 * @author Charles Xie
 */
public class SolarPanelArrayOptimizer extends SolarOutputOptimizer {

    private double minimumRowSpacing;
    private double maximumRowSpacing;
    private int minimumPanelRows = 1;
    private int maximumPanelRows = Rack.MAXIMUM_SOLAR_PANEL_ROWS;
    private double baseHeight;
    private SolarPanel solarPanel;
    private boolean outputPerSolarPanel;
    private boolean netProfit;
    private double pricePerKWh = 0.225;
    private double dailyCostPerSolarPanel = 0.15;

    SolarPanelArrayOptimizer(final int populationSize, final int chromosomeLength, final int discretizationSteps) {
        super(populationSize, chromosomeLength, discretizationSteps);
        setGeneName(0, I18n.get("param.inter_row_spacing"));
        setGeneName(1, I18n.get("param.tilt_angle"));
        setGeneName(2, I18n.get("param.solar_panel_rows_per_rack"));
    }

    @Override
    public void setFoundation(final Foundation foundation) {
        super.setFoundation(foundation);
        final Vector3 p = foundation.getAbsPoint(1).subtract(foundation.getAbsPoint(0), null);
        final List<Rack> racks = foundation.getRacks();
        final int n = racks.size();
        if (n > 0) {
            final Random random = new Random();
            final Rack rack = racks.get(0);
            solarPanel = rack.getSolarPanel();
            baseHeight = rack.getPoleHeight() * Scene.getInstance().getScale();
            final int panelRowsPerRack = rack.getSolarPanelRowAndColumnNumbers()[1];
            maximumRowSpacing = p.length() * Scene.getInstance().getScale() - rack.getRackHeight(); // two racks at the opposite edges of the rectangular area
            // minimumRowSpacing = rack.getRackHeight(); // WARNING: Can't do this as the rack height can change during a run
            minimumRowSpacing = minimumPanelRows * (solarPanel.isRotated() ? solarPanel.getPanelWidth() : solarPanel.getPanelHeight());
            setGeneMinimum(0, minimumRowSpacing);
            setGeneMaximum(0, maximumRowSpacing);
            double normalizedValue;
            final Individual firstBorn = population.getIndividual(0); // initialize the population with the first-born being the current design
            if (n > 1) {
                final Vector3 q = rack.getAbsCenter().subtractLocal(racks.get(1).getAbsCenter());
                final double rowSpacing = Math.abs(q.dot(p.normalize(null))) * Scene.getInstance().getScale();
                normalizedValue = (rowSpacing - minimumRowSpacing) / (maximumRowSpacing - minimumRowSpacing);
                if (normalizedValue < 0) {
                    normalizedValue = 0;
                } else if (normalizedValue > 1) {
                    normalizedValue = 1;
                }
                setInitialGene(0, rowSpacing);
            } else {
                normalizedValue = 1;
                setInitialGene(0, maximumRowSpacing);
            }
            firstBorn.setGene(0, normalizedValue);
            if (searchMethod == LOCAL_SEARCH_RANDOM_OPTIMIZATION) {
                for (int k = 1; k < population.size(); k++) {
                    final Individual individual = population.getIndividual(k);
                    double v = random.nextGaussian() * localSearchRadius + normalizedValue;
                    while (v < 0 || v > 1) {
                        v = random.nextGaussian() * localSearchRadius + normalizedValue;
                    }
                    individual.setGene(0, v);
                }
            }

            setGeneMinimum(1, -90);
            setGeneMaximum(1, 90);
            normalizedValue = 0.5 * (1.0 + rack.getTiltAngle() / 90.0);
            firstBorn.setGene(1, normalizedValue);
            setInitialGene(1, rack.getTiltAngle());
            if (searchMethod == LOCAL_SEARCH_RANDOM_OPTIMIZATION) {
                for (int k = 1; k < population.size(); k++) {
                    final Individual individual = population.getIndividual(k);
                    double v = random.nextGaussian() * localSearchRadius + normalizedValue;
                    while (v < 0 || v > 1) {
                        v = random.nextGaussian() * localSearchRadius + normalizedValue;
                    }
                    individual.setGene(1, v);
                }
            }

            setGeneMinimum(2, minimumPanelRows);
            setGeneMaximum(2, maximumPanelRows);
            setGeneInteger(2, true);
            normalizedValue = (double) (panelRowsPerRack - minimumPanelRows) / (double) (maximumPanelRows - minimumPanelRows);
            if (normalizedValue < 0) {
                normalizedValue = 0;
            } else if (normalizedValue > 1) {
                normalizedValue = 1;
            }
            firstBorn.setGene(2, normalizedValue);
            setInitialGene(2, panelRowsPerRack);
            if (searchMethod == LOCAL_SEARCH_RANDOM_OPTIMIZATION) {
                for (int k = 1; k < population.size(); k++) {
                    final Individual individual = population.getIndividual(k);
                    double v = random.nextGaussian() * localSearchRadius + normalizedValue;
                    while (v < 0 || v > 1) {
                        v = random.nextGaussian() * localSearchRadius + normalizedValue;
                    }
                    individual.setGene(2, v);
                }
            }

        } else {
            throw new RuntimeException("Must have at least one solar panel rack on this foundation");
        }
    }

    public void setMinimumPanelRows(final int min) {
        minimumPanelRows = min;
    }

    public void setMaximumPanelRows(final int max) {
        maximumPanelRows = max;
    }

    public void setPricePerKWh(final double x) {
        pricePerKWh = x;
    }

    public void setDailyCostPerSolarPanel(final double x) {
        dailyCostPerSolarPanel = x;
    }

    public void setOutputPerSolarPanel(final boolean b) {
        outputPerSolarPanel = b;
    }

    public void setNetProfit(final boolean b) {
        netProfit = b;
    }

    @Override
    void computeIndividualFitness(final Individual individual) {
        final double rowSpacing = minimumRowSpacing + individual.getGene(0) * (maximumRowSpacing - minimumRowSpacing);
        final double tiltAngle = (2 * individual.getGene(1) - 1) * 90;
        final int panelRowsPerRack = (int) Math.round(minimumPanelRows + individual.getGene(2) * (maximumPanelRows - minimumPanelRows));
        foundation.generateSolarRackArrays(solarPanel, tiltAngle, baseHeight, panelRowsPerRack, rowSpacing, 1);
        for (final Rack r : foundation.getRacks()) {
            r.setPoleDistanceX(10);
            r.setPoleDistanceY(10);
        }
        final double output = objectiveFunction.compute();
        final int count = foundation.countSolarPanels();
        if (netProfit) {
            double cost = dailyCostPerSolarPanel;
            if (objectiveFunction.getType() == ObjectiveFunction.ANNUAL) {
                cost *= 12;
            }
            individual.setFitness(output * pricePerKWh - cost * count);
        } else if (outputPerSolarPanel) {
            individual.setFitness(output / count);
        } else {
            individual.setFitness(output);
        }
    }

    @Override
    public void applyFittest() {
        final Individual best = population.getFittest();
        final double rowSpacing = minimumRowSpacing + best.getGene(0) * (maximumRowSpacing - minimumRowSpacing);
        final double tiltAngle = (2 * best.getGene(1) - 1) * 90;
        final int panelRowsPerRack = (int) Math.round(minimumPanelRows + best.getGene(2) * (maximumPanelRows - minimumPanelRows));
        foundation.generateSolarRackArrays(solarPanel, tiltAngle, baseHeight, panelRowsPerRack, rowSpacing, 1);
        for (final Rack r : foundation.getRacks()) {
            r.setPoleDistanceX(10);
            r.setPoleDistanceY(10);
        }
        setFinalGene(0, rowSpacing);
        setFinalGene(1, tiltAngle);
        setFinalGene(2, panelRowsPerRack);
        setFinalFitness(best.getFitness());
        System.out.println("Fittest: " + individualToString(best));
        displayFittest();
    }

    @Override
    String individualToString(final Individual individual) {
        String s = "(";
        s += (minimumRowSpacing + individual.getGene(0) * (maximumRowSpacing - minimumRowSpacing)) + ", ";
        s += (2 * individual.getGene(1) - 1) * 90 + ", ";
        return s.substring(0, s.length() - 2) + ") = " + individual.getFitness();
    }

    @Override
    public void displayFittest() {
        final Individual best = population.getIndividual(0);
        String s = null;
            switch (objectiveFunction.getType()) {
            case ObjectiveFunction.DAILY:
                if (netProfit) {
                    s = I18n.get("label.daily_profit");
                } else if (outputPerSolarPanel) {
                    s = I18n.get("label.daily_output_per_solar_panel");
                } else {
                    s = I18n.get("label.total_daily_output");
                }
                s += ": " + EnergyPanel.TWO_DECIMALS.format(best.getFitness());
                break;
            case ObjectiveFunction.ANNUAL:
                if (netProfit) {
                    s = I18n.get("label.annual_profit");
                } else if (outputPerSolarPanel) {
                    s = I18n.get("label.annual_output_per_solar_panel");
                } else {
                    s = I18n.get("label.total_annual_output");
                }
                s += ": " + EnergyPanel.ONE_DECIMAL.format(best.getFitness() * 365.0 / 12.0);
                break;
        }
        foundation.setLabelCustomText(s);
        foundation.draw();
        SceneManager.getInstance().refresh();
        super.displayFittest();
    }

    @Override
    void updateInfo(final Individual individual) {
        final Individual best = population.getIndividual(0);
        String s = null;
        switch (objectiveFunction.getType()) {
            case ObjectiveFunction.DAILY:
                if (netProfit) {
                    s = I18n.get("label.daily_profit");
                } else if (outputPerSolarPanel) {
                    s = I18n.get("label.daily_output_per_solar_panel");
                } else {
                    s = I18n.get("label.total_daily_output");
                }
                s += "\n" + I18n.get("label.current") + ": " + EnergyPanel.TWO_DECIMALS.format(individual.getFitness()) + ", " + I18n.get("label.top") + ": " + EnergyPanel.TWO_DECIMALS.format(best.getFitness());
                break;
            case ObjectiveFunction.ANNUAL:
                if (netProfit) {
                    s = I18n.get("label.annual_profit");
                } else if (outputPerSolarPanel) {
                    s = I18n.get("label.annual_output_per_solar_panel");
                } else {
                    s = I18n.get("label.total_annual_output");
                }
                s += "\n" + I18n.get("label.current") + ": " + EnergyPanel.ONE_DECIMAL.format(individual.getFitness() * 365.0 / 12.0) + ", " + I18n.get("label.top") + ": " + EnergyPanel.ONE_DECIMAL.format(best.getFitness() * 365.0 / 12.0);
                break;
        }
        foundation.setLabelCustomText(s);
        foundation.draw();
        EventQueue.invokeLater(() -> {
            final Calendar today = Heliodon.getInstance().getCalendar();
            EnergyPanel.getInstance().getDateSpinner().setValue(today.getTime());
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart instanceof Foundation) { // synchronize with daily graph
                final PvProjectDailyEnergyGraph g = EnergyPanel.getInstance().getPvProjectDailyEnergyGraph();
                g.setCalendar(today);
                EnergyPanel.getInstance().getPvProjectTabbedPane().setSelectedComponent(g);
                if (g.hasGraph()) {
                    g.updateGraph();
                } else {
                    g.addGraph((Foundation) selectedPart);
                }
            }
        });
    }

    private static SolarPanelArrayOptimizerMaker maker;

    public static void make(final Foundation foundation) {
        if (maker == null) {
            maker = new SolarPanelArrayOptimizerMaker();
        }
        maker.make(foundation);
    }

    public static void stopIt() {
        if (maker != null) {
            maker.stop();
        }
    }

    public static void runIt(final Foundation foundation, final boolean local, final boolean daily, final boolean profit,
                             final int population, final int generations, final float mutation, final float convergence, final float searchRadius) {
        if (maker == null) {
            maker = new SolarPanelArrayOptimizerMaker();
        }
        maker.run(foundation, local, daily, profit, population, generations, mutation, convergence, searchRadius);
    }

    @Override
    public String toJson() {
        String json = super.toJson();
        json = json.substring(0, json.length() - 1);
        json += ", \"" + I18n.get("json.profit") + "\": " + netProfit;
        json += ", \"" + I18n.get("json.solar_panel_average") + "\": " + outputPerSolarPanel;
        json += ", \"" + I18n.get("json.price_per_kwh") + "\": " + pricePerKWh;
        json += ", \"" + I18n.get("json.daily_cost_per_solar_panel") + "\": " + dailyCostPerSolarPanel;
        json += "}";
        return json;
    }

}