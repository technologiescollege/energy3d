package org.concord.energy3d.agents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.undo.AddPartCommand;
import org.concord.energy3d.undo.AdjustThermostatCommand;
import org.concord.energy3d.undo.ChangeBuildingUValueCommand;
import org.concord.energy3d.undo.ChangeCityCommand;
import org.concord.energy3d.undo.ChangeDateCommand;
import org.concord.energy3d.undo.ChangePartColorCommand;
import org.concord.energy3d.undo.ChangePartUValueCommand;
import org.concord.energy3d.undo.EditPartCommand;
import org.concord.energy3d.undo.MovePartCommand;
import org.concord.energy3d.undo.PastePartCommand;
import org.concord.energy3d.undo.RemovePartCommand;
import org.concord.energy3d.undo.RotateBuildingCommand;
import org.concord.energy3d.util.Util;

/**
 * @author Charles Xie
 *
 */
public class EventMinerSheet2 implements Agent {

	String eventString;

	private final String name;

	private final Map<String, Feedback> warnings;
	private final Map<String, Feedback> reminders;
	private final Map<String, Feedback> mustdos;
	private final String validationRegex;
	private final List<Feedback> progressFeedback;

	static List<Class<?>> observers = new ArrayList<Class<?>>();
	static {
		observers.add(AnalysisEvent.class);
		observers.add(DataCollectionEvent.class);
		observers.add(ChangePartUValueCommand.class);
		observers.add(ChangeBuildingUValueCommand.class);
		observers.add(ChangeDateCommand.class);
		observers.add(ChangeCityCommand.class);
		observers.add(ChangePartColorCommand.class);
		observers.add(AddPartCommand.class);
		observers.add(EditPartCommand.class);
		observers.add(PastePartCommand.class);
		observers.add(RemovePartCommand.class);
		observers.add(MovePartCommand.class);
		observers.add(RotateBuildingCommand.class);
		observers.add(AdjustThermostatCommand.class);
	}

	public EventMinerSheet2(final String name) {

		this.name = name;

		// cases that we can immediately decide the feedback
		mustdos = new LinkedHashMap<String, Feedback>();
		mustdos.put("A+?", new Feedback(JOptionPane.INFORMATION_MESSAGE, false, I18n.get("feedback.mustdo.try_analyzing")));
		mustdos.put("#{2,}", new Feedback(JOptionPane.QUESTION_MESSAGE, false, I18n.get("feedback.mustdo.collect_uvalue_result")));
		mustdos.put("W+?", new Feedback(JOptionPane.INFORMATION_MESSAGE, false, I18n.get("feedback.mustdo.investigate_uvalue")));

		// warnings upon the occurrence of the specified events
		warnings = new LinkedHashMap<String, Feedback>();
		warnings.put("[C]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.changed_location")));
		warnings.put("[D]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.changed_date")));
		warnings.put("[L]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.changed_color")));
		warnings.put("[P]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.modified_house")));
		warnings.put("[R]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.changed_uvalue_not_wall")));
		warnings.put("[T]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.adjusted_thermostat")));
		warnings.put("[U]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.changed_all_walls")));
		warnings.put("[Y]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.annual_analysis")));
		warnings.put("[Z]+?", new Feedback(JOptionPane.WARNING_MESSAGE, false, I18n.get("feedback.warning.rotation")));

		// reminders upon the absence of the specified events
		reminders = new LinkedHashMap<String, Feedback>();
		reminders.put("#{2,}", new Feedback(JOptionPane.QUESTION_MESSAGE, true, I18n.get("feedback.reminder.collect_uvalue_analysis")));

		// compound regex
		String violations = "";
		for (final String x : warnings.keySet()) {
			violations += x.substring(x.indexOf('[') + 1, x.lastIndexOf(']'));
		}
		validationRegex = "(A([^" + violations + "]*?W+?[^" + violations + "]*?)(?=A))+?";

		// instruction for progress
		progressFeedback = new ArrayList<Feedback>();
		progressFeedback.add(new Feedback(JOptionPane.INFORMATION_MESSAGE, false, I18n.get("feedback.progress.only_one_analysis")));
		progressFeedback.add(new Feedback(JOptionPane.QUESTION_MESSAGE, false, I18n.get("feedback.progress.two_analyses")));
		progressFeedback.add(new Feedback(JOptionPane.QUESTION_MESSAGE, false, I18n.get("feedback.progress.multiple_analyses")));

	}

	@Override
	public void sense(final MyEvent e) {
		eventString = EventUtil.eventsToString(observers, 10000, null);
		System.out.println(this + " Sensing:" + e.getName() + ">>> " + eventString);
	}

	@Override
	public void actuate() {
		System.out.println(this + " Actuating: " + eventString);
		Feedback f = null;
		for (final String regex : mustdos.keySet()) { // check the must-do's first
			if (Util.countMatch(Pattern.compile(regex).matcher(eventString)) == 0) {
				f = mustdos.get(regex);
				break;
			}
		}
		if (f == null) {
			final String[] segments = eventString.split("A+?"); // if no A is found, the entire event string is the only segment returned
			if (segments != null && segments.length > 0) {
				f = getFeedbackSingleCharacter(segments, warnings); // prioritize warnings
				if (f == null) {
					f = getFeedbackMultiCharacter(segments, reminders); // if there is no warning, check reminders
				}
				if (f == null) { // if no warning or reminder is found, check progress
					boolean latestHandled = false;
					String latestSegment;
					if (segments.length == 1) {
						latestSegment = eventString;
					} else {
						latestSegment = segments[segments.length - 1];
					}
					if (latestSegment.indexOf('W') != -1) {
						latestHandled = true;
						f = new Feedback(JOptionPane.QUESTION_MESSAGE, false, I18n.get("feedback.question.adjusted_uvalue"));
					}
					if (!latestHandled) {
						String s = "";
						for (int i = 0; i < segments.length - 1; i++) {
							s += segments[i] + 'A';
						}
						final int count = Util.countMatch(Pattern.compile(validationRegex).matcher(s));
						// System.out.println(">>>" + s + " = " + count + ", " + validationRegex);
						f = progressFeedback.get(Math.min(count, progressFeedback.size() - 1));
						f.setCustomMessage(f.getMessage().replaceAll("\\{COUNT_PATTERN\\}", (count + 1) + ""));
					}
				}
			}
		}
		final String msg = f == null ? I18n.get("msg.no_advice") : f.getCustomMessage();
		JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>" + msg + "</html>", I18n.get("title.advice"), f == null ? JOptionPane.PLAIN_MESSAGE : f.getType());
	}

	// This applies to only single-character indicators
	private Feedback getFeedbackSingleCharacter(final String[] segments, final Map<String, Feedback> map) {
		// Forgive the starter (meaning don't use i >= 0 in the for loop) because:
		// 1) no analysis has been run and 2) modifying the state before analysis is fine as long as the condition is kept the same later.
		for (int i = segments.length - 1; i > 0; i--) {
			String seg = segments[i];
			if ("".equals(seg)) {
				continue; // skip AA (A immediately followed by A)
			}
			if (i == segments.length - 1 && seg.endsWith("?")) { // skip the ask if it is the last event in the whole event string (not the current segment)
				seg = seg.substring(0, seg.length() - 1);
			}
			// reverse the order so that the latest can be processed first
			for (int x = seg.length() - 1; x >= 0; x--) {
				final String s = Character.toString(seg.charAt(x));
				for (final String regex : map.keySet()) {
					boolean find = Pattern.compile(regex).matcher(s).find();
					final Feedback f = map.get(regex);
					if (f.getNegate()) {
						find = !find;
					}
					if (find) {
						// from the start of the string to the first A has the index 0, so the analysis index will start from 1
						f.setCustomMessage(f.getMessage().replaceAll("\\{ANALYSIS_NUMBER\\}", i + ""));
						return f;
					}
				}
			}
			if (seg.lastIndexOf('?') != -1) { // go no further than the last ask
				break;
			}
		}
		return null;
	}

	private Feedback getFeedbackMultiCharacter(final String[] segments, final Map<String, Feedback> map) {
		// Forgive the starter (meaning don't use i >= 0 in the for loop) because:
		// 1) no analysis has been run and 2) modifying the state before analysis is fine as long as the condition is kept the same later.
		for (int i = segments.length - 1; i > 0; i--) {
			String seg = segments[i];
			if ("".equals(seg)) {
				continue; // skip AA (A immediately followed by A)
			}
			if (i == segments.length - 1 && seg.endsWith("?")) { // skip the ask if it is the last event in the whole event string (not the current segment)
				seg = seg.substring(0, seg.length() - 1);
			}
			// reverse the order so that the latest can be processed first (TODO: This somehow applies to only single-character indicators!)
			seg = new StringBuilder(seg).reverse().toString();
			for (final String regex : map.keySet()) {
				boolean find = Pattern.compile(regex).matcher(seg).find();
				final Feedback f = map.get(regex);
				if (f.getNegate()) {
					find = !find;
				}
				if (find) {
					// from the start of the string to the first A has the index 0, so the analysis index will start from 1
					f.setCustomMessage(f.getMessage().replaceAll("\\{ANALYSIS_NUMBER\\}", i + ""));
					return f;
				}
			}
			if (seg.lastIndexOf('?') != -1) { // go no further than the last ask
				break;
			}
		}
		return null;
	}

	MyEvent idChangeEvent() {
		final List<MyEvent> u = EventUtil.getEvents(ChangePartUValueCommand.class);
		if (u.size() < 2) {
			return null;
		}
		long oldId = -1;
		long newId = -1;
		for (final MyEvent x : u) {
			if (x instanceof ChangePartUValueCommand) {
				final ChangePartUValueCommand command = (ChangePartUValueCommand) x;
				newId = command.getPart().getId();
				if (oldId == -1) { // first
					oldId = newId;
				} else {
					if (newId != oldId) {
						return x;
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

}
