package com.takoy3466.accesschest.init;

import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.CompatRegistry;
import com.takoy3466.accesschest.recipe.ChestColoringRecipe;
import com.takoy3466.accesschest.recipe.ChestCopyRecipe;
import com.takoy3466.accesschest.recipe.ChestUpgradeRecipe;
import com.takoy3466.accesschest.recipe.CodecRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Recipe serializers of the custom crafting recipes.
 */
public final class CompatSerializers {

    public static final CompatHolder<RecipeSerializer<ChestUpgradeRecipe>> CHEST_UPGRADE = CompatRegistry.registerRecipeSerializer(
            "chest_upgrade", () -> new CodecRecipeSerializer<>(ChestUpgradeRecipe.CODEC, ChestUpgradeRecipe.STREAM_CODEC));

    public static final CompatHolder<RecipeSerializer<ChestCopyRecipe>> CHEST_COPY = CompatRegistry.registerRecipeSerializer(
            "chest_copy", () -> new CodecRecipeSerializer<>(ChestCopyRecipe.CODEC, ChestCopyRecipe.STREAM_CODEC));

    public static final CompatHolder<RecipeSerializer<ChestColoringRecipe>> CHEST_COLORING = CompatRegistry.registerRecipeSerializer(
            "chest_coloring", () -> new CodecRecipeSerializer<>(ChestColoringRecipe.CODEC, ChestColoringRecipe.STREAM_CODEC));

    private CompatSerializers() {
    }

    /** Forces class loading so that the static fields above are registered. */
    public static void init() {
    }
}
