package com.takoy3466.accesschest.block.entity;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestKind;
import com.takoy3466.accesschest.init.CompatBlockEntities;
import com.takoy3466.accesschest.storage.BoundedStorage;
import com.takoy3466.accesschest.storage.ChestStorage;
import com.takoy3466.accesschest.storage.StorageNbt;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Placed Compressed Chest. The items are stored in this block entity and dropped when the block is broken.
 */
public class CompressedChestBlockEntity extends StorageChestBlockEntity {
    private static final String STORAGE_KEY = "storage";

    private ChestStorage storage = createStorage(ChestGrade.LOWEST);

    public CompressedChestBlockEntity(BlockPos pos, BlockState state) {
        super(CompatBlockEntities.COMPRESSED_CHEST.get(), pos, state);
    }

    private ChestStorage createStorage(ChestGrade grade) {
        return new ChestStorage(grade.slotCount(), this::setChanged);
    }

    /**
     * The grade is normally assigned once, before anything is stored. If it changes later (for example through
     * commands), the items are carried over; items that no longer fit are reported instead of silently dropped.
     */
    @Override
    protected void onGradeAssigned(ChestGrade grade) {
        if (storage.capacity() == grade.slotCount()) {
            return;
        }
        ChestStorage resized = createStorage(grade);
        for (int i = 0; i < storage.capacity(); i++) {
            ItemStack stack = storage.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (i < resized.capacity()) {
                resized.set(i, stack);
            } else {
                AccessChestCommon.LOGGER.error("Compressed Chest at {} lost {} after its grade changed to {}", getBlockPos(), stack, grade);
            }
        }
        storage = resized;
    }

    @Override
    protected Optional<BoundedStorage> serverStorage() {
        if (level != null && !level.isClientSide) {
            return Optional.of(BoundedStorage.of(storage, grade(), ChestKind.COMPRESSED));
        }
        return Optional.empty();
    }

    @Override
    public void clearContent() {
        storage.clear();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(STORAGE_KEY, Tag.TAG_COMPOUND)) {
            storage = createStorage(grade());
            StorageNbt.read(tag.getCompound(STORAGE_KEY), storage, registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(STORAGE_KEY, StorageNbt.write(storage, registries));
    }
}
