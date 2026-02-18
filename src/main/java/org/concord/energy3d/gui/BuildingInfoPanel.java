package org.concord.energy3d.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.concord.energy3d.model.Building;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.Wall;
import org.concord.energy3d.model.Window;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.simulation.DesignSpecs;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 */
public class BuildingInfoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DecimalFormat twoDecimals = new DecimalFormat();
    private final DecimalFormat noDecimals = new DecimalFormat();
    private final JPanel heightPanel, areaPanel, windowToFloorPanel;
    private final ColorBar heightBar, areaBar, windowToFloorBar;
    private final JPanel solarPanelCountPanel, windowCountPanel, wallCountPanel;
    private final ColorBar solarPanelCountBar, windowCountBar, wallCountBar;

    BuildingInfoPanel() {

        super(new BorderLayout());

        twoDecimals.setMaximumFractionDigits(2);
        noDecimals.setMaximumFractionDigits(0);

        final JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        add(container, BorderLayout.NORTH);

        // area for the selected building

        areaPanel = new JPanel(new BorderLayout());
        areaPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.area_m2"), true));
        areaPanel.setToolTipText("<html>" + I18n.get("tooltip.building_area") + "</html>");
        container.add(areaPanel);
        areaBar = new ColorBar(Color.WHITE, Color.LIGHT_GRAY);
        areaBar.setUnit("");
        areaBar.setUnitPrefix(false);
        areaBar.setVerticalLineRepresentation(false);
        areaBar.setDecimalDigits(1);
        areaBar.setToolTipText(areaPanel.getToolTipText());
        areaBar.setPreferredSize(new Dimension(100, 16));
        areaPanel.add(areaBar, BorderLayout.CENTER);

        // height for the selected building

        heightPanel = new JPanel(new BorderLayout());
        heightPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.height_m"), true));
        heightPanel.setToolTipText("<html>" + I18n.get("tooltip.building_height") + "</html>");
        container.add(heightPanel);
        heightBar = new ColorBar(Color.WHITE, Color.LIGHT_GRAY);
        heightBar.setUnit("");
        heightBar.setUnitPrefix(false);
        heightBar.setVerticalLineRepresentation(false);
        heightBar.setDecimalDigits(2);
        heightBar.setToolTipText(heightPanel.getToolTipText());
        heightBar.setPreferredSize(new Dimension(100, 16));
        heightPanel.add(heightBar, BorderLayout.CENTER);

        // window-to-floor area ratio for the selected building

        windowToFloorPanel = new JPanel(new BorderLayout());
        windowToFloorPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.window_floor_ratio"), true));
        windowToFloorPanel.setToolTipText("<html>" + I18n.get("tooltip.window_to_floor_ratio") + "</html>");
        container.add(windowToFloorPanel);
        windowToFloorBar = new ColorBar(Color.WHITE, Color.LIGHT_GRAY);
        windowToFloorBar.setUnit("");
        windowToFloorBar.setUnitPrefix(false);
        windowToFloorBar.setVerticalLineRepresentation(false);
        windowToFloorBar.setDecimalDigits(3);
        windowToFloorBar.setToolTipText(windowToFloorPanel.getToolTipText());
        windowToFloorBar.setPreferredSize(new Dimension(100, 16));
        windowToFloorPanel.add(windowToFloorBar, BorderLayout.CENTER);

        // window count for the selected building

        windowCountPanel = new JPanel(new BorderLayout());
        windowCountPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.num_windows"), true));
        windowCountPanel.setToolTipText("<html>" + I18n.get("tooltip.window_count") + "</html>");
        container.add(windowCountPanel);
        windowCountBar = new ColorBar(Color.WHITE, Color.LIGHT_GRAY);
        windowCountBar.setUnit("");
        windowCountBar.setUnitPrefix(false);
        windowCountBar.setVerticalLineRepresentation(false);
        windowCountBar.setDecimalDigits(0);
        windowCountBar.setToolTipText(windowCountPanel.getToolTipText());
        windowCountBar.setPreferredSize(new Dimension(100, 16));
        windowCountPanel.add(windowCountBar, BorderLayout.CENTER);

        // wall count for the selected building

        wallCountPanel = new JPanel(new BorderLayout());
        wallCountPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.num_walls"), true));
        wallCountPanel.setToolTipText("<html>" + I18n.get("tooltip.wall_count") + "</html>");
        container.add(wallCountPanel);
        wallCountBar = new ColorBar(Color.WHITE, Color.LIGHT_GRAY);
        wallCountBar.setUnit("");
        wallCountBar.setUnitPrefix(false);
        wallCountBar.setVerticalLineRepresentation(false);
        wallCountBar.setDecimalDigits(0);
        wallCountBar.setToolTipText(wallCountPanel.getToolTipText());
        wallCountBar.setPreferredSize(new Dimension(100, 16));
        wallCountPanel.add(wallCountBar, BorderLayout.CENTER);

        // solar panel count for the selected building

        solarPanelCountPanel = new JPanel(new BorderLayout());
        solarPanelCountPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.num_solar_panels"), true));
        solarPanelCountPanel.setToolTipText("<html>" + I18n.get("tooltip.solar_panel_count") + "</html>");
        container.add(solarPanelCountPanel);
        solarPanelCountBar = new ColorBar(Color.WHITE, Color.LIGHT_GRAY);
        solarPanelCountBar.setUnit("");
        solarPanelCountBar.setUnitPrefix(false);
        solarPanelCountBar.setVerticalLineRepresentation(false);
        solarPanelCountBar.setDecimalDigits(0);
        solarPanelCountBar.setToolTipText(solarPanelCountPanel.getToolTipText());
        solarPanelCountBar.setPreferredSize(new Dimension(100, 16));
        solarPanelCountPanel.add(solarPanelCountBar, BorderLayout.CENTER);

    }

    /** Updates border titles after locale change. */
    public void refreshLabelsAfterLocaleChange() {
        areaPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.area_m2"), true));
        heightPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.height_m"), true));
        windowToFloorPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.window_floor_ratio"), true));
        windowCountPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.num_windows"), true));
        wallCountPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.num_walls"), true));
        solarPanelCountPanel.setBorder(EnergyPanel.createTitledBorder(I18n.get("info.num_solar_panels"), true));
    }

    void update(final Foundation foundation) {
        final Building b = new Building(foundation);
        if (b.areWallsAcceptable()) {
            b.calculate(false);
            switch (Scene.getInstance().getUnit()) {
                case InternationalSystemOfUnits:
                    areaBar.setValue((float) b.getArea());
                    break;
                case USCustomaryUnits:
                    areaBar.setValue((float) (b.getArea() * 3.28084 * 3.28084));
                    break;
            }
            windowToFloorBar.setValue((float) b.getWindowToFloorRatio());
        } else {
            areaBar.setValue(0);
            windowToFloorBar.setValue(0);
        }
        // relax the requirement of a building
        solarPanelCountBar.setValue(foundation.getNumberOfSolarPanels());
        windowCountBar.setValue(foundation.countParts(Window.class));
        wallCountBar.setValue(foundation.countParts(Wall.class));
        final double height = Scene.getInstance().getScale() * foundation.getBoundingHeight();
        switch (Scene.getInstance().getUnit()) {
            case InternationalSystemOfUnits:
                heightBar.setValue((float) height);
                break;
            case USCustomaryUnits:
                heightBar.setValue((float) (height * 3.28084));
                break;
        }
    }

    public void updateAreaBounds() {
        final DesignSpecs specs = Scene.getInstance().getDesignSpecs();
        if (specs == null) {
            return;
        }
        final double r = 3.28084 * 3.28084;
        String t;
        switch (Scene.getInstance().getUnit()) {
            case InternationalSystemOfUnits:
                t = I18n.get("info.area_m2");
                if (specs.isAreaEnabled()) {
                    t += I18n.get("info.range_prefix") + twoDecimals.format(specs.getMinimumArea()) + I18n.get("info.range_separator") + twoDecimals.format(specs.getMaximumArea()) + I18n.get("info.range_suffix");
                }
                areaBar.setMinimum(specs.getMinimumArea());
                areaBar.setMaximum(specs.getMaximumArea());
                break;
            case USCustomaryUnits:
                t = I18n.get("info.area_ft2");
                if (specs.isAreaEnabled()) {
                    t += I18n.get("info.range_prefix") + noDecimals.format(specs.getMinimumArea() * r) + I18n.get("info.range_separator") + noDecimals.format(specs.getMaximumArea() * r) + I18n.get("info.range_suffix");
                }
                areaBar.setMinimum(specs.getMinimumArea() * r);
                areaBar.setMaximum(specs.getMaximumArea() * r);
                break;
            default:
                t = I18n.get("info.area_m2");
                break;
        }
        areaPanel.setBorder(EnergyPanel.createTitledBorder(t, true));
        areaBar.setEnabled(specs.isAreaEnabled());
        areaBar.repaint();
    }

    public void updateHeightBounds() {
        final DesignSpecs specs = Scene.getInstance().getDesignSpecs();
        if (specs == null) {
            return;
        }
        final double r = 3.28084;
        String t;
        switch (Scene.getInstance().getUnit()) {
            case InternationalSystemOfUnits:
                t = I18n.get("info.height_m");
                if (specs.isHeightEnabled()) {
                    t += I18n.get("info.range_prefix") + twoDecimals.format(specs.getMinimumHeight()) + I18n.get("info.range_separator") + twoDecimals.format(specs.getMaximumHeight()) + I18n.get("info.range_suffix");
                }
                heightBar.setMinimum(specs.getMinimumHeight());
                heightBar.setMaximum(specs.getMaximumHeight());
                break;
            case USCustomaryUnits:
                t = I18n.get("info.height_ft");
                if (specs.isHeightEnabled()) {
                    t += I18n.get("info.range_prefix") + noDecimals.format(specs.getMinimumHeight() * r) + I18n.get("info.range_separator") + noDecimals.format(specs.getMaximumHeight() * r) + I18n.get("info.range_suffix");
                }
                heightBar.setMinimum(specs.getMinimumHeight() * r);
                heightBar.setMaximum(specs.getMaximumHeight() * r);
                break;
            default:
                t = I18n.get("info.height_m");
                break;
        }
        heightPanel.setBorder(EnergyPanel.createTitledBorder(t, true));
        heightBar.setEnabled(specs.isHeightEnabled());
        heightBar.repaint();
    }

    public void updateWindowToFloorRatioBounds() {
        final DesignSpecs specs = Scene.getInstance().getDesignSpecs();
        if (specs == null) {
            return;
        }
        String t = I18n.get("info.window_floor_ratio");
        if (specs.isWindowToFloorRatioEnabled()) {
            t += I18n.get("info.range_prefix") + twoDecimals.format(specs.getMinimumWindowToFloorRatio()) + I18n.get("info.range_separator") + twoDecimals.format(specs.getMaximumWindowToFloorRatio()) + I18n.get("info.range_suffix");
        }
        windowToFloorBar.setMinimum(specs.getMinimumWindowToFloorRatio());
        windowToFloorBar.setMaximum(specs.getMaximumWindowToFloorRatio());
        windowToFloorPanel.setBorder(EnergyPanel.createTitledBorder(t, true));
        windowToFloorBar.setEnabled(specs.isWindowToFloorRatioEnabled());
        windowToFloorBar.repaint();
    }

    public void updateSolarPanelNumberBounds() {
        final DesignSpecs specs = Scene.getInstance().getDesignSpecs();
        if (specs == null) {
            return;
        }
        String t = I18n.get("info.num_solar_panels");
        if (specs.isNumberOfSolarPanelsEnabled()) {
            t += I18n.get("info.range_prefix") + specs.getMinimumNumberOfSolarPanels() + I18n.get("info.range_separator") + specs.getMaximumNumberOfSolarPanels() + I18n.get("info.range_suffix");
        }
        solarPanelCountBar.setMinimum(specs.getMinimumNumberOfSolarPanels());
        solarPanelCountBar.setMaximum(specs.getMaximumNumberOfSolarPanels());
        solarPanelCountPanel.setBorder(EnergyPanel.createTitledBorder(t, true));
        solarPanelCountBar.setEnabled(specs.isNumberOfSolarPanelsEnabled());
        solarPanelCountBar.repaint();
    }

    public void updateWindowNumberBounds() {
        final DesignSpecs specs = Scene.getInstance().getDesignSpecs();
        if (specs == null) {
            return;
        }
        String t = I18n.get("info.num_windows");
        if (specs.isNumberOfWindowsEnabled()) {
            if (specs.getMinimumNumberOfWindows() == 0) {
                t += I18n.get("info.range_less_than") + specs.getMaximumNumberOfWindows() + I18n.get("info.range_suffix");
            } else {
                t += I18n.get("info.range_prefix") + specs.getMinimumNumberOfWindows() + I18n.get("info.range_separator") + specs.getMaximumNumberOfWindows() + I18n.get("info.range_suffix");
            }
        }
        windowCountBar.setMinimum(specs.getMinimumNumberOfWindows());
        windowCountBar.setMaximum(specs.getMaximumNumberOfWindows());
        windowCountPanel.setBorder(EnergyPanel.createTitledBorder(t, true));
        windowCountBar.setEnabled(specs.isNumberOfWindowsEnabled());
        windowCountBar.repaint();
    }

    public void updateWallNumberBounds() {
        final DesignSpecs specs = Scene.getInstance().getDesignSpecs();
        if (specs == null) {
            return;
        }
        String t = I18n.get("info.num_walls");
        if (specs.isNumberOfWallsEnabled()) {
            t += I18n.get("info.range_prefix") + specs.getMinimumNumberOfWalls() + I18n.get("info.range_separator") + specs.getMaximumNumberOfWalls() + I18n.get("info.range_suffix");
        }
        wallCountBar.setMinimum(specs.getMinimumNumberOfWalls());
        wallCountBar.setMaximum(specs.getMaximumNumberOfWalls());
        wallCountPanel.setBorder(EnergyPanel.createTitledBorder(t, true));
        wallCountBar.setEnabled(specs.isNumberOfWallsEnabled());
        wallCountBar.repaint();
    }

}