package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client pressed "Eject".
 */
public record EjectPayload() implements CustomPacketPayload {
    public static final EjectPayload INSTANCE = new EjectPayload();
    public static final CustomPacketPayload.Type<EjectPayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("eject"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EjectPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public CustomPacketPayload.Type<EjectPayload> type() {
        return TYPE;
    }
}
