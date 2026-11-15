package com.takoy3466.accesschest.neoforge.platform;

import com.mojang.serialization.Codec;
import com.takoy3466.accesschest.core.platform.IRegistryPlatform;
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
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Registers through NeoForge deferred registers; objects become available during the registry events.
 */
public final class NeoRegistryPlatform implements IRegistryPlatform {

    @Override
    public <B extends Block, I extends BlockItem> void registerBlock(CompatBlockHolder<B, I> holder, Supplier<B> block, Function<B, I> item) {
        DeferredBlock<B> deferredBlock = NeoDeferredRegisters.BLOCKS.register(holder.getId(), block);
        DeferredItem<I> deferredItem = NeoDeferredRegisters.ITEMS.register(holder.getId(), () -> item.apply(deferredBlock.get()));
        holder.blockHolder().set(deferredBlock);
        holder.itemHolder().set(deferredItem);
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityType(CompatHolder<BlockEntityType<T>> holder, CompatBlockEntitySupplier<T> factory, CompatHolder<? extends Block> block) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> deferred = NeoDeferredRegisters.BLOCK_ENTITY_TYPES.register(holder.getId(),
                () -> BlockEntityType.Builder.<T>of(factory::create, block.get()).build(null));
        holder.set(deferred);
    }

    @Override
    public <T> void registerDataComponentType(CompatHolder<DataComponentType<T>> holder, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DeferredHolder<DataComponentType<?>, DataComponentType<T>> deferred = NeoDeferredRegisters.DATA_COMPONENTS.register(holder.getId(),
                () -> DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build());
        holder.set(deferred);
    }

    @Override
    public <T extends Recipe<?>> void registerRecipeSerializer(CompatHolder<RecipeSerializer<T>> holder, Supplier<RecipeSerializer<T>> serializer) {
        DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> deferred = NeoDeferredRegisters.RECIPE_SERIALIZERS.register(holder.getId(), serializer);
        holder.set(deferred);
    }

    @Override
    public void registerCreativeTab(CompatHolder<CreativeModeTab> holder, UnaryOperator<CreativeModeTab.Builder> configure) {
        DeferredHolder<CreativeModeTab, CreativeModeTab> deferred = NeoDeferredRegisters.CREATIVE_TABS.register(holder.getId(),
                () -> configure.apply(CreativeModeTab.builder()).build());
        holder.set(deferred);
    }
}
