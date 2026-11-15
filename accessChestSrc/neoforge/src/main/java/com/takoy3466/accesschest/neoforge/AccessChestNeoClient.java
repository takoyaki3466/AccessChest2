package com.takoy3466.accesschest.neoforge;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.client.ChestColors;
import com.takoy3466.accesschest.client.ModeToggleKeys;
import com.takoy3466.accesschest.client.screen.AccessChestScreen;
import com.takoy3466.accesschest.init.CompatMenus;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * NeoForge client entry point; only constructed on the physical client.
 */
@Mod(value = AccessChestCommon.MOD_ID, dist = Dist.CLIENT)
public class AccessChestNeoClient {

    public AccessChestNeoClient(IEventBus modBus) {
        modBus.addListener(AccessChestNeoClient::onRegisterKeyMappings);
        modBus.addListener(AccessChestNeoClient::onRegisterMenuScreens);
        modBus.addListener(AccessChestNeoClient::onRegisterBlockColors);
        modBus.addListener(AccessChestNeoClient::onRegisterItemColors);
        NeoForge.EVENT_BUS.addListener(AccessChestNeoClient::onClientTick);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        ModeToggleKeys.keyMappings().forEach(event::register);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(CompatMenus.STORAGE_CHEST.get(), AccessChestScreen::new);
    }

    private static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(ChestColors.BLOCK_COLOR, ChestColors.tintedBlocks().toArray(Block[]::new));
    }

    private static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(ChestColors.ITEM_COLOR, ChestColors.tintedItems().toArray(ItemLike[]::new));
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        ModeToggleKeys.onClientTick();
    }
}
