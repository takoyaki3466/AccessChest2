package com.takoy3466.accesschest.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.init.CompatSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * One chest plus one dye recolors the chest. Grade and copy flag are kept.
 */
public class ChestColoringRecipe extends CustomRecipe {
    private static final int INGREDIENT_COUNT = 2;

    public static final MapCodec<ChestColoringRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CustomRecipe::category),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("chest").forGetter(recipe -> recipe.chest)
    ).apply(instance, ChestColoringRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestColoringRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
            ByteBufCodecs.registry(Registries.ITEM), recipe -> recipe.chest,
            ChestColoringRecipe::new
    );

    private final Item chest;

    public ChestColoringRecipe(CraftingBookCategory category, Item chest) {
        super(category);
        this.chest = chest;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return input.ingredientCount() == INGREDIENT_COUNT && findChest(input) != null && findDye(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = findChest(input);
        DyeItem dye = findDye(input);
        if (source == null || dye == null) {
            return ItemStack.EMPTY;
        }
        return ChestStacks.create(chest, ChestStacks.specOf(source).withColor(dye.getDyeColor()));
    }

    private ItemStack findChest(CraftingInput input) {
        for (ItemStack stack : input.items()) {
            if (stack.is(chest)) {
                return stack;
            }
        }
        return null;
    }

    private DyeItem findDye(CraftingInput input) {
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof DyeItem dye) {
                return dye;
            }
        }
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= INGREDIENT_COUNT;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompatSerializers.CHEST_COLORING.get();
    }
}
