package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.BarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LivingEffectHelper {

    private LivingEffectHelper() {}

    public static float bleedingDamage(LivingEntity entity, Vec3 vec3) {
        float movement = (float) vec3.horizontalDistance() / 1250f;

        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() <= 1) return 7.5f;

            if (player.isSprinting()) return 1.5f * movement;
            else if (player.isCrouching()) return 0.0625f * movement;
        }
        return movement;
    }

    public static boolean hasBarrier(LivingEntity livingEntity) {
        BarrierEffect barrierEffect = LivingEffectGetter.takeHolder(livingEntity, BarrierEffect.class);
        if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

        LayeredBarrierEffect layeredBarrierEffect = LivingEffectGetter.takeHolder(livingEntity, LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

        AbsoluteBarrierEffect absoluteBarrierEffect = LivingEffectGetter.takeHolder(livingEntity, AbsoluteBarrierEffect.class);
        if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;

        return false;
    }

    public static boolean isDisguised(LivingEntity livingEntity) {
        return livingEntity.hasEffect(FTZMobEffects.DISGUISED);
    }

    public static boolean hurtRedColor(LivingEntity livingEntity) {
        if (hasBarrier(livingEntity)) return false;
        if (livingEntity.getLastDamageSource() != null && livingEntity.getLastDamageSource().is(FTZDamageTypeTags.NOT_TURNING_RED)) return false;
        return true;
    }

    public static void infiniteEffectWithoutParticles(LivingEntity entity, Holder<MobEffect> effect, int level) {
        effectWithoutParticles(entity, effect, -1, level);
    }

    public static void effectWithoutParticles(LivingEntity entity, Holder<MobEffect> effect, int duration, int level) {
        entity.addEffect(new MobEffectInstance(effect, duration, level, true, false, true));
    }

    public static void effectWithoutParticles(LivingEntity entity, Holder<MobEffect> effect, int duration) {
        effectWithoutParticles(entity, effect, duration, 0);
    }

    public static void puppeteer(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.PUPPETEERED, duration);
    }

    public static void makeFurious(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FURY , duration);
    }

    public static void makeStunned(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.STUN , duration);
    }

    public static void makeDeaf(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEAFENED , duration);
    }

    public static void makeDoomed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DOOMED , duration);
    }

    public static void makeDisarmed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DISARM , duration);
    }

    public static void makeFrozen(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FROZEN , duration);
    }

    public static void giveBarrier(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.ABSOLUTE_BARRIER , duration);
    }

    public static void giveReflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.REFLECT , duration);
    }

    public static void giveDeflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEFLECT , duration);
    }

    public static void giveHaemorrhage(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.HAEMORRHAGE, duration);
    }

    public static void makeDisguised(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DISGUISED, duration);
    }

    public static void microStun(LivingEntity entity) {
        effectWithoutParticles(entity, FTZMobEffects.MICROSTUN, 1);
    }
    public static void unDisguise(LivingEntity entity) {
        EffectCleansing.forceCleanse(entity, FTZMobEffects.DISGUISED);
    }
}
