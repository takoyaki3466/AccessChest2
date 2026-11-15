package com.takoy3466.accesschest.client;

import com.takoy3466.accesschest.block.StorageChestBlock;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.init.CompatBlocks;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Tints the chest models with the chest color. Faces with {@link #CHEST_TINT_INDEX} in the model are tinted.
 */
public final class ChestColors {
    public static final int CHEST_TINT_INDEX = 0;
    /** Opaque white, i.e. no tint. */
    private static final int NO_TINT = 0xFFFFFFFF;

    public static final BlockColor BLOCK_COLOR = (state, level, pos, tintIndex) ->
            tintIndex == CHEST_TINT_INDEX ? state.getValue(StorageChestBlock.COLOR).getTextureDiffuseColor() : NO_TINT;

    public static final ItemColor ITEM_COLOR = (stack, tintIndex) ->
            tintIndex == CHEST_TINT_INDEX ? ChestStacks.specOf(stack).color().getTextureDiffuseColor() : NO_TINT;

    private ChestColors() {
    }

    public static List<Block> tintedBlocks() {
        return List.of(CompatBlocks.ACCESS_CHEST.block(), CompatBlocks.COMPRESSED_CHEST.block());
    }

    public static List<Item> tintedItems() {
        return List.of(CompatBlocks.ACCESS_CHEST.item(), CompatBlocks.COMPRESSED_CHEST.item());
    }
}
