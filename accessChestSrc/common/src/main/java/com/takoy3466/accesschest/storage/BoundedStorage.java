package com.takoy3466.accesschest.storage;

import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestKind;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * The part of a {@link ChestStorage} that a chest of a given grade can see, together with the rule of which stacks it
 * accepts. All item movement of the mod goes through this class so that the range and the rule are enforced uniformly.
 *
 * @param storage       backing storage
 * @param size          number of visible slots, starting at index 0
 * @param insertionRule stacks that may be inserted
 */
public record BoundedStorage(ChestStorage storage, int size, Predicate<ItemStack> insertionRule) {

    public BoundedStorage {
        Objects.requireNonNull(storage, "storage");
        Objects.requireNonNull(insertionRule, "insertionRule");
        if (size <= 0 || size > storage.capacity()) {
            throw new IllegalArgumentException("size " + size + " does not fit capacity " + storage.capacity());
        }
    }

    public static BoundedStorage of(ChestStorage storage, ChestGrade grade, ChestKind kind) {
        return new BoundedStorage(storage, grade.slotCount(), kind::accepts);
    }

    public ItemStack get(int index) {
        Objects.checkIndex(index, size);
        return storage.get(index);
    }

    public void set(int index, ItemStack stack) {
        Objects.checkIndex(index, size);
        storage.set(index, stack);
    }

    public ItemStack remove(int index, int amount) {
        Objects.checkIndex(index, size);
        return storage.remove(index, amount);
    }

    public ItemStack take(int index) {
        Objects.checkIndex(index, size);
        return storage.take(index);
    }

    public boolean canStore(ItemStack stack) {
        return !stack.isEmpty() && insertionRule.test(stack);
    }

    public PriorityTable priorities() {
        return storage.priorities();
    }

    /**
     * Inserts as much of {@code stack} as possible: first into matching stacks, then into empty slots.
     * The given stack is not modified.
     *
     * @return what could not be inserted ({@link ItemStack#EMPTY} if everything fit)
     */
    public ItemStack insert(ItemStack stack) {
        if (!canStore(stack)) {
            return stack;
        }
        ItemStack remaining = stack.copy();
        boolean changed = mergeIntoExisting(remaining);
        if (!remaining.isEmpty()) {
            changed |= fillEmptySlots(remaining);
        }
        if (changed) {
            storage.markChanged();
        }
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }

    private boolean mergeIntoExisting(ItemStack remaining) {
        boolean changed = false;
        for (int i = 0; i < size && !remaining.isEmpty(); i++) {
            ItemStack existing = storage.get(i);
            if (existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, remaining)) {
                continue;
            }
            int moved = Math.min(existing.getMaxStackSize() - existing.getCount(), remaining.getCount());
            if (moved > 0) {
                existing.grow(moved);
                remaining.shrink(moved);
                changed = true;
            }
        }
        return changed;
    }

    private boolean fillEmptySlots(ItemStack remaining) {
        boolean changed = false;
        for (int i = 0; i < size && !remaining.isEmpty(); i++) {
            if (storage.get(i).isEmpty()) {
                storage.restore(i, remaining.split(remaining.getMaxStackSize()));
                changed = true;
            }
        }
        return changed;
    }
}
