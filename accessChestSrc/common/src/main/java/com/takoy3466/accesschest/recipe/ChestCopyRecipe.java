package com.takoy3466.accesschest.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.takoy3466.accesschest.chest.ChestGrade;
import com.takoy3466.accesschest.chest.ChestStacks;
import com.takoy3466.accesschest.init.CompatSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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

import java.util.OptionalInt;

/**
 * One chest of a grade plus a number of material items makes a copy of that chest. The source chest stays in the grid.
 */
public class ChestCopyRecipe extends CustomRecipe {
    private static final int MAX_MATERIAL_COUNT = 8;

    public static final MapCodec<ChestCopyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(CustomRecipe::category),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("chest").forGetter(recipe -> recipe.chest),
            ChestGrade.CODEC.fieldOf("grade").forGetter(recipe -> recipe.grade),
            Ingredient.CODEC_NONEMPTY.fieldOf("material").forGetter(recipe -> recipe.material),
            Codec.intRange(1, MAX_MATERIAL_COUNT).fieldOf("material_count").forGetter(recipe -> recipe.materialCount)
    ).apply(instance, ChestCopyRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChestCopyRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, CustomRecipe::category,
            ByteBufCodecs.registry(Registries.ITEM), recipe -> recipe.chest,
            ChestGrade.STREAM_CODEC, recipe -> recipe.grade,
            Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.material,
            ByteBufCodecs.VAR_INT, recipe -> recipe.materialCount,
            ChestCopyRecipe::new
    );

    private final Item chest;
    private final ChestGrade grade;
    private final Ingredient material;
    private final int materialCount;

    public ChestCopyRecipe(CraftingBookCategory category, Item chest, ChestGrade grade, Ingredient material, int materialCount) {
        super(category);
        this.chest = chest;
        this.grade = grade;
        this.material = material;
        this.materialCount = materialCount;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return findSourceChest(input).isPresent();
    }

    /**
     * @return the grid index of the single source chest, if the grid contains exactly one such chest,
     * exactly {@link #materialCount} material items and nothing else
     */
    private OptionalInt findSourceChest(CraftingInput input) {
        int chestIndex = -1;
        int materials = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.is(chest) && ChestStacks.specOf(stack).grade() == grade && chestIndex < 0) {
                chestIndex = i;
            } else if (material.test(stack)) {
                materials++;
            } else {
                return OptionalInt.empty();
            }
        }
        return chestIndex >= 0 && materials == materialCount ? OptionalInt.of(chestIndex) : OptionalInt.empty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        OptionalInt source = findSourceChest(input);
        if (source.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack sourceChest = input.getItem(source.getAsInt());
        return ChestStacks.create(chest, ChestStacks.specOf(sourceChest).asCopy());
    }

    /** Leaves the source chest in the crafting grid; only the materials are consumed. */
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = super.getRemainingItems(input);
        findSourceChest(input).ifPresent(index -> remaining.set(index, input.getItem(index).copyWithCount(1)));
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= materialCount + 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CompatSerializers.CHEST_COPY.get();
    }
}
