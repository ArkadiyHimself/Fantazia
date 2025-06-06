package net.arkadiyhimself.fantazia.data.recipes;

import net.arkadiyhimself.fantazia.recipe.EnchantmentReplaceRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchantmentReplaceRecipeBuilder implements RecipeBuilder {

    private final Holder<Enchantment> previous;
    private final Holder<Enchantment> next;
    private final int fee;
    private final int wisdom;
    private final int requiredLevel;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();

    public EnchantmentReplaceRecipeBuilder(Holder<Enchantment> previous, Holder<Enchantment> next, int fee, int wisdom, int requiredLevel) {
        this.fee = fee;
        this.previous = previous;
        this.wisdom = wisdom;
        this.requiredLevel = requiredLevel;
        this.next = next;
    }
    public static EnchantmentReplaceRecipeBuilder enchantmentReplace(Holder<Enchantment> previous, Holder<Enchantment> next, int fee, int wisdom, int requiredLevel) {
        return new EnchantmentReplaceRecipeBuilder(previous, next, fee, wisdom, requiredLevel);
    }

    public static EnchantmentReplaceRecipeBuilder enchantmentReplace(Holder<Enchantment> previous, Holder<Enchantment> next, int fee, int wisdom) {
        return enchantmentReplace(previous, next, fee, wisdom, 0);
    }

    public EnchantmentReplaceRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public EnchantmentReplaceRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public EnchantmentReplaceRecipeBuilder requires(ItemLike item, int quantity) {
        for(int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    public EnchantmentReplaceRecipeBuilder requires(ItemStack... stacks) {
        return this.requires(Ingredient.of(stacks));
    }

    public EnchantmentReplaceRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public EnchantmentReplaceRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for(int i = 0; i < quantity; ++i) this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return Items.AIR;
    }

    @Override
    public @NotNull EnchantmentReplaceRecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public @NotNull EnchantmentReplaceRecipeBuilder unlockedBy(@NotNull String s, @NotNull Criterion<?> criterion) {
        throw new IllegalStateException("This builder does not support advancements");
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        EnchantmentReplaceRecipe enchantmentReplaceRecipe = new EnchantmentReplaceRecipe(ingredients, previous, next, requiredLevel == 0 ? Optional.empty() : Optional.of(requiredLevel), fee, wisdom);
        recipeOutput.accept(id, enchantmentReplaceRecipe, null);
    }

    public void save(@NotNull RecipeOutput output) {
        save(output, next.getKey().location().withPrefix("enchantment_replace/"));
    }
}
