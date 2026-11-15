package com.takoy3466.accesschest.menu;

import com.takoy3466.accesschest.init.CompatMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Menu of both chest kinds: a fixed window of {@link ChestMenuLayout#WINDOW_SLOT_COUNT} slots over a large storage,
 * followed by the player inventory.
 * <p>
 * On the server the window is backed by a {@link ServerChestSession}. On the client it is a plain container that
 * receives the window contents through vanilla slot synchronisation; the client never sees the whole storage.
 */
public class StorageChestMenu extends AbstractContainerMenu {
    private static final int PLAYER_SLOT_START = ChestMenuLayout.WINDOW_SLOT_COUNT;
    private static final int PLAYER_SLOT_END = PLAYER_SLOT_START + Inventory.INVENTORY_SIZE;

    private final ChestMenuData data;
    private final Container window;
    private final ViewState viewState;
    /** Present on the logical server only. */
    @Nullable
    private final ServerChestSession session;

    private StorageChestMenu(int containerId, Inventory playerInventory, ChestMenuData data, Container window, ViewState viewState, @Nullable ServerChestSession session) {
        super(CompatMenus.STORAGE_CHEST.get(), containerId);
        this.data = data;
        this.window = window;
        this.viewState = viewState;
        this.session = session;

        window.startOpen(playerInventory.player);
        addWindowSlots();
        addPlayerSlots(playerInventory);
        addDataSlot(viewState.matchCountSlot());
        addDataSlot(viewState.scrollRowSlot());
    }

    /** Client side factory, used by the registered menu type. */
    public static StorageChestMenu forClient(int containerId, Inventory playerInventory, ChestMenuData data) {
        return new StorageChestMenu(containerId, playerInventory, data, new SimpleContainer(ChestMenuLayout.WINDOW_SLOT_COUNT), new ViewState(), null);
    }

    /** Server side factory. */
    public static StorageChestMenu forServer(int containerId, Inventory playerInventory, ChestMenuData data, ChestAccess access) {
        ServerChestSession session = new ServerChestSession(access);
        return new StorageChestMenu(containerId, playerInventory, data, session.window(), session.viewState(), session);
    }

    private void addWindowSlots() {
        for (int row = 0; row < ChestMenuLayout.VISIBLE_ROWS; row++) {
            for (int column = 0; column < ChestMenuLayout.COLUMNS; column++) {
                int index = row * ChestMenuLayout.COLUMNS + column;
                int x = ChestMenuLayout.CHEST_X + column * ChestMenuLayout.SLOT_SIZE;
                int y = ChestMenuLayout.CHEST_Y + row * ChestMenuLayout.SLOT_SIZE;
                addSlot(new WindowSlot(window, index, x, y, viewState, data.kind()));
            }
        }
    }

    private void addPlayerSlots(Inventory playerInventory) {
        int hotbarSize = Inventory.getSelectionSize();
        for (int row = 0; row < ChestMenuLayout.PLAYER_INVENTORY_ROWS; row++) {
            for (int column = 0; column < ChestMenuLayout.PLAYER_INVENTORY_COLUMNS; column++) {
                int index = hotbarSize + row * ChestMenuLayout.PLAYER_INVENTORY_COLUMNS + column;
                int x = ChestMenuLayout.PLAYER_INVENTORY_X + column * ChestMenuLayout.SLOT_SIZE;
                int y = ChestMenuLayout.PLAYER_INVENTORY_Y + row * ChestMenuLayout.SLOT_SIZE;
                addSlot(new Slot(playerInventory, index, x, y));
            }
        }
        for (int column = 0; column < hotbarSize; column++) {
            int x = ChestMenuLayout.PLAYER_INVENTORY_X + column * ChestMenuLayout.SLOT_SIZE;
            addSlot(new Slot(playerInventory, column, x, ChestMenuLayout.HOTBAR_Y));
        }
    }

    public ChestMenuData data() {
        return data;
    }

    public ViewState viewState() {
        return viewState;
    }

    public Optional<ServerChestSession> session() {
        return Optional.ofNullable(session);
    }

    /** Used by block entities to count the players looking into them. */
    public boolean isAccessedThrough(ChestAccess access) {
        return session != null && session.access() == access;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < PLAYER_SLOT_START) {
            if (!moveItemStackTo(stack, PLAYER_SLOT_START, PLAYER_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            return original;
        }

        // Moving into the storage needs the whole storage, which only the server has. The client waits for the sync.
        if (session == null) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = session.insert(stack);
        if (remainder.getCount() == stack.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.setByPlayer(remainder);
        return original;
    }

    @Override
    public boolean stillValid(Player player) {
        return window.stillValid(player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        window.stopOpen(player);
    }
}
