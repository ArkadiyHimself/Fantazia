package net.arkadiyhimself.fantazia.datagen.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import java.util.Objects;

public class SmithingTransformRecipeNoAdvancement {

    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final Item result;

    public SmithingTransformRecipeNoAdvancement(Ingredient template, Ingredient base, Ingredient addition, Item result) {
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.result = result;
    }

    public static SmithingTransformRecipeNoAdvancement smithing(Ingredient template, Ingredient base, Ingredient addition, Item result) {
        return new SmithingTransformRecipeNoAdvancement(template, base, addition, result);
    }

    public void save(RecipeOutput recipeOutput, ResourceLocation recipeId) {
        Advancement.Builder advancement$builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).requirements(AdvancementRequirements.Strategy.OR);
        Objects.requireNonNull(advancement$builder);
        SmithingTransformRecipe smithingtransformrecipe = new SmithingTransformRecipe(this.template, this.base, this.addition, new ItemStack(this.result));
        recipeOutput.accept(recipeId, smithingtransformrecipe, null);
    }
}
