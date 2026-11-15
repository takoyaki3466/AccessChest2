package com.takoy3466.accesschest.forge;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.network.ClientboundEntry;
import com.takoy3466.accesschest.network.ModNetwork;
import com.takoy3466.accesschest.network.ServerboundEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.payload.PayloadFlow;
import net.minecraftforge.network.payload.PayloadProtocol;

/**
 * Builds the Forge payload channel from {@link ModNetwork}. Handlers are moved to the main thread with {@code addMain}.
 */
public final class ForgeNetworking {
    private static Channel<CustomPacketPayload> channel;

    private ForgeNetworking() {
    }

    static void init() {
        PayloadProtocol<RegistryFriendlyByteBuf, CustomPacketPayload> play = ChannelBuilder
                .named(AccessChestCommon.id(ModNetwork.CHANNEL_NAME))
                .networkProtocolVersion(ModNetwork.PROTOCOL_VERSION)
                .payloadChannel()
                .play();

        PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> serverbound = play.serverbound();
        for (ServerboundEntry<?> entry : ModNetwork.SERVERBOUND) {
            serverbound = addServerbound(serverbound, entry);
        }
        PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> clientbound = serverbound.clientbound();
        for (ClientboundEntry<?> entry : ModNetwork.CLIENTBOUND) {
            clientbound = addClientbound(clientbound, entry);
        }
        channel = clientbound.build();
    }

    private static <T extends CustomPacketPayload> PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> addServerbound(
            PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> flow, ServerboundEntry<T> entry) {
        return flow.addMain(entry.type(), entry.codec(), (payload, context) -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                entry.handler().handle(payload, player);
            }
        });
    }

    private static <T extends CustomPacketPayload> PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> addClientbound(
            PayloadFlow<RegistryFriendlyByteBuf, CustomPacketPayload> flow, ClientboundEntry<T> entry) {
        return flow.addMain(entry.type(), entry.codec(), (payload, context) -> entry.handler().accept(payload));
    }

    public static Channel<CustomPacketPayload> channel() {
        if (channel == null) {
            throw new IllegalStateException("Access Chest network channel is not initialised yet");
        }
        return channel;
    }
}
