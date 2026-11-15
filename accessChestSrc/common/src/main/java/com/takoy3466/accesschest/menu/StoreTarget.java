package com.takoy3466.accesschest.menu;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.IntFunction;

/**
 * Part of the player inventory that the "store" buttons move into the chest.
 */
public enum StoreTarget {
    /** The 27 slots above the hotbar. */
    MAIN_INVENTORY(Inventory.getSelectionSize(), Inventory.INVENTORY_SIZE),
    /** The 9 hotbar slots. */
    HOTBAR(0, Inventory.getSelectionSize());

    private static final IntFunction<StoreTarget> BY_ID = ByIdMap.continuous(StoreTarget::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, StoreTarget> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, StoreTarget::ordinal);

    private final int firstSlot;
    private final int endSlot;

    StoreTarget(int firstSlot, int endSlot) {
        this.firstSlot = firstSlot;
        this.endSlot = endSlot;
    }

    /** First inventory index, inclusive. */
    public int firstSlot() {
        return firstSlot;
    }

    /** Last inventory index, exclusive. */
    public int endSlot() {
        return endSlot;
    }
}
