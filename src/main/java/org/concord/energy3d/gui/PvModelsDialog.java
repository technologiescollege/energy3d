package org.concord.energy3d.gui;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.PvFinancialModel;
import org.concord.energy3d.simulation.PvModuleSpecs;
import org.concord.energy3d.simulation.PvModulesData;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Charles Xie
 */

class PvModelsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final static DecimalFormat FORMAT = new DecimalFormat("#0.##");
    private JTextField customSolarPanelPriceField;
    private JTextField[] priceFields;

    PvModelsDialog() {

        super(MainFrame.getInstance(), true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(I18n.get("dialog.pv_models"));

        getContentPane().setLayout(new BorderLayout());

        final JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 8));
        titlePanel.add(new JLabel("<html><font size=2>" + I18n.get("msg.pv_models_info") + "</html>"));
        getContentPane().add(titlePanel, BorderLayout.NORTH);

        final JPanel pvModelPanel = new JPanel(new SpringLayout());
        pvModelPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));
        final JScrollPane scrollPane = new JScrollPane(pvModelPanel);
        scrollPane.setPreferredSize(new Dimension(360, 400));
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        final PvFinancialModel pvFinance = Scene.getInstance().getPvFinancialModel();
        final Map<String, PvModuleSpecs> modules = PvModulesData.getInstance().getModules();
        priceFields = new JTextField[modules.size()];

        List<String> selectedSolarPanelBrands = Scene.getInstance().getSolarPanelBrandNames();

        JLabel label = FinancialSettingsDialog.createPvLabel(I18n.get("label.custom") + ": ");
        if (selectedSolarPanelBrands.contains(I18n.get("label.custom"))) {
            label.setBackground(Color.YELLOW);
        }
        pvModelPanel.add(label);
        pvModelPanel.add(new JLabel("$"));
        customSolarPanelPriceField = new JTextField(FORMAT.format(pvFinance.getCustomSolarPanelCost()), 6);
        pvModelPanel.add(customSolarPanelPriceField);
        pvModelPanel.add(new JLabel(I18n.get("label.custom")));
        int i = 0;
        for (final String key : modules.keySet()) {
            label = FinancialSettingsDialog.createPvLabel(key + ": ");
            pvModelPanel.add(label);
            if (selectedSolarPanelBrands.contains(key)) {
                label.setBackground(Color.YELLOW);
            }
            pvModelPanel.add(new JLabel("$"));
            priceFields[i] = new JTextField(FORMAT.format(pvFinance.getPvModelCost(key)), 6);
            pvModelPanel.add(priceFields[i]);
            pvModelPanel.add(new JLabel(modules.get(key).getBrand()));
            i++;
        }
        SpringUtilities.makeCompactGrid(pvModelPanel, i + 1, 4, 6, 6, 6, 3);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton okButton = new JButton(I18n.get("dialog.ok"));
        okButton.addActionListener(e -> {
            double customSolarPanelCost = 0;
            final double[] pvModelCosts = new double[priceFields.length];
            try {
                customSolarPanelCost = Double.parseDouble(customSolarPanelPriceField.getText());
                for (int j = 0; j < pvModelCosts.length; j++) {
                    pvModelCosts[j] = Double.parseDouble(priceFields[j].getText());
                }
            } catch (final NumberFormatException err) {
                err.printStackTrace();
                JOptionPane.showMessageDialog(PvModelsDialog.this, I18n.get("msg.invalid_input") + ": " + err.getMessage(), I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (customSolarPanelCost < 0 || customSolarPanelCost > 10000) {
                JOptionPane.showMessageDialog(PvModelsDialog.this, I18n.get("msg.solar_panel_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (double pvModelPrice : pvModelCosts) {
                if (pvModelPrice < 0 || pvModelPrice > 10000) {
                    JOptionPane.showMessageDialog(PvModelsDialog.this, I18n.get("msg.solar_panel_price_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            pvFinance.setCustomSolarPanelCost(customSolarPanelCost);
            int k = 0;
            for (final String key : modules.keySet()) {
                pvFinance.setPvModelCost(key, pvModelCosts[k]);
                k++;
            }
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart != null) {
                if (selectedPart instanceof Foundation) {
                    EnergyPanel.getInstance().getPvProjectZoneInfoPanel().update((Foundation) selectedPart);
                } else {
                    final Foundation foundation = selectedPart.getTopContainer();
                    if (foundation != null) {
                        EnergyPanel.getInstance().getPvProjectZoneInfoPanel().update(foundation);
                    }
                }
            }
            dispose();
        });
        okButton.setActionCommand("OK");
        buttonPanel.add(okButton);
        getRootPane().setDefaultButton(okButton);

        final JButton cancelButton = new JButton(I18n.get("dialog.cancel"));
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setActionCommand("Cancel");
        buttonPanel.add(cancelButton);

        pack();
        setLocationRelativeTo(MainFrame.getInstance());

    }

}