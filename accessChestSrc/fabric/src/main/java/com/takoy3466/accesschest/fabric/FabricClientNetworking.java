package com.takoy3466.accesschest.fabric;

import com.takoy3466.accesschest.network.ClientboundEntry;
import com.takoy3466.accesschest.network.ModNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client receivers of the server to client payloads. Only loaded from the client entry point.
 */
final class FabricClientNetworking {

    private FabricClientNetworking() {
    }

    static void registerReceivers() {
        for (ClientboundEntry<?> entry : ModNetwork.CLIENTBOUND) {
            registerReceiver(entry);
        }
    }

    private static <T extends CustomPacketPayload> void registerReceiver(ClientboundEntry<T> entry) {
        // Fabric calls play payload handlers on the client (render) thread.
        ClientPlayNetworking.registerGlobalReceiver(entry.type(), (payload, context) -> entry.handler().accept(payload));
    }
}
