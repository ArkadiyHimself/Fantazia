package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WisdomCraftingRewardCategory(
        IDrawable background,
        IDrawable icon
) implements IWisdomRewardCategory {

    public static final RecipeType<RewardPair> DUMMY = RecipeType.create(Fantazia.MODID, "wisdom_reward.crafting", RewardPair.class);

    public static WisdomCraftingRewardCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.location("textures/gui/jei/wisdom_reward/category.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 92, 48);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.CRAFTING_TABLE));
        return new WisdomCraftingRewardCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<RewardPair> getRecipeType() {
        return DUMMY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.wisdom_reward.title.crafting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RewardPair recipe, @NotNull IFocusGroup focuses) {
        ClientLevel level = Minecraft.getInstance().level;
        if (recipe.isDefault() && level != null) {
            Ingredient ingredient = Ingredient.of(Items.CRAFTING_TABLE);
            builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addIngredients(ingredient);

            return;
        }

        ResourceLocation itemId = recipe.getA();
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (!BuiltInRegistries.ITEM.containsKey(itemId)) return;

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addItemStack(new ItemStack(item));
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
