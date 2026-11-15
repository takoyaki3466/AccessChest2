package com.takoy3466.accesschest.block.entity;

import com.takoy3466.accesschest.init.CompatBlockEntities;
import com.takoy3466.accesschest.storage.BoundedStorage;
import com.takoy3466.accesschest.storage.SharedChestSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Placed Access Chest. It owns no items itself; it is a window into the shared storage of its color.
 */
public class AccessChestBlockEntity extends StorageChestBlockEntity {

    public AccessChestBlockEntity(BlockPos pos, BlockState state) {
        super(CompatBlockEntities.ACCESS_CHEST.get(), pos, state);
    }

    @Override
    protected Optional<BoundedStorage> serverStorage() {
        if (level instanceof ServerLevel serverLevel) {
            return Optional.of(SharedChestSavedData.boundedStorage(serverLevel.getServer(), spec()));
        }
        return Optional.empty();
    }

    /**
     * Intentionally keeps the shared storage untouched: the items belong to every Access Chest of this color, so
     * replacing or clearing this one block (for example with {@code /setblock}) must not delete them.
     */
    @Override
    public void clearContent() {
    }
}
