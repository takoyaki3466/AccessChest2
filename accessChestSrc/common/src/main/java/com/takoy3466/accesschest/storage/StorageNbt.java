package com.takoy3466.accesschest.storage;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Versioned NBT format of a {@link ChestStorage}.
 * <pre>
 * { format: int, capacity: int, items: [{slot: int, ...item stack}], priorities: [{item: "id", priority: int}] }
 * </pre>
 * Slots are stored sparsely because large storages are mostly empty.
 */
public final class StorageNbt {

    /** Increase when the layout changes and add a migration in {@link #read}. */
    public static final int FORMAT_VERSION = 1;

    private static final String FORMAT_KEY = "format";
    private static final String CAPACITY_KEY = "capacity";
    private static final String ITEMS_KEY = "items";
    private static final String SLOT_KEY = "slot";
    private static final String PRIORITIES_KEY = "priorities";
    private static final String ITEM_KEY = "item";
    private static final String PRIORITY_KEY = "priority";

    private StorageNbt() {
    }

    public static CompoundTag write(ChestStorage storage, HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(FORMAT_KEY, FORMAT_VERSION);
        tag.putInt(CAPACITY_KEY, storage.capacity());

        ListTag items = new ListTag();
        for (int slot = 0; slot < storage.capacity(); slot++) {
            ItemStack stack = storage.get(slot);
            if (!stack.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                entry.putInt(SLOT_KEY, slot);
                items.add(stack.save(registries, entry));
            }
        }
        tag.put(ITEMS_KEY, items);

        ListTag priorities = new ListTag();
        storage.priorities().forEach((item, priority) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString(ITEM_KEY, BuiltInRegistries.ITEM.getKey(item).toString());
            entry.putInt(PRIORITY_KEY, priority);
            priorities.add(entry);
        });
        tag.put(PRIORITIES_KEY, priorities);
        return tag;
    }

    /**
     * Loads {@code tag} into an empty storage.
     *
     * @throws IllegalStateException if the data was written by a newer, unknown format version
     */
    public static void read(CompoundTag tag, ChestStorage storage, HolderLookup.Provider registries) {
        int format = tag.getInt(FORMAT_KEY);
        if (format > FORMAT_VERSION) {
            throw new IllegalStateException("Access Chest storage format " + format + " is newer than supported format " + FORMAT_VERSION);
        }

        ListTag items = tag.getList(ITEMS_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag entry = items.getCompound(i);
            int slot = entry.getInt(SLOT_KEY);
            if (slot < 0 || slot >= storage.capacity()) {
                AccessChestCommon.LOGGER.error("Skipping stored item in slot {} outside of capacity {}: {}", slot, storage.capacity(), entry);
                continue;
            }
            ItemStack.parse(registries, entry).ifPresentOrElse(
                    stack -> storage.restore(slot, stack),
                    () -> AccessChestCommon.LOGGER.error("Could not read stored item in slot {}: {}", slot, entry));
        }

        ListTag priorities = tag.getList(PRIORITIES_KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < priorities.size(); i++) {
            CompoundTag entry = priorities.getCompound(i);
            ResourceLocation id = ResourceLocation.tryParse(entry.getString(ITEM_KEY));
            if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
                AccessChestCommon.LOGGER.warn("Dropping sort priority of unknown item {}", entry.getString(ITEM_KEY));
                continue;
            }
            Item item = BuiltInRegistries.ITEM.get(id);
            storage.priorities().restore(item, entry.getInt(PRIORITY_KEY));
        }
    }
}
