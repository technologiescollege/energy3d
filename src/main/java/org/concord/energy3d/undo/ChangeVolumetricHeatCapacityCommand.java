package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.model.Thermal;
import org.concord.energy3d.util.I18n;

public class ChangeVolumetricHeatCapacityCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private double oldValue, newValue;
	private final HousePart part;

	public ChangeVolumetricHeatCapacityCommand(final HousePart part) {
		this.part = part;
		if (part instanceof Thermal) {
			oldValue = ((Thermal) part).getVolumetricHeatCapacity();
		}
	}

	public double getOldValue() {
		return oldValue;
	}

	public HousePart getPart() {
		return part;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		if (part instanceof Thermal) {
			newValue = ((Thermal) part).getVolumetricHeatCapacity();
			((Thermal) part).setVolumetricHeatCapacity(oldValue);
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		if (part instanceof Thermal) {
			((Thermal) part).setVolumetricHeatCapacity(newValue);
		}
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.volumetric_heat_capacity_change_selected_part");
	}

}
