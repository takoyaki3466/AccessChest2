package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.chest.ChestKind;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.Slot;

/**
 * One of the fixed chest slots of the menu. It is only usable while the view maps it to a storage slot.
 */
final class WindowSlot extends Slot {
    private final ViewState viewState;
    private final ChestKind kind;

    WindowSlot(Container window, int windowIndex, int x, int y, ViewState viewState, ChestKind kind) {
        super(window, windowIndex, x, y);
        this.viewState = viewState;
        this.kind = kind;
    }

    @Override
    public boolean isActive() {
        return viewState.isVisible(getContainerSlot());
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isActive() && kind.accepts(stack);
    }

    @Override
    public boolean mayPickup(Player player) {
        return isActive();
    }
}
