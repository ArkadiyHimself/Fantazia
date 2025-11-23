package net.arkadiyhimself.fantazia.integration.jei.categories.tool_recharge;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.arkadiyhimself.fantazia.integration.jei.categories.RecipeHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record ToolRechargeCategory(
        IDrawable background,
        IDrawable icon
) implements IRecipeCategory<ToolDataHolder> {

    public static final RecipeType<ToolDataHolder> DUMMY =
            RecipeType.create(Fantazia.MODID, "tool_recharge", ToolDataHolder.class);

    public static ToolRechargeCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.location("textures/gui/jei/tool_recharge.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 118, 58);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, FTZBlocks.OAK_ENGINEERING_TABLE.toStack());
        return new ToolRechargeCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<ToolDataHolder> getRecipeType() {
        return DUMMY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.title.tool_recharge");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ToolDataHolder toolDataHolder, @NotNull IFocusGroup iFocusGroup) {
        RechargeableToolData data = toolDataHolder.data();

        ItemStack tool = new ItemStack(toolDataHolder.item());
        builder.addSlot(RecipeIngredientRole.INPUT, 21, 21).addItemStack(tool);

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                IRecipeSlotBuilder slot = builder.addInputSlot(x * 18 + 57, y * 18 + 3);
                inputSlots.add(slot);
            }
        }

        List<Ingredient> ingredients = Lists.newArrayList();
        for (RechargeableToolData.SimpleIngredient simpleIngredient : data.ingredients()) {
            ingredients.add(Ingredient.of(new ItemStack(simpleIngredient.item(), simpleIngredient.amount())));
        }
        RecipeHelper.setIngredients(inputSlots, ingredients, 3, 3);
    }

    @Override
    public void draw(@NotNull ToolDataHolder recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {

    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
