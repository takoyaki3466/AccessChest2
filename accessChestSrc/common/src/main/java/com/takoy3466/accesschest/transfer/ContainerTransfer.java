package com.takoy3466.accesschest.transfer;

import com.takoy3466.accesschest.storage.BoundedStorage;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * Bulk item movement between a chest storage and another container.
 */
public final class ContainerTransfer {

    private ContainerTransfer() {
    }

    /** Moves everything from {@code source} that the storage accepts and has room for into the storage. */
    public static void extract(Container source, BoundedStorage target) {
        for (int slot = 0; slot < source.getContainerSize(); slot++) {
            ItemStack stack = source.getItem(slot);
            if (!target.canStore(stack)) {
                continue;
            }
            ItemStack remainder = target.insert(stack);
            if (remainder.getCount() != stack.getCount()) {
                source.setItem(slot, remainder);
            }
        }
        source.setChanged();
    }

    /** Fills the empty slots of {@code target} with stacks from the storage, in storage order. */
    public static void pour(BoundedStorage source, Container target) {
        int next = 0;
        for (int slot = 0; slot < target.getContainerSize(); slot++) {
            if (!target.getItem(slot).isEmpty()) {
                continue;
            }
            next = nextNonEmpty(source, next);
            if (next >= source.size()) {
                break;
            }
            ItemStack candidate = source.get(next);
            if (!target.canPlaceItem(slot, candidate)) {
                continue;
            }
            int amount = Math.min(candidate.getCount(), target.getMaxStackSize(candidate));
            target.setItem(slot, source.remove(next, amount));
        }
        target.setChanged();
    }

    private static int nextNonEmpty(BoundedStorage storage, int from) {
        int index = from;
        while (index < storage.size() && storage.get(index).isEmpty()) {
            index++;
        }
        return index;
    }
}
