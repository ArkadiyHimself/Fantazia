package net.arkadiyhimself.fantazia.datagen;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FantazicRecipeProvider extends RecipeProvider {

    public static final ImmutableList<ItemLike> FANTAZIUM_SMELTABLES;

    public FantazicRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        oreSmelting(recipeOutput, FANTAZIUM_SMELTABLES, RecipeCategory.MISC, FTZItems.FANTAZIUM_INGOT,1.5f,200,"fantazium_ingot");
        oreBlasting(recipeOutput, FANTAZIUM_SMELTABLES, RecipeCategory.MISC, FTZItems.FANTAZIUM_INGOT,2F,100,"fantazium_ingot");
        nineBlockStorageRecipes(recipeOutput, RecipeCategory.MISC, FTZItems.RAW_FANTAZIUM, RecipeCategory.BUILDING_BLOCKS, FTZBlocks.RAW_FANTAZIUM_BLOCK);
        nineBlockStorageRecipesRecipesWithCustomUnpacking(
                recipeOutput,
                RecipeCategory.MISC,
                FTZItems.FANTAZIUM_INGOT,
                RecipeCategory.BUILDING_BLOCKS,
                FTZBlocks.FANTAZIUM_BLOCK,
                "fantazium_ingot_from_fantazium_block",
                "fantazium_ingot");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FTZItems.WISDOM_CATCHER)
                .pattern("#B#")
                .pattern("#B#")
                .pattern(" H ")
                .define('B', FTZItems.FANTAZIUM_INGOT)
                .define('#', Items.GOLD_INGOT)
                .define('H', Items.GLASS_BOTTLE)
                .unlockedBy("has_ingot", has(FTZItems.FANTAZIUM_INGOT)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FTZItems.FANTAZIC_PAINTING)
                .pattern("Y#Y")
                .pattern("#B#")
                .pattern("Y#Y")
                .define('B', Items.PAPER)
                .define('#', FTZBlocks.OBSCURE_PLANKS)
                .define('Y', Items.STICK)
                .unlockedBy("has_planks", has(FTZBlocks.OBSCURE_PLANKS)).save(recipeOutput);
    }

    protected static void oreSmelting(@NotNull RecipeOutput recipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory,
                                      @NotNull ItemLike pResult, float pExperience, int pCookingTIme, @NotNull String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients,
                pCategory, pResult, pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(@NotNull RecipeOutput recipeOutput, List<ItemLike> pIngredients, @NotNull RecipeCategory pCategory,
                                      @NotNull ItemLike pResult, float pExperience, int pCookingTime, @NotNull String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients,
                pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static <T extends AbstractCookingRecipe> void oreCooking(@NotNull RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer,
                                                                       AbstractCookingRecipe.@NotNull Factory<T> factory, List<ItemLike> pIngredients,
                                                                       @NotNull RecipeCategory pCategory, @NotNull ItemLike pResult, float pExperience,
                                                                       int pCookingTime, @NotNull String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, Fantazia.MODID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    protected static void nineBlockStorageRecipes(@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory unpackedCategory, ItemLike unpacked, @NotNull RecipeCategory packedCategory, ItemLike packed) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getSimpleName(packed), null, getSimpleName(unpacked), null);
    }

    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(@NotNull RecipeOutput recipeOutput, @NotNull RecipeCategory unpackedCategory, ItemLike unpacked, @NotNull RecipeCategory packedCategory, ItemLike packed, String unpackedName, @NotNull String unpackedGroup) {
        nineBlockStorageRecipes(recipeOutput, unpackedCategory, unpacked, packedCategory, packed, getSimpleName(packed), getSimpleName(packed) ,Fantazia.MODID + ":" + unpackedName,Fantazia.MODID + ":" + unpackedGroup);
    }

    private static String getSimpleName(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem()).toString();
    }

    static {
        FANTAZIUM_SMELTABLES = ImmutableList.of(FTZBlocks.FANTAZIUM_ORE, FTZBlocks.DEEPSLATE_FANTAZIUM_ORE, FTZItems.RAW_FANTAZIUM);
    }
}
