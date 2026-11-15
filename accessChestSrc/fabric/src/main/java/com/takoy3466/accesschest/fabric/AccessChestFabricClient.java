package com.takoy3466.accesschest.fabric;

import com.takoy3466.accesschest.client.ChestColors;
import com.takoy3466.accesschest.client.ModeToggleKeys;
import com.takoy3466.accesschest.client.screen.AccessChestScreen;
import com.takoy3466.accesschest.init.CompatMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

/**
 * Fabric client entry point.
 */
public class AccessChestFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModeToggleKeys.keyMappings().forEach(KeyBindingHelper::registerKeyBinding);
        MenuScreens.register(CompatMenus.STORAGE_CHEST.get(), AccessChestScreen::new);
        ColorProviderRegistry.BLOCK.register(ChestColors.BLOCK_COLOR, ChestColors.tintedBlocks().toArray(Block[]::new));
        ColorProviderRegistry.ITEM.register(ChestColors.ITEM_COLOR, ChestColors.tintedItems().toArray(ItemLike[]::new));
        ClientTickEvents.END_CLIENT_TICK.register(client -> ModeToggleKeys.onClientTick());
        FabricClientNetworking.registerReceivers();
    }
}
