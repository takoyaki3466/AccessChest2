package com.takoy3466.accesschest.forge;

import com.takoy3466.accesschest.client.ChestColors;
import com.takoy3466.accesschest.client.ModeToggleKeys;
import com.takoy3466.accesschest.client.screen.AccessChestScreen;
import com.takoy3466.accesschest.init.CompatMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client setup. Only referenced when running on the physical client.
 */
final class AccessChestForgeClient {

    private AccessChestForgeClient() {
    }

    static void init(IEventBus modBus) {
        modBus.addListener(AccessChestForgeClient::onClientSetup);
        modBus.addListener(AccessChestForgeClient::onRegisterKeyMappings);
        modBus.addListener(AccessChestForgeClient::onRegisterBlockColors);
        modBus.addListener(AccessChestForgeClient::onRegisterItemColors);
        MinecraftForge.EVENT_BUS.addListener(AccessChestForgeClient::onClientTick);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        // MenuScreens is not thread safe; register on the main thread.
        event.enqueueWork(() -> MenuScreens.register(CompatMenus.STORAGE_CHEST.get(), AccessChestScreen::new));
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ModeToggleKeys.keyMappings().forEach(event::register);
    }

    private static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(ChestColors.BLOCK_COLOR, ChestColors.tintedBlocks().toArray(Block[]::new));
    }

    private static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(ChestColors.ITEM_COLOR, ChestColors.tintedItems().toArray(ItemLike[]::new));
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        ModeToggleKeys.onClientTick();
    }
}
