package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.PuppeteeredEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.entities.magic_projectile.SimpleChasingProjectile;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.packets.attachment_modify.AllInPreviousOutcomeS2C;
import net.arkadiyhimself.fantazia.packets.attachment_modify.WanderersSpiritLocationS2C;
import net.arkadiyhimself.fantazia.particless.options.EntityChasingParticleOption;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class Spells {

    private Spells() {}

    public static final class Self {
        private Self() {}

        public static final SelfSpell ENTANGLE = new SelfSpell.Builder(0f, 50, FTZSoundEvents.ENTANGLE_CAST,null)
                .conditions(entity -> entity.getHealth() <= entity.getMaxHealth() * 0.15f)
                .onCast(entity -> LivingEffectHelper.giveBarrier(entity,10))
                .build();

        public static final SelfSpell REWIND = new SelfSpell.Builder(2f, 300, FTZSoundEvents.REWIND_CAST, FTZSoundEvents.REWIND_RECHARGE)
                .conditions(entity -> entity.getData(FTZAttachmentTypes.REWIND_PARAMETERS).writtenParameters())
                .onCast(entity -> {
                    if (entity.getData(FTZAttachmentTypes.REWIND_PARAMETERS).tryReadParameters(0, entity)) return;
                    EffectCleansing.tryCleanseAll(entity, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                    if (!(entity.level() instanceof ServerLevel)) return;
                    VisualHelper.particleOnEntityServer(entity, FTZParticleTypes.TIME_TRAVEL.get(), ParticleMovement.REGULAR, 12);
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

        public static final SelfSpell VANISH = new SelfSpell.Builder(2f, 800, FTZSoundEvents.VANISH_CAST, null)
                .conditions(owner -> !owner.isOnFire() && owner.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() <= 0)
                .recharge(livingEntity -> livingEntity.level().isNight() ? 400 : 800)
                .onCast(owner -> {
                    LivingEffectHelper.makeDisguised(owner, 300);

                    if (owner.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION, owner.getX(), owner.getY() + 1, owner.getZ(),4,0.7,0.35,0.7,0.5);
                        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), owner.getX(), owner.getY() + 1, owner.getZ(),35,1.5,0.75,1.5,0);
                        for (Entity entity : serverLevel.getAllEntities()) if (entity instanceof Mob mob && mob.getType() != EntityType.WARDEN && mob.getTarget() == owner) FantazicCombat.clearTarget(mob, owner);
                    }

                    EffectCleansing.forceCleanse(owner, MobEffects.GLOWING);
                })
                .build();

        public static final SelfSpell ALL_IN = new SelfSpell.Builder(1.5f, 300, FTZSoundEvents.ALL_IN_CAST, FTZSoundEvents.ALL_IN_RECHARGE)
                .recharge(livingEntity -> {
                    AttributeInstance luck = livingEntity.getAttribute(Attributes.LUCK);
                    if (luck == null) return 300; // default value

                    int luckLevel = (int) luck.getValue();

                    int recharge = 300 - luckLevel * 20;

                    return Mth.clamp(recharge, 100, 400);
                })
                .onCast(owner -> {
                    int prev = owner.getData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME);
                    int outcome;

                    if (prev == 0) outcome = Fantazia.RANDOM.nextInt(1,5);
                    else {
                        RandomList<Integer> outcomes = RandomList.emptyRandomList();
                        for (int i = 1; i < 5; i++) if (i != prev) outcomes.add(i);
                        Integer random = outcomes.random();
                        outcome = random == null ? 0 : random;
                    }

                    if (outcome == 1) SpellHelper.allIn1(owner);
                    else if (outcome == 2) SpellHelper.allIn2(owner);
                    else if (outcome == 3) SpellHelper.allIn3(owner);
                    else if (outcome == 4) SpellHelper.allIn4(owner);

                    owner.setData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME, outcome);
                    if (owner instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new AllInPreviousOutcomeS2C(outcome));
                })
                .extendTooltip(owner -> {
                    List<Component> components = Lists.newArrayList();

                    int prev = owner.getData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME);
                    if (prev == 0) {
                        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.RED};
                        components.add(GuiHelper.bakeComponent("spell.fantazia.all_in.extended.no_previous_outcome", text, null));
                    } else {
                        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.BLUE};
                        ChatFormatting[] pos = new ChatFormatting[]{ChatFormatting.DARK_BLUE};
                        components.add(GuiHelper.bakeComponent("spell.fantazia.all_in.extended.previous_outcome", text, pos, prev));
                    }

                    return components;
                })
                .build();

        public static final SelfSpell WANDERERS_SPIRIT = new SelfSpell.Builder(3f,600,null,null)
                .conditions(owner -> owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION).length() > 0 || owner.isShiftKeyDown())
                .recharge(owner -> owner.isShiftKeyDown() ? 0 : 600)
                .onCast(owner -> {
                    if (owner.isShiftKeyDown()) {
                        owner.setData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION, owner.position());
                        if (owner instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new WanderersSpiritLocationS2C(owner.position().toVector3f()));
                    } else {
                        Vec3 pos = owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
                        owner.teleportTo(pos.x, pos.y, pos.z);
                        owner.level().playSound(null, owner.blockPosition(), FTZSoundEvents.WANDERERS_SPIRIT_CAST.value(), SoundSource.PLAYERS);
                        VisualHelper.particleOnEntityServer(owner, ParticleTypes.PORTAL, ParticleMovement.REGULAR, 16);
                    }
                })
                .extendTooltip(owner -> {
                    List<Component> extended = Lists.newArrayList();
                    Vec3 location = owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
                    if (location == Vec3.ZERO) {
                        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.RED};
                        extended.add(GuiHelper.bakeComponent("spell.fantazia.wanderers_spirit.extended.unready", text, null));
                    } else {
                        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.BLUE};
                        ChatFormatting[] pos = new ChatFormatting[]{ChatFormatting.DARK_BLUE};
                        extended.add(GuiHelper.bakeComponent("spell.fantazia.wanderers_spirit.extended.ready", text, pos, (int) location.x, (int) location.y, (int) location.z));
                    }

                    return extended;
                })
                .build();
    }
    public static final class Targeted {

        private Targeted() {}

        public static final TargetedSpell<LivingEntity> SONIC_BOOM = new TargetedSpell.Builder<>(4.5f, 240, null, FTZSoundEvents.SONIC_BOOM_RECHARGE, LivingEntity.class,12f)
                .conditions((caster, target) -> !(target instanceof ArmorStand) && !target.isDeadOrDying())
                .beforeBlockChecking((caster, target) -> {
                    VisualHelper.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.SONIC_BOOM_CAST.value(), SoundSource.NEUTRAL);
                })
                .afterBlockChecking((caster, target) -> target.hurt(caster.level().damageSources().sonicBoom(caster), 15f))
                .build();

        public static final TargetedSpell<Mob> DEVOUR = new TargetedSpell.Builder<>(5f, 2000, FTZSoundEvents.DEVOUR_CAST, null, Mob.class, 8f)
                .conditions((caster, target) -> target.getMaxHealth() <= 100 && !target.isDeadOrDying())
                .afterBlockChecking((caster, target) -> {
                    float healing = target.getType().is(EntityTypeTags.INVERTED_HEALING_AND_HARM) ? target.getHealth() / 8 : target.getHealth() / 4;
                    LevelAttributesHelper.healEntityByOther(caster, target, healing, HealingSourcesHolder::devour);
                    LivingEffectHelper.effectWithoutParticles(caster, FTZMobEffects.BARRIER,  500, (int) target.getHealth() / 4 - 1);
                    LivingEffectHelper.effectWithoutParticles(caster, FTZMobEffects.MIGHT, 500, (int) target.getHealth() / 4 - 1);
                    FantazicCombat.dropExperience(target, 5, caster);

                    VisualHelper.particleOnEntityServer(target, ParticleTypes.SMOKE, ParticleMovement.REGULAR, 15);
                    VisualHelper.particleOnEntityServer(target, ParticleTypes.FLAME, ParticleMovement.REGULAR, 15);

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

        public static final TargetedSpell<LivingEntity> BOUNCE = new TargetedSpell.Builder<>(3.5f,160, FTZSoundEvents.BOUNCE_CAST, FTZSoundEvents.BOUNCE_RECHARGE, LivingEntity.class,16f)
                .conditions((caster, target) -> !FantazicCombat.isInvulnerable(target) && !target.isDeadOrDying())
                .beforeBlockChecking((caster, entity) -> {
                    VisualHelper.particleOnEntityServer(caster, ParticleTypes.PORTAL, ParticleMovement.REGULAR, 20);
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
                    if ((livingEntity.tickCount & 2) == 0) VisualHelper.particleOnEntityServer(livingEntity, ParticleTypes.PORTAL, ParticleMovement.REGULAR);
                })
                .cleanse(Cleanse.MEDIUM)
                .build();

        public static final TargetedSpell<LivingEntity> LIGHTNING_STRIKE = new TargetedSpell.Builder<>(5.5f, 400, null, FTZSoundEvents.LIGHTNING_STRIKE_RECHARGE, LivingEntity.class, 12f)
                .conditions((caster,target) -> target.level().canSeeSky(target.blockPosition()) && !target.isDeadOrDying())
                .afterBlockChecking((caster,entity) -> {
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(caster.level());
                    if (lightningBolt == null) return;
                    lightningBolt.moveTo(entity.position());
                    lightningBolt.setCause(caster instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    caster.level().addFreshEntity(lightningBolt);
                })
                .recharge(livingEntity -> livingEntity.level().isThundering() ? 240 : 400)
                .tickingConditions(AbstractSpell.TickingConditions.NOT_ON_COOLDOWN)
                .ownerTick(livingEntity -> {
                    if (livingEntity.tickCount % 3 == 0) VisualHelper.entityChasingParticle(livingEntity, FTZParticleTypes.ELECTRO.random(), 2, 0.65f);
                    if (livingEntity.tickCount % 16 == 0) livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.LIGHTNING_STRIKE_TICK.get(), SoundSource.PLAYERS, 0.115f,1.05f);
                })
                .build();

        public static final TargetedSpell<Monster> PUPPETEER = new TargetedSpell.Builder<>(6f,1200, FTZSoundEvents.PUPPETEER_CAST, null, Monster.class, 10f)
                .conditions((caster,target) -> target.isInvertedHealAndHarm() && !target.isDeadOrDying())
                .afterBlockChecking((caster,monster) -> {
                    PuppeteeredEffectHolder puppetHolder = LivingEffectHelper.takeHolder(monster, PuppeteeredEffectHolder.class);
                    if (puppetHolder == null) return;
                    puppetHolder.enslave(caster);

                    int dur = FantazicMath.toTicks(0, 8, 36);
                    LivingEffectHelper.puppeteer(monster, dur);
                    LivingEffectHelper.effectWithoutParticles(monster, FTZMobEffects.MIGHT, dur, 2);
                    FantazicCombat.clearTarget(monster, caster);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.PUPPETEER_CAST.value(), SoundSource.NEUTRAL);

                    PuppeteeredEffectHolder masterHolder = LivingEffectHelper.takeHolder(caster, PuppeteeredEffectHolder.class);
                    if (masterHolder != null) masterHolder.givePuppet(monster.getUUID());
                })
                .build();

        public static final TargetedSpell<LivingEntity> KNOCK_OUT = new TargetedSpell.Builder<>(4f,300, null, FTZSoundEvents.KNOCK_OUT_RECHARGE, LivingEntity.class, 12f)
                .conditions((caster, target) -> !target.isDeadOrDying())
                .beforeBlockChecking((owner, target) -> owner.level().playSound(null, owner.blockPosition(), FTZSoundEvents.KNOCK_OUT_CAST.value(), SoundSource.AMBIENT))
                .afterBlockChecking((caster,target) -> {
                    Level level = caster.level();
                    SimpleChasingProjectile projectile = new SimpleChasingProjectile(level, caster,"knock_out",100,6f / 20,12785985);
                    projectile.addParticle(ParticleTypes.CRIT);
                    projectile.setNeedsTarget(true);
                    projectile.setTarget(target);
                    projectile.setPos(caster.getEyePosition());
                    projectile.setDestroyedByCollision(true);
                    projectile.setCanBeDeflected(true);
                    projectile.setMeleeBlocked(true);
                    level.addFreshEntity(projectile);
                })
                .build();
    }

    public static final class Passive {

        private Passive() {}

        public static final PassiveSpell REFLECT = new PassiveSpell.Builder(1.5f,200, FTZSoundEvents.EFFECT_REFLECT, null)
                .onActivation(owner -> {
                    if (owner instanceof ServerPlayer serverPlayer) IPacket.mirrorReflect(serverPlayer);
                })
                .build();
        public static final PassiveSpell DAMNED_WRATH = new PassiveSpell.Builder(0f,600, FTZSoundEvents.DAMNED_WRATH, null)
                .onActivation(owner -> {
                    EffectCleansing.tryCleanseAll(owner, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                    LivingEffectHelper.makeFurious(owner,200);
                    LivingEffectHelper.giveBarrier(owner,20);
                    if (owner instanceof ServerPlayer serverPlayer) IPacket.soundForUI(serverPlayer, FTZSoundEvents.DAMNED_WRATH.value());
                })
                .cleanse(Cleanse.MEDIUM).build();
        public static final PassiveSpell SHOCKWAVE = new PassiveSpell.Builder(0.8f, 0, null, null).build();
        public static final PassiveSpell SUSTAIN = new PassiveSpell.Builder(0f, 0, null, null)
                .uponEquipping(owner -> owner.removeEffect(MobEffects.WITHER))
                .ownerTick(owner -> {
                    DamageSourcesHolder holder = LevelAttributesHelper.getDamageSources(owner.level());
                    if (holder == null || owner instanceof Player player && player.getAbilities().invulnerable) return;
                    owner.hurt(holder.removal(),0.125f / 20);
                    if ((owner.tickCount & 2) == 0) VisualHelper.entityChasingParticle(owner, FTZParticleTypes.WITHER.value(), 1,0.75f);
                }).cleanse().build();
        public static final PassiveSpell REINFORCE = new PassiveSpell.Builder(0.25f, 0, null, null)
                .onActivation(owner -> {
                    VisualHelper.particleOnEntityServer(owner, ParticleTypes.SMOKE, ParticleMovement.REGULAR, 10);
                    owner.level().playSound(null, owner.blockPosition(), FTZSoundEvents.REINFORCE_BLOCK.value(), SoundSource.AMBIENT);
                })
                .build();
    }
}
