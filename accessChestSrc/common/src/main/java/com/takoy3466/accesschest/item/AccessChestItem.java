package com.takoy3466.accesschest.item;

import com.takoy3466.accesschest.chest.ChestKind;
import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.menu.ChestMenuData;
import com.takoy3466.accesschest.menu.ChestMenus;
import com.takoy3466.accesschest.menu.HeldChestAccess;
import com.takoy3466.accesschest.player.PlayerMode;
import com.takoy3466.accesschest.player.PlayerModes;
import com.takoy3466.accesschest.storage.SharedChestSavedData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Access Chest item. Using it without sneaking opens the shared storage of its color from anywhere.
 * Sneaking places the block, unless collective transfer is enabled (then right clicks are reserved for transfers).
 */
public class AccessChestItem extends StorageChestItem {
    /** Same click sound settings as the 1.7.10 version. */
    private static final float OPEN_SOUND_VOLUME = 0.7F;
    private static final float OPEN_SOUND_PITCH = 1.35F;

    public AccessChestItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            return InteractionResultHolder.pass(stack);
        }
        if (player instanceof ServerPlayer serverPlayer) {
            openRemotely(serverPlayer, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && !player.isShiftKeyDown()) {
            return use(context.getLevel(), player, context.getHand()).getResult();
        }
        if (player != null && PlayerModes.isEnabled(player, PlayerMode.COLLECTIVE_TRANSFER)) {
            return InteractionResult.PASS;
        }
        return super.useOn(context);
    }

    private static void openRemotely(ServerPlayer player, ItemStack stack) {
        ChestSpec spec = ChestStacks.specOf(stack);
        HeldChestAccess access = new HeldChestAccess(SharedChestSavedData.boundedStorage(player.server, spec), spec);
        ChestMenus.open(player, stack.getHoverName(), new ChestMenuData(ChestKind.ACCESS, spec, true), access);
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, OPEN_SOUND_VOLUME, OPEN_SOUND_PITCH);
    }
}
