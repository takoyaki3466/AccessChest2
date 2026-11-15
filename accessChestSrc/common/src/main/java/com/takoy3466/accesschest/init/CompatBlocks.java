package com.takoy3466.accesschest.init;

import com.takoy3466.accesschest.block.AccessChestBlock;
import com.takoy3466.accesschest.block.CompressedChestBlock;
import com.takoy3466.accesschest.block.StorageChestBlock;
import com.takoy3466.accesschest.core.registry.CompatBlockHolder;
import com.takoy3466.accesschest.core.registry.CompatRegistry;
import com.takoy3466.accesschest.item.AccessChestItem;
import com.takoy3466.accesschest.item.StorageChestItem;
import net.minecraft.world.item.Item;

/**
 * Blocks and their block items.
 */
public final class CompatBlocks {

    public static final CompatBlockHolder<AccessChestBlock, AccessChestItem> ACCESS_CHEST = CompatRegistry.registerBlock(
            "access_chest",
            () -> new AccessChestBlock(StorageChestBlock.defaultProperties()),
            block -> new AccessChestItem(block, new Item.Properties()));

    public static final CompatBlockHolder<CompressedChestBlock, StorageChestItem> COMPRESSED_CHEST = CompatRegistry.registerBlock(
            "compressed_chest",
            () -> new CompressedChestBlock(StorageChestBlock.defaultProperties()),
            block -> new StorageChestItem(block, new Item.Properties()));

    private CompatBlocks() {
    }

    /** Forces class loading so that the static fields above are registered. */
    public static void init() {
    }
}
