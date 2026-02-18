package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.simulation.AnnualGraph;
import org.concord.energy3d.simulation.UtilityBill;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

/**
 * @author Charles Xie
 */
class UtilityBillDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final static DecimalFormat FORMAT1 = new DecimalFormat("#0.##");

    UtilityBillDialog(final UtilityBill utilityBill) {

        super(MainFrame.getInstance(), true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(I18n.get("dialog.utility_bill"));

        final JLabel[] labels = new JLabel[12];
        final JTextField[] fields = new JTextField[12];
        for (int i = 0; i < 12; i++) {
            labels[i] = new JLabel(AnnualGraph.getThreeLetterMonth()[i]);
            fields[i] = new JTextField(FORMAT1.format(utilityBill.getMonthlyEnergy(i)), 10);
        }

        getContentPane().setLayout(new BorderLayout());
        final JPanel container = new JPanel(new GridLayout(1, 2, 10, 10));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        getContentPane().add(container, BorderLayout.CENTER);

        final JPanel panel = new JPanel();
        container.add(panel);
        panel.setLayout(new SpringLayout());
        for (int i = 0; i < 12; i++) {
            panel.add(labels[i]);
            panel.add(fields[i]);
        }
        SpringUtilities.makeCompactGrid(panel, 6, 4, 6, 6, 6, 6);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton okButton = new JButton(I18n.get("dialog.ok"));
        okButton.addActionListener(e -> {
            final double[] x = new double[12];
            try {
                for (int i = 0; i < 12; i++) {
                    x[i] = Double.parseDouble(fields[i].getText());
                }
            } catch (final NumberFormatException err) {
                err.printStackTrace();
                JOptionPane.showMessageDialog(UtilityBillDialog.this, I18n.get("msg.invalid_input") + ": " + err.getMessage(), I18n.get("msg.invalid_input_title"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            for (int i = 0; i < 12; i++) {
                if (x[i] < 0) {
                    JOptionPane.showMessageDialog(UtilityBillDialog.this, I18n.get("msg.energy_usage_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                utilityBill.setMonthlyEnergy(i, x[i]);
            }
            Scene.getInstance().setEdited(true);
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