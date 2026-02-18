package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import javax.swing.*;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.CspFinancialModel;
import org.concord.energy3d.simulation.PvFinancialModel;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

/**
 * @author Charles Xie
 */
public class FinancialSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final static DecimalFormat FORMAT = new DecimalFormat("#0.###");
    private final static Color pvBackgroundColor = new Color(169, 223, 191);
    private final static Color cspBackgroundColor = new Color(252, 243, 207);

    private JTabbedPane tabbedPane;
    private JPanel pvSystemPanel;
    private JPanel cspSystemPanel;
    private Runnable runAfterOK;

    class PvSystemFinancePanel extends JPanel {

        private static final long serialVersionUID = 1L;

        final JTextField rackBaseField;
        final JTextField rackHeightField;
        final JTextField hsatField;
        final JTextField vsatField;
        final JTextField aadatField;
        JTextField lifespanField;
        JTextField kWhSellingPriceField;
        JTextField landCostField;
        JTextField cleaningCostField;
        JTextField maintenanceCostField;
        JTextField loanInterestRateField;

        PvSystemFinancePanel() {

            super();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            final PvFinancialModel finance = Scene.getInstance().getPvFinancialModel();

            JPanel container = new JPanel(new SpringLayout());
            container.setBorder(BorderFactory.createTitledBorder(I18n.get("finance.revenue_goals")));
            add(container);

            container.add(createPvLabel(I18n.get("finance.project_lifespan")));
            container.add(new JLabel());
            lifespanField = new JTextField(FORMAT.format(finance.getLifespan()), 6);
            container.add(lifespanField);
            container.add(new JLabel("<html>" + I18n.get("label.years") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.electricity_selling_price")));
            container.add(new JLabel("$"));
            kWhSellingPriceField = new JTextField(FORMAT.format(finance.getkWhSellingPrice()), 6);
            container.add(kWhSellingPriceField);
            container.add(new JLabel("<html>" + I18n.get("label.per_kwh") + "</html>"));

            SpringUtilities.makeCompactGrid(container, 2, 4, 6, 6, 6, 3);

            container = new JPanel(new SpringLayout());
            container.setBorder(BorderFactory.createTitledBorder(I18n.get("finance.operational_costs")));
            add(container);

            container.add(createPvLabel(I18n.get("finance.land_rental")));
            container.add(new JLabel("$"));
            landCostField = new JTextField(FORMAT.format(finance.getLandRentalCost()), 6);
            container.add(landCostField);
            container.add(new JLabel("<html>" + I18n.get("label.per_year_per_m2") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.cleaning_service")));
            container.add(new JLabel("$"));
            cleaningCostField = new JTextField(FORMAT.format(finance.getCleaningCost()), 6);
            container.add(cleaningCostField);
            container.add(new JLabel("<html>" + I18n.get("label.per_year_per_panel") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.maintenance")));
            container.add(new JLabel("$"));
            maintenanceCostField = new JTextField(FORMAT.format(finance.getMaintenanceCost()), 6);
            container.add(maintenanceCostField);
            container.add(new JLabel("<html>" + I18n.get("label.per_year_per_panel") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.loan_interest_rate")));
            container.add(new JLabel("%"));
            loanInterestRateField = new JTextField(FORMAT.format(finance.getLoanInterestRate() * 100), 6);
            container.add(loanInterestRateField);
            container.add(new JLabel("<html>" + I18n.get("label.for_upfront_costs") + "</html>"));

            SpringUtilities.makeCompactGrid(container, 4, 4, 6, 6, 6, 3);

            container = new JPanel(new SpringLayout());
            container.setBorder(BorderFactory.createTitledBorder(I18n.get("finance.upfront_costs")));
            add(container);

            container.add(createPvLabel(I18n.get("finance.photovoltaic_solar_panel")));
            container.add(new JLabel("$"));
            final JButton solarPanelMarketplace = new JButton(I18n.get("dialog.set_price"));
            solarPanelMarketplace.addActionListener(e -> {
                PvModelsDialog dialog = new PvModelsDialog();
                dialog.setVisible(true);
            });
            container.add(solarPanelMarketplace);
            container.add(new JLabel("<html>" + I18n.get("label.per_panel") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.rack_base")));
            container.add(new JLabel("$"));
            rackBaseField = new JTextField(FORMAT.format(finance.getSolarPanelRackBaseCost()), 6);
            container.add(rackBaseField);
            container.add(new JLabel("<html>" + I18n.get("label.per_panel") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.rack_extra_height")));
            container.add(new JLabel("$"));
            rackHeightField = new JTextField(FORMAT.format(finance.getSolarPanelRackHeightCost()), 6);
            container.add(rackHeightField);
            container.add(new JLabel("<html>" + I18n.get("label.per_meter_per_panel") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.horizontal_single_axis_tracker")));
            container.add(new JLabel("$"));
            hsatField = new JTextField(FORMAT.format(finance.getSolarPanelHsatCost()), 6);
            container.add(hsatField);
            container.add(new JLabel("<html>" + I18n.get("label.per_panel_if_used") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.vertical_single_axis_tracker")));
            container.add(new JLabel("$"));
            vsatField = new JTextField(FORMAT.format(finance.getSolarPanelVsatCost()), 6);
            container.add(vsatField);
            container.add(new JLabel("<html>" + I18n.get("label.per_panel_if_used") + "</html>"));

            container.add(createPvLabel(I18n.get("finance.azimuth_altitude_dual_axis_tracker")));
            container.add(new JLabel("$"));
            aadatField = new JTextField(FORMAT.format(finance.getSolarPanelAadatCost()), 6);
            container.add(aadatField);
            container.add(new JLabel("<html>" + I18n.get("label.per_panel_if_used") + "</html>"));

            SpringUtilities.makeCompactGrid(container, 6, 4, 6, 6, 6, 3);

        }

    }

    class CspSystemFinancePanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private JTextField heliostatField;
        private JTextField towerField;
        private JTextField parabolicTroughField;
        final JTextField parabolicDishField;
        final JTextField fresnelReflectorField;
        JTextField lifespanField;
        JTextField landCostField;
        JTextField kWhSellingPriceField;
        JTextField cleaningCostField;
        JTextField maintenanceCostField;
        JTextField loanInterestRateField;

        CspSystemFinancePanel() {

            super();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            final CspFinancialModel finance = Scene.getInstance().getCspFinancialModel();

            JPanel container = new JPanel(new SpringLayout());
            container.setBorder(BorderFactory.createTitledBorder(I18n.get("finance.revenue_goals")));
            add(container);

            container.add(createCspLabel(I18n.get("finance.project_lifespan")));
            container.add(new JLabel());
            lifespanField = new JTextField(FORMAT.format(finance.getLifespan()), 6);
            container.add(lifespanField);
            container.add(new JLabel("<html>" + I18n.get("label.years") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.electricity_selling_price")));
            container.add(new JLabel("$"));
            kWhSellingPriceField = new JTextField(FORMAT.format(finance.getkWhSellingPrice()), 6);
            container.add(kWhSellingPriceField);
            container.add(new JLabel("<html>" + I18n.get("label.per_kwh") + "</html>"));

            SpringUtilities.makeCompactGrid(container, 2, 4, 6, 6, 6, 3);

            container = new JPanel(new SpringLayout());
            container.setBorder(BorderFactory.createTitledBorder(I18n.get("finance.operational_costs")));
            add(container);

            container.add(createCspLabel(I18n.get("finance.land_rental")));
            container.add(new JLabel("$"));
            landCostField = new JTextField(FORMAT.format(finance.getLandRentalCost()), 6);
            container.add(landCostField);
            container.add(new JLabel("<html>" + I18n.get("label.per_year_per_m2") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.cleaning_cost")));
            container.add(new JLabel("$"));
            cleaningCostField = new JTextField(FORMAT.format(finance.getCleaningCost()), 6);
            container.add(cleaningCostField);
            container.add(new JLabel("<html>" + I18n.get("label.per_year_per_unit") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.maintenance_cost")));
            container.add(new JLabel("$"));
            maintenanceCostField = new JTextField(FORMAT.format(finance.getMaintenanceCost()), 6);
            container.add(maintenanceCostField);
            container.add(new JLabel("<html>" + I18n.get("label.per_year_per_unit") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.loan_interest_rate")));
            container.add(new JLabel("%"));
            loanInterestRateField = new JTextField(FORMAT.format(finance.getLoanInterestRate() * 100), 6);
            container.add(loanInterestRateField);
            container.add(new JLabel("<html>" + I18n.get("label.for_upfront_costs") + "</html>"));

            SpringUtilities.makeCompactGrid(container, 4, 4, 6, 6, 6, 3);

            container = new JPanel(new SpringLayout());
            container.setBorder(BorderFactory.createTitledBorder(I18n.get("finance.upfront_costs")));
            add(container);

            container.add(createCspLabel(I18n.get("finance.heliostat_cost")));
            container.add(new JLabel("$"));
            heliostatField = new JTextField(FORMAT.format(finance.getHeliostatUnitCost()), 6);
            container.add(heliostatField);
            container.add(new JLabel("<html>" + I18n.get("label.per_m2") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.fresnel_reflector_cost")));
            container.add(new JLabel("$"));
            fresnelReflectorField = new JTextField(FORMAT.format(finance.getFresnelReflectorUnitCost()), 6);
            container.add(fresnelReflectorField);
            container.add(new JLabel("<html>" + I18n.get("label.per_m2") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.receiver_cost")));
            container.add(new JLabel("$"));
            towerField = new JTextField(FORMAT.format(finance.getReceiverUnitCost()), 6);
            container.add(towerField);
            container.add(new JLabel("<html>" + I18n.get("label.per_meter_height") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.parabolic_trough_cost")));
            container.add(new JLabel("$"));
            parabolicTroughField = new JTextField(FORMAT.format(finance.getParabolicTroughUnitCost()), 6);
            container.add(parabolicTroughField);
            container.add(new JLabel("<html>" + I18n.get("label.per_m2") + "</html>"));

            container.add(createCspLabel(I18n.get("finance.parabolic_dish")));
            container.add(new JLabel("$"));
            parabolicDishField = new JTextField(FORMAT.format(finance.getParabolicDishUnitCost()), 6);
            container.add(parabolicDishField);
            container.add(new JLabel("<html>" + I18n.get("label.per_m2") + "</html>"));

            SpringUtilities.makeCompactGrid(container, 5, 4, 6, 6, 6, 3);

        }

    }

    static JLabel createPvLabel(final String text) {
        final JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(pvBackgroundColor);
        l.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        return l;
    }

    static JLabel createCspLabel(final String text) {
        final JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(cspBackgroundColor);
        l.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 2));
        return l;
    }

    public FinancialSettingsDialog() {

        super(MainFrame.getInstance(), true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(I18n.get("dialog.cost_revenue"));

        tabbedPane = new JTabbedPane();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        final PvSystemFinancePanel pvSystemFinancePanel = new PvSystemFinancePanel();
        pvSystemFinancePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        pvSystemPanel = new JPanel(new BorderLayout());
        pvSystemPanel.add(pvSystemFinancePanel, BorderLayout.NORTH);
        tabbedPane.addTab(I18n.get("tab.pv_system"), pvSystemPanel);

        final CspSystemFinancePanel cspSystemFinancePanel = new CspSystemFinancePanel();
        cspSystemFinancePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        cspSystemPanel = new JPanel(new BorderLayout());
        cspSystemPanel.add(cspSystemFinancePanel, BorderLayout.NORTH);
        tabbedPane.addTab(I18n.get("tab.csp_system"), cspSystemPanel);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton okButton = new JButton(I18n.get("dialog.ok"));
        okButton.addActionListener(e -> {
            int pvLifespan;
            double pvKWhSellPrice;
            double pvLandUnitCost;
            double pvCleaningCost;
            double pvMaintenanceCost;
            double pvLoanInterestCost;
            double solarPanelRackBaseCost;
            double solarPanelRackHeightCost;
            double solarPanelHsatCost;
            double solarPanelVsatCost;
            double solarPanelAadatCost;

            int cspLifespan;
            double cspKWhSellPrice;
            double cspLandUnitCost;
            double cspCleaningCost;
            double cspMaintenanceCost;
            double cspLoanInterestCost;
            double heliostatUnitCost;
            double towerHeightUnitCost;
            double parabolicTroughUnitCost;
            final double parabolicDishUnitCost;
            double fresnelReflectorUnitCost;
            try {
                pvLifespan = Integer.parseInt(pvSystemFinancePanel.lifespanField.getText());
                pvKWhSellPrice = Double.parseDouble(pvSystemFinancePanel.kWhSellingPriceField.getText());
                pvLandUnitCost = Double.parseDouble(pvSystemFinancePanel.landCostField.getText());
                pvCleaningCost = Double.parseDouble(pvSystemFinancePanel.cleaningCostField.getText());
                pvMaintenanceCost = Double.parseDouble(pvSystemFinancePanel.maintenanceCostField.getText());
                pvLoanInterestCost = Double.parseDouble(pvSystemFinancePanel.loanInterestRateField.getText());
                solarPanelRackBaseCost = Double.parseDouble(pvSystemFinancePanel.rackBaseField.getText());
                solarPanelRackHeightCost = Double.parseDouble(pvSystemFinancePanel.rackHeightField.getText());
                solarPanelHsatCost = Double.parseDouble(pvSystemFinancePanel.hsatField.getText());
                solarPanelVsatCost = Double.parseDouble(pvSystemFinancePanel.vsatField.getText());
                solarPanelAadatCost = Double.parseDouble(pvSystemFinancePanel.aadatField.getText());

                cspLifespan = Integer.parseInt(cspSystemFinancePanel.lifespanField.getText());
                cspKWhSellPrice = Double.parseDouble(cspSystemFinancePanel.kWhSellingPriceField.getText());
                cspLandUnitCost = Double.parseDouble(cspSystemFinancePanel.landCostField.getText());
                cspCleaningCost = Double.parseDouble(cspSystemFinancePanel.cleaningCostField.getText());
                cspMaintenanceCost = Double.parseDouble(cspSystemFinancePanel.maintenanceCostField.getText());
                cspLoanInterestCost = Double.parseDouble(cspSystemFinancePanel.loanInterestRateField.getText());
                heliostatUnitCost = Double.parseDouble(cspSystemFinancePanel.heliostatField.getText());
                towerHeightUnitCost = Double.parseDouble(cspSystemFinancePanel.towerField.getText());
                parabolicTroughUnitCost = Double.parseDouble(cspSystemFinancePanel.parabolicTroughField.getText());
                parabolicDishUnitCost = Double.parseDouble(cspSystemFinancePanel.parabolicDishField.getText());
                fresnelReflectorUnitCost = Double.parseDouble(cspSystemFinancePanel.fresnelReflectorField.getText());
            } catch (final NumberFormatException err) {
                err.printStackTrace();
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.invalid_input") + ": " + err.getMessage(), I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            // PV system

            if (pvLifespan < 10 || pvLifespan > 30) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.pv_lifespan_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pvKWhSellPrice <= 0 || pvKWhSellPrice > 1) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.sell_price_kwh_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pvLandUnitCost < 0 || pvLandUnitCost > 1000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.land_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pvCleaningCost < 0 || pvCleaningCost > 100) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.cleaning_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pvMaintenanceCost < 0 || pvMaintenanceCost > 100) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.maintenance_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (pvLoanInterestCost < 0 || pvLoanInterestCost > 100) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.loan_interest_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (solarPanelRackBaseCost < 0 || solarPanelRackBaseCost > 1000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.rack_base_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (solarPanelRackHeightCost < 0 || solarPanelRackHeightCost > 1000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.rack_height_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (solarPanelHsatCost < 0 || solarPanelHsatCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.hsat_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (solarPanelVsatCost < 0 || solarPanelVsatCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.vsat_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (solarPanelAadatCost < 0 || solarPanelAadatCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.aadat_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            // CSP system

            if (cspLifespan < 20 || cspLifespan > 50) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.csp_lifespan_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cspKWhSellPrice <= 0 || cspKWhSellPrice > 1) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.sell_price_kwh_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cspLandUnitCost < 0 || cspLandUnitCost > 1000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.land_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cspCleaningCost < 0 || cspCleaningCost > 100) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.cleaning_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cspMaintenanceCost < 0 || cspMaintenanceCost > 100) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.maintenance_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cspLoanInterestCost < 0 || cspLoanInterestCost > 100) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.loan_interest_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (heliostatUnitCost < 0 || heliostatUnitCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.mirror_unit_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (towerHeightUnitCost < 0 || towerHeightUnitCost > 100000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.tower_height_unit_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (parabolicTroughUnitCost < 0 || parabolicTroughUnitCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.parabolic_trough_unit_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (parabolicDishUnitCost < 0 || parabolicDishUnitCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.parabolic_trough_unit_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fresnelReflectorUnitCost < 0 || fresnelReflectorUnitCost > 10000) {
                JOptionPane.showMessageDialog(FinancialSettingsDialog.this, I18n.get("msg.fresnel_reflector_unit_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            final PvFinancialModel pvFinance = Scene.getInstance().getPvFinancialModel();
            pvFinance.setLifespan(pvLifespan);
            pvFinance.setkWhSellingPrice(pvKWhSellPrice);
            pvFinance.setLandRentalCost(pvLandUnitCost);
            pvFinance.setCleaningCost(pvCleaningCost);
            pvFinance.setMaintenanceCost(pvMaintenanceCost);
            pvFinance.setLoanInterestRate(pvLoanInterestCost * 0.01);
            pvFinance.setSolarPanelRackBaseCost(solarPanelRackBaseCost);
            pvFinance.setSolarPanelRackHeightCost(solarPanelRackHeightCost);
            pvFinance.setSolarPanelHsatCost(solarPanelHsatCost);
            pvFinance.setSolarPanelVsatCost(solarPanelVsatCost);
            pvFinance.setSolarPanelAadatCost(solarPanelAadatCost);

            final CspFinancialModel cspFinance = Scene.getInstance().getCspFinancialModel();
            cspFinance.setLifespan(cspLifespan);
            cspFinance.setkWhSellingPrice(cspKWhSellPrice);
            cspFinance.setLandRentalCost(cspLandUnitCost);
            cspFinance.setCleaningCost(cspCleaningCost);
            cspFinance.setMaintenanceCost(cspMaintenanceCost);
            cspFinance.setLoanInterestRate(cspLoanInterestCost * 0.01);
            cspFinance.setHeliostatUnitCost(heliostatUnitCost);
            cspFinance.setReceiverUnitCost(towerHeightUnitCost);
            cspFinance.setParabolicTroughUnitCost(parabolicTroughUnitCost);
            cspFinance.setParabolicDishUnitCost(parabolicDishUnitCost);
            cspFinance.setFresnelReflectorUnitCost(fresnelReflectorUnitCost);

            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart != null) {
                if (selectedPart instanceof Foundation) {
                    EnergyPanel.getInstance().getPvProjectZoneInfoPanel().update((Foundation) selectedPart);
                    EnergyPanel.getInstance().getCspProjectZoneInfoPanel().update((Foundation) selectedPart);
                } else {
                    final Foundation foundation = selectedPart.getTopContainer();
                    if (foundation != null) {
                        EnergyPanel.getInstance().getPvProjectZoneInfoPanel().update(foundation);
                        EnergyPanel.getInstance().getCspProjectZoneInfoPanel().update(foundation);
                    }
                }
            }

            FinancialSettingsDialog.this.dispose();
            if (runAfterOK != null) {
                runAfterOK.run();
            }

        });
        okButton.setActionCommand("OK");
        buttonPanel.add(okButton);
        getRootPane().setDefaultButton(okButton);

        final JButton cancelButton = new JButton(I18n.get("dialog.cancel"));
        cancelButton.addActionListener(e -> FinancialSettingsDialog.this.dispose());
        cancelButton.setActionCommand("Cancel");
        buttonPanel.add(cancelButton);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(final WindowEvent e) {
                switch (Scene.getInstance().getProjectType()) {
                    case Foundation.TYPE_PV_PROJECT:
                        tabbedPane.setSelectedComponent(pvSystemPanel);
                        break;
                    case Foundation.TYPE_CSP_PROJECT:
                        tabbedPane.setSelectedComponent(cspSystemPanel);
                        break;
                }
            }
        });

        pack();
        setLocationRelativeTo(MainFrame.getInstance());

    }

    public void selectPvPrices() {
        tabbedPane.setSelectedComponent(pvSystemPanel);
    }

    public void selectCspPrices() {
        tabbedPane.setSelectedComponent(cspSystemPanel);
    }

    public void setRunAfterOK(Runnable runAfterOK) {
        this.runAfterOK = runAfterOK;
    }

}