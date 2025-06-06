package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.client.screen.TalentScreen;
import net.arkadiyhimself.fantazia.integration.jei.Dummy;
import net.arkadiyhimself.fantazia.items.WisdomCatcherItem;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WisdomConvertingCategory(
        IDrawable background,
        IDrawable icon
) implements IRecipeCategory<Dummy> {

    private static final ResourceLocation EXPERIENCE_ORB = Fantazia.res("textures/gui/jei/wisdom_reward/experience_orb.png");

    public static final RecipeType<Dummy> DUMMY = RecipeType.create(Fantazia.MODID, "wisdom_reward.converting", Dummy.class);

    public static WisdomConvertingCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.res("textures/gui/jei/wisdom_reward/converting.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 92, 48);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, WisdomCatcherItem.itemStack());
        return new WisdomConvertingCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<Dummy> getRecipeType() {
        return DUMMY;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.wisdom_reward.title.converting");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull Dummy recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 38, 4).addItemStack(WisdomCatcherItem.itemStack());
    }

    @Override
    public void draw(@NotNull Dummy recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        int convert = TalentsHolder.XP_PER_WISDOM;

        int eX = 8;
        int eY = 24;
        guiGraphics.blit(EXPERIENCE_ORB, eX, eY, 0,0,10,10,10,10);
        guiGraphics.drawString(font, Component.literal(String.valueOf(convert)).withStyle(ChatFormatting.GREEN), eX + 11, eY + 1, 0);

        int wX = 62;
        int wY = 24;
        guiGraphics.blit(TalentScreen.WISDOM_ICON, wX, wY, 0,0,10,10,10,10);
        guiGraphics.drawString(font, Component.literal(String.valueOf(1)).withStyle(ChatFormatting.BLUE), wX + 11, wY + 1, 0);

        Component component = Component.translatable("fantazia.jei.wisdom_reward.title.converting.prompt").withStyle(ChatFormatting.BLUE);
        int width = font.width(component);
        int x0 = (96 - width) / 2;
        guiGraphics.drawString(font, component, x0, 38, 0, false);

        if (FantazicMath.within(eX, eX + 52 + 16, mouseX) && FantazicMath.within(24, 34, mouseY))
            guiGraphics.renderTooltip(font, Component.translatable("fantazia.jei.wisdom_reward.title.converting.rate").withStyle(ChatFormatting.BLUE), (int) mouseX, (int) mouseY);
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
