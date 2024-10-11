package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nullable;

public enum Cleanse {
    BASIC(Component.translatable("fantazia.cleanse.basic"),0, null),
    MEDIUM(Component.translatable("fantazia.cleanse.medium"),1, FTZMobEffectTags.CleanseTags.MEDIUM),
    POWERFUL(Component.translatable("fantazia.cleanse.powerful"),2, FTZMobEffectTags.CleanseTags.POWERFUL),
    ABSOLUTE(Component.translatable("fantazia.cleanse.absolute"),3, FTZMobEffectTags.CleanseTags.ABSOLUTE);
    private final Component name;
    private final int strength;
    private final TagKey<MobEffect> tagKey;
    Cleanse(Component name, int strength, @Nullable TagKey<MobEffect> tagKey) {
        this.name = name;
        this.strength = strength;
        this.tagKey = tagKey;
    }
    public Component getName() {
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
