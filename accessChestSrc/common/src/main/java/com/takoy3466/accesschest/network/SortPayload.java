package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client pressed "Sort".
 */
public record SortPayload() implements CustomPacketPayload {
    public static final SortPayload INSTANCE = new SortPayload();
    public static final CustomPacketPayload.Type<SortPayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("sort"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SortPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public CustomPacketPayload.Type<SortPayload> type() {
        return TYPE;
    }
}
