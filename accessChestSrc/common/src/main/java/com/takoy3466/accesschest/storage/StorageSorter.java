package com.takoy3466.accesschest.storage;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts the visible range of a storage and merges equal stacks.
 * Order: priority, registry id, components, larger stacks first.
 */
public final class StorageSorter {

    private StorageSorter() {
    }

    public static void sort(BoundedStorage storage) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < storage.size(); i++) {
            ItemStack stack = storage.get(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        stacks.sort(comparator(storage.priorities()));
        List<ItemStack> merged = merge(stacks);

        ChestStorage backing = storage.storage();
        for (int i = 0; i < storage.size(); i++) {
            backing.restore(i, i < merged.size() ? merged.get(i) : ItemStack.EMPTY);
        }
        backing.markChanged();
    }

    private static Comparator<ItemStack> comparator(PriorityTable priorities) {
        return Comparator.<ItemStack>comparingInt(priorities::priorityOf)
                .thenComparing(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString())
                // Groups stacks with equal components next to each other so that merge() can combine them.
                .thenComparingInt(stack -> stack.getComponentsPatch().hashCode())
                .thenComparing(Comparator.comparingInt(ItemStack::getCount).reversed());
    }

    /** Combines neighbouring stacks of the same item and components. The input stacks are reused. */
    private static List<ItemStack> merge(List<ItemStack> sorted) {
        List<ItemStack> merged = new ArrayList<>(sorted.size());
        for (ItemStack stack : sorted) {
            if (!merged.isEmpty()) {
                ItemStack last = merged.get(merged.size() - 1);
                if (ItemStack.isSameItemSameComponents(last, stack)) {
                    int moved = Math.min(last.getMaxStackSize() - last.getCount(), stack.getCount());
                    last.grow(moved);
                    stack.shrink(moved);
                }
            }
            if (!stack.isEmpty()) {
                merged.add(stack);
            }
        }
        return merged;
    }
}
