package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.storage.BoundedStorage;
import com.takoy3466.accesschest.storage.FilterQuery;
import com.takoy3466.accesschest.storage.PriorityTable;
import com.takoy3466.accesschest.storage.StorageSorter;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Server half of an open chest menu: the storage, the filtered view of it and the operations the screen buttons trigger.
 * Callers (the network handlers) are responsible for validating the request before calling an operation.
 */
public final class ServerChestSession {
    private final ChestAccess access;
    private final BoundedStorage storage;
    private final ChestView view = new ChestView();
    private final WindowContainer window;

    ServerChestSession(ChestAccess access) {
        this.access = access;
        this.storage = access.storage();
        this.view.rebuild(storage);
        this.window = new WindowContainer(view, storage, access);
    }

    ChestAccess access() {
        return access;
    }

    Container window() {
        return window;
    }

    ViewState viewState() {
        return view.state();
    }

    /** @return the part of {@code stack} that did not fit */
    ItemStack insert(ItemStack stack) {
        return storage.insert(stack);
    }

    public void applyFilter(String rawQuery) {
        view.setQuery(FilterQuery.parse(rawQuery));
        view.rebuild(storage);
    }

    public void scrollTo(int row) {
        view.scrollTo(row);
    }

    public void sort() {
        StorageSorter.sort(storage);
        view.rebuild(storage);
    }

    /** Gives every item type matching the current filter the given sort priority. */
    public void assignPriority(int priority) {
        if (!PriorityTable.isValid(priority)) {
            throw new IllegalArgumentException("priority out of range: " + priority);
        }
        for (int index : view.matches()) {
            ItemStack stack = storage.get(index);
            if (!stack.isEmpty()) {
                storage.priorities().assign(stack.getItem(), priority);
            }
        }
        view.rebuild(storage);
    }

    /** Drops up to {@code stackLimit} stacks matching the current filter at the player's position. */
    public void eject(Player player, int stackLimit) {
        IntList targets = new IntArrayList(view.matches());
        int ejected = 0;
        for (int index : targets) {
            if (ejected >= stackLimit) {
                break;
            }
            ItemStack stack = storage.take(index);
            if (!stack.isEmpty()) {
                player.drop(stack, false);
                ejected++;
            }
        }
        view.rebuild(storage);
    }

    /** Moves the given part of the player inventory into the storage as far as it fits. */
    public void store(Inventory inventory, StoreTarget target) {
        for (int slot = target.firstSlot(); slot < target.endSlot(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack remainder = storage.insert(stack);
            if (remainder.getCount() != stack.getCount()) {
                inventory.setItem(slot, remainder);
            }
        }
        view.rebuild(storage);
    }
}
