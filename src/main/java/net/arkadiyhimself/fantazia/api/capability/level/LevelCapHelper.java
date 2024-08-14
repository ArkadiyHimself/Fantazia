package net.arkadiyhimself.fantazia.api.capability.level;

import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class LevelCapHelper {
    @Nullable
    public static HealingSources healingSources(Level level) {
        LevelCap levelCap = LevelCapGetter.getLevelCap(level);
        if (levelCap == null) return null;
        return levelCap.healingSources();
    }
}
