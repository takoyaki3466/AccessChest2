package com.takoy3466.accesschest.forge.platform;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

/**
 * All deferred registers of the mod, shared by the Forge platform implementations.
 */
public final class ForgeDeferredRegisters {
    static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, AccessChestCommon.MOD_ID);
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, AccessChestCommon.MOD_ID);
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AccessChestCommon.MOD_ID);
    static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AccessChestCommon.MOD_ID);
    static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, AccessChestCommon.MOD_ID);
    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AccessChestCommon.MOD_ID);
    static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AccessChestCommon.MOD_ID);

    private ForgeDeferredRegisters() {
    }

    public static void register(IEventBus modBus) {
        DATA_COMPONENTS.register(modBus);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITY_TYPES.register(modBus);
        MENU_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        CREATIVE_TABS.register(modBus);
    }
}
