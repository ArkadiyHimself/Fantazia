package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.CurrentAndInitialValue;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.ManaHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.StaminaHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.mob_effect.BarrierMobEffect;
import net.arkadiyhimself.fantazia.common.mob_effect.DisarmMobEffect;
import net.arkadiyhimself.fantazia.common.mob_effect.SimpleMobEffect;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiConsumer;

public class FTZMobEffects {

    public static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(Registries.MOB_EFFECT, Fantazia.MODID);

    private static final BiConsumer<LivingEntity, Integer> frozenTick = (livingEntity, integer) -> {
        DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(livingEntity.level());
        if (sources == null) return;
        if (livingEntity.fireImmune()) livingEntity.hurt(sources.frozen(), 2.25f + integer * 1.25f);
        else if (livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) livingEntity.hurt(sources.frozen(), 1f + integer);
    };

    private static final BiConsumer<LivingEntity, Integer> furyTick = (livingEntity, integer) -> {
        if (livingEntity != Minecraft.getInstance().player) return;
        int value = livingEntity.tickCount % 18;
        float volume = 1f;
        CurrentAndInitialValue holder = LivingEffectHelper.getDurationHolder(livingEntity, FTZMobEffects.FURY.value());
        if (holder != null) {
            int dur = holder.value();
            volume = dur == -1 ? 1f : (float) Math.min(dur, 20) / 20;
        }

        if (value == 0) FantazicUtil.playSoundUI(FTZSoundEvents.HEART_BEAT1.value(),1f, volume * 0.85f);
        else if (value == 9) FantazicUtil.playSoundUI(FTZSoundEvents.HEART_BEAT2.value(),1f, volume * 0.85f);
    };

    private static final BiConsumer<LivingEntity, Integer> mightTick = (livingEntity, integer) -> {
        if ((livingEntity.tickCount % 2) == 0) VisualHelper.particleOnEntityServer(livingEntity, ParticleTypes.SMALL_FLAME, ParticleMovement.REGULAR);
    };

    private static final BiConsumer<LivingEntity, Integer> doomedTick = (livingEntity, integer) -> {
        if (livingEntity.level().isClientSide()) return;
        if ((livingEntity.tickCount % 6) == 0) VisualHelper.entityChasingParticle(livingEntity, FTZParticleTypes.DOOMED_SOULS.random(), 1, 0.75f);
        if (livingEntity instanceof ServerPlayer serverPlayer && (livingEntity.tickCount % 100) == 0) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.WHISPER.get());
    };

    private static final BiConsumer<LivingEntity, Integer> recoveryTick = (livingEntity, integer) -> {
        if (!(livingEntity instanceof Player player)) return;
        PlayerAbilityHelper.acceptConsumer(player, StaminaHolder.class, staminaHolder -> staminaHolder.recover((float) integer * 0.05f));
    };

    private static final BiConsumer<LivingEntity, Integer> surgeTick = (livingEntity, integer) -> {
        if (!(livingEntity instanceof Player player)) return;
        PlayerAbilityHelper.acceptConsumer(player, ManaHolder.class, manaHolder -> manaHolder.regenerate((float) integer * 0.05f + 0.05f, false));
    };

    private static final BiConsumer<LivingEntity, Integer> electrocutedTick = (livingEntity, integer) -> {
        DamageSourcesHolder damageSourcesHolder = LevelAttributesHelper.getDamageSources(livingEntity.level());
        if (damageSourcesHolder != null && (livingEntity.tickCount % 10) == 0) livingEntity.hurt(damageSourcesHolder.electric(), 0.675f + integer * 0.125f);
        if ((livingEntity.tickCount % 3) == 0 && !livingEntity.level().isClientSide()) VisualHelper.entityChasingParticle(livingEntity, FTZParticleTypes.ELECTRO.random(), (int) Math.min(integer * 1.5 + 3, 3), 0.65f);
    };

    private static final BiConsumer<LivingEntity, Integer> puppeteeredTick = (livingEntity, integer) -> VisualHelper.particleOnEntityServer(livingEntity, ParticleTypes.SMOKE, ParticleMovement.FALL, 3);

    private static final BiConsumer<LivingEntity, Integer> chainedTick = (livingEntity, integer) -> VisualHelper.particleOnEntityServer(livingEntity, FTZParticleTypes.CHAINED.get(), ParticleMovement.REGULAR, 2);

    public static final DeferredHolder<MobEffect, SimpleMobEffect> ABSOLUTE_BARRIER = REGISTER.register("absolute_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 7995643,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.location("effect.absolute_barrier"), 0.5, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> ACE_IN_THE_HOLE = REGISTER.register("ace_in_the_hole", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16057348, true, false));
    public static final DeferredHolder<MobEffect, BarrierMobEffect> BARRIER = REGISTER.register("barrier", BarrierMobEffect::new); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> WITHERS_BARRIER = REGISTER.register("withers_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16447222, true));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> HAEMORRHAGE = REGISTER.register("haemorrhage", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 6553857, true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> FURY = REGISTER.register("fury", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 16057348, furyTick).addAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.location("effect.fury"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> STUN = REGISTER.register("stun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691, true).addAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.location("effect.stun"), -10, AttributeModifier.Operation.ADD_VALUE));// finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> LAYERED_BARRIER = REGISTER.register("layered_barrier", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 126,true).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.location("effect.layered_barrier"), 0.5, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DEAFENED = REGISTER.register("deafened", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 4693243, true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> FROZEN = REGISTER.register("frozen", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 8780799, frozenTick).addAttributeModifier(Attributes.MOVEMENT_SPEED, Fantazia.location("effect.frozen"), AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, i -> -0.25 - 0.1 * i).addAttributeModifier(Attributes.ATTACK_SPEED, Fantazia.location("effect.frozen"), -0.6, AttributeModifier.Operation.ADD_VALUE).addAttributeModifier(Attributes.BLOCK_BREAK_SPEED, Fantazia.location("effect.frozen"), -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> MIGHT = REGISTER.register("might", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16767061, mightTick).addAttributeModifier(Attributes.ATTACK_DAMAGE, Fantazia.location("effect.might"), 1, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DOOMED = REGISTER.register("doomed", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 0, doomedTick)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DISARM = REGISTER.register("disarm", DisarmMobEffect::new); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> REFLECT = REGISTER.register("reflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DEFLECT = REGISTER.register("deflect", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 8780799,true)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> MICROSTUN = REGISTER.register("microstun", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10179691,false, false)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> CORROSION = REGISTER.register("corrosion", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16057348,true).addAttributeModifier(Attributes.ARMOR, Fantazia.location("effect.corrosion"), -1, AttributeModifier.Operation.ADD_VALUE).addAttributeModifier(Attributes.ARMOR_TOUGHNESS, Fantazia.location("effect.corrosion"), -1, AttributeModifier.Operation.ADD_VALUE).addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, Fantazia.location("effect.corrosion"), -1, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> MANA_BOOST = REGISTER.register("mana_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true, false).addAttributeModifier(FTZAttributes.MAX_MANA, Fantazia.location("effect.mana_boost"), 4, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> STAMINA_BOOST = REGISTER.register("stamina_boost", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true, false).addAttributeModifier(FTZAttributes.MAX_STAMINA, Fantazia.location("effect.stamina_boost"), 4, AttributeModifier.Operation.ADD_VALUE));
    public static final DeferredHolder<MobEffect, SimpleMobEffect> CURSED_MARK = REGISTER.register("cursed_mark", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 0, true, false)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> RECOVERY = REGISTER.register("recovery", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL,52224, recoveryTick)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> SURGE = REGISTER.register("surge", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL,3785983, surgeTick)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> ELECTROCUTED = REGISTER.register("electrocuted", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 10079487, electrocutedTick)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> RAPID = REGISTER.register("rapid", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 16777011, true, false).addAttributeModifier(Attributes.ATTACK_SPEED, Fantazia.location("effect.rapid"), 1, AttributeModifier.Operation.ADD_VALUE)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> DISGUISED = REGISTER.register("disguised", () -> new SimpleMobEffect(MobEffectCategory.BENEFICIAL, 4693243, true, false)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> PUPPETEERED = REGISTER.register("puppeteered", () -> new SimpleMobEffect(MobEffectCategory.NEUTRAL, 16447222, puppeteeredTick, false)); // finished and implemented
    public static final DeferredHolder<MobEffect, SimpleMobEffect> CHAINED = REGISTER.register("chained", () -> new SimpleMobEffect(MobEffectCategory.HARMFUL, 16447222, chainedTick)); // finished and implemented

    public static void register(IEventBus eventBus) {
        REGISTER.register(eventBus);
    }

}
