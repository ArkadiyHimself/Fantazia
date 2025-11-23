package net.arkadiyhimself.fantazia.data.recipes;

import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.custom_registry.DeferredRune;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.recipe.RuneCarvingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class RuneCarvingRecipeBuilder implements RecipeBuilder {

    private final DeferredRune<Rune> rune;
    private final int fee;
    private final int wisdom;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();

    public RuneCarvingRecipeBuilder(DeferredRune<Rune> rune, int fee, int wisdom) {
        this.fee = fee;
        this.rune = rune;
        this.wisdom = wisdom;
    }
    public static RuneCarvingRecipeBuilder carving(DeferredRune<Rune> rune, int fee, int wisdom) {
        return new RuneCarvingRecipeBuilder(rune, fee, wisdom);
    }

    public RuneCarvingRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public RuneCarvingRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public RuneCarvingRecipeBuilder requires(ItemLike item, int quantity) {
        for(int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    public RuneCarvingRecipeBuilder requires(ItemStack... stacks) {
        return this.requires(Ingredient.of(stacks));
    }

    public RuneCarvingRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public RuneCarvingRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for(int i = 0; i < quantity; ++i) this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return FTZItems.RUNE_WIELDER.asItem();
    }

    @Override
    public @NotNull RuneCarvingRecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public @NotNull RuneCarvingRecipeBuilder unlockedBy(@NotNull String s, @NotNull Criterion<?> criterion) {
        throw new IllegalStateException("This builder does not support advancements");
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        RuneCarvingRecipe runeCarvingRecipe = new RuneCarvingRecipe(ingredients, rune, fee, wisdom);
        recipeOutput.accept(id, runeCarvingRecipe, null);
    }

    public void save(@NotNull RecipeOutput output) {
        save(output, rune.getKey().location().withPrefix("rune_carving/"));
    }
}
