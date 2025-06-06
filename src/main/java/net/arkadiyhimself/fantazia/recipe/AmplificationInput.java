package net.arkadiyhimself.fantazia.recipe;

import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AmplificationInput(ItemStack initial, List<ItemStack> input, StackedContents contents, int count, int fee,  int wisdom) implements RecipeInput {

    public static AmplificationInput input(ItemStack initial, List<ItemStack> stacks, int fee, int wisdom) {
        StackedContents contents = new StackedContents();
        int i = 0;
        for (ItemStack itemStack : stacks) {
            if (!itemStack.isEmpty()) {
                i++;
                contents.accountStack(itemStack, 1);
            }
        }

        return new AmplificationInput(initial, stacks, contents, i, fee, wisdom);
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
