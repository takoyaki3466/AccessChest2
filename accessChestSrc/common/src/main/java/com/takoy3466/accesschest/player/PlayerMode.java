package com.takoy3466.accesschest.player;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

/**
 * Per player switches toggled with key bindings.
 */
public enum PlayerMode implements StringRepresentable {
    /** Right clicking a container with an Access Chest moves items between them. */
    COLLECTIVE_TRANSFER("collective_transfer"),
    /** Picked up items that do not fit into the inventory go into carried Access Chests. */
    AUTO_COLLECT("auto_collect");

    private static final IntFunction<PlayerMode> BY_ID = ByIdMap.continuous(PlayerMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, PlayerMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, PlayerMode::ordinal);
    public static final StringRepresentable.EnumCodec<PlayerMode> CODEC = StringRepresentable.fromEnum(PlayerMode::values);

    private final String name;

    PlayerMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    /** Translation key of the mode name, also used for the key binding. */
    public String translationKey() {
        return "key.accesschest." + name;
    }
}
