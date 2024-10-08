package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.arkadiyhimself.fantazia.events.FTZEvents;
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
        boolean flag = FTZEvents.ForgeExtension.onEffectCleanse(livingEntity, livingEntity.getEffect(effect), cleanse);
        if (!flag) return;
        if (cleanse.strongEnough(effect)) livingEntity.removeEffect(effect);
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
}
