package com.takoy3466.accesschest.storage;

import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

/**
 * Fixed capacity item storage plus the sort priorities of its owner.
 * <p>
 * Every mutation reports to the change listener so that the owner (saved data or block entity) is persisted.
 * Range limits of chest grades are applied by {@link BoundedStorage}, not here.
 */
public final class ChestStorage {
    private final NonNullList<ItemStack> items;
    private final PriorityTable priorities;
    private final Runnable changeListener;

    public ChestStorage(int capacity, Runnable changeListener) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive: " + capacity);
        }
        this.items = NonNullList.withSize(capacity, ItemStack.EMPTY);
        this.changeListener = Objects.requireNonNull(changeListener, "changeListener");
        this.priorities = new PriorityTable(changeListener);
    }

    public int capacity() {
        return items.size();
    }

    /**
     * @return the live stack at {@code index}. Callers that modify it must call {@link #markChanged()}.
     */
    public ItemStack get(int index) {
        return items.get(index);
    }

    public void set(int index, ItemStack stack) {
        items.set(index, stack);
        markChanged();
    }

    public ItemStack remove(int index, int amount) {
        ItemStack removed = ContainerHelper.removeItem(items, index, amount);
        if (!removed.isEmpty()) {
            markChanged();
        }
        return removed;
    }

    public ItemStack take(int index) {
        ItemStack taken = ContainerHelper.takeItem(items, index);
        if (!taken.isEmpty()) {
            markChanged();
        }
        return taken;
    }

    public void clear() {
        for (int i = 0; i < items.size(); i++) {
            items.set(i, ItemStack.EMPTY);
        }
        markChanged();
    }

    public PriorityTable priorities() {
        return priorities;
    }

    public void markChanged() {
        changeListener.run();
    }

    /** Sets a slot while loading, without reporting a change. */
    void restore(int index, ItemStack stack) {
        items.set(index, stack);
    }
}
