package com.takoy3466.accesschest.fabric;

import com.takoy3466.accesschest.network.ClientboundEntry;
import com.takoy3466.accesschest.network.ModNetwork;
import com.takoy3466.accesschest.network.ServerboundEntry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Registers the payload types (both directions, on both sides) and the server receivers.
 * Client receivers are registered by {@link FabricClientNetworking} from the client entry point.
 */
final class FabricNetworking {

    private FabricNetworking() {
    }

    static void registerCommon() {
        for (ServerboundEntry<?> entry : ModNetwork.SERVERBOUND) {
            registerServerbound(entry);
        }
        for (ClientboundEntry<?> entry : ModNetwork.CLIENTBOUND) {
            registerClientboundType(entry);
        }
    }

    private static <T extends CustomPacketPayload> void registerServerbound(ServerboundEntry<T> entry) {
        PayloadTypeRegistry.playC2S().register(entry.type(), entry.codec());
        // Fabric calls play payload handlers on the server thread.
        ServerPlayNetworking.registerGlobalReceiver(entry.type(), (payload, context) -> entry.handler().handle(payload, context.player()));
    }

    private static <T extends CustomPacketPayload> void registerClientboundType(ClientboundEntry<T> entry) {
        PayloadTypeRegistry.playS2C().register(entry.type(), entry.codec());
    }
}
