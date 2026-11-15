package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.storage.BoundedStorage;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Server side container behind the window slots. Each window slot is redirected to the storage slot the view maps it to.
 */
final class WindowContainer implements Container {
    private final ChestView view;
    private final BoundedStorage storage;
    private final ChestAccess access;

    WindowContainer(ChestView view, BoundedStorage storage, ChestAccess access) {
        this.view = view;
        this.storage = storage;
        this.access = access;
    }

    @Override
    public int getContainerSize() {
        return ChestMenuLayout.WINDOW_SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        int index = view.storageIndex(slot);
        return index == ChestView.NO_SLOT ? ItemStack.EMPTY : storage.get(index);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        int index = view.storageIndex(slot);
        return index == ChestView.NO_SLOT ? ItemStack.EMPTY : storage.remove(index, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        int index = view.storageIndex(slot);
        return index == ChestView.NO_SLOT ? ItemStack.EMPTY : storage.take(index);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        int index = view.storageIndex(slot);
        if (index != ChestView.NO_SLOT) {
            storage.set(index, stack);
        } else if (!stack.isEmpty()) {
            // WindowSlot#mayPlace rejects unmapped slots, so reaching this point is a bug. Fail loudly instead of deleting the stack.
            throw new IllegalStateException("Tried to put " + stack + " into unmapped window slot " + slot);
        }
    }

    @Override
    public void setChanged() {
        storage.storage().markChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return access.stillValid(player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return view.storageIndex(slot) != ChestView.NO_SLOT && storage.canStore(stack);
    }

    @Override
    public void startOpen(Player player) {
        access.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        access.stopOpen(player);
    }

    /** Clears the storage slots currently shown in the window. */
    @Override
    public void clearContent() {
        for (int i = 0; i < getContainerSize(); i++) {
            int index = view.storageIndex(i);
            if (index != ChestView.NO_SLOT) {
                storage.set(index, ItemStack.EMPTY);
            }
        }
    }
}
