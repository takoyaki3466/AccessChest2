package com.takoy3466.accesschest.neoforge.platform;

import com.takoy3466.accesschest.core.platform.IMenuPlatform;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.ExtendedMenuFactory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Uses NeoForge's menu type extension, which passes a buffer written by the server to the client factory.
 */
public final class NeoMenuPlatform implements IMenuPlatform {

    @Override
    public <T extends AbstractContainerMenu, D> void registerMenuType(CompatHolder<MenuType<T>> holder, ExtendedMenuFactory<T, D> factory, StreamCodec<ByteBuf, D> dataCodec) {
        DeferredHolder<MenuType<?>, MenuType<T>> deferred = NeoDeferredRegisters.MENU_TYPES.register(holder.getId(),
                () -> IMenuTypeExtension.create((containerId, inventory, buffer) -> factory.create(containerId, inventory, dataCodec.decode(buffer))));
        holder.set(deferred);
    }

    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<ByteBuf, D> dataCodec) {
        player.openMenu(provider, buffer -> dataCodec.encode(buffer, data));
    }
}
