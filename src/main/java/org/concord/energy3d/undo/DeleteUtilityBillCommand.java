package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.simulation.UtilityBill;
import org.concord.energy3d.util.I18n;

public class DeleteUtilityBillCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final Foundation foundation;
	private final UtilityBill bill;

	public DeleteUtilityBillCommand(final Foundation foundation) {
		this.foundation = foundation;
		bill = foundation.getUtilityBill();
	}

	public Foundation getFoundation() {
		return foundation;
	}

	public UtilityBill getUtilityBill() {
		return bill;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		foundation.setUtilityBill(bill);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		foundation.setUtilityBill(null);
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.delete_utility_bill");
	}

}
