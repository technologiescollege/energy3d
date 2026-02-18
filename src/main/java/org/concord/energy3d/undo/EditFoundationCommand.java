package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.gui.EnergyPanel;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.HousePart;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.scene.SceneManager.Operation;
import org.concord.energy3d.util.I18n;

public class EditFoundationCommand extends EditPartCommand {

	private static final long serialVersionUID = 1L;
	private final Foundation foundation;
	private final boolean isResizeMode;
	private final boolean isMoveMode;

	public EditFoundationCommand(final Foundation foundation, final boolean moveMode) {
		super(foundation);
		this.foundation = foundation;
		isResizeMode = SceneManager.getInstance().getOperation() == Operation.RESIZE;
		isMoveMode = moveMode;
	}

	@Override
	public HousePart getPart() {
		return foundation;
	}

	@Override
	public void undo() throws CannotUndoException {
		foundation.setResizeHouseMode(isResizeMode);
		// if (!isResizeMode)
		// foundation.saveOrgPoints();
		foundation.setEditPoint(editPoint);
		super.undo();
		try {
			foundation.complete();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		foundation.setResizeHouseMode(SceneManager.getInstance().getOperation() == Operation.RESIZE);
		foundation.draw();
		foundation.drawChildren();
		foundation.updateHandlesOfAllFoudations();
		Scene.getInstance().updateTrackables(foundation);
		SceneManager.getInstance().refresh();
		if (SceneManager.getInstance().getSolarHeatMap()) {
			EnergyPanel.getInstance().updateRadiationHeatMap();
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		foundation.setResizeHouseMode(isResizeMode);
		// if (!isResizeMode)
		// foundation.saveOrgPoints();
		foundation.setEditPoint(editPoint);
		super.redo();
		try {
			foundation.complete();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		foundation.setResizeHouseMode(SceneManager.getInstance().getOperation() == Operation.RESIZE);
		foundation.draw();
		foundation.drawChildren();
		foundation.updateHandlesOfAllFoudations();
		Scene.getInstance().updateTrackables(foundation);
		SceneManager.getInstance().refresh();
		if (SceneManager.getInstance().getSolarHeatMap()) {
			EnergyPanel.getInstance().updateRadiationHeatMap();
		}
	}

	@Override
	public String getPresentationName() {
		if (isMoveMode) {
			return I18n.get("undo.move_building");
		} else if (isResizeMode) {
			return I18n.get("undo.resize_building");
		} else {
			return I18n.get("undo.edit") + " " + MyAbstractUndoableEdit.getPartDisplayName(foundation.getClass());
		}
	}

}
