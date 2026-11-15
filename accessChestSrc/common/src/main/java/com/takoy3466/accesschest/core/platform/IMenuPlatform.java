package com.takoy3466.accesschest.core.platform;

import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.ExtendedMenuFactory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

/**
 * Menus whose client side needs extra opening data. Every loader has its own API for this.
 */
public interface IMenuPlatform {

    <T extends AbstractContainerMenu, D> void registerMenuType(CompatHolder<MenuType<T>> holder, ExtendedMenuFactory<T, D> factory, StreamCodec<ByteBuf, D> dataCodec);

    /**
     * Opens {@code provider} for {@code player} and sends {@code data} to the client menu factory.
     */
    <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<ByteBuf, D> dataCodec);
}
