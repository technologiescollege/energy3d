package org.concord.energy3d.util;

import java.awt.FileDialog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.concord.energy3d.MainApplication;
import org.concord.energy3d.gui.MainFrame;
import org.concord.energy3d.scene.Scene;
import org.concord.energy3d.util.I18n;

/**
 * This is a file chooser that remembers the latest path and files.
 *
 * @author Charles Xie
 */

public class FileChooser {

    private static ExtensionFileFilter pngFilterInstance;
    private static ExtensionFileFilter daeFilterInstance;
    private static ExtensionFileFilter objFilterInstance;
    private static ExtensionFileFilter ng3FilterInstance;
    private static ExtensionFileFilter zipFilterInstance;
    
    public static ExtensionFileFilter getPngFilter() {
        if (pngFilterInstance == null) {
            pngFilterInstance = new ExtensionFileFilter(I18n.get("filefilter.image_png"), "png");
        }
        return pngFilterInstance;
    }
    public static ExtensionFileFilter getDaeFilter() {
        if (daeFilterInstance == null) {
            daeFilterInstance = new ExtensionFileFilter(I18n.get("filefilter.collada_dae"), "dae");
        }
        return daeFilterInstance;
    }
    public static ExtensionFileFilter getObjFilter() {
        if (objFilterInstance == null) {
            objFilterInstance = new ExtensionFileFilter(I18n.get("filefilter.wavefront_obj"), "obj");
        }
        return objFilterInstance;
    }
    public static ExtensionFileFilter getNg3Filter() {
        if (ng3FilterInstance == null) {
            ng3FilterInstance = new ExtensionFileFilter(I18n.get("filefilter.energy3d_ng3"), "ng3");
        }
        return ng3FilterInstance;
    }
    public static ExtensionFileFilter getZipFilter() {
        if (zipFilterInstance == null) {
            zipFilterInstance = new ExtensionFileFilter(I18n.get("filefilter.zip"), "zip");
        }
        return zipFilterInstance;
    }
    
    public static void invalidateFileFilterCache() {
        pngFilterInstance = null;
        daeFilterInstance = null;
        objFilterInstance = null;
        ng3FilterInstance = null;
        zipFilterInstance = null;
    }
    
    @Deprecated
    public static final ExtensionFileFilter pngFilter = getPngFilter();
    @Deprecated
    public static final ExtensionFileFilter daeFilter = getDaeFilter();
    @Deprecated
    public static final ExtensionFileFilter objFilter = getObjFilter();
    @Deprecated
    public static final ExtensionFileFilter ng3Filter = getNg3Filter();
    @Deprecated
    public static final ExtensionFileFilter zipFilter = getZipFilter();

    private static final int MAX = 4;
    private static FileChooser instance;
    private final List<String> recentFiles = new ArrayList<>();
    private final JFileChooser fileChooser;
    private FileDialog fileDialog;

    public static FileChooser getInstance() {
        if (instance == null) {
            instance = new FileChooser();
        }
        return instance;
    }

    public FileChooser() {
        String directoryPath = null;
        if (!MainApplication.runFromOnlyJar()) {
            final Preferences pref = Preferences.userNodeForPackage(MainApplication.class);
            addRecentFile(pref.get("Recent File 0", null));
            addRecentFile(pref.get("Recent File 1", null));
            addRecentFile(pref.get("Recent File 2", null));
            addRecentFile(pref.get("Recent File 3", null));
            directoryPath = pref.get("dir", null);
        }
        if (!Config.isWebStart() && directoryPath == null) {
            directoryPath = System.getProperties().getProperty("user.dir");
        }
        fileChooser = new JFileChooser(directoryPath);
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (Config.isMac()) {
            fileDialog = new FileDialog(MainFrame.getInstance());
            fileDialog.setDirectory(directoryPath);
        }
    }

    public void setCurrentDirectory(final File dir) {
        fileChooser.setCurrentDirectory(dir);
    }

    public void rememberFile(final String fileName) {
        if (fileName == null) {
            return;
        }
        if (recentFiles.contains(fileName)) {
            recentFiles.remove(fileName);
        } else {
            if (recentFiles.size() >= MAX) {
                recentFiles.remove(0);
            }
        }
        recentFiles.add(fileName);
    }

    private void addRecentFile(final String fileName) {
        if (fileName != null) {
            recentFiles.add(fileName);
        }
    }

    public String[] getRecentFiles() {
        final int n = recentFiles.size();
        if (n == 0) {
            return new String[]{};
        }
        final String[] s = new String[n];
        for (int i = 0; i < n; i++) {
            s[n - 1 - i] = recentFiles.get(i);
        }
        return s;
    }

    public File showDialog(final String dotExtension, final FileFilter filter, final boolean isSaveDialog) {
        if (Config.isMac() && filter != null) {
            fileDialog.setMode(isSaveDialog ? FileDialog.SAVE : FileDialog.LOAD);
            fileDialog.setTitle(isSaveDialog ? I18n.get("menu.save") : I18n.get("menu.open"));
            fileDialog.setFilenameFilter((dir, name) -> name.toLowerCase().endsWith(dotExtension));

            if (isSaveDialog && dotExtension.equals(".ng3") && Scene.getURL() != null) {
                fileDialog.setFile(Scene.getURL().getFile());
            } else {
                fileDialog.setFile(null);
            }

            fileDialog.setVisible(true);
            final String filename = fileDialog.getFile();
            if (filename == null) {
                System.out.println("cancelled.");
                return null;
            } else {
                final String filenameFull = fileDialog.getDirectory() + filename + (filename.toLowerCase().endsWith(dotExtension) ? "" : dotExtension);
                final File file = new File(filenameFull);
                if (!MainApplication.runFromOnlyJar()) {
                    Preferences.userNodeForPackage(MainApplication.class).put("dir", fileDialog.getDirectory());
                }
                return file;
            }
        } else {
            fileChooser.setFileSelectionMode(filter == null ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
            fileChooser.resetChoosableFileFilters();
            if (filter != null) {
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setFileFilter(filter);
            }

            if (isSaveDialog && dotExtension.equals(".ng3") && Scene.getURL() != null) {
                fileChooser.setSelectedFile(new File(Scene.getURL().getFile()));
            } else {
                fileChooser.setSelectedFile(new File(""));
            }

            while (true) {
                final MainFrame parent = MainFrame.getInstance();
                if (isSaveDialog) {
                    if (fileChooser.showSaveDialog(parent) == JFileChooser.CANCEL_OPTION) {
                        return null;
                    }
                } else if (fileChooser.showOpenDialog(parent) == JFileChooser.CANCEL_OPTION) {
                    return null;
                }
                File file = fileChooser.getSelectedFile();
                if (!file.toString().toLowerCase().endsWith(dotExtension)) {
                    file = new File(file.getParentFile(), Util.getFileName(file.toString()) + dotExtension);
                }
                if (!isSaveDialog || !file.exists() || JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parent, I18n.get("msg.file_already_exists", file.getName()), I18n.get("title.save_file"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
                    if (!MainApplication.runFromOnlyJar()) {
                        Preferences.userNodeForPackage(MainApplication.class).put("dir", fileChooser.getCurrentDirectory().toString());
                    }
                    return file;
                }
            }
        }
    }

}