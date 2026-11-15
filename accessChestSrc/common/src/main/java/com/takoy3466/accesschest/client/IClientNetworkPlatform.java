package com.takoy3466.accesschest.client;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client to server sending. Loaded only on the physical client.
 */
public interface IClientNetworkPlatform {

    void sendToServer(CustomPacketPayload payload);
}
