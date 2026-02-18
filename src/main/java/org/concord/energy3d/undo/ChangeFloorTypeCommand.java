package org.concord.energy3d.undo;

import org.concord.energy3d.model.Floor;
import org.concord.energy3d.util.I18n;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

public class ChangeFloorTypeCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final int oldValue;
	private int newValue;
	private final Floor floor;

	public ChangeFloorTypeCommand(final Floor floor) {
		this.floor = floor;
		oldValue = floor.getType();
	}

	public Floor getFloor() {
		return floor;
	}

	public int getOldValue() {
		return oldValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		newValue = floor.getType();
		floor.setType(oldValue);
		floor.draw();
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		floor.setType(newValue);
		floor.draw();
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.type_change_floor");
	}

}