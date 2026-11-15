package com.takoy3466.accesschest.init;

import com.takoy3466.accesschest.block.entity.AccessChestBlockEntity;
import com.takoy3466.accesschest.block.entity.CompressedChestBlockEntity;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.CompatRegistry;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Block entity types.
 */
public final class CompatBlockEntities {

    public static final CompatHolder<BlockEntityType<AccessChestBlockEntity>> ACCESS_CHEST = CompatRegistry.registerBlockEntityType(
            "access_chest", AccessChestBlockEntity::new, CompatBlocks.ACCESS_CHEST.blockHolder());

    public static final CompatHolder<BlockEntityType<CompressedChestBlockEntity>> COMPRESSED_CHEST = CompatRegistry.registerBlockEntityType(
            "compressed_chest", CompressedChestBlockEntity::new, CompatBlocks.COMPRESSED_CHEST.blockHolder());

    private CompatBlockEntities() {
    }

    /** Forces class loading so that the static fields above are registered. */
    public static void init() {
    }
}
