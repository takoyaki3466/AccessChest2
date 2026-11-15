package com.takoy3466.accesschest.fabric.platform;

import com.takoy3466.accesschest.core.platform.INetworkPlatform;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class FabricNetworkPlatform implements INetworkPlatform {

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
