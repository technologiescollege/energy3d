package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.Rack;
import org.concord.energy3d.util.I18n;

public class SetSolarPanelShadeToleranceForRackCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final int oldValue;
	private int newValue;
	private final Rack rack;

	public SetSolarPanelShadeToleranceForRackCommand(final Rack rack) {
		this.rack = rack;
		oldValue = rack.getSolarPanel().getShadeTolerance();
	}

	public Rack getRack() {
		return rack;
	}

	public int getOldValue() {
		return oldValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		newValue = rack.getSolarPanel().getShadeTolerance();
		rack.getSolarPanel().setShadeTolerance(oldValue);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		rack.getSolarPanel().setShadeTolerance(newValue);
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.set_shade_tolerance_solar_panels_selected_rack");
	}

}
