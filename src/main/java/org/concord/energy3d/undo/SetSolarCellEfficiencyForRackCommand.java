package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.Rack;
import org.concord.energy3d.util.I18n;

public class SetSolarCellEfficiencyForRackCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final double oldValue;
	private double newValue;
	private final Rack rack;

	public SetSolarCellEfficiencyForRackCommand(final Rack rack) {
		this.rack = rack;
		oldValue = rack.getSolarPanel().getCellEfficiency();
	}

	public Rack getRack() {
		return rack;
	}

	public double getOldValue() {
		return oldValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		newValue = rack.getSolarPanel().getCellEfficiency();
		rack.getSolarPanel().setCellEfficiency(oldValue);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		rack.getSolarPanel().setCellEfficiency(newValue);
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.set_cell_efficiency_solar_panels_selected_rack");
	}

}
