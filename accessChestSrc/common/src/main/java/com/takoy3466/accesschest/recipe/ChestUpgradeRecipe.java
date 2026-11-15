package com.takoy3466.accesschest.recipe;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestSpec;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.init.CompatSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * Eight original chests of one grade around a material block make one chest of the next grade (white).
 * The color of the input chests does not matter, as in the 1.7.10 version.
 */
public class ChestUpgradeRecipe extends CustomRecipe {
    private static final int GRID_SIZE = 3;
    private static final int CENTER_INDEX = GRID_SIZE * GRID_SIZE / 2;

    public static final MapCodec<ChestUpgradeRecipe> CODEC = RecordCodecBuilder.<ChestUpgradeRecipe>mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CustomRecipe::category),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("chest").forGetter(recipe -> recipe.chest),
            Ingredient.CODEC_NONEMPTY.fieldOf("material").forGetter(recipe -> recipe.material),
            ChestGrade.CODEC.fieldOf("from_grade").forGetter(recipe -> recipe.fromGrade)
    ).apply(instance, ChestUpgradeRecipe::new)).validate(recipe -> recipe.fromGrade.next().isPresent()
            ? DataResult.success(recipe)
            : DataResult.error(() -> "The highest chest grade can not be upgraded"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
            ByteBufCodecs.registry(Registries.ITEM), recipe -> recipe.chest,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.material,
            ChestGrade.STREAM_CODEC, recipe -> recipe.fromGrade,
            ChestUpgradeRecipe::new
    );

    private final Item chest;
    private final Ingredient material;
    private final ChestGrade fromGrade;

    public ChestUpgradeRecipe(CraftingBookCategory category, Item chest, Ingredient material, ChestGrade fromGrade) {
        super(category);
        this.chest = chest;
        this.material = material;
        this.fromGrade = fromGrade;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != GRID_SIZE || input.height() != GRID_SIZE) {
            return false;
        }
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            boolean valid = i == CENTER_INDEX ? material.test(stack) : isUpgradeableChest(stack);
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    private boolean isUpgradeableChest(ItemStack stack) {
        if (!stack.is(chest)) {
            return false;
        }
        ChestSpec spec = ChestStacks.specOf(stack);
        return spec.grade() == fromGrade && !spec.copy();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ChestGrade result = fromGrade.next().orElseThrow();
        return ChestStacks.create(chest, new ChestSpec(ChestSpec.DEFAULT.color(), result, false));
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= GRID_SIZE && height >= GRID_SIZE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompatSerializers.CHEST_UPGRADE.get();
    }
}
