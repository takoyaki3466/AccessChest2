package com.takoy3466.accesschest.client.screen;

import com.takoy3466.accesschest.chest.ChestSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Remembers, for the current game session, where the player left each chest opened from an item.
 */
final class ScrollMemory {
    private static final Map<ChestSpec, Integer> ROWS = new HashMap<>();

    private ScrollMemory() {
    }

    static void remember(ChestSpec spec, int row) {
        ROWS.put(spec, row);
    }

    static OptionalInt recall(ChestSpec spec) {
        Integer row = ROWS.get(spec);
        return row == null ? OptionalInt.empty() : OptionalInt.of(row);
    }
}
