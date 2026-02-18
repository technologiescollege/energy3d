package org.concord.energy3d.gui;

import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import org.concord.energy3d.geneticalgorithms.applications.*;
import org.concord.energy3d.logger.TimeSeriesLogger;
import org.concord.energy3d.model.*;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.simulation.*;
import org.concord.energy3d.undo.*;
import org.concord.energy3d.util.*;
import org.concord.energy3d.util.I18n;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class PopupMenuForFoundation extends PopupMenuFactory {

    private static JPopupMenu popupMenuForFoundation;
    private static double solarPanelArrayPoleHeight = 1;
    private static int solarPanelArrayRowAxis = 0;
    private static double solarPanelArrayRowSpacing = solarPanelHeight + 1;
    private static double solarPanelArrayColSpacing = solarPanelWidth + 1;
    private static int solarPanelOrientation = 0;
    private static String solarPanelModel = I18n.get("model.custom");
    private static int solarPanelColorOption = SolarPanel.COLOR_OPTION_BLUE;
    private static int solarPanelCellType = SolarPanel.MONOCRYSTALLINE;
    private static double solarPanelRackArrayInterRowSpacing = solarPanelWidth * solarPanelRowsPerRack + 1;
    private static double solarPanelRackPoleSpacingX = 4;
    private static double solarPanelRackPoleSpacingY = 2;
    private static double solarPanelRackPoleHeight = 3;
    private static double solarPanelTiltAngle = 0;
    private static int solarPanelShadeTolerance = SolarPanel.PARTIAL_SHADE_TOLERANCE;
    private static HeliostatRectangularFieldLayout heliostatRectangularFieldLayout = new HeliostatRectangularFieldLayout();
    private static HeliostatConcentricFieldLayout heliostatConcentricFieldLayout = new HeliostatConcentricFieldLayout();
    private static HeliostatSpiralFieldLayout heliostatSpiralFieldLayout = new HeliostatSpiralFieldLayout();

    static class SolarPanelArrayLayoutManager {

        private Foundation foundation;
        private JComboBox<String> modelComboBox;
        private JComboBox<String> orientationComboBox;
        private JComboBox<String> sizeComboBox;
        private JComboBox<String> cellTypeComboBox;
        private JComboBox<String> colorOptionComboBox;
        private JComboBox<String> shadeToleranceComboBox;
        private JComboBox<String> rowAxisComboBox;
        private JTextField cellEfficiencyField;
        private JTextField noctField;
        private JTextField pmaxTcField;
        private int numberOfCellsInX = 6;
        private int numberOfCellsInY = 10;

        SolarPanelArrayLayoutManager() {
        }

        private void enableSettings(final boolean b) {
            sizeComboBox.setEnabled(b);
            cellTypeComboBox.setEnabled(b);
            colorOptionComboBox.setEnabled(b);
            shadeToleranceComboBox.setEnabled(b);
            cellEfficiencyField.setEnabled(b);
            noctField.setEnabled(b);
            pmaxTcField.setEnabled(b);
        }

        private void addSolarRackArrays() {
            solarPanelColorOption = colorOptionComboBox.getSelectedIndex();
            solarPanelCellType = cellTypeComboBox.getSelectedIndex();
            solarPanelShadeTolerance = shadeToleranceComboBox.getSelectedIndex();
            solarPanelArrayRowAxis = rowAxisComboBox.getSelectedIndex();
            solarPanelModel = (String) modelComboBox.getSelectedItem();
            final SolarPanel sp = new SolarPanel();
            sp.setModelName((String) modelComboBox.getSelectedItem());
            sp.setRotated(solarPanelOrientation == 0);
            sp.setCellType(solarPanelCellType);
            sp.setColorOption(solarPanelColorOption);
            sp.setPanelWidth(solarPanelWidth);
            sp.setPanelHeight(solarPanelHeight);
            sp.setNumberOfCellsInX(numberOfCellsInX);
            sp.setNumberOfCellsInY(numberOfCellsInY);
            sp.setShadeTolerance(solarPanelShadeTolerance);
            sp.setCellEfficiency(solarCellEfficiencyPercentage * 0.01);
            sp.setInverterEfficiency(inverterEfficiencyPercentage * 0.01);
            sp.setTemperatureCoefficientPmax(solarPanelTemperatureCoefficientPmaxPercentage * 0.01);
            sp.setNominalOperatingCellTemperature(solarPanelNominalOperatingCellTemperature);
            SceneManager.getTaskManager().update(() -> {
                foundation.addSolarRackArrays(sp, solarPanelTiltAngle, solarPanelRackPoleHeight, solarPanelRowsPerRack, solarPanelRackArrayInterRowSpacing, solarPanelArrayRowAxis, solarPanelRackPoleSpacingX, solarPanelRackPoleSpacingY);
                return null;
            });
            updateAfterEdit();
        }

        void open(final int operationType) {
            final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
            if (selectedPart instanceof Foundation) {
                foundation = (Foundation) selectedPart;
                int n = foundation.countParts(Rack.class);
                if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_racks", n), I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                    return;
                }
                n = foundation.countParts(SolarPanel.class);
                if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_panels", n), I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                    return;
                }

                final List<Rack> racks = foundation.getRacks();
                if (racks != null && !racks.isEmpty()) {
                    final Rack rack0 = racks.get(0);
                    final SolarPanel solarPanel = rack0.getSolarPanel();
                    solarPanelModel = solarPanel.getModelName();
                    solarPanelColorOption = solarPanel.getColorOption();
                    solarPanelCellType = solarPanel.getCellType();
                    solarPanelShadeTolerance = solarPanel.getShadeTolerance();
                    solarPanelWidth = solarPanel.getPanelWidth();
                    solarPanelHeight = solarPanel.getPanelHeight();
                    solarCellEfficiencyPercentage = solarPanel.getCellEfficiency() * 100;
                    inverterEfficiencyPercentage = solarPanel.getInverterEfficiency() * 100;
                    solarPanelNominalOperatingCellTemperature = solarPanel.getNominalOperatingCellTemperature();
                    solarPanelTemperatureCoefficientPmaxPercentage = solarPanel.getTemperatureCoefficientPmax();
                    solarPanelOrientation = solarPanel.isRotated() ? 0 : 1;
                    solarPanelTiltAngle = rack0.getTiltAngle();
                    solarPanelRowsPerRack = rack0.getSolarPanelRowAndColumnNumbers()[1];
                    solarPanelRackPoleHeight = rack0.getPoleHeight() * Scene.getInstance().getScale();
                    solarPanelRackPoleSpacingX = rack0.getPoleDistanceX();
                    solarPanelRackPoleSpacingY = rack0.getPoleDistanceY();
                    if (racks.size() > 1) {
                        final Rack rack1 = racks.get(1);
                        final Vector3 p = foundation.getAbsPoint(1).subtract(foundation.getAbsPoint(0), null);
                        final Vector3 q = rack0.getAbsCenter().subtractLocal(rack1.getAbsCenter());
                        solarPanelRackArrayInterRowSpacing = Math.abs(q.dot(p.normalize(null))) * Scene.getInstance().getScale();
                    }
                }

                final JPanel panel = new JPanel(new SpringLayout());

                int rowCount = 0;

                final Map<String, PvModuleSpecs> modules = PvModulesData.getInstance().getModules();
                final String[] models = new String[modules.size() + 1];
                int j = 0;
                models[j] = I18n.get("model.custom");
                for (final String key : modules.keySet()) {
                    models[++j] = key;
                }
                modelComboBox = new JComboBox<>(models);
                modelComboBox.setSelectedItem(solarPanelModel);
                cellTypeComboBox = new JComboBox<>(new String[]{I18n.get("cell_type.polycrystalline"), I18n.get("cell_type.monocrystalline"), I18n.get("cell_type.thin_film")});
                cellTypeComboBox.setSelectedIndex(solarPanelCellType);
                colorOptionComboBox = new JComboBox<>(new String[]{I18n.get("color.blue"), I18n.get("color.black"), I18n.get("color.gray")});
                colorOptionComboBox.setSelectedIndex(solarPanelColorOption);
                sizeComboBox = new JComboBox<>(solarPanelNominalSize.getStrings());
                final int nItems = sizeComboBox.getItemCount();
                for (int i = 0; i < nItems; i++) {
                    if (Util.isZero(solarPanelHeight - solarPanelNominalSize.getNominalHeights()[i]) && Util.isZero(solarPanelWidth - solarPanelNominalSize.getNominalWidths()[i])) {
                        sizeComboBox.setSelectedIndex(i);
                    }
                }
                cellEfficiencyField = new JTextField(threeDecimalsFormat.format(solarCellEfficiencyPercentage));
                noctField = new JTextField(threeDecimalsFormat.format(solarPanelNominalOperatingCellTemperature));
                pmaxTcField = new JTextField(sixDecimalsFormat.format(solarPanelTemperatureCoefficientPmaxPercentage));
                shadeToleranceComboBox = new JComboBox<>(new String[]{I18n.get("shade_tolerance.partial"), I18n.get("shade_tolerance.high"), I18n.get("shade_tolerance.none")});
                shadeToleranceComboBox.setSelectedIndex(solarPanelShadeTolerance);
                final JTextField inverterEfficiencyField = new JTextField(threeDecimalsFormat.format(inverterEfficiencyPercentage));

                if (operationType != 1) {

                    panel.add(new JLabel(I18n.get("label.solar_panel_model")));
                    modelComboBox.addItemListener(e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            final boolean isCustom = modelComboBox.getSelectedIndex() == 0;
                            enableSettings(isCustom);
                            if (!isCustom) {
                                final PvModuleSpecs specs = modules.get(modelComboBox.getSelectedItem());
                                cellTypeComboBox.setSelectedItem(specs.getCellType());
                                shadeToleranceComboBox.setSelectedItem(specs.getShadeTolerance());
                                cellEfficiencyField.setText(threeDecimalsFormat.format(specs.getCelLEfficiency() * 100));
                                noctField.setText(threeDecimalsFormat.format(specs.getNoct()));
                                pmaxTcField.setText(sixDecimalsFormat.format(specs.getPmaxTc()));
                                final String s = threeDecimalsFormat.format(specs.getNominalWidth()) + "m \u00D7 " + threeDecimalsFormat.format(specs.getNominalLength()) + "m (" + specs.getLayout().width + " \u00D7 " + specs.getLayout().height + " cells)";
                                sizeComboBox.setSelectedItem(s);
                                colorOptionComboBox.setSelectedItem(specs.getColor());
                            }
                        }
                    });
                    panel.add(modelComboBox);
                    rowCount++;

                    panel.add(new JLabel(I18n.get("label.solar_panel_cell_type")));
                    panel.add(cellTypeComboBox);
                    rowCount++;

                    panel.add(new JLabel(I18n.get("label.solar_panel_color")));
                    panel.add(colorOptionComboBox);
                    rowCount++;

                    panel.add(new JLabel(I18n.get("label.solar_panel_size")));
                    panel.add(sizeComboBox);
                    rowCount++;

                    panel.add(new JLabel(I18n.get("label.solar_cell_efficiency")));
                    panel.add(cellEfficiencyField);
                    rowCount++;

                    panel.add(new JLabel("<html>" + I18n.get("label.noct")));
                    panel.add(noctField);
                    rowCount++;

                    panel.add(new JLabel("<html>" + I18n.get("label.temp_coeff_pmax")));
                    panel.add(pmaxTcField);
                    rowCount++;

                    panel.add(new JLabel(I18n.get("label.shade_tolerance")));
                    panel.add(shadeToleranceComboBox);
                    rowCount++;

                    panel.add(new JLabel(I18n.get("label.inverter_efficiency")));
                    panel.add(inverterEfficiencyField);
                    rowCount++;

                }

                panel.add(new JLabel(I18n.get("label.inter_row_spacing")));
                final JTextField interrowSpacingField = new JTextField(threeDecimalsFormat.format(solarPanelRackArrayInterRowSpacing));
                panel.add(interrowSpacingField);
                rowCount++;

                panel.add(new JLabel(I18n.get("label.tilt_angle")));
                final JTextField tiltAngleField = new JTextField(threeDecimalsFormat.format(solarPanelTiltAngle), 10);
                panel.add(tiltAngleField);
                rowCount++;

                panel.add(new JLabel(I18n.get("label.solar_panel_rows_per_rack")));
                final JTextField rowsPerRackField = new JTextField(threeDecimalsFormat.format(solarPanelRowsPerRack));
                panel.add(rowsPerRackField);
                rowCount++;

                orientationComboBox = new JComboBox<>(new String[]{I18n.get("orientation.landscape"), I18n.get("orientation.portrait")});
                rowAxisComboBox = new JComboBox<>(new String[]{I18n.get("row_axis.east_west"), I18n.get("row_axis.north_south")});
                final JTextField poleSpacingXField = new JTextField(threeDecimalsFormat.format(solarPanelRackPoleSpacingX));
                final JTextField poleSpacingYField = new JTextField(threeDecimalsFormat.format(solarPanelRackPoleSpacingY));
                final JTextField poleHeightField = new JTextField(threeDecimalsFormat.format(solarPanelRackPoleHeight));
                if (operationType != 1) {
                    panel.add(new JLabel(I18n.get("label.solar_panel_orientation")));
                    orientationComboBox.setSelectedIndex(solarPanelOrientation);
                    panel.add(orientationComboBox);
                    rowCount++;
                    panel.add(new JLabel(I18n.get("label.row_axis")));
                    rowAxisComboBox.setSelectedIndex(solarPanelArrayRowAxis);
                    panel.add(rowAxisComboBox);
                    rowCount++;
                    panel.add(new JLabel(I18n.get("label.pole_spacing_x")));
                    panel.add(poleSpacingXField);
                    rowCount++;
                    panel.add(new JLabel(I18n.get("label.pole_spacing_y")));
                    panel.add(poleSpacingYField);
                    rowCount++;
                    panel.add(new JLabel(I18n.get("label.pole_height_m")));
                    panel.add(poleHeightField);
                    rowCount++;
                }

                SpringUtilities.makeCompactGrid(panel, rowCount, 2, 6, 6, 6, 6);

                enableSettings(modelComboBox.getSelectedIndex() == 0);

                final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_rack_options"));

                while (true) {
                    dialog.setVisible(true);
                    final Object choice = optionPane.getValue();
                    if (choice == options[1] || choice == null) {
                        break;
                    } else {
                        boolean ok = true;
                        try {
                            solarPanelRackArrayInterRowSpacing = Double.parseDouble(interrowSpacingField.getText());
                            solarPanelTiltAngle = Double.parseDouble(tiltAngleField.getText());
                            solarPanelRowsPerRack = Integer.parseInt(rowsPerRackField.getText());
                            solarCellEfficiencyPercentage = Double.parseDouble(cellEfficiencyField.getText());
                            inverterEfficiencyPercentage = Double.parseDouble(inverterEfficiencyField.getText());
                            solarPanelTemperatureCoefficientPmaxPercentage = Double.parseDouble(pmaxTcField.getText());
                            solarPanelNominalOperatingCellTemperature = Double.parseDouble(noctField.getText());
                            solarPanelRackPoleSpacingX = Double.parseDouble(poleSpacingXField.getText());
                            solarPanelRackPoleSpacingY = Double.parseDouble(poleSpacingYField.getText());
                            solarPanelRackPoleHeight = Double.parseDouble(poleHeightField.getText());
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            ok = false;
                        }
                        if (ok) {
                            final int i = sizeComboBox.getSelectedIndex();
                            solarPanelWidth = solarPanelNominalSize.getNominalWidths()[i];
                            solarPanelHeight = solarPanelNominalSize.getNominalHeights()[i];
                            numberOfCellsInX = solarPanelNominalSize.getCellNx()[i];
                            numberOfCellsInY = solarPanelNominalSize.getCellNy()[i];
                            solarPanelOrientation = orientationComboBox.getSelectedIndex();
                            final int minimumSolarPanelRowsPerRack = 1;
                            final int maximumSolarPanelRowsPerRack = Rack.MAXIMUM_SOLAR_PANEL_ROWS;
                            final double minimumInterRowSpacing = minimumSolarPanelRowsPerRack * (solarPanelOrientation == 1 ? solarPanelHeight : solarPanelWidth);
                            final double rackHeight = (solarPanelOrientation == 1 ? solarPanelHeight : solarPanelWidth) * solarPanelRowsPerRack;
                            if (solarPanelTiltAngle < -90 || solarPanelTiltAngle > 90) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.rack_tilt_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelRackPoleSpacingX < 1 || solarPanelRackPoleSpacingX > 50) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_spacing_x_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelRackPoleHeight < 0) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (Math.abs(0.5 * rackHeight * Math.sin(Math.toRadians(solarPanelTiltAngle))) > solarPanelRackPoleHeight) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_panels_intersect_ground"), I18n.get("msg.geometry_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelRackPoleSpacingY < 1 || solarPanelRackPoleSpacingY > 50) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_spacing_y_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelRowsPerRack < minimumSolarPanelRowsPerRack || solarPanelRowsPerRack > maximumSolarPanelRowsPerRack) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_panel_rows_per_rack_range", minimumSolarPanelRowsPerRack, maximumSolarPanelRowsPerRack), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarCellEfficiencyPercentage < SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE || solarCellEfficiencyPercentage > SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_cell_efficiency_range", SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (inverterEfficiencyPercentage < SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE || inverterEfficiencyPercentage >= SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inverter_efficiency_range", SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelTemperatureCoefficientPmaxPercentage < -1 || solarPanelTemperatureCoefficientPmaxPercentage > 0) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.temp_coeff_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelNominalOperatingCellTemperature < 33 || solarPanelNominalOperatingCellTemperature > 58) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.noct_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (solarPanelRackArrayInterRowSpacing < minimumInterRowSpacing) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inter_row_spacing_range", EnergyPanel.TWO_DECIMALS.format(minimumInterRowSpacing), EnergyPanel.ONE_DECIMAL.format(0.5 * (maximumSolarPanelRowsPerRack + minimumSolarPanelRowsPerRack)), EnergyPanel.TWO_DECIMALS.format((solarPanelOrientation == 1 ? solarPanelHeight : solarPanelWidth))), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                addSolarRackArrays();
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }

    }

    static JPopupMenu getPopupMenu(final MouseEvent mouseEvent) {

        if (mouseEvent.isShiftDown()) {
            SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().pasteToPickedLocationOnFoundation();
                return null;
            });
            Scene.getInstance().setEdited(true);
            return null;
        }

        if (popupMenuForFoundation == null) {

            final JMenuItem miImportCollada = new JMenuItem(I18n.get("menu.import_collada"));
            miImportCollada.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final File file = FileChooser.getInstance().showDialog(".dae", FileChooser.daeFilter, false);
                    if (file != null) {
                        EnergyPanel.getInstance().updateRadiationHeatMap();
                        SceneManager.getTaskManager().update(() -> {
                            boolean success = true;
                            final Vector3 position = SceneManager.getInstance().getPickedLocationOnFoundation();
                            try {
                                ((Foundation) selectedPart).importCollada(file.toURI().toURL(), position);
                            } catch (final Throwable t) {
                                BugReporter.report(t);
                                success = false;
                            }
                            if (success) {
                                SceneManager.getInstance().getUndoManager().addEdit(new AddNodeCommand((Foundation) selectedPart));
                            }
                            return null;
                        });
                    }
                }
            });

            final JMenuItem miPaste = new JMenuItem(I18n.get("menu.paste"));
            miPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            miPaste.addActionListener(e -> {
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().pasteToPickedLocationOnFoundation();
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });

            final JMenuItem miCopy = new JMenuItem(I18n.get("menu.copy"));
            miCopy.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    Scene.getInstance().setCopyBuffer(selectedPart);
                }
            });

            final JMenuItem miRescale = new JMenuItem(I18n.get("menu.rescale"));
            miRescale.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Foundation)) {
                    return;
                }
                new RescaleBuildingDialog((Foundation) selectedPart).setVisible(true);
                if (SceneManager.getInstance().getSolarHeatMap()) {
                    EnergyPanel.getInstance().updateRadiationHeatMap();
                }
                Scene.getInstance().setEdited(true);
            });

            final JMenu rotateMenu = new JMenu(I18n.get("menu.rotate"));

            final JMenuItem mi180 = new JMenuItem(I18n.get("menu.rotate_180"));
            mi180.addActionListener(e -> {
                SceneManager.getInstance().rotate(Math.PI); // already run in the Task Manager thread
                Scene.getInstance().setEdited(true);
            });
            rotateMenu.add(mi180);

            final JMenuItem mi90CW = new JMenuItem(I18n.get("menu.rotate_90_cw"));
            mi90CW.addActionListener(e -> {
                SceneManager.getInstance().rotate(-Math.PI / 2); // already run in the Task Manager thread
                Scene.getInstance().setEdited(true);
            });
            rotateMenu.add(mi90CW);

            final JMenuItem mi90CCW = new JMenuItem(I18n.get("menu.rotate_90_ccw"));
            mi90CCW.addActionListener(e -> {
                SceneManager.getInstance().rotate(Math.PI / 2); // already run in the Task Manager thread
                Scene.getInstance().setEdited(true);
            });
            rotateMenu.add(mi90CCW);
            rotateMenu.addSeparator();

            final JMenuItem miArbitraryRotation = new JMenuItem(I18n.get("menu.arbitrary"));
            rotateMenu.add(miArbitraryRotation);
            miArbitraryRotation.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Foundation)) {
                    return;
                }
                final Foundation foundation = (Foundation) selectedPart;
                final String partInfo = foundation.toString().substring(0, foundation.toString().indexOf(')') + 1);
                final String title = "<html>" + I18n.get("title.rotate_foundation", partInfo) + "</html>";
                final String footnote = "<html><hr><font size=2>" + I18n.get("msg.rotate_foundation_footnote") + "<hr></html>";
                final JPanel gui = new JPanel(new BorderLayout());
                final JTextField inputField = new JTextField("0");
                gui.add(inputField, BorderLayout.SOUTH);
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.rotation_angle"));
                while (true) {
                    inputField.selectAll();
                    inputField.requestFocusInWindow();
                    dialog.setVisible(true);
                    final Object choice = optionPane.getValue();
                    if (choice == options[1] || choice == null) {
                        break;
                    } else {
                        boolean ok = true;
                        double a = 0;
                        try {
                            a = Double.parseDouble(inputField.getText());
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            ok = false;
                        }
                        if (ok) {
                            if (!Util.isZero(a)) {
                                SceneManager.getInstance().rotate(-Math.toRadians(a)); // already run in the Task Manager thread
                                updateAfterEdit();
                            }
                            if (choice == options[0]) {
                                break;
                            }
                        }
                    }
                }
            });

            final JMenuItem miAzimuth = new JMenuItem(I18n.get("menu.azimuth"));
            rotateMenu.add(miAzimuth);
            miAzimuth.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Foundation)) {
                    return;
                }
                final Foundation foundation = (Foundation) selectedPart;
                final String partInfo = foundation.toString().substring(0, foundation.toString().indexOf(')') + 1);
                final String title = "<html>" + I18n.get("title.set_azimuth_for", partInfo) + "</html>";
                final String footnote = "<html><hr><font size=2>" + I18n.get("msg.set_azimuth_footnote") + "<hr></html>";
                final JPanel gui = new JPanel(new BorderLayout());
                final JTextField inputField = new JTextField(EnergyPanel.TWO_DECIMALS.format(foundation.getAzimuth()));
                gui.add(inputField, BorderLayout.SOUTH);
                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{title, footnote, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.azimuth"));
                while (true) {
                    inputField.selectAll();
                    inputField.requestFocusInWindow();
                    dialog.setVisible(true);
                    final Object choice = optionPane.getValue();
                    if (choice == options[1] || choice == null) {
                        break;
                    } else {
                        boolean ok = true;
                        double a = 0;
                        try {
                            a = Double.parseDouble(inputField.getText());
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", inputField.getText()), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            ok = false;
                        }
                        if (ok) {
                            final double a2 = a;
                            SceneManager.getTaskManager().update(() -> {
                                final ChangeFoundationAzimuthCommand c = new ChangeFoundationAzimuthCommand(foundation);
                                foundation.setAzimuth(a2);
                                EventQueue.invokeLater(() -> {
                                    updateAfterEdit();
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                });
                                return null;
                            });
                            if (choice == options[0]) {
                                break;
                            }
                        }
                    }
                }
            });

            final JMenu clearMenu = new JMenu(I18n.get("menu.clear"));

            final JMenuItem miRemoveAllWalls = new JMenuItem(I18n.get("menu.remove_all_walls"));
            miRemoveAllWalls.addActionListener(e -> Scene.getInstance().removeAllWalls()); // actual scene removal already runs in the Task Manager thread
            clearMenu.add(miRemoveAllWalls);

            final JMenuItem miRemoveAllWindows = new JMenuItem(I18n.get("menu.remove_all_windows"));
            miRemoveAllWindows.addActionListener(e -> Scene.getInstance().removeAllWindows());
            clearMenu.add(miRemoveAllWindows);

            final JMenuItem miRemoveAllWindowShutters = new JMenuItem(I18n.get("menu.remove_all_window_shutters"));
            miRemoveAllWindowShutters.addActionListener(e -> Scene.getInstance().removeAllWindowShutters());
            clearMenu.add(miRemoveAllWindowShutters);

            final JMenuItem miRemoveAllSolarPanels = new JMenuItem(I18n.get("menu.remove_all_solar_panels"));
            miRemoveAllSolarPanels.addActionListener(e -> Scene.getInstance().removeAllSolarPanels(null));
            clearMenu.add(miRemoveAllSolarPanels);

            final JMenuItem miRemoveAllRacks = new JMenuItem(I18n.get("menu.remove_all_solar_panel_racks"));
            miRemoveAllRacks.addActionListener(e -> Scene.getInstance().removeAllRacks());
            clearMenu.add(miRemoveAllRacks);

            final JMenuItem miRemoveAllHeliostats = new JMenuItem(I18n.get("menu.remove_all_heliostats"));
            miRemoveAllHeliostats.addActionListener(e -> Scene.getInstance().removeAllHeliostats());
            clearMenu.add(miRemoveAllHeliostats);

            final JMenuItem miRemoveAllParabolicTroughs = new JMenuItem(I18n.get("menu.remove_all_parabolic_troughs"));
            miRemoveAllParabolicTroughs.addActionListener(e -> Scene.getInstance().removeAllParabolicTroughs());
            clearMenu.add(miRemoveAllParabolicTroughs);

            final JMenuItem miRemoveAllParabolicDishes = new JMenuItem(I18n.get("menu.remove_all_parabolic_dishes"));
            miRemoveAllParabolicDishes.addActionListener(e -> Scene.getInstance().removeAllParabolicDishes());
            clearMenu.add(miRemoveAllParabolicDishes);

            final JMenuItem miRemoveAllFresnelReflectors = new JMenuItem(I18n.get("menu.remove_all_fresnel_reflectors"));
            miRemoveAllFresnelReflectors.addActionListener(e -> Scene.getInstance().removeAllFresnelReflectors());
            clearMenu.add(miRemoveAllFresnelReflectors);

            final JMenuItem miRemoveAllSensors = new JMenuItem(I18n.get("menu.remove_all_sensors"));
            miRemoveAllSensors.addActionListener(e -> Scene.getInstance().removeAllSensors());
            clearMenu.add(miRemoveAllSensors);

            final JMenuItem removeAllFloorsMenuItem = new JMenuItem(I18n.get("menu.remove_all_floors"));
            removeAllFloorsMenuItem.addActionListener(e -> Scene.getInstance().removeAllFloors());
            clearMenu.add(removeAllFloorsMenuItem);

            final JMenuItem miRemoveAllImportedNodes = new JMenuItem(I18n.get("menu.remove_all_nodes"));
            miRemoveAllImportedNodes.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    f.removeAllImports(); // already run in Task Manager thread
                    f.setMeshSelectionVisible(false);
                    MainPanel.getInstance().getEnergyButton().setSelected(false);
                    Scene.getInstance().setEdited(true);
                }
            });
            clearMenu.add(miRemoveAllImportedNodes);

            final JMenuItem miRemoveAllWithinInset = new JMenuItem(I18n.get("menu.remove_all_objects_within_inset"));
            miRemoveAllWithinInset.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    ((Foundation) selectedPart).removeAllWithinPolygon(); // already run in Task Manager thread
                    MainPanel.getInstance().getEnergyButton().setSelected(false);
                    Scene.getInstance().setEdited(true);
                }
            });
            clearMenu.add(miRemoveAllWithinInset);

            final JMenuItem miResetPolygonInset = new JMenuItem(I18n.get("menu.reset_inset"));
            miResetPolygonInset.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    ((Foundation) selectedPart).resetPolygon(); // already run in Task Manager thread
                    Scene.getInstance().setEdited(true);
                }
            });
            clearMenu.add(miResetPolygonInset);

            final JMenu layoutMenu = new JMenu(I18n.get("menu.layout"));

            final JMenuItem miSolarPanelArrays = new JMenuItem(I18n.get("menu.solar_panel_arrays"));
            layoutMenu.add(miSolarPanelArrays);
            miSolarPanelArrays.addActionListener(new ActionListener() {

                private Foundation f;
                private JComboBox<String> modelComboBox;
                private JComboBox<String> cellTypeComboBox;
                private JComboBox<String> colorOptionComboBox;
                private JComboBox<String> sizeComboBox;
                private JComboBox<String> shadeToleranceComboBox;
                private JComboBox<String> orientationComboBox;
                private JComboBox<String> rowAxisComboBox;
                private JTextField cellEfficiencyField;
                private JTextField noctField;
                private JTextField pmaxTcField;
                private int numberOfCellsInX = 6;
                private int numberOfCellsInY = 10;

                private void enableSettings(final boolean b) {
                    sizeComboBox.setEnabled(b);
                    cellTypeComboBox.setEnabled(b);
                    colorOptionComboBox.setEnabled(b);
                    shadeToleranceComboBox.setEnabled(b);
                    cellEfficiencyField.setEnabled(b);
                    noctField.setEnabled(b);
                    pmaxTcField.setEnabled(b);
                }

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Foundation) {
                        f = (Foundation) selectedPart;
                        int n = f.countParts(SolarPanel.class);
                        if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_panels", n), I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                        n = f.countParts(Rack.class);
                        if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_racks", n), I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                            return;
                        }

                        final JPanel panel = new JPanel(new SpringLayout());

                        final Map<String, PvModuleSpecs> modules = PvModulesData.getInstance().getModules();
                        final String[] models = new String[modules.size() + 1];
                        int j = 0;
                        models[j] = I18n.get("model.custom");
                        for (final String key : modules.keySet()) {
                            models[++j] = key;
                        }
                        panel.add(new JLabel(I18n.get("label.model")));
                        modelComboBox = new JComboBox<>(models);
                        modelComboBox.setSelectedItem(solarPanelModel);
                        modelComboBox.addItemListener(e1 -> {
                            if (e1.getStateChange() == ItemEvent.SELECTED) {
                                final boolean isCustom = modelComboBox.getSelectedIndex() == 0;
                                enableSettings(isCustom);
                                if (!isCustom) {
                                    final PvModuleSpecs specs = modules.get(modelComboBox.getSelectedItem());
                                    cellTypeComboBox.setSelectedItem(specs.getCellType());
                                    shadeToleranceComboBox.setSelectedItem(specs.getShadeTolerance());
                                    cellEfficiencyField.setText(threeDecimalsFormat.format(specs.getCelLEfficiency() * 100));
                                    noctField.setText(threeDecimalsFormat.format(specs.getNoct()));
                                    pmaxTcField.setText(sixDecimalsFormat.format(specs.getPmaxTc()));
                                    final String s = threeDecimalsFormat.format(specs.getNominalWidth()) + "m \u00D7 " + threeDecimalsFormat.format(specs.getNominalLength()) + "m (" + specs.getLayout().width + " \u00D7 " + specs.getLayout().height + " cells)";
                                    sizeComboBox.setSelectedItem(s);
                                    colorOptionComboBox.setSelectedItem(specs.getColor());
                                }
                            }
                        });
                        panel.add(modelComboBox);

                        panel.add(new JLabel(I18n.get("label.cell_type")));
                        cellTypeComboBox = new JComboBox<>(new String[]{I18n.get("cell_type.polycrystalline"), I18n.get("cell_type.monocrystalline"), I18n.get("cell_type.thin_film")});
                        cellTypeComboBox.setSelectedIndex(solarPanelCellType);
                        panel.add(cellTypeComboBox);

                        panel.add(new JLabel(I18n.get("label.color")));
                        colorOptionComboBox = new JComboBox<>(new String[]{I18n.get("color.blue"), I18n.get("color.black"), I18n.get("color.gray")});
                        colorOptionComboBox.setSelectedIndex(solarPanelColorOption);
                        panel.add(colorOptionComboBox);

                        panel.add(new JLabel(I18n.get("label.size")));
                        sizeComboBox = new JComboBox<>(solarPanelNominalSize.getStrings());
                        final int nItems = sizeComboBox.getItemCount();
                        for (int i = 0; i < nItems; i++) {
                            if (Util.isZero(solarPanelHeight - solarPanelNominalSize.getNominalHeights()[i]) && Util.isZero(solarPanelWidth - solarPanelNominalSize.getNominalWidths()[i])) {
                                sizeComboBox.setSelectedIndex(i);
                            }
                        }
                        panel.add(sizeComboBox);

                        panel.add(new JLabel(I18n.get("label.solar_cell_efficiency")));
                        cellEfficiencyField = new JTextField(threeDecimalsFormat.format(solarCellEfficiencyPercentage));
                        panel.add(cellEfficiencyField);

                        panel.add(new JLabel("<html>" + I18n.get("label.noct")));
                        noctField = new JTextField(threeDecimalsFormat.format(solarPanelNominalOperatingCellTemperature));
                        panel.add(noctField);

                        panel.add(new JLabel("<html>" + I18n.get("label.temp_coeff_pmax")));
                        pmaxTcField = new JTextField(sixDecimalsFormat.format(solarPanelTemperatureCoefficientPmaxPercentage));
                        panel.add(pmaxTcField);

                        panel.add(new JLabel(I18n.get("label.shade_tolerance")));
                        shadeToleranceComboBox = new JComboBox<>(new String[]{I18n.get("shade_tolerance.partial"), I18n.get("shade_tolerance.high"), I18n.get("shade_tolerance.none")});
                        shadeToleranceComboBox.setSelectedIndex(solarPanelShadeTolerance);
                        panel.add(shadeToleranceComboBox);

                        panel.add(new JLabel(I18n.get("label.inverter_efficiency")));
                        final JTextField inverterEfficiencyField = new JTextField(threeDecimalsFormat.format(inverterEfficiencyPercentage));
                        panel.add(inverterEfficiencyField);

                        panel.add(new JLabel(I18n.get("label.tilt_angle")));
                        final JTextField tiltAngleField = new JTextField(threeDecimalsFormat.format(solarPanelTiltAngle));
                        panel.add(tiltAngleField);

                        panel.add(new JLabel(I18n.get("label.orientation")));
                        orientationComboBox = new JComboBox<>(new String[]{I18n.get("orientation.portrait"), I18n.get("orientation.landscape")});
                        orientationComboBox.setSelectedIndex(solarPanelOrientation);
                        panel.add(orientationComboBox);

                        panel.add(new JLabel(I18n.get("label.row_axis")));
                        rowAxisComboBox = new JComboBox<>(new String[]{I18n.get("row_axis.east_west"), I18n.get("row_axis.north_south")});
                        rowAxisComboBox.setSelectedIndex(solarPanelArrayRowAxis);
                        panel.add(rowAxisComboBox);

                        panel.add(new JLabel(I18n.get("label.row_spacing_m")));
                        final JTextField rowSpacingField = new JTextField(threeDecimalsFormat.format(solarPanelArrayRowSpacing));
                        panel.add(rowSpacingField);

                        panel.add(new JLabel(I18n.get("label.column_spacing_m")));
                        final JTextField colSpacingField = new JTextField(threeDecimalsFormat.format(solarPanelArrayColSpacing));
                        panel.add(colSpacingField);

                        panel.add(new JLabel(I18n.get("label.pole_height_m")));
                        final JTextField poleHeightField = new JTextField(threeDecimalsFormat.format(solarPanelArrayPoleHeight));
                        panel.add(poleHeightField);

                        SpringUtilities.makeCompactGrid(panel, 15, 2, 6, 6, 6, 6);

                        enableSettings(modelComboBox.getSelectedIndex() == 0);

                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.solar_panel_array_options"));

                        while (true) {
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1] || choice == null) {
                                break;
                            } else {
                                boolean ok = true;
                                try {
                                    solarPanelArrayRowSpacing = Double.parseDouble(rowSpacingField.getText());
                                    solarPanelArrayColSpacing = Double.parseDouble(colSpacingField.getText());
                                    solarPanelArrayPoleHeight = Double.parseDouble(poleHeightField.getText());
                                    solarPanelTiltAngle = Double.parseDouble(tiltAngleField.getText());
                                    solarCellEfficiencyPercentage = Double.parseDouble(cellEfficiencyField.getText());
                                    inverterEfficiencyPercentage = Double.parseDouble(inverterEfficiencyField.getText());
                                    solarPanelTemperatureCoefficientPmaxPercentage = Double.parseDouble(pmaxTcField.getText());
                                    solarPanelNominalOperatingCellTemperature = Double.parseDouble(noctField.getText());
                                } catch (final NumberFormatException exception) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    final int i = sizeComboBox.getSelectedIndex();
                                    solarPanelWidth = solarPanelNominalSize.getNominalWidths()[i];
                                    solarPanelHeight = solarPanelNominalSize.getNominalHeights()[i];
                                    numberOfCellsInX = solarPanelNominalSize.getCellNx()[i];
                                    numberOfCellsInY = solarPanelNominalSize.getCellNy()[i];
                                    solarPanelOrientation = orientationComboBox.getSelectedIndex();
                                    if (solarPanelArrayRowSpacing < (solarPanelOrientation == 0 ? solarPanelHeight : solarPanelWidth) || solarPanelArrayColSpacing < (solarPanelOrientation == 0 ? solarPanelWidth : solarPanelHeight)) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.spacing_too_small"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (solarPanelArrayPoleHeight < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (solarPanelTiltAngle < -90 || solarPanelTiltAngle > 90) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_panel_tilt_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (Math.abs(0.5 * (solarPanelOrientation == 0 ? solarPanelHeight : solarPanelWidth) * Math.sin(Math.toRadians(solarPanelTiltAngle))) > solarPanelArrayPoleHeight) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_panels_intersect_ground"), I18n.get("msg.geometry_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (solarCellEfficiencyPercentage < SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE || solarCellEfficiencyPercentage > SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.solar_cell_efficiency_range", SolarPanel.MIN_SOLAR_CELL_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_SOLAR_CELL_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (inverterEfficiencyPercentage < SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE || inverterEfficiencyPercentage >= SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.inverter_efficiency_range", SolarPanel.MIN_INVERTER_EFFICIENCY_PERCENTAGE, SolarPanel.MAX_INVERTER_EFFICIENCY_PERCENTAGE), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (solarPanelTemperatureCoefficientPmaxPercentage < -1 || solarPanelTemperatureCoefficientPmaxPercentage > 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.temp_coeff_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (solarPanelNominalOperatingCellTemperature < 33 || solarPanelNominalOperatingCellTemperature > 58) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.noct_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        addSolarPanelArrays();
                                        if (choice == options[0]) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                private void addSolarPanelArrays() {
                    solarPanelArrayRowAxis = rowAxisComboBox.getSelectedIndex();
                    solarPanelShadeTolerance = shadeToleranceComboBox.getSelectedIndex();
                    solarPanelColorOption = colorOptionComboBox.getSelectedIndex();
                    solarPanelCellType = cellTypeComboBox.getSelectedIndex();
                    solarPanelModel = (String) modelComboBox.getSelectedItem();
                    final SolarPanel sp = new SolarPanel();
                    sp.setModelName((String) modelComboBox.getSelectedItem());
                    sp.setRotated(solarPanelOrientation == 1);
                    sp.setCellType(solarPanelCellType);
                    sp.setColorOption(solarPanelColorOption);
                    sp.setTiltAngle(solarPanelTiltAngle);
                    sp.setPanelWidth(solarPanelWidth);
                    sp.setPanelHeight(solarPanelHeight);
                    sp.setNumberOfCellsInX(numberOfCellsInX);
                    sp.setNumberOfCellsInY(numberOfCellsInY);
                    sp.setPoleHeight(solarPanelArrayPoleHeight / Scene.getInstance().getScale());
                    sp.setShadeTolerance(solarPanelShadeTolerance);
                    sp.setCellEfficiency(solarCellEfficiencyPercentage * 0.01);
                    sp.setInverterEfficiency(inverterEfficiencyPercentage * 0.01);
                    sp.setTemperatureCoefficientPmax(solarPanelTemperatureCoefficientPmaxPercentage * 0.01);
                    sp.setNominalOperatingCellTemperature(solarPanelNominalOperatingCellTemperature);
                    SceneManager.getTaskManager().update(() -> {
                        f.addSolarPanelArrays(sp, solarPanelArrayRowSpacing, solarPanelArrayColSpacing, solarPanelArrayRowAxis);
                        return null;
                    });
                    updateAfterEdit();
                }

            });

            final JMenuItem miSolarRackArrays = new JMenuItem(I18n.get("menu.solar_panel_rack_arrays"));
            layoutMenu.add(miSolarRackArrays);
            miSolarRackArrays.addActionListener(e -> new SolarPanelArrayLayoutManager().open(0));

            layoutMenu.addSeparator();

            final JMenuItem miHeliostatConcentricArrays = new JMenuItem(I18n.get("menu.heliostat_concentric_layout"));
            layoutMenu.add(miHeliostatConcentricArrays);
            miHeliostatConcentricArrays.addActionListener(new ActionListener() {

                private Foundation f;
                private JComboBox<String> typeComboBox;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Foundation) {
                        f = (Foundation) selectedPart;
                        final int n = f.countParts(Mirror.class);
                        if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                                I18n.get("msg.confirm_remove_heliostats", n),
                                I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                            return;
                        }

                        final JPanel panel = new JPanel(new SpringLayout());
                        panel.add(new JLabel(I18n.get("label.type")));
                        typeComboBox = new JComboBox<>(new String[]{I18n.get("heliostat_layout.equal_azimuthal"), I18n.get("heliostat_layout.radial_stagger")});
                        typeComboBox.setSelectedIndex(heliostatConcentricFieldLayout.getType());
                        panel.add(typeComboBox);
                        panel.add(new JLabel());

                        panel.add(new JLabel(I18n.get("label.aperture_width")));
                        final JTextField widthField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getApertureWidth()));
                        panel.add(widthField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        panel.add(new JLabel(I18n.get("label.aperture_height")));
                        final JTextField heightField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getApertureHeight()));
                        panel.add(heightField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        panel.add(new JLabel(I18n.get("label.azimuthal_spacing")));
                        final JTextField azimuthalSpacingField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getAzimuthalSpacing()));
                        panel.add(azimuthalSpacingField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        panel.add(new JLabel(I18n.get("label.radial_spacing")));
                        final JTextField rowSpacingField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getRadialSpacing()));
                        panel.add(rowSpacingField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        panel.add(new JLabel(I18n.get("label.radial_expansion_ratio")));
                        final JTextField radialSpacingIncrementField = new JTextField(sixDecimalsFormat.format(heliostatConcentricFieldLayout.getRadialExpansionRatio()));
                        panel.add(radialSpacingIncrementField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.dimensionless")));

                        panel.add(new JLabel(I18n.get("label.starting_angle")));
                        final JTextField startAngleField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getStartAngle()));
                        panel.add(startAngleField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.counter_clockwise_from_east")));

                        panel.add(new JLabel(I18n.get("label.ending_angle")));
                        final JTextField endAngleField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getEndAngle()));
                        panel.add(endAngleField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.counter_clockwise_from_east")));

                        panel.add(new JLabel(I18n.get("label.pole_height")));
                        final JTextField poleHeightField = new JTextField(threeDecimalsFormat.format(heliostatConcentricFieldLayout.getPoleHeight()));
                        panel.add(poleHeightField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        SpringUtilities.makeCompactGrid(panel, 9, 3, 6, 6, 6, 6);

                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.concentric_heliostat_array_options"));

                        while (true) {
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1] || choice == null) {
                                break;
                            } else {
                                boolean ok = true;
                                try {
                                    heliostatConcentricFieldLayout.setRadialSpacing(Double.parseDouble(rowSpacingField.getText()));
                                    heliostatConcentricFieldLayout.setRadialExpansionRatio(Double.parseDouble(radialSpacingIncrementField.getText()));
                                    heliostatConcentricFieldLayout.setAzimuthalSpacing(Double.parseDouble(azimuthalSpacingField.getText()));
                                    heliostatConcentricFieldLayout.setApertureWidth(Double.parseDouble(widthField.getText()));
                                    heliostatConcentricFieldLayout.setApertureHeight(Double.parseDouble(heightField.getText()));
                                    heliostatConcentricFieldLayout.setStartAngle(Double.parseDouble(startAngleField.getText()));
                                    heliostatConcentricFieldLayout.setEndAngle(Double.parseDouble(endAngleField.getText()));
                                    heliostatConcentricFieldLayout.setPoleHeight(Double.parseDouble(poleHeightField.getText()));
                                } catch (final NumberFormatException exception) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    if (heliostatConcentricFieldLayout.getRadialSpacing() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_radial_spacing_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getAzimuthalSpacing() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_azimuthal_spacing_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getRadialExpansionRatio() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_expansion_ratio_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getStartAngle() < -360 || heliostatConcentricFieldLayout.getStartAngle() > 360) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.starting_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getEndAngle() < -360 || heliostatConcentricFieldLayout.getEndAngle() > 360) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.ending_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getEndAngle() <= heliostatConcentricFieldLayout.getStartAngle()) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.ending_angle_greater"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getApertureWidth() < 1 || heliostatConcentricFieldLayout.getApertureWidth() > 50) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_aperture_width_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getApertureHeight() < 1 || heliostatConcentricFieldLayout.getApertureHeight() > 50) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_aperture_height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatConcentricFieldLayout.getPoleHeight() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        addCircularHeliostatArrays();
                                        if (choice == options[0]) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                private void addCircularHeliostatArrays() {
                    heliostatConcentricFieldLayout.setType(typeComboBox.getSelectedIndex());
                    SceneManager.getTaskManager().update(() -> {
                        final int count = f.addHeliostats(heliostatConcentricFieldLayout);
                        if (count == 0) {
                            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                    I18n.get("msg.heliostat_array_cant_be_created"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE));
                        }
                        return null;
                    });
                    updateAfterEdit();
                }

            });

            final JMenuItem miHeliostatFermatSpiralArrays = new JMenuItem(I18n.get("menu.heliostat_spiral_layout"));
            layoutMenu.add(miHeliostatFermatSpiralArrays);
            miHeliostatFermatSpiralArrays.addActionListener(new ActionListener() {

                private Foundation f;
                private JComboBox<String> typeComboBox;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Foundation) {
                        f = (Foundation) selectedPart;
                        final int n = f.countParts(Mirror.class);
                        if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_heliostats", n), I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                            return;
                        }

                        final JPanel panel = new JPanel(new SpringLayout());
                        panel.add(new JLabel(I18n.get("label.pattern")));
                        typeComboBox = new JComboBox<>(new String[]{I18n.get("pattern.fermat_spiral")});
                        typeComboBox.setSelectedIndex(heliostatSpiralFieldLayout.getType());
                        panel.add(typeComboBox);
                        panel.add(new JLabel());

                        panel.add(new JLabel(I18n.get("label.heliostat_aperture_width")));
                        final JTextField widthField = new JTextField(threeDecimalsFormat.format(heliostatSpiralFieldLayout.getApertureWidth()));
                        panel.add(widthField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        panel.add(new JLabel(I18n.get("label.heliostat_aperture_height")));
                        final JTextField heightField = new JTextField(threeDecimalsFormat.format(heliostatSpiralFieldLayout.getApertureHeight()));
                        panel.add(heightField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        panel.add(new JLabel(I18n.get("label.divergence_angle")));
                        final JTextField divergenceField = new JTextField(threeDecimalsFormat.format(Math.toDegrees(heliostatSpiralFieldLayout.getDivergence())));
                        panel.add(divergenceField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.degrees")));

                        panel.add(new JLabel(I18n.get("label.scaling_factor")));
                        final JTextField scalingFactorField = new JTextField(threeDecimalsFormat.format(heliostatSpiralFieldLayout.getScalingFactor()));
                        panel.add(scalingFactorField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.relative_to_heliostat_size")));

                        panel.add(new JLabel(I18n.get("label.radial_expansion_ratio")));
                        final JTextField radialExpansionRatioField = new JTextField(sixDecimalsFormat.format(heliostatSpiralFieldLayout.getRadialExpansionRatio()));
                        panel.add(radialExpansionRatioField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.relative_to_distance_to_tower")));

                        panel.add(new JLabel(I18n.get("label.starting_turn")));
                        final JTextField startTurnField = new JTextField(heliostatSpiralFieldLayout.getStartTurn() + "");
                        panel.add(startTurnField);
                        panel.add(new JLabel(""));

                        panel.add(new JLabel(I18n.get("label.starting_field_angle")));
                        final JTextField startAngleField = new JTextField(threeDecimalsFormat.format(heliostatSpiralFieldLayout.getStartAngle()));
                        panel.add(startAngleField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.counter_clockwise_from_east")));

                        panel.add(new JLabel(I18n.get("label.ending_field_angle")));
                        final JTextField endAngleField = new JTextField(threeDecimalsFormat.format(heliostatSpiralFieldLayout.getEndAngle()));
                        panel.add(endAngleField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.counter_clockwise_from_east")));

                        panel.add(new JLabel(I18n.get("label.pole_height")));
                        final JTextField poleHeightField = new JTextField(threeDecimalsFormat.format(heliostatSpiralFieldLayout.getPoleHeight()));
                        panel.add(poleHeightField);
                        panel.add(new JLabel("<html><font size=2>" + I18n.get("label.meters")));

                        SpringUtilities.makeCompactGrid(panel, 10, 3, 6, 6, 6, 6);

                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.spiral_heliostat_array_options"));

                        while (true) {
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1] || choice == null) {
                                break;
                            } else {
                                boolean ok = true;
                                try {
                                    heliostatSpiralFieldLayout.setApertureWidth(Double.parseDouble(widthField.getText()));
                                    heliostatSpiralFieldLayout.setApertureHeight(Double.parseDouble(heightField.getText()));
                                    heliostatSpiralFieldLayout.setStartTurn(Integer.parseInt(startTurnField.getText()));
                                    heliostatSpiralFieldLayout.setScalingFactor(Double.parseDouble(scalingFactorField.getText()));
                                    heliostatSpiralFieldLayout.setRadialExpansionRatio(Double.parseDouble(radialExpansionRatioField.getText()));
                                    heliostatSpiralFieldLayout.setStartAngle(Double.parseDouble(startAngleField.getText()));
                                    heliostatSpiralFieldLayout.setEndAngle(Double.parseDouble(endAngleField.getText()));
                                    heliostatSpiralFieldLayout.setDivergence(Math.toRadians(Double.parseDouble(divergenceField.getText())));
                                    heliostatSpiralFieldLayout.setPoleHeight(Double.parseDouble(poleHeightField.getText()));
                                } catch (final NumberFormatException exception) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    if (heliostatSpiralFieldLayout.getStartTurn() <= 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.start_turn_positive"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getScalingFactor() <= 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.scaling_factor_positive"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getRadialExpansionRatio() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.radial_expansion_ratio_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getDivergence() < Math.toRadians(5) || heliostatSpiralFieldLayout.getDivergence() > Math.toRadians(175)) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.divergence_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getStartAngle() < -360 || heliostatSpiralFieldLayout.getStartAngle() > 360) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.starting_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getEndAngle() < -360 || heliostatSpiralFieldLayout.getEndAngle() > 360) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.ending_angle_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getEndAngle() <= heliostatSpiralFieldLayout.getStartAngle()) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.ending_angle_greater"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getApertureWidth() < 1 || heliostatSpiralFieldLayout.getApertureWidth() > 50) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_width_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getApertureHeight() < 1 || heliostatSpiralFieldLayout.getApertureHeight() > 50) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatSpiralFieldLayout.getPoleHeight() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        addSpiralHeliostatArrays();
                                        if (choice == options[0]) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                private void addSpiralHeliostatArrays() {
                    heliostatSpiralFieldLayout.setType(typeComboBox.getSelectedIndex());
                    SceneManager.getTaskManager().update(() -> {
                        final int count = f.addHeliostats(heliostatSpiralFieldLayout);
                        if (count == 0) {
                            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                    I18n.get("msg.heliostat_array_cant_be_created"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE));
                        }
                        return null;
                    });
                    updateAfterEdit();
                }

            });

            final JMenuItem miHeliostatRectangularArrays = new JMenuItem(I18n.get("menu.heliostat_rectangular_layout"));
            layoutMenu.add(miHeliostatRectangularArrays);
            miHeliostatRectangularArrays.addActionListener(new ActionListener() {

                private Foundation f;
                private JComboBox<String> rowAxisComboBox;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Foundation) {
                        f = (Foundation) selectedPart;
                        final int n = f.countParts(Mirror.class);
                        if (n > 0 && JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_heliostats", n), I18n.get("dialog.confirmation"), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION) {
                            return;
                        }

                        final JPanel panel = new JPanel(new SpringLayout());
                        panel.add(new JLabel(I18n.get("label.row_axis")));
                        rowAxisComboBox = new JComboBox<>(new String[]{I18n.get("row_axis.north_south"), I18n.get("row_axis.east_west")});
                        rowAxisComboBox.setSelectedIndex(heliostatRectangularFieldLayout.getRowAxis());
                        panel.add(rowAxisComboBox);
                        panel.add(new JLabel(I18n.get("label.aperture_width")));
                        final JTextField widthField = new JTextField(threeDecimalsFormat.format(heliostatRectangularFieldLayout.getApertureWidth()));
                        panel.add(widthField);
                        panel.add(new JLabel(I18n.get("label.aperture_height")));
                        final JTextField heightField = new JTextField(threeDecimalsFormat.format(heliostatRectangularFieldLayout.getApertureHeight()));
                        panel.add(heightField);
                        panel.add(new JLabel(I18n.get("label.row_spacing")));
                        final JTextField rowSpacingField = new JTextField(threeDecimalsFormat.format(heliostatRectangularFieldLayout.getRowSpacing()));
                        panel.add(rowSpacingField);
                        panel.add(new JLabel(I18n.get("label.column_spacing")));
                        final JTextField columnSpacingField = new JTextField(threeDecimalsFormat.format(heliostatRectangularFieldLayout.getColumnSpacing()));
                        panel.add(columnSpacingField);
                        panel.add(new JLabel(I18n.get("label.pole_height")));
                        final JTextField poleHeightField = new JTextField(threeDecimalsFormat.format(heliostatRectangularFieldLayout.getPoleHeight()));
                        panel.add(poleHeightField);
                        SpringUtilities.makeCompactGrid(panel, 6, 2, 6, 6, 6, 6);

                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.rectangular_heliostat_array_options"));

                        while (true) {
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1] || choice == null) {
                                break;
                            } else {
                                boolean ok = true;
                                try {
                                    heliostatRectangularFieldLayout.setRowSpacing(Double.parseDouble(rowSpacingField.getText()));
                                    heliostatRectangularFieldLayout.setColumnSpacing(Double.parseDouble(columnSpacingField.getText()));
                                    heliostatRectangularFieldLayout.setApertureWidth(Double.parseDouble(widthField.getText()));
                                    heliostatRectangularFieldLayout.setApertureHeight(Double.parseDouble(heightField.getText()));
                                    heliostatRectangularFieldLayout.setPoleHeight(Double.parseDouble(poleHeightField.getText()));
                                } catch (final NumberFormatException exception) {
                                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                                    ok = false;
                                }
                                if (ok) {
                                    if (heliostatRectangularFieldLayout.getRowSpacing() < 0 || heliostatRectangularFieldLayout.getColumnSpacing() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.heliostat_spacing_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatRectangularFieldLayout.getApertureWidth() < 1 || heliostatRectangularFieldLayout.getApertureWidth() > 50) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_width_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatRectangularFieldLayout.getApertureHeight() < 1 || heliostatRectangularFieldLayout.getApertureHeight() > 50) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.aperture_height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else if (heliostatRectangularFieldLayout.getPoleHeight() < 0) {
                                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.pole_height_negative"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        addRectangularHeliostatArrays();
                                        if (choice == options[0]) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                private void addRectangularHeliostatArrays() {
                    heliostatRectangularFieldLayout.setRowAxis(rowAxisComboBox.getSelectedIndex());
                    SceneManager.getTaskManager().update(() -> {
                        final int count = f.addHeliostats(heliostatRectangularFieldLayout);
                        if (count == 0) {
                            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.getInstance(),
                                    I18n.get("msg.heliostat_array_cant_be_created"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE));
                        }
                        return null;
                    });
                    updateAfterEdit();
                }

            });

            final JMenu optimizeMenu = new JMenu(I18n.get("menu.optimize"));

            final JMenuItem miBuildingLocation = new JMenuItem(I18n.get("menu.building_location"));
            miBuildingLocation.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    BuildingLocationOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miBuildingLocation);

            final JMenuItem miBuildingOrientation = new JMenuItem(I18n.get("menu.building_orientation"));
            miBuildingOrientation.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    BuildingOrientationOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miBuildingOrientation);

            final JMenuItem miWindows = new JMenuItem(I18n.get("menu.window_sizes"));
            miWindows.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    WindowOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miWindows);
            optimizeMenu.addSeparator();

            final JMenuItem miSolarPanelTiltAngle = new JMenuItem(I18n.get("menu.solar_panel_tilt_angles"));
            miSolarPanelTiltAngle.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    SolarPanelTiltAngleOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miSolarPanelTiltAngle);

            final JMenuItem miSolarArray = new JMenuItem(I18n.get("menu.solar_panel_arrays"));
            miSolarArray.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    SolarPanelArrayOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miSolarArray);
            optimizeMenu.addSeparator();

            final JMenuItem miHeliostatPositions = new JMenuItem(I18n.get("menu.heliostat_positions"));
            miHeliostatPositions.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    HeliostatPositionOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miHeliostatPositions);

            final JMenuItem miHeliostatConcentricField = new JMenuItem(I18n.get("menu.heliostat_concentric_field"));
            miHeliostatConcentricField.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    HeliostatConcentricFieldOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miHeliostatConcentricField);

            final JMenuItem miHeliostatSpiralField = new JMenuItem(I18n.get("menu.heliostat_spiral_field"));
            miHeliostatSpiralField.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    HeliostatSpiralFieldOptimizer.make((Foundation) selectedPart);
                }
            });
            optimizeMenu.add(miHeliostatSpiralField);

            optimizeMenu.addMenuListener(new MenuListener() {

                @Override
                public void menuSelected(final MenuEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Foundation)) {
                        return;
                    }
                    final Foundation foundation = (Foundation) selectedPart;
                    miBuildingLocation.setEnabled(!foundation.getLockEdit() && foundation.getWalls().size() > 0);
                    miBuildingOrientation.setEnabled(miBuildingLocation.isEnabled());
                    miWindows.setEnabled(foundation.getWindows().size() > 0);
                    miSolarPanelTiltAngle.setEnabled(foundation.getRacks().size() > 0);
                    miSolarArray.setEnabled(miSolarPanelTiltAngle.isEnabled());
                    miHeliostatPositions.setEnabled(foundation.getHeliostats().size() > 0);
                    miHeliostatConcentricField.setEnabled(miHeliostatPositions.isEnabled());
                    miHeliostatSpiralField.setEnabled(miHeliostatPositions.isEnabled());
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    optimizeMenu.setEnabled(true);
                }

                @Override
                public void menuCanceled(final MenuEvent e) {
                    optimizeMenu.setEnabled(true);
                }

            });

            final JMenu utilityMenu = new JMenu(I18n.get("menu.utility_bill"));

            final JMenuItem miAddUtilityBill = new JMenuItem(I18n.get("menu.add"));
            utilityMenu.add(miAddUtilityBill);
            miAddUtilityBill.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    UtilityBill b = f.getUtilityBill();
                    if (b == null) {
                        if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.no_utility_bill_create"), I18n.get("title.utility_bill_for_building", f.getId()), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                            return;
                        }
                        b = new UtilityBill();
                        f.setUtilityBill(b);
                    }
                    new UtilityBillDialog(b).setVisible(true);
                    Scene.getInstance().setEdited(true);
                }
            });

            final JMenuItem miDeleteUtilityBill = new JMenuItem(I18n.get("menu.delete"));
            utilityMenu.add(miDeleteUtilityBill);
            miDeleteUtilityBill.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    if (f.getUtilityBill() == null) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_utility_bill"), I18n.get("title.no_utility_bill"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.confirm_remove_utility_bill"), I18n.get("dialog.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                            final DeleteUtilityBillCommand c = new DeleteUtilityBillCommand(f);
                            f.setUtilityBill(null);
                            Scene.getInstance().setEdited(true);
                            SceneManager.getInstance().getUndoManager().addEdit(c);
                        }
                    }
                }
            });

            final JMenuItem miGroupMaster = new JCheckBoxMenuItem(I18n.get("menu.group_master"));
            miGroupMaster.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    SceneManager.getInstance().getUndoManager().addEdit(new SetGroupMasterCommand((Foundation) selectedPart));
                    ((Foundation) selectedPart).setGroupMaster(miGroupMaster.isSelected());
                    Scene.getInstance().setEdited(true);
                }
            });

            final JCheckBoxMenuItem miEnableInset = new JCheckBoxMenuItem(I18n.get("menu.enable_polygon_inset"));
            miEnableInset.addItemListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation foundation = (Foundation) selectedPart;
                    SceneManager.getInstance().getUndoManager().addEdit(new ShowFoundationInsetCommand(foundation));
                    SceneManager.getTaskManager().update(() -> {
                        foundation.getPolygon().setVisible(miEnableInset.isSelected());
                        foundation.draw();
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                }
            });

            final JCheckBoxMenuItem miDisableEditPoints = new JCheckBoxMenuItem(I18n.get("menu.disable_edit_points"));
            miDisableEditPoints.addItemListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    selectedPart.setLockEdit(miDisableEditPoints.isSelected());
                    Scene.getInstance().setEdited(true);
                }
            });

            final JMenu optionsMenu = new JMenu(I18n.get("menu.options"));

            final JMenuItem miChildGridSize = new JMenuItem(I18n.get("menu.grid_size"));
            miChildGridSize.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Foundation)) {
                    return;
                }
                final Foundation f = (Foundation) selectedPart;
                while (true) {
                    final String newValue = JOptionPane.showInputDialog(MainFrame.getInstance(), I18n.get("dialog.grid_size_m"), f.getChildGridSize() * Scene.getInstance().getScale());
                    if (newValue == null) {
                        break;
                    } else {
                        try {
                            final double val = Double.parseDouble(newValue);
                            if (val < 0.1 || val > 5) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.grid_size_range"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                f.setChildGridSize(val / Scene.getInstance().getScale());
                                updateAfterEdit();
                                break;
                            }
                        } catch (final NumberFormatException exception) {
                            exception.printStackTrace();
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", newValue), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            optionsMenu.add(miChildGridSize);

            final JMenu projectTypeSubMenu = new JMenu(I18n.get("menu.project_type"));
            optionsMenu.add(projectTypeSubMenu);

            final ButtonGroup bgStructureTypes = new ButtonGroup();

            final JRadioButtonMenuItem rbmiTypeAutoDetected = new JRadioButtonMenuItem(I18n.get("project_type.auto_detected"));
            rbmiTypeAutoDetected.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation foundation = (Foundation) selectedPart;
                    foundation.setProjectType(Foundation.TYPE_AUTO_DETECTED);
                }
            });
            projectTypeSubMenu.add(rbmiTypeAutoDetected);
            bgStructureTypes.add(rbmiTypeAutoDetected);

            final JRadioButtonMenuItem rbmiTypeBuilding = new JRadioButtonMenuItem(I18n.get("project_type.building"));
            rbmiTypeBuilding.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation foundation = (Foundation) selectedPart;
                    foundation.setProjectType(Foundation.TYPE_BUILDING);
                }
            });
            projectTypeSubMenu.add(rbmiTypeBuilding);
            bgStructureTypes.add(rbmiTypeBuilding);

            final JRadioButtonMenuItem rbmiTypePvStation = new JRadioButtonMenuItem(I18n.get("project_type.pv_system"));
            rbmiTypePvStation.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation foundation = (Foundation) selectedPart;
                    foundation.setProjectType(Foundation.TYPE_PV_PROJECT);
                }
            });
            projectTypeSubMenu.add(rbmiTypePvStation);
            bgStructureTypes.add(rbmiTypePvStation);

            final JRadioButtonMenuItem rbmiTypeCspStation = new JRadioButtonMenuItem(I18n.get("project_type.csp_system"));
            rbmiTypeCspStation.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation foundation = (Foundation) selectedPart;
                    foundation.setProjectType(Foundation.TYPE_CSP_PROJECT);
                }
            });
            projectTypeSubMenu.add(rbmiTypeCspStation);
            bgStructureTypes.add(rbmiTypeCspStation);

            final JMenuItem miThermostat = new JMenuItem(I18n.get("menu.thermostat"));
            miThermostat.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation foundation = (Foundation) selectedPart;
                    MainPanel.getInstance().getEnergyButton().setSelected(false);
                    new ThermostatDialog(foundation).setVisible(true);
                    TimeSeriesLogger.getInstance().logAdjustThermostatButton();
                    Scene.getInstance().setEdited(true);
                }
            });

            final JMenuItem miSize = new JMenuItem(I18n.get("menu.size"));
            miSize.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Foundation)) {
                    return;
                }
                final Foundation f = (Foundation) selectedPart;
                final boolean hasChildren = !f.getChildren().isEmpty();
                final Vector3 v0 = f.getAbsPoint(0);
                final Vector3 v1 = f.getAbsPoint(1);
                final Vector3 v2 = f.getAbsPoint(2);
                double lx0 = v0.distance(v2) * Scene.getInstance().getScale();
                double ly0 = v0.distance(v1) * Scene.getInstance().getScale();
                double lz0 = f.getHeight() * Scene.getInstance().getScale();

                final JPanel gui = new JPanel(new BorderLayout());
                final String title = "<html>" + I18n.get("title.size_of_foundation", f.getId()) + "</html>";
                gui.add(new JLabel(title), BorderLayout.NORTH);
                final JPanel inputPanel = new JPanel(new SpringLayout());
                inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                gui.add(inputPanel, BorderLayout.CENTER);
                JLabel l = new JLabel(I18n.get("label.length"), JLabel.TRAILING);
                inputPanel.add(l);
                final JTextField lxField = new JTextField(threeDecimalsFormat.format(lx0), 5);
                lxField.setEditable(!hasChildren);
                l.setLabelFor(lxField);
                inputPanel.add(lxField);
                l = new JLabel(I18n.get("label.width"), JLabel.TRAILING);
                inputPanel.add(l);
                final JTextField lyField = new JTextField(threeDecimalsFormat.format(ly0), 5);
                lyField.setEditable(!hasChildren);
                l.setLabelFor(lyField);
                inputPanel.add(lyField);
                l = new JLabel(I18n.get("label.height"), JLabel.TRAILING);
                inputPanel.add(l);
                final JTextField lzField = new JTextField(threeDecimalsFormat.format(lz0), 5);
                l.setLabelFor(lzField);
                inputPanel.add(lzField);
                SpringUtilities.makeCompactGrid(inputPanel, 3, 2, 6, 6, 6, 6);

                final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                final JOptionPane optionPane = new JOptionPane(gui, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.foundation_size"));
                while (true) {
                    dialog.setVisible(true);
                    final Object choice = optionPane.getValue();
                    if (choice == options[1] || choice == null) {
                        break;
                    } else {
                        double lx1 = lx0, ly1 = ly0, lz1 = lz0;
                        boolean ok = true;
                        try {
                            lx1 = Double.parseDouble(lxField.getText());
                            ly1 = Double.parseDouble(lyField.getText());
                            lz1 = Double.parseDouble(lzField.getText());
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_input"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                            ok = false;
                        }
                        if (ok) {
                            if (lx1 < 0.1 || lx1 > 1000) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.length_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (ly1 < 0.1 || ly1 > 1000) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.width_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else if (lz1 < 0.01 || lz1 > 100) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.height_range"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                if (lx1 != lx0 || ly1 != ly0 || lz1 != lz0) {
                                    final double scaleX = lx1 / lx0;
                                    final double scaleY = ly1 / ly0;
                                    final double scaleZ = lz1 / Scene.getInstance().getScale();
                                    SceneManager.getTaskManager().update(() -> {
                                        f.rescale(scaleX, scaleY, 1);
                                        f.setHeight(scaleZ);
                                        f.draw();
                                        f.drawChildren();
                                        SceneManager.getInstance().refresh();
                                        return null;
                                    });
                                    SceneManager.getInstance().getUndoManager().addEdit(new ChangeFoundationSizeCommand(f, lx0, lx1, ly0, ly1, lz0, lz1));
                                    updateAfterEdit();
                                    lx0 = lx1;
                                    ly0 = ly1;
                                    lz0 = lz1;
                                }
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }

                    }
                }

            });

            final JMenuItem miResize = new JMenuItem(I18n.get("menu.resize_structure_above"));
            miResize.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (!(selectedPart instanceof Foundation)) {
                    return;
                }
                final Foundation f = (Foundation) selectedPart;
                if (f.getChildren().isEmpty()) {
                    return;
                }
                SceneManager.getTaskManager().update(() -> {
                    for (final HousePart p : Scene.getInstance().getParts()) {
                        if (p instanceof Foundation) {
                            if (p != f) {
                                ((Foundation) p).setResizeHouseMode(false);
                            }
                        }
                    }
                    f.setResizeHouseMode(true);
                    return null;
                });
            });

            final JMenu labelMenu = new JMenu(I18n.get("menu.label"));

            final JCheckBoxMenuItem miLabelNone = new JCheckBoxMenuItem(I18n.get("label.none"), true);
            miLabelNone.addActionListener(e -> {
                if (miLabelNone.isSelected()) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Foundation) {
                        final Foundation f = (Foundation) selectedPart;
                        final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                        SceneManager.getTaskManager().update(() -> {
                            f.clearLabels();
                            f.draw();
                            SceneManager.getInstance().refresh();
                            return null;
                        });
                        SceneManager.getInstance().getUndoManager().addEdit(c);
                        Scene.getInstance().setEdited(true);
                    }
                }
            });
            labelMenu.add(miLabelNone);

            final JCheckBoxMenuItem miLabelCustom = new JCheckBoxMenuItem(I18n.get("label.custom"));
            miLabelCustom.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelCustom(miLabelCustom.isSelected());
                    if (f.getLabelCustom()) {
                        f.setLabelCustomText(JOptionPane.showInputDialog(MainFrame.getInstance(), I18n.get("dialog.custom_text"), f.getLabelCustomText()));
                    }
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelCustom);

            final JCheckBoxMenuItem miLabelId = new JCheckBoxMenuItem(I18n.get("label.id"));
            miLabelId.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelId(miLabelId.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelId);

            final JCheckBoxMenuItem miLabelNumberOfSolarPanels = new JCheckBoxMenuItem(I18n.get("label.number_of_solar_panels"));
            miLabelNumberOfSolarPanels.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelNumberOfSolarPanels(miLabelNumberOfSolarPanels.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelNumberOfSolarPanels);

            final JCheckBoxMenuItem miLabelPvEnergy = new JCheckBoxMenuItem(I18n.get("label.photovoltaic_output"));
            miLabelPvEnergy.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelPvEnergy(miLabelPvEnergy.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelPvEnergy);

            final JCheckBoxMenuItem miLabelSolarPotential = new JCheckBoxMenuItem(I18n.get("label.solar_potential"));
            miLabelSolarPotential.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelSolarPotential(miLabelSolarPotential.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelSolarPotential);

            final JCheckBoxMenuItem miLabelBuildingEnergy = new JCheckBoxMenuItem(I18n.get("label.building_energy"));
            miLabelBuildingEnergy.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelBuildingEnergy(miLabelBuildingEnergy.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            labelMenu.add(miLabelBuildingEnergy);

            final JMenu powerTowerLabelMenu = new JMenu(I18n.get("menu.power_tower"));
            labelMenu.add(powerTowerLabelMenu);

            final JCheckBoxMenuItem miLabelNumberOfHeliostats = new JCheckBoxMenuItem(I18n.get("label.number_of_heliostats"));
            miLabelNumberOfHeliostats.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelNumberOfMirrors(miLabelNumberOfHeliostats.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            powerTowerLabelMenu.add(miLabelNumberOfHeliostats);

            final JCheckBoxMenuItem miLabelPowerTowerHeight = new JCheckBoxMenuItem(I18n.get("label.tower_height"));
            miLabelPowerTowerHeight.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelPowerTowerHeight(miLabelPowerTowerHeight.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            powerTowerLabelMenu.add(miLabelPowerTowerHeight);

            final JCheckBoxMenuItem miLabelPowerTowerOutput = new JCheckBoxMenuItem(I18n.get("label.energy_output"));
            miLabelPowerTowerOutput.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    final SetFoundationLabelCommand c = new SetFoundationLabelCommand(f);
                    f.setLabelPowerTowerOutput(miLabelPowerTowerOutput.isSelected());
                    SceneManager.getTaskManager().update(() -> {
                        f.draw();
                        SceneManager.getInstance().refresh();
                        return null;
                    });
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                    Scene.getInstance().setEdited(true);
                }
            });
            powerTowerLabelMenu.add(miLabelPowerTowerOutput);

            final JMenu textureMenu = new JMenu(I18n.get("menu.texture"));
            final ButtonGroup textureGroup = new ButtonGroup();
            final JRadioButtonMenuItem rbmiTextureNone = createTextureMenuItem(Foundation.TEXTURE_NONE, null);
            final JRadioButtonMenuItem rbmiTextureEdge = createTextureMenuItem(Foundation.TEXTURE_EDGE, null);
            final JRadioButtonMenuItem rbmiTexture01 = createTextureMenuItem(Foundation.TEXTURE_01, "icons/foundation_01.png");
            textureGroup.add(rbmiTextureNone);
            textureGroup.add(rbmiTextureEdge);
            textureGroup.add(rbmiTexture01);
            textureMenu.add(rbmiTextureNone);
            textureMenu.add(rbmiTextureEdge);
            textureMenu.addSeparator();
            textureMenu.add(rbmiTexture01);

            textureMenu.addMenuListener(new MenuListener() {

                @Override
                public void menuSelected(final MenuEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Foundation)) {
                        return;
                    }
                    final Foundation foundation = (Foundation) selectedPart;
                    switch (foundation.getTextureType()) {
                        case Foundation.TEXTURE_NONE:
                            Util.selectSilently(rbmiTextureNone, true);
                            break;
                        case Foundation.TEXTURE_EDGE:
                            Util.selectSilently(rbmiTextureEdge, true);
                            break;
                        case Foundation.TEXTURE_01:
                            Util.selectSilently(rbmiTexture01, true);
                            break;
                        default:
                            textureGroup.clearSelection();
                    }
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    textureMenu.setEnabled(true);
                }

                @Override
                public void menuCanceled(final MenuEvent e) {
                    textureMenu.setEnabled(true);
                }

            });

            popupMenuForFoundation = createPopupMenu(false, true, () -> {
                final HousePart p = SceneManager.getInstance().getSelectedPart();
                if (p instanceof Foundation) {
                    final Foundation f = (Foundation) p;
                    if (Scene.getInstance().isStudentMode()) {
                        miDisableEditPoints.setEnabled(false);
                        miThermostat.setEnabled(false);
                    } else {
                        miDisableEditPoints.setEnabled(true);
                        miThermostat.setEnabled(true);
                    }
                    miDeleteUtilityBill.setEnabled(f.getUtilityBill() != null);
                    Util.selectSilently(miGroupMaster, f.isGroupMaster());
                    Util.selectSilently(miDisableEditPoints, f.getLockEdit());
                    Util.selectSilently(miEnableInset, f.getPolygon().isVisible());
                    Util.selectSilently(miLabelNone, !f.isLabelVisible());
                    Util.selectSilently(miLabelCustom, f.getLabelCustom());
                    Util.selectSilently(miLabelId, f.getLabelId());
                    Util.selectSilently(miLabelPowerTowerOutput, f.getLabelPowerTowerOutput());
                    Util.selectSilently(miLabelPowerTowerHeight, f.getLabelPowerTowerHeight());
                    Util.selectSilently(miLabelNumberOfHeliostats, f.getLabelNumberOfMirrors());
                    Util.selectSilently(miLabelSolarPotential, f.getLabelSolarPotential());
                    Util.selectSilently(miLabelPvEnergy, f.getLabelPvEnergy());
                    Util.selectSilently(miLabelNumberOfSolarPanels, f.getLabelNumberOfSolarPanels());
                    Util.selectSilently(miLabelBuildingEnergy, f.getLabelBuildingEnergy());
                    powerTowerLabelMenu.setEnabled(f.hasSolarReceiver());
                    switch (f.getProjectType()) {
                        case Foundation.TYPE_BUILDING:
                            Util.selectSilently(rbmiTypeBuilding, true);
                            break;
                        case Foundation.TYPE_PV_PROJECT:
                            Util.selectSilently(rbmiTypePvStation, true);
                            break;
                        case Foundation.TYPE_CSP_PROJECT:
                            Util.selectSilently(rbmiTypeCspStation, true);
                            break;
                        default:
                            Util.selectSilently(rbmiTypeAutoDetected, true);
                    }
                    miResize.setEnabled(!f.getChildren().isEmpty());
                    SceneManager.getTaskManager().update(() -> {
                        for (final HousePart x : Scene.getInstance().getParts()) {
                            if (x instanceof Foundation) {
                                if (x != f) {
                                    ((Foundation) x).setResizeHouseMode(false);
                                }
                            }
                        }
                        return null;
                    });
                }
                final HousePart copyBuffer = Scene.getInstance().getCopyBuffer();
                final Node copyNode = Scene.getInstance().getCopyNode();
                miPaste.setEnabled(copyBuffer instanceof SolarCollector || copyBuffer instanceof Human || copyBuffer instanceof Tree || copyNode != null);
            });

            popupMenuForFoundation.add(miPaste);
            popupMenuForFoundation.add(miCopy);
            popupMenuForFoundation.addSeparator();
            popupMenuForFoundation.add(miImportCollada);
            popupMenuForFoundation.add(miResize);
            popupMenuForFoundation.add(miSize);
            popupMenuForFoundation.add(miRescale);
            popupMenuForFoundation.add(rotateMenu);
            popupMenuForFoundation.add(clearMenu);
            popupMenuForFoundation.add(layoutMenu);
            popupMenuForFoundation.add(optimizeMenu);
            popupMenuForFoundation.addSeparator();
            popupMenuForFoundation.add(miDisableEditPoints);
            popupMenuForFoundation.add(miEnableInset);
            popupMenuForFoundation.add(miGroupMaster);
            popupMenuForFoundation.add(optionsMenu);
            popupMenuForFoundation.add(labelMenu);
            popupMenuForFoundation.addSeparator();
            popupMenuForFoundation.add(colorAction);
            popupMenuForFoundation.add(textureMenu);
            // floor insulation only for the first floor, so this U-value is associated with the Foundation class, not the Floor class
            popupMenuForFoundation.add(createInsulationMenuItem(false));
            popupMenuForFoundation.add(createVolumetricHeatCapacityMenuItem());
            popupMenuForFoundation.add(miThermostat);
            popupMenuForFoundation.add(utilityMenu);
            popupMenuForFoundation.addSeparator();

            final JMenu analysisMenu = new JMenu(I18n.get("menu.analysis"));
            popupMenuForFoundation.add(analysisMenu);

            JMenu subMenu = new JMenu(I18n.get("menu.buildings"));
            analysisMenu.add(subMenu);

            JMenuItem mi = new JMenuItem(I18n.get("menu.daily_building_energy_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    final EnergyDailyAnalysis analysis = new EnergyDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        analysis.updateGraph();
                    }
                    analysis.show(I18n.get("title.daily_building_energy"));
                }
            });
            subMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_building_energy_analysis"));
            mi.addActionListener(e -> {
                if (EnergyPanel.getInstance().adjustCellSize()) {
                    return;
                }
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    new EnergyAnnualAnalysis().show(I18n.get("title.annual_building_energy"));
                }
            });
            subMenu.add(mi);

            subMenu = new JMenu(I18n.get("menu.solar_panels"));
            analysisMenu.add(subMenu);

            mi = new JMenuItem(I18n.get("menu.daily_solar_panel_yield_analysis"));
            mi.addActionListener(e -> {
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    final Foundation f = (Foundation) SceneManager.getInstance().getSelectedPart();
                    if (f.countParts(new Class[]{SolarPanel.class, Rack.class}) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_solar_panel_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final PvDailyAnalysis a = new PvDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
            subMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_solar_panel_yield_analysis"));
            mi.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    if (f.countParts(new Class[]{SolarPanel.class, Rack.class}) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_solar_panel_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final PvAnnualAnalysis a = new PvAnnualAnalysis();
                    if (f.getUtilityBill() != null) {
                        a.setUtilityBill(f.getUtilityBill());
                    }
                    a.show();
                }
            });
            subMenu.add(mi);

            subMenu = new JMenu(I18n.get("menu.heliostats"));
            analysisMenu.add(subMenu);

            mi = new JMenuItem(I18n.get("menu.daily_heliostat_yield_analysis"));
            mi.addActionListener(e -> {
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    final Foundation f = (Foundation) SceneManager.getInstance().getSelectedPart();
                    if (f.countParts(Mirror.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_heliostat_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HeliostatDailyAnalysis a = new HeliostatDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
            subMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_heliostat_yield_analysis"));
            mi.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    if (f.countParts(Mirror.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_heliostat_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    new HeliostatAnnualAnalysis().show();
                }
            });
            subMenu.add(mi);

            subMenu = new JMenu(I18n.get("menu.parabolic_troughs"));
            analysisMenu.add(subMenu);

            mi = new JMenuItem(I18n.get("menu.daily_parabolic_trough_yield_analysis"));
            mi.addActionListener(e -> {
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    final Foundation f = (Foundation) SceneManager.getInstance().getSelectedPart();
                    if (f.countParts(ParabolicTrough.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_parabolic_trough_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final ParabolicTroughDailyAnalysis a = new ParabolicTroughDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
            subMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_parabolic_trough_yield_analysis"));
            mi.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    if (f.countParts(ParabolicTrough.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_parabolic_trough_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    new ParabolicTroughAnnualAnalysis().show();
                }
            });
            subMenu.add(mi);

            subMenu = new JMenu(I18n.get("menu.parabolic_dishes"));
            analysisMenu.add(subMenu);

            mi = new JMenuItem(I18n.get("menu.daily_parabolic_dish_yield_analysis"));
            mi.addActionListener(e -> {
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    final Foundation f = (Foundation) SceneManager.getInstance().getSelectedPart();
                    if (f.countParts(ParabolicDish.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_parabolic_dish_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final ParabolicDishDailyAnalysis a = new ParabolicDishDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
            subMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_parabolic_dish_yield_analysis"));
            mi.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    if (f.countParts(ParabolicDish.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_parabolic_dish_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    new ParabolicDishAnnualAnalysis().show();
                }
            });
            subMenu.add(mi);

            subMenu = new JMenu(I18n.get("menu.linear_fresnel_reflectors"));
            analysisMenu.add(subMenu);

            mi = new JMenuItem(I18n.get("menu.daily_fresnel_reflector_yield_analysis"));
            mi.addActionListener(e -> {
                if (SceneManager.getInstance().getSelectedPart() instanceof Foundation) {
                    final Foundation f = (Foundation) SceneManager.getInstance().getSelectedPart();
                    if (f.countParts(FresnelReflector.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_fresnel_reflector_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final FresnelReflectorDailyAnalysis a = new FresnelReflectorDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
            subMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.annual_fresnel_reflector_yield_analysis"));
            mi.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart instanceof Foundation) {
                    final Foundation f = (Foundation) selectedPart;
                    if (f.countParts(FresnelReflector.class) <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_fresnel_reflector_to_analyze"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    new FresnelReflectorAnnualAnalysis().show();
                }
            });
            subMenu.add(mi);

        }

        return popupMenuForFoundation;

    }

    private static JRadioButtonMenuItem createTextureMenuItem(final int type, final String imageFile) {

        final JRadioButtonMenuItem m;
        if (type == HousePart.TEXTURE_NONE) {
            m = new JRadioButtonMenuItem(I18n.get("texture.none"));
        } else if (type == HousePart.TEXTURE_EDGE) {
            m = new JRadioButtonMenuItem(I18n.get("texture.edge"));
        } else {
            m = new JRadioButtonMenuItem(new ImageIcon(MainPanel.class.getResource(imageFile)));
            m.setText(I18n.get("texture.number", type));
        }

        m.addItemListener(new ItemListener() {

            private int selectedScopeIndex = 0; // remember the scope selection as the next action will likely be applied to the same scope

            @Override
            public void itemStateChanged(final ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (!(selectedPart instanceof Foundation)) {
                        return;
                    }
                    final Foundation foundation = (Foundation) selectedPart;
                    final String partInfo = foundation.toString().substring(0, selectedPart.toString().indexOf(')') + 1);
                    final JPanel gui = new JPanel(new BorderLayout());
                    final JPanel scopePanel = new JPanel();
                    scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));
                    scopePanel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));
                    final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_foundation"), true);
                    final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_foundations_in_group"));
                    final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_foundations"));
                    scopePanel.add(rb1);
                    scopePanel.add(rb2);
                    scopePanel.add(rb3);
                    final ButtonGroup bg = new ButtonGroup();
                    bg.add(rb1);
                    bg.add(rb2);
                    bg.add(rb3);
                    switch (selectedScopeIndex) {
                        case 0:
                            rb1.setSelected(true);
                            break;
                        case 1:
                            rb2.setSelected(true);
                            break;
                        case 2:
                            rb3.setSelected(true);
                            break;
                    }
                    gui.add(scopePanel, BorderLayout.NORTH);

                    final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                    final JOptionPane optionPane = new JOptionPane(new Object[]{I18n.get("title.set_texture_for", partInfo), gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                    final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.foundation_texture"));

                    while (true) {
                        dialog.setVisible(true);
                        final Object choice = optionPane.getValue();
                        if (choice == options[1] || choice == null) {
                            break;
                        } else {
                            if (rb1.isSelected()) {
                                final ChangeTextureCommand c = new ChangeTextureCommand(foundation);
                                foundation.setTextureType(type);
                                SceneManager.getTaskManager().update(() -> {
                                    foundation.draw();
                                    return null;
                                });
                                SceneManager.getInstance().getUndoManager().addEdit(c);
                                selectedScopeIndex = 0;
                            } else if (rb2.isSelected()) {
                                final List<Foundation> group = Scene.getInstance().getFoundationGroup(foundation);
                                if (group != null && !group.isEmpty()) {
                                    final List<HousePart> parts = new ArrayList<HousePart>();
                                    parts.addAll(group);
                                    final SetTextureForPartsCommand c = new SetTextureForPartsCommand(parts);
                                    for (final Foundation f : group) {
                                        f.setTextureType(type);
                                        SceneManager.getTaskManager().update(() -> {
                                            f.draw();
                                            return null;
                                        });
                                    }
                                    SceneManager.getInstance().getUndoManager().addEdit(c);
                                }
                                selectedScopeIndex = 1;
                            } else if (rb3.isSelected()) {
                                final List<HousePart> foundations = Scene.getInstance().getAllPartsOfSameType(foundation);
                                final SetTextureForPartsCommand c = new SetTextureForPartsCommand(foundations);
                                for (final HousePart f : foundations) {
                                    f.setTextureType(type);
                                    SceneManager.getTaskManager().update(() -> {
                                        f.draw();
                                        return null;
                                    });
                                }
                                SceneManager.getInstance().getUndoManager().addEdit(c);
                                selectedScopeIndex = 2;
                            }
                            updateAfterEdit();
                            if (MainPanel.getInstance().getEnergyButton().isSelected()) {
                                MainPanel.getInstance().getEnergyButton().setSelected(false);
                            }
                            SceneManager.getInstance().refresh();
                            if (choice == options[0]) {
                                break;
                            }
                        }
                    }
                }
            }
        });

        return m;

    }

}