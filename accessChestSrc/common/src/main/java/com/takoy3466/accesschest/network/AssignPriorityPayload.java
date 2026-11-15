package com.takoy3466.accesschest.network;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client pressed "Sort" while holding a number key: assign that priority to the filtered items.
 */
public record AssignPriorityPayload(int priority) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AssignPriorityPayload> TYPE = new CustomPacketPayload.Type<>(AccessChestCommon.id("assign_priority"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AssignPriorityPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, AssignPriorityPayload::priority, AssignPriorityPayload::new);

    @Override
    public CustomPacketPayload.Type<AssignPriorityPayload> type() {
        return TYPE;
    }
}
