package com.takoy3466.accesschest.chest;

import com.takoy3466.accesschest.init.CompatData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Reads and writes the {@link ChestSpec} of chest item stacks.
 * <p>
 * The default spec is represented by the absence of the component so that crafted, dropped and creative stacks of the
 * same variant always stack together.
 */
public final class ChestStacks {

    private ChestStacks() {
    }

    public static ChestSpec specOf(ItemStack stack) {
        return stack.getOrDefault(CompatData.CHEST_SPEC.get(), ChestSpec.DEFAULT);
    }

    public static ItemStack create(ItemLike item, ChestSpec spec) {
        ItemStack stack = new ItemStack(item);
        applySpec(stack, spec);
        return stack;
    }

    public static void applySpec(ItemStack stack, ChestSpec spec) {
        if (ChestSpec.DEFAULT.equals(spec)) {
            stack.remove(CompatData.CHEST_SPEC.get());
        } else {
            stack.set(CompatData.CHEST_SPEC.get(), spec);
        }
    }
}
