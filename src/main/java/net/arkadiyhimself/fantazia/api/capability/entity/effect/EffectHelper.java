package net.arkadiyhimself.fantazia.api.capability.entity.effect;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.data.spawn.MobEffectsOnSpawnManager;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class EffectHelper {
    private EffectHelper() {}
    public static float bleedingDamage(LivingEntity entity, Vec3 vec3) {
        float movement = (float) vec3.horizontalDistance() / 500f;

        if (entity instanceof Player player) {
            Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
            if (dash != null && dash.isDashing() && dash.getLevel() <= 1) return 7.5f;

            if (player.isSprinting()) return 1.5f * movement;
            else if (player.isCrouching()) return 0.0625f * movement;
        }
        return movement;
    }
    public static boolean hasBarrier(LivingEntity livingEntity) {
        BarrierEffect barrierEffect = EffectGetter.takeEffectHolder(livingEntity, BarrierEffect.class);
        if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

        LayeredBarrierEffect layeredBarrierEffect = EffectGetter.takeEffectHolder(livingEntity, LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

        AbsoluteBarrierEffect absoluteBarrierEffect = EffectGetter.takeEffectHolder(livingEntity, AbsoluteBarrierEffect.class);
        if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;

        return false;
    }
    public static boolean hurtRedColor(LivingEntity livingEntity) {
        if (hasBarrier(livingEntity)) return false;
        if (livingEntity.getLastDamageSource() != null && livingEntity.getLastDamageSource().is(FTZDamageTypeTags.NOT_TURNING_RED)) return false;
        return true;
    }
    public static void infiniteEffect(LivingEntity entity, MobEffect effect, int level) {
        effectWithoutParticles(entity, effect, -1, level);
    }
    public static void effectWithoutParticles(LivingEntity entity, MobEffect effect, int duration, int level) {
        entity.addEffect(new MobEffectInstance(effect, duration, level, true, false, true));
    }

    public static void effectWithoutParticles(LivingEntity entity, MobEffect effect, int duration) {
        effectWithoutParticles(entity, effect, duration, 0);
    }
    public static void makeFurious(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FURY.get(), duration);
    }
    public static void makeStunned(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.STUN.get(), duration);
    }
    public static void makeDeaf(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEAFENED.get(), duration);
    }
    public static void makeDoomed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DOOMED.get(), duration);
    }
    public static void makeDisarmed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DISARM.get(), duration);
    }
    public static void makeFrozen(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FROZEN.get(), duration);
    }
    public static void giveBarrier(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.ABSOLUTE_BARRIER.get(), duration);
    }
    public static void giveReflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.REFLECT.get(), duration);
    }
    public static void giveDeflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEFLECT.get(), duration);
    }
    public static void giveHaemorrhage(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.HAEMORRHAGE.get(), duration);
    }
    public static void microStun(LivingEntity entity) {
        effectWithoutParticles(entity, FTZMobEffects.MICROSTUN.get(), 1);
    }
}
