package com.takoy3466.accesschest.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.function.Consumer;

/**
 * A server to client payload with its codec and client side handler. The handler runs on the client thread and must
 * not reference client only classes, because it is registered on dedicated servers too.
 */
public record ClientboundEntry<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<RegistryFriendlyByteBuf, T> codec,
        Consumer<T> handler) {
}
