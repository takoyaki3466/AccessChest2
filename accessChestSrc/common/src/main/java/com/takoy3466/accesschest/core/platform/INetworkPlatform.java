package com.takoy3466.accesschest.core.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Server to client sending. Payload registration is driven by {@code ModNetwork} in each loader module.
 */
public interface INetworkPlatform {

    void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);
}
