package org.concord.energy3d.undo;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.concord.energy3d.gui.MainPanel;
import org.concord.energy3d.model.Tree;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.util.I18n;

public class ChangePlantCommand extends MyAbstractUndoableEdit {

	private static final long serialVersionUID = 1L;
	private final int oldValue;
	private int newValue;
	private final Tree plant;

	public ChangePlantCommand(final Tree plant) {
		this.plant = plant;
		oldValue = plant.getPlantType();
	}

	public int getOldValue() {
		return oldValue;
	}

	public Tree getTree() {
		return plant;
	}

	@Override
	public void undo() throws CannotUndoException {
		super.undo();
		newValue = plant.getPlantType();
		plant.setPlantType(oldValue);
		plant.draw();
		SceneManager.getInstance().refresh();
		if (MainPanel.getInstance().getEnergyButton().isSelected()) {
			MainPanel.getInstance().getEnergyButton().setSelected(false);
		}
	}

	@Override
	public void redo() throws CannotRedoException {
		super.redo();
		plant.setPlantType(newValue);
		plant.draw();
		SceneManager.getInstance().refresh();
		if (MainPanel.getInstance().getEnergyButton().isSelected()) {
			MainPanel.getInstance().getEnergyButton().setSelected(false);
		}
	}

	@Override
	public String getPresentationName() {
		return I18n.get("undo.plant_type_change");
	}

}
