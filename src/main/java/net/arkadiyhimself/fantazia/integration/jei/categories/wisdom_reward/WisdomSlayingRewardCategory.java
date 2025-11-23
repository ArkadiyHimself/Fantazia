package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardCategories;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WisdomSlayingRewardCategory(
        IDrawable background,
        IDrawable icon
) implements IWisdomRewardCategory {

    public static final RecipeType<RewardPair> DUMMY = RecipeType.create(Fantazia.MODID, "category.slaying", RewardPair.class);

    public static WisdomSlayingRewardCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.location("textures/gui/jei/wisdom_reward/category.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 92, 48);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.IRON_SWORD));
        return new WisdomSlayingRewardCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<RewardPair> getRecipeType() {
        return DUMMY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.wisdom_reward.title.slaying");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RewardPair recipe, @NotNull IFocusGroup focuses) {
        if (recipe.isDefault()) {
            Ingredient ingredient = Ingredient.of(Items.IRON_SWORD);
            builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addIngredients(ingredient);

            return;
        }
        ResourceLocation entityId = recipe.getA();
        EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(entityId);
        Item spawner;
        if (!BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)|| (spawner = getSpawnerItem(entityType)) == null) return;

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 6).addItemStack(new ItemStack(spawner));
    }

    @Override
    public ResourceLocation category() {
        return WisdomRewardCategories.SLAYED;
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }

    private static Item getSpawnerItem(EntityType<?> entityType) {
        if (entityType == FTZEntityTypes.BLOCK_FLY.value()) return FTZItems.BLOCK_FLY.asItem();
        else return SpawnEggItem.byId(entityType);
    }
}
