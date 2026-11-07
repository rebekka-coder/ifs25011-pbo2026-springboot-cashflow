package org.delcom.ifs25011pbo2026springbootcashflow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Memuat pasangan KEY=VALUE dari file .env ke system properties.
 * Variabel yang sudah ada di environment atau system properties tidak ditimpa.
 */
public final class DotEnv {

    private static final Path DEFAULT_PATH = Path.of(".env");

    private DotEnv() {
    }

    /** Memuat file .env dari direktori kerja; diabaikan jika file tidak ada. */
    public static void load() throws IOException {
        load(DEFAULT_PATH);
    }

    /** Memuat file .env dari path tertentu; diabaikan jika file tidak ada. */
    public static void load(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        for (String line : Files.readAllLines(path)) {
            applyLine(line);
        }
    }

    private static void applyLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }

        int separator = trimmed.indexOf('=');
        if (separator <= 0) {
            return;
        }

        String key = trimmed.substring(0, separator).trim();
        String value = unquote(trimmed.substring(separator + 1).trim());

        if (System.getenv(key) == null && System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' || first == '\'') && first == last) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
