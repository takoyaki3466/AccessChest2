package com.takoy3466.accesschest.core.platform;

import com.mojang.serialization.Codec;
import com.takoy3466.accesschest.core.registry.CompatBlockEntitySupplier;
import com.takoy3466.accesschest.core.registry.CompatBlockHolder;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Registers game objects with the loader's registry system.
 * Implementations must fill the given holder so that {@link CompatHolder#get()} works after registration.
 */
public interface IRegistryPlatform {

    <B extends Block, I extends BlockItem> void registerBlock(CompatBlockHolder<B, I> holder, Supplier<B> block, Function<B, I> item);

    <T extends BlockEntity> void registerBlockEntityType(CompatHolder<BlockEntityType<T>> holder, CompatBlockEntitySupplier<T> factory, CompatHolder<? extends Block> block);

    <T> void registerDataComponentType(CompatHolder<DataComponentType<T>> holder, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);

    <T extends Recipe<?>> void registerRecipeSerializer(CompatHolder<RecipeSerializer<T>> holder, Supplier<RecipeSerializer<T>> serializer);

    /**
     * @param configure receives the loader specific builder and must return it after setting title, icon and items
     */
    void registerCreativeTab(CompatHolder<CreativeModeTab> holder, UnaryOperator<CreativeModeTab.Builder> configure);
}
