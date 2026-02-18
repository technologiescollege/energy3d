package org.concord.energy3d.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Internationalisation (i18n) pour Energy3D.
 * Charge les chaînes depuis le dossier <b>locales</b> au format JSON (UTF-8).
 * <p>
 * Une fois l'application compilée/déployée, les fichiers sont lus depuis le dossier <b>app/locales/</b>
 * (à côté du JAR), modifiables par l'utilisateur : les changements sont pris en compte à chaque lancement.
 * Si le dossier externe est absent (ex. en développement), les fichiers dans le JAR sont utilisés.
 * <p>
 * Usage : {@code I18n.get("menu.file")}, {@code I18n.get("msg.file_overwrite", fileName)}.
 */
public final class I18n {

    private static final String LOCALES_PATH = "org/concord/energy3d/locales/";
    private static final String FALLBACK_LANG = "en";
    private static Locale currentLocale = Locale.getDefault();
    private static Map<String, String> strings;
    private static File externalLocalesDir;

    private I18n() {
    }

    /** Dossier locales à côté du JAR (app/locales en déploiement), ou null si absent. */
    private static File getExternalLocalesDir() {
        if (externalLocalesDir != null) return externalLocalesDir;
        try {
            URL url = I18n.class.getProtectionDomain().getCodeSource().getLocation();
            if (url == null) return null;
            if ("jar".equals(url.getProtocol())) {
                String path = url.getPath();
                int bang = path.indexOf('!');
                if (bang >= 0) path = path.substring(0, bang);
                url = new URL(path);
            }
            File base = new File(url.toURI());
            File dir = base.isFile() ? base.getParentFile() : base;
            File localesDir = new File(dir, "locales");
            externalLocalesDir = localesDir.isDirectory() ? localesDir : null;
        } catch (Exception e) {
            externalLocalesDir = null;
        }
        return externalLocalesDir;
    }

    private static Map<String, String> loadStrings() {
        if (strings == null) {
            strings = new HashMap<>();
        }
        final String lang = currentLocale.getLanguage();
        loadJsonIfPresent(lang);
        if (strings.isEmpty() && !FALLBACK_LANG.equals(lang)) {
            loadJsonIfPresent(FALLBACK_LANG);
        }
        return strings;
    }

    private static void loadJsonIfPresent(final String lang) {
        final String filename = lang + ".json";
        // 1) Dossier externe (app/locales) : modifiable par l'utilisateur, pris en compte à chaque lancement
        final File externalDir = getExternalLocalesDir();
        if (externalDir != null) {
            final File file = new File(externalDir, filename);
            if (file.isFile()) {
                try (FileInputStream fis = new FileInputStream(file);
                     InputStreamReader reader = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
                    final Map<String, String> loaded = parseFlatJson(reader);
                    if (!loaded.isEmpty()) {
                        strings.clear();
                        strings.putAll(loaded);
                        return;
                    }
                } catch (IOException e) {
                    // fall through to classpath
                }
            }
        }
        // 2) Ressources dans le JAR (repli)
        final String resource = LOCALES_PATH + filename;
        try (InputStream is = I18n.class.getClassLoader().getResourceAsStream(resource)) {
            if (is != null) {
                final PushbackInputStream pin = new PushbackInputStream(is, 3);
                final byte[] bom = new byte[3];
                int n = pin.read(bom);
                if (n != 3 || bom[0] != (byte) 0xEF || bom[1] != (byte) 0xBB || bom[2] != (byte) 0xBF) {
                    if (n > 0) pin.unread(bom, 0, n);
                }
                final Map<String, String> loaded = parseFlatJson(new InputStreamReader(pin, StandardCharsets.UTF_8));
                if (!loaded.isEmpty()) {
                    strings.clear();
                    strings.putAll(loaded);
                }
            }
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * Parse un objet JSON plat {"clé": "valeur", ...} sans dépendance externe.
     * Gère les échappements \" \\ dans les valeurs.
     */
    private static Map<String, String> parseFlatJson(final InputStreamReader reader) throws IOException {
        final Map<String, String> out = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        final char[] buf = new char[4096];
        int n;
        while ((n = reader.read(buf)) != -1) {
            sb.append(buf, 0, n);
        }
        final String s = sb.toString();
        int i = 0;
        while (i < s.length()) {
            final int keyStart = s.indexOf('"', i);
            if (keyStart == -1) break;
            final int keyEnd = s.indexOf('"', keyStart + 1);
            if (keyEnd == -1) break;
            final int colon = s.indexOf(':', keyEnd);
            if (colon == -1) break;
            final int valueStart = s.indexOf('"', colon + 1);
            if (valueStart == -1) break;
            final String key = s.substring(keyStart + 1, keyEnd);
            final StringBuilder value = new StringBuilder();
            int j = valueStart + 1;
            while (j < s.length()) {
                final char c = s.charAt(j);
                if (c == '\\') {
                    j++;
                    if (j < s.length()) {
                        final char next = s.charAt(j);
                        if (next == '"') value.append('"');
                        else if (next == '\\') value.append('\\');
                        else if (next == 'n') value.append('\n');
                        else value.append(next);
                    }
                    j++;
                } else if (c == '"') {
                    j++;
                    break;
                } else {
                    value.append(c);
                    j++;
                }
            }
            out.put(key, value.toString());
            i = j;
        }
        return out;
    }

    /**
     * Recharge les chaînes (appelé après setLocale).
     */
    private static void clearCache() {
        strings = null;
    }

    /**
     * Retourne la chaîne pour la clé, ou [clé] si absente.
     */
    public static String get(final String key) {
        final Map<String, String> map = loadStrings();
        final String v = map.get(key);
        return v != null ? v : "[" + key + "]";
    }

    /**
     * Retourne la chaîne formatée (MessageFormat) avec les arguments donnés.
     */
    public static String get(final String key, final Object... args) {
        final String pattern = get(key);
        if (pattern.startsWith("[")) {
            return pattern;
        }
        try {
            return java.text.MessageFormat.format(pattern, args);
        } catch (final Exception e) {
            return pattern;
        }
    }

    public static void setLocale(final Locale locale) {
        currentLocale = locale != null ? locale : Locale.getDefault();
        clearCache();
        // Invalider les caches des autres classes qui dépendent de la locale
        org.concord.energy3d.simulation.AnnualGraph.invalidateMonthCache();
        org.concord.energy3d.util.FileChooser.invalidateFileFilterCache();
    }

    public static Locale getLocale() {
        return currentLocale;
    }

    /**
     * Liste des codes de langue disponibles (dossier <b>locales</b> scanné à chaque appel).
     * Si le dossier externe app/locales existe, retourne les codes des fichiers *.json présents ;
     * sinon retourne une liste par défaut (en, fr). Permet un menu des langues dynamique.
     */
    public static String[] getAvailableLocaleCodes() {
        final File dir = getExternalLocalesDir();
        if (dir != null) {
            final File[] files = dir.listFiles((d, name) -> name != null && name.endsWith(".json"));
            if (files != null && files.length > 0) {
                final List<String> codes = new ArrayList<>();
                for (final File f : files) {
                    final String name = f.getName();
                    final String code = name.substring(0, name.length() - 5); // ".json"
                    if (!code.isEmpty()) codes.add(code);
                }
                Collections.sort(codes);
                return codes.toArray(new String[0]);
            }
        }
        return new String[]{"en", "fr"};
    }

    /**
     * Retourne le libellé traduit du type de cellule PV (identifiant interne en anglais).
     */
    public static String getCellTypeDisplayName(final String internalCellType) {
        if (internalCellType == null) return get("label.not_applicable");
        switch (internalCellType) {
            case "Polycrystalline":
                return get("cell_type.polycrystalline");
            case "Monocrystalline":
                return get("cell_type.monocrystalline");
            case "Thin Film":
                return get("cell_type.thin_film");
            default:
                return internalCellType;
        }
    }

    /**
     * Retourne le libellé traduit de la couleur (identifiant interne en anglais).
     */
    public static String getColorDisplayName(final String internalColor) {
        if (internalColor == null) return get("label.not_applicable");
        switch (internalColor) {
            case "Blue":
                return get("color.blue");
            case "Black":
                return get("color.black");
            case "Gray":
                return get("color.gray");
            default:
                return internalColor;
        }
    }

    /** Nom d'affichage natif pour un code (ex. "fr" → "Français"). */
    public static String getDisplayNameForLocaleCode(final String code) {
        final String name = new Locale(code).getDisplayLanguage(new Locale(code));
        if (name == null || name.isEmpty()) return code;
        return name.substring(0, 1).toUpperCase(Locale.ROOT) + name.substring(1);
    }
}
