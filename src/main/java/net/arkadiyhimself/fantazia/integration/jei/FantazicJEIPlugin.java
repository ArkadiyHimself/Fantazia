package net.arkadiyhimself.fantazia.integration.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.screen.AmplificationScreen;
import net.arkadiyhimself.fantazia.integration.jei.categories.AmplificationCategory;
import net.arkadiyhimself.fantazia.integration.jei.categories.EnchantmentReplaceCategory;
import net.arkadiyhimself.fantazia.integration.jei.categories.RuneCarvingCategory;
import net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward.*;
import net.arkadiyhimself.fantazia.common.item.WisdomCatcherItem;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class FantazicJEIPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(RuneCarvingCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(AmplificationCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(EnchantmentReplaceCategory.create(registry.getJeiHelpers().getGuiHelper()));

        registry.addRecipeCategories(WisdomConvertingCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(WisdomSlayingRewardCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(WisdomCraftingRewardCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(WisdomBrewingRewardCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(WisdomConsumingRewardCategory.create(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(WisdomTamedRewardCategory.create(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        FantazicRecipes modRecipes = new FantazicRecipes();
        registration.addRecipes(JEIRecipeTypes.RUNE_CARVING, modRecipes.getRuneCarvingRecipes());
        registration.addRecipes(JEIRecipeTypes.AMPLIFICATION, modRecipes.getAmplificationRecipes());
        registration.addRecipes(JEIRecipeTypes.ENCHANTMENT_REPLACE, modRecipes.getEnchantmentReplaceRecipes());
        registration.addRecipes(WisdomConvertingCategory.DUMMY, ImmutableList.of(new Dummy()));
        registration.addRecipes(WisdomSlayingRewardCategory.DUMMY, IWisdomRewardCategory.getSlayed());
        registration.addRecipes(WisdomCraftingRewardCategory.DUMMY, IWisdomRewardCategory.getCrafted());
        registration.addRecipes(WisdomBrewingRewardCategory.DUMMY, IWisdomRewardCategory.getBrewed());
        registration.addRecipes(WisdomConsumingRewardCategory.DUMMY, IWisdomRewardCategory.getConsumed());
        registration.addRecipes(WisdomTamedRewardCategory.DUMMY, IWisdomRewardCategory.getTamed());

        registration.addIngredientInfo(new ItemStack(FTZItems.OBSCURE_SUBSTANCE.asItem()), VanillaTypes.ITEM_STACK, Component.translatable("fantazia.jei.info.obscure_substance"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(FTZBlocks.AMPLIFICATION_BENCH), JEIRecipeTypes.AMPLIFICATION, JEIRecipeTypes.ENCHANTMENT_REPLACE, JEIRecipeTypes.RUNE_CARVING);
        registration.addRecipeCatalyst(FTZItems.WISDOM_CATCHER.toStack(), WisdomConvertingCategory.DUMMY, WisdomSlayingRewardCategory.DUMMY, WisdomCraftingRewardCategory.DUMMY, WisdomBrewingRewardCategory.DUMMY, WisdomConsumingRewardCategory.DUMMY, WisdomTamedRewardCategory.DUMMY);

    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(AmplificationScreen.class, 6, 8, 42, 12, JEIRecipeTypes.RUNE_CARVING, JEIRecipeTypes.AMPLIFICATION, JEIRecipeTypes.ENCHANTMENT_REPLACE);
        registration.addRecipeClickArea(AmplificationScreen.class, -110, 10, 100, 30, getWisdomPairs());

    }

    @Override
    public ResourceLocation getPluginUid() {
        return Fantazia.location("jei_plugin");
    }

    public RecipeType<?>[] getWisdomPairs() {
        return new RecipeType[]{
                WisdomBrewingRewardCategory.DUMMY,
                WisdomConsumingRewardCategory.DUMMY,
                WisdomConvertingCategory.DUMMY,
                WisdomCraftingRewardCategory.DUMMY,
                WisdomSlayingRewardCategory.DUMMY,
                WisdomTamedRewardCategory.DUMMY
        };
    }
}
