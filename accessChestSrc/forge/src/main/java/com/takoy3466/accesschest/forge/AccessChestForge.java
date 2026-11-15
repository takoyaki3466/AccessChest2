package com.takoy3466.accesschest.forge;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.forge.platform.ForgeDeferredRegisters;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.transfer.CollectiveTransferHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/**
 * Forge entry point. Client only setup is delegated to {@link AccessChestForgeClient} on the physical client.
 */
@Mod(AccessChestCommon.MOD_ID)
public class AccessChestForge {

    public AccessChestForge(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ForgeDeferredRegisters.register(modBus);
        AccessChestCommon.init();
        ForgeNetworking.init();

        MinecraftForge.EVENT_BUS.addListener(AccessChestForge::onRightClickBlock);
        MinecraftForge.EVENT_BUS.addListener(AccessChestForge::onPlayerLoggedIn);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            AccessChestForgeClient.init(modBus);
        }
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
