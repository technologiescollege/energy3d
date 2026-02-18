package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.SolarReflector;
import org.concord.energy3d.util.I18n;

public class ChangeSolarReflectorReflectanceCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final double oldValue;
	private double newValue;
	private final SolarReflector reflector;

	public ChangeSolarReflectorReflectanceCommand(final SolarReflector reflector) {
		this.reflector = reflector;
		oldValue = reflector.getReflectance();
	}

	public SolarReflector getSolarReflector() {
		return reflector;
	}

	public double getOldValue() {
		return oldValue;
	}

	public double getNewValue() {
		return newValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		newValue = reflector.getReflectance();
		reflector.setReflectance(oldValue);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		reflector.setReflectance(newValue);
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.reflectance_change_selected") + " " + MyAbstractUndoableEdit.getPartDisplayName(reflector.getClass());
	}

}
