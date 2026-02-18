package org.concord.energy3d.simulation;

import java.util.ArrayList;
import java.util.List;

import org.concord.energy3d.util.I18n;

/**
 * Annual graph (12 months)
 *
 * @author Charles Xie
 */
public abstract class AnnualGraph extends Graph {

    private static final long serialVersionUID = 1L;
    private static String[] threeLetterMonthCache;
    public static String[] getThreeLetterMonth() {
        if (threeLetterMonthCache == null) {
            threeLetterMonthCache = new String[]{
                I18n.get("month.jan"),
                I18n.get("month.feb"),
                I18n.get("month.mar"),
                I18n.get("month.apr"),
                I18n.get("month.may"),
                I18n.get("month.jun"),
                I18n.get("month.jul"),
                I18n.get("month.aug"),
                I18n.get("month.sep"),
                I18n.get("month.oct"),
                I18n.get("month.nov"),
                I18n.get("month.dec")
            };
        }
        return threeLetterMonthCache;
    }
    public static void invalidateMonthCache() {
        threeLetterMonthCache = null;
    }

    static List<Results> records;

    static {
        records = new ArrayList<>();
    }

    AnnualGraph() {
        super();
        xAxisLabel = I18n.get("axis.month");
        yAxisLabel = I18n.get("axis.energy_per_day");
        xmin = 0;
        xmax = 11;
        numberOfTicks = 12;
    }

    String getXAxisLabel(int i) {
        return getThreeLetterMonth()[i];
    }

    double getXAxisLabelScalingFactor() {
        return 1.0;
    }

    String getXAxisUnit() {
        return "";
    }

}