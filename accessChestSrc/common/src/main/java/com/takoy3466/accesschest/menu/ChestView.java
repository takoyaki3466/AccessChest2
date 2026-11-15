package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.storage.BoundedStorage;
import com.takoy3466.accesschest.storage.FilterQuery;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.util.Mth;

/**
 * Server side mapping from window slots to storage indices: the storage indices matching the filter, shifted by the
 * scroll position.
 */
final class ChestView {
    static final int NO_SLOT = -1;

    private final IntList matches = new IntArrayList();
    private final ViewState state = new ViewState();
    private FilterQuery query = FilterQuery.ALL;

    ViewState state() {
        return state;
    }

    void setQuery(FilterQuery query) {
        this.query = query;
    }

    void rebuild(BoundedStorage storage) {
        matches.clear();
        for (int i = 0; i < storage.size(); i++) {
            if (query.matches(storage.get(i), storage.priorities())) {
                matches.add(i);
            }
        }
        state.setMatchCount(matches.size());
        scrollTo(state.scrollRow());
    }

    void scrollTo(int row) {
        state.setScrollRow(Mth.clamp(row, 0, state.maxScrollRow()));
    }

    int storageIndex(int windowIndex) {
        if (windowIndex < 0 || windowIndex >= ChestMenuLayout.WINDOW_SLOT_COUNT) {
            return NO_SLOT;
        }
        int position = state.scrollRow() * ChestMenuLayout.COLUMNS + windowIndex;
        return position < matches.size() ? matches.getInt(position) : NO_SLOT;
    }

    /** @return the storage indices matching the current filter, in storage order */
    IntList matches() {
        return IntLists.unmodifiable(matches);
    }
}
