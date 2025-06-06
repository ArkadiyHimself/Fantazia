package net.arkadiyhimself.fantazia.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface IRecipeWithFee<T extends RecipeInput> extends Recipe<T> {

    int getFee();
    int getWisdom();
}
