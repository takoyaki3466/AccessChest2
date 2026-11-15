package com.takoy3466.accesschest.fabric.platform;

import com.mojang.serialization.Codec;
import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.core.platform.IRegistryPlatform;
import com.takoy3466.accesschest.core.registry.CompatBlockEntitySupplier;
import com.takoy3466.accesschest.core.registry.CompatBlockHolder;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
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
 * Fabric registers directly into the built-in registries during mod initialization.
 */
public final class FabricRegistryPlatform implements IRegistryPlatform {

    @Override
    public <B extends Block, I extends BlockItem> void registerBlock(CompatBlockHolder<B, I> holder, Supplier<B> block, Function<B, I> item) {
        B registeredBlock = Registry.register(BuiltInRegistries.BLOCK, AccessChestCommon.id(holder.getId()), block.get());
        I registeredItem = Registry.register(BuiltInRegistries.ITEM, AccessChestCommon.id(holder.getId()), item.apply(registeredBlock));
        holder.blockHolder().set(() -> registeredBlock);
        holder.itemHolder().set(() -> registeredItem);
    }

    @Override
    public <T extends BlockEntity> void registerBlockEntityType(CompatHolder<BlockEntityType<T>> holder, CompatBlockEntitySupplier<T> factory, CompatHolder<? extends Block> block) {
        BlockEntityType<T> type = BlockEntityType.Builder.<T>of(factory::create, block.get()).build(null);
        BlockEntityType<T> registered = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, AccessChestCommon.id(holder.getId()), type);
        holder.set(() -> registered);
    }

    @Override
    public <T> void registerDataComponentType(CompatHolder<DataComponentType<T>> holder, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DataComponentType<T> type = DataComponentType.<T>builder().persistent(codec).networkSynchronized(streamCodec).build();
        DataComponentType<T> registered = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, AccessChestCommon.id(holder.getId()), type);
        holder.set(() -> registered);
    }

    @Override
    public <T extends Recipe<?>> void registerRecipeSerializer(CompatHolder<RecipeSerializer<T>> holder, Supplier<RecipeSerializer<T>> serializer) {
        RecipeSerializer<T> registered = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, AccessChestCommon.id(holder.getId()), serializer.get());
        holder.set(() -> registered);
    }

    @Override
    public void registerCreativeTab(CompatHolder<CreativeModeTab> holder, UnaryOperator<CreativeModeTab.Builder> configure) {
        CreativeModeTab tab = configure.apply(FabricItemGroup.builder()).build();
        CreativeModeTab registered = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, AccessChestCommon.id(holder.getId()), tab);
        holder.set(() -> registered);
    }
}
