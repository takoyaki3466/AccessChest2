package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.item.AccessChestItem;
import com.takoy3466.accesschest.storage.BoundedStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Access to a shared storage through an Access Chest item. The menu stays valid while the player still carries an
 * Access Chest with the same color and grade.
 */
public record HeldChestAccess(BoundedStorage storage, ChestSpec spec) implements ChestAccess {

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive() && player.getInventory().contains(this::opensSameStorage);
    }

    private boolean opensSameStorage(ItemStack stack) {
        if (!(stack.getItem() instanceof AccessChestItem)) {
            return false;
        }
        ChestSpec held = ChestStacks.specOf(stack);
        return held.color() == spec.color() && held.grade() == spec.grade();
    }
}
