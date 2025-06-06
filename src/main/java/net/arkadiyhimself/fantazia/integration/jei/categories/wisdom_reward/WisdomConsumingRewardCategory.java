package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record WisdomConsumingRewardCategory(
        IDrawable background,
        IDrawable icon
) implements IWisdomRewardCategory {

    public static final RecipeType<RewardPair> DUMMY = RecipeType.create(Fantazia.MODID, "wisdom_reward.consuming", RewardPair.class);

    public static WisdomConsumingRewardCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.res("textures/gui/jei/wisdom_reward/category.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 92, 48);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.WHEAT));
        return new WisdomConsumingRewardCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<RewardPair> getRecipeType() {
        return DUMMY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.wisdom_reward.title.consuming");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RewardPair recipe, @NotNull IFocusGroup focuses) {
        if (recipe.isDefault()) {
            Ingredient ingredient = Ingredient.of(Items.WHEAT);
            builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addIngredients(ingredient);
            return;
        }

        ResourceLocation itemId = recipe.getA();
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (!BuiltInRegistries.ITEM.containsKey(itemId)) return;

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addItemStack(new ItemStack(item));
    }

    @Override
    public void draw(@NotNull RewardPair recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IWisdomRewardCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
