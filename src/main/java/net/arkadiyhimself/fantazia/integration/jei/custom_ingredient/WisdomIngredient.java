package net.arkadiyhimself.fantazia.integration.jei.custom_ingredient;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public class WisdomIngredient implements ICustomIngredient {

    @Override
    public boolean test(@NotNull ItemStack itemStack) {
        return false;
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull IngredientType<?> getType() {
        return null;
    }


}
