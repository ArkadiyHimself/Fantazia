package net.arkadiyhimself.fantazia.advanced.cleansing;

import com.mojang.serialization.Codec;
import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public enum Cleanse implements StringRepresentable {

    BASIC("basic", Component.translatable("fantazia.cleanse.basic"),0, null, ChatFormatting.BLUE, ChatFormatting.BOLD),
    MEDIUM("medium", Component.translatable("fantazia.cleanse.medium"),1, FTZMobEffectTags.Cleanse.MEDIUM, ChatFormatting.AQUA, ChatFormatting.BOLD),
    POWERFUL("powerful", Component.translatable("fantazia.cleanse.powerful"),2, FTZMobEffectTags.Cleanse.POWERFUL, ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
    ABSOLUTE("absolute", Component.translatable("fantazia.cleanse.absolute"),3, FTZMobEffectTags.Cleanse.ABSOLUTE, ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);

    public static final Codec<Cleanse> CODEC = StringRepresentable.fromEnum(Cleanse::values);

    private final String name;
    private final Component description;
    private final int strength;
    private final TagKey<MobEffect> tagKey;
    private final ChatFormatting[] formatting;

    Cleanse(String name, Component description, int strength, @Nullable TagKey<MobEffect> tagKey, ChatFormatting... formatting) {
        this.name = name;
        this.description = description;
        this.strength = strength;
        this.tagKey = tagKey;
        this.formatting = formatting;
    }

    public Component getDescription() {
        return description.copy().withStyle(formatting);
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }

    public boolean strongEnough(Holder<MobEffect> effect) {
        return this.strength >= requiredCleanse(effect).strength;
    }

    public static Cleanse requiredCleanse(Holder<MobEffect> mobEffect) {
        for (Cleanse cleanse : Cleanse.values()) if (cleanse.tagKey != null && mobEffect.is(cleanse.tagKey)) return cleanse;
        return BASIC;
    }

}
