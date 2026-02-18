package org.concord.energy3d.gui;

import org.concord.energy3d.Designer;
import org.concord.energy3d.MainApplication;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.util.I18n;
import org.concord.energy3d.util.SpringUtilities;

import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.prefs.Preferences;

/**
 * @author Charles Xie
 */
class PropertiesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    PropertiesDialog() {

        super(MainFrame.getInstance(), true);
        final Scene s = Scene.getInstance();
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setTitle(I18n.get("props.title", Integer.toString(s.getParts().size())));

        getContentPane().setLayout(new BorderLayout());
        final JPanel panel = new JPanel(new SpringLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        getContentPane().add(panel, BorderLayout.CENTER);

        final JComboBox<String> onlySolarAnalysisComboBox = new JComboBox<>(new String[]{I18n.get("props.no"), I18n.get("props.yes")});
        final JTextField designerNameField = new JTextField(s.getDesigner() == null ? I18n.get("props.user") : s.getDesigner().getName());
        final JTextField designerEmailField = new JTextField(s.getDesigner() == null ? "" : s.getDesigner().getEmail());
        final JTextField designerOrganizationField = new JTextField(s.getDesigner() == null ? "" : s.getDesigner().getOrganization());
        final JComboBox<String> projectTypeComboBox = new JComboBox<>(new String[]{I18n.get("props.building"), I18n.get("panel.pv_system"), I18n.get("panel.csp_system")});
        projectTypeComboBox.setSelectedIndex(s.getProjectType() - 1);
        projectTypeComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                if (projectTypeComboBox.getSelectedIndex() == 0) {
                    onlySolarAnalysisComboBox.setSelectedIndex(0);
                } else {
                    onlySolarAnalysisComboBox.setSelectedIndex(1);
                }
            }
        });
        final JComboBox<String> unitSystemComboBox = new JComboBox<>(new String[]{I18n.get("props.unit_si"), I18n.get("props.unit_us")});
        if (s.getUnit() == Scene.Unit.USCustomaryUnits) {
            unitSystemComboBox.setSelectedIndex(1);
        }
        final JComboBox<String> studentModeComboBox = new JComboBox<>(new String[]{I18n.get("props.no"), I18n.get("props.yes")});
        if (s.isStudentMode()) {
            studentModeComboBox.setSelectedIndex(1);
        }
        final JTextField projectNameField = new JTextField(s.getProjectName());
        final JTextField projectDescriptionField = new JTextField(s.getProjectDescription());
        projectDescriptionField.setColumns(10);
        final JComboBox<String> foundationOverlapComboBox = new JComboBox<>(new String[]{I18n.get("props.disallowed"), I18n.get("props.allowed")});
        if (!s.getDisallowFoundationOverlap()) {
            foundationOverlapComboBox.setSelectedIndex(1);
        }
        if (s.getOnlySolarAnalysis()) {
            onlySolarAnalysisComboBox.setSelectedIndex(1);
        }
        final JComboBox<String> snapshotLoggingComboBox = new JComboBox<>(new String[]{I18n.get("props.yes"), I18n.get("props.no")});
        if (s.getNoSnaphshotLogging()) {
            snapshotLoggingComboBox.setSelectedIndex(1);
        }
        final JComboBox<String> groundImageColorationComboBox = new JComboBox<>(new String[]{I18n.get("props.dark_colored"), I18n.get("props.light_colored")});
        groundImageColorationComboBox.setSelectedIndex(s.isGroundImageLightColored() ? 1 : 0);
        final JComboBox<String> instructionTabHeaderComboBox = new JComboBox<>(new String[]{I18n.get("props.show"), I18n.get("props.hide")});
        instructionTabHeaderComboBox.setSelectedIndex(s.isInstructionTabHeaderVisible() ? 0 : 1);
        final JComboBox<String> dateFixedComboBox = new JComboBox<>(new String[]{I18n.get("props.no"), I18n.get("props.yes")});
        dateFixedComboBox.setSelectedIndex(s.isDateFixed() ? 1 : 0);
        final JComboBox<String> locationFixedComboBox = new JComboBox<>(new String[]{I18n.get("props.no"), I18n.get("props.yes")});
        locationFixedComboBox.setSelectedIndex(s.isLocationFixed() ? 1 : 0);

        // Language (UI) - list built by scanning app/locales/*.json (menu dynamique).
        final String[] localeCodes = I18n.getAvailableLocaleCodes();
        final String[] localeNames = new String[localeCodes.length];
        for (int i = 0; i < localeCodes.length; i++) {
            localeNames[i] = I18n.getDisplayNameForLocaleCode(localeCodes[i]);
        }
        final JComboBox<String> languageComboBox = new JComboBox<>(localeNames);
        final String currentLang = I18n.getLocale().getLanguage();
        for (int i = 0; i < localeCodes.length; i++) {
            if (localeCodes[i].equals(currentLang)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
        }
        final Preferences prefs = Preferences.userNodeForPackage(MainApplication.class);

        final ActionListener okListener = e -> {
            switch (unitSystemComboBox.getSelectedIndex()) {
                case 0:
                    s.setUnit(Scene.Unit.InternationalSystemOfUnits);
                    break;
                case 1:
                    s.setUnit(Scene.Unit.USCustomaryUnits);
                    break;
            }
            final String designerName = designerNameField.getText();
            final String designerEmail = designerEmailField.getText();
            final String designerOrganization = designerOrganizationField.getText();
            if (designerName != null && !designerName.trim().equals("")) {
                Designer designer = s.getDesigner();
                if (designer == null) {
                    designer = new Designer();
                    s.setDesigner(designer);
                }
                designer.setName(designerName);
            } else {
                Designer designer = s.getDesigner();
                if (designer == null) {
                    designer = new Designer();
                    s.setDesigner(designer);
                }
                designer.setName(I18n.get("props.user"));
            }
            if (designerEmail != null && !designerEmail.trim().equals("")) {
                Designer designer = s.getDesigner();
                if (designer == null) {
                    designer = new Designer();
                    s.setDesigner(designer);
                }
                designer.setEmail(designerEmail);
            } else {
                Designer designer = s.getDesigner();
                if (designer == null) {
                    designer = new Designer();
                    s.setDesigner(designer);
                }
                designer.setEmail(null);
            }
            if (designerOrganization != null && !designerOrganization.trim().equals("")) {
                Designer designer = s.getDesigner();
                if (designer == null) {
                    designer = new Designer();
                    s.setDesigner(designer);
                }
                designer.setOrganization(designerOrganization);
            } else {
                Designer designer = s.getDesigner();
                if (designer == null) {
                    designer = new Designer();
                    s.setDesigner(designer);
                }
                designer.setOrganization(null);
            }
            int oldProjectType = s.getProjectType();
            s.setProjectType(projectTypeComboBox.getSelectedIndex() + 1);
            if (s.getProjectType() != oldProjectType) { // if project type changes, adjust the cell size for others
                if (s.getProjectType() == Foundation.TYPE_PV_PROJECT || s.getProjectType() == Foundation.TYPE_CSP_PROJECT) {
                    if (s.getSolarStep() * s.getScale() < 1) {
                        s.setSolarStep(10 / s.getScale());
                    }
                } else {
                    if (s.getSolarStep() * s.getScale() > 2) {
                        s.setSolarStep(0.4 / s.getScale());
                    }
                }
            }
            s.setProjectName(projectNameField.getText());
            s.setProjectDescription(projectDescriptionField.getText());
            s.setStudentMode(studentModeComboBox.getSelectedIndex() == 1);
            s.setDisallowFoundationOverlap(foundationOverlapComboBox.getSelectedIndex() == 0);
            s.setOnlySolarAnalysis(onlySolarAnalysisComboBox.getSelectedIndex() == 1);
            s.setNoSnapshotLogging(snapshotLoggingComboBox.getSelectedIndex() == 1);
            s.setGroundImageLightColored(groundImageColorationComboBox.getSelectedIndex() == 1);
            s.setInstructionTabHeaderVisible(instructionTabHeaderComboBox.getSelectedIndex() == 0);
            s.setDateFixed(dateFixedComboBox.getSelectedIndex() == 1);
            s.setLocationFixed(locationFixedComboBox.getSelectedIndex() == 1);
            final String selectedLocale = localeCodes[languageComboBox.getSelectedIndex()];
            prefs.put("locale", selectedLocale);
            I18n.setLocale(new Locale(selectedLocale));
            MainFrame.getInstance().refreshMenuLabelsAfterLocaleChange();
            s.setEdited(true);
            EnergyPanel.getInstance().updateWeatherData();
            EnergyPanel.getInstance().update();
            PropertiesDialog.this.dispose();
        };

        // set designer name
        panel.add(new JLabel(I18n.get("props.designer_name")));
        panel.add(designerNameField);

        // set designer email
        panel.add(new JLabel(I18n.get("props.designer_email")));
        panel.add(designerEmailField);

        // set designer organization
        panel.add(new JLabel(I18n.get("props.designer_organization")));
        panel.add(designerOrganizationField);

        // set project type
        panel.add(new JLabel(I18n.get("props.project_type")));
        panel.add(projectTypeComboBox);

        // set project name
        panel.add(new JLabel(I18n.get("props.project_name")));
        panel.add(projectNameField);

        // set project description
        panel.add(new JLabel(I18n.get("props.project_description")));
        panel.add(projectDescriptionField);

        // set student mode
        panel.add(new JLabel(I18n.get("props.student_mode")));
        panel.add(studentModeComboBox);

        // choose unit system
        panel.add(new JLabel(I18n.get("props.unit_system")));
        panel.add(unitSystemComboBox);

        // allow building overlap
        panel.add(new JLabel(I18n.get("props.foundation_overlap")));
        panel.add(foundationOverlapComboBox);

        // restrict to only solar analysis
        panel.add(new JLabel(I18n.get("props.only_solar_analysis")));
        panel.add(onlySolarAnalysisComboBox);

        // enable or disable snapshot logging for UX
        panel.add(new JLabel(I18n.get("props.snapshot_logging")));
        panel.add(snapshotLoggingComboBox);

        // ground image color
        panel.add(new JLabel(I18n.get("props.ground_image_coloration")));
        panel.add(groundImageColorationComboBox);

        // instruction tab
        panel.add(new JLabel(I18n.get("props.instruction_tab_header")));
        panel.add(instructionTabHeaderComboBox);

        // fixed date
        panel.add(new JLabel(I18n.get("props.fixed_date")));
        panel.add(dateFixedComboBox);

        // fixed location
        panel.add(new JLabel(I18n.get("props.fixed_location")));
        panel.add(locationFixedComboBox);

        // UI language
        panel.add(new JLabel(I18n.get("prefs.language") + ": "));
        panel.add(languageComboBox);

        SpringUtilities.makeCompactGrid(panel, 16, 2, 8, 8, 8, 8);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        final JButton okButton = new JButton(I18n.get("dialog.ok"));
        okButton.addActionListener(okListener);
        okButton.setActionCommand("OK");
        buttonPanel.add(okButton);
        getRootPane().setDefaultButton(okButton);

        final JButton cancelButton = new JButton(I18n.get("dialog.cancel"));
        cancelButton.addActionListener(e -> PropertiesDialog.this.dispose());
        cancelButton.setActionCommand("Cancel");
        buttonPanel.add(cancelButton);

        pack();
        setLocationRelativeTo(MainFrame.getInstance());
        projectNameField.requestFocusInWindow();
        projectNameField.selectAll();

    }

}