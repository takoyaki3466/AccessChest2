package com.takoy3466.accesschest.item;

import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.chest.ChestStacks;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Block item of both chests. Shows the variant in the tooltip.
 */
public class StorageChestItem extends BlockItem {
    public static final String TOOLTIP_GRADE = "tooltip.accesschest.grade";
    public static final String TOOLTIP_COPY = "tooltip.accesschest.copy";
    private static final String COLOR_TRANSLATION_PREFIX = "color.minecraft.";

    public StorageChestItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ChestSpec spec = ChestStacks.specOf(stack);
        tooltip.add(Component.translatable(COLOR_TRANSLATION_PREFIX + spec.color().getName()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(TOOLTIP_GRADE, spec.grade().level()).withStyle(ChatFormatting.GRAY));
        if (spec.copy()) {
            tooltip.add(Component.translatable(TOOLTIP_COPY).withStyle(ChatFormatting.GRAY));
        }
    }
}
