package net.arkadiyhimself.fantazia.datagen.recipe;

import net.minecraft.advancements.Criterion;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public class ShapelessRecipeNoAdvancement implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final ItemStack resultStack;
    private final NonNullList<Ingredient> ingredients;
    @Nullable
    private String group;

    public ShapelessRecipeNoAdvancement(RecipeCategory category, ItemLike result, int count) {
        this(category, new ItemStack(result, count));
    }

    public ShapelessRecipeNoAdvancement(RecipeCategory p_250837_, ItemStack result) {
        this.ingredients = NonNullList.create();
        this.category = p_250837_;
        this.result = result.getItem();
        this.count = result.getCount();
        this.resultStack = result;
    }

    public static ShapelessRecipeNoAdvancement shapeless(RecipeCategory category, ItemLike result) {
        return new ShapelessRecipeNoAdvancement(category, result, 1);
    }

    public static ShapelessRecipeNoAdvancement shapeless(RecipeCategory category, ItemLike result, int count) {
        return new ShapelessRecipeNoAdvancement(category, result, count);
    }

    public static ShapelessRecipeNoAdvancement shapeless(RecipeCategory category, ItemStack result) {
        return new ShapelessRecipeNoAdvancement(category, result);
    }

    public ShapelessRecipeNoAdvancement requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public ShapelessRecipeNoAdvancement requires(ItemLike item) {
        return this.requires((ItemLike)item, 1);
    }

    public ShapelessRecipeNoAdvancement requires(ItemLike item, int quantity) {
        for(int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    public ShapelessRecipeNoAdvancement requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public ShapelessRecipeNoAdvancement requires(Ingredient ingredient, int quantity) {
        for(int i = 0; i < quantity; ++i) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    @Deprecated
    public @NotNull ShapelessRecipeNoAdvancement unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        throw new IllegalStateException("This builder does not support advancements");
    }

    public @NotNull ShapelessRecipeNoAdvancement group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    public @NotNull Item getResult() {
        return this.result;
    }

    public void save(RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        ShapelessRecipe shapelessrecipe = new ShapelessRecipe(Objects.requireNonNullElse(this.group, ""), RecipeBuilder.determineBookCategory(this.category), this.resultStack, this.ingredients);
        recipeOutput.accept(id, shapelessrecipe, null);
    }
}
