package net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager;

import net.arkadiyhimself.fantazia.HandlersAndHelpers.CustomEvents.NewEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class EffectCleansing {
    public static void forceCleanse(LivingEntity livingEntity, MobEffect effect) {
        tryCleanse(livingEntity, Cleansing.ABSOLUTE, effect);
    }
    public static boolean tryCleanse(LivingEntity livingEntity, Cleansing cleansing, MobEffect effect) {
        if (!livingEntity.hasEffect(effect)) return false;
        boolean flag = NewEvents.ForgeExtenstion.onEffectCleanse(livingEntity, livingEntity.getEffect(effect), cleansing);
        if (!flag) return false;
        if (cleansing.isStrongEnough(CleanseStrength.getRequiredStrength(effect))) {
            livingEntity.removeEffect(effect);
            return true;
        } else return false;
    }
    public static void tryCleanseAll(LivingEntity livingEntity, Cleansing cleansing, MobEffectCategory category) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().filter(effectInstance -> effectInstance.getEffect().getCategory() == category).toList();
        if (effectInstances.isEmpty()) return;
        effectInstances.forEach(effectInstance -> tryCleanse(livingEntity, cleansing, effectInstance.getEffect()));
    }
    public static void tryCleanseAll(LivingEntity livingEntity, Cleansing cleansing) {
        List<MobEffectInstance> effectInstances = livingEntity.getActiveEffects().stream().toList();
        if (effectInstances.isEmpty()) return;
        effectInstances.forEach(effectInstance -> tryCleanse(livingEntity, cleansing, effectInstance.getEffect()));
    }
}
