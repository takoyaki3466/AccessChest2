package com.takoy3466.accesschest.transfer;

import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.item.AccessChestItem;
import com.takoy3466.accesschest.player.PlayerMode;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.storage.SharedChestSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Auto collect: after the vanilla pickup logic ran, whatever did not fit into the inventory is put into the Access
 * Chests the player carries.
 */
public final class AutoCollectHandler {

    private AutoCollectHandler() {
    }

    /**
     * Called by the item entity mixin at the end of {@code ItemEntity#playerTouch}.
     *
     * @param pickupTarget the only player allowed to pick up the item, or {@code null} for everyone
     */
    public static void afterPlayerTouch(ItemEntity itemEntity, Player player, @Nullable UUID pickupTarget) {
        if (!(player instanceof ServerPlayer serverPlayer) || itemEntity.isRemoved() || itemEntity.hasPickUpDelay()) {
            return;
        }
        if (pickupTarget != null && !pickupTarget.equals(player.getUUID())) {
            return;
        }
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty() || !PlayerModes.isEnabled(player, PlayerMode.AUTO_COLLECT)) {
            return;
        }

        ItemStack remaining = stack;
        for (ItemStack carried : player.getInventory().items) {
            if (remaining.isEmpty()) {
                break;
            }
            if (carried.getItem() instanceof AccessChestItem) {
                remaining = SharedChestSavedData.boundedStorage(serverPlayer.server, ChestStacks.specOf(carried)).insert(remaining);
            }
        }

        int stored = stack.getCount() - remaining.getCount();
        if (stored <= 0) {
            return;
        }
        player.take(itemEntity, stored);
        if (remaining.isEmpty()) {
            itemEntity.discard();
        } else {
            itemEntity.setItem(remaining);
        }
    }
}
