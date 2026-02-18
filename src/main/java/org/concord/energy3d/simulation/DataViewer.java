package org.concord.energy3d.simulation;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.model.Door;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Mirror;
import org.concord.energy3d.model.Rack;
import org.concord.energy3d.model.Roof;
import org.concord.energy3d.model.Sensor;
import org.concord.energy3d.model.SolarPanel;
import org.concord.energy3d.model.Wall;
import org.concord.energy3d.model.Window;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 */
class DataViewer {

    private DataViewer() {
    }

    /** Returns the display label for a data table header key (internal keys stay in English for getData). */
    private static String headerKeyToDisplayLabel(final String key) {
        if (key == null) return "";
        if ("Hour".equals(key)) return I18n.get("axis.hour");
        if ("Month".equals(key)) return I18n.get("axis.month");
        if ("Windows".equals(key)) return I18n.get("chart.series.windows");
        if ("Solar Panels".equals(key)) return I18n.get("chart.series.solar_panels");
        if ("Heater".equals(key)) return I18n.get("chart.series.heater");
        if ("AC".equals(key)) return I18n.get("chart.series.ac");
        if ("Net".equals(key)) return I18n.get("chart.series.net");
        if ("Solar".equals(key)) return I18n.get("chart.series.solar");
        if ("Heat Gain".equals(key)) return I18n.get("series.heat_gain");
        if (key.startsWith("Solar ")) return I18n.get("chart.series.solar") + " " + key.substring(6);
        if (key.startsWith("Heat Gain ")) return I18n.get("series.heat_gain") + " " + key.substring(10);
        if (key.startsWith("PV ")) return I18n.get("panel.title_pv") + " " + key.substring(3);
        if (key.startsWith("CSP ")) return I18n.get("panel.title_csp") + " " + key.substring(4);
        if (key.startsWith("Building ")) return I18n.get("panel.title_building") + " " + key.substring(9);
        return key;
    }

    @SuppressWarnings("serial")
    private static void showDataWindow(final String title, final Object[][] column, final String[] header, final java.awt.Window parent) {
        final JDialog dataWindow = new JDialog(JOptionPane.getFrameForComponent(parent), title, true);
        dataWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        final JTable table = new JTable(column, header);
        table.setModel(new DefaultTableModel(column, header) {
            @Override
            public boolean isCellEditable(final int row, final int col) {
                return false;
            }
        });
        dataWindow.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);
        final JPanel p = new JPanel();
        dataWindow.getContentPane().add(p, BorderLayout.SOUTH);
        JButton button = new JButton(I18n.get("common.copy_data"));
        button.addActionListener(e -> {
            table.selectAll();
            final ActionEvent ae = new ActionEvent(table, ActionEvent.ACTION_PERFORMED, "copy");
            table.getActionMap().get(ae.getActionCommand()).actionPerformed(ae);
            JOptionPane.showMessageDialog(parent, I18n.get("msg.data_ready_for_pasting"), I18n.get("title.copy_data"), JOptionPane.INFORMATION_MESSAGE);
            table.clearSelection();
        });
        button.setToolTipText(I18n.get("tooltip.copy_data_clipboard"));
        p.add(button);
        button = new JButton(I18n.get("common.close"));
        button.addActionListener(e -> dataWindow.dispose());
        p.add(button);
        dataWindow.pack();
        dataWindow.setLocationRelativeTo(parent);
        dataWindow.setVisible(true);
    }

    static void viewRawData(final java.awt.Window parent, final Graph graph, final boolean selectAll) {
        String[] header = null;
        if (graph instanceof BuildingEnergyDailyGraph) {
            header = new String[]{"Hour", "Windows", "Solar Panels", "Heater", "AC", "Net"};
        } else if (graph instanceof BuildingEnergyAnnualGraph) {
            header = new String[]{"Month", "Windows", "Solar Panels", "Heater", "AC", "Net"};
        } else if (graph instanceof PartEnergyDailyGraph) {
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectAll || selectedPart instanceof SolarPanel || selectedPart instanceof Rack || selectedPart instanceof Mirror || selectedPart instanceof Foundation) {
                header = new String[]{"Hour", "Solar"};
            } else if (selectedPart instanceof Wall || selectedPart instanceof Roof || selectedPart instanceof Door) {
                header = new String[]{"Hour", "Heat Gain"};
            } else if (selectedPart instanceof Window) {
                header = new String[]{"Hour", "Solar", "Heat Gain"};
            }
            if (graph.instrumentType == Graph.SENSOR) {
                final List<HousePart> parts = Scene.getInstance().getParts();
                final List<String> sensorList = new ArrayList<String>();
                for (final HousePart p : parts) {
                    if (p instanceof Sensor) {
                        final Sensor sensor = (Sensor) p;
                        String label = sensor.getLabelText() != null ? sensor.getLabelText() : sensor.getId() + "";
                        if (!sensor.isLightOff()) {
                            sensorList.add(I18n.get("series.light") + ": #" + label);
                        }
                        if (!sensor.isHeatFluxOff()) {
                            sensorList.add(I18n.get("series.heat_flux") + ": #" + label);
                        }
                    }
                }
                if (!sensorList.isEmpty()) {
                    header = new String[1 + sensorList.size()];
                    header[0] = "Hour";
                    for (int i = 1; i < header.length; i++) {
                        header[i] = sensorList.get(i - 1);
                    }
                }
            }
        } else if (graph instanceof PartEnergyAnnualGraph) {
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectAll || selectedPart instanceof SolarPanel || selectedPart instanceof Rack || selectedPart instanceof Mirror || selectedPart instanceof Foundation) {
                header = new String[]{"Month", "Solar"};
            } else if (selectedPart instanceof Wall || selectedPart instanceof Roof || selectedPart instanceof Door) {
                header = new String[]{"Month", "Heat Gain"};
            } else if (selectedPart instanceof Window) {
                header = new String[]{"Month", "Solar", "Heat Gain"};
            }
            if (graph.instrumentType == Graph.SENSOR) {
                final List<HousePart> parts = Scene.getInstance().getParts();
                final List<String> sensorList = new ArrayList<String>();
                for (final HousePart p : parts) {
                    if (p instanceof Sensor) {
                        final Sensor sensor = (Sensor) p;
                        String label = sensor.getLabelText() != null ? sensor.getLabelText() : sensor.getId() + "";
                        if (!sensor.isLightOff()) {
                            sensorList.add(I18n.get("series.light") + ": #" + label);
                        }
                        if (!sensor.isHeatFluxOff()) {
                            sensorList.add(I18n.get("series.heat_flux") + ": #" + label);
                        }
                    }
                }
                if (!sensorList.isEmpty()) {
                    header = new String[1 + sensorList.size()];
                    header[0] = "Month";
                    for (int i = 1; i < header.length; i++) {
                        header[i] = sensorList.get(i - 1);
                    }
                }
            }
        }
        if (header == null) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.problem_finding_data"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        final int m = header.length;
        final int n = graph.getLength();
        final Object[][] column = new Object[n][m + 1];
        for (int i = 0; i < n; i++) {
            column[i][0] = header[0].equals("Hour") ? i : (i + 1);
        }
        for (int j = 1; j < m; j++) {
            final List<Double> list = graph.getData(header[j]);
            for (int i = 0; i < n; i++) {
                column[i][j] = list.get(i);
            }
        }
        final String[] headerDisplay = new String[m];
        for (int j = 0; j < m; j++) {
            headerDisplay[j] = headerKeyToDisplayLabel(header[j]);
        }
        showDataWindow(I18n.get("title.data"), column, headerDisplay, parent);
    }

    static void viewRawData(final java.awt.Window parent, final Graph graph, final List<HousePart> selectedParts) {
        if (selectedParts == null || selectedParts.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_part_selected"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        final ArrayList<String> headers = new ArrayList<>();
        if (graph instanceof PartEnergyDailyGraph) {
            headers.add("Hour");
        } else if (graph instanceof PartEnergyAnnualGraph) {
            headers.add("Month");
        }
        for (final HousePart p : selectedParts) {
            if (p instanceof SolarPanel || p instanceof Rack || p instanceof Mirror) {
                headers.add("Solar " + p.getId());
            } else if (p instanceof Wall || p instanceof Roof || p instanceof Door) {
                headers.add("Heat Gain " + p.getId());
            } else if (p instanceof Window) {
                headers.add("Solar " + p.getId());
                headers.add("Heat Gain " + p.getId());
            } else if (p instanceof Foundation) {
                final Foundation foundation = (Foundation) p;
                switch (foundation.getProjectType()) {
                    case Foundation.TYPE_PV_PROJECT:
                        headers.add("PV " + p.getId());
                        break;
                    case Foundation.TYPE_CSP_PROJECT:
                        headers.add("CSP " + p.getId());
                        break;
                    case Foundation.TYPE_BUILDING:
                        headers.add("Building " + p.getId());
                        break;
                }
            }
        }
        final String[] headersArray = new String[headers.size()];
        for (int i = 0; i < headersArray.length; i++) {
            headersArray[i] = headers.get(i);
        }
        final int m = headersArray.length;
        final int n = graph.getLength();
        final Object[][] column = new Object[n][m + 1];
        for (int i = 0; i < n; i++) {
            column[i][0] = (i + 1);
        }
        for (int j = 1; j < m; j++) {
            final List<Double> list = graph.getData(headersArray[j]);
            for (int i = 0; i < n; i++) {
                column[i][j] = list.get(i);
            }
        }
        final String[] headerDisplay = new String[m];
        for (int j = 0; j < m; j++) {
            headerDisplay[j] = headerKeyToDisplayLabel(headersArray[j]);
        }
        showDataWindow(I18n.get("title.data"), column, headerDisplay, parent);
    }

}