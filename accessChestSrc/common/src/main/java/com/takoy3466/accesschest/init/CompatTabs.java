package com.takoy3466.accesschest.init;

import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.CompatRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ItemLike;

/**
 * Creative tab listing every chest variant.
 */
public final class CompatTabs {
    public static final String TITLE_KEY = "itemGroup.accesschest";

    public static final CompatHolder<CreativeModeTab> MAIN = CompatRegistry.registerCreativeTab("main", builder -> builder
            .title(Component.translatable(TITLE_KEY))
            .icon(() -> ChestStacks.create(CompatBlocks.ACCESS_CHEST.item(), ChestSpec.DEFAULT))
            .displayItems((parameters, output) -> {
                acceptVariants(output, CompatBlocks.ACCESS_CHEST.item(), true);
                acceptVariants(output, CompatBlocks.COMPRESSED_CHEST.item(), false);
            }));

    private CompatTabs() {
    }

    private static void acceptVariants(CreativeModeTab.Output output, ItemLike item, boolean withCopies) {
        for (DyeColor color : DyeColor.values()) {
            for (ChestGrade grade : ChestGrade.values()) {
                ChestSpec original = new ChestSpec(color, grade, false);
                output.accept(ChestStacks.create(item, original));
                if (withCopies) {
                    output.accept(ChestStacks.create(item, original.asCopy()));
                }
            }
        }
    }

    /** Forces class loading so that the static fields above are registered. */
    public static void init() {
    }
}
