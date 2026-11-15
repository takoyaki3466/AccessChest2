package com.takoy3466.accesschest.neoforge.platform;

import com.takoy3466.accesschest.AccessChestCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * All deferred registers of the mod, shared by the NeoForge platform implementations.
 */
public final class NeoDeferredRegisters {
    static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AccessChestCommon.MOD_ID);
    static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AccessChestCommon.MOD_ID);
    static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AccessChestCommon.MOD_ID);
    static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AccessChestCommon.MOD_ID);
    static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, AccessChestCommon.MOD_ID);
    static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, AccessChestCommon.MOD_ID);
    static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AccessChestCommon.MOD_ID);

    private NeoDeferredRegisters() {
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
