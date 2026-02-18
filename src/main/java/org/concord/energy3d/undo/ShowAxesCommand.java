package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.util.I18n;

public class ShowAxesCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final boolean oldValue, newValue;

	public ShowAxesCommand() {
		oldValue = SceneManager.getInstance().areAxesVisible();
		newValue = !oldValue;
	}

	public boolean getNewValue() {
		return newValue;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		SceneManager.getInstance().setAxesVisible(oldValue);
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		SceneManager.getInstance().setAxesVisible(newValue);
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.show_axes");
	}

}
