package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.arkadiyhimself.fantazia.api.custom_events.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.events.FTZHooks;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EffectCleansing {

    private EffectCleansing() {}

    public static void forceCleanse(LivingEntity livingEntity, Holder<MobEffect> effect) {
        tryCleanse(livingEntity, Cleanse.ABSOLUTE, effect);
    }

    public static void tryCleanse(LivingEntity livingEntity, Cleanse cleanse, Holder<MobEffect> effect) {
        if (!livingEntity.hasEffect(effect)) return;
        VanillaEventsExtension.CleanseEffectEvent event = FTZHooks.ForgeExtension.onEffectCleanse(livingEntity, livingEntity.getEffect(effect), cleanse);
        if (event.isCanceled()) return;
        if (event.getStrength().strongEnough(effect)) livingEntity.removeEffect(effect);
    }

    public static void tryCleanseAll(LivingEntity livingEntity, Cleanse cleanse, MobEffectCategory category) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().filter(effectInstance -> effectInstance.getEffect().value().getCategory() == category).toList();
        if (effectInstances.isEmpty()) return;
        effectInstances.forEach(effectInstance -> tryCleanse(livingEntity, cleanse, effectInstance.getEffect()));
    }

    public static void tryCleanseAll(LivingEntity livingEntity, Cleanse cleanse) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().toList();
        if (effectInstances.isEmpty()) return;
        effectInstances.forEach(effectInstance -> tryCleanse(livingEntity, cleanse, effectInstance.getEffect()));
    }

    public static void reduceDuration(LivingEntity livingEntity, Holder<MobEffect> effect, int duration) {
        MobEffectInstance instance = livingEntity.getEffect(effect);
        if (instance == null || instance.isInfiniteDuration()) return;
        int newDur = instance.getDuration() - duration;
        int amplifier = instance.getAmplifier();
        livingEntity.removeEffect(effect);
        if (newDur > 0) livingEntity.addEffect(new MobEffectInstance(effect, newDur, amplifier));
    }

    public static void reduceLevel(LivingEntity livingEntity, Holder<MobEffect> effect, int level) {
        MobEffectInstance instance = livingEntity.getEffect(effect);
        if (instance == null) return;
        int duration = instance.getDuration();
        int newAmpl = instance.getAmplifier() - level;
        livingEntity.removeEffect(effect);
        if (newAmpl >= 0) livingEntity.addEffect(new MobEffectInstance(effect, duration, newAmpl));
    }

    public static void reduceLevel(LivingEntity livingEntity, Holder<MobEffect> effect) {
        reduceLevel(livingEntity, effect, 1);
    }
}
