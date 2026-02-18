package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

import org.concord.energy3d.model.SolarPanel;
import org.concord.energy3d.simulation.PvModuleSpecs;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

/**
 * @author Charles Xie
 */
class PropertiesDialogForSolarPanel extends PropertiesDialogFactory {

    static JDialog getDialog(final SolarPanel solarPanel) {

        final JDialog dialog = new JDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel"), true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        final String info = solarPanel.toString().substring(0, solarPanel.toString().indexOf(')') + 1);
        dialog.setTitle(I18n.get("dialog.properties_part", info));

        dialog.getContentPane().setLayout(new BorderLayout());
        final JPanel panel = new JPanel(new SpringLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        final JScrollPane scroller = new JScrollPane(panel);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setPreferredSize(new Dimension(400, 300));
        dialog.getContentPane().add(scroller, BorderLayout.CENTER);

        final PvModuleSpecs specs = solarPanel.getPvModuleSpecs();

        int i = 0;

        panel.add(new JLabel(I18n.get("label.manufacturer")));
        final JTextField brandField = new JTextField(specs.getBrand());
        brandField.setEditable(false);
        panel.add(brandField);
        i++;

        panel.add(new JLabel(I18n.get("label.model")));
        final JTextField modelField = new JTextField(specs.getModel());
        modelField.setEditable(false);
        panel.add(modelField);
        i++;

        panel.add(new JLabel(I18n.get("label.color")));
        final JTextField colorField = new JTextField(specs.getColor());
        colorField.setEditable(false);
        panel.add(colorField);
        i++;

        panel.add(new JLabel(I18n.get("label.cell_type")));
        final JTextField cellTypeField = new JTextField(specs.getCellType());
        cellTypeField.setEditable(false);
        panel.add(cellTypeField);
        i++;

        panel.add(new JLabel(I18n.get("label.cell_efficiency")));
        final JTextField cellEfficiencyField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getCelLEfficiency() * 100) + "%");
        cellEfficiencyField.setEditable(false);
        panel.add(cellEfficiencyField);
        i++;

        panel.add(new JLabel(I18n.get("label.dimension")));
        final JTextField dimensionField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getLength()) + "\u00D7" +
                PopupMenuFactory.threeDecimalsFormat.format(specs.getWidth()) + "\u00D7" + PopupMenuFactory.threeDecimalsFormat.format(specs.getThickness()) + " m");
        dimensionField.setEditable(false);
        panel.add(dimensionField);
        i++;

        panel.add(new JLabel(I18n.get("label.cells")));
        final JTextField cellsField = new JTextField(specs.getLayout().width + "\u00D7" + specs.getLayout().height);
        cellsField.setEditable(false);
        panel.add(cellsField);
        i++;

        panel.add(new JLabel(I18n.get("label.maximal_power")));
        final JTextField pmaxField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getPmax()) + " W");
        pmaxField.setEditable(false);
        panel.add(pmaxField);
        i++;

        panel.add(new JLabel(I18n.get("label.voltage_mpp")));
        final JTextField vmppField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getVmpp()) + " V");
        vmppField.setEditable(false);
        panel.add(vmppField);
        i++;

        panel.add(new JLabel(I18n.get("label.current_mpp")));
        final JTextField imppField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getImpp()) + " A");
        imppField.setEditable(false);
        panel.add(imppField);
        i++;

        panel.add(new JLabel(I18n.get("label.voltage_oc")));
        final JTextField vocField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getVoc()) + " V");
        vocField.setEditable(false);
        panel.add(vocField);
        i++;

        panel.add(new JLabel(I18n.get("label.current_sc")));
        final JTextField iscField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getIsc()) + " A");
        iscField.setEditable(false);
        panel.add(iscField);
        i++;

        panel.add(new JLabel(I18n.get("label.noct")));
        final JTextField noctField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getNoct()) + " \u00B0C");
        noctField.setEditable(false);
        panel.add(noctField);
        i++;

        panel.add(new JLabel(I18n.get("label.temp_coefficient_power")));
        final JTextField pmaxTcField = new JTextField(PopupMenuFactory.sixDecimalsFormat.format(specs.getPmaxTc()) + "%/\u00B0C");
        pmaxTcField.setEditable(false);
        panel.add(pmaxTcField);
        i++;

        panel.add(new JLabel(I18n.get("label.weight")));
        final JTextField weightField = new JTextField(PopupMenuFactory.threeDecimalsFormat.format(specs.getWeight()) + " kg");
        weightField.setEditable(false);
        panel.add(weightField);
        i++;

        panel.add(new JLabel(I18n.get("label.tracker")));
        final JTextField trackerField = new JTextField(solarPanel.getTrackerName() == null ? I18n.get("label.none") : solarPanel.getTrackerName());
        trackerField.setEditable(false);
        panel.add(trackerField);
        i++;

        SpringUtilities.makeCompactGrid(panel, i, 2, 4, 4, 4, 4);

        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton button = new JButton(I18n.get("dialog.close"));
        button.addActionListener(e -> dialog.dispose());
        buttonPanel.add(button);

        dialog.pack();

        return dialog;

    }

}