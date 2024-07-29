package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EffectCleansing {
    public static void forceCleanse(LivingEntity livingEntity, MobEffect effect) {
        tryCleanse(livingEntity, Cleanse.ABSOLUTE, effect);
    }
    public static boolean tryCleanse(LivingEntity livingEntity, Cleanse cleanse, MobEffect effect) {
        if (!livingEntity.hasEffect(effect)) return false;
        boolean flag = FTZEvents.ForgeExtenstion.onEffectCleanse(livingEntity, livingEntity.getEffect(effect), cleanse);
        if (!flag) return false;
        if (cleanse.isStrongEnough(CleanseStrength.getRequiredStrength(effect))) {
            livingEntity.removeEffect(effect);
            return true;
        } else return false;
    }
    public static void tryCleanseAll(LivingEntity livingEntity, Cleanse cleanse, MobEffectCategory category) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().filter(effectInstance -> effectInstance.getEffect().getCategory() == category).toList();
        if (effectInstances.isEmpty()) return;
        effectInstances.forEach(effectInstance -> tryCleanse(livingEntity, cleanse, effectInstance.getEffect()));
    }
    public static void tryCleanseAll(LivingEntity livingEntity, Cleanse cleanse) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().toList();
        if (effectInstances.isEmpty()) return;
        effectInstances.forEach(effectInstance -> tryCleanse(livingEntity, cleanse, effectInstance.getEffect()));
    }
}
