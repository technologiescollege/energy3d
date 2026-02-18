package org.concord.energy3d.agents;

import javax.swing.JOptionPane;

import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 *
 */
public class EventMinerSheet3 extends EventMinerSheet2 {

	public EventMinerSheet3(final String name) {
		super(name);
	}

	@Override
	public void actuate() {
		System.out.println(this + " Actuating: " + eventString);
		final int i = eventString.lastIndexOf('D');
		if (i == -1) {
			final String msg = I18n.get("msg.investigation_requires_season");
			JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>" + msg + "</html>", I18n.get("title.advice"), JOptionPane.WARNING_MESSAGE);
		} else {
			super.actuate();
		}
	}

}
