package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.Util;

/**
 * @author Charles Xie
 */
class VisualizationSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final static DecimalFormat FORMAT1 = new DecimalFormat("#0.##");

    VisualizationSettingsDialog() {

        super(MainFrame.getInstance(), true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(I18n.get("dialog.visualization_settings"));

        getContentPane().setLayout(new BorderLayout());
        final JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        getContentPane().add(box, BorderLayout.CENTER);

        final Scene s = Scene.getInstance();
        final JTextField solarPanelNxTextField = new JTextField(s.getSolarPanelNx() + "", 6);
        final JTextField solarPanelNyTextField = new JTextField(s.getSolarPanelNy() + "", 6);
        final JTextField rackNxTextField = new JTextField(s.getRackNx() + "", 6);
        final JTextField rackNyTextField = new JTextField(s.getRackNy() + "", 6);
        final JTextField heatVectorLengthTextField = new JTextField(FORMAT1.format(s.getHeatVectorLength()));
        final JTextField heatVectorGridSizeTextField = new JTextField(FORMAT1.format(s.getHeatVectorGridSize() * s.getScale()));

        final ActionListener okListener = e -> {
            double heatVectorLength;
            double heatVectorGridSize;
            int solarPanelNx;
            int solarPanelNy;
            int rackNx;
            int rackNy;
            try {
                heatVectorLength = Double.parseDouble(heatVectorLengthTextField.getText());
                heatVectorGridSize = Double.parseDouble(heatVectorGridSizeTextField.getText());
                solarPanelNx = Integer.parseInt(solarPanelNxTextField.getText());
                solarPanelNy = Integer.parseInt(solarPanelNyTextField.getText());
                rackNx = Integer.parseInt(rackNxTextField.getText());
                rackNy = Integer.parseInt(rackNyTextField.getText());
            } catch (final NumberFormatException err) {
                err.printStackTrace();
                JOptionPane.showMessageDialog(this, I18n.get("msg.invalid_input") + ": " + err.getMessage(),
                        I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (heatVectorLength < 100) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.heat_arrow_length_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (heatVectorGridSize < 0.4) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.heat_arrow_grid_size_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (solarPanelNx < 2 || solarPanelNy < 2) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.solar_panel_grid_min"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Util.isPowerOfTwo(solarPanelNx) || !Util.isPowerOfTwo(solarPanelNy)) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.solar_panel_grid_power_of_two"), I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (rackNx < 2 || rackNy < 2) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.solar_rack_grid_min"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Util.isPowerOfTwo(rackNx) || !Util.isPowerOfTwo(rackNy)) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.solar_rack_grid_power_of_two"), I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            s.setSolarPanelNx(solarPanelNx);
            s.setSolarPanelNy(solarPanelNy);
            s.setRackNx(rackNx);
            s.setRackNy(rackNy);
            s.setHeatVectorLength(heatVectorLength);
            s.setHeatFluxGridSize(heatVectorGridSize / s.getScale());
            s.setEdited(true);
            if (SceneManager.getInstance().getSolarHeatMap()) {
                EnergyPanel.getInstance().updateRadiationHeatMap();
            }
            dispose();
        };

        /* Solar radiation heat maps */

        JPanel panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8),
                BorderFactory.createTitledBorder("<html><font size=2><b>" + I18n.get("title.solar_radiation_heat_maps") + "</html>")));
        box.add(panel);

        // set number of grid points for creating a radiation heat map on a solar panel
        panel.add(new JLabel(" " + I18n.get("label.solar_panels") + ": "));
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p1.add(solarPanelNxTextField);
        p1.add(new JLabel("  \u00D7  "));
        p1.add(solarPanelNyTextField);
        panel.add(p1);
        panel.add(new JLabel(I18n.get("label.must_be_power_of_2")));

        // set number of grid points for creating a radiation heat map on a solar rack
        panel.add(new JLabel(" " + I18n.get("label.solar_panel_racks") + ": "));
        p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p1.add(rackNxTextField);
        p1.add(new JLabel("  \u00D7  "));
        p1.add(rackNyTextField);
        panel.add(p1);
        panel.add(new JLabel(I18n.get("label.must_be_power_of_2")));

        /* Heat flux arrows */

        panel = new JPanel(new GridLayout(2, 3, 8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8),
                BorderFactory.createTitledBorder("<html><font size=2><b>" + I18n.get("title.heat_flux_arrows") + "</html>")));
        box.add(panel);

        // set the heat arrow length
        panel.add(new JLabel(" " + I18n.get("label.heat_arrow_length") + ": "));
        panel.add(heatVectorLengthTextField);
        panel.add(new JLabel(I18n.get("label.internal_unit")));

        // set the heat arrow grid size
        panel.add(new JLabel(" " + I18n.get("label.heat_arrow_grid_size") + ": "));
        panel.add(heatVectorGridSizeTextField);
        panel.add(new JLabel(I18n.get("label.meter")));

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton okButton = new JButton(I18n.get("dialog.ok"));
        okButton.addActionListener(okListener);
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