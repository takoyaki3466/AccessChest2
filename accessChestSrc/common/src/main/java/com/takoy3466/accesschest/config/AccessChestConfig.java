package com.takoy3466.accesschest.config;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

/**
 * Settings stored in {@code config/accesschest.properties}. Missing or invalid values fall back to the default and the
 * file is rewritten with every key present.
 */
public final class AccessChestConfig {
    public static final String FILE_NAME = AccessChestCommon.MOD_ID + ".properties";

    /** Server side: how many stacks one press of "Eject" drops. */
    public static final IntSetting EJECT_LIMIT = new IntSetting("eject_limit", 96, 1, Short.MAX_VALUE,
            "Maximum number of stacks dropped by the Eject button. Used by the server.");
    /** Client side: how many rows one mouse wheel step scrolls. */
    public static final IntSetting WHEEL_SCROLL_ROWS = new IntSetting("wheel_scroll_rows", 8, 0, Byte.MAX_VALUE,
            "Rows scrolled per mouse wheel step in the chest screen. Used by the client.");

    private static final List<IntSetting> SETTINGS = List.of(EJECT_LIMIT, WHEEL_SCROLL_ROWS);

    private AccessChestConfig() {
    }

    public static void load(Path configDirectory) {
        Path file = configDirectory.resolve(FILE_NAME);
        Properties properties = new Properties();
        if (Files.exists(file)) {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                properties.load(reader);
            } catch (IOException e) {
                AccessChestCommon.LOGGER.error("Could not read {}, using default settings", file, e);
            }
        }

        boolean complete = true;
        for (IntSetting setting : SETTINGS) {
            complete &= setting.load(properties);
        }
        if (!complete) {
            write(file);
        }
    }

    private static void write(Path file) {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                writer.write("# Access Chest settings" + System.lineSeparator());
                for (IntSetting setting : SETTINGS) {
                    setting.write(writer);
                }
            }
        } catch (IOException e) {
            AccessChestCommon.LOGGER.error("Could not write {}", file, e);
        }
    }

    /**
     * An integer setting with a range. The current value is readable from any thread.
     */
    public static final class IntSetting {
        private final String key;
        private final int defaultValue;
        private final int min;
        private final int max;
        private final String comment;
        private volatile int value;

        private IntSetting(String key, int defaultValue, int min, int max, String comment) {
            this.key = key;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
            this.comment = comment;
            this.value = defaultValue;
        }

        public int get() {
            return value;
        }

        /** @return false if the key was missing or invalid and the default value was used */
        private boolean load(Properties properties) {
            String raw = properties.getProperty(key);
            if (raw == null) {
                value = defaultValue;
                return false;
            }
            try {
                int parsed = Integer.parseInt(raw.trim());
                value = Mth.clamp(parsed, min, max);
                if (parsed != value) {
                    AccessChestCommon.LOGGER.warn("Setting {}={} is outside {}..{}, using {}", key, parsed, min, max, value);
                }
                return true;
            } catch (NumberFormatException e) {
                AccessChestCommon.LOGGER.warn("Setting {}={} is not a number, using default {}", key, raw, defaultValue);
                value = defaultValue;
                return false;
            }
        }

        private void write(Writer writer) throws IOException {
            String newline = System.lineSeparator();
            writer.write(newline + "# " + comment + newline);
            writer.write("# Range: " + min + " - " + max + ", default: " + defaultValue + newline);
            writer.write(key + "=" + value + newline);
        }
    }
}
