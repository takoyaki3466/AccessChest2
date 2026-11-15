package com.takoy3466.accesschest.fabric;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.transfer.CollectiveTransferHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

/**
 * Fabric common entry point.
 */
public class AccessChestFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        AccessChestCommon.init();
        FabricNetworking.registerCommon();

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) ->
                CollectiveTransferHandler.onUseBlock(player, level, hand, hitResult.getBlockPos()));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PlayerModes.sync(handler.getPlayer()));
    }
}
