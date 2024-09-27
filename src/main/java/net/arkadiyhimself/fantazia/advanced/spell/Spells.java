package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.LivingData;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;

public class Spells {
    private Spells() {}

    public static final class Self {
        private Self() {}
        public static final SelfSpell ENTANGLE = new SelfSpell(0, 50, FTZSoundEvents.ENTANGLE)
                .setConditions(entity -> entity.getHealth() <= entity.getMaxHealth() * 0.15f)
                .setOnCast(entity -> EffectHelper.giveBarrier(entity, 10))
                .cleanse(Cleanse.POWERFUL);
        public static final SelfSpell REWIND = new SelfSpell(0,300, FTZSoundEvents.REWIND)
                .setConditions(entity -> {
                    LivingData data = DataGetter.takeDataHolder(entity, LivingData.class);
                    return data != null && data.writtenParameters();
                })
                .setOnCast(entity -> {
                    LivingData data = DataGetter.takeDataHolder(entity, LivingData.class);
                    if (data == null || !data.tryReadParameters(0)) return;
                    EffectCleansing.tryCleanseAll(entity, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                    if (!(entity.level() instanceof ServerLevel)) return;
                    for (int i = 0; i < 12; i++) VisualHelper.randomParticleOnModel(entity, FTZParticleTypes.TIME_TRAVEL.get(), VisualHelper.ParticleMovement.REGULAR);
                })
                .cleanse(Cleanse.MEDIUM);
    }
    public static final class Targeted {
        private Targeted() {}
        public static final TargetedSpell<LivingEntity> SONIC_BOOM = new TargetedSpell<>(LivingEntity.class, 12f, 4.5f, 240, () -> SoundEvents.WARDEN_SONIC_BOOM)
                .setConditions((caster, target) -> !(target instanceof ArmorStand))
                .setBefore((caster, target) -> {
                    VisualHelper.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
                    caster.level().playSound(null, caster.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL);
                })
                .setAfter((caster, target) -> target.hurt(caster.level().damageSources().sonicBoom(caster), 15f));
        public static final TargetedSpell<Mob> DEVOUR = new TargetedSpell<>(Mob.class, 6f, 5f, 2000)
                .setConditions((caster, entity) -> entity.getMaxHealth() <= 100)
                .setAfter((caster, target) -> {
                    float healing = target.getMobType() == MobType.UNDEAD ? target.getHealth() / 8 : target.getHealth() / 4;
                    HealingSources healingSources = LevelCapHelper.getHealingSources(target.level());
                    if (healingSources != null) AdvancedHealing.tryHeal(caster, healingSources.devour(target), healing);
                    EffectHelper.effectWithoutParticles(caster, FTZMobEffects.BARRIER.get(),  500, (int) target.getHealth() / 4 - 1);
                    EffectHelper.effectWithoutParticles(caster, FTZMobEffects.MIGHT.get(), 500, (int) target.getHealth() / 4 - 1);
                    FantazicCombat.dropExperience(target, 5);

                    for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() * 15 + 15; ++i) VisualHelper.randomParticleOnModel(target, ParticleTypes.SMOKE, VisualHelper.ParticleMovement.REGULAR);
                    for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() * 5 + 15; ++i) VisualHelper.randomParticleOnModel(target, ParticleTypes.FLAME, VisualHelper.ParticleMovement.REGULAR);

                    target.playSound(FTZSoundEvents.DEVOUR.get());
                    target.remove(Entity.RemovalReason.KILLED);

                    if (!(caster instanceof ServerPlayer player)) return;

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
                });
        public static final TargetedSpell<LivingEntity> BOUNCE = new TargetedSpell<>(LivingEntity.class, 16f, 3.5f, 160)
                .setConditions((caster, entity) -> !FantazicCombat.isInvulnerable(entity))
                .setBefore((caster, entity) -> {
                    for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() * 8 + 16; i++) VisualHelper.randomParticleOnModel(caster, ParticleTypes.PORTAL, VisualHelper.ParticleMovement.REGULAR);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.BOUNCE.get(), SoundSource.NEUTRAL);
                })
                .setAfter((caster, entity) -> {
                    Vec3 delta1 = entity.position().subtract(caster.position());
                    Vec3 normal = delta1.normalize();
                    Vec3 delta2 = delta1.subtract(normal.scale(0.5));
                    Vec3 finalPos = caster.position().add(delta2);
                    caster.teleportTo(finalPos.x(), entity.getY(), finalPos.z());
                    EffectHelper.microStun(entity);
                    EffectHelper.makeDisarmed(entity, 50);
                });
        public static final TargetedSpell<LivingEntity> LIGHTNING_STRIKE = new TargetedSpell<>(LivingEntity.class, 12f, 5.5f, 400)
                .setAfter((caster, entity) -> {
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(caster.level());
                    if (lightningBolt == null) return;
                    lightningBolt.moveTo(entity.position());
                    lightningBolt.setCause(caster instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    caster.level().addFreshEntity(lightningBolt);
                });
    }
    public static final class Passive {
        private Passive() {}
        public static final PassiveSpell REFLECT = new PassiveSpell(1.5f, 200, FTZSoundEvents.REFLECT);
        public static final PassiveSpell DAMNED_WRATH = new PassiveSpell(0f, 600, FTZSoundEvents.BLOODLUST_AMULET).cleanse(Cleanse.MEDIUM);
    }
}
