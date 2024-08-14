package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.arkadiyhimself.fantazia.tags.FTZMobEffectTags;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

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
    public boolean strongEnough(MobEffect effect) {
        return this.strength >= requiredCleanse(effect).strength;
    }
    public static Cleanse requiredCleanse(MobEffect mobEffect) {
        ITagManager<MobEffect> tagManager = ForgeRegistries.MOB_EFFECTS.tags();
        if (tagManager == null) return BASIC;
        for (Cleanse cleanse : Cleanse.values()) if (cleanse.tagKey != null && tagManager.getTag(cleanse.tagKey).contains(mobEffect)) return cleanse;
        return BASIC;
    }
}
