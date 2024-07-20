package net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;

public class CleanseStrength {
    public static final HashMap<MobEffect, Cleansing> EFFECTS = Maps.newHashMap();
    public static void onSetup() {
        // vanilla effects
        EFFECTS.put(MobEffects.DIG_SPEED, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.DIG_SLOWDOWN, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.DAMAGE_RESISTANCE, Cleansing.POWERFUL);
        EFFECTS.put(MobEffects.FIRE_RESISTANCE, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.WATER_BREATHING, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.INVISIBILITY, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.WITHER, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.HEALTH_BOOST, Cleansing.POWERFUL);
        EFFECTS.put(MobEffects.ABSORPTION, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.GLOWING, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.LUCK, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.UNLUCK, Cleansing.MEDIUM);
        EFFECTS.put(MobEffects.BAD_OMEN, Cleansing.ABSOLUTE);
        EFFECTS.put(MobEffects.HERO_OF_THE_VILLAGE, Cleansing.ABSOLUTE);

        // my own effects
        EFFECTS.put(MobEffectRegistry.FURY.get(), Cleansing.POWERFUL);
        EFFECTS.put(MobEffectRegistry.STUN.get(), Cleansing.POWERFUL);
        EFFECTS.put(MobEffectRegistry.BARRIER.get(), Cleansing.MEDIUM);
        EFFECTS.put(MobEffectRegistry.LAYERED_BARRIER.get(), Cleansing.MEDIUM);
        EFFECTS.put(MobEffectRegistry.ABSOLUTE_BARRIER.get(), Cleansing.ABSOLUTE);
        EFFECTS.put(MobEffectRegistry.ABSOLUTE_BARRIER.get(), Cleansing.ABSOLUTE);
        EFFECTS.put(MobEffectRegistry.DOOMED.get(), Cleansing.ABSOLUTE);
        EFFECTS.put(MobEffectRegistry.DISARM.get(), Cleansing.POWERFUL);
        EFFECTS.put(MobEffectRegistry.REFLECT.get(), Cleansing.MEDIUM);
        EFFECTS.put(MobEffectRegistry.DEFLECT.get(), Cleansing.MEDIUM);
    }
    public static Cleansing getRequiredStrength(MobEffect effect) {
        return EFFECTS.getOrDefault(effect, Cleansing.BASIC);
    }
}
