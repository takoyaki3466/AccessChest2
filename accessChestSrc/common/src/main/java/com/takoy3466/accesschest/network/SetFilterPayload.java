package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.storage.FilterQuery;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client changed the search text. The codec rejects texts longer than {@link FilterQuery#MAX_LENGTH}.
 */
public record SetFilterPayload(String text) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SetFilterPayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("set_filter"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetFilterPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.stringUtf8(FilterQuery.MAX_LENGTH), SetFilterPayload::text, SetFilterPayload::new);

    @Override
    public CustomPacketPayload.Type<SetFilterPayload> type() {
        return TYPE;
    }
}
