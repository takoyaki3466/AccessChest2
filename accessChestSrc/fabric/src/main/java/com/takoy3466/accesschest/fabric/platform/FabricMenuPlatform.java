package com.takoy3466.accesschest.fabric.platform;

import com.takoy3466.accesschest.AccessChestCommon;
import com.takoy3466.accesschest.core.platform.IMenuPlatform;
import com.takoy3466.accesschest.core.registry.CompatHolder;
import com.takoy3466.accesschest.core.registry.ExtendedMenuFactory;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

/**
 * Uses Fabric's extended screen handler API to send the opening data.
 */
public final class FabricMenuPlatform implements IMenuPlatform {

    @Override
    public <T extends AbstractContainerMenu, D> void registerMenuType(CompatHolder<MenuType<T>> holder, ExtendedMenuFactory<T, D> factory, StreamCodec<ByteBuf, D> dataCodec) {
        MenuType<T> type = new ExtendedScreenHandlerType<>(factory::create, dataCodec);
        MenuType<T> registered = Registry.register(BuiltInRegistries.MENU, AccessChestCommon.id(holder.getId()), type);
        holder.set(() -> registered);
    }

    @Override
    public <D> void openMenu(ServerPlayer player, MenuProvider provider, D data, StreamCodec<ByteBuf, D> dataCodec) {
        // The codec is attached to the menu type on Fabric, so only the data itself is needed here.
        player.openMenu(new ExtendedScreenHandlerFactory<D>() {
            @Override
            public D getScreenOpeningData(ServerPlayer openingPlayer) {
                return data;
            }

            @Override
            public Component getDisplayName() {
                return provider.getDisplayName();
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player menuPlayer) {
                return provider.createMenu(containerId, inventory, menuPlayer);
            }
        });
    }
}
