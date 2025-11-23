package net.arkadiyhimself.fantazia.integration.jei.categories;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.arkadiyhimself.fantazia.client.screen.TalentScreen;
import net.arkadiyhimself.fantazia.data.recipe.IRecipeWithFee;
import net.arkadiyhimself.fantazia.integration.jei.FantazicJEIPlugin;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public interface IAmplificationBenchCategory<T extends IRecipeWithFee<?>> extends IRecipeCategory<RecipeHolder<T>> {

    @Override
    default void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull RecipeHolder<T> recipe, @NotNull IFocusGroup focuses) {
        IRecipeCategory.super.createRecipeExtras(builder, recipe, focuses);
        Font font = Minecraft.getInstance().font;
        String str = String.valueOf(recipe.value().getWisdom());
        Component component = Component.literal(str).withStyle(ChatFormatting.BLUE);
        int wdt = font.width(component);
        int windowWidth = 12 + wdt;
        builder.addInputHandler(new IJeiInputHandler() {

            @Override
            public @NotNull ScreenRectangle getArea() {
                return new ScreenRectangle(90,5, windowWidth, 10);
            }

            @Override
            public boolean handleInput(double mouseX, double mouseY, @NotNull IJeiUserInput input) {
                if (FantazicJEIPlugin.RUNTIME != null && input.getKey().getValue() == 0 && input.getModifiers() == 0) {
                    FantazicJEIPlugin.RUNTIME.getRecipesGui().showTypes(Arrays.asList(FantazicJEIPlugin.getWisdomPairs()));
                    return true;
                } else return false;
            }
        });
    }

    @Override
    default void draw(@NotNull RecipeHolder<T> recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        int x0 = 90;
        int y0 = 5;
        guiGraphics.blit(TalentScreen.WISDOM_ICON, x0, y0, 0,0,10,10,10,10);
        Font font = Minecraft.getInstance().font;
        String str = String.valueOf(recipe.value().getWisdom());
        Component component = Component.literal(str).withStyle(ChatFormatting.BLUE);
        guiGraphics.drawString(font, component, 101, 7, 0);
        int wdt = font.width(component);
        int windowWidth = 11 + wdt;
        if (FantazicMath.isWithin(x0, x0 + windowWidth, mouseX) && FantazicMath.isWithin(y0, y0 + 10, mouseY)) {
            List<Component> components = Lists.newArrayList();
            components.add(Component.translatable("fantazia.jei.required_wisdom").withStyle(ChatFormatting.BLUE));
            components.add(Component.translatable("fantazia.jei.required_wisdom.click_to_see").withStyle(ChatFormatting.BLUE));
            guiGraphics.renderComponentTooltip(font, components, (int) mouseX, (int) mouseY);
        }
    }
}