package com.takoy3466.accesschest.neoforge.platform;

import com.takoy3466.accesschest.core.platform.INetworkPlatform;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public final class NeoNetworkPlatform implements INetworkPlatform {

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }
}
