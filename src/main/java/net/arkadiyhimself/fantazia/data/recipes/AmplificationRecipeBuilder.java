package net.arkadiyhimself.fantazia.data.recipes;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.recipe.AmplificationRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AmplificationRecipeBuilder implements RecipeBuilder {

    private final Holder<Enchantment> enchantment;
    private final int fee;
    private final int wisdom;
    private final int limit;
    private final NonNullList<Ingredient> ingredients = NonNullList.create();

    public AmplificationRecipeBuilder(Holder<Enchantment> enchantment, int fee, int wisdom, int limit) {
        this.fee = fee;
        this.enchantment = enchantment;
        this.wisdom = wisdom;
        this.limit = limit;
    }
    public static AmplificationRecipeBuilder amplification(Holder<Enchantment> enchantmentHolder, int fee, int wisdom, int limit) {
        return new AmplificationRecipeBuilder(enchantmentHolder, fee, wisdom, limit);
    }

    public static AmplificationRecipeBuilder amplification(Holder<Enchantment> enchantment, int fee, int wisdom) {
        return amplification(enchantment, fee, wisdom, 0);
    }

    public AmplificationRecipeBuilder requires(TagKey<Item> tag) {
        return this.requires(Ingredient.of(tag));
    }

    public AmplificationRecipeBuilder requires(ItemLike item) {
        return this.requires(item, 1);
    }

    public AmplificationRecipeBuilder requires(ItemLike item, int quantity) {
        for(int i = 0; i < quantity; ++i) {
            this.requires(Ingredient.of(item));
        }
        return this;
    }

    public AmplificationRecipeBuilder requires(ItemStack... stacks) {
        return this.requires(Ingredient.of(stacks));
    }

    public AmplificationRecipeBuilder requires(Ingredient ingredient) {
        return this.requires(ingredient, 1);
    }

    public AmplificationRecipeBuilder requires(Ingredient ingredient, int quantity) {
        for(int i = 0; i < quantity; ++i) this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return FTZItems.RUNE_WIELDER.asItem();
    }

    @Override
    public @NotNull AmplificationRecipeBuilder group(@Nullable String s) {
        return this;
    }

    @Override
    public @NotNull AmplificationRecipeBuilder unlockedBy(@NotNull String s, @NotNull Criterion<?> criterion) {
        throw new IllegalStateException("This builder does not support advancements");
    }

    @Override
    public void save(@NotNull RecipeOutput recipeOutput, @NotNull ResourceLocation id) {
        AmplificationRecipe amplificationRecipe = new AmplificationRecipe(ingredients, enchantment, limit == 0 ? Optional.empty() : Optional.of(limit), fee, wisdom);
        recipeOutput.accept(id, amplificationRecipe, null);
    }

    public void save(@NotNull RecipeOutput output, boolean fantazia) {
        ResourceLocation id = enchantment.getKey().location().withPrefix("amplification/");
        if (fantazia) id = Fantazia.changeNamespace(id);
        save(output, id);
    }

    public void save(@NotNull RecipeOutput output) {
        save(output, enchantment.getKey().location().withPrefix("amplification/"));
    }
}
