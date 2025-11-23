package net.arkadiyhimself.fantazia.integration.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.recipe.EnchantmentReplaceRecipe;
import net.arkadiyhimself.fantazia.integration.jei.JEIRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record EnchantmentReplaceCategory(
        IDrawable background,
        IDrawable icon
) implements IAmplificationBenchCategory<EnchantmentReplaceRecipe> {

    public static EnchantmentReplaceCategory create(IGuiHelper helper) {
        ResourceLocation backgroundImage = Fantazia.location("textures/gui/jei/amplification_bench.png");
        IDrawable background = helper.createDrawable(backgroundImage, 0, 0, 118, 58);
        IDrawable icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.ENCHANTED_BOOK));
        return new EnchantmentReplaceCategory(background, icon);
    }

    @Override
    public @NotNull RecipeType<RecipeHolder<EnchantmentReplaceRecipe>> getRecipeType() {
        return JEIRecipeTypes.ENCHANTMENT_REPLACE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("fantazia.jei.title.enchantment_replace");
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RecipeHolder<EnchantmentReplaceRecipe> recipe, @NotNull IFocusGroup focuses) {
        Holder<Enchantment> previous = recipe.value().previous();
        Holder<Enchantment> next = recipe.value().next();
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        List<ItemStack> inputList = Lists.newArrayList();
        for (Holder<Item> itemHolder : previous.value().definition().supportedItems().stream().toList()) {
            ItemStack stack = new ItemStack(itemHolder);
            Optional<Integer> required = recipe.value().requiredLevel();
            stack.enchant(previous, required.orElseGet(() -> previous.value().getMaxLevel()));
            inputList.add(stack);
        }
        Ingredient input = Ingredient.of(inputList.stream());

        List<ItemStack> outputList = Lists.newArrayList();
        for (Holder<Item> itemHolder : next.value().definition().supportedItems().stream().toList()) {
            ItemStack stack = new ItemStack(itemHolder);
            stack.enchant(next, 1);
            outputList.add(stack);
        }
        Ingredient output = Ingredient.of(outputList.stream());

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 12).addIngredients(input);
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 30).addItemStack(new ItemStack(FTZItems.OBSCURE_SUBSTANCE.asItem(), recipe.value().fee()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 21).addIngredients(output);

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                IRecipeSlotBuilder slot = builder.addInputSlot(x * 18 + 30, y * 18 + 3);
                inputSlots.add(slot);
            }
        }
        RecipeHelper.setIngredients(inputSlots, recipe.value().getIngredients(), 3, 3);
    }

    @Override
    public @Nullable IDrawable getBackground() {
        return background;
    }
}
