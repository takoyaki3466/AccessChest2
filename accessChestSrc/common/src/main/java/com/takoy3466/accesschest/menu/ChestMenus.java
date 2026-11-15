package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.core.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;

/**
 * Opens chest menus on the server.
 */
public final class ChestMenus {

    private ChestMenus() {
    }

    public static void open(ServerPlayer player, Component title, ChestMenuData data, ChestAccess access) {
        SimpleMenuProvider provider = new SimpleMenuProvider(
                (containerId, inventory, menuPlayer) -> StorageChestMenu.forServer(containerId, inventory, data, access),
                title);
        Services.MENU.openMenu(player, provider, data, ChestMenuData.STREAM_CODEC);
    }
}
