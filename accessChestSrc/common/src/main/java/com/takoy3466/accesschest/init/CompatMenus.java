package com.takoy3466.accesschest.init;

import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.CompatRegistry;
import com.takoy3466.accesschest.menu.ChestMenuData;
import com.takoy3466.accesschest.menu.StorageChestMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Menu types.
 */
public final class CompatMenus {

    public static final CompatHolder<MenuType<StorageChestMenu>> STORAGE_CHEST =
            CompatRegistry.registerMenuType("storage_chest", StorageChestMenu::forClient, ChestMenuData.STREAM_CODEC);

    private CompatMenus() {
    }

    /** Forces class loading so that the static fields above are registered. */
    public static void init() {
    }
}
