package net.arkadiyhimself.fantazia.integration.jei;

import net.arkadiyhimself.fantazia.data.recipe.AmplificationRecipe;
import net.arkadiyhimself.fantazia.data.recipe.EnchantmentReplaceRecipe;
import net.arkadiyhimself.fantazia.data.recipe.RuneCarvingRecipe;
import net.arkadiyhimself.fantazia.common.registries.FTZRecipeTypes;
import net.minecraft.world.item.crafting.RecipeHolder;

public class JEIRecipeTypes {

    public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<RuneCarvingRecipe>> RUNE_CARVING =
            mezz.jei.api.recipe.RecipeType.createFromVanilla(FTZRecipeTypes.RUNE_CARVING.value());

    public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<AmplificationRecipe>> AMPLIFICATION =
            mezz.jei.api.recipe.RecipeType.createFromVanilla(FTZRecipeTypes.AMPLIFICATION.value());

    public static final mezz.jei.api.recipe.RecipeType<RecipeHolder<EnchantmentReplaceRecipe>> ENCHANTMENT_REPLACE =
            mezz.jei.api.recipe.RecipeType.createFromVanilla(FTZRecipeTypes.ENCHANTMENT_REPLACE.value());
}
