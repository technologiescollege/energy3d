package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.Rack;
import org.concord.energy3d.util.I18n;

public class SetTemperatureCoefficientPmaxForRackCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final double oldValue;
	private double newValue;
	private final Rack rack;

	public SetTemperatureCoefficientPmaxForRackCommand(final Rack rack) {
		this.rack = rack;
		oldValue = rack.getSolarPanel().getTemperatureCoefficientPmax();
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
		newValue = rack.getSolarPanel().getTemperatureCoefficientPmax();
		rack.getSolarPanel().setTemperatureCoefficientPmax(oldValue);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		rack.getSolarPanel().setTemperatureCoefficientPmax(newValue);
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.set_temperature_coefficient_pmax_selected_rack");
	}

}
