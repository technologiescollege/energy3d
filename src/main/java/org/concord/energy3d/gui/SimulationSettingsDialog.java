package org.concord.energy3d.gui;

import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.SolarRadiation;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;
import org.concord.energy3d.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * @author Charles Xie
 */
class SimulationSettingsDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final static DecimalFormat FORMAT2 = new DecimalFormat("##.##");
    private JTextField cellSizeTextField;

    SimulationSettingsDialog() {

        super(MainFrame.getInstance(), true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(I18n.get("dialog.simulation_settings"));

        getContentPane().setLayout(new BorderLayout());
        final JPanel panel = new JPanel(new SpringLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panel, BorderLayout.CENTER);

        final Scene s = Scene.getInstance();
        final JTextField mirrorNxTextField = new JTextField(s.getMirrorNx() + "", 6);
        final JTextField mirrorNyTextField = new JTextField(s.getMirrorNy() + "", 6);
        final JTextField parabolicDishNTextField = new JTextField(s.getParabolicDishN() + "", 6);
        final JTextField rackCellSizeTextField = new JTextField(FORMAT2.format(s.getRackCellSize()));
        final JLabel rackCellSizeLabelLeft = new JLabel(I18n.get("label.rack_cell_size"));
        final JLabel rackCellSizeLabelRight = new JLabel(I18n.get("label.meters"));
        final JLabel rackModelLabelRight = new JLabel(I18n.get("label.fast_but_inaccurate"));
        final JTextField timeStepTextField = new JTextField(FORMAT2.format(s.getTimeStep()));
        cellSizeTextField = new JTextField(FORMAT2.format(s.getSolarStep() * s.getScale()));

        final JComboBox<String> airMassComboBox = new JComboBox<>(new String[]{I18n.get("label.none"), I18n.get("air_mass.kasten_young"), I18n.get("air_mass.sphere_model")});
        airMassComboBox.setSelectedIndex(SolarRadiation.getInstance().getAirMassSelection() + 1);

        final JComboBox<String> rackModelComboBox = new JComboBox<>(new String[]{I18n.get("rack_model.approximate"), I18n.get("rack_model.exact")});
        rackModelComboBox.addItemListener(e -> {
            final boolean approximate = rackModelComboBox.getSelectedIndex() == 0;
            rackCellSizeTextField.setEnabled(approximate);
            rackCellSizeLabelLeft.setEnabled(approximate);
            rackCellSizeLabelRight.setEnabled(approximate);
            rackModelLabelRight.setText(approximate ? I18n.get("label.fast_but_inaccurate") : I18n.get("label.slow_but_accurate"));
        });
        rackModelComboBox.setSelectedIndex(s.isRackModelExact() ? 1 : 0);

        final JComboBox<String> calculateRoiComboBox = new JComboBox<>(new String[]{I18n.get("common.no"), I18n.get("common.yes")});
        calculateRoiComboBox.setSelectedIndex(s.getCalculateRoi() ? 1 : 0);

        final ActionListener okListener = e -> {
            int mirrorNx;
            int mirrorNy;
            int parabolicDishN;
            double rackCellSize;
            double cellSize;
            int timeStep;
            try {
                mirrorNx = Integer.parseInt(mirrorNxTextField.getText());
                mirrorNy = Integer.parseInt(mirrorNyTextField.getText());
                parabolicDishN = Integer.parseInt(parabolicDishNTextField.getText());
                rackCellSize = Double.parseDouble(rackCellSizeTextField.getText());
                cellSize = Double.parseDouble(cellSizeTextField.getText());
                timeStep = (int) Double.parseDouble(timeStepTextField.getText());
            } catch (final NumberFormatException err) {
                err.printStackTrace();
                JOptionPane.showMessageDialog(this, I18n.get("msg.invalid_input") + ": " + err.getMessage(), I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            // range check
            if (cellSize < 0.01 || cellSize > 100) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.cell_size_range"),
                        I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            // power of two check
            if (!Util.isPowerOfTwo(mirrorNx) || !Util.isPowerOfTwo(mirrorNy)) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.mirror_grid_power_of_two"),
                        I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            // range check
            if (timeStep < 5 || timeStep > 60) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.time_step_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (rackCellSize < 0.5 || rackCellSize > 50) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.rack_cell_size_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (mirrorNx < 2 || mirrorNy < 2 || parabolicDishN < 2) {
                JOptionPane.showMessageDialog(this, I18n.get("msg.mirror_grid_min"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            s.setMirrorNx(mirrorNx);
            s.setMirrorNy(mirrorNy);
            s.setParabolicDishN(parabolicDishN);
            s.setRackModelExact(rackModelComboBox.getSelectedIndex() == 1);
            s.setRackCellSize(rackCellSize);
            s.setTimeStep(timeStep);
            s.setSolarStep(cellSize / s.getScale());
            s.setCalculateRoi(calculateRoiComboBox.getSelectedIndex() == 1);
            s.setEdited(true);
            SolarRadiation.getInstance().setAirMassSelection(airMassComboBox.getSelectedIndex() - 1);
            if (SceneManager.getInstance().getSolarHeatMap()) {
                EnergyPanel.getInstance().updateRadiationHeatMap();
            }
            SimulationSettingsDialog.this.dispose();
        };

        // set the grid size ("solar step")
        panel.add(new JLabel(I18n.get("label.cell_size")));
        panel.add(cellSizeTextField);
        panel.add(new JLabel(I18n.get("label.meter")));

        // set the time step
        panel.add(new JLabel(I18n.get("label.time_step")));
        panel.add(timeStepTextField);
        timeStepTextField.setColumns(6);
        panel.add(new JLabel(I18n.get("label.minutes")));

        // select the model for racks
        panel.add(new JLabel(I18n.get("label.solar_panel_rack_model")));
        panel.add(rackModelComboBox);
        panel.add(rackModelLabelRight);

        // set number of grid points for a solar rack, used in both heat map generation and energy calculation
        panel.add(rackCellSizeLabelLeft);
        panel.add(rackCellSizeTextField);
        panel.add(rackCellSizeLabelRight);

        // set number of grid points for a mirror, used in both heat map generation and energy calculation
        panel.add(new JLabel(I18n.get("label.heliostat_mirror_mesh")));
        final JPanel p1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        p1.add(mirrorNxTextField);
        p1.add(new JLabel("  \u00D7  "));
        p1.add(mirrorNyTextField);
        panel.add(p1);
        panel.add(new JLabel(I18n.get("label.must_be_power_of_2")));

        // set number of grid points for a parabolic dish, used in both heat map generation and energy calculation
        panel.add(new JLabel(I18n.get("label.parabolic_dish_mesh")));
        panel.add(parabolicDishNTextField);
        panel.add(new JLabel(I18n.get("label.must_be_power_of_2")));

        // choose air mass
        panel.add(new JLabel(I18n.get("label.air_mass")));
        panel.add(airMassComboBox);
        panel.add(new JLabel());

        // choose to calculate ROI or not
        panel.add(new JLabel(I18n.get("label.financial_analysis")));
        panel.add(calculateRoiComboBox);
        panel.add(new JLabel());

        SpringUtilities.makeCompactGrid(panel, 8, 3, 8, 8, 8, 8);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton okButton = new JButton(I18n.get("dialog.ok"));
        okButton.addActionListener(okListener);
        okButton.setActionCommand("OK");
        buttonPanel.add(okButton);
        getRootPane().setDefaultButton(okButton);

        final JButton cancelButton = new JButton(I18n.get("dialog.cancel"));
        cancelButton.addActionListener(e -> SimulationSettingsDialog.this.dispose());
        cancelButton.setActionCommand("Cancel");
        buttonPanel.add(cancelButton);

        pack();
        setLocationRelativeTo(MainFrame.getInstance());

    }

    JTextField getCellSizeField() {
        return cellSizeTextField;
    }

}