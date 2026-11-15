package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.menu.StoreTarget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client pressed one of the "store" buttons.
 */
public record StorePayload(StoreTarget target) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<StorePayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("store"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StorePayload> STREAM_CODEC =
            StreamCodec.composite(StoreTarget.STREAM_CODEC, StorePayload::target, StorePayload::new);

    @Override
    public CustomPacketPayload.Type<StorePayload> type() {
        return TYPE;
    }
}
