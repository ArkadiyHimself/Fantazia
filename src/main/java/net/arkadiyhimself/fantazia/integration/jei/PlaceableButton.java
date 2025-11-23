package net.arkadiyhimself.fantazia.integration.jei;

import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record PlaceableButton(int width, int height) implements IRecipeSlotBuilder {

    @Override
    public @NotNull IRecipeSlotBuilder setPosition(int i, int i1) {
        return this;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public static PlaceableButton button(int width, int height) {
        return new PlaceableButton(width, height);
    }

    @Override
    public IRecipeSlotBuilder addTooltipCallback(@NotNull IRecipeSlotTooltipCallback iRecipeSlotTooltipCallback) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addRichTooltipCallback(@NotNull IRecipeSlotRichTooltipCallback iRecipeSlotRichTooltipCallback) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder setSlotName(String s) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder setStandardSlotBackground() {
        return null;
    }

    @Override
    public IRecipeSlotBuilder setOutputSlotBackground() {
        return null;
    }

    @Override
    public IRecipeSlotBuilder setBackground(IDrawable iDrawable, int i, int i1) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder setOverlay(IDrawable iDrawable, int i, int i1) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder setFluidRenderer(long l, boolean b, int i, int i1) {
        return null;
    }

    @Override
    public <T> IRecipeSlotBuilder setCustomRenderer(IIngredientType<T> iIngredientType, IIngredientRenderer<T> iIngredientRenderer) {
        return null;
    }

    @Override
    public <I> IRecipeSlotBuilder addIngredients(IIngredientType<I> iIngredientType, List<@Nullable I> list) {
        return null;
    }

    @Override
    public <I> IRecipeSlotBuilder addIngredient(IIngredientType<I> iIngredientType, I i) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addIngredientsUnsafe(List<?> list) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addTypedIngredients(List<ITypedIngredient<?>> list) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> list) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid, long l) {
        return null;
    }

    @Override
    public IRecipeSlotBuilder addFluidStack(Fluid fluid, long l, DataComponentPatch dataComponentPatch) {
        return null;
    }
}
