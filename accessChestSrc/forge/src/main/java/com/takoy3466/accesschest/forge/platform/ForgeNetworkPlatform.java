package com.takoy3466.accesschest.forge.platform;

import com.takoy3466.accesschest.core.platform.INetworkPlatform;
import com.takoy3466.accesschest.forge.ForgeNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class ForgeNetworkPlatform implements INetworkPlatform {

    @Override
    public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ForgeNetworking.channel().send(payload, PacketDistributor.PLAYER.with(player));
    }
}
