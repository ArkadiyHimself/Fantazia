package net.arkadiyhimself.fantazia.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class TextComponents {

    public static final Component HOLD_SHIFT_TO_SEE_MORE_COMPONENT = Component.translatable("tooltip.fantazia.common.shift_to_see_more")
            .withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC, ChatFormatting.DARK_PURPLE);
    public static final Component JEI_PRESS_TO_SEE_WISDOM_REWARDS = Component.translatable( "fantazia.jei.click_to_see_wisdom_rewards")
            .withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC, ChatFormatting.DARK_BLUE);


}
