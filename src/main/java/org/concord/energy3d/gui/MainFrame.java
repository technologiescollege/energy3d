package org.concord.energy3d.gui;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.MathUtils;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import org.concord.energy3d.MainApplication;
import org.concord.energy3d.agents.*;
import org.concord.energy3d.logger.DesignReplay;
import org.concord.energy3d.logger.PlayControl;
import org.concord.energy3d.logger.PostProcessor;
import org.concord.energy3d.logger.SnapshotLogger;
import org.concord.energy3d.model.Window;
import org.concord.energy3d.model.*;
import org.concord.energy3d.scene.PrintController;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.scene.SceneManager;
import org.concord.energy3d.scene.SceneManager.CameraMode;
import org.concord.energy3d.scene.SceneManager.ViewMode;
import org.concord.energy3d.shapes.Heliodon;
import org.concord.energy3d.simulation.*;
import org.concord.energy3d.undo.*;
import org.concord.energy3d.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.prefs.Preferences;

public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final MainFrame instance = new MainFrame();
    private final List<JComponent> recentFileMenuItems = new ArrayList<>();
    private final JColorChooser colorChooser;
    private int fileMenuItemCount;

    private JMenuBar appMenuBar;
    private JMenu fileMenu;
    private JMenuItem newMenuItem;
    private JMenuItem openMenuItem;
    private JMenuItem recoveryMenuItem;
    private JMenuItem listLoggedSnapshotsMenuItem;
    private JMenuItem clearLogMenuItem;
    private JMenuItem replayFolderMenuItem;
    private JMenuItem replayLastFolderMenuItem;
    private JMenu replayControlsMenu;
    private JMenuItem forwardReplayMenuItem;
    private JMenuItem backwardReplayMenuItem;
    private JMenuItem endReplayMenuItem;
    private JMenuItem pauseReplayMenuItem;
    private JMenuItem analyzeFolderMenuItem;
    private JMenuItem saveMenuItem;
    private JMenuItem preferencesMenuItem;
    private JMenuItem printMenuItem;
    private JCheckBoxMenuItem previewMenuItem;
    private JRadioButtonMenuItem orbitMenuItem;
    private JRadioButtonMenuItem firstPersonMenuItem;
    private JMenuItem resetCameraMenuItem;
    private JMenuItem saveasMenuItem;
    private JMenuItem submitToVsgMenuItem;
    private JMenu viewMenu;
    private JMenu analysisMenu;
    private JMenuItem sortIdMenuItem;
    private JMenuItem overallUtilityBillMenuItem;
    private JMenuItem simulationSettingsMenuItem;
    private JMenuItem visualizationSettingsMenuItem;
    private JMenuItem annualEnergyAnalysisMenuItem;
    private JMenuItem annualEnergyAnalysisForSelectionMenuItem;
    private JMenuItem dailyEnergyAnalysisMenuItem;
    private JMenuItem dailyEnergyAnalysisForSelectionMenuItem;
    private JMenuItem groupDailyAnalysisMenuItem;
    private JMenuItem groupAnnualAnalysisMenuItem;
    private JMenuItem annualPvAnalysisMenuItem;
    private JMenuItem dailyPvAnalysisMenuItem;
    private JMenuItem annualHeliostatAnalysisMenuItem;
    private JMenuItem dailyHeliostatAnalysisMenuItem;
    private JMenuItem annualParabolicTroughAnalysisMenuItem;
    private JMenuItem dailyParabolicTroughAnalysisMenuItem;
    private JMenuItem annualParabolicDishAnalysisMenuItem;
    private JMenuItem dailyParabolicDishAnalysisMenuItem;
    private JMenuItem annualFresnelReflectorAnalysisMenuItem;
    private JMenuItem dailyFresnelReflectorAnalysisMenuItem;
    private JMenuItem annualSensorMenuItem;
    private JMenuItem dailySensorMenuItem;
    private JMenuItem costBreakdownMenuItem;
    private JMenuItem monthlySunshineHoursMenuItem;
    private JMenuItem annualEnvironmentalTemperatureMenuItem;
    private JMenuItem dailyEnvironmentalTemperatureMenuItem;
    private JCheckBoxMenuItem solarRadiationHeatMapMenuItem;
    private JCheckBoxMenuItem solarAbsorptionHeatMapMenuItem;
    private JCheckBoxMenuItem onlyReflectionHeatMapMenuItem;
    private JCheckBoxMenuItem showSolarLandMenuItem;
    private JCheckBoxMenuItem onlySolarComponentsInSolarMapMenuItem;
    private JCheckBoxMenuItem showHeatFluxVectorsMenuItem;
    private JCheckBoxMenuItem axesMenuItem;
    private JMenu sunAnglesMenu;
    private JCheckBoxMenuItem zenithAngleMenuItem;
    private JCheckBoxMenuItem elevationAngleMenuItem;
    private JCheckBoxMenuItem azimuthAngleMenuItem;
    private JCheckBoxMenuItem lightBeamsMenuItem;
    private JCheckBoxMenuItem shadowMenuItem;
    private JCheckBoxMenuItem roofDashedLineMenuItem;
    private JCheckBoxMenuItem disableShadowInActionMenuItem;
    private JMenuItem exitMenuItem;
    private JMenu helpMenu;
    private JMenuItem aboutMenuItem;
    private JDialog aboutDialog;
    private JCheckBoxMenuItem annotationsMenuItem;
    private JCheckBoxMenuItem annotationsInwardMenuItem;
    private JMenu editMenu;
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;
    private JMenuItem cutMenuItem;
    private JMenuItem copyMenuItem;
    private JMenuItem pasteMenuItem;
    private JMenuItem pageSetupMenuItem;
    private JRadioButtonMenuItem scaleToFitRadioButtonMenuItem;
    private JRadioButtonMenuItem exactSizeRadioButtonMenuItem;
    private final ButtonGroup printSizeOptionBbuttonGroup = new ButtonGroup();
    private JMenuItem importMenuItem, importColladaMenuItem, exportModelMenuItem;
    private JCheckBoxMenuItem snapMenuItem;
    private JCheckBoxMenuItem snapToGridsMenuItem;
    private JCheckBoxMenuItem topViewCheckBoxMenuItem;
    private JMenuItem zoomInMenuItem;
    private JMenuItem zoomOutMenuItem;
    private JMenu environmentMenu;
    private JRadioButtonMenuItem defaultMenuItem;
    private JRadioButtonMenuItem desertMenuItem;
    private JRadioButtonMenuItem grasslandMenuItem;
    private JRadioButtonMenuItem forestMenuItem;
    private final ButtonGroup themeButtonGroup = new ButtonGroup();
    private JMenuItem exportImageMenuItem;
    private JMenuItem copyImageMenuItem;
    private JMenuItem exportLogMenuItem;
    private JMenuItem enableAllBaseEditPointsMenuItem;
    private JMenuItem disableAllBaseEditPointsMenuItem;
    private JMenuItem enableAllTreeEditPointsMenuItem;
    private JMenuItem disableAllTreeEditPointsMenuItem;
    private JMenuItem specificationsMenuItem;
    private JMenuItem propertiesMenuItem;
    private JMenuItem financialSettingsMenuItem;
    private JMenuItem setRegionMenuItem;
    private JCheckBoxMenuItem noteCheckBoxMenuItem;
    private JCheckBoxMenuItem infoPanelCheckBoxMenuItem;
    private JMenu examplesMenu;
    private JMenu tutorialsMenu;
    private JCheckBoxMenuItem autoRecomputeEnergyMenuItem;
    private JMenuItem removeAllFoundationsMenuItem;
    private JMenuItem removeAllRoofsMenuItem;
    private JMenuItem removeAllFloorsMenuItem;
    private JMenuItem removeAllSolarPanelsMenuItem;
    private JMenuItem removeAllRacksMenuItem;
    private JMenuItem removeAllHeliostatsMenuItem;
    private JMenuItem removeAllParabolicTroughsMenuItem;
    private JMenuItem removeAllParabolicDishesMenuItem;
    private JMenuItem removeAllFresnelReflectorsMenuItem;
    private JMenuItem removeAllSensorsMenuItem;
    private JMenuItem removeAllWallsMenuItem;
    private JMenuItem removeAllWindowsMenuItem;
    private JMenuItem removeAllWindowShuttersMenuItem;
    private JMenuItem removeAllTreesMenuItem;
    private JMenuItem removeAllHumansMenuItem;
    private JMenuItem removeAllEditLocksMenuItem;
    private JMenuItem removeAllUtilityBillsMenuItem;
    private JMenuItem fixProblemsMenuItem;
    private JMenuItem moveEastMenuItem;
    private JMenuItem moveWestMenuItem;
    private JMenuItem moveNorthMenuItem;
    private JMenuItem moveSouthMenuItem;
    private JMenuItem rotate180MenuItem;
    private JMenuItem rotate90CwMenuItem;
    private JMenuItem rotate90CcwMenuItem;
    private JMenu groundImageMenu;
    private JMenuItem useImageFileMenuItem;
    private JMenuItem useEarthViewMenuItem;
    private JMenuItem rescaleGroundImageMenuItem;
    private JMenuItem clearGroundImageMenuItem;
    private JCheckBoxMenuItem showGroundImageMenuItem;

    private final static FilenameFilter ng3NameFilter = (dir, name) -> name.endsWith(".ng3");

    public static MainFrame getInstance() {
        return instance;
    }

    private MainFrame() {
        super();
        System.out.print("Initiating GUI...");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icons/icon.png")));
        colorChooser = new JColorChooser();
        initialize();
        setMinimumSize(new Dimension(800, 600));
        System.out.println("done");
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            final ImageIcon icon_cw = new ImageIcon(MainPanel.class.getResource("icons/rotate_cw.png"));
            final ImageIcon icon_ccw = new ImageIcon(MainPanel.class.getResource("icons/rotate_ccw.png"));

            @Override
            public boolean dispatchKeyEvent(final KeyEvent e) {
                double a = MainPanel.getInstance().getRotationAngleAbsolute();
                if (e.isShiftDown()) {
                    a *= 0.2;
                    SceneManager.getInstance().setFineGrid(true);
                } else {
                    SceneManager.getInstance().setFineGrid(false);
                }
                switch (e.getID()) {
                    case KeyEvent.KEY_PRESSED:
                    case KeyEvent.KEY_RELEASED:
                        MainPanel.getInstance().getRotateButton().setIcon(e.isControlDown() ? icon_ccw : icon_cw);
                        MainPanel.getInstance().setRotationAngle(e.isControlDown() ? a : -a);
                        break;
                }
                return false;
            }
        });
    }

    private void initialize() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(I18n.get("app.title", MainApplication.VERSION));

        setJMenuBar(getAppMenuBar());
        setContentPane(MainPanel.getInstance());

        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
        setSize(Math.min(pref.getInt("window_size_width", Math.max(900, MainPanel.getInstance().getAppToolbar().getPreferredSize().width)), screenSize.width),
                Math.min(pref.getInt("window_size_height", 600), screenSize.height));
        setLocation(pref.getInt("window_location_x", (int) (screenSize.getWidth() - getSize().getWidth()) / 2),
                pref.getInt("window_location_y", (int) (screenSize.getHeight() - getSize().getHeight()) / 2));
        setLocation(MathUtils.clamp(getLocation().x, 0, screenSize.width - getSize().width), MathUtils.clamp(getLocation().y, 0, screenSize.height - getSize().height));
        final int windowState = pref.getInt("window_state", JFrame.NORMAL);
        if ((windowState & JFrame.ICONIFIED) == 0) {
            setExtendedState(windowState);
        }

        if (Config.isMac()) {
            Mac.init();
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(final ComponentEvent e) {
                if (MainFrame.this.getExtendedState() == 0) {
                    pref.putInt("window_location_x", e.getComponent().getLocation().x);
                    pref.putInt("window_location_y", e.getComponent().getLocation().y);
                }
            }

            @Override
            public void componentResized(final ComponentEvent e) {
                if (MainFrame.this.getExtendedState() == 0) {
                    pref.putInt("window_size_width", e.getComponent().getSize().width);
                    pref.putInt("window_size_height", e.getComponent().getSize().height);
                }
            }
        });

        addWindowStateListener(e -> {
            pref.putInt("window_state", e.getNewState());
            SceneManager.getInstance().refresh();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                exit();
            }

            @Override
            public void windowDeiconified(final WindowEvent e) {
                SceneManager.getInstance().refresh();
            }

            @Override
            public void windowActivated(final WindowEvent e) {
            }
        });
    }

    private JMenuBar getAppMenuBar() {
        if (appMenuBar == null) {
            appMenuBar = new JMenuBar();
            appMenuBar.add(getFileMenu());
            appMenuBar.add(getEditMenu());
            appMenuBar.add(getViewMenu());
            appMenuBar.add(getAnalysisMenu());
            appMenuBar.add(getTemplatesMenu());
            appMenuBar.add(getTutorialsMenu());
            appMenuBar.add(getHelpMenu());

            addCommonActionListeners(appMenuBar);
        }
        return appMenuBar;
    }

    private void addCommonActionListeners(final JMenuBar menuBar) {
        for (final Component c : menuBar.getComponents()) {
            if (c instanceof JMenu) {
                addCommonActionListeners((JMenu) c);
            }
        }
    }

    private void addCommonActionListeners(final JMenu menu) {
        for (final Component c : menu.getMenuComponents()) {
            if (c instanceof JMenuItem) {
                final JMenuItem menuItem = (JMenuItem) c;
                menuItem.addActionListener(e -> MainPanel.getInstance().defaultTool());
            }
        }
    }

    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.addMenuListener(new MenuListener() {

                private void enableMenuItems(final boolean b) {
                    replayFolderMenuItem.setEnabled(b);
                    replayLastFolderMenuItem.setEnabled(b);
                    replayControlsMenu.setEnabled(b);
                    analyzeFolderMenuItem.setEnabled(b);
                    saveMenuItem.setEnabled(b);
                }

                @Override
                public void menuCanceled(final MenuEvent e) {
                    enableMenuItems(true); // if any of these actions is registered with a keystroke, we must re-enable it
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                    enableMenuItems(true);
                }

                @Override
                public void menuSelected(final MenuEvent e) {

                    MainPanel.getInstance().defaultTool();

                    enableMenuItems(true);
                    saveMenuItem.setEnabled(!Scene.isInternalFile()); // cannot overwrite a template

                    // prevent multiple replay or postprocessing commands
                    final boolean inactive = !PlayControl.active;
                    replayFolderMenuItem.setEnabled(inactive);
                    final File lastFolder = DesignReplay.getInstance().getLastFolder();
                    replayLastFolderMenuItem.setEnabled(lastFolder != null && inactive);
                    replayLastFolderMenuItem.setText(lastFolder != null ? I18n.get("menu.replay_last_folder") + ": " + lastFolder : I18n.get("menu.replay_last_folder"));
                    replayControlsMenu.setEnabled(!inactive);
                    analyzeFolderMenuItem.setEnabled(inactive);

                    // recent files
                    if (!recentFileMenuItems.isEmpty()) {
                        for (final JComponent x : recentFileMenuItems) {
                            fileMenu.remove(x);
                        }
                    }
                    final String[] recentFiles = FileChooser.getInstance().getRecentFiles();
                    if (recentFiles != null) {
                        final int n = recentFiles.length;
                        if (n > 0) {
                            for (int i = 0; i < n; i++) {
                                final JMenuItem x = new JMenuItem((i + 1) + "  " + Util.getFileName(recentFiles[i]));
                                x.setToolTipText(recentFiles[i]);
                                final File rf = new File(recentFiles[i]);
                                x.addActionListener(e1 -> {
                                    boolean ok = false;
                                    if (Scene.getInstance().isEdited()) {
                                        final int save = JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.save_changes"),
                                                I18n.get("menu.save"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                                        if (save == JOptionPane.YES_OPTION) {
                                            save();
                                            if (!Scene.getInstance().isEdited()) {
                                                ok = true;
                                            }
                                        } else if (save != JOptionPane.CANCEL_OPTION) {
                                            ok = true;
                                        }
                                    } else {
                                        ok = true;
                                    }
                                    if (ok) {
                                        SceneManager.getTaskManager().update(() -> {
                                            try {
                                                Scene.open(rf.toURI().toURL());
                                                EventQueue.invokeLater(() -> {
                                                    updateTitleBar();
                                                    FileChooser.getInstance().rememberFile(rf.getPath());
                                                });
                                            } catch (final Throwable err) {
                                                BugReporter.report(err, rf.getAbsolutePath());
                                            }
                                            return null;
                                        });
                                    }
                                });
                                fileMenu.insert(x, fileMenuItemCount + i);
                                recentFileMenuItems.add(x);
                            }
                            final JSeparator s = new JSeparator();
                            fileMenu.add(s, fileMenuItemCount + n);
                            recentFileMenuItems.add(s);
                        }
                    }

                }
            });
            fileMenu.setText(I18n.get("menu.file"));
            addItemToFileMenu(getNewMenuItem());
            addItemToFileMenu(getOpenMenuItem());
            addItemToFileMenu(getSaveMenuItem());
            addItemToFileMenu(getSaveasMenuItem());
            addItemToFileMenu(getSubmitToVsgMenuItem());
            addItemToFileMenu(new JSeparator());
            addItemToFileMenu(getImportMenuItem());
            addItemToFileMenu(getImportColladaMenuItem());
            addItemToFileMenu(getCopyImageMenuItem());
            addItemToFileMenu(getExportModelMenuItem());
            addItemToFileMenu(getExportImageMenuItem());
            addItemToFileMenu(getExportLogMenuItem());
            addItemToFileMenu(new JSeparator());

            addItemToFileMenu(getReplayFolderMenuItem());
            addItemToFileMenu(getReplayLastFolderMenuItem());

            replayControlsMenu = new JMenu(I18n.get("menu.replay_controls"));
            replayControlsMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                    // if any of these actions is registered with a keystroke, we must re-enable it
                    endReplayMenuItem.setEnabled(true);
                    pauseReplayMenuItem.setEnabled(true);
                    forwardReplayMenuItem.setEnabled(true);
                    backwardReplayMenuItem.setEnabled(true);
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    endReplayMenuItem.setEnabled(true);
                    pauseReplayMenuItem.setEnabled(true);
                    forwardReplayMenuItem.setEnabled(true);
                    backwardReplayMenuItem.setEnabled(true);
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    endReplayMenuItem.setEnabled(PlayControl.active);
                    pauseReplayMenuItem.setEnabled(PlayControl.active);
                    pauseReplayMenuItem.setText(PlayControl.replaying ? I18n.get("menu.resume_replay") : I18n.get("menu.pause_replay"));
                    forwardReplayMenuItem.setEnabled(!PlayControl.replaying);
                    backwardReplayMenuItem.setEnabled(!PlayControl.replaying);
                }
            });
            addItemToFileMenu(replayControlsMenu);
            replayControlsMenu.add(getPauseReplayMenuItem());
            replayControlsMenu.add(getBackwardReplayMenuItem());
            replayControlsMenu.add(getForwardReplayMenuItem());
            replayControlsMenu.add(getEndReplayMenuItem());

            addItemToFileMenu(getAnalyzeFolderMenuItem());
            addItemToFileMenu(new JSeparator());

            addItemToFileMenu(getScaleToFitRadioButtonMenuItem());
            addItemToFileMenu(getExactSizeRadioButtonMenuItem());
            addItemToFileMenu(getPageSetupMenuItem());
            addItemToFileMenu(getPreviewMenuItem());
            addItemToFileMenu(getPrintMenuItem());
            addItemToFileMenu(new JSeparator());
            fileMenu.add(getExitMenuItem());
        }
        return fileMenu;
    }

    private void addItemToFileMenu(final JComponent c) {
        fileMenu.add(c);
        fileMenuItemCount++;
    }

    private JMenuItem getNewMenuItem() {
        if (newMenuItem == null) {
            newMenuItem = new JMenuItem(I18n.get("menu.new"));
            newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            newMenuItem.addActionListener(e -> {
                boolean ok = false;
                if (Scene.getInstance().isEdited()) {
                    final int save = JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.save_changes"), I18n.get("dialog.save"),
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (save == JOptionPane.YES_OPTION) {
                        save();
                        if (!Scene.getInstance().isEdited()) {
                            ok = true;
                        }
                    } else if (save != JOptionPane.CANCEL_OPTION) {
                        ok = true;
                    }
                } else {
                    ok = true;
                }
                if (ok) {
                    SceneManager.getTaskManager().update(() -> {
                        try {
                            Scene.newFile(true);
                            SceneManager.getInstance().resetCamera(ViewMode.NORMAL);
                            SceneManager.getInstance().getCameraControl().reset();
                            EventQueue.invokeLater(() -> {
                                updateTitleBar();
                                EnergyPanel.getInstance().update();
                                EnergyPanel.getInstance().clearAllGraphs();
                                EnergyPanel.getInstance().selectInstructionSheet(0);
                                MainApplication.addEvent(new OperationEvent(Scene.getURL(), System.currentTimeMillis(), I18n.get("menu.new"), null));
                            });
                        } catch (final Throwable err) {
                            BugReporter.report(err);
                        }
                        return null;
                    });
                }
            });
        }
        return newMenuItem;
    }

    private JMenuItem getOpenMenuItem() {
        if (openMenuItem == null) {
            openMenuItem = new JMenuItem(I18n.get("menu.open") + "...");
            openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            openMenuItem.addActionListener(e -> {
                if (Scene.getInstance().isEdited()) {
                    final int save = JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.save_changes"),
                            I18n.get("dialog.save"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (save == JOptionPane.YES_OPTION) {
                        save();
                        if (!Scene.getInstance().isEdited()) {
                            open();
                        }
                    } else if (save != JOptionPane.CANCEL_OPTION) {
                        open();
                    }
                } else {
                    open();
                }
            });
        }
        return openMenuItem;
    }

    public void open() {
        SceneManager.getInstance().refresh(1);
        final File file = FileChooser.getInstance().showDialog(".ng3", FileChooser.ng3Filter, false);
        if (file == null) {
            return;
        }
        SceneManager.getTaskManager().update(() -> {
            try {
                Scene.open(file.toURI().toURL());
                EventQueue.invokeLater(() -> FileChooser.getInstance().rememberFile(file.getPath()));
            } catch (final Throwable err) {
                BugReporter.report(err, file.getAbsolutePath());
            }
            return null;
        });
        topViewCheckBoxMenuItem.setSelected(false);
    }

    void reopen() {
        if (Scene.getInstance().isEdited()) {
            final int save = JOptionPane.showConfirmDialog(this, I18n.get("msg.save_changes"), I18n.get("menu.save_as"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (save == JOptionPane.YES_OPTION) {
                saveFile(false);
                if (!Scene.getInstance().isEdited()) {
                    reload();
                }
            } else if (save != JOptionPane.CANCEL_OPTION) {
                reload();
            }
        } else {
            reload();
        }
    }

    private void reload() {
        final URL url = Scene.getURL();
        if (url != null) {
            SceneManager.getTaskManager().update(() -> {
                try {
                    Scene.open(url);
                } catch (final Exception e) {
                    BugReporter.report(e, I18n.get("msg.error_reopening", url));
                }
                return null;
            });
        }
    }

    private JMenuItem getRecoveryMenuItem() {
        if (recoveryMenuItem == null) {
            recoveryMenuItem = new JMenuItem(I18n.get("menu.recover_snapshot"));
            recoveryMenuItem.addActionListener(e -> {
                if (Scene.getInstance().getNoSnaphshotLogging()) {
                    JOptionPane.showMessageDialog(instance, "<html>" + I18n.get("msg.recovery_snapshot_disabled") + "</html>", I18n.get("dialog.file_recovery"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    final File f = SnapshotLogger.getInstance().getLatestSnapshot();
                    if (f != null) {
                        SceneManager.getTaskManager().update(() -> {
                            try {
                                Scene.open(f.toURI().toURL());
                                EventQueue.invokeLater(() -> {
                                    updateTitleBar();
                                    JOptionPane.showMessageDialog(instance, "<html>" + I18n.get("msg.recovery_overwrite") + "</html>", I18n.get("dialog.file_recovery"), JOptionPane.INFORMATION_MESSAGE);
                                    saveasMenuItem.doClick();
                                });
                            } catch (final Throwable err) {
                                BugReporter.report(err, I18n.get("msg.recovery_error"));
                            }
                            return null;
                        });
                    }
                }
            });
        }
        return recoveryMenuItem;
    }

    private JMenuItem getListLoggedSnapshotsMenuItem() {
        if (listLoggedSnapshotsMenuItem == null) {
            listLoggedSnapshotsMenuItem = new JMenuItem(I18n.get("menu.list_snapshots"));
            listLoggedSnapshotsMenuItem.addActionListener(e -> {
                if (Scene.getInstance().getNoSnaphshotLogging()) {
                    JOptionPane.showMessageDialog(instance, "<html>" + I18n.get("msg.snapshot_logging_disabled") + "</html>", I18n.get("dialog.file_recovery"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    final FileChooser fileChooser = new FileChooser();
                    fileChooser.setCurrentDirectory(SnapshotLogger.getLogFolder());
                    final File file = fileChooser.showDialog(".ng3", FileChooser.ng3Filter, false);
                    if (file == null) {
                        return;
                    }
                    SceneManager.getInstance().refresh(1);
                    SceneManager.getTaskManager().update(() -> {
                        try {
                            Scene.open(file.toURI().toURL());
                        } catch (final Throwable err) {
                            BugReporter.report(err, file.getAbsolutePath());
                        }
                        return null;
                    });
                    topViewCheckBoxMenuItem.setSelected(false);
                }
            });
        }
        return listLoggedSnapshotsMenuItem;
    }

    private JMenuItem getClearLogMenuItem() {
        if (clearLogMenuItem == null) {
            clearLogMenuItem = new JMenuItem(I18n.get("menu.clear_log"));
            clearLogMenuItem.addActionListener(e -> {
                final File logFolder = SnapshotLogger.getLogFolder();
                if (!logFolder.exists()) {
                    JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.nothing_logged"), I18n.get("dialog.no_log"), JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (JOptionPane.CANCEL_OPTION == JOptionPane.showConfirmDialog(MainFrame.this,
                        "<html>" + I18n.get("msg.clear_log_confirm") + "</html>",
                        I18n.get("dialog.clear_log"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                    return;
                }
                final File[] files = logFolder.listFiles();
                if (files != null) {
                    new SwingWorker<Object, Void>() {
                        @Override
                        protected Object doInBackground() {
                            for (final File f : files) {
                                f.delete();
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.files_deleted", Integer.toString(files.length)), I18n.get("dialog.deletion_completed"), JOptionPane.INFORMATION_MESSAGE);
                        }
                    }.execute();
                }
            });
        }
        return clearLogMenuItem;
    }

    private JMenuItem getPreferencesMenuItem() {
        if (preferencesMenuItem == null) {
            preferencesMenuItem = new JMenuItem(I18n.get("menu.system_preferences"));
            preferencesMenuItem.addActionListener(e -> showPreferences());
        }
        return preferencesMenuItem;
    }

    void showPreferences() {
        final Runtime runtime = Runtime.getRuntime();
        final JPanel gui = new JPanel(new BorderLayout());
        final JPanel inputPanel = new JPanel(new SpringLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        gui.add(inputPanel, BorderLayout.CENTER);

        JLabel label = new JLabel(I18n.get("label.maximum_memory"));
        inputPanel.add(label);
        final JTextField maxMemoryField = new JTextField(Math.round(runtime.maxMemory() / (1024.0 * 1024.0)) + " MB");
        maxMemoryField.setEditable(false);
        label.setLabelFor(maxMemoryField);
        inputPanel.add(maxMemoryField);

        label = new JLabel(I18n.get("label.total_memory"));
        inputPanel.add(label);
        final JTextField totalMemoryField = new JTextField(Math.round(runtime.totalMemory() / (1024.0 * 1024.0)) + " MB");
        totalMemoryField.setEditable(false);
        label.setLabelFor(totalMemoryField);
        inputPanel.add(totalMemoryField);

        label = new JLabel(I18n.get("label.processors"));
        inputPanel.add(label);
        final JTextField processorsField = new JTextField(Runtime.getRuntime().availableProcessors() + "");
        processorsField.setEditable(false);
        label.setLabelFor(processorsField);
        inputPanel.add(processorsField);

        label = new JLabel(I18n.get("label.java_vendor"));
        inputPanel.add(label);
        final JTextField javaVendorField = new JTextField(System.getProperty("java.vendor"), 12);
        javaVendorField.setEditable(false);
        label.setLabelFor(javaVendorField);
        inputPanel.add(javaVendorField);

        label = new JLabel(I18n.get("label.java_version"));
        inputPanel.add(label);
        final JTextField javaVersionField = new JTextField(System.getProperty("java.version"));
        javaVersionField.setEditable(false);
        label.setLabelFor(javaVersionField);
        inputPanel.add(javaVersionField);

        SpringUtilities.makeCompactGrid(inputPanel, 5, 2, 6, 6, 6, 6);
        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel")};
        final JOptionPane optionPane = new JOptionPane(new Object[]{"<html><font size=2>" + I18n.get("msg.system_preferences_info") + "</html>", gui},
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, options, options[1]);
        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.system_info_preferences"));
        dialog.setVisible(true);
    }

    private JMenuItem getAnalyzeFolderMenuItem() {
        if (analyzeFolderMenuItem == null) {
            analyzeFolderMenuItem = new JMenuItem(I18n.get("menu.analyze_folder"));
            analyzeFolderMenuItem.addActionListener(e -> {
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.researchers_only"),
                        I18n.get("dialog.research_mode"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
                    return;
                }
                SceneManager.getInstance().refresh(1);
                final File dir = FileChooser.getInstance().showDialog(".", null, false);
                if (dir == null) {
                    return;
                }
                if (dir.isDirectory()) {
                    PostProcessor.getInstance().analyze(dir.listFiles(ng3NameFilter), new File(dir + System.getProperty("file.separator") + "prop.txt"), () -> updateTitleBar());
                }
            });
        }
        return analyzeFolderMenuItem;
    }

    private JMenuItem getReplayFolderMenuItem() {
        if (replayFolderMenuItem == null) {
            replayFolderMenuItem = new JMenuItem(I18n.get("menu.replay_folder"));
            replayFolderMenuItem.addActionListener(e -> {
                if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.researchers_only"),
                        I18n.get("dialog.research_mode"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
                    return;
                }
                SceneManager.getInstance().refresh(1);
                final File dir = FileChooser.getInstance().showDialog(".", null, false);
                if (dir == null) {
                    return;
                }
                if (dir.isDirectory()) {
                    DesignReplay.getInstance().play(dir.listFiles(ng3NameFilter));
                }
            });
        }
        return replayFolderMenuItem;

    }

    private JMenuItem getReplayLastFolderMenuItem() {
        if (replayLastFolderMenuItem == null) {
            replayLastFolderMenuItem = new JMenuItem(I18n.get("menu.replay_last_folder"));
            replayLastFolderMenuItem.addActionListener(e -> {
                if (DesignReplay.getInstance().getLastFolder() != null) {
                    DesignReplay.getInstance().play(DesignReplay.getInstance().getLastFolder().listFiles(ng3NameFilter));
                }
            });
        }
        return replayLastFolderMenuItem;
    }

    private JMenuItem getEndReplayMenuItem() {
        if (endReplayMenuItem == null) {
            endReplayMenuItem = new JMenuItem(I18n.get("menu.end_replay"));
            endReplayMenuItem.addActionListener(e -> DesignReplay.active = false);
        }
        return endReplayMenuItem;
    }

    private JMenuItem getPauseReplayMenuItem() {
        if (pauseReplayMenuItem == null) {
            pauseReplayMenuItem = new JMenuItem(I18n.get("menu.pause_replay"));
            pauseReplayMenuItem.addActionListener(e -> {
                if (PlayControl.active) {
                    PlayControl.replaying = !PlayControl.replaying;
                }
            });
        }
        return pauseReplayMenuItem;
    }

    private JMenuItem getForwardReplayMenuItem() {
        if (forwardReplayMenuItem == null) {
            forwardReplayMenuItem = new JMenuItem(I18n.get("menu.forward_replay"));
            forwardReplayMenuItem.addActionListener(e -> {
                if (PlayControl.active) {
                    PlayControl.replaying = false;
                    PlayControl.forward = true;
                }
            });
        }
        return forwardReplayMenuItem;
    }

    private JMenuItem getBackwardReplayMenuItem() {
        if (backwardReplayMenuItem == null) {
            backwardReplayMenuItem = new JMenuItem(I18n.get("menu.backward_replay"));
            backwardReplayMenuItem.addActionListener(e -> {
                if (PlayControl.active) {
                    PlayControl.replaying = false;
                    PlayControl.backward = true;
                }
            });
        }
        return backwardReplayMenuItem;
    }

    public void updateTitleBar() {
        final String star = Scene.getInstance().isEdited() ? "*" : "";
        final String baseTitle = I18n.get("app.title", MainApplication.VERSION) + star;
        if (Scene.getURL() == null) {
            setTitle(baseTitle);
        } else {
            if (Scene.isInternalFile()) {
                final String s = Scene.getURL().toString();
                setTitle(baseTitle + " - @" + s.substring(s.lastIndexOf("/") + 1).replaceAll("%20", " "));
            } else {
                setTitle(baseTitle + " - " + new File(Scene.getURL().getFile()).toString().replaceAll("%20", " "));
            }
        }
    }

    private JMenuItem getSaveMenuItem() {
        if (saveMenuItem == null) {
            saveMenuItem = new JMenuItem(I18n.get("menu.save"));
            saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            saveMenuItem.addActionListener(e -> save());
        }
        return saveMenuItem;
    }

    private JMenuItem getPrintMenuItem() {
        if (printMenuItem == null) {
            printMenuItem = new JMenuItem(I18n.get("menu.print"));
            printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            printMenuItem.addActionListener(e -> {
                final PrintController printController = PrintController.getInstance();
                if (!printController.isPrintPreview()) {
                    getPreviewMenuItem().setSelected(true);
                    new Thread("Energy3D Print") {
                        @Override
                        public void run() {
                            while (!printController.isFinished()) {
                                try {
                                    Thread.sleep(500);
                                } catch (final InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            PrintController.getInstance().print();
                        }
                    }.start();
                } else {
                    PrintController.getInstance().print();
                }
            });
        }
        return printMenuItem;
    }

    public JCheckBoxMenuItem getPreviewMenuItem() {
        if (previewMenuItem == null) {
            previewMenuItem = new JCheckBoxMenuItem(I18n.get("menu.print_preview"));
            previewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            previewMenuItem.addItemListener(e -> MainPanel.getInstance().getPreviewButton().setSelected(previewMenuItem.isSelected()));
        }
        return previewMenuItem;
    }

    private JRadioButtonMenuItem getOrbitMenuItem() {
        if (orbitMenuItem == null) {
            orbitMenuItem = new JRadioButtonMenuItem();
            orbitMenuItem.setText(I18n.get("menu.orbit"));
            orbitMenuItem.setSelected(true);
            orbitMenuItem.addActionListener(e -> SceneManager.getInstance().setCameraControl(CameraMode.ORBIT));
        }
        return orbitMenuItem;
    }

    private JRadioButtonMenuItem getFirstPersonMenuItem() {
        if (firstPersonMenuItem == null) {
            firstPersonMenuItem = new JRadioButtonMenuItem();
            firstPersonMenuItem.setText(I18n.get("menu.first_person"));
            firstPersonMenuItem.addActionListener(e -> SceneManager.getInstance().setCameraControl(CameraMode.FIRST_PERSON));
        }
        return firstPersonMenuItem;
    }

    private JMenuItem getResetCameraMenuItem() {
        if (resetCameraMenuItem == null) {
            resetCameraMenuItem = new JMenuItem();
            resetCameraMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            resetCameraMenuItem.setText(I18n.get("menu.reset_view"));
            resetCameraMenuItem.addActionListener(e -> SceneManager.getInstance().resetCamera());
        }
        return resetCameraMenuItem;
    }

    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText(I18n.get("menu.exit"));
            exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            exitMenuItem.addActionListener(e -> exit());
        }
        return exitMenuItem;
    }

    /** Called after the user changes the UI language in Edit &gt; Properties so menu labels update immediately. */
    public void refreshMenuLabelsAfterLocaleChange() {
        setTitle(I18n.get("app.title", MainApplication.VERSION));
        getFileMenu();
        getEditMenu();
        getViewMenu();
        getAnalysisMenu();
        getHelpMenu();
        getTemplatesMenu();
        getTutorialsMenu();
        if (fileMenu != null) fileMenu.setText(I18n.get("menu.file"));
        if (newMenuItem != null) newMenuItem.setText(I18n.get("menu.new"));
        if (openMenuItem != null) openMenuItem.setText(I18n.get("menu.open") + "...");
        if (saveMenuItem != null) saveMenuItem.setText(I18n.get("menu.save"));
        if (saveasMenuItem != null) saveasMenuItem.setText(I18n.get("menu.save_as") + "...");
        if (exitMenuItem != null) exitMenuItem.setText(I18n.get("menu.exit"));
        if (replayControlsMenu != null) replayControlsMenu.setText(I18n.get("menu.replay_controls"));
        if (pauseReplayMenuItem != null) pauseReplayMenuItem.setText(PlayControl.replaying ? I18n.get("menu.resume_replay") : I18n.get("menu.pause_replay"));
        if (recoveryMenuItem != null) recoveryMenuItem.setText(I18n.get("menu.recover_snapshot"));
        if (listLoggedSnapshotsMenuItem != null) listLoggedSnapshotsMenuItem.setText(I18n.get("menu.list_snapshots"));
        if (clearLogMenuItem != null) clearLogMenuItem.setText(I18n.get("menu.clear_log"));
        if (printMenuItem != null) printMenuItem.setText(I18n.get("menu.print"));
        if (previewMenuItem != null) previewMenuItem.setText(I18n.get("menu.print_preview"));
        if (submitToVsgMenuItem != null) submitToVsgMenuItem.setText(I18n.get("menu.submit_vsg"));
        if (editMenu != null) editMenu.setText(I18n.get("menu.edit"));
        if (undoMenuItem != null) undoMenuItem.setText(I18n.get("menu.undo"));
        if (redoMenuItem != null) redoMenuItem.setText(I18n.get("menu.redo"));
        if (cutMenuItem != null) cutMenuItem.setText(I18n.get("menu.cut"));
        if (copyMenuItem != null) copyMenuItem.setText(I18n.get("menu.copy"));
        if (pasteMenuItem != null) pasteMenuItem.setText(I18n.get("menu.paste"));
        if (propertiesMenuItem != null) propertiesMenuItem.setText(I18n.get("menu.properties"));
        if (viewMenu != null) viewMenu.setText(I18n.get("menu.view"));
        if (orbitMenuItem != null) orbitMenuItem.setText(I18n.get("menu.orbit"));
        if (firstPersonMenuItem != null) firstPersonMenuItem.setText(I18n.get("menu.first_person"));
        if (resetCameraMenuItem != null) resetCameraMenuItem.setText(I18n.get("menu.reset_view"));
        if (visualizationSettingsMenuItem != null) visualizationSettingsMenuItem.setText(I18n.get("menu.visualization_settings"));
        if (zoomInMenuItem != null) zoomInMenuItem.setText(I18n.get("menu.zoom_in"));
        if (zoomOutMenuItem != null) zoomOutMenuItem.setText(I18n.get("menu.zoom_out"));
        if (groundImageMenu != null) groundImageMenu.setText(I18n.get("menu.ground_image"));
        if (showGroundImageMenuItem != null) showGroundImageMenuItem.setText(I18n.get("menu.show_image"));
        if (sunAnglesMenu != null) sunAnglesMenu.setText(I18n.get("menu.sun_angles"));
        if (axesMenuItem != null) axesMenuItem.setText(I18n.get("menu.axes"));
        if (zenithAngleMenuItem != null) zenithAngleMenuItem.setText(I18n.get("menu.zenith_angle"));
        if (elevationAngleMenuItem != null) elevationAngleMenuItem.setText(I18n.get("menu.elevation_angle"));
        if (azimuthAngleMenuItem != null) azimuthAngleMenuItem.setText(I18n.get("menu.azimuth_angle"));
        if (lightBeamsMenuItem != null) lightBeamsMenuItem.setText(I18n.get("menu.reflector_light_beams"));
        if (disableShadowInActionMenuItem != null) disableShadowInActionMenuItem.setText(I18n.get("menu.disable_shadows_in_action"));
        if (roofDashedLineMenuItem != null) roofDashedLineMenuItem.setText(I18n.get("menu.roof_dashed_lines"));
        if (shadowMenuItem != null) shadowMenuItem.setText(I18n.get("menu.shadows"));
        if (solarRadiationHeatMapMenuItem != null) solarRadiationHeatMapMenuItem.setText(I18n.get("menu.solar_irradiance_heat_map"));
        if (onlySolarComponentsInSolarMapMenuItem != null) onlySolarComponentsInSolarMapMenuItem.setText(I18n.get("menu.only_on_solar_components"));
        if (showSolarLandMenuItem != null) showSolarLandMenuItem.setText(I18n.get("menu.show_on_land"));
        if (solarAbsorptionHeatMapMenuItem != null) solarAbsorptionHeatMapMenuItem.setText(I18n.get("menu.only_absorbed_energy"));
        if (onlyReflectionHeatMapMenuItem != null) onlyReflectionHeatMapMenuItem.setText(I18n.get("menu.only_reflected_energy"));
        if (showHeatFluxVectorsMenuItem != null) showHeatFluxVectorsMenuItem.setText(I18n.get("menu.heat_flux_vectors"));
        if (annotationsMenuItem != null) annotationsMenuItem.setText(I18n.get("menu.annotations"));
        if (defaultMenuItem != null) defaultMenuItem.setText(I18n.get("menu.default_env"));
        if (desertMenuItem != null) desertMenuItem.setText(I18n.get("menu.desert"));
        if (grasslandMenuItem != null) grasslandMenuItem.setText(I18n.get("menu.grassland"));
        if (forestMenuItem != null) forestMenuItem.setText(I18n.get("menu.forest"));
        if (noteCheckBoxMenuItem != null) noteCheckBoxMenuItem.setText(I18n.get("menu.show_note"));
        if (infoPanelCheckBoxMenuItem != null) infoPanelCheckBoxMenuItem.setText(I18n.get("menu.show_info_panel"));
        if (autoRecomputeEnergyMenuItem != null) autoRecomputeEnergyMenuItem.setText(I18n.get("menu.auto_recompute_energy_label"));
        if (analysisMenu != null) analysisMenu.setText(I18n.get("menu.analysis"));
        if (simulationSettingsMenuItem != null) simulationSettingsMenuItem.setText(I18n.get("menu.simulation_settings"));
        if (sortIdMenuItem != null) sortIdMenuItem.setText(I18n.get("menu.sort_id"));
        if (helpMenu != null) helpMenu.setText(I18n.get("menu.help"));
        if (aboutMenuItem != null) aboutMenuItem.setText(I18n.get("menu.about"));
        if (preferencesMenuItem != null) preferencesMenuItem.setText(I18n.get("menu.system_preferences"));
        if (examplesMenu != null) {
            examplesMenu.setText(I18n.get("menu.examples"));
            refreshMenuLabelsRecursive(examplesMenu);
        }
        if (tutorialsMenu != null) {
            tutorialsMenu.setText(I18n.get("menu.tutorials"));
            refreshMenuLabelsRecursive(tutorialsMenu);
        }
        if (environmentMenu != null) environmentMenu.setText(I18n.get("menu.set_environment"));
        if (aboutDialog != null) aboutDialog.setTitle(I18n.get("dialog.about"));
        if (EnergyPanel.getInstance() != null) EnergyPanel.getInstance().refreshLabelsAfterLocaleChange();
    }

    private JMenu getHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu(I18n.get("menu.help"));
            helpMenu.addMenuListener(new MenuListener() {

                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                }
            });

            // User data and models

            final JMenu userHistoryMenu = new JMenu(I18n.get("menu.view_my_history"));
            helpMenu.add(userHistoryMenu);
            helpMenu.addSeparator();

            final JMenu userEventsMenu = new JMenu(I18n.get("menu.events"));
            userHistoryMenu.add(userEventsMenu);

            JMenuItem mi = new JMenuItem(I18n.get("menu.event_string"));
            mi.addActionListener(e -> new EventString().showGui());
            userEventsMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.event_time_series"));
            mi.addActionListener(e -> new EventTimeSeries().showGui());
            userEventsMenu.add(mi);

            mi = new JMenuItem(I18n.get("menu.event_frequency"));
            mi.addActionListener(e -> new EventFrequency().showGui());
            userEventsMenu.add(mi);

            final JMenu userResultsMenu = new JMenu(I18n.get("menu.results"));
            userHistoryMenu.add(userResultsMenu);

            mi = new JMenuItem(I18n.get("menu.analysis_results"));
            mi.addActionListener(e -> new ResultList().showGui());
            userResultsMenu.add(mi);

            if (!Config.isMac()) {
                helpMenu.add(getPreferencesMenuItem());
            }
            helpMenu.add(getFixProblemsMenuItem());
            helpMenu.add(getSortIdMenuItem());
            helpMenu.add(getRecoveryMenuItem());
            helpMenu.add(getListLoggedSnapshotsMenuItem());
            helpMenu.add(getClearLogMenuItem());

            final JMenuItem miUpdate = new JMenuItem(I18n.get("menu.check_update")); // the automatic updater can fail sometimes. This provides an independent check.
            helpMenu.add(miUpdate);
            miUpdate.setEnabled(!Config.isWebStart() && !Config.isEclipse());
            miUpdate.addActionListener(e -> {
                File jarFile;
                try {
                    jarFile = new File(MainApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                } catch (final URISyntaxException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(instance, e1.getMessage(), I18n.get("msg.url_error_local"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!jarFile.toString().endsWith("energy3d.jar")) {
                    return;
                }
                final long localLastModified = jarFile.lastModified();
                new SwingWorker<Void, Void>() {

                    URLConnection connection = null;
                    String msg = null;
                    long remoteLastModified;

                    @Override
                    protected Void doInBackground() throws Exception {
                        try {
                            connection = new URL("http://energy.concord.org/energy3d/update/energy3d.jar").openConnection();
                            remoteLastModified = connection.getLastModified();
                        } catch (final Exception e1) {
                            e1.printStackTrace();
                            msg = e1.getMessage();
                            throw e1;
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        if (connection == null) {
                            JOptionPane.showMessageDialog(instance, msg, I18n.get("msg.url_error_remote"), JOptionPane.ERROR_MESSAGE);
                        } else {
                            if (remoteLastModified <= localLastModified) {
                                JOptionPane.showMessageDialog(instance, I18n.get("msg.software_up_to_date"), I18n.get("dialog.update_status"), JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(instance, "<html>" + I18n.get("msg.software_out_of_date") + "</html>", I18n.get("dialog.update_status"), JOptionPane.INFORMATION_MESSAGE);
                                Util.openBrowser("http://energy3d.concord.org");
                            }
                        }
                    }

                }.execute();
            });

            helpMenu.addSeparator();
            helpMenu.add(getInfoPanelCheckBoxMenuItem());
            helpMenu.add(getNoteCheckBoxMenuItem());
            helpMenu.addSeparator();

            // Energy3D web pages

            mi = new JMenuItem(I18n.get("menu.visit_home"));
            mi.addActionListener(e -> Util.openBrowser("http://energy3d.concord.org"));
            helpMenu.add(mi);
            mi = new JMenuItem(I18n.get("menu.visit_vsg"));
            mi.addActionListener(e -> Util.openBrowser("http://energy.concord.org/energy3d/vsg/syw.html"));
            helpMenu.add(mi);
            mi = new JMenuItem(I18n.get("menu.visit_forum"));
            mi.addActionListener(e -> Util.openBrowser("https://energy.concord.org/energy3d/forum/"));
            helpMenu.add(mi);
            mi = new JMenuItem(I18n.get("menu.contact_us"));
            mi.addActionListener(e -> Util.openBrowser("http://energy.concord.org/energy3d/contact.html"));
            helpMenu.add(mi);
            if (!Config.isMac()) {
                helpMenu.add(getAboutMenuItem());
            }
        }
        return helpMenu;
    }

    void showAbout() {
        final JDialog aboutDialog = getAboutDialog();
        final Dimension frameSize = getSize();
        final Dimension dialogSize = aboutDialog.getSize();
        final Point location = getLocation();
        aboutDialog.setLocation((int) (location.getX() + frameSize.getWidth() / 2 - dialogSize.getWidth() / 2), (int) (location.getY() + frameSize.getHeight() / 2 - dialogSize.getHeight() / 2));
        aboutDialog.setVisible(true);
    }

    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText(I18n.get("menu.about"));
            aboutMenuItem.addActionListener(e -> showAbout());
        }
        return aboutMenuItem;
    }

    private JDialog getAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new JDialog(this);
            aboutDialog.setTitle(I18n.get("dialog.about"));
            final JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            final String title = "<h3>" + I18n.get("app.name") + "</h3><h4><i>" + I18n.get("about.tagline") + "</i></h4>" + I18n.get("about.version") + ": " + MainApplication.VERSION + ", &copy; 2011-" + Calendar.getInstance().get(Calendar.YEAR);
            final String developer = "<br>" + I18n.get("about.laboratory") + "<hr><h4>" + I18n.get("about.developers") + "</h4>" + I18n.get("about.brought_by") + ":" +
                    "<ul><li>Dr. Charles Xie (2009-present) <li>Dr. Saeid Nourian (2010-2017)</ul>" + I18n.get("about.credits");
            final String license = "<br>" + I18n.get("about.license");
            final String funder = "<h4>" + I18n.get("about.funders") + "</h4>" + I18n.get("about.funding_nsf") +
                    "<br>0918449, 1304485, 1348530, 1503196, 1512868, and 1721054 " + I18n.get("about.funding_and") +
                    "<br>" + I18n.get("about.funding_gm") +
                    "<br>" + I18n.get("about.funding_disclaimer");
            final String source = "<h4>" + I18n.get("about.source_code") + "</h4>https://github.com/concord-consortium/energy3d";
            String acknowledge = "<h4>" + I18n.get("about.acknowledgement") + "</h4>";
            acknowledge += "<font size=2>" + I18n.get("about.dedication") + "<br>";
            acknowledge += I18n.get("about.thanks") + "<br>";
            acknowledge += "Katie Armstrong, Siobhan Bailey, Jie Chao,<br>";
            acknowledge += "Guanhua Chen, Amos Decker, Maya Haigis, Xudong Huang, Shiyan Jiang,<br>";
            acknowledge += "Mark Liao, Shasha Liu, Jeff Lockwood, Joy Massicotte, Ethan McElroy, Scott Ogle,<br>";
            acknowledge += "Cormac Paterson, Allison Pearson, Molla Rahman, Corey Schimpf, Elena Sereiviene,<br>";
            acknowledge += "Zhenghui Sha, Shannon Sung, Wanli Xing, Helen Zhang";
            p.add(new JLabel("<html>" + title + developer + license + funder + source + acknowledge + "</html>"), BorderLayout.CENTER);
            final JButton button = new JButton(I18n.get("common.close"));
            button.addActionListener(e -> aboutDialog.dispose());
            final JPanel p2 = new JPanel();
            p2.add(button);
            p.add(p2, BorderLayout.SOUTH);
            aboutDialog.setContentPane(p);
            aboutDialog.pack();
        }
        return aboutDialog;
    }

    private JMenuItem getSaveasMenuItem() {
        if (saveasMenuItem == null) {
            saveasMenuItem = new JMenuItem(I18n.get("menu.save_as") + "...");
            saveasMenuItem.setAccelerator(KeyStroke.getKeyStroke("F12"));
            saveasMenuItem.addActionListener(e -> saveFile(false));
        }
        return saveasMenuItem;
    }

    private JMenuItem getSubmitToVsgMenuItem() {
        if (submitToVsgMenuItem == null) {
            submitToVsgMenuItem = new JMenuItem(I18n.get("menu.submit_vsg"));
            submitToVsgMenuItem.addActionListener(e -> {
                final GeoLocation geo = Scene.getInstance().getGeoLocation();
                if (geo == null) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_geolocation"), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String legal = "<html><hr><font size=2>" + I18n.get("msg.vsg_legal_notice") + "<hr></html>";
                final JPanel gui = new JPanel(new BorderLayout());
                String s = "<html><b>" + I18n.get("msg.vsg_authorization") + "</b></html>";
                s += "";
                final Object[] options = new Object[]{I18n.get("dialog.accept"), I18n.get("dialog.decline"), I18n.get("dialog.check_vsg")};
                final JOptionPane optionPane = new JOptionPane(new Object[]{s, legal, gui}, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[1]);
                final JDialog dialog = optionPane.createDialog(instance, I18n.get("dialog.authorization_publication"));
                dialog.setVisible(true);
                final Object choice = optionPane.getValue();
                if (choice == options[0]) {
                    VsgSubmitter.submit();
                } else if (choice == options[2]) {
                    Util.openBrowser("http://energy.concord.org/energy3d/vsg/syw.html");
                } else if (choice == options[1] || choice == null) {
                }
            });
        }
        return submitToVsgMenuItem;
    }

    private JMenu getAnalysisMenu() {
        if (analysisMenu == null) {
            analysisMenu = new JMenu(I18n.get("menu.analysis"));
            analysisMenu.addMenuListener(new MenuListener() {

                private void enableEnergyAnalysis(final boolean b) {
                    annualEnergyAnalysisMenuItem.setEnabled(b);
                    annualEnergyAnalysisForSelectionMenuItem.setEnabled(b);
                    dailyEnergyAnalysisMenuItem.setEnabled(b);
                    dailyEnergyAnalysisForSelectionMenuItem.setEnabled(b);
                }

                @Override
                public void menuCanceled(final MenuEvent e) {
                    simulationSettingsMenuItem.setEnabled(true);
                    visualizationSettingsMenuItem.setEnabled(true);
                    enableEnergyAnalysis(true);
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                    simulationSettingsMenuItem.setEnabled(true);
                    visualizationSettingsMenuItem.setEnabled(true);
                    enableEnergyAnalysis(true);
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    MainPanel.getInstance().defaultTool();
                    final boolean b = !Scene.getInstance().isStudentMode();
                    simulationSettingsMenuItem.setEnabled(b);
                    visualizationSettingsMenuItem.setEnabled(b);
                    enableEnergyAnalysis(!Scene.getInstance().getOnlySolarAnalysis());
                }
            });

            analysisMenu.add(getSimulationSettingsMenuItem());

            final JMenu weatherMenu = new JMenu(I18n.get("menu.weather"));
            weatherMenu.add(getMonthlySunshineHoursMenuItem());
            weatherMenu.add(getAnnualEnvironmentalTemperatureMenuItem());
            weatherMenu.add(getDailyEnvironmentalTemperatureMenuItem());
            analysisMenu.add(weatherMenu);

            analysisMenu.addSeparator();

            final JMenu buildingsMenu = new JMenu(I18n.get("menu.buildings"));
            analysisMenu.add(buildingsMenu);
            buildingsMenu.add(getDailyEnergyAnalysisMenuItem());
            buildingsMenu.add(getAnnualEnergyAnalysisMenuItem());
            buildingsMenu.addSeparator();
            buildingsMenu.add(getDailyEnergyAnalysisForSelectionMenuItem());
            buildingsMenu.add(getAnnualEnergyAnalysisForSelectionMenuItem());

            final JMenu solarPanelsMenu = new JMenu(I18n.get("menu.solar_panels"));
            analysisMenu.add(solarPanelsMenu);
            solarPanelsMenu.add(getDailyPvAnalysisMenuItem());
            solarPanelsMenu.add(getAnnualPvAnalysisMenuItem());

            final JMenu heliostatsMenu = new JMenu(I18n.get("menu.heliostats"));
            analysisMenu.add(heliostatsMenu);
            heliostatsMenu.add(getDailyHeliostatAnalysisMenuItem());
            heliostatsMenu.add(getAnnualHeliostatAnalysisMenuItem());

            final JMenu parabolicTroughsMenu = new JMenu(I18n.get("menu.parabolic_troughs"));
            analysisMenu.add(parabolicTroughsMenu);
            parabolicTroughsMenu.add(getDailyParabolicTroughAnalysisMenuItem());
            parabolicTroughsMenu.add(getAnnualParabolicTroughAnalysisMenuItem());

            final JMenu parabolicDishesMenu = new JMenu(I18n.get("menu.parabolic_dishes"));
            analysisMenu.add(parabolicDishesMenu);
            parabolicDishesMenu.add(getDailyParabolicDishAnalysisMenuItem());
            parabolicDishesMenu.add(getAnnualParabolicDishAnalysisMenuItem());

            final JMenu fresnelReflectorsMenu = new JMenu(I18n.get("menu.fresnel_reflectors"));
            analysisMenu.add(fresnelReflectorsMenu);
            fresnelReflectorsMenu.add(getDailyFresnelReflectorAnalysisMenuItem());
            fresnelReflectorsMenu.add(getAnnualFresnelReflectorAnalysisMenuItem());

            analysisMenu.addSeparator();

            final JMenu sensorsMenu = new JMenu(I18n.get("menu.sensors"));
            analysisMenu.add(sensorsMenu);
            sensorsMenu.add(getDailySensorMenuItem());
            sensorsMenu.add(getAnnualSensorMenuItem());

            final JMenu groupMenu = new JMenu(I18n.get("menu.group"));
            analysisMenu.add(groupMenu);
            groupMenu.add(getGroupDailyAnalysisMenuItem());
            groupMenu.add(getGroupAnnualAnalysisMenuItem());

            analysisMenu.addSeparator();

            analysisMenu.add(getFinancialSettingsMenuItem());
            analysisMenu.add(getCostBreakdownMenuItem());

        }
        return analysisMenu;
    }

    private JMenu getTemplatesMenu() {
        if (examplesMenu == null) {
            examplesMenu = new JMenu(I18n.get("menu.examples"));
            examplesMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    MainPanel.getInstance().defaultTool();
                }
            });
            final JMenu benchmarksMenu = new JMenu(I18n.get("menu.benchmarks"));
            benchmarksMenu.putClientProperty("i18n.key", "menu.benchmarks");
            examplesMenu.add(benchmarksMenu);
            final JMenu bestestMenu = new JMenu(I18n.get("menu.bestest"));
            bestestMenu.putClientProperty("i18n.key", "menu.bestest");
            benchmarksMenu.add(bestestMenu);
            addModel(bestestMenu, "ex.bestest_600", "benchmarks/bestest600.ng3");
            addModel(bestestMenu, "ex.bestest_610", "benchmarks/bestest610.ng3");
            addModel(bestestMenu, "ex.bestest_620", "benchmarks/bestest620.ng3");
            addModel(bestestMenu, "ex.bestest_630", "benchmarks/bestest630.ng3");
            final JMenu simpleMenu = new JMenu(I18n.get("menu.simple_buildings"));
            simpleMenu.putClientProperty("i18n.key", "menu.simple_buildings");
            examplesMenu.add(simpleMenu);
            addModel(simpleMenu, "ex.apartment_1", "templates/apartment-template-1.ng3");
            addModel(simpleMenu, "ex.apartment_2", "templates/apartment-template-2.ng3");
            addModel(simpleMenu, "ex.box_gabled_roof", "templates/box-gabled-template.ng3");
            addModel(simpleMenu, "ex.bungalow", "templates/bungalow-template.ng3");
            addModel(simpleMenu, "ex.butterfly_roof", "templates/butterfly-template.ng3");
            addModel(simpleMenu, "ex.cape_cod", "templates/cape-cod-template.ng3");
            addModel(simpleMenu, "ex.colonial", "templates/colonial-template.ng3");
            addModel(simpleMenu, "ex.combination_roof", "templates/combination-roof-template.ng3");
            addModel(simpleMenu, "ex.cross_gabled_roof", "templates/cross-gabled-template.ng3");
            addModel(simpleMenu, "ex.cross_hipped_roof", "templates/cross-hipped-template.ng3");
            addModel(simpleMenu, "ex.dutch_colonial", "templates/gambrel-template.ng3");
            addModel(simpleMenu, "ex.flat_roof", "templates/flat-roof-template.ng3");
            addModel(simpleMenu, "ex.gable_valley_roof", "templates/gable-valley-template.ng3");
            addModel(simpleMenu, "ex.gablet_roof", "templates/gablet-template.ng3");
            addModel(simpleMenu, "ex.hip_roof", "templates/hip-roof-template.ng3");
            addModel(simpleMenu, "ex.hip_valley_roof", "templates/hip-valley-template.ng3");
            addModel(simpleMenu, "ex.m_shaped_roof", "templates/m-shaped-template.ng3");
            addModel(simpleMenu, "ex.mansard", "templates/mansard-template.ng3");
            addModel(simpleMenu, "ex.saltbox_1", "templates/saltbox-template-1.ng3");
            addModel(simpleMenu, "ex.saltbox_2", "templates/saltbox-template-2.ng3");
            addModel(simpleMenu, "ex.shed_roof", "templates/shed-roof-template.ng3");
            addModel(simpleMenu, "ex.hexagonal_house", "templates/hexagonal-template.ng3");
            addModel(simpleMenu, "ex.h_shaped_house", "templates/h-shape-template.ng3");
            addModel(simpleMenu, "ex.u_shaped_house", "templates/u-shape-template.ng3");
            addModel(simpleMenu, "ex.square_tower", "templates/example-square-tower.ng3");
            final JMenu complexMenu = new JMenu(I18n.get("menu.complex_buildings"));
            complexMenu.putClientProperty("i18n.key", "menu.complex_buildings");
            examplesMenu.add(complexMenu);
            addModel(complexMenu, "ex.cape_cod_front_porch", "templates/example-cape-cod-front-porch.ng3");
            addModel(complexMenu, "ex.cape_cod_garage", "templates/example-cape-cod-attached-garage.ng3");
            addModel(complexMenu, "ex.cape_cod_shed_gable_dormers", "templates/example-cape-cod-shed-gable-dormers.ng3");
            addModel(complexMenu, "ex.cape_cod_complex", "templates/example-cape-cod-complex.ng3");
            addModel(complexMenu, "ex.colonial_fence", "templates/example-colonial-fence.ng3");
            addModel(complexMenu, "ex.colonial_front_porch", "templates/example-colonial-front-porch.ng3");
            addModel(complexMenu, "ex.colonial_l_shaped", "templates/example-colonial-l-shaped.ng3");
            addModel(complexMenu, "ex.dutch_colonial_front_porch", "templates/example-dutch-colonial.ng3");
            addModel(complexMenu, "ex.german_pub", "templates/example-german.ng3");
            addModel(complexMenu, "ex.federal", "templates/example-federal.ng3");
            addModel(complexMenu, "ex.victorian", "templates/example-victorian.ng3");
            addModel(complexMenu, "ex.shingle", "templates/example-shingle.ng3");
            addModel(complexMenu, "ex.sunroom", "templates/example-sunroom.ng3");
            addModel(complexMenu, "ex.barn_house", "templates/example-barn-house.ng3");
            addModel(complexMenu, "ex.santa_fe", "templates/example-santa-fe.ng3");
            addModel(complexMenu, "ex.courtyard_house", "templates/example-courtyard-house.ng3");
            addModel(complexMenu, "ex.hotel", "templates/example-hotel.ng3");
            addModel(complexMenu, "ex.hospital", "templates/example-hospital.ng3");
            addModel(complexMenu, "ex.modern_house_1", "templates/example-modern-house-1.ng3");
            addModel(complexMenu, "ex.modern_house_2", "templates/example-modern-house-2.ng3");
            addModel(complexMenu, "ex.mansion_1", "templates/example-mansion-1.ng3");
            final JMenu famousMenu = new JMenu(I18n.get("menu.famous_buildings"));
            famousMenu.putClientProperty("i18n.key", "menu.famous_buildings");
            examplesMenu.add(famousMenu);
            addModel(famousMenu, "ex.white_house", "templates/white-house.ng3");
            addModel(famousMenu, "ex.library_of_congress", "templates/library-of-congress.ng3");
            addModel(famousMenu, "ex.empire_state", "templates/empire-state-building.ng3");
            addModel(famousMenu, "ex.one_world_trade_center", "templates/one-world-trade-center.ng3");
            addModel(famousMenu, "ex.willis_tower", "templates/willis-tower.ng3");
            addModel(famousMenu, "ex.apple_headquarters", "templates/apple-headquarters.ng3");
            addModel(famousMenu, "ex.huqiu_tower", "templates/chinese-tower-template.ng3");
            final JMenu multipleMenu = new JMenu(I18n.get("menu.multiple_buildings"));
            multipleMenu.putClientProperty("i18n.key", "menu.multiple_buildings");
            examplesMenu.add(multipleMenu);
            addModel(multipleMenu, "ex.campus_1", "templates/example-campus-1.ng3");
            addModel(multipleMenu, "ex.campus_2", "templates/example-campus-2.ng3");
            addModel(multipleMenu, "ex.street_1", "templates/example-street-1.ng3");
            addModel(multipleMenu, "ex.street_2", "templates/example-street-2.ng3");
            addModel(multipleMenu, "ex.street_3", "templates/example-street-3.ng3");
            final JMenu pvSolarMenu = new JMenu(I18n.get("menu.pv_systems"));
            pvSolarMenu.putClientProperty("i18n.key", "menu.pv_systems");
            addModel(pvSolarMenu, "ex.solar_canopy_wavy", "templates/example-solar-canopy-wavy-top.ng3");
            addModel(pvSolarMenu, "ex.solar_canopy_curvy", "templates/example-solar-canopy-curvy-top.ng3");
            addModel(pvSolarMenu, "ex.solar_canopy_bus_stop", "templates/example-solar-canopy-bus-stop.ng3");
            addModel(pvSolarMenu, "ex.solar_canopy_parking_1", "templates/example-solar-canopy-parking-garage-1.ng3");
            addModel(pvSolarMenu, "ex.solar_canopy_parking_2", "templates/example-solar-canopy-parking-garage-2.ng3");
            addModel(pvSolarMenu, "ex.solar_canopy_overhang", "templates/example-solar-canopy-overhang.ng3");
            addModel(pvSolarMenu, "ex.solar_facade_1", "templates/example-solar-facade1.ng3");
            addModel(pvSolarMenu, "ex.solar_facade_2", "templates/example-solar-facade2.ng3");
            addModel(pvSolarMenu, "ex.solar_facade_3", "templates/example-solar-facade3.ng3");
            addModel(pvSolarMenu, "ex.solar_facade_4", "templates/example-solar-facade4.ng3");
            addModel(pvSolarMenu, "ex.agrivoltaics_1", "templates/example-agrivoltaics-01.ng3");
            examplesMenu.add(pvSolarMenu);
            final JMenu cspSolarMenu = new JMenu(I18n.get("menu.csp_systems"));
            cspSolarMenu.putClientProperty("i18n.key", "menu.csp_systems");
            cspSolarMenu.setEnabled(false);
            examplesMenu.add(cspSolarMenu);
            final JMenu miscMenu = new JMenu(I18n.get("menu.miscellaneous"));
            miscMenu.putClientProperty("i18n.key", "menu.miscellaneous");
            examplesMenu.add(miscMenu);
            addModel(miscMenu, "ex.mosque", "templates/mosque-template.ng3");
            addModel(miscMenu, "ex.temple", "templates/temple-template.ng3");
            addModel(miscMenu, "ex.tibetan_temple", "templates/tibetan-temple-template.ng3");
            addModel(miscMenu, "ex.church_1", "templates/church-template-1.ng3");
            addModel(miscMenu, "ex.church_2", "templates/church-template-2.ng3");
            addModel(miscMenu, "ex.church_3", "templates/church-template-3.ng3");
            addModel(miscMenu, "ex.cathedral", "templates/cathedral-template.ng3");
            addModel(miscMenu, "ex.mexican_church", "templates/mexican-church-template.ng3");
            addModel(miscMenu, "ex.dome", "templates/dome-template.ng3");
            addModel(miscMenu, "ex.egyptian_pyramid", "templates/egyptian-pyramid-template.ng3");
            addModel(miscMenu, "ex.mayan_pyramid", "templates/mayan-pyramid-template.ng3");
            addModel(miscMenu, "ex.stadium", "templates/stadium-template.ng3");
            addModel(miscMenu, "ex.amphitheatre", "templates/amphitheatre-template.ng3");
            addModel(miscMenu, "ex.lighthouse", "templates/example-lighthouse.ng3");
            addModel(miscMenu, "ex.volkswagen", "templates/example-volkwagen.ng3");
            addModel(miscMenu, "ex.sailboat", "templates/example-sailboat.ng3");
            addModel(miscMenu, "ex.drone", "templates/example-drone.ng3");
        }
        return examplesMenu;
    }

    private void refreshMenuLabelsRecursive(final JComponent c) {
        if (c instanceof JMenu) {
            final JMenu m = (JMenu) c;
            final Object key = m.getClientProperty("i18n.key");
            if (key != null) {
                m.setText(I18n.get(key.toString()));
            }
            for (int i = 0; i < m.getItemCount(); i++) {
                final JMenuItem item = m.getItem(i);
                if (item != null) {
                    refreshMenuLabelsRecursive(item);
                }
            }
        } else if (c instanceof JMenuItem) {
            final Object key = ((JMenuItem) c).getClientProperty("i18n.key");
            if (key != null) {
                ((JMenuItem) c).setText(I18n.get(key.toString()));
            }
        }
    }

    private void addModel(final JMenu menu, final String i18nKey, final String url) {
        final JMenuItem mi = new JMenuItem(I18n.get(i18nKey));
        mi.putClientProperty("i18n.key", i18nKey);
        mi.addActionListener(e -> openModel(MainApplication.class.getResource(url)));
        menu.add(mi);
    }

    private JMenu getTutorialsMenu() {
        if (tutorialsMenu == null) {
            tutorialsMenu = new JMenu(I18n.get("menu.tutorials"));
            tutorialsMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    MainPanel.getInstance().defaultTool();
                }
            });

            final JMenu uiBasicsMenu = new JMenu(I18n.get("menu.ui_basics"));
            uiBasicsMenu.putClientProperty("i18n.key", "menu.ui_basics");
            uiBasicsMenu.setEnabled(false);
            tutorialsMenu.add(uiBasicsMenu);

            final JMenu buildingBasicsMenu = new JMenu(I18n.get("menu.building_basics"));
            buildingBasicsMenu.putClientProperty("i18n.key", "menu.building_basics");
            tutorialsMenu.add(buildingBasicsMenu);
            addModel(buildingBasicsMenu, "tut.two_houses_sizes", "tutorials/building-size.ng3");
            addModel(buildingBasicsMenu, "tut.two_houses_shapes", "tutorials/building-shape.ng3");
            addModel(buildingBasicsMenu, "tut.two_houses_roof_insulation", "tutorials/building-roof-insulation.ng3");
            addModel(buildingBasicsMenu, "tut.two_houses_roof_colors", "tutorials/building-cool-roof.ng3");
            addModel(buildingBasicsMenu, "tut.two_houses_window_shgc", "tutorials/building-window-shgc.ng3");
            addModel(buildingBasicsMenu, "tut.two_houses_orientations", "tutorials/building-orientation.ng3");
            addModel(buildingBasicsMenu, "tut.two_houses_thermostat", "tutorials/building-thermostat.ng3");
            buildingBasicsMenu.addSeparator();
            addModel(buildingBasicsMenu, "tut.energy_use_locations", "tutorials/building-location.ng3");
            addModel(buildingBasicsMenu, "tut.effect_environment_albedo", "tutorials/building-albedo.ng3");
            addModel(buildingBasicsMenu, "tut.passive_heating_windows", "tutorials/building-passive-heating.ng3");
            addModel(buildingBasicsMenu, "tut.passive_cooling_trees", "tutorials/building-tree-passive-cooling.ng3");

            final JMenu solarBasicsMenu = new JMenu(I18n.get("menu.solar_basics"));
            solarBasicsMenu.putClientProperty("i18n.key", "menu.solar_basics");
            tutorialsMenu.add(solarBasicsMenu);
            addModel(solarBasicsMenu, "tut.sun_path", "tutorials/sun-path.ng3");
            addModel(solarBasicsMenu, "tut.projection_effect", "tutorials/projection-effect.ng3");
            addModel(solarBasicsMenu, "tut.air_mass", "tutorials/air-mass.ng3");
            addModel(solarBasicsMenu, "tut.weather_effect", "tutorials/weather-effect.ng3");
            addModel(solarBasicsMenu, "tut.solar_radiation_pathways", "tutorials/solar-radiation-pathways.ng3");
            addModel(solarBasicsMenu, "tut.optimize_it", "tutorials/optimize-it.ng3");
            solarBasicsMenu.addSeparator();
            addModel(solarBasicsMenu, "tut.solar_box", "tutorials/solar-box.ng3");
            addModel(solarBasicsMenu, "tut.solar_irradiance_heat_map", "tutorials/solar-heat-map.ng3");
            addModel(solarBasicsMenu, "tut.solar_analysis_cities", "tutorials/city-block.ng3");
            solarBasicsMenu.addSeparator();
            addModel(solarBasicsMenu, "tut.solar_panel_tilt_angles", "tutorials/solar-panel-tilt-angle.ng3");
            addModel(solarBasicsMenu, "tut.solar_panel_azimuthal_angles", "tutorials/solar-panel-azimuth-angle.ng3");
            addModel(solarBasicsMenu, "tut.solar_panel_orientation", "tutorials/solar-panel-orientation.ng3");
            addModel(solarBasicsMenu, "tut.solar_panel_cell_efficiency", "tutorials/solar-panel-cell-efficiency.ng3");
            addModel(solarBasicsMenu, "tut.nominal_operating_cell_temp", "tutorials/solar-panel-noct.ng3");
            addModel(solarBasicsMenu, "tut.solar_trackers", "tutorials/solar-trackers.ng3");

            tutorialsMenu.addSeparator();

            final JMenu inquiryMethodMenu = new JMenu(I18n.get("menu.inquiry_method"));
            inquiryMethodMenu.putClientProperty("i18n.key", "menu.inquiry_method");
            tutorialsMenu.add(inquiryMethodMenu);
            addModel(inquiryMethodMenu, "tut.u_value_investigation", "tutorials/guided-inquiry-u-value.ng3");
            addModel(inquiryMethodMenu, "tut.passive_solar_investigation", "tutorials/guided-inquiry-passive-solar.ng3");

            final JMenu designMethodMenu = new JMenu(I18n.get("menu.design_method"));
            designMethodMenu.putClientProperty("i18n.key", "menu.design_method");
            tutorialsMenu.add(designMethodMenu);
            addModel(designMethodMenu, "tut.building_location_optimization", "tutorials/optimization-building-locations.ng3");
            addModel(designMethodMenu, "tut.building_orientation_optimization", "tutorials/optimization-building-orientation.ng3");
            addModel(designMethodMenu, "tut.window_sizing_optimization", "tutorials/optimization-window-sizes.ng3");
            addModel(designMethodMenu, "tut.solar_panel_tilt_optimization", "tutorials/optimization-solar-panel-tilt-angle.ng3");
            addModel(designMethodMenu, "tut.solar_farm_optimization", "tutorials/optimization-solar-panel-array-rectangular-lot.ng3");
            addModel(designMethodMenu, "tut.single_heliostat_optimization", "tutorials/optimization-single-heliostat.ng3");
            addModel(designMethodMenu, "tut.concentric_heliostat_optimization", "tutorials/optimization-concentric-heliostat-field.ng3");
            addModel(designMethodMenu, "tut.spiral_heliostat_optimization", "tutorials/optimization-spiral-heliostat-field.ng3");

            tutorialsMenu.addSeparator();

            final JMenu pvMenu = new JMenu(I18n.get("menu.pv_power"));
            pvMenu.putClientProperty("i18n.key", "menu.pv_power");
            tutorialsMenu.add(pvMenu);
            addModel(pvMenu, "tut.brand_name_solar_panels", "tutorials/solar-panel-brand-names.ng3");
            addModel(pvMenu, "tut.single_solar_rack", "tutorials/solar-single-rack.ng3");
            addModel(pvMenu, "tut.multiple_solar_racks", "tutorials/solar-multiple-racks.ng3");
            addModel(pvMenu, "tut.solar_trackers_racks", "tutorials/solar-tracker-racks.ng3");
            addModel(pvMenu, "tut.solar_canopy_form_factors", "tutorials/solar-canopy-form-factors.ng3");
            pvMenu.addSeparator();
            addModel(pvMenu, "tut.rooftop_solar_system", "tutorials/pv-rooftop-system.ng3");
            addModel(pvMenu, "tut.parking_lot_solar_canopy", "tutorials/solar-canopy.ng3");
            addModel(pvMenu, "tut.pv_fixed_rack_arrays", "tutorials/pv-fixed-rack-arrays.ng3");
            addModel(pvMenu, "tut.pv_seasonal_tilt_arrays", "tutorials/solar-rack-array-seasonal-tilt.ng3");
            addModel(pvMenu, "tut.pv_hsat_arrays", "tutorials/pv-hsat-rack-arrays.ng3");
            addModel(pvMenu, "tut.pv_cost_effectiveness", "tutorials/solar-rack-why-array.ng3");
            addModel(pvMenu, "tut.pv_inter_row_spacing", "tutorials/solar-rack-array-row-spacing.ng3");
            addModel(pvMenu, "tut.pv_landscape_portrait_arrays", "tutorials/solar-rack-array-row-spacing-portrait.ng3");
            addModel(pvMenu, "tut.pv_dual_axis_layout", "tutorials/azdat-layout.ng3");

            final JMenu cspMenu = new JMenu(I18n.get("menu.csp_power"));
            cspMenu.putClientProperty("i18n.key", "menu.csp_power");
            tutorialsMenu.add(cspMenu);
            addModel(cspMenu, "tut.parabolic_trough_focal_line", "tutorials/parabolic-trough-focal-line.ng3");
            addModel(cspMenu, "tut.parabolic_trough_rim_angle", "tutorials/parabolic-trough-curvature.ng3");
            addModel(cspMenu, "tut.parabolic_trough_arrays", "tutorials/parabolic-trough-array.ng3");
            addModel(cspMenu, "tut.parabolic_trough_rhomboid", "tutorials/parabolic-trough-rhomboid-layout.ng3");
            addModel(cspMenu, "tut.parabolic_trough_azimuth_angles", "tutorials/parabolic-trough-azimuth-angles.ng3");
            cspMenu.addSeparator();
            addModel(cspMenu, "tut.parabolic_dish_stirling", "tutorials/parabolic-dish-single.ng3");
            addModel(cspMenu, "tut.parabolic_dish_focal_length", "tutorials/parabolic-dish-focal-length.ng3");
            addModel(cspMenu, "tut.parabolic_dish_array", "tutorials/parabolic-dish-array.ng3");
            cspMenu.addSeparator();
            addModel(cspMenu, "tut.linear_fresnel_reflectors", "tutorials/linear-fresnel-reflectors.ng3");
            addModel(cspMenu, "tut.linear_fresnel_absorber_height", "tutorials/linear-fresnel-reflectors-absorber-height.ng3");
            addModel(cspMenu, "tut.linear_fresnel_orientation", "tutorials/linear-fresnel-reflectors-orientation.ng3");
            addModel(cspMenu, "tut.linear_fresnel_multiple_absorbers", "tutorials/compact-linear-fresnel-reflectors.ng3");
            cspMenu.addSeparator();
            addModel(cspMenu, "tut.csp_tower", "tutorials/concentrated-solar-power-tower.ng3");
            addModel(cspMenu, "tut.cosine_efficiency", "tutorials/csp-cosine-efficiency.ng3");
            addModel(cspMenu, "tut.shadowing_blocking", "tutorials/csp-shadowing-blocking.ng3");
            addModel(cspMenu, "tut.shadowing_blocking_reduced", "tutorials/csp-shadowing-blocking-less.ng3");
            addModel(cspMenu, "tut.shadowing_blocking_spacing", "tutorials/csp-shadowing-blocking-even-less.ng3");
            addModel(cspMenu, "tut.csp_tower_height", "tutorials/csp-tower-height.ng3");
            addModel(cspMenu, "tut.fermat_spiral_layout", "tutorials/csp-spiral-layout.ng3");

        }
        return tutorialsMenu;
    }

    private JMenu getViewMenu() {
        if (viewMenu == null) {
            viewMenu = new JMenu(I18n.get("menu.view"));
            viewMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    Util.selectSilently(showSolarLandMenuItem, Scene.getInstance().getSolarMapForLand());
                    Util.selectSilently(onlySolarComponentsInSolarMapMenuItem, Scene.getInstance().getOnlySolarComponentsInSolarMap());
                    Util.selectSilently(solarRadiationHeatMapMenuItem, SceneManager.getInstance().getSolarHeatMap());
                    Util.selectSilently(solarAbsorptionHeatMapMenuItem, Scene.getInstance().getOnlyAbsorptionInSolarMap());
                    Util.selectSilently(onlyReflectionHeatMapMenuItem, Scene.getInstance().getOnlyReflectedEnergyInMirrorSolarMap());
                    Util.selectSilently(showHeatFluxVectorsMenuItem, Scene.getInstance().getAlwaysComputeHeatFluxVectors());
                    Util.selectSilently(shadowMenuItem, SceneManager.getInstance().isShadowEnabled());
                    Util.selectSilently(axesMenuItem, SceneManager.getInstance().areAxesVisible());
                    Util.selectSilently(zenithAngleMenuItem, Scene.getInstance().isZenithAngleVisible());
                    Util.selectSilently(elevationAngleMenuItem, Scene.getInstance().isElevationAngleVisible());
                    Util.selectSilently(azimuthAngleMenuItem, Scene.getInstance().isAzimuthAngleVisible());
                    Util.selectSilently(lightBeamsMenuItem, Scene.getInstance().areLightBeamsVisible());
                    Util.selectSilently(disableShadowInActionMenuItem, Scene.getInstance().getDisableShadowInAction());
                    Util.selectSilently(roofDashedLineMenuItem, Scene.getInstance().areDashedLinesOnRoofShown());
                    Util.selectSilently(lightBeamsMenuItem, Scene.getInstance().areLightBeamsVisible());
                    Util.selectSilently(annotationsMenuItem, Scene.getInstance().areAnnotationsVisible());
                    MainPanel.getInstance().defaultTool();
                    sunAnglesMenu.setEnabled(Heliodon.getInstance().isVisible());
                }
            });

            final JMenu solarHeatMapMenu = new JMenu(I18n.get("menu.solar_heat_map_options"));
            solarHeatMapMenu.add(getOnlySolarComponentsInSolarMapMenuItem());
            solarHeatMapMenu.add(getSolarAbsorptionHeatMapMenuItem());
            solarHeatMapMenu.add(getOnlyReflectionHeatMapMenuItem());
            solarHeatMapMenu.add(getShowSolarLandMenuItem());

            sunAnglesMenu = new JMenu(I18n.get("menu.sun_angles"));
            sunAnglesMenu.add(getZenithAngleMenuItem());
            sunAnglesMenu.add(getElevationAngleMenuItem());
            sunAnglesMenu.add(getAzimuthAngleMenuItem());

            viewMenu.add(getVisualizationSettingsMenuItem());
            viewMenu.addSeparator();
            viewMenu.add(getOrbitMenuItem());
            viewMenu.add(getFirstPersonMenuItem());
            final ButtonGroup bg = new ButtonGroup();
            bg.add(orbitMenuItem);
            bg.add(firstPersonMenuItem);
            viewMenu.add(getTopViewCheckBoxMenuItem());
            viewMenu.add(getResetCameraMenuItem());
            viewMenu.add(getZoomInMenuItem());
            viewMenu.add(getZoomOutMenuItem());
            viewMenu.addSeparator();
            viewMenu.add(getGroundImageMenu());
            viewMenu.addSeparator();
            viewMenu.add(getSolarRadiationHeatMapMenuItem());
            viewMenu.add(solarHeatMapMenu);
            viewMenu.add(sunAnglesMenu);
            viewMenu.add(getHeatFluxMenuItem());
            viewMenu.add(getShadowMenuItem());
            viewMenu.add(getDisableShadowInActionMenuItem());
            viewMenu.add(getLightBeamsMenuItem());
            viewMenu.addSeparator();
            viewMenu.add(getAxesMenuItem());
            viewMenu.add(getRoofDashedLineMenuItem());
            viewMenu.add(getAnnotationsMenuItem());
            viewMenu.add(getAnnotationsInwardMenuItem());

        }
        return viewMenu;
    }

    private JMenu getEnvironmentMenu() {

        if (environmentMenu == null) {
            environmentMenu = new JMenu(I18n.get("menu.set_environment"));
            environmentMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    Util.selectSilently(defaultMenuItem, Scene.getInstance().getEnvironment() == Scene.DEFAULT_THEME);
                    Util.selectSilently(desertMenuItem, Scene.getInstance().getEnvironment() == Scene.DESERT_THEME);
                    Util.selectSilently(grasslandMenuItem, Scene.getInstance().getEnvironment() == Scene.GRASSLAND_THEME);
                    Util.selectSilently(forestMenuItem, Scene.getInstance().getEnvironment() == Scene.FOREST_THEME);
                }
            });

            environmentMenu.add(getDefaultMenuItem());
            environmentMenu.add(getDesertMenuItem());
            environmentMenu.add(getGrasslandMenuItem());
            environmentMenu.add(getForestMenuItem());

        }
        return environmentMenu;

    }

    private JMenu getGroundImageMenu() {

        if (groundImageMenu == null) {
            groundImageMenu = new JMenu(I18n.get("menu.ground_image"));
            groundImageMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuCanceled(final MenuEvent e) {
                    showGroundImageMenuItem.setEnabled(true);
                    clearGroundImageMenuItem.setEnabled(true);
                    rescaleGroundImageMenuItem.setEnabled(true);
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    showGroundImageMenuItem.setEnabled(true);
                    clearGroundImageMenuItem.setEnabled(true);
                    rescaleGroundImageMenuItem.setEnabled(true);
                    SceneManager.getInstance().refresh();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    final boolean hasGroundImage = Scene.getInstance().isGroundImageEnabled();
                    showGroundImageMenuItem.setEnabled(hasGroundImage);
                    clearGroundImageMenuItem.setEnabled(hasGroundImage);
                    rescaleGroundImageMenuItem.setEnabled(hasGroundImage && !Scene.getInstance().isGroundImageEarthView());
                    Util.selectSilently(showGroundImageMenuItem, SceneManager.getInstance().getGroundImageLand().isVisible());
                }
            });

            groundImageMenu.add(getUseEarthViewMenuItem());
            groundImageMenu.add(getUseImageFileMenuItem());
            groundImageMenu.addSeparator();
            groundImageMenu.add(getRescaleGroundImageMenuItem());
            groundImageMenu.add(getClearGroundImageMenuItem());
            groundImageMenu.add(getShowGroundImageMenuItem());

        }
        return groundImageMenu;

    }

    private JMenuItem getUseEarthViewMenuItem() {
        if (useEarthViewMenuItem == null) {
            useEarthViewMenuItem = new JMenuItem(I18n.get("menu.use_earth_view"));
            useEarthViewMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            useEarthViewMenuItem.addActionListener(e -> new MapDialog(MainFrame.this).setVisible(true));
        }
        return useEarthViewMenuItem;
    }

    private JMenuItem getUseImageFileMenuItem() {
        if (useImageFileMenuItem == null) {
            useImageFileMenuItem = new JMenuItem(I18n.get("menu.use_image_file"));
            useImageFileMenuItem.addActionListener(e -> {
                final File file = FileChooser.getInstance().showDialog(".png", FileChooser.pngFilter, false);
                if (file == null) {
                    return;
                }
                try {
                    Scene.getInstance().setGroundImageEarthView(false);
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setGroundImage(ImageIO.read(file), 1);
                        return null;
                    });
                } catch (final Throwable t) {
                    t.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, t.getMessage(), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                }
                Scene.getInstance().setEdited(true);
            });
        }
        return useImageFileMenuItem;
    }

    private JMenuItem getRescaleGroundImageMenuItem() {
        if (rescaleGroundImageMenuItem == null) {
            rescaleGroundImageMenuItem = new JMenuItem(I18n.get("menu.rescale_image"));
            rescaleGroundImageMenuItem.addActionListener(e -> {
                final String title = I18n.get("title.scale_ground_image");
                while (true) {
                    final String newValue = JOptionPane.showInputDialog(MainFrame.getInstance(), title, Scene.getInstance().getGroundImageScale());
                    if (newValue == null) {
                        break;
                    } else {
                        try {
                            final double val = Double.parseDouble(newValue);
                            if (val <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.scaling_factor_positive"), I18n.get("msg.range_error"), JOptionPane.ERROR_MESSAGE);
                            } else {
                                // final RescaleGroundImageCommand c = new RescaleGroundImageCommand();
                                SceneManager.getTaskManager().update(() -> {
                                    Scene.getInstance().setGroundImageScale(val);
                                    return null;
                                });
                                // SceneManager.getInstance().getUndoManager().addEdit(c);
                                break;
                            }
                        } catch (final NumberFormatException exception) {
                            JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.invalid_value", newValue), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                Scene.getInstance().setEdited(true);
            });
        }
        return rescaleGroundImageMenuItem;
    }

    private JMenuItem getClearGroundImageMenuItem() {
        if (clearGroundImageMenuItem == null) {
            clearGroundImageMenuItem = new JMenuItem(I18n.get("menu.clear_image"));
            clearGroundImageMenuItem.addActionListener(e -> {
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setGroundImage(null, 1);
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
        }
        return clearGroundImageMenuItem;
    }

    private JCheckBoxMenuItem getShowGroundImageMenuItem() {
        if (showGroundImageMenuItem == null) {
            showGroundImageMenuItem = new JCheckBoxMenuItem(I18n.get("menu.show_image"));
            showGroundImageMenuItem.addItemListener(e -> {
                final boolean b = showGroundImageMenuItem.isSelected();
                SceneManager.getInstance().getGroundImageLand().setVisible(b);
                Scene.getInstance().setShowGroundImage(b);
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().refresh();
            });
        }
        return showGroundImageMenuItem;
    }

    private JCheckBoxMenuItem getAxesMenuItem() {
        if (axesMenuItem == null) {
            axesMenuItem = new JCheckBoxMenuItem(I18n.get("menu.axes"), true);
            axesMenuItem.addItemListener(e -> {
                final ShowAxesCommand c = new ShowAxesCommand();
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().setAxesVisible(axesMenuItem.isSelected());
                    return null;
                });
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return axesMenuItem;
    }

    private JCheckBoxMenuItem getZenithAngleMenuItem() {
        if (zenithAngleMenuItem == null) {
            zenithAngleMenuItem = new JCheckBoxMenuItem(I18n.get("menu.zenith_angle"));
            zenithAngleMenuItem.addItemListener(e -> {
                final ShowZenithAngleCommand c = new ShowZenithAngleCommand();
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setZenithAngleVisible(zenithAngleMenuItem.isSelected());
                    Heliodon.getInstance().drawSunTriangle();
                    return null;
                });
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return zenithAngleMenuItem;
    }

    private JCheckBoxMenuItem getElevationAngleMenuItem() {
        if (elevationAngleMenuItem == null) {
            elevationAngleMenuItem = new JCheckBoxMenuItem(I18n.get("menu.elevation_angle"));
            elevationAngleMenuItem.addItemListener(e -> {
                final ShowElevationAngleCommand c = new ShowElevationAngleCommand();
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setElevationAngleVisible(elevationAngleMenuItem.isSelected());
                    Heliodon.getInstance().drawSunTriangle();
                    return null;
                });
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return elevationAngleMenuItem;
    }

    private JCheckBoxMenuItem getAzimuthAngleMenuItem() {
        if (azimuthAngleMenuItem == null) {
            azimuthAngleMenuItem = new JCheckBoxMenuItem(I18n.get("menu.azimuth_angle"));
            azimuthAngleMenuItem.addItemListener(e -> {
                final ShowAzimuthAngleCommand c = new ShowAzimuthAngleCommand();
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setAzimuthAngleVisible(azimuthAngleMenuItem.isSelected());
                    Heliodon.getInstance().drawSunTriangle();
                    return null;
                });
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return azimuthAngleMenuItem;
    }

    private JCheckBoxMenuItem getLightBeamsMenuItem() {
        if (lightBeamsMenuItem == null) {
            lightBeamsMenuItem = new JCheckBoxMenuItem(I18n.get("menu.reflector_light_beams"), true);
            lightBeamsMenuItem.addItemListener(e -> {
                final ShowReflectorLightBeamsCommand c = new ShowReflectorLightBeamsCommand();
                Scene.getInstance().setLightBeamsVisible(lightBeamsMenuItem.isSelected());
                Scene.getInstance().setEdited(true);
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().redrawAll();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return lightBeamsMenuItem;
    }

    private JCheckBoxMenuItem getDisableShadowInActionMenuItem() {
        if (disableShadowInActionMenuItem == null) {
            disableShadowInActionMenuItem = new JCheckBoxMenuItem(I18n.get("menu.disable_shadows_in_action"), false);
            disableShadowInActionMenuItem.addItemListener(e -> {
                Scene.getInstance().setDisableShadowInAction(disableShadowInActionMenuItem.isSelected());
                Scene.getInstance().setEdited(true);
            });
        }
        return disableShadowInActionMenuItem;
    }

    private JCheckBoxMenuItem getRoofDashedLineMenuItem() {
        if (roofDashedLineMenuItem == null) {
            roofDashedLineMenuItem = new JCheckBoxMenuItem(I18n.get("menu.roof_dashed_lines"), false);
            roofDashedLineMenuItem.addItemListener(e -> {
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setDashedLinesOnRoofShown(roofDashedLineMenuItem.isSelected());
                    Scene.getInstance().redrawAll();
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
        }
        return roofDashedLineMenuItem;
    }

    public JCheckBoxMenuItem getShadowMenuItem() {
        if (shadowMenuItem == null) {
            shadowMenuItem = new JCheckBoxMenuItem(I18n.get("menu.shadows"), false);
            shadowMenuItem.addItemListener(e -> {
                final ShowShadowCommand c = new ShowShadowCommand();
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().setShadow(shadowMenuItem.isSelected());
                    return null;
                });
                Util.selectSilently(MainPanel.getInstance().getShadowButton(), shadowMenuItem.isSelected());
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return shadowMenuItem;
    }

    private JMenuItem getSortIdMenuItem() {
        if (sortIdMenuItem == null) {
            sortIdMenuItem = new JMenuItem(I18n.get("menu.sort_id"));
            sortIdMenuItem.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.sort_ids_confirm"),
                        I18n.get("dialog.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION) {
                    return;
                }
                Scene.getInstance().sortID();
                Scene.getInstance().redrawAll();
            });
        }
        return sortIdMenuItem;
    }

    private JMenuItem getSimulationSettingsMenuItem() {
        if (simulationSettingsMenuItem == null) {
            simulationSettingsMenuItem = new JMenuItem(I18n.get("menu.simulation_settings"));
            simulationSettingsMenuItem.addActionListener(e -> new SimulationSettingsDialog().setVisible(true));
        }
        return simulationSettingsMenuItem;
    }

    private JMenuItem getVisualizationSettingsMenuItem() {
        if (visualizationSettingsMenuItem == null) {
            visualizationSettingsMenuItem = new JMenuItem(I18n.get("menu.visualization_settings"));
            visualizationSettingsMenuItem.addActionListener(e -> new VisualizationSettingsDialog().setVisible(true));
        }
        return visualizationSettingsMenuItem;
    }

    private JMenuItem getAnnualEnergyAnalysisMenuItem() {
        if (annualEnergyAnalysisMenuItem == null) {
            annualEnergyAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_energy_building"));
            annualEnergyAnalysisMenuItem.setAccelerator(KeyStroke.getKeyStroke("F3"));
            annualEnergyAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    if (SceneManager.getInstance().autoSelectBuilding(true) != null) {
                        new EnergyAnnualAnalysis().show(I18n.get("analysis.annual_energy"));
                    }
                }
            });
        }
        return annualEnergyAnalysisMenuItem;
    }

    private JMenuItem getAnnualEnergyAnalysisForSelectionMenuItem() {
        if (annualEnergyAnalysisForSelectionMenuItem == null) {
            annualEnergyAnalysisForSelectionMenuItem = new JMenuItem(I18n.get("menu.annual_energy_part"));
            annualEnergyAnalysisForSelectionMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Window
                            || selectedPart instanceof Wall
                            || selectedPart instanceof Roof
                            || selectedPart instanceof Door
                            || selectedPart instanceof SolarPanel
                            || selectedPart instanceof Rack
                            || selectedPart instanceof Foundation) {
                        new EnergyAnnualAnalysis().show(I18n.get("analysis.annual_energy_selected_part"));
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_selection"), I18n.get("msg.no_selection_title"), JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }
        return annualEnergyAnalysisForSelectionMenuItem;
    }

    private JMenuItem getDailyEnergyAnalysisMenuItem() {
        if (dailyEnergyAnalysisMenuItem == null) {
            dailyEnergyAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_energy_building"));
            dailyEnergyAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    if (SceneManager.getInstance().autoSelectBuilding(true) != null) {
                        final EnergyDailyAnalysis analysis = new EnergyDailyAnalysis();
                        if (SceneManager.getInstance().getSolarHeatMap()) {
                            analysis.updateGraph();
                        }
                        analysis.show(I18n.get("analysis.daily_energy"));
                    }
                }
            });
        }
        return dailyEnergyAnalysisMenuItem;
    }

    private JMenuItem getDailyEnergyAnalysisForSelectionMenuItem() {
        if (dailyEnergyAnalysisForSelectionMenuItem == null) {
            dailyEnergyAnalysisForSelectionMenuItem = new JMenuItem(I18n.get("menu.daily_energy_part"));
            dailyEnergyAnalysisForSelectionMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart instanceof Window
                            || selectedPart instanceof Wall
                            || selectedPart instanceof Roof
                            || selectedPart instanceof Door
                            || selectedPart instanceof SolarPanel
                            || selectedPart instanceof Rack
                            || selectedPart instanceof Foundation) {
                        new EnergyDailyAnalysis().show(I18n.get("analysis.daily_energy_selected_part"));
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_selection"), I18n.get("msg.no_selection_title"), JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }
        return dailyEnergyAnalysisForSelectionMenuItem;
    }

    private JMenuItem getAnnualPvAnalysisMenuItem() {
        if (annualPvAnalysisMenuItem == null) {
            annualPvAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_pv"));
            annualPvAnalysisMenuItem.setAccelerator(KeyStroke.getKeyStroke("F4"));
            annualPvAnalysisMenuItem.addActionListener(e -> TaskFactory.annualYieldAnalysisOfSolarPanels());
        }
        return annualPvAnalysisMenuItem;
    }

    private JMenuItem getDailyPvAnalysisMenuItem() {
        if (dailyPvAnalysisMenuItem == null) {
            dailyPvAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_pv"));
            dailyPvAnalysisMenuItem.addActionListener(e -> TaskFactory.dailyYieldAnalysisOfSolarPanels());
        }
        return dailyPvAnalysisMenuItem;
    }

    private JMenuItem getDailyHeliostatAnalysisMenuItem() {
        if (dailyHeliostatAnalysisMenuItem == null) {
            dailyHeliostatAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_heliostat"));
            dailyHeliostatAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(Mirror.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_heliostat"), I18n.get("msg.no_heliostat_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(Mirror.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_heliostat_on_foundation"), I18n.get("msg.no_heliostat_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    final HeliostatDailyAnalysis a = new HeliostatDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
        }
        return dailyHeliostatAnalysisMenuItem;
    }

    private JMenuItem getAnnualHeliostatAnalysisMenuItem() {
        if (annualHeliostatAnalysisMenuItem == null) {
            annualHeliostatAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_heliostat"));
            annualHeliostatAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(Mirror.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_heliostat"), I18n.get("msg.no_heliostat_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HeliostatAnnualAnalysis a = new HeliostatAnnualAnalysis();
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(Mirror.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_heliostat_on_foundation"), I18n.get("msg.no_heliostat_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    a.show();
                }
            });
        }
        return annualHeliostatAnalysisMenuItem;
    }

    private JMenuItem getDailyParabolicTroughAnalysisMenuItem() {
        if (dailyParabolicTroughAnalysisMenuItem == null) {
            dailyParabolicTroughAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_parabolic_trough"));
            dailyParabolicTroughAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(ParabolicTrough.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_trough"), I18n.get("msg.no_parabolic_trough_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(ParabolicTrough.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_trough_on_foundation"),
                                        I18n.get("msg.no_parabolic_trough_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    final ParabolicTroughDailyAnalysis a = new ParabolicTroughDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
        }
        return dailyParabolicTroughAnalysisMenuItem;
    }

    private JMenuItem getAnnualParabolicTroughAnalysisMenuItem() {
        if (annualParabolicTroughAnalysisMenuItem == null) {
            annualParabolicTroughAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_parabolic_trough"));
            annualParabolicTroughAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(ParabolicTrough.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_trough"), I18n.get("msg.no_parabolic_trough_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final ParabolicTroughAnnualAnalysis a = new ParabolicTroughAnnualAnalysis();
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(ParabolicTrough.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_trough_on_foundation"),
                                        I18n.get("msg.no_parabolic_trough_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    a.show();
                }
            });
        }
        return annualParabolicTroughAnalysisMenuItem;
    }

    private JMenuItem getDailyParabolicDishAnalysisMenuItem() {
        if (dailyParabolicDishAnalysisMenuItem == null) {
            dailyParabolicDishAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_parabolic_dish"));
            dailyParabolicDishAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(ParabolicDish.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_dish"), I18n.get("msg.no_parabolic_dish_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(ParabolicDish.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_dish_on_foundation"),
                                        I18n.get("msg.no_parabolic_dish_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    final ParabolicDishDailyAnalysis a = new ParabolicDishDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
        }
        return dailyParabolicDishAnalysisMenuItem;
    }

    private JMenuItem getAnnualParabolicDishAnalysisMenuItem() {
        if (annualParabolicDishAnalysisMenuItem == null) {
            annualParabolicDishAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_parabolic_dish"));
            annualParabolicDishAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(ParabolicDish.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_dish"), I18n.get("msg.no_parabolic_dish_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final ParabolicDishAnnualAnalysis a = new ParabolicDishAnnualAnalysis();
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(ParabolicDish.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_parabolic_dish_on_foundation"),
                                        I18n.get("msg.no_parabolic_dish_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    a.show();
                }
            });
        }
        return annualParabolicDishAnalysisMenuItem;
    }

    private JMenuItem getDailyFresnelReflectorAnalysisMenuItem() {
        if (dailyFresnelReflectorAnalysisMenuItem == null) {
            dailyFresnelReflectorAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_fresnel"));
            dailyFresnelReflectorAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(FresnelReflector.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_fresnel"), I18n.get("msg.no_fresnel_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(FresnelReflector.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_fresnel_on_foundation"),
                                        I18n.get("msg.no_fresnel_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    final FresnelReflectorDailyAnalysis a = new FresnelReflectorDailyAnalysis();
                    if (SceneManager.getInstance().getSolarHeatMap()) {
                        a.updateGraph();
                    }
                    a.show();
                }
            });
        }
        return dailyFresnelReflectorAnalysisMenuItem;
    }

    private JMenuItem getAnnualFresnelReflectorAnalysisMenuItem() {
        if (annualFresnelReflectorAnalysisMenuItem == null) {
            annualFresnelReflectorAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_fresnel"));
            annualFresnelReflectorAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    int n = Scene.getInstance().countParts(FresnelReflector.class);
                    if (n <= 0) {
                        JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_fresnel"), I18n.get("msg.no_fresnel_title"), JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final FresnelReflectorAnnualAnalysis a = new FresnelReflectorAnnualAnalysis();
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart != null) {
                        Foundation foundation;
                        if (selectedPart instanceof Foundation) {
                            foundation = (Foundation) selectedPart;
                        } else {
                            foundation = selectedPart.getTopContainer();
                        }
                        if (foundation != null) {
                            n = foundation.countParts(FresnelReflector.class);
                            if (n <= 0) {
                                JOptionPane.showMessageDialog(MainFrame.this, I18n.get("msg.no_fresnel_on_foundation"),
                                        I18n.get("msg.no_fresnel_title"), JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                        }
                    }
                    a.show();
                }
            });
        }
        return annualFresnelReflectorAnalysisMenuItem;
    }

    private JMenuItem getGroupDailyAnalysisMenuItem() {
        if (groupDailyAnalysisMenuItem == null) {
            groupDailyAnalysisMenuItem = new JMenuItem(I18n.get("menu.daily_group"));
            groupDailyAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final PartGroup g = new GroupSelector().select();
                    if (g != null) {
                        final GroupDailyAnalysis a = new GroupDailyAnalysis(g);
                        a.show(g.getType() + ": " + g.getIds());
                    }
                    SceneManager.getInstance().hideAllEditPoints();
                }
            });
        }
        return groupDailyAnalysisMenuItem;
    }

    private JMenuItem getGroupAnnualAnalysisMenuItem() {
        if (groupAnnualAnalysisMenuItem == null) {
            groupAnnualAnalysisMenuItem = new JMenuItem(I18n.get("menu.annual_group"));
            groupAnnualAnalysisMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    if (EnergyPanel.getInstance().adjustCellSize()) {
                        return;
                    }
                    final PartGroup g = new GroupSelector().select();
                    if (g != null) {
                        final GroupAnnualAnalysis a = new GroupAnnualAnalysis(g);
                        a.show(g.getType() + ": " + g.getIds());
                    }
                    SceneManager.getInstance().hideAllEditPoints();
                }
            });
        }
        return groupAnnualAnalysisMenuItem;
    }

    private JMenuItem getDailySensorMenuItem() {
        if (dailySensorMenuItem == null) {
            dailySensorMenuItem = new JMenuItem(I18n.get("menu.daily_sensor"));
            dailySensorMenuItem.addActionListener(e -> TaskFactory.dailySensorData());
        }
        return dailySensorMenuItem;
    }

    private JMenuItem getAnnualSensorMenuItem() {
        if (annualSensorMenuItem == null) {
            annualSensorMenuItem = new JMenuItem(I18n.get("menu.annual_sensor"));
            annualSensorMenuItem.addActionListener(e -> TaskFactory.annualSensorData());
        }
        return annualSensorMenuItem;
    }

    private JCheckBoxMenuItem getSolarRadiationHeatMapMenuItem() {
        if (solarRadiationHeatMapMenuItem == null) {
            solarRadiationHeatMapMenuItem = new JCheckBoxMenuItem(I18n.get("menu.solar_irradiance_heat_map"));
            solarRadiationHeatMapMenuItem.addActionListener(e -> {
                SceneManager.getInstance().setSolarHeatMap(solarRadiationHeatMapMenuItem.isSelected());
                Util.selectSilently(MainPanel.getInstance().getEnergyButton(), solarRadiationHeatMapMenuItem.isSelected());
            });
        }
        return solarRadiationHeatMapMenuItem;
    }

    private JCheckBoxMenuItem getOnlySolarComponentsInSolarMapMenuItem() {
        if (onlySolarComponentsInSolarMapMenuItem == null) {
            onlySolarComponentsInSolarMapMenuItem = new JCheckBoxMenuItem(I18n.get("menu.only_on_solar_components"));
            onlySolarComponentsInSolarMapMenuItem.addItemListener(e -> {
                final boolean b = onlySolarComponentsInSolarMapMenuItem.isSelected();
                Scene.getInstance().setOnlySolarComponentsInSolarMap(b);
                MainPanel.getInstance().getEnergyButton().setSelected(false);
                Scene.getInstance().setEdited(true);
                Scene.getInstance().redrawAll();
            });
        }
        return onlySolarComponentsInSolarMapMenuItem;
    }

    private JCheckBoxMenuItem getShowSolarLandMenuItem() {
        if (showSolarLandMenuItem == null) {
            showSolarLandMenuItem = new JCheckBoxMenuItem(I18n.get("menu.show_on_land"));
            showSolarLandMenuItem.addItemListener(e -> {
                // final ShowSolarLandCommand c = new ShowSolarLandCommand();
                final boolean b = showSolarLandMenuItem.isSelected();
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().getSolarLand().setVisible(b);
                    Scene.getInstance().setSolarMapForLand(b);
                    Scene.getInstance().redrawAll();
                    return null;
                });
                Scene.getInstance().setEdited(true);
                MainPanel.getInstance().getEnergyButton().setSelected(false);
                // SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return showSolarLandMenuItem;
    }

    private JCheckBoxMenuItem getSolarAbsorptionHeatMapMenuItem() {
        if (solarAbsorptionHeatMapMenuItem == null) {
            solarAbsorptionHeatMapMenuItem = new JCheckBoxMenuItem(I18n.get("menu.only_absorbed_energy"));
            solarAbsorptionHeatMapMenuItem.addActionListener(e -> {
                Scene.getInstance().setOnlyAbsorptionInSolarMap(solarAbsorptionHeatMapMenuItem.isSelected());
                if (SceneManager.getInstance().getSolarHeatMap()) {
                    SceneManager.getInstance().setSolarHeatMap(true);
                }
            });
        }
        return solarAbsorptionHeatMapMenuItem;
    }

    private JCheckBoxMenuItem getOnlyReflectionHeatMapMenuItem() {
        if (onlyReflectionHeatMapMenuItem == null) {
            onlyReflectionHeatMapMenuItem = new JCheckBoxMenuItem(I18n.get("menu.only_reflected_energy"));
            onlyReflectionHeatMapMenuItem.addActionListener(e -> {
                Scene.getInstance().setOnlyReflectedEnergyInMirrorSolarMap(onlyReflectionHeatMapMenuItem.isSelected());
                if (SceneManager.getInstance().getSolarHeatMap()) {
                    SceneManager.getInstance().setSolarHeatMap(true);
                }
            });
        }
        return onlyReflectionHeatMapMenuItem;
    }

    public JCheckBoxMenuItem getHeatFluxMenuItem() {
        if (showHeatFluxVectorsMenuItem == null) {
            showHeatFluxVectorsMenuItem = new JCheckBoxMenuItem(I18n.get("menu.heat_flux_vectors"));
            showHeatFluxVectorsMenuItem.addActionListener(e -> {
                final ShowHeatFluxCommand c = new ShowHeatFluxCommand();
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setAlwaysComputeHeatFluxVectors(showHeatFluxVectorsMenuItem.isSelected());
                    return null;
                });
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return showHeatFluxVectorsMenuItem;
    }

    private JMenuItem getFinancialSettingsMenuItem() {
        if (financialSettingsMenuItem == null) {
            financialSettingsMenuItem = new JMenuItem(I18n.get("menu.financial_settings"));
            financialSettingsMenuItem.addActionListener(e -> {
                FinancialSettingsDialog dialog = new FinancialSettingsDialog();
                switch (Scene.getInstance().getProjectType()) {
                    case Foundation.TYPE_PV_PROJECT:
                        dialog.selectPvPrices();
                        break;
                    case Foundation.TYPE_CSP_PROJECT:
                        dialog.selectCspPrices();
                        break;
                }
                dialog.setVisible(true);
            });
        }
        return financialSettingsMenuItem;
    }

    private JMenuItem getCostBreakdownMenuItem() {
        if (costBreakdownMenuItem == null) {
            costBreakdownMenuItem = new JMenuItem(I18n.get("menu.cost_breakdown"));
            costBreakdownMenuItem.setAccelerator(KeyStroke.getKeyStroke("F7"));
            costBreakdownMenuItem.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                int i;
                if (selectedPart == null) {
                    i = Scene.getInstance().getProjectType();
                } else {
                    final Foundation f = selectedPart instanceof Foundation ? (Foundation) selectedPart : selectedPart.getTopContainer();
                    i = f.getProjectType();
                }
                switch (i) {
                    case Foundation.TYPE_PV_PROJECT:
                        PvProjectCost.getInstance().showGraph();
                        break;
                    case Foundation.TYPE_CSP_PROJECT:
                        CspProjectCost.getInstance().showGraph();
                        break;
                    default:
                        BuildingCost.getInstance().showGraph();
                }
            });
        }
        return costBreakdownMenuItem;
    }

    private JMenuItem getMonthlySunshineHoursMenuItem() {
        if (monthlySunshineHoursMenuItem == null) {
            monthlySunshineHoursMenuItem = new JMenuItem(I18n.get("menu.monthly_sunshine"));
            monthlySunshineHoursMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    new MonthlySunshineHours().showDialog();
                }
            });
        }
        return monthlySunshineHoursMenuItem;
    }

    private JMenuItem getAnnualEnvironmentalTemperatureMenuItem() {
        if (annualEnvironmentalTemperatureMenuItem == null) {
            annualEnvironmentalTemperatureMenuItem = new JMenuItem(I18n.get("menu.annual_env_temp"));
            annualEnvironmentalTemperatureMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    new AnnualEnvironmentalTemperature().showDialog();
                }
            });
        }
        return annualEnvironmentalTemperatureMenuItem;
    }

    private JMenuItem getDailyEnvironmentalTemperatureMenuItem() {
        if (dailyEnvironmentalTemperatureMenuItem == null) {
            dailyEnvironmentalTemperatureMenuItem = new JMenuItem(I18n.get("menu.daily_env_temp"));
            dailyEnvironmentalTemperatureMenuItem.addActionListener(e -> {
                if (EnergyPanel.getInstance().checkRegion()) {
                    new DailyEnvironmentalTemperature().showDialog();
                }
            });
        }
        return dailyEnvironmentalTemperatureMenuItem;
    }

    private JCheckBoxMenuItem getAnnotationsMenuItem() {
        if (annotationsMenuItem == null) {
            annotationsMenuItem = new JCheckBoxMenuItem(I18n.get("menu.annotations"));
            annotationsMenuItem.addItemListener(e -> {
                final ShowAnnotationCommand c = new ShowAnnotationCommand();
                SceneManager.getTaskManager().update(() -> {
                    Scene.getInstance().setAnnotationsVisible(annotationsMenuItem.isSelected());
                    return null;
                });
                ((Component) SceneManager.getInstance().getCanvas()).requestFocusInWindow();
                Scene.getInstance().setEdited(true);
                SceneManager.getInstance().getUndoManager().addEdit(c);
                Util.selectSilently(MainPanel.getInstance().getAnnotationButton(), annotationsMenuItem.isSelected());
            });

        }
        return annotationsMenuItem;
    }

    private JCheckBoxMenuItem getAnnotationsInwardMenuItem() {
        if (annotationsInwardMenuItem == null) {
            annotationsInwardMenuItem = new JCheckBoxMenuItem(I18n.get("menu.annotations_cutouts"));
            annotationsInwardMenuItem.addItemListener(e -> SceneManager.getTaskManager().update(() -> {
                Scene.setDrawAnnotationsInside(annotationsInwardMenuItem.isSelected());
                return null;
            }));
        }
        return annotationsInwardMenuItem;
    }

    JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu(I18n.get("menu.edit"));
            editMenu.addMenuListener(new MenuListener() {

                private void enableMenuItems() {
                    cutMenuItem.setEnabled(true);
                    copyMenuItem.setEnabled(true);
                    pasteMenuItem.setEnabled(true);
                    removeAllEditLocksMenuItem.setEnabled(true);
                    specificationsMenuItem.setEnabled(true);
                    autoRecomputeEnergyMenuItem.setEnabled(true);
                    sortIdMenuItem.setEnabled(true);
                }

                @Override
                public void menuCanceled(final MenuEvent e) {
                    // enable the cut-copy-paste menu items when the menu disappears, otherwise the keystrokes will be disabled with the menu items
                    enableMenuItems();
                }

                @Override
                public void menuDeselected(final MenuEvent e) {
                    SceneManager.getInstance().refresh();
                    // enable the cut-copy-paste menu items when the menu disappears, otherwise the keystrokes will be disabled with the menu items
                    enableMenuItems();
                }

                @Override
                public void menuSelected(final MenuEvent e) {
                    enableMenuItems();
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    cutMenuItem.setEnabled(selectedPart != null);
                    copyMenuItem.setEnabled(selectedPart != null && selectedPart.isCopyable());
                    final HousePart copyBuffer = Scene.getInstance().getCopyBuffer();
                    pasteMenuItem.setEnabled(copyBuffer != null && !(copyBuffer instanceof Foundation));
                    Util.selectSilently(noteCheckBoxMenuItem, MainPanel.getInstance().isNoteVisible());
                    Util.selectSilently(infoPanelCheckBoxMenuItem, EnergyPanel.getInstance().isVisible());
                    Util.selectSilently(snapToGridsMenuItem, Scene.getInstance().isSnapToGrids());
                    MainPanel.getInstance().defaultTool();
                    if (Scene.getInstance().isStudentMode()) {
                        removeAllEditLocksMenuItem.setEnabled(false);
                        specificationsMenuItem.setEnabled(false);
                        autoRecomputeEnergyMenuItem.setEnabled(false);
                        sortIdMenuItem.setEnabled(false);
                    }
                    refreshUndoRedo();
                }
            });

            final JMenu clearMenu = new JMenu(I18n.get("menu.clear"));
            clearMenu.add(getRemoveAllFoundationsMenuItem());
            clearMenu.add(getRemoveAllWallsMenuItem());
            clearMenu.add(getRemoveAllWindowsMenuItem());
            clearMenu.add(getRemoveAllWindowShuttersMenuItem());
            clearMenu.add(getRemoveAllSolarPanelsMenuItem());
            clearMenu.add(getRemoveAllRacksMenuItem());
            clearMenu.add(getRemoveAllHeliostatsMenuItem());
            clearMenu.add(getRemoveAllParabolicTroughsMenuItem());
            clearMenu.add(getRemoveAllParabolicDishesMenuItem());
            clearMenu.add(getRemoveAllFresnelReflectorsMenuItem());
            clearMenu.add(getRemoveAllTreesMenuItem());
            clearMenu.add(getRemoveAllHumansMenuItem());
            clearMenu.add(getRemoveAllRoofsMenuItem());
            clearMenu.add(getRemoveAllFloorsMenuItem());
            clearMenu.add(getRemoveAllSensorsMenuItem());
            clearMenu.add(getRemoveAllEditLocksMenuItem());
            clearMenu.add(getRemoveAllUtilityBillsMenuItem());

            final JMenu moveMenu = new JMenu(I18n.get("menu.move"));
            moveMenu.add(getMoveEastMenuItem());
            moveMenu.add(getMoveWestMenuItem());
            moveMenu.add(getMoveNorthMenuItem());
            moveMenu.add(getMoveSouthMenuItem());

            final JMenu rotateMenu = new JMenu(I18n.get("menu.rotate"));
            rotateMenu.add(getRotate180MenuItem());
            rotateMenu.add(getRotate90CwMenuItem());
            rotateMenu.add(getRotate90CcwMenuItem());

            final JMenu enableEditPointsMenu = new JMenu(I18n.get("menu.enable_edit_points"));
            enableEditPointsMenu.add(getEnableAllBaseEditPointsMenuItem());
            enableEditPointsMenu.add(getEnableAllTreeEditPointsMenuItem());
            final JMenu disableEditPointsMenu = new JMenu(I18n.get("menu.disable_edit_points"));
            disableEditPointsMenu.add(getDisableAllBaseEditPointsMenuItem());
            disableEditPointsMenu.add(getDisableAllTreeEditPointsMenuItem());

            editMenu.add(getUndoMenuItem());
            editMenu.add(getRedoMenuItem());
            editMenu.addSeparator();
            editMenu.add(getCutMenuItem());
            editMenu.add(getCopyMenuItem());
            editMenu.add(getPasteMenuItem());
            editMenu.addSeparator();
            editMenu.add(moveMenu);
            editMenu.add(rotateMenu);
            editMenu.add(clearMenu);
            editMenu.addSeparator();
            editMenu.add(enableEditPointsMenu);
            editMenu.add(disableEditPointsMenu);
            editMenu.addSeparator();
            editMenu.add(getSnapToGridsMenuItem());
            editMenu.add(getSnapMenuItem());
            editMenu.add(getAutoRecomputeEnergyMenuItem());
            editMenu.addSeparator();
            editMenu.add(getSpecificationsMenuItem());
            editMenu.add(getOverallUtilityBillMenuItem());
            editMenu.add(getSetRegionMenuItem());
            editMenu.add(getEnvironmentMenu());
            editMenu.addSeparator();
            editMenu.add(getPropertiesMenuItem());
        }
        return editMenu;
    }

    public void refreshUndoRedo() {
        final MyUndoManager um = SceneManager.getInstance().getUndoManager();
        final UndoableEdit lastEdit = um.lastEdit();
        long timestampUndo = -1;
        long timestampRedo = -1;
        if (lastEdit instanceof MyAbstractUndoableEdit) {
            if (um.editToBeUndone() != null) {
                timestampUndo = ((MyAbstractUndoableEdit) um.editToBeUndone()).getTimestamp();
            }
            if (um.editToBeRedone() != null) {
                timestampRedo = ((MyAbstractUndoableEdit) um.editToBeRedone()).getTimestamp();
            }
        }
        getUndoMenuItem().setText(um.getUndoPresentationName() + (timestampUndo == -1 ? "" : " (" + EnergyPanel.ONE_DECIMAL.format(0.001 * (System.currentTimeMillis() - timestampUndo)) + " seconds ago)"));
        getUndoMenuItem().setEnabled(um.canUndo());
        getRedoMenuItem().setText(um.getRedoPresentationName() + (timestampRedo == -1 ? "" : " (" + EnergyPanel.ONE_DECIMAL.format(0.001 * (System.currentTimeMillis() - timestampRedo)) + " seconds ago)"));
        getRedoMenuItem().setEnabled(um.canRedo());
    }

    private JMenuItem getUndoMenuItem() {
        if (undoMenuItem == null) {
            undoMenuItem = new JMenuItem(I18n.get("menu.undo"));
            undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            undoMenuItem.setEnabled(false);
            undoMenuItem.addActionListener(e -> {
                MainPanel.getInstance().defaultTool();
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().hideAllEditPoints();
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().undo();
                EnergyPanel.getInstance().update();
            });
        }
        return undoMenuItem;
    }

    private JMenuItem getRedoMenuItem() {
        if (redoMenuItem == null) {
            redoMenuItem = new JMenuItem(I18n.get("menu.redo"));
            redoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            redoMenuItem.setEnabled(false);
            redoMenuItem.addActionListener(e -> {
                MainPanel.getInstance().defaultTool();
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().hideAllEditPoints();
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().redo();
                EnergyPanel.getInstance().update();
            });
        }
        return redoMenuItem;
    }

    private JMenuItem getCutMenuItem() {
        if (cutMenuItem == null) {
            cutMenuItem = new JMenuItem(I18n.get("menu.cut"));
            cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            cutMenuItem.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart != null) {
                    Scene.getInstance().setCopyBuffer(selectedPart);
                    SceneManager.getInstance().deleteCurrentSelection(); // this runs in the Task Manager thread
                }
            });
        }
        return cutMenuItem;
    }

    private JMenuItem getCopyMenuItem() {
        if (copyMenuItem == null) {
            copyMenuItem = new JMenuItem(I18n.get("menu.copy"));
            copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            copyMenuItem.addActionListener(e -> {
                final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                if (selectedPart != null) {
                    Scene.getInstance().setCopyBuffer(selectedPart);
                }
            });
        }
        return copyMenuItem;
    }

    private JMenuItem getPasteMenuItem() {
        if (pasteMenuItem == null) {
            pasteMenuItem = new JMenuItem(I18n.get("menu.paste"));
            pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Config.isMac() ? KeyEvent.META_MASK : InputEvent.CTRL_MASK));
            pasteMenuItem.addActionListener(e -> SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().paste();
                return null;
            }));
        }
        return pasteMenuItem;

    }

    private void save() {
        try {
            final URL url = Scene.getURL();
            if (url != null) {
                if (Scene.isInternalFile()) {
                    saveFile(true);
                } else {
                    Scene.saveOutsideTaskManager(url);
                    Scene.getInstance().setEdited(false);
                }
            } else {
                saveFile(true);
            }
        } catch (final Throwable err) {
            err.printStackTrace();
            JOptionPane.showMessageDialog(this, err.getMessage(), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    void saveFile(final boolean outsideTaskManager) {
        final File file = FileChooser.getInstance().showDialog(".ng3", FileChooser.ng3Filter, true);
        if (file == null) {
            return;
        }
        URL url;
        try {
            url = file.toURI().toURL();
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            return;
        }
        if (outsideTaskManager) {
            Scene.saveOutsideTaskManager(url);
        } else {
            Scene.save(url, true);
        }
        EventQueue.invokeLater(() -> {
            Scene.getInstance().setEdited(false);
            updateTitleBar();
            FileChooser.getInstance().rememberFile(file.getAbsolutePath());
        });
    }

    void importFile() {
        final File file = FileChooser.getInstance().showDialog(".ng3", FileChooser.ng3Filter, false);
        if (file != null) {
            EnergyPanel.getInstance().updateRadiationHeatMap();
            SceneManager.getTaskManager().update(() -> {
                try {
                    Scene.getInstance().importFile(file.toURI().toURL());
                } catch (final Throwable err) {
                    BugReporter.report(err);
                }
                return null;
            });
        }
    }

    void importColladaFile() {
        final File file = FileChooser.getInstance().showDialog(".dae", FileChooser.daeFilter, false);
        if (file != null) {
            EnergyPanel.getInstance().updateRadiationHeatMap();
            SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().importCollada(file);
                return null;
            });
        }
    }

    private void exportObjFile() {
        final File file = FileChooser.getInstance().showDialog(".obj", FileChooser.objFilter, true);
        if (file != null) {
            EnergyPanel.getInstance().updateRadiationHeatMap();
            SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().exportObj(file);
                return null;
            });
        }
    }

    private JMenuItem getPageSetupMenuItem() {
        if (pageSetupMenuItem == null) {
            pageSetupMenuItem = new JMenuItem(I18n.get("menu.page_setup"));
            pageSetupMenuItem.addActionListener(e -> PrintController.getInstance().pageSetup());
        }
        return pageSetupMenuItem;
    }

    private JRadioButtonMenuItem getScaleToFitRadioButtonMenuItem() {
        if (scaleToFitRadioButtonMenuItem == null) {
            scaleToFitRadioButtonMenuItem = new JRadioButtonMenuItem(I18n.get("menu.scale_to_fit_paper"));
            scaleToFitRadioButtonMenuItem.addActionListener(e -> PrintController.getInstance().setScaleToFit(true));
            printSizeOptionBbuttonGroup.add(scaleToFitRadioButtonMenuItem);
            scaleToFitRadioButtonMenuItem.setSelected(true);
        }
        return scaleToFitRadioButtonMenuItem;
    }

    private JRadioButtonMenuItem getExactSizeRadioButtonMenuItem() {
        if (exactSizeRadioButtonMenuItem == null) {
            exactSizeRadioButtonMenuItem = new JRadioButtonMenuItem(I18n.get("menu.exact_size_paper"));
            exactSizeRadioButtonMenuItem.addActionListener(e -> PrintController.getInstance().setScaleToFit(false));
            printSizeOptionBbuttonGroup.add(exactSizeRadioButtonMenuItem);
        }
        return exactSizeRadioButtonMenuItem;
    }

    private JMenuItem getImportMenuItem() {
        if (importMenuItem == null) {
            importMenuItem = new JMenuItem(I18n.get("menu.import"));
            importMenuItem.setToolTipText(I18n.get("tooltip.import_energy3d"));
            importMenuItem.addActionListener(e -> importFile());
        }
        return importMenuItem;
    }

    private JMenuItem getImportColladaMenuItem() {
        if (importColladaMenuItem == null) {
            importColladaMenuItem = new JMenuItem(I18n.get("menu.import_collada"));
            importColladaMenuItem.setToolTipText(I18n.get("tooltip.import_collada"));
            importColladaMenuItem.addActionListener(e -> importColladaFile());
        }
        return importColladaMenuItem;
    }

    private JMenuItem getExportModelMenuItem() {
        if (exportModelMenuItem == null) {
            exportModelMenuItem = new JMenuItem(I18n.get("menu.export_model"));
            exportModelMenuItem.addActionListener(e -> exportObjFile());
        }
        return exportModelMenuItem;
    }

    private JCheckBoxMenuItem getSnapMenuItem() {
        if (snapMenuItem == null) {
            snapMenuItem = new JCheckBoxMenuItem(I18n.get("menu.snap_walls"));
            snapMenuItem.setSelected(true);
            snapMenuItem.addActionListener(e -> HousePart.setSnapToObjects(snapMenuItem.isSelected()));
        }
        return snapMenuItem;
    }

    private JCheckBoxMenuItem getSnapToGridsMenuItem() {
        if (snapToGridsMenuItem == null) {
            snapToGridsMenuItem = new JCheckBoxMenuItem(I18n.get("menu.snap_to_grids"));
            snapToGridsMenuItem.setSelected(true);
            snapToGridsMenuItem.addActionListener(e -> Scene.getInstance().setSnapToGrids(snapToGridsMenuItem.isSelected()));
        }
        return snapToGridsMenuItem;
    }

    public JCheckBoxMenuItem getTopViewCheckBoxMenuItem() {
        if (topViewCheckBoxMenuItem == null) {
            topViewCheckBoxMenuItem = new JCheckBoxMenuItem(I18n.get("menu.top_view"));
            topViewCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            topViewCheckBoxMenuItem.addActionListener(e -> {
                final TopViewCommand c = new TopViewCommand();
                final boolean isTopView = topViewCheckBoxMenuItem.isSelected();
                SceneManager.getTaskManager().update(() -> {
                    if (isTopView) {
                        Scene.saveCameraLocation();
                        SceneManager.getInstance().resetCamera(ViewMode.TOP_VIEW);
                    } else {
                        SceneManager.getInstance().resetCamera(ViewMode.NORMAL);
                        Scene.loadCameraLocation();
                    }
                    SceneManager.getInstance().refresh();
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return topViewCheckBoxMenuItem;
    }

    private JMenuItem getZoomInMenuItem() {
        if (zoomInMenuItem == null) {
            zoomInMenuItem = new JMenuItem(I18n.get("menu.zoom_in"));
            zoomInMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            zoomInMenuItem.addActionListener(e -> {
                final ZoomCommand c = new ZoomCommand(true);
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().zoom(true);
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return zoomInMenuItem;
    }

    private JMenuItem getZoomOutMenuItem() {
        if (zoomOutMenuItem == null) {
            zoomOutMenuItem = new JMenuItem(I18n.get("menu.zoom_out"));
            zoomOutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            zoomOutMenuItem.addActionListener(e -> {
                final ZoomCommand c = new ZoomCommand(false);
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().zoom(false);
                    return null;
                });
                SceneManager.getInstance().getUndoManager().addEdit(c);
            });
        }
        return zoomOutMenuItem;
    }

    private JRadioButtonMenuItem getDefaultMenuItem() {
        if (defaultMenuItem == null) {
            defaultMenuItem = new JRadioButtonMenuItem(I18n.get("menu.default_env"));
            defaultMenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.DEFAULT_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            themeButtonGroup.add(defaultMenuItem);
        }
        return defaultMenuItem;
    }

    private JRadioButtonMenuItem getDesertMenuItem() {
        if (desertMenuItem == null) {
            desertMenuItem = new JRadioButtonMenuItem(I18n.get("menu.desert"));
            desertMenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.DESERT_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            themeButtonGroup.add(desertMenuItem);
        }
        return desertMenuItem;
    }

    private JRadioButtonMenuItem getGrasslandMenuItem() {
        if (grasslandMenuItem == null) {
            grasslandMenuItem = new JRadioButtonMenuItem(I18n.get("menu.grassland"));
            grasslandMenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.GRASSLAND_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            themeButtonGroup.add(grasslandMenuItem);
        }
        return grasslandMenuItem;
    }

    private JRadioButtonMenuItem getForestMenuItem() {
        if (forestMenuItem == null) {
            forestMenuItem = new JRadioButtonMenuItem(I18n.get("menu.forest"));
            forestMenuItem.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    final ChangeEnvironmentCommand c = new ChangeEnvironmentCommand();
                    SceneManager.getTaskManager().update(() -> {
                        Scene.getInstance().setEnvironment(Scene.FOREST_THEME);
                        return null;
                    });
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(c);
                }
            });
            themeButtonGroup.add(forestMenuItem);
        }
        return forestMenuItem;
    }

    void showColorDialogForParts() {
        final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
        ActionListener colorActionListener;
        if (selectedPart == null) {
            final ReadOnlyColorRGBA color = Scene.getInstance().getLandColor();
            if (color != null) {
                colorChooser.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
            }
            colorActionListener = e -> {
                final float[] newColor = colorChooser.getColor().getComponents(null);
                final ColorRGBA rgba = new ColorRGBA(newColor[0], newColor[1], newColor[2], 0.5f);
                if (!Scene.getInstance().getLandColor().equals(rgba)) {
                    final ChangeLandColorCommand cmd = new ChangeLandColorCommand();
                    Scene.getInstance().setLandColor(rgba);
                    Scene.getInstance().setEdited(true);
                    SceneManager.getInstance().getUndoManager().addEdit(cmd);
                }
            };
        } else {
            if (selectedPart.getTextureType() != HousePart.TEXTURE_NONE && selectedPart.getTextureType() != HousePart.TEXTURE_EDGE) { // when the user wants to set the color, automatically switch to no texture
                if (JOptionPane.showConfirmDialog(this, I18n.get("msg.remove_texture_for_color"), I18n.get("dialog.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            final ReadOnlyColorRGBA color = selectedPart.getColor();
            if (color != null) {
                colorChooser.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
            }
            colorActionListener = new ActionListener() {

                private boolean changed;

                @Override
                public void actionPerformed(final ActionEvent e) {
                    final HousePart selectedPart = SceneManager.getInstance().getSelectedPart();
                    if (selectedPart == null) {
                        return;
                    }
                    final Color c = colorChooser.getColor();
                    final float[] newColor = c.getComponents(null);
                    final boolean restartPrintPreview = Scene.getInstance().getDefaultRoofColor().equals(ColorRGBA.WHITE) || c.equals(Color.WHITE);
                    final ColorRGBA color = new ColorRGBA(newColor[0], newColor[1], newColor[2], newColor[3]);
                    final JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                    panel.setBorder(BorderFactory.createTitledBorder(I18n.get("scope.apply_to")));

                    if (selectedPart instanceof Wall) {

                        final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_wall"), true);
                        final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_walls_connected"));
                        final JRadioButton rb3 = new JRadioButton(I18n.get("scope.all_walls_building"));
                        final JRadioButton rb4 = new JRadioButton(I18n.get("scope.all_walls"));
                        panel.add(rb1);
                        panel.add(rb2);
                        panel.add(rb3);
                        panel.add(rb4);
                        final ButtonGroup bg = new ButtonGroup();
                        bg.add(rb1);
                        bg.add(rb2);
                        bg.add(rb3);
                        bg.add(rb4);
                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.wall_color"));
                        while (true) {
                            changed = false;
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1]) {
                                break;
                            } else {
                                changed = !color.equals(selectedPart.getColor());
                                if (rb1.isSelected()) { // apply to only this part
                                    if (changed) {
                                        final ChangePartColorCommand cmd = new ChangePartColorCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            selectedPart.setColor(color);
                                            selectedPart.setTextureType(HousePart.TEXTURE_NONE);
                                            selectedPart.draw();
                                            SceneManager.getInstance().refresh();
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                } else if (rb2.isSelected()) {
                                    final Wall w = (Wall) selectedPart;
                                    if (!changed) {
                                        w.visitNeighbors((currentWall, prev, next) -> {
                                            if (!color.equals(currentWall.getColor())) {
                                                changed = true;
                                            }
                                        });
                                    }
                                    if (changed) {
                                        final ChangeColorOfConnectedWallsCommand cmd = new ChangeColorOfConnectedWallsCommand(w);
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setColorOfConnectedWalls(w, color);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                } else if (rb3.isSelected()) {
                                    if (!changed) {
                                        for (final HousePart x : Scene.getInstance().getPartsOfSameTypeInBuilding(selectedPart)) {
                                            if (!color.equals(x.getColor())) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (changed) {
                                        final ChangeBuildingColorCommand cmd = new ChangeBuildingColorCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setPartColorOfBuilding(selectedPart, color);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                } else if (rb4.isSelected()) {
                                    if (!changed) {
                                        for (final HousePart x : Scene.getInstance().getAllPartsOfSameType(selectedPart)) {
                                            if (!color.equals(x.getColor())) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (changed) {
                                        final ChangeColorOfAllPartsOfSameTypeCommand cmd = new ChangeColorOfAllPartsOfSameTypeCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setColorOfAllPartsOfSameType(selectedPart, color);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                }
                                Scene.getInstance().setDefaultWallColor(color); // remember the color decision for the next wall to be added
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }

                    } else if (selectedPart instanceof Roof) {

                        final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_roof"), true);
                        final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_roofs"));
                        panel.add(rb1);
                        panel.add(rb2);
                        final ButtonGroup bg = new ButtonGroup();
                        bg.add(rb1);
                        bg.add(rb2);
                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.roof_color"));
                        while (true) {
                            changed = false;
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1]) {
                                break;
                            } else {
                                changed = !color.equals(selectedPart.getColor());
                                if (rb1.isSelected()) { // apply to only this part
                                    if (changed) {
                                        final ChangePartColorCommand cmd = new ChangePartColorCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            selectedPart.setColor(color);
                                            selectedPart.setTextureType(HousePart.TEXTURE_NONE);
                                            selectedPart.draw();
                                            SceneManager.getInstance().refresh();
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                } else {
                                    if (!changed) {
                                        for (final HousePart x : Scene.getInstance().getAllPartsOfSameType(selectedPart)) {
                                            if (!color.equals(x.getColor())) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (changed) {
                                        final ChangeColorOfAllPartsOfSameTypeCommand cmd = new ChangeColorOfAllPartsOfSameTypeCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setColorOfAllPartsOfSameType(selectedPart, color);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                }
                                Scene.getInstance().setDefaultRoofColor(color); // remember the color decision for the next roof to be added
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }

                    } else if (selectedPart instanceof Foundation) {

                        final JRadioButton rb1 = new JRadioButton(I18n.get("scope.only_this_foundation"), true);
                        final JRadioButton rb2 = new JRadioButton(I18n.get("scope.all_foundations"));
                        panel.add(rb1);
                        panel.add(rb2);
                        final ButtonGroup bg = new ButtonGroup();
                        bg.add(rb1);
                        bg.add(rb2);
                        final Object[] options = new Object[]{I18n.get("dialog.ok"), I18n.get("dialog.cancel"), I18n.get("common.apply")};
                        final JOptionPane optionPane = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_CANCEL_OPTION, null, options, options[2]);
                        final JDialog dialog = optionPane.createDialog(MainFrame.getInstance(), I18n.get("dialog.foundation_color"));
                        while (true) {
                            changed = false;
                            dialog.setVisible(true);
                            final Object choice = optionPane.getValue();
                            if (choice == options[1]) {
                                break;
                            } else {
                                changed = !color.equals(selectedPart.getColor());
                                if (rb1.isSelected()) { // apply to only this part
                                    if (changed) {
                                        final ChangePartColorCommand cmd = new ChangePartColorCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            selectedPart.setColor(color);
                                            selectedPart.setTextureType(HousePart.TEXTURE_NONE);
                                            selectedPart.draw();
                                            SceneManager.getInstance().refresh();
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                } else {
                                    if (!changed) {
                                        for (final HousePart x : Scene.getInstance().getAllPartsOfSameType(selectedPart)) {
                                            if (!color.equals(x.getColor())) {
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (changed) {
                                        final ChangeColorOfAllPartsOfSameTypeCommand cmd = new ChangeColorOfAllPartsOfSameTypeCommand(selectedPart);
                                        SceneManager.getTaskManager().update(() -> {
                                            Scene.getInstance().setColorOfAllPartsOfSameType(selectedPart, color);
                                            return null;
                                        });
                                        SceneManager.getInstance().getUndoManager().addEdit(cmd);
                                    }
                                }
                                Scene.getInstance().setDefaultFoundationColor(color); // remember the color decision for the next foundation to be added
                                if (choice == options[0]) {
                                    break;
                                }
                            }
                        }

                    } else {
                        changed = !color.equals(selectedPart.getColor());
                        if (changed) {
                            final ChangePartColorCommand cmd = new ChangePartColorCommand(selectedPart);
                            SceneManager.getTaskManager().update(() -> {
                                selectedPart.setColor(color);
                                selectedPart.setTextureType(HousePart.TEXTURE_NONE);
                                selectedPart.draw();
                                SceneManager.getInstance().refresh();
                                return null;
                            });
                            SceneManager.getInstance().getUndoManager().addEdit(cmd);
                            if (selectedPart instanceof Door) { // remember the color decision for the next part
                                Scene.getInstance().setDefaultDoorColor(color);
                            } else if (selectedPart instanceof Floor) {
                                Scene.getInstance().setDefaultFloorColor(color);
                            }
                        }
                    }

                    if (restartPrintPreview && PrintController.getInstance().isPrintPreview()) {
                        PrintController.getInstance().restartAnimation();
                    }
                    MainPanel.getInstance().getEnergyButton().setSelected(false);
                    Scene.getInstance().setEdited(changed);
                }

            };
        }
        JColorChooser.createDialog(this, I18n.get("dialog.select_color"), true, colorChooser, colorActionListener, null).setVisible(true);
    }

    public void open(final String filename) {
        SceneManager.getTaskManager().update(() -> {
            try {
                Scene.open(new File(filename).toURI().toURL());
                EventQueue.invokeLater(() -> FileChooser.getInstance().rememberFile(filename));
            } catch (final Throwable e) {
                BugReporter.report(e, new File(filename).getAbsolutePath());
                throw e;
            }
            return null;
        });
    }

    void openModel(final URL url) {
        boolean ok = false;
        if (Scene.getInstance().isEdited()) {
            final int save = JOptionPane.showConfirmDialog(MainFrame.this, I18n.get("msg.save_changes"), I18n.get("menu.save"),
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (save == JOptionPane.YES_OPTION) {
                save();
                if (!Scene.getInstance().isEdited()) {
                    ok = true;
                }
            } else if (save != JOptionPane.CANCEL_OPTION) {
                ok = true;
            }
        } else {
            ok = true;
        }
        if (ok) {
            try {
                SceneManager.getTaskManager().update(() -> {
                    Scene.open(url);
                    return null;
                });
            } catch (final Throwable e) {
                BugReporter.report(e);
            }
        }
    }

    public void exit() {
        if (!MainApplication.runFromOnlyJar()) {
            final String[] recentFiles = FileChooser.getInstance().getRecentFiles();
            if (recentFiles != null) {
                final int n = recentFiles.length;
                if (n > 0) {
                    final Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
                    for (int i = 0; i < n; i++) {
                        pref.put("Recent File " + i, recentFiles[n - i - 1]);
                    }
                }
            }
        }
        if (Scene.getInstance().isEdited()) {
            final int save = JOptionPane.showConfirmDialog(this, I18n.get("msg.save_changes"), I18n.get("menu.save"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (save == JOptionPane.YES_OPTION) {
                save();
                while (Scene.isSaving()) {
                    Thread.yield();
                }
                if (!Scene.getInstance().isEdited()) {
                    MainApplication.exit();
                }
            } else if (save != JOptionPane.CANCEL_OPTION) {
                MainApplication.exit();
            }
        } else {
            MainApplication.exit();
        }
    }

    private JMenuItem getExportLogMenuItem() {
        if (exportLogMenuItem == null) {
            exportLogMenuItem = new JMenuItem(I18n.get("menu.export_log"));
            exportLogMenuItem.addActionListener(e -> {
                final File file = FileChooser.getInstance().showDialog(".zip", FileChooser.zipFilter, true);
                if (file == null) {
                    return;
                }
                try {
                    new LogZipper(file).createDialog();
                } catch (final Throwable err) {
                    err.printStackTrace();
                    JOptionPane.showMessageDialog(MainFrame.this, err.getMessage(), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
                }
            });
        }
        return exportLogMenuItem;
    }

    private JMenuItem getCopyImageMenuItem() {
        if (copyImageMenuItem == null) {
            copyImageMenuItem = new JMenuItem(I18n.get("menu.copy_image"));
            copyImageMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, (Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK) | KeyEvent.ALT_MASK));
            copyImageMenuItem.addActionListener(e -> new ClipImage().copyImageToClipboard(MainPanel.getInstance().getCanvasPanel()));
        }
        return copyImageMenuItem;
    }

    private JMenuItem getExportImageMenuItem() {
        if (exportImageMenuItem == null) {
            exportImageMenuItem = new JMenuItem(I18n.get("menu.export_image"));
            exportImageMenuItem.addActionListener(e -> exportImage());
        }
        return exportImageMenuItem;
    }

    private void exportImage() {
        System.out.print("Saving snapshot: ");
        final File file = FileChooser.getInstance().showDialog(".png", FileChooser.pngFilter, true);
        if (file == null) {
            return;
        }
        System.out.print(file + "...");
        try {
            final BufferedImage snapShot = Printout.takeSnapShot();
            ImageIO.write(snapShot, "png", file);
            System.out.println("done");
        } catch (final Throwable err) {
            err.printStackTrace();
            JOptionPane.showMessageDialog(this, err.getMessage(), I18n.get("msg.error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private JMenuItem getSpecificationsMenuItem() {
        if (specificationsMenuItem == null) {
            specificationsMenuItem = new JMenuItem(I18n.get("menu.specifications"));
            specificationsMenuItem.addActionListener(e -> {
                final SpecsDialog specsDialog = new SpecsDialog();
                switch (Scene.getInstance().getProjectType()) {
                    case Foundation.TYPE_PV_PROJECT:
                        specsDialog.selectPvSpecs();
                        break;
                    case Foundation.TYPE_CSP_PROJECT:
                        specsDialog.selectCspSpecs();
                        break;
                    default:
                        specsDialog.selectBuildingSpecs();
                }
                specsDialog.setVisible(true);
            });
        }
        return specificationsMenuItem;
    }

    private JMenuItem getOverallUtilityBillMenuItem() {
        if (overallUtilityBillMenuItem == null) {
            overallUtilityBillMenuItem = new JMenuItem(I18n.get("menu.overall_utility_bill"));
            overallUtilityBillMenuItem.addActionListener(e -> {
                UtilityBill b = Scene.getInstance().getUtilityBill();
                if (b == null) {
                    if (JOptionPane.showConfirmDialog(MainFrame.this, "<html>" + I18n.get("msg.no_overall_utility_bill") + "</html>",
                            I18n.get("dialog.overall_utility_bill"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION) {
                        return;
                    }
                    b = new UtilityBill();
                    Scene.getInstance().setUtilityBill(b);
                }
                new UtilityBillDialog(b).setVisible(true);
            });
        }
        return overallUtilityBillMenuItem;
    }

    private JMenuItem getSetRegionMenuItem() {
        if (setRegionMenuItem == null) {
            setRegionMenuItem = new JMenuItem(I18n.get("menu.set_region"));
            setRegionMenuItem.addActionListener(e -> new GlobalMap(MainFrame.this).setVisible(true));
        }
        return setRegionMenuItem;
    }

    private JMenuItem getPropertiesMenuItem() {
        if (propertiesMenuItem == null) {
            propertiesMenuItem = new JMenuItem(I18n.get("menu.properties"));
            propertiesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            propertiesMenuItem.addActionListener(e -> new PropertiesDialog().setVisible(true));
        }
        return propertiesMenuItem;
    }

    private JCheckBoxMenuItem getNoteCheckBoxMenuItem() {
        if (noteCheckBoxMenuItem == null) {
            noteCheckBoxMenuItem = new JCheckBoxMenuItem(I18n.get("menu.show_note"));
            noteCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke("F11"));
            noteCheckBoxMenuItem.addActionListener(e -> {
                MainPanel.getInstance().setNoteVisible(noteCheckBoxMenuItem.isSelected());
                Util.selectSilently(MainPanel.getInstance().getNoteButton(), noteCheckBoxMenuItem.isSelected());
            });
        }
        return noteCheckBoxMenuItem;
    }

    private JCheckBoxMenuItem getInfoPanelCheckBoxMenuItem() {
        if (infoPanelCheckBoxMenuItem == null) {
            infoPanelCheckBoxMenuItem = new JCheckBoxMenuItem(I18n.get("menu.show_info_panel"), true);
            infoPanelCheckBoxMenuItem.addActionListener(e -> {
                MainPanel.getInstance().setSplitComponentVisible(infoPanelCheckBoxMenuItem.isSelected(), MainPanel.getInstance().getEnergyCanvasNoteSplitPane(), EnergyPanel.getInstance());
                ((Component) SceneManager.getInstance().getCanvas()).requestFocusInWindow();
            });
        }
        return infoPanelCheckBoxMenuItem;
    }

    private JCheckBoxMenuItem getAutoRecomputeEnergyMenuItem() {
        if (autoRecomputeEnergyMenuItem == null) {
            autoRecomputeEnergyMenuItem = new JCheckBoxMenuItem(I18n.get("menu.auto_recompute_energy_label"));
            autoRecomputeEnergyMenuItem.addItemListener(e -> EnergyPanel.setAutoRecomputeEnergy(autoRecomputeEnergyMenuItem.isSelected()));
        }
        return autoRecomputeEnergyMenuItem;
    }

    private JMenuItem getRemoveAllRoofsMenuItem() {
        if (removeAllRoofsMenuItem == null) {
            removeAllRoofsMenuItem = new JMenuItem(I18n.get("menu.remove_all_roofs"));
            removeAllRoofsMenuItem.addActionListener(e -> Scene.getInstance().removeAllRoofs());
        }
        return removeAllRoofsMenuItem;
    }

    private JMenuItem getRemoveAllFloorsMenuItem() {
        if (removeAllFloorsMenuItem == null) {
            removeAllFloorsMenuItem = new JMenuItem(I18n.get("menu.remove_all_floors"));
            removeAllFloorsMenuItem.addActionListener(e -> Scene.getInstance().removeAllFloors());
        }
        return removeAllFloorsMenuItem;
    }

    private JMenuItem getRemoveAllSolarPanelsMenuItem() {
        if (removeAllSolarPanelsMenuItem == null) {
            removeAllSolarPanelsMenuItem = new JMenuItem(I18n.get("menu.remove_all_solar_panels"));
            removeAllSolarPanelsMenuItem.addActionListener(e -> Scene.getInstance().removeAllSolarPanels(null));
        }
        return removeAllSolarPanelsMenuItem;
    }

    private JMenuItem getRemoveAllRacksMenuItem() {
        if (removeAllRacksMenuItem == null) {
            removeAllRacksMenuItem = new JMenuItem(I18n.get("menu.remove_all_racks"));
            removeAllRacksMenuItem.addActionListener(e -> Scene.getInstance().removeAllRacks());
        }
        return removeAllRacksMenuItem;
    }

    private JMenuItem getRemoveAllHeliostatsMenuItem() {
        if (removeAllHeliostatsMenuItem == null) {
            removeAllHeliostatsMenuItem = new JMenuItem(I18n.get("menu.remove_all_heliostats"));
            removeAllHeliostatsMenuItem.addActionListener(e -> Scene.getInstance().removeAllHeliostats());
        }
        return removeAllHeliostatsMenuItem;
    }

    private JMenuItem getRemoveAllParabolicTroughsMenuItem() {
        if (removeAllParabolicTroughsMenuItem == null) {
            removeAllParabolicTroughsMenuItem = new JMenuItem(I18n.get("menu.remove_all_parabolic_troughs"));
            removeAllParabolicTroughsMenuItem.addActionListener(e -> Scene.getInstance().removeAllParabolicTroughs());
        }
        return removeAllParabolicTroughsMenuItem;
    }

    private JMenuItem getRemoveAllParabolicDishesMenuItem() {
        if (removeAllParabolicDishesMenuItem == null) {
            removeAllParabolicDishesMenuItem = new JMenuItem(I18n.get("menu.remove_all_parabolic_dishes"));
            removeAllParabolicDishesMenuItem.addActionListener(e -> Scene.getInstance().removeAllParabolicDishes());
        }
        return removeAllParabolicDishesMenuItem;
    }

    private JMenuItem getRemoveAllFresnelReflectorsMenuItem() {
        if (removeAllFresnelReflectorsMenuItem == null) {
            removeAllFresnelReflectorsMenuItem = new JMenuItem(I18n.get("menu.remove_all_fresnel_reflectors"));
            removeAllFresnelReflectorsMenuItem.addActionListener(e -> Scene.getInstance().removeAllFresnelReflectors());
        }
        return removeAllFresnelReflectorsMenuItem;
    }

    private JMenuItem getRemoveAllSensorsMenuItem() {
        if (removeAllSensorsMenuItem == null) {
            removeAllSensorsMenuItem = new JMenuItem(I18n.get("menu.remove_all_sensors"));
            removeAllSensorsMenuItem.addActionListener(e -> Scene.getInstance().removeAllSensors());
        }
        return removeAllSensorsMenuItem;
    }

    private JMenuItem getRemoveAllWallsMenuItem() {
        if (removeAllWallsMenuItem == null) {
            removeAllWallsMenuItem = new JMenuItem(I18n.get("menu.remove_all_walls"));
            removeAllWallsMenuItem.addActionListener(e -> Scene.getInstance().removeAllWalls()); // actual scene removal already runs in the Task Manager thread
        }
        return removeAllWallsMenuItem;
    }

    private JMenuItem getRemoveAllWindowsMenuItem() {
        if (removeAllWindowsMenuItem == null) {
            removeAllWindowsMenuItem = new JMenuItem(I18n.get("menu.remove_all_windows"));
            removeAllWindowsMenuItem.addActionListener(e -> Scene.getInstance().removeAllWindows());
        }
        return removeAllWindowsMenuItem;
    }

    private JMenuItem getRemoveAllWindowShuttersMenuItem() {
        if (removeAllWindowShuttersMenuItem == null) {
            removeAllWindowShuttersMenuItem = new JMenuItem(I18n.get("menu.remove_all_shutters"));
            removeAllWindowShuttersMenuItem.addActionListener(e -> Scene.getInstance().removeAllWindowShutters());
        }
        return removeAllWindowShuttersMenuItem;
    }

    private JMenuItem getRemoveAllFoundationsMenuItem() {
        if (removeAllFoundationsMenuItem == null) {
            removeAllFoundationsMenuItem = new JMenuItem(I18n.get("menu.remove_all_foundations"));
            removeAllFoundationsMenuItem.addActionListener(e -> Scene.getInstance().removeAllFoundations());
        }
        return removeAllFoundationsMenuItem;
    }

    private JMenuItem getRemoveAllTreesMenuItem() {
        if (removeAllTreesMenuItem == null) {
            removeAllTreesMenuItem = new JMenuItem(I18n.get("menu.remove_all_trees"));
            removeAllTreesMenuItem.addActionListener(e -> Scene.getInstance().removeAllTrees());
        }
        return removeAllTreesMenuItem;
    }

    private JMenuItem getRemoveAllHumansMenuItem() {
        if (removeAllHumansMenuItem == null) {
            removeAllHumansMenuItem = new JMenuItem(I18n.get("menu.remove_all_humans"));
            removeAllHumansMenuItem.addActionListener(e -> Scene.getInstance().removeAllHumans());
        }
        return removeAllHumansMenuItem;
    }

    private JMenuItem getRemoveAllUtilityBillsMenuItem() {
        if (removeAllUtilityBillsMenuItem == null) {
            removeAllUtilityBillsMenuItem = new JMenuItem(I18n.get("menu.remove_all_utility_bills"));
            removeAllUtilityBillsMenuItem.addActionListener(e -> {
                final ArrayList<Foundation> list = new ArrayList<>();
                for (final HousePart p : Scene.getInstance().getParts()) {
                    if (p instanceof Foundation && ((Foundation) p).getUtilityBill() != null) {
                        list.add((Foundation) p);
                    }
                }
                if (list.isEmpty()) {
                    JOptionPane.showMessageDialog(MainFrame.getInstance(), I18n.get("msg.no_utility_bill"), I18n.get("dialog.no_utility_bill"), JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (JOptionPane.showConfirmDialog(MainFrame.getInstance(), I18n.get("msg.remove_all_utility_bills", Integer.toString(list.size())), I18n.get("dialog.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    for (final Foundation f : list) {
                        f.setUtilityBill(null);
                    }
                }
                Scene.getInstance().setEdited(true);
            });
        }
        return removeAllUtilityBillsMenuItem;
    }

    private JMenuItem getRotate180MenuItem() {
        if (rotate180MenuItem == null) {
            rotate180MenuItem = new JMenuItem(I18n.get("menu.rotate_180"));
            rotate180MenuItem.addActionListener(e -> SceneManager.getInstance().rotate(Math.PI));
        }
        return rotate180MenuItem;
    }

    private JMenuItem getRotate90CwMenuItem() {
        if (rotate90CwMenuItem == null) {
            rotate90CwMenuItem = new JMenuItem(I18n.get("menu.rotate_90_cw"));
            rotate90CwMenuItem.addActionListener(e -> SceneManager.getInstance().rotate(-Math.PI / 2));
        }
        return rotate90CwMenuItem;
    }

    private JMenuItem getRotate90CcwMenuItem() {
        if (rotate90CcwMenuItem == null) {
            rotate90CcwMenuItem = new JMenuItem(I18n.get("menu.rotate_90_ccw"));
            rotate90CcwMenuItem.addActionListener(e -> SceneManager.getInstance().rotate(Math.PI / 2));
        }
        return rotate90CcwMenuItem;
    }

    private JMenuItem getMoveEastMenuItem() {
        if (moveEastMenuItem == null) {
            moveEastMenuItem = new JMenuItem(I18n.get("menu.move_east"));
            moveEastMenuItem.addActionListener(e -> {
                if (MainPanel.getInstance().getNoteTextArea().hasFocus()) {
                    return;
                }
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().move(new Vector3(1, 0, 0));
                    return null;
                });
            });
        }
        return moveEastMenuItem;
    }

    private JMenuItem getMoveWestMenuItem() {
        if (moveWestMenuItem == null) {
            moveWestMenuItem = new JMenuItem(I18n.get("menu.move_west"));
            moveWestMenuItem.addActionListener(e -> {
                if (MainPanel.getInstance().getNoteTextArea().hasFocus()) {
                    return;
                }
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().move(new Vector3(-1, 0, 0));
                    return null;
                });
            });
        }
        return moveWestMenuItem;
    }

    private JMenuItem getMoveSouthMenuItem() {
        if (moveSouthMenuItem == null) {
            moveSouthMenuItem = new JMenuItem(I18n.get("menu.move_south"));
            moveSouthMenuItem.addActionListener(e -> {
                if (MainPanel.getInstance().getNoteTextArea().hasFocus()) {
                    return;
                }
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().move(new Vector3(0, -1, 0));
                    return null;
                });
            });
        }
        return moveSouthMenuItem;
    }

    private JMenuItem getMoveNorthMenuItem() {
        if (moveNorthMenuItem == null) {
            moveNorthMenuItem = new JMenuItem(I18n.get("menu.move_north"));
            moveNorthMenuItem.addActionListener(e -> {
                if (MainPanel.getInstance().getNoteTextArea().hasFocus()) {
                    return;
                }
                SceneManager.getTaskManager().update(() -> {
                    SceneManager.getInstance().move(new Vector3(0, 1, 0));
                    return null;
                });
            });
        }
        return moveNorthMenuItem;
    }

    private JMenuItem getFixProblemsMenuItem() {
        if (fixProblemsMenuItem == null) {
            fixProblemsMenuItem = new JMenuItem(I18n.get("menu.fix_problems"));
            fixProblemsMenuItem.addActionListener(e -> SceneManager.getTaskManager().update(() -> {
                Scene.getInstance().fixProblems(true);
                return null;
            }));
        }
        return fixProblemsMenuItem;
    }

    private JMenuItem getRemoveAllEditLocksMenuItem() {
        if (removeAllEditLocksMenuItem == null) {
            removeAllEditLocksMenuItem = new JMenuItem(I18n.get("menu.remove_all_edit_locks"));
            removeAllEditLocksMenuItem.addActionListener(e -> Scene.getInstance().lockAll(false));
        }
        return removeAllEditLocksMenuItem;
    }

    private JMenuItem getEnableAllBaseEditPointsMenuItem() {
        if (enableAllBaseEditPointsMenuItem == null) {
            enableAllBaseEditPointsMenuItem = new JMenuItem(I18n.get("menu.all_bases"));
            enableAllBaseEditPointsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK));
            enableAllBaseEditPointsMenuItem.addActionListener(e -> {
                final List<Foundation> foundations = Scene.getInstance().getAllFoundations();
                SceneManager.getTaskManager().update(() -> {
                    for (final Foundation f : foundations) {
                        f.setLockEdit(false);
                    }
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
        }
        return enableAllBaseEditPointsMenuItem;
    }

    private JMenuItem getDisableAllBaseEditPointsMenuItem() {
        if (disableAllBaseEditPointsMenuItem == null) {
            disableAllBaseEditPointsMenuItem = new JMenuItem(I18n.get("menu.all_bases"));
            disableAllBaseEditPointsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, (Config.isMac() ? KeyEvent.META_MASK : KeyEvent.CTRL_MASK) | KeyEvent.SHIFT_MASK));
            disableAllBaseEditPointsMenuItem.addActionListener(e -> {
                final List<Foundation> foundations = Scene.getInstance().getAllFoundations();
                SceneManager.getTaskManager().update(() -> {
                    for (final Foundation f : foundations) {
                        f.setLockEdit(true);
                    }
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
        }
        return disableAllBaseEditPointsMenuItem;
    }

    private JMenuItem getEnableAllTreeEditPointsMenuItem() {
        if (enableAllTreeEditPointsMenuItem == null) {
            enableAllTreeEditPointsMenuItem = new JMenuItem(I18n.get("menu.all_trees"));
            enableAllTreeEditPointsMenuItem.addActionListener(e -> {
                final List<Tree> trees = Scene.getInstance().getAllTrees();
                SceneManager.getTaskManager().update(() -> {
                    for (final Tree t : trees) {
                        t.setLockEdit(false);
                    }
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
        }
        return enableAllTreeEditPointsMenuItem;
    }

    private JMenuItem getDisableAllTreeEditPointsMenuItem() {
        if (disableAllTreeEditPointsMenuItem == null) {
            disableAllTreeEditPointsMenuItem = new JMenuItem(I18n.get("menu.all_trees"));
            disableAllTreeEditPointsMenuItem.addActionListener(e -> {
                final List<Tree> trees = Scene.getInstance().getAllTrees();
                SceneManager.getTaskManager().update(() -> {
                    for (final Tree t : trees) {
                        t.setLockEdit(true);
                    }
                    return null;
                });
                Scene.getInstance().setEdited(true);
            });
        }
        return disableAllTreeEditPointsMenuItem;
    }

    public JColorChooser getColorChooser() {
        return colorChooser;
    }

}