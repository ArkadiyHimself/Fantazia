package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;

public enum Cleanse {
    BASIC(Component.translatable("fantazia.cleanse.basic"),0, null, ChatFormatting.BLUE, ChatFormatting.BOLD),
    MEDIUM(Component.translatable("fantazia.cleanse.medium"),1, FTZMobEffectTags.Cleanse.MEDIUM, ChatFormatting.AQUA, ChatFormatting.BOLD),
    POWERFUL(Component.translatable("fantazia.cleanse.powerful"),2, FTZMobEffectTags.Cleanse.POWERFUL, ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
    ABSOLUTE(Component.translatable("fantazia.cleanse.absolute"),3, FTZMobEffectTags.Cleanse.ABSOLUTE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);

    private final Component name;
    private final int strength;
    private final TagKey<MobEffect> tagKey;
    private final ChatFormatting[] formatting;

    Cleanse(Component name, int strength, @Nullable TagKey<MobEffect> tagKey, ChatFormatting... formatting) {
        this.name = name;
        this.strength = strength;
        this.tagKey = tagKey;
        this.formatting = formatting;
    }

    public Component getName() {
        return name.copy().withStyle(formatting);
    }

    public boolean strongEnough(Holder<MobEffect> effect) {
        return this.strength >= requiredCleanse(effect).strength;
    }

    public static Cleanse requiredCleanse(Holder<MobEffect> mobEffect) {
        for (Cleanse cleanse : Cleanse.values()) if (cleanse.tagKey != null && mobEffect.is(cleanse.tagKey)) return cleanse;
        return BASIC;
    }

}
