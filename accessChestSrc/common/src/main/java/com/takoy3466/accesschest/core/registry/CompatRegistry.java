package com.takoy3466.accesschest.core.registry;

import com.mojang.serialization.Codec;
import com.takoy3466.accesschest.core.platform.Services;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
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
 * Static facade used by the {@code init} classes. It creates holders and forwards to the platform services.
 */
public final class CompatRegistry {

    private CompatRegistry() {
    }

    public static <B extends Block, I extends BlockItem> CompatBlockHolder<B, I> registerBlock(String id, Supplier<B> block, Function<B, I> item) {
        CompatBlockHolder<B, I> holder = new CompatBlockHolder<>(id);
        Services.REGISTRY.registerBlock(holder, block, item);
        return holder;
    }

    public static <T extends BlockEntity> CompatHolder<BlockEntityType<T>> registerBlockEntityType(String id, CompatBlockEntitySupplier<T> factory, CompatHolder<? extends Block> block) {
        CompatHolder<BlockEntityType<T>> holder = CompatHolder.create(id);
        Services.REGISTRY.registerBlockEntityType(holder, factory, block);
        return holder;
    }

    public static <T extends AbstractContainerMenu, D> CompatHolder<MenuType<T>> registerMenuType(String id, ExtendedMenuFactory<T, D> factory, StreamCodec<ByteBuf, D> dataCodec) {
        CompatHolder<MenuType<T>> holder = CompatHolder.create(id);
        Services.MENU.registerMenuType(holder, factory, dataCodec);
        return holder;
    }

    public static <T> CompatHolder<DataComponentType<T>> registerDataComponentType(String id, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        CompatHolder<DataComponentType<T>> holder = CompatHolder.create(id);
        Services.REGISTRY.registerDataComponentType(holder, codec, streamCodec);
        return holder;
    }

    public static <T extends Recipe<?>> CompatHolder<RecipeSerializer<T>> registerRecipeSerializer(String id, Supplier<RecipeSerializer<T>> serializer) {
        CompatHolder<RecipeSerializer<T>> holder = CompatHolder.create(id);
        Services.REGISTRY.registerRecipeSerializer(holder, serializer);
        return holder;
    }

    public static CompatHolder<CreativeModeTab> registerCreativeTab(String id, UnaryOperator<CreativeModeTab.Builder> configure) {
        CompatHolder<CreativeModeTab> holder = CompatHolder.create(id);
        Services.REGISTRY.registerCreativeTab(holder, configure);
        return holder;
    }
}
