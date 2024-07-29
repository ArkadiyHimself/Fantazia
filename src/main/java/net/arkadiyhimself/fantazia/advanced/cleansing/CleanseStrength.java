package net.arkadiyhimself.fantazia.advanced.cleansing;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;

public class CleanseStrength {
    public static final HashMap<MobEffect, Cleanse> EFFECTS = Maps.newHashMap();
    public static void onSetup() {
        // vanilla effects
        EFFECTS.put(MobEffects.DIG_SPEED, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.DIG_SLOWDOWN, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.DAMAGE_RESISTANCE, Cleanse.POWERFUL);
        EFFECTS.put(MobEffects.FIRE_RESISTANCE, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.WATER_BREATHING, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.INVISIBILITY, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.WITHER, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.HEALTH_BOOST, Cleanse.POWERFUL);
        EFFECTS.put(MobEffects.ABSORPTION, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.GLOWING, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.LUCK, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.UNLUCK, Cleanse.MEDIUM);
        EFFECTS.put(MobEffects.BAD_OMEN, Cleanse.ABSOLUTE);
        EFFECTS.put(MobEffects.HERO_OF_THE_VILLAGE, Cleanse.ABSOLUTE);

        // my own effects
        EFFECTS.put(FTZMobEffects.FURY, Cleanse.POWERFUL);
        EFFECTS.put(FTZMobEffects.STUN, Cleanse.POWERFUL);
        EFFECTS.put(FTZMobEffects.BARRIER, Cleanse.MEDIUM);
        EFFECTS.put(FTZMobEffects.LAYERED_BARRIER, Cleanse.MEDIUM);
        EFFECTS.put(FTZMobEffects.ABSOLUTE_BARRIER, Cleanse.ABSOLUTE);
        EFFECTS.put(FTZMobEffects.DOOMED, Cleanse.ABSOLUTE);
        EFFECTS.put(FTZMobEffects.DISARM, Cleanse.POWERFUL);
        EFFECTS.put(FTZMobEffects.REFLECT, Cleanse.MEDIUM);
        EFFECTS.put(FTZMobEffects.REFLECT, Cleanse.MEDIUM);
    }
    public static Cleanse getRequiredStrength(MobEffect effect) {
        return EFFECTS.getOrDefault(effect, Cleanse.BASIC);
    }
}
