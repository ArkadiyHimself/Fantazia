package net.arkadiyhimself.fantazia.api.attachment.level;

import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class LevelAttributesHelper {
    private LevelAttributesHelper() {}
    public static @Nullable HealingSourcesHolder getHealingSources(Level level) {
        return LevelAttributesGetter.takeHolder(level, HealingSourcesHolder.class);
    }
    public static @Nullable DamageSourcesHolder getDamageSources(Level level) {
        return LevelAttributesGetter.takeHolder(level, DamageSourcesHolder.class);
    }
}
