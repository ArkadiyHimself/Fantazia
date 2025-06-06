package net.arkadiyhimself.fantazia.integration.jei.custom_ingredient;

import mezz.jei.api.ingredients.IIngredientType;
import net.arkadiyhimself.fantazia.integration.jei.Dummy;
import org.jetbrains.annotations.NotNull;

public final class WisdomIngredientType implements IIngredientType<Dummy> {

    public static final WisdomIngredientType INSTANCE = new WisdomIngredientType();

    @Override
    public @NotNull Class<Dummy> getIngredientClass() {
        return Dummy.class;
    }

    @Override
    public @NotNull String getUid() {
        return "wisdom_ingredient";
    }

}
