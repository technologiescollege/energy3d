package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.model.ParabolicDish;
import org.concord.energy3d.util.I18n;

public class SetParabolicDishFocalLengthCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final double oldValue;
	private double newValue;
	private final ParabolicDish dish;

	public SetParabolicDishFocalLengthCommand(final ParabolicDish dish) {
		this.dish = dish;
		oldValue = dish.getFocalLength();
	}

	public ParabolicDish getParabolicDish() {
		return dish;
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
		newValue = dish.getFocalLength();
		dish.setFocalLength(oldValue);
		dish.draw();
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		dish.setFocalLength(newValue);
		dish.draw();
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.set_focal_length_selected_parabolic_dish");
	}

}
