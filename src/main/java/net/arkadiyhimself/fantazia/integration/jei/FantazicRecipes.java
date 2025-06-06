package net.arkadiyhimself.fantazia.integration.jei;

import net.arkadiyhimself.fantazia.recipe.AmplificationRecipe;
import net.arkadiyhimself.fantazia.recipe.EnchantmentReplaceRecipe;
import net.arkadiyhimself.fantazia.recipe.RuneCarvingRecipe;
import net.arkadiyhimself.fantazia.registries.FTZRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class FantazicRecipes {

    private final RecipeManager recipeManager;

    public FantazicRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;

        if (level != null) this.recipeManager = level.getRecipeManager();
        else throw new NullPointerException("minecraft world must not be null.");
    }

    public List<RecipeHolder<RuneCarvingRecipe>> getRuneCarvingRecipes() {
        return recipeManager.getAllRecipesFor(FTZRecipeTypes.RUNE_CARVING.get());
    }

    public List<RecipeHolder<AmplificationRecipe>> getAmplificationRecipes() {
        return recipeManager.getAllRecipesFor(FTZRecipeTypes.AMPLIFICATION.get());
    }

    public List<RecipeHolder<EnchantmentReplaceRecipe>> getEnchantmentReplaceRecipes() {
        return recipeManager.getAllRecipesFor(FTZRecipeTypes.ENCHANTMENT_REPLACE.get());
    }
}
