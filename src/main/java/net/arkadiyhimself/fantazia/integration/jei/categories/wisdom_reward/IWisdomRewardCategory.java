package net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward;

import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.screen.TalentScreen;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerWisdomRewardManager;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardCategories;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardInstance;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardsCombined;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface IWisdomRewardCategory extends IRecipeCategory<RewardPair> {

    static List<RewardPair> getBrewed() {
        List<RewardPair> list = Lists.newArrayList();
        int defaultReward = 0;
        for (WisdomRewardsCombined.Builder builder : ServerWisdomRewardManager.getBuilders()) if (builder.category().equals(WisdomRewardCategories.BREWED)) {
            for (Map.Entry<ResourceLocation, WisdomRewardInstance.Builder> entry : builder.builders().entrySet()) {
                list.add(new RewardPair(entry.getKey(), entry.getValue()));
            }
            int reward = builder.defaultReward();
            if (reward > defaultReward) defaultReward = reward;
        }
        list.add(new RewardPair(Fantazia.location("default"), new WisdomRewardInstance.Builder(defaultReward), true));
        return list;
    }

    static List<RewardPair> getConsumed() {
        List<RewardPair> list = Lists.newArrayList();
        int defaultReward = 0;
        for (WisdomRewardsCombined.Builder builder : ServerWisdomRewardManager.getBuilders()) if (builder.category().equals(WisdomRewardCategories.CONSUMED)) {
            for (Map.Entry<ResourceLocation, WisdomRewardInstance.Builder> entry : builder.builders().entrySet()) {
                list.add(new RewardPair(entry.getKey(), entry.getValue()));
            }
            int reward = builder.defaultReward();
            if (reward > defaultReward) defaultReward = reward;
        }
        list.add(new RewardPair(Fantazia.location("default"), new WisdomRewardInstance.Builder(defaultReward), true));
        return list;
    }

    static List<RewardPair> getCrafted() {
        List<RewardPair> list = Lists.newArrayList();
        int defaultReward = 0;
        for (WisdomRewardsCombined.Builder builder : ServerWisdomRewardManager.getBuilders()) if (builder.category().equals(WisdomRewardCategories.CRAFTED)) {
            for (Map.Entry<ResourceLocation, WisdomRewardInstance.Builder> entry : builder.builders().entrySet()) {
                list.add(new RewardPair(entry.getKey(), entry.getValue()));
            }
            int reward = builder.defaultReward();
            if (reward > defaultReward) defaultReward = reward;
        }
        list.add(new RewardPair(Fantazia.location("default"), new WisdomRewardInstance.Builder(defaultReward), true));
        return list;
    }

    static List<RewardPair> getSlayed() {
        List<RewardPair> list = Lists.newArrayList();
        int defaultReward = 0;
        for (WisdomRewardsCombined.Builder builder : ServerWisdomRewardManager.getBuilders()) if (builder.category().equals(WisdomRewardCategories.SLAYED)) {
            for (Map.Entry<ResourceLocation, WisdomRewardInstance.Builder> entry : builder.builders().entrySet()) {
                list.add(new RewardPair(entry.getKey(), entry.getValue()));
            }
            int reward = builder.defaultReward();
            if (reward > defaultReward) defaultReward = reward;
        }
        list.add(new RewardPair(Fantazia.location("default"), new WisdomRewardInstance.Builder(defaultReward), true));
        return list;
    }

    static List<RewardPair> getTamed() {
        List<RewardPair> list = Lists.newArrayList();
        int defaultReward = 0;
        for (WisdomRewardsCombined.Builder builder : ServerWisdomRewardManager.getBuilders()) if (builder.category().equals(WisdomRewardCategories.TAMED)) {
            for (Map.Entry<ResourceLocation, WisdomRewardInstance.Builder> entry : builder.builders().entrySet()) {
                list.add(new RewardPair(entry.getKey(), entry.getValue()));
            }
            int reward = builder.defaultReward();
            if (reward > defaultReward) defaultReward = reward;
        }
        list.add(new RewardPair(Fantazia.location("default"), new WisdomRewardInstance.Builder(defaultReward), true));
        return list;
    }

    @Override
    default void draw(@NotNull RewardPair recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        MutableComponent component;
        if (recipe.isDefault()) component = Component.translatable("fantazia.jei.default_wisdom_reward", recipe.getB().reward()).withStyle(ChatFormatting.BLUE);
        else if (recipe.getB().reward() <= 0) component = Component.translatable("fantazia.jei.no_wisdom_reward").withStyle(ChatFormatting.RED);
        else component = Component.literal(String.valueOf(recipe.getB().reward())).withStyle(ChatFormatting.BLUE);
        int width = font.width(component) + 11;
        int x0 = (92 - width) / 2;
        int y0 = 32;

        guiGraphics.blit(TalentScreen.WISDOM_ICON, x0, y0, 0,0,10,10,10,10);
        guiGraphics.drawString(font, component, x0 + 11, y0 + 1, 0);
    }
}
