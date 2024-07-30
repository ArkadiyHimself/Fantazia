package net.arkadiyhimself.fantazia.api.capability.entity.effect;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class EffectHelper {
    public static List<ResourceKey<DamageType>> NO_RED_GLOW = new ArrayList<>() {{
        add(FTZDamageTypes.BLEEDING);
    }};
    public static float bleedingDamage(LivingEntity entity, Vec3 vec3) {
        float movement = (float) vec3.horizontalDistance() / 50f;
        if (entity instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                Dash dash = abilityManager.takeAbility(Dash.class);
                if (dash != null && dash.isDashing() && dash.getLevel() <= 1) {
                    return 7.5f;
                }
            }
            if (player.isSprinting()) {
                return 1.5f * movement;
            } else if (player.isCrouching()) {
                return 0.0625f * movement;
            }
        }
        return movement;
    }
    public static boolean hasBarrier(LivingEntity livingEntity) {
        EffectManager effectManager = EffectGetter.getUnwrap(livingEntity);
        if (effectManager == null) return false;

        BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
        if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

        LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

        AbsoluteBarrierEffect absoluteBarrierEffect = effectManager.takeEffect(AbsoluteBarrierEffect.class);
        if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;

        return false;
    }
    public static boolean hurtRedColor(LivingEntity livingEntity) {
        if (hasBarrier(livingEntity)) return false;
        if (livingEntity.getLastDamageSource() != null) for (ResourceKey<DamageType> resourceKey : NO_RED_GLOW) if (livingEntity.getLastDamageSource().is(resourceKey)) return false;
        return true;
    }
    public static void effectWithoutParticles(LivingEntity entity, MobEffect effect, int duration, int level) {
        entity.addEffect(new MobEffectInstance(effect, duration, level, true, false, true));
    }

    public static void effectWithoutParticles(LivingEntity entity, MobEffect effect, int duration) {
        effectWithoutParticles(entity, effect, duration, 0);
    }
    public static void makeFurious(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FURY, duration);
    }
    public static void makeStunned(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.STUN, duration);
    }
    public static void makeDeaf(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEAFENED, duration);
    }
    public static void makeDoomed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DOOMED, duration);
    }
    public static void makeDisarmed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DISARM, duration);
    }
    public static void giveBarrier(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.ABSOLUTE_BARRIER, duration);
    }
    public static void giveReflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.REFLECT, duration);
    }
    public static void giveDeflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEFLECT, duration);
    }
}