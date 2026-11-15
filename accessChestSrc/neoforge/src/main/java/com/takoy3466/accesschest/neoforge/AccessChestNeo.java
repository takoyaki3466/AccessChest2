package com.takoy3466.accesschest.neoforge;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.neoforge.platform.NeoDeferredRegisters;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.transfer.CollectiveTransferHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/**
 * NeoForge common entry point. Client only setup lives in {@link AccessChestNeoClient}.
 */
@Mod(AccessChestCommon.MOD_ID)
public class AccessChestNeo {

    public AccessChestNeo(IEventBus modBus) {
        NeoDeferredRegisters.register(modBus);
        AccessChestCommon.init();

        modBus.addListener(NeoNetworking::register);
        NeoForge.EVENT_BUS.addListener(AccessChestNeo::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(AccessChestNeo::onPlayerLoggedIn);
    }

    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = CollectiveTransferHandler.onUseBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getPos());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerModes.sync(player);
        }
    }
}
