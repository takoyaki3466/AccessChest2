package com.takoy3466.accesschest.fabric.platform;

import com.takoy3466.accesschest.client.IClientNetworkPlatform;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Loaded through {@code ClientServices} only, so dedicated servers never load this class.
 */
public final class FabricClientNetworkPlatform implements IClientNetworkPlatform {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }
}
