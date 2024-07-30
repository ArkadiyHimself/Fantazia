package net.arkadiyhimself.fantazia.advanced.capacity.spellhandler;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.advanced.healing.HealingTypes;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.decoration.ArmorStand;

public class Spells {
    public final static SelfSpell ENTANGLE = new SelfSpell(0, 50, Fantazia.res("entangle"), FTZSoundEvents.ENTANGLE)
            .setConditions(entity -> entity.getHealth() <= entity.getMaxHealth() * 0.1f)
            .setOnCast(entity -> EffectHelper.giveBarrier(entity, 10))
            .cleanse(Cleanse.POWERFUL);
    public final static TargetedSpell<LivingEntity> SONIC_BOOM = new TargetedSpell<>(LivingEntity.class, 12f, 4.5f, 240, Fantazia.res("sonic_boom"), SoundEvents.WARDEN_SONIC_BOOM)
            .setConditions((caster, target) -> !(target instanceof ArmorStand))
            .setBefore((caster, target) -> {
                VisualHelper.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
                caster.level().playSound(null, caster.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL);
            })
            .setAfter((caster, target) -> target.hurt(caster.level().damageSources().sonicBoom(caster), 15f))
            .seeThruWalls();
    public final static TargetedSpell<Mob> DEVOUR = new TargetedSpell<>(Mob.class, 6f, 5f, 2000, Fantazia.res("devour"), FTZSoundEvents.DEVOUR)
            .setConditions((caster, entity) -> entity.getMaxHealth() <= 100)
            .setAfter((caster, target) -> {
                float healing = target.getMobType() == MobType.UNDEAD ? target.getHealth() / 8 : target.getHealth() / 4;
                HealingSource source = new HealingSource(HealingTypes.DEVOUR, target);
                AdvancedHealing.heal(caster, source, healing);
                if (caster instanceof ServerPlayer player) {
                    int devour = (int) (target.getHealth() / 4);
                    int hunger = 20 - player.getFoodData().getFoodLevel();
                    int food;
                    int saturation;
                    if (hunger >= devour) {
                        food = devour;
                        saturation = 0;
                    } else {
                        food = hunger;
                        saturation = devour - hunger;
                    }
                    player.getFoodData().eat(food, saturation);
                }
                EffectHelper.effectWithoutParticles(caster, FTZMobEffects.BARRIER,  500, (int) target.getHealth() / 4 - 1);
                EffectHelper.effectWithoutParticles(caster, FTZMobEffects.MIGHT, 500, (int) target.getHealth() / 4 - 1);
                FantazicCombat.dropExperience(target, 5);
                int particles = switch (Minecraft.getInstance().options.particles().get()) {
                    case MINIMAL -> 15;
                    case DECREASED -> 30;
                    case ALL -> 45;
                };
                for (int i = 0; i < particles; ++i) VisualHelper.randomParticleOnModel(target, ParticleTypes.SMOKE, VisualHelper.ParticleMovement.REGULAR);

                int flameParts = particles / 2;
                for (int i = 0; i < flameParts; ++i) VisualHelper.randomParticleOnModel(target, ParticleTypes.FLAME, VisualHelper.ParticleMovement.REGULAR);

                target.playSound(FTZSoundEvents.DEVOUR);
                target.remove(Entity.RemovalReason.KILLED);
            });
    public final static PassiveSpell REFLECT = new PassiveSpell(1.5f, 200, Fantazia.res("reflect"), FTZSoundEvents.MYSTIC_MIRROR);
    public final static PassiveSpell DAMNED_WRATH = new PassiveSpell(0f, 600, Fantazia.res("damned_wrath"), FTZSoundEvents.BLOODLUST_AMULET).cleanse(Cleanse.MEDIUM);
}
