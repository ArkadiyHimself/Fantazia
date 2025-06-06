package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class ApplyEffect {

    public static void makePuppeteered(@Nullable Entity master, LivingEntity puppet, int duration) {
        puppet.addEffect(new MobEffectInstance(FTZMobEffects.PUPPETEERED, duration), master);
    }

    public static void makeFurious(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.FURY, duration));
    }

    public static void makeStunned(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.STUN, duration));
    }

    public static void makeDeaf(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.DEAFENED, duration));
    }

    public static void makeDoomed(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.DOOMED, duration));
    }

    public static void makeDisarmed(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.DISARM, duration));
    }

    public static void makeFrozen(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.FROZEN, duration));
    }

    public static void giveAbsoluteBarrier(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.ABSOLUTE_BARRIER, duration));
    }

    public static void giveHaemorrhage(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.HAEMORRHAGE, duration));
    }

    public static void makeDisguised(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.DISGUISED, duration));
    }

    public static void microStun(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.MICROSTUN));
    }
    public static void unDisguise(LivingEntity entity) {
        EffectCleansing.forceCleanse(entity, FTZMobEffects.DISGUISED);
    }

    public static void giveAceInTheHole(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.ACE_IN_THE_HOLE, duration));
    }

    public static void makeElectrocuted(LivingEntity entity, int duration) {
        entity.addEffect(new MobEffectInstance(FTZMobEffects.ELECTROCUTED, duration));
    }

}
