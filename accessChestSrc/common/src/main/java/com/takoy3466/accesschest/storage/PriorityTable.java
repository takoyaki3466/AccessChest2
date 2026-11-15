package com.takoy3466.accesschest.storage;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Sort priority per item type. Lower values are sorted first. Items without an entry use {@link #DEFAULT_PRIORITY}.
 */
public final class PriorityTable {
    public static final int MIN_PRIORITY = 0;
    public static final int MAX_PRIORITY = 9;
    public static final int DEFAULT_PRIORITY = 7;

    private final Map<Item, Integer> priorities = new HashMap<>();
    private final Runnable changeListener;

    PriorityTable(Runnable changeListener) {
        this.changeListener = changeListener;
    }

    public static boolean isValid(int priority) {
        return priority >= MIN_PRIORITY && priority <= MAX_PRIORITY;
    }

    public int priorityOf(ItemStack stack) {
        return priorities.getOrDefault(stack.getItem(), DEFAULT_PRIORITY);
    }

    public void assign(Item item, int priority) {
        if (!isValid(priority)) {
            throw new IllegalArgumentException("priority out of range: " + priority);
        }
        Integer previous = priority == DEFAULT_PRIORITY ? priorities.remove(item) : priorities.put(item, priority);
        int previousPriority = previous == null ? DEFAULT_PRIORITY : previous;
        if (previousPriority != priority) {
            changeListener.run();
        }
    }

    void forEach(BiConsumer<Item, Integer> action) {
        priorities.forEach(action);
    }

    /** Adds an entry while loading, without reporting a change. */
    void restore(Item item, int priority) {
        if (isValid(priority) && priority != DEFAULT_PRIORITY) {
            priorities.put(item, priority);
        }
    }
}
