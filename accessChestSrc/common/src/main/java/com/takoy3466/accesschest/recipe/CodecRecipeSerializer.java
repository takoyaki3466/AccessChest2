package com.takoy3466.accesschest.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Recipe serializer defined only by its codecs. Shared by all recipes of this mod.
 */
public record CodecRecipeSerializer<T extends Recipe<?>>(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec)
        implements RecipeSerializer<T> {
}
