package org.concord.energy3d.undo;

import java.net.URL;

import javax.swing.undo.AbstractUndoableEdit;

import org.concord.energy3d.agents.UndoableEvent;
import org.concord.energy3d.model.Door;
import org.concord.energy3d.model.Floor;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.FresnelReflector;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Human;
import org.concord.energy3d.model.Mirror;
import org.concord.energy3d.model.ParabolicDish;
import org.concord.energy3d.model.ParabolicTrough;
import org.concord.energy3d.model.Plant;
import org.concord.energy3d.model.Rack;
import org.concord.energy3d.model.Roof;
import org.concord.energy3d.model.Sensor;
import org.concord.energy3d.model.SolarPanel;
import org.concord.energy3d.model.Wall;
import org.concord.energy3d.model.Window;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.util.I18n;

/**
 * Store the timestamp and other information for an undoable edit in order to analyze it
 * 
 * @author Charles Xie
 *
 */
public abstract class MyAbstractUndoableEdit extends AbstractUndoableEdit implements UndoableEvent {

	private static final long serialVersionUID = 1L;
	protected long timestamp;
	protected URL file;

	public MyAbstractUndoableEdit() {
		super();
		timestamp = System.currentTimeMillis();
		file = Scene.getURL();
	}

	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String getName() {
		return getPresentationName();
	}

	@Override
	public char getOneLetterCode() {
		if (getName().length() <= 0) {
			return '*';
		}
		return getName().charAt(0);
	}

	@Override
	public URL getFile() {
		return file;
	}

	/**
	 * Returns the internationalized display name for a HousePart class.
	 * Used by undo commands and other UI (e.g. BuildingCost table) to translate class simple names to user-friendly labels.
	 */
	public static String getPartDisplayName(final Class<?> partClass) {
		if (partClass == Foundation.class) {
			return I18n.get("part.foundation");
		} else if (partClass == Wall.class) {
			return I18n.get("part.wall");
		} else if (partClass == Roof.class) {
			return I18n.get("part.roof");
		} else if (partClass == Window.class) {
			return I18n.get("part.window");
		} else if (partClass == Door.class) {
			return I18n.get("part.door");
		} else if (partClass == Floor.class) {
			return I18n.get("part.floor");
		} else if (partClass == Plant.class) {
			return I18n.get("part.plant");
		} else if (partClass == Human.class) {
			return I18n.get("part.human");
		} else if (partClass == Sensor.class) {
			return I18n.get("part.sensor_module");
		} else if (partClass == SolarPanel.class) {
			return I18n.get("part.single_solar_panel");
		} else if (partClass == Rack.class) {
			return I18n.get("part.solar_panel_rack");
		} else if (partClass == Mirror.class) {
			return I18n.get("part.heliostat");
		} else if (partClass == ParabolicTrough.class) {
			return I18n.get("part.parabolic_trough");
		} else if (partClass == ParabolicDish.class) {
			return I18n.get("part.parabolic_dish");
		} else if (partClass == FresnelReflector.class) {
			return I18n.get("part.fresnel_reflector");
		}
		// Fallback to simple name if no translation found
		return partClass.getSimpleName();
	}

}
