package net.arkadiyhimself.fantazia.api.capability.level;

import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class LevelCapHelper {
    private LevelCapHelper() {}
    public static @Nullable HealingSources getHealingSources(Level level) {
        LevelCap levelCap = LevelCapGetter.getLevelCap(level);
        if (levelCap == null) return null;
        return levelCap.healingSources();
    }
    public static @Nullable FTZDamageTypes.DamageSources getDamageSources(Level level) {
        LevelCap levelCap = LevelCapGetter.getLevelCap(level);
        if (levelCap == null) return null;
        return levelCap.damageSources();
    }
}
