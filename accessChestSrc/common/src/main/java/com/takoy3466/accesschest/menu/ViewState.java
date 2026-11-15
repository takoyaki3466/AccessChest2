package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.chest.ChestGrade;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.DataSlot;

/**
 * What the window currently shows: how many slots match the filter and which row is the first visible one.
 * The server writes it, {@link DataSlot}s copy it to the client.
 */
public final class ViewState {

    static {
        // DataSlot values are sent as signed shorts.
        if (ChestGrade.HIGHEST.slotCount() > Short.MAX_VALUE) {
            throw new IllegalStateException("Largest chest does not fit into a menu data slot");
        }
    }

    private int matchCount;
    private int scrollRow;

    public int matchCount() {
        return matchCount;
    }

    public int scrollRow() {
        return scrollRow;
    }

    void setMatchCount(int matchCount) {
        this.matchCount = Math.max(0, matchCount);
    }

    void setScrollRow(int scrollRow) {
        this.scrollRow = Math.max(0, scrollRow);
    }

    public int maxScrollRow() {
        return ChestMenuLayout.maxScrollRow(matchCount);
    }

    public int visibleSlotCount() {
        return Mth.clamp(matchCount - scrollRow * ChestMenuLayout.COLUMNS, 0, ChestMenuLayout.WINDOW_SLOT_COUNT);
    }

    public boolean isVisible(int windowIndex) {
        return windowIndex >= 0 && windowIndex < visibleSlotCount();
    }

    DataSlot matchCountSlot() {
        return new DataSlot() {
            @Override
            public int get() {
                return matchCount;
            }

            @Override
            public void set(int value) {
                setMatchCount(value);
            }
        };
    }

    DataSlot scrollRowSlot() {
        return new DataSlot() {
            @Override
            public int get() {
                return scrollRow;
            }

            @Override
            public void set(int value) {
                setScrollRow(value);
            }
        };
    }
}
