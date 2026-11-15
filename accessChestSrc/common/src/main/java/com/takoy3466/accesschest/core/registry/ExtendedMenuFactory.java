package com.takoy3466.accesschest.core.registry;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

/**
 * Creates the client side menu from the data the server sent when opening it.
 */
@FunctionalInterface
public interface ExtendedMenuFactory<T extends AbstractContainerMenu, D> {
    T create(int containerId, Inventory inventory, D data);
}
