package com.takoy3466.accesschest.chest;

import com.takoy3466.accesschest.item.AccessChestItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntFunction;

/**
 * The two kinds of chests this mod adds.
 */
public enum ChestKind {
    /** Contents are shared world wide by every Access Chest of the same color. */
    ACCESS(false),
    /** Contents are stored in the block itself. */
    COMPRESSED(true);

    private static final IntFunction<ChestKind> BY_ID = ByIdMap.continuous(ChestKind::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, ChestKind> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, ChestKind::ordinal);

    private final boolean acceptsAccessChests;

    ChestKind(boolean acceptsAccessChests) {
        this.acceptsAccessChests = acceptsAccessChests;
    }

    /**
     * Whether a stack may be stored in a chest of this kind.
     * Access Chests are never stored inside an Access Chest storage so that a shared storage can not contain a key to itself.
     */
    public boolean accepts(ItemStack stack) {
        return acceptsAccessChests || !(stack.getItem() instanceof AccessChestItem);
    }
}
