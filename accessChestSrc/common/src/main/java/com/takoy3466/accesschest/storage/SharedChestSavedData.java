package com.takoy3466.accesschest.storage;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestKind;
import com.takoy3466.accesschest.chest.ChestSpec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * World wide storage shared by every Access Chest of one color.
 * Stored in the overworld data folder as {@code accesschest_<color>.dat}, independent of the dimension a chest is in.
 * The capacity is always the size of the highest grade; lower grades only see the beginning of it.
 */
public final class SharedChestSavedData extends SavedData {

    private final ChestStorage storage = new ChestStorage(ChestGrade.HIGHEST.slotCount(), this::setDirty);

    private SharedChestSavedData() {
    }

    private static SavedData.Factory<SharedChestSavedData> factory() {
        // Item stacks are handled by their own codec, so no vanilla data fixer type applies.
        return new SavedData.Factory<>(SharedChestSavedData::new, SharedChestSavedData::load, null);
    }

    private static SharedChestSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        SharedChestSavedData data = new SharedChestSavedData();
        StorageNbt.read(tag, data.storage, registries);
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.merge(StorageNbt.write(storage, registries));
        return tag;
    }

    public static String dataName(DyeColor color) {
        return AccessChestCommon.MOD_ID + "_" + color.getName();
    }

    public static ChestStorage storage(MinecraftServer server, DyeColor color) {
        return server.overworld().getDataStorage().computeIfAbsent(factory(), dataName(color)).storage;
    }

    /**
     * @return the part of the shared storage visible to an Access Chest with {@code spec}
     */
    public static BoundedStorage boundedStorage(MinecraftServer server, ChestSpec spec) {
        return BoundedStorage.of(storage(server, spec.color()), spec.grade(), ChestKind.ACCESS);
    }
}
