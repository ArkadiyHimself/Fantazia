package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record WisdomBrewingRewardCategory(
        IDrawable background,
        IDrawable icon
) implements IWisdomRewardCategory {

    public static final RecipeType<RewardPair> DUMMY = RecipeType.create(Fantazia.MODID, "wisdom_reward.brewing", RewardPair.class);

    public static WisdomBrewingRewardCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.location("textures/gui/jei/wisdom_reward/category.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 92, 48);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.BREWING_STAND));
        return new WisdomBrewingRewardCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<RewardPair> getRecipeType() {
        return DUMMY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.wisdom_reward.title.brewing");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RewardPair recipe, @NotNull IFocusGroup focuses) {
        if (recipe.isDefault()) {
            Ingredient ingredient = Ingredient.of(Items.BREWING_STAND);
            builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addIngredients(ingredient);

            return;
        }

        ResourceLocation effectId = recipe.getA();
        if (!BuiltInRegistries.POTION.containsKey(effectId)) return;
        Optional<Holder.Reference<Potion>> potion = BuiltInRegistries.POTION.getHolder(effectId);
        if (potion.isEmpty()) return;
        ItemStack stack = PotionContents.createItemStack(Items.POTION, potion.get());

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addItemStack(stack);
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
