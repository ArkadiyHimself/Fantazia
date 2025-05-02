package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.BarrierEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.LayeredBarrierEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.MobEffectDurationSyncHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.SimpleMobEffectHolderSyncHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingEffectHelper {

    private LivingEffectHelper() {}

    public static <T extends ILivingEffectHolder> @Nullable T takeHolder(LivingEntity livingEntity, Class<T> tClass) {
        return livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).actualHolder(tClass);
    }

    public static <T extends ILivingEffectHolder> void acceptConsumer(LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }

    public static void simpleSetEffect(LivingEntity entity, MobEffect mobEffect, boolean present) {
        acceptConsumer(entity, SimpleMobEffectHolderSyncHolder.class, holder -> holder.setEffect(mobEffect, present));
    }

    public static void simpleEffectAdded(LivingEntity entity, MobEffectInstance instance) {
        if (entity instanceof ServerPlayer serverPlayer && instance.getEffect().value() == FTZMobEffects.DOOMED.value()) IPacket.soundForUI(serverPlayer, FTZSoundEvents.DOOMED.get());

        if (instance.getEffect().value() == FTZMobEffects.HAEMORRHAGE.value()) {
            entity.level().playSound(null, entity.blockPosition(), FTZSoundEvents.EFFECT_HAEMORRHAGE_FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(entity.level());
            if (sources != null) entity.hurt(sources.bleeding(), entity.getHealth() * 0.1f);
            entity.setData(FTZAttachmentTypes.HAEMORRHAGE_TO_HEAL,4f + 2 * instance.getAmplifier());
        }
    }

    public static void simpleEffectRemoved(LivingEntity entity, MobEffect effect) {
        if (entity instanceof ServerPlayer serverPlayer && effect == FTZMobEffects.DOOMED.value()) IPacket.soundForUI(serverPlayer, FTZSoundEvents.UNDOOMED.get());
    }

    public static void simpleEffectOnHit(LivingIncomingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getAmount();
        Entity attacker = source.getEntity();

        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && livingEntity.hasEffect(FTZMobEffects.ABSOLUTE_BARRIER)) {
            event.setCanceled(true);
            return;
        }

        if (attacker instanceof LivingEntity livAtt && livAtt.hasEffect(FTZMobEffects.FURY)) {
            event.setAmount(event.getAmount() * 2);
            if (SpellHelper.spellAvailable(livAtt, FTZSpells.DAMNED_WRATH)) LevelAttributesHelper.healEntityByOther(livAtt, livingEntity,0.15f * event.getAmount(), HealingSourcesHolder::lifesteal);
        }

        if (livingEntity.hasEffect(FTZMobEffects.FURY)) {
            float multiplier = SpellHelper.spellAvailable(livingEntity, FTZSpells.DAMNED_WRATH) ? 1.5f : 2f;
            event.setAmount(event.getAmount() * multiplier);
        }
    }

    public static void simpleEffectOnHit(LivingDamageEvent.Pre event) {
        LivingEntity livingEntity = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();

        if (livingEntity.hasEffect(FTZMobEffects.DOOMED) && amount > 0 && !source.is(FTZDamageTypeTags.NON_LETHAL)) {
            event.setNewDamage(Float.MAX_VALUE);
            livingEntity.playSound(FTZSoundEvents.ENTITY_FALLEN_BREATH.get());
            double x = livingEntity.getX();
            double y = livingEntity.getY();
            double z = livingEntity.getZ();
            double height = livingEntity.getBbHeight();
            if (Minecraft.getInstance().level != null) Minecraft.getInstance().level.addParticle(FTZParticleTypes.FALLEN_SOUL.get(), x, y + height * 2 / 3, z, 0.0D, -0.135D, 0.0D);

            BlockPos blockPos = livingEntity.getOnPos();
            Block block = livingEntity.level().getBlockState(blockPos).getBlock();
            if (block == Blocks.DIRT || block == Blocks.SAND || block == Blocks.NETHERRACK || block == Blocks.GRASS_BLOCK) livingEntity.level().setBlockAndUpdate(blockPos, Blocks.SOUL_SAND.defaultBlockState());

        }
    }

    public static void simpleEffectOnHit(LivingDamageEvent.Post event) {
        LivingEntity livingEntity = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();

        if (source.is(DamageTypes.SONIC_BOOM) || event.getSource().is(DamageTypeTags.IS_EXPLOSION) && amount > 0) {
            LivingEffectHelper.makeDeaf(livingEntity, 200);
            LivingEffectHelper.microStun(livingEntity);
            if (livingEntity instanceof ServerPlayer serverPlayer) IPacket.soundForUI(serverPlayer, FTZSoundEvents.RINGING.get());
        }
    }

    public static boolean hasEffectSimple(LivingEntity livingEntity, MobEffect mobEffect) {
        SimpleMobEffectHolderSyncHolder holder = takeHolder(livingEntity, SimpleMobEffectHolderSyncHolder.class);
        return holder != null && holder.hasEffect(mobEffect);
    }

    public static boolean hasEffect(LivingEntity livingEntity, MobEffect mobEffect) {
        MobEffectDurationSyncHolder holder = takeHolder(livingEntity, MobEffectDurationSyncHolder.class);
        if (holder == null) return false;
        DurationHolder durationHolder = holder.getDuration(mobEffect);
        return durationHolder != null && durationHolder.dur() > 0;
    }

    public static @Nullable DurationHolder getDurationHolder(LivingEntity livingEntity, MobEffect mobEffect) {
        MobEffectDurationSyncHolder holder = takeHolder(livingEntity, MobEffectDurationSyncHolder.class);
        return holder == null ? null : holder.getDuration(mobEffect);
    }

    public static float bleedingDamage(LivingEntity entity, Vec3 vec3) {
        float movement = (float) vec3.horizontalDistance() / 500f;

        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() <= 1) return 7.5f;

            if (player.isSprinting()) return 1.5f * movement;
            else if (player.isCrouching()) return 0.0625f * movement;
        }
        return movement;
    }

    public static boolean hasBarrier(LivingEntity livingEntity) {
        BarrierEffectHolder barrierEffect = takeHolder(livingEntity, BarrierEffectHolder.class);
        if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

        LayeredBarrierEffectHolder layeredBarrierEffect = takeHolder(livingEntity, LayeredBarrierEffectHolder.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

        if (hasEffectSimple(livingEntity, FTZMobEffects.ABSOLUTE_BARRIER.value())) return true;

        return false;
    }

    public static boolean isDisguised(LivingEntity livingEntity) {
        return livingEntity.hasEffect(FTZMobEffects.DISGUISED);
    }

    public static boolean hurtRedColor(LivingEntity livingEntity) {
        if (hasBarrier(livingEntity)) return false;
        if (livingEntity.getLastDamageSource() != null && livingEntity.getLastDamageSource().is(FTZDamageTypeTags.NOT_TURNING_RED)) return false;
        return true;
    }

    public static void infiniteEffectWithoutParticles(LivingEntity entity, Holder<MobEffect> effect, int level) {
        effectWithoutParticles(entity, effect, -1, level);
    }

    public static void effectWithoutParticles(LivingEntity entity, Holder<MobEffect> effect, int duration, int level) {
        entity.addEffect(new MobEffectInstance(effect, duration, level, true, false, true));
    }

    public static void effectWithoutParticles(LivingEntity entity, Holder<MobEffect> effect, int duration) {
        effectWithoutParticles(entity, effect, duration, 0);
    }

    public static void puppeteer(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.PUPPETEERED, duration);
    }

    public static void makeFurious(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FURY , duration);
    }

    public static void makeStunned(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.STUN , duration);
    }

    public static void makeDeaf(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEAFENED , duration);
    }

    public static void makeDoomed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DOOMED , duration);
    }

    public static void makeDisarmed(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DISARM , duration);
    }

    public static void makeFrozen(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.FROZEN , duration);
    }

    public static void giveBarrier(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.ABSOLUTE_BARRIER , duration);
    }

    public static void giveReflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.REFLECT , duration);
    }

    public static void giveDeflect(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DEFLECT , duration);
    }

    public static void giveHaemorrhage(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.HAEMORRHAGE, duration);
    }

    public static void makeDisguised(LivingEntity entity, int duration) {
        effectWithoutParticles(entity, FTZMobEffects.DISGUISED, duration);
    }

    public static void microStun(LivingEntity entity) {
        effectWithoutParticles(entity, FTZMobEffects.MICROSTUN, 1);
    }
    public static void unDisguise(LivingEntity entity) {
        EffectCleansing.forceCleanse(entity, FTZMobEffects.DISGUISED);
    }
}
