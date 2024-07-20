package net.arkadiyhimself.fantazia.AdvancedMechanics.Abilities;

import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.AdvancedHealing;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingSource;
import net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager.HealingTypes;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
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
    public final static SelfSpell ENTANGLE = new SelfSpell(0, 50, Fantazia.res("entangle"), SoundRegistry.ENTANGLE.get())
            .setConditions(entity -> entity.getHealth() <= entity.getMaxHealth() * 0.1f)
            .setOnCast(entity -> WhereMagicHappens.Abilities.addEffectWithoutParticles(entity, MobEffectRegistry.ABSOLUTE_BARRIER.get(), 10));
    public final static TargetedSpell<LivingEntity> SONIC_BOOM = new TargetedSpell<>(LivingEntity.class, 12f, 4.5f, 240, Fantazia.res("sonic_boom"), SoundEvents.WARDEN_SONIC_BOOM)
            .setConditions((caster, target) -> !(target instanceof ArmorStand))
            .setBefore((caster, target) -> {
                WhereMagicHappens.Abilities.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
                caster.level().playSound(null, caster.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL);
            })
            .setAfter((caster, target) -> target.hurt(caster.level().damageSources().sonicBoom(caster), 15f))
            .seeThruWalls();
    public final static TargetedSpell<Mob> DEVOUR = new TargetedSpell<>(Mob.class, 6f, 5f, 2000, Fantazia.res("devour"), SoundRegistry.DEVOUR.get())
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
                WhereMagicHappens.Abilities.addEffectWithoutParticles(caster, MobEffectRegistry.BARRIER.get(),  500, (int) target.getHealth() / 4 - 1);
                WhereMagicHappens.Abilities.addEffectWithoutParticles(caster, MobEffectRegistry.MIGHT.get(), 500, (int) target.getHealth() / 4 - 1);
                WhereMagicHappens.Abilities.dropExperience(target, 5);
                int particles = switch (Minecraft.getInstance().options.particles().get()) {
                    case MINIMAL -> 15;
                    case DECREASED -> 30;
                    case ALL -> 45;
                };
                for (int i = 0; i < particles; ++i) {
                    WhereMagicHappens.Abilities.randomParticleOnModel(target, ParticleTypes.SMOKE, WhereMagicHappens.Abilities.ParticleMovement.REGULAR);
                }
                int flameParts = particles / 2;
                for (int i = 0; i < flameParts; ++i) {
                    WhereMagicHappens.Abilities.randomParticleOnModel(target, ParticleTypes.FLAME, WhereMagicHappens.Abilities.ParticleMovement.REGULAR);
                }
                target.playSound(SoundRegistry.DEVOUR.get());
                target.remove(Entity.RemovalReason.KILLED);
            });
    public final static PassiveSpell REFLECT = new PassiveSpell(1.5f, 200, Fantazia.res("reflect"), SoundRegistry.MYSTIC_MIRROR.get());
    public final static PassiveSpell DAMNED_WRATH = new PassiveSpell(0f, 600, Fantazia.res("damned_wrath"), SoundRegistry.BLOODLUST_AMULET.get());
}
