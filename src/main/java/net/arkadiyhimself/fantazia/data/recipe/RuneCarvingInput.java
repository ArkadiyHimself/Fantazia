package net.arkadiyhimself.fantazia.data.recipe;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record RuneCarvingInput(ItemStack emptyRune, List<ItemStack> input, StackedContents contents, int count, int fee, int wisdom) implements RecipeInput {

    public static RuneCarvingInput input(ItemStack emptyRune, List<ItemStack> stacks, int fee, int wisdom) {
        StackedContents contents = new StackedContents();
        int i = 0;
        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                contents.accountStack(itemStack, 1);
                ++i;
            }
        }

        return new RuneCarvingInput(emptyRune, stacks, contents, i, fee, wisdom);
    }


    @Override
    public @NotNull ItemStack getItem(int index) {
        return input.get(index);
    }

    @Override
    public int size() {
        return input.size();
    }
}
