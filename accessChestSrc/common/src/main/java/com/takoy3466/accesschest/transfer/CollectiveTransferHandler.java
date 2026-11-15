package com.takoy3466.accesschest.transfer;

import com.takoy3466.accesschest.block.entity.AccessChestBlockEntity;
import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.item.AccessChestItem;
import com.takoy3466.accesschest.player.PlayerMode;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.storage.BoundedStorage;
import com.takoy3466.accesschest.storage.SharedChestSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

/**
 * Collective transfer: with the mode enabled, right clicking a container with an Access Chest moves the container's
 * items into the shared storage, or, while sneaking, fills the container from the shared storage.
 * <p>
 * Loaders call {@link #onUseBlock} before the clicked block handles the click, on both sides.
 */
public final class CollectiveTransferHandler {

    private CollectiveTransferHandler() {
    }

    /**
     * @return {@link InteractionResult#PASS} if this is not a collective transfer, otherwise a consuming result
     */
    public static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand, BlockPos pos) {
        ItemStack held = player.getItemInHand(hand);
        if (player.isSpectator() || !(held.getItem() instanceof AccessChestItem)
                || !PlayerModes.isEnabled(player, PlayerMode.COLLECTIVE_TRANSFER)) {
            return InteractionResult.PASS;
        }
        Container target = HopperBlockEntity.getContainerAt(level, pos);
        ChestSpec spec = ChestStacks.specOf(held);
        if (target == null || isSameStorage(target, spec)) {
            return InteractionResult.PASS;
        }
        if (level instanceof ServerLevel serverLevel) {
            BoundedStorage storage = SharedChestSavedData.boundedStorage(serverLevel.getServer(), spec);
            if (player.isShiftKeyDown()) {
                ContainerTransfer.pour(storage, target);
            } else {
                ContainerTransfer.extract(target, storage);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** Transferring between an Access Chest and its own storage would do nothing, so the click is left to the block. */
    private static boolean isSameStorage(Container target, ChestSpec spec) {
        return target instanceof AccessChestBlockEntity chest && chest.color() == spec.color();
    }
}
