package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.chest.ChestKind;
import com.takoy3466.accesschest.chest.ChestSpec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data sent to the client when a chest menu opens.
 *
 * @param kind   decides which stacks the client lets the player put into the window
 * @param spec   the chest that was opened
 * @param remote true when opened from a held item; the client then remembers the scroll position
 */
public record ChestMenuData(ChestKind kind, ChestSpec spec, boolean remote) {

    public static final StreamCodec<ByteBuf, ChestMenuData> STREAM_CODEC = StreamCodec.composite(
            ChestKind.STREAM_CODEC, ChestMenuData::kind,
            ChestSpec.STREAM_CODEC, ChestMenuData::spec,
            ByteBufCodecs.BOOL, ChestMenuData::remote,
            ChestMenuData::new
    );
}
