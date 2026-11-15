package com.takoy3466.accesschest.neoforge.platform;

import com.takoy3466.accesschest.client.IClientNetworkPlatform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Loaded through {@code ClientServices} only.
 */
public final class NeoClientNetworkPlatform implements IClientNetworkPlatform {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }
}
