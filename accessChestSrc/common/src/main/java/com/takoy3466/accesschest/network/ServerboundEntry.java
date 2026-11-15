package com.takoy3466.accesschest.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * A client to server payload with its codec and server side handler. The handler runs on the server thread.
 */
public record ServerboundEntry<T extends CustomPacketPayload>(
        CustomPacketPayload.Type<T> type,
        StreamCodec<RegistryFriendlyByteBuf, T> codec,
        Handler<T> handler) {

    @FunctionalInterface
    public interface Handler<T> {
        void handle(T payload, ServerPlayer player);
    }
}
