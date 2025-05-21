package net.arkadiyhimself.fantazia.datagen.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShapedRecipeNoAdvancement implements RecipeBuilder {

    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final ItemStack resultStack; // Neo: add stack result support
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    @Nullable
    private String group;
    private boolean showNotification = true;

    public ShapedRecipeNoAdvancement(RecipeCategory category, ItemLike result, int count) {
        this(category, new ItemStack(result, count));
    }

    public ShapedRecipeNoAdvancement(RecipeCategory category, ItemStack result) {
        this.category = category;
        this.result = result.getItem();
        this.count = result.getCount();
        this.resultStack = result;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeNoAdvancement shaped(RecipeCategory category, ItemLike result) {
        return shaped(category, result, 1);
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static ShapedRecipeNoAdvancement shaped(RecipeCategory category, ItemLike result, int count) {
        return new ShapedRecipeNoAdvancement(category, result, count);
    }

    public static ShapedRecipeNoAdvancement shaped(RecipeCategory p_251325_, ItemStack result) {
        return new ShapedRecipeNoAdvancement(p_251325_, result);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedRecipeNoAdvancement define(Character symbol, TagKey<Item> tag) {
        return this.define(symbol, Ingredient.of(tag));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedRecipeNoAdvancement define(Character symbol, ItemLike item) {
        return this.define(symbol, Ingredient.of(item));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public ShapedRecipeNoAdvancement define(Character symbol, Ingredient ingredient) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredient);
            return this;
        }
    }

    /**
     * Adds a new entry to the patterns for this recipe.
     */
    public ShapedRecipeNoAdvancement pattern(String pattern) {
        if (!this.rows.isEmpty() && pattern.length() != this.rows.getFirst().length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pattern);
            return this;
        }
    }

    @Deprecated
    public @NotNull ShapedRecipeNoAdvancement unlockedBy(@NotNull String name, @NotNull Criterion<?> criterion) {
        throw new IllegalStateException("This builder does not support advancements");
    }

    public @NotNull ShapedRecipeNoAdvancement group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    public ShapedRecipeNoAdvancement showNotification(boolean showNotification) {
        this.showNotification = showNotification;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result;
    }

    @Override
    public void save(RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        ShapedRecipePattern shapedrecipepattern = ShapedRecipePattern.of(this.key, this.rows);;
        ShapedRecipe shapedrecipe = new ShapedRecipe(
                Objects.requireNonNullElse(this.group, ""),
                RecipeBuilder.determineBookCategory(this.category),
                shapedrecipepattern,
                this.resultStack,
                this.showNotification
        );
        recipeOutput.accept(id, shapedrecipe, null);
    }
}
