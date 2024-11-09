package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.CommonDataHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.particless.options.ElectroParticleOption;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class Spells {

    private Spells() {}

    public static final class Self {
        private Self() {}

        public static final SelfSpell ENTANGLE = new SelfSpell.Builder(0f, 50, FTZSoundEvents.ENTANGLE_CAST, null)
                .conditions(entity -> entity.getHealth() <= entity.getMaxHealth() * 0.15f)
                .onCast(entity -> LivingEffectHelper.giveBarrier(entity, 10))
                .build();

        public static final SelfSpell REWIND = new SelfSpell.Builder(2f, 300, FTZSoundEvents.REWIND_CAST, null)
                .conditions(entity -> {
                    CommonDataHolder data = LivingDataGetter.takeHolder(entity, CommonDataHolder.class);
                    return data != null && data.writtenParameters();
                })
                .onCast(entity -> {
                    CommonDataHolder data = LivingDataGetter.takeHolder(entity, CommonDataHolder.class);
                    if (data == null || !data.tryReadParameters(0, entity)) return;
                    EffectCleansing.tryCleanseAll(entity, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                    if (!(entity.level() instanceof ServerLevel)) return;
                    for (int i = 0; i < 12; i++) VisualHelper.randomParticleOnModel(entity, FTZParticleTypes.TIME_TRAVEL.get(), VisualHelper.ParticleMovement.REGULAR);
                })
                .cleanse()
                .build();

        public static final SelfSpell TRANSFER = new SelfSpell.Builder(2.5f,600, FTZSoundEvents.EFFECT_HAEMORRHAGE_FLESH_RIPPING,null)
                .conditions(owner -> !(owner instanceof Player player) || !player.getAbilities().invulnerable)
                .cleanse(Cleanse.MEDIUM)
                .onCast(livingEntity -> {
                    DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(livingEntity.level());
                    if (damageSourcesHolder == null) return;

                    float damage = Math.min(livingEntity.getHealth() - 0.01f, 4);
                    int sacrifice = (int) Math.max(damage, 1);

                    LivingEffectHelper.effectWithoutParticles(livingEntity, FTZMobEffects.MIGHT,200, sacrifice);
                    LivingEffectHelper.effectWithoutParticles(livingEntity, FTZMobEffects.RAPID,200, sacrifice);

                    livingEntity.hurt(damageSourcesHolder.removal(), sacrifice);
                })
                .build();
    }
    public static final class Targeted {
        private Targeted() {}

        public static final TargetedSpell<LivingEntity> SONIC_BOOM = new TargetedSpell.Builder<>(4.5f, 240, Holder.direct(SoundEvents.WARDEN_SONIC_BOOM), null, LivingEntity.class, 12f)
                .conditions((caster, target) -> !(target instanceof ArmorStand))
                .beforeBlockChecking((caster, target) -> {
                    VisualHelper.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
                    caster.level().playSound(null, caster.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.NEUTRAL);
                })
                .afterBlockChecking((caster, target) -> target.hurt(caster.level().damageSources().sonicBoom(caster), 15f))
                .build();

        public static final TargetedSpell<Mob> DEVOUR = new TargetedSpell.Builder<>(5f, 2000, FTZSoundEvents.DEVOUR_CAST, null, Mob.class, 8f)
                .conditions((caster, entity) -> entity.getMaxHealth() <= 100)
                .afterBlockChecking((caster, target) -> {
                    float healing = target.getType().is(EntityTypeTags.INVERTED_HEALING_AND_HARM) ? target.getHealth() / 8 : target.getHealth() / 4;
                    HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(target.level());
                    if (healingSources != null) AdvancedHealing.tryHeal(caster, healingSources.devour(target), healing);
                    LivingEffectHelper.effectWithoutParticles(caster, FTZMobEffects.BARRIER,  500, (int) target.getHealth() / 4 - 1);
                    LivingEffectHelper.effectWithoutParticles(caster, FTZMobEffects.MIGHT, 500, (int) target.getHealth() / 4 - 1);
                    FantazicCombat.dropExperience(target, 5, caster);

                    for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() * 15 + 15; ++i) VisualHelper.randomParticleOnModel(target, ParticleTypes.SMOKE, VisualHelper.ParticleMovement.REGULAR);
                    for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() * 5 + 15; ++i) VisualHelper.randomParticleOnModel(target, ParticleTypes.FLAME, VisualHelper.ParticleMovement.REGULAR);

                    target.playSound(FTZSoundEvents.DEVOUR_CAST.get());
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
                })
                .ownerTick(entity -> {
                    if (!(entity instanceof Player player)) return;
                    LivingEffectHelper.effectWithoutParticles(player, MobEffects.HUNGER, 2);
                    player.causeFoodExhaustion(player.getFoodData().getSaturationLevel() > 0 ? 0.1f : 0.01f);
                })
                .build();

        public static final TargetedSpell<LivingEntity> BOUNCE = new TargetedSpell.Builder<>(3.5f, 160, FTZSoundEvents.BOUNCE_CAST, FTZSoundEvents.BOUNCE_RECHARGE, LivingEntity.class, 16f)
                .conditions((caster, entity) -> !FantazicCombat.isInvulnerable(entity))
                .beforeBlockChecking((caster, entity) -> {
                    for (int i = 0; i < Minecraft.getInstance().options.particles().get().getId() * 8 + 16; i++) VisualHelper.randomParticleOnModel(caster, ParticleTypes.PORTAL, VisualHelper.ParticleMovement.REGULAR);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.BOUNCE_CAST.get(), SoundSource.NEUTRAL);
                })
                .afterBlockChecking((caster, entity) -> {
                    Vec3 delta1 = entity.position().subtract(caster.position());
                    Vec3 normal = delta1.normalize();
                    Vec3 delta2 = delta1.subtract(normal.scale(0.5));
                    Vec3 finalPos = caster.position().add(delta2);
                    caster.teleportTo(finalPos.x(), entity.getY(), finalPos.z());
                    LivingEffectHelper.microStun(entity);
                    LivingEffectHelper.makeDisarmed(entity, 50);
                })
                .tickingConditions(AbstractSpell.TickingConditions.NOT_ON_COOLDOWN)
                .ownerTick(livingEntity -> {
                    if ((livingEntity.tickCount & 2) == 0) VisualHelper.randomParticleOnModel(livingEntity, ParticleTypes.PORTAL, VisualHelper.ParticleMovement.REGULAR);
                })
                .cleanse(Cleanse.MEDIUM)
                .build();

        public static final TargetedSpell<LivingEntity> LIGHTNING_STRIKE = new TargetedSpell.Builder<>(5.5f, 400, null, FTZSoundEvents.LIGHTNING_STRIKE_RECHARGE, LivingEntity.class, 12f)
                .afterBlockChecking((caster, entity) -> {
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(caster.level());
                    if (lightningBolt == null) return;
                    lightningBolt.moveTo(entity.position());
                    lightningBolt.setCause(caster instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    caster.level().addFreshEntity(lightningBolt);
                })
                .tickingConditions(AbstractSpell.TickingConditions.NOT_ON_COOLDOWN)
                .ownerTick(livingEntity -> {
                    if (livingEntity.tickCount % 3 == 0) for (int i = 0; i < 2; i++) VisualHelper.randomEntityChasingParticle(livingEntity, ((entity, vec3) -> new ElectroParticleOption(entity.getId(), vec3, FTZParticleTypes.ELECTRO.random())), 0.65f);
                    if (livingEntity.tickCount % 16 == 0) livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.LIGHTNING_STRIKE_TICK.get(), SoundSource.PLAYERS, 0.115f,1.05f);
                })
                .build();

    }

    public static final class Passive {
        private Passive() {}

        public static final PassiveSpell REFLECT = new PassiveSpell.Builder(1.5f, 200, FTZSoundEvents.EFFECT_REFLECT, null).build();
        public static final PassiveSpell DAMNED_WRATH = new PassiveSpell.Builder(0f, 600, FTZSoundEvents.DAMNED_WRATH, null).cleanse(Cleanse.MEDIUM).build();
        public static final PassiveSpell SHOCKWAVE = new PassiveSpell.Builder(0.8f, 0, null, null).build();

    }
}
