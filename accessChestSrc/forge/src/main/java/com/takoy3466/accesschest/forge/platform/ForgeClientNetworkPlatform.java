package com.takoy3466.accesschest.forge.platform;

import com.takoy3466.accesschest.client.IClientNetworkPlatform;
import com.takoy3466.accesschest.forge.ForgeNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.network.PacketDistributor;

/**
 * Loaded through {@code ClientServices} only.
 */
public final class ForgeClientNetworkPlatform implements IClientNetworkPlatform {

    @Override
    public void sendToServer(CustomPacketPayload payload) {
        ForgeNetworking.channel().send(payload, PacketDistributor.SERVER.noArg());
    }
}
