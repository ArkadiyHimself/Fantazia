package net.arkadiyhimself.fantazia.common.advanced.cleanse;

import net.arkadiyhimself.fantazia.common.FantazicHooks;
import net.arkadiyhimself.fantazia.common.api.custom_events.VanillaEventsExtension;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EffectCleansing {

    public static void forceCleanse(LivingEntity livingEntity, Holder<MobEffect> effect) {
        tryCleanse(livingEntity, Cleanse.ABSOLUTE, effect);
    }

    public static void tryCleanse(LivingEntity livingEntity, Cleanse cleanse, Holder<MobEffect> effect) {
        if (!livingEntity.hasEffect(effect)) return;
        VanillaEventsExtension.CleanseEffectEvent event = FantazicHooks.ForgeExtension.onEffectCleanse(livingEntity, livingEntity.getEffect(effect), cleanse);
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

    public static void changeDuration(LivingEntity livingEntity, Holder<MobEffect> effect, int delta) {
        MobEffectInstance instance = livingEntity.getEffect(effect);
        if (instance == null || instance.isInfiniteDuration()) return;
        int newDur = instance.getDuration() + delta;
        int amplifier = instance.getAmplifier();
        boolean ambient = instance.isAmbient();
        boolean visible = instance.isVisible();
        boolean showIcon = instance.showIcon();
        livingEntity.removeEffect(effect);
        if (newDur > 0) livingEntity.addEffect(new MobEffectInstance(effect, newDur, amplifier, ambient, visible, showIcon));
    }

    public static void reduceLevel(LivingEntity livingEntity, Holder<MobEffect> effect, int level) {
        MobEffectInstance instance = livingEntity.getEffect(effect);
        if (instance == null) return;
        int duration = instance.getDuration();
        int newAmpl = instance.getAmplifier() - level;
        boolean ambient = instance.isAmbient();
        boolean visible = instance.isVisible();
        boolean showIcon = instance.showIcon();
        livingEntity.removeEffect(effect);
        if (newAmpl >= 0) livingEntity.addEffect(new MobEffectInstance(effect, duration, newAmpl, ambient, visible, showIcon));
    }

    public static void reduceLevel(LivingEntity livingEntity, Holder<MobEffect> effect) {
        reduceLevel(livingEntity, effect, 1);
    }
}
