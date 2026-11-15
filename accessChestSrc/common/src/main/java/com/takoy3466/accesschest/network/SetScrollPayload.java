package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client asks to show the chest from the given row on. The server clamps the row.
 */
public record SetScrollPayload(int row) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetScrollPayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("set_scroll"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetScrollPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, SetScrollPayload::row, SetScrollPayload::new);

    @Override
    public CustomPacketPayload.Type<SetScrollPayload> type() {
        return TYPE;
    }
}
