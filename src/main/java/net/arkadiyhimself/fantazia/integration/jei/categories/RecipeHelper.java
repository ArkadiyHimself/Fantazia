package net.arkadiyhimself.fantazia.integration.jei.categories;

import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class RecipeHelper {

    public static void setIngredients(List<IRecipeSlotBuilder> slotBuilders, List<Ingredient> ingredients, int width, int height) {
        if (slotBuilders.size() < width * height) {
            throw new IllegalArgumentException(String.format("There are not enough slots (%s) to hold a recipe of this size. (%sx%s)", slotBuilders.size(), width, height));
        }

        for (int i = 0; i < ingredients.size(); i++) {
            int index = getCraftingIndex(i, width, height);
            IRecipeSlotBuilder slot = slotBuilders.get(index);

            Ingredient ingredient = ingredients.get(i);
            if (ingredient != null) {
                slot.addIngredients(ingredient);
            }
        }
    }

    private static int getCraftingIndex(int i, int width, int height) {
        int index;
        if (width == 1) {
            if (height == 3) {
                index = (i * 3) + 1;
            } else if (height == 2) {
                index = (i * 3) + 1;
            } else {
                index = 4;
            }
        } else if (height == 1) {
            index = i + 3;
        } else if (width == 2) {
            index = i;
            if (i > 1) {
                index++;
                if (i > 3) {
                    index++;
                }
            }
        } else if (height == 2) {
            index = i + 3;
        } else {
            index = i;
        }
        return index;
    }
}
