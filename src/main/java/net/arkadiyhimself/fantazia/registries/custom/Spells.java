package net.arkadiyhimself.fantazia.registries.custom;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellCastResult;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.advanced.spell.types.*;
import net.arkadiyhimself.fantazia.api.attachment.basis_attachments.LocationHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.api.custom_registry.DeferredSpell;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.client.gui.GuiHelper;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.entities.magic_projectile.SimpleChasingProjectile;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
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
import net.minecraft.world.effect.MobEffectInstance;
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
import net.neoforged.bus.api.IEventBus;
import org.apache.commons.compress.utils.Lists;

import java.util.EnumSet;
import java.util.List;

public class Spells {

    private static final FantazicRegistries.Spells REGISTER = FantazicRegistries.createSpells(Fantazia.MODID);

    // self
    public static final DeferredSpell<SelfSpell> ENTANGLE;
    public static final DeferredSpell<SelfSpell> REWIND;
    public static final DeferredSpell<SelfSpell> TRANSFER;
    public static final DeferredSpell<SelfSpell> VANISH;
    public static final DeferredSpell<SelfSpell> ALL_IN;
    public static final DeferredSpell<SelfSpell> WANDERERS_SPIRIT;
    // targeted
    public static final DeferredSpell<TargetedSpell<Mob>> DEVOUR;
    public static final DeferredSpell<TargetedSpell<LivingEntity>> SONIC_BOOM;
    public static final DeferredSpell<TargetedSpell<LivingEntity>> BOUNCE;
    public static final DeferredSpell<TargetedSpell<LivingEntity>> LIGHTNING_STRIKE;
    public static final DeferredSpell<TargetedSpell<Monster>> PUPPETEER;
    public static final DeferredSpell<TargetedSpell<LivingEntity>> KNOCK_OUT;
    // passive
    public static final DeferredSpell<PassiveSpell> REFLECT;
    public static final DeferredSpell<PassiveSpell> DAMNED_WRATH;
    public static final DeferredSpell<PassiveSpell> SHOCKWAVE;
    public static final DeferredSpell<PassiveSpell> SUSTAIN;
    public static final DeferredSpell<PassiveSpell> REINFORCE;

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

    private static <T extends AbstractSpell> DeferredSpell<T> register(String id, SpellBuilder<T> builder) {
        return REGISTER.register(id, builder::build);
    }

    static {
        // self
        ENTANGLE = register("entangle", SelfSpell.builder(0f, 50, FTZSoundEvents.ENTANGLE_CAST,null)
                .conditions(entity -> entity.getHealth() <= entity.getMaxHealth() * 0.15f)
                .onCast(entity -> {
                    ApplyEffect.giveAbsoluteBarrier(entity, 10);
                    return SpellCastResult.defaultResult();
                }));

        REWIND = register("rewind", SelfSpell.builder(2f, 300, FTZSoundEvents.REWIND_CAST, FTZSoundEvents.REWIND_RECHARGE)
                .conditions(entity -> entity.getData(FTZAttachmentTypes.REWIND_PARAMETERS).writtenParameters())
                .onCast(entity -> {
                    if (entity.getData(FTZAttachmentTypes.REWIND_PARAMETERS).tryReadParameters(0, entity)) return SpellCastResult.free();
                    EffectCleansing.tryCleanseAll(entity, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                    if (!entity.level().isClientSide()) VisualHelper.particleOnEntityServer(entity, FTZParticleTypes.TIME_TRAVEL.get(), ParticleMovement.REGULAR, 12);
                    return SpellCastResult.defaultResult();
                }).cleanse());

        TRANSFER = register("transfer", SelfSpell.builder(2.5f,600, FTZSoundEvents.EFFECT_HAEMORRHAGE_FLESH_RIPPING,null)
                .conditions(owner -> !(owner instanceof Player player) || !player.getAbilities().invulnerable)
                .cleanse(Cleanse.MEDIUM)
                .onCast((entity, ampl) -> {
                    DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(entity.level());
                    if (damageSourcesHolder == null) return SpellCastResult.free();

                    float damage = Math.min(entity.getHealth() - 0.01f, 4);
                    int sacrifice = (int) Math.max(damage, 1) + ampl;

                    entity.addEffect(new MobEffectInstance(FTZMobEffects.MIGHT, 200, sacrifice));
                    entity.addEffect(new MobEffectInstance(FTZMobEffects.RAPID, 200, sacrifice));

                    entity.hurt(damageSourcesHolder.removal(), sacrifice);
                    return SpellCastResult.defaultResult();
                }));

        VANISH = register("vanish", SelfSpell.builder(2f, 800, FTZSoundEvents.VANISH_CAST, null)
                .conditions(owner -> !owner.isOnFire() && owner.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() <= 0)
                .recharge(livingEntity -> livingEntity.level().isNight() ? 400 : 800)
                .onCast(owner -> {
                    ApplyEffect.makeDisguised(owner, 300);

                    if (owner.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION, owner.getX(), owner.getY() + 1, owner.getZ(),4,0.7,0.35,0.7,0.5);
                        serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.SAND.defaultBlockState()), owner.getX(), owner.getY() + 1, owner.getZ(),35,1.5,0.75,1.5,0);
                        for (Entity entity : serverLevel.getAllEntities()) if (entity instanceof Mob mob && mob.getType() != EntityType.WARDEN && mob.getTarget() == owner) FantazicCombat.clearTarget(mob, owner);
                    }

                    EffectCleansing.forceCleanse(owner, MobEffects.GLOWING);
                    return SpellCastResult.defaultResult();
                }));

        ALL_IN = register("all_in", SelfSpell.builder(1.5f, 300, FTZSoundEvents.ALL_IN_CAST, FTZSoundEvents.ALL_IN_RECHARGE)
                .recharge(livingEntity -> {
                    AttributeInstance luck = livingEntity.getAttribute(Attributes.LUCK);
                    if (luck == null) return 300; // default value
                    int luckLevel = (int) luck.getValue();
                    int recharge = 300 - luckLevel * 20;
                    return Mth.clamp(recharge, 100, 400);
                })
                .onCast((owner, ampl) -> {
                    int prev = owner.getData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME);
                    int outcome;

                    if (prev == 0) outcome = RandomUtil.nextInt(1,5);
                    else {
                        RandomList<Integer> outcomes = RandomList.emptyRandomList();
                        for (int i = 1; i < 5; i++) if (i != prev) outcomes.add(i);
                        Integer random = outcomes.random();
                        outcome = random == null ? 0 : random;
                    }

                    SpellCastResult result = SpellCastResult.defaultResult();
                    if (outcome == 1) result = SpellHelper.allIn1(owner, ampl);
                    else if (outcome == 2) result = SpellHelper.allIn2(owner, ampl);
                    else if (outcome == 3) result = SpellHelper.allIn3(owner, ampl);
                    else if (outcome == 4) result = SpellHelper.allIn4(owner, ampl);

                    owner.setData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME, outcome);
                    if (owner instanceof ServerPlayer serverPlayer) IPacket.allInPreviousOutcome(serverPlayer);
                    return result;
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
                }));

        WANDERERS_SPIRIT = register("wanderers_spirit", SelfSpell.builder(3f,600,null,null)
                .conditions(owner -> !owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION).empty() || owner.isShiftKeyDown())
                .onCast(owner -> {
                    if (!(owner.level() instanceof ServerLevel serverLevel)) return SpellCastResult.free();
                    if (owner.isShiftKeyDown()) {
                        LocationHolder holder = owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
                        holder.setLocation(owner.position(), owner.level().dimension());
                        if (owner instanceof ServerPlayer serverPlayer) IPacket.wanderersSpiritLocation(serverPlayer, true);
                        return SpellCastResult.free();
                    } else {
                        LocationHolder locationHolder = owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
                        ServerLevel newLevel = serverLevel.getServer().getLevel(locationHolder.dimension());
                        Vec3 vec3 = locationHolder.position();
                        if (newLevel != null) owner.teleportTo(newLevel, vec3.x, vec3.y, vec3.z, EnumSet.noneOf(RelativeMovement.class), owner.getYRot(), owner.getXRot());

                        owner.level().playSound(null, owner.blockPosition(), FTZSoundEvents.WANDERERS_SPIRIT_CAST.value(), SoundSource.PLAYERS);
                        VisualHelper.particleOnEntityServer(owner, ParticleTypes.PORTAL, ParticleMovement.REGULAR, 16);
                        return SpellCastResult.defaultResult();
                    }
                })
                .extendTooltip(owner -> {
                    List<Component> extended = Lists.newArrayList();
                    LocationHolder holder = owner.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);

                    if (holder.empty()) {
                        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.RED};
                        extended.add(GuiHelper.bakeComponent("spell.fantazia.wanderers_spirit.extended.unready", text, null));
                    } else {
                        Vec3 position = holder.position();
                        Component level = GuiHelper.bakeLevelComponent(holder.dimension());

                        ChatFormatting[] text = new ChatFormatting[]{ChatFormatting.BLUE};
                        ChatFormatting[] pos = new ChatFormatting[]{ChatFormatting.DARK_BLUE};
                        extended.add(GuiHelper.bakeComponent("spell.fantazia.wanderers_spirit.extended.ready1", text, pos, (int) position.x, (int) position.y, (int) position.z));
                        extended.add(GuiHelper.bakeComponent("spell.fantazia.wanderers_spirit.extended.ready2", text, pos, level));
                    }

                    return extended;
                }));

        // targeted
        DEVOUR = register("devour", TargetedSpell.builder(5f, 2000, FTZSoundEvents.DEVOUR_CAST, null, Mob.class, 8f)
                .conditions((caster, target) -> target.getMaxHealth() <= 100 && !target.isDeadOrDying())
                .afterBlockChecking((caster, target, ampl) -> {
                    float part = (0.25f + 0.075f * ampl) * target.getHealth();
                    float healing = part;
                    if (target.getType().is(EntityTypeTags.INVERTED_HEALING_AND_HARM)) healing /= 2;
                    LevelAttributesHelper.healEntityByOther(caster, target, healing, HealingSourcesHolder::devour);
                    caster.addEffect(new MobEffectInstance(FTZMobEffects.BARRIER, 500, (int) part - 1));
                    caster.addEffect(new MobEffectInstance(FTZMobEffects.MIGHT, 500, (int) part - 1));
                    if (caster.getMainHandItem().is(FTZItems.WISDOM_CATCHER)) {
                        if (caster instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.awardWisdom(serverPlayer, RandomUtil.nextInt(4, 7 + ampl));
                    } else FantazicCombat.dropExperience(target, 5 + ampl, caster);

                    VisualHelper.particleOnEntityServer(target, ParticleTypes.SMOKE, ParticleMovement.REGULAR, 15);
                    VisualHelper.particleOnEntityServer(target, ParticleTypes.FLAME, ParticleMovement.REGULAR, 15);

                    target.playSound(FTZSoundEvents.DEVOUR_CAST.get());
                    target.remove(Entity.RemovalReason.KILLED);

                    if (!(caster instanceof ServerPlayer player)) return;

                    int devour = (int) (target.getHealth() * part);
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
                    player.addEffect(new MobEffectInstance(MobEffects.HUNGER,2));
                    player.causeFoodExhaustion(player.getFoodData().getSaturationLevel() > 0 ? 0.1f : 0.01f);
                }));

        SONIC_BOOM = register("sonic_boom", TargetedSpell.builder(3.5f, 240, null, FTZSoundEvents.SONIC_BOOM_RECHARGE, LivingEntity.class,12f)
                .conditions((caster, target) -> !(target instanceof ArmorStand) && !target.isDeadOrDying())
                .beforeBlockChecking((caster, target) -> {
                    VisualHelper.rayOfParticles(caster, target, ParticleTypes.SONIC_BOOM);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.SONIC_BOOM_CAST.value(), SoundSource.NEUTRAL);
                    return SpellCastResult.defaultResult();
                })
                .afterBlockChecking((caster, target, ampl) -> target.hurt(caster.level().damageSources().sonicBoom(caster), 13f + ampl * 2)));

        BOUNCE = register("bounce", TargetedSpell.builder(3.5f,160, FTZSoundEvents.BOUNCE_CAST, FTZSoundEvents.BOUNCE_RECHARGE, LivingEntity.class,16f)
                .conditions((caster, target) -> !FantazicCombat.isInvulnerable(target) && !target.isDeadOrDying())
                .beforeBlockChecking((caster, entity) -> {
                    VisualHelper.particleOnEntityServer(caster, ParticleTypes.PORTAL, ParticleMovement.REGULAR, 20);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.BOUNCE_CAST.get(), SoundSource.NEUTRAL);
                    return SpellCastResult.defaultResult();
                })
                .afterBlockChecking((caster, entity, ampl) -> {
                    Vec3 delta1 = entity.position().subtract(caster.position());
                    Vec3 normal = delta1.normalize();
                    Vec3 delta2 = delta1.subtract(normal.scale(0.5));
                    Vec3 finalPos = caster.position().add(delta2);
                    caster.teleportTo(finalPos.x(), entity.getY(), finalPos.z());
                    ApplyEffect.microStun(entity);
                    ApplyEffect.makeDisarmed(entity, 50 + ampl * 25);
                })
                .tickingConditions(AbstractSpell.TickingConditions.NOT_ON_COOLDOWN)
                .ownerTick(livingEntity -> {
                    if ((livingEntity.tickCount & 2) == 0) VisualHelper.particleOnEntityServer(livingEntity, ParticleTypes.PORTAL, ParticleMovement.REGULAR);
                })
                .cleanse(Cleanse.MEDIUM));

        LIGHTNING_STRIKE = register("lightning_strike", TargetedSpell.builder(6f,480, null, FTZSoundEvents.LIGHTNING_STRIKE_RECHARGE, LivingEntity.class, 12f)
                .conditions((caster,target) -> target.level().canSeeSky(target.blockPosition()) && !target.isDeadOrDying())
                .afterBlockChecking((caster,entity) -> {
                    LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(caster.level());
                    if (lightningBolt == null) return;
                    lightningBolt.moveTo(entity.position());
                    lightningBolt.setCause(caster instanceof ServerPlayer serverPlayer ? serverPlayer : null);
                    caster.level().addFreshEntity(lightningBolt);
                })
                .recharge(livingEntity -> livingEntity.level().isThundering() ? 240 : 480)
                .tickingConditions(AbstractSpell.TickingConditions.NOT_ON_COOLDOWN)
                .ownerTick(livingEntity -> {
                    if (livingEntity.tickCount % 3 == 0) VisualHelper.entityChasingParticle(livingEntity, FTZParticleTypes.ELECTRO.random(), 2, 0.65f);
                    if (livingEntity.tickCount % 16 == 0) livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.LIGHTNING_STRIKE_TICK.get(), SoundSource.PLAYERS, 0.115f,1.05f);
                }));

        PUPPETEER = register("puppeteer", TargetedSpell.builder(6f,1200, FTZSoundEvents.PUPPETEER_CAST, null, Monster.class, 10f)
                .conditions((caster,target) -> target.isInvertedHealAndHarm() && !target.isDeadOrDying())
                .afterBlockChecking((caster,monster, ampl) -> {
                    int dur = FantazicMath.toTicks(0, 8, 36);
                    ApplyEffect.makePuppeteered(caster, monster, dur);
                    monster.addEffect(new MobEffectInstance(FTZMobEffects.MIGHT, dur, 1 + ampl));
                    FantazicCombat.clearTarget(monster, caster);
                    caster.level().playSound(null, caster.blockPosition(), FTZSoundEvents.PUPPETEER_CAST.value(), SoundSource.NEUTRAL);
                }));

        KNOCK_OUT = register("knock_out", TargetedSpell.builder(4f,300, null, FTZSoundEvents.KNOCK_OUT_RECHARGE, LivingEntity.class, 12f)
                .conditions((caster, target) -> !target.isDeadOrDying())
                .beforeBlockChecking((owner, target) -> {
                    owner.level().playSound(null, owner.blockPosition(), FTZSoundEvents.KNOCK_OUT_CAST.value(), SoundSource.AMBIENT);
                    return SpellCastResult.defaultResult();
                })
                .afterBlockChecking((caster,target, ampl) -> {
                    Level level = caster.level();
                    SimpleChasingProjectile projectile = new SimpleChasingProjectile(level, caster,"knock_out",100,(6f + ampl) / 20,12785985);
                    projectile.addParticle(ParticleTypes.CRIT);
                    projectile.setNeedsTarget(true);
                    projectile.setTarget(target);
                    projectile.setPos(caster.getEyePosition());
                    projectile.setDestroyedByCollision(true);
                    projectile.setCanBeDeflected(true);
                    projectile.setMeleeBlocked(true);
                    level.addFreshEntity(projectile);
                }));

        // passive
        REFLECT = register("reflect", PassiveSpell.builder(1.5f,200, FTZSoundEvents.EFFECT_REFLECT, null)
                .onActivation(owner -> {
                    if (owner instanceof ServerPlayer serverPlayer) IPacket.reflectActivate(serverPlayer);
                    return SpellCastResult.defaultResult();
                }));

        DAMNED_WRATH = register("damned_wrath", PassiveSpell.builder(0f,600, FTZSoundEvents.DAMNED_WRATH, null)
                .onActivation((owner, ampl) -> {
                    EffectCleansing.tryCleanseAll(owner, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
                    ApplyEffect.makeFurious(owner,200);
                    ApplyEffect.giveAbsoluteBarrier(owner,20 + ampl * 5);
                    if (owner instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DAMNED_WRATH.value());
                    return SpellCastResult.defaultResult();
                }).cleanse(Cleanse.MEDIUM));

        SHOCKWAVE = register("shockwave", PassiveSpell.builder(0.8f, 0, null, null));

        SUSTAIN = register("sustain", PassiveSpell.builder(0f, 0, null, null)
                .uponEquipping(owner -> owner.removeEffect(MobEffects.WITHER))
                .ownerTick(owner -> {
                    DamageSourcesHolder holder = LevelAttributesHelper.getDamageSources(owner.level());
                    if (holder == null || owner instanceof Player player && player.getAbilities().invulnerable) return;
                    owner.hurt(holder.removal(),0.125f / 20);
                    if ((owner.tickCount & 2) == 0) VisualHelper.entityChasingParticle(owner, FTZParticleTypes.WITHER.value(), 1,0.75f);
                }).cleanse());

        REINFORCE = register("reinforce", PassiveSpell.builder(0.25f, 0, null, null)
                .onActivation(owner -> {
                    VisualHelper.particleOnEntityServer(owner, ParticleTypes.SMOKE, ParticleMovement.REGULAR, 10);
                    owner.level().playSound(null, owner.blockPosition(), FTZSoundEvents.REINFORCE_BLOCK.value(), SoundSource.AMBIENT);
                    return SpellCastResult.defaultResult();
                }));
    }
}
