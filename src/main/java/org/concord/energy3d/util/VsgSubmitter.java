package org.concord.energy3d.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.net.InetAddress;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;

import org.concord.energy3d.MainApplication;
import org.concord.energy3d.gui.EnergyPanel;
import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.logger.SnapshotLogger;
import org.concord.energy3d.model.Foundation;
import org.concord.energy3d.model.GeoLocation;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.shapes.Heliodon;
import org.concord.energy3d.simulation.AnnualGraph;
import org.concord.energy3d.util.I18n;

/**
 * @author Charles Xie
 */
public class VsgSubmitter {

    public static void submit() {

        final GeoLocation geo = Scene.getInstance().getGeoLocation();
        if (geo == null) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_geolocation"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Calendar calendar = Heliodon.getInstance().getCalendar();
        if (calendar.get(Calendar.DAY_OF_MONTH) != 1) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>" + I18n.get("msg.annual_simulation_required") + "</html>", I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Scene.getInstance().getSolarResults() == null) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>" + I18n.get("msg.no_annual_simulation_result") + "</html>", I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        String s = "{\n";
        s += "\t\"lat\": " + EnergyPanel.FIVE_DECIMALS.format(geo.getLatitude()) + ",\n";
        s += "\t\"lng\": " + EnergyPanel.FIVE_DECIMALS.format(geo.getLongitude()) + ",\n";
        s += "\t\"address\": \"" + geo.getAddress() + "\",\n";
        switch (Scene.getInstance().getProjectType()) {
            case Foundation.TYPE_PV_PROJECT:
                s += "\t\"type\": \"PV\",\n";
                s += "\t\"module_number\": " + Scene.getInstance().countSolarPanels() + ",\n";
                break;
            case Foundation.TYPE_CSP_PROJECT:
                s += "\t\"type\": \"CSP\",\n";
                break;
        }
        if (Scene.getInstance().getDesigner() != null) {
            s += "\t\"author\": \"" + Scene.getInstance().getDesigner().getName() + "\",\n";
        }
        if (Scene.getInstance().getProjectName() != null) {
            s += "\t\"label\": \"" + Scene.getInstance().getProjectName() + "\",\n";
        }
        final double[][] solarResults = Scene.getInstance().getSolarResults();
        if (solarResults != null) {
            for (int i = 0; i < solarResults.length; i++) {
                s += "\t\"" + AnnualGraph.getThreeLetterMonth()[i] + "\": \"";
                for (int j = 0; j < solarResults[i].length; j++) {
                    s += EnergyPanel.FIVE_DECIMALS.format(solarResults[i][j]).replaceAll(",", "") + " ";
                }
                s = s.trim() + "\",\n";
            }
        }
        s += "\t\"url\": \"tbd\"\n";
        s += "}";

        File file;
        try {
            file = SnapshotLogger.getInstance().saveSnapshot("vsg_model");
        } catch (final Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.failed_snapshot"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            return;
        }

        final File currentFile = file;
        final String info = s;
        EventQueue.invokeLater(() -> {

            final JTextArea textArea = new JTextArea(info);
            textArea.setEditable(false);
            final JPanel panel = new JPanel(new BorderLayout(10, 10));
            final JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            panel.add(scrollPane, BorderLayout.NORTH);

            final JTextField nameField = new JTextField(Scene.getInstance().getDesigner() == null ? "User" : Scene.getInstance().getDesigner().getName());
            final JTextField emailField = new JTextField(Scene.getInstance().getDesigner() == null ? "" : Scene.getInstance().getDesigner().getEmail());
            final JTextField organizationField = new JTextField(Scene.getInstance().getDesigner() == null ? "" : Scene.getInstance().getDesigner().getOrganization());
            final JPanel personalInfoPanel = new JPanel(new SpringLayout());
            personalInfoPanel.setBorder(BorderFactory.createTitledBorder(I18n.get("label.contributor_info")));
            personalInfoPanel.add(new JLabel(I18n.get("label.name")));
            personalInfoPanel.add(nameField);
            personalInfoPanel.add(new JLabel(I18n.get("label.email")));
            personalInfoPanel.add(emailField);
            personalInfoPanel.add(new JLabel(I18n.get("label.organization")));
            personalInfoPanel.add(organizationField);
            SpringUtilities.makeCompactGrid(personalInfoPanel, 3, 2, 8, 8, 8, 8);
            panel.add(personalInfoPanel, BorderLayout.CENTER);

            String s1 = "<html><font size=2>";
            s1 += I18n.get("msg.vsg_terms");
            s1 += "</font><br><br>";
            s1 += "<b>" + I18n.get("msg.vsg_submit_question");
            s1 += "</b></html>";
            panel.add(new JLabel(s1), BorderLayout.SOUTH);

            if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), panel, I18n.get("title.virtual_solar_grid"), JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.YES_OPTION) {
                Scene.getInstance().getDesigner().setName(nameField.getText());
                Scene.getInstance().getDesigner().setEmail(emailField.getText());
                Scene.getInstance().getDesigner().setOrganization(organizationField.getText());
                new Uploader(nameField.getText(), emailField.getText(), organizationField.getText(), info, currentFile).execute();
            }
        });

    }

    private static class Uploader extends SwingWorker<String, Void> {

        private final String name, email, organization;
        private final String text;
        private final File currentFile;

        Uploader(final String name, final String email, final String organization, final String text, final File currentFile) {
            super();
            this.name = name;
            this.email = email;
            this.organization = organization;
            this.text = text;
            this.currentFile = currentFile;
        }

        @Override
        protected String doInBackground() throws Exception {
            return upload(name, email, organization, text, currentFile);
        }

        @Override
        protected void done() {
            try {
                JOptionPane.showMessageDialog(MainFrame.getInstance(), get(), I18n.get("title.notice"), JOptionPane.INFORMATION_MESSAGE);
            } catch (final Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(MainFrame.getInstance(), "<html>" + I18n.get("msg.failed_uploading_file") + "<hr>" + e.getMessage() + "</html>", I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String upload(final String name, final String email, final String organization, final String info, final File currentFile) throws Exception {
        final MultipartUtility multipart = new MultipartUtility("http://energy3d.concord.org/vsg/data.php", "UTF-8");
        multipart.addFormField("ip_address", InetAddress.getLocalHost().getHostAddress());
        multipart.addFormField("os_name", System.getProperty("os.name"));
        multipart.addFormField("user_name", name != null && !name.trim().equals("") ? name : System.getProperty("user.name"));
        multipart.addFormField("user_email", email);
        multipart.addFormField("user_organization", organization);
        multipart.addFormField("os_version", System.getProperty("os.version"));
        multipart.addFormField("energy3d_version", MainApplication.VERSION);
        multipart.addFormField("data", info);
        if (!Scene.isInternalFile() && currentFile != null) {
            multipart.addFilePart("model", currentFile);
        }
        return multipart.finish();
    }

}