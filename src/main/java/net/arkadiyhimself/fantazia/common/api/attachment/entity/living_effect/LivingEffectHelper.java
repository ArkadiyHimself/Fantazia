package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect;

import net.arkadiyhimself.fantazia.common.advanced.cleanse.EffectCleansing;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.MobEffectDurationSyncHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.PuppeteeredEffectHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.SimpleMobEffectSyncHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class LivingEffectHelper {

    public static <T extends ILivingEffectHolder> @Nullable T takeHolder(@Nullable LivingEntity livingEntity, Class<T> tClass) {
        return livingEntity == null ? null : livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).actualHolder(tClass);
    }

    public static <T extends ILivingEffectHolder> void acceptConsumer(@Nullable LivingEntity livingEntity, Class<T> tClass, Consumer<T> consumer) {
        if (livingEntity == null) return;
        livingEntity.getData(FTZAttachmentTypes.EFFECT_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }

    public static void simpleSetEffect(LivingEntity entity, MobEffect mobEffect, boolean present) {
        acceptConsumer(entity, SimpleMobEffectSyncHolder.class, holder -> holder.setEffect(mobEffect, present));
    }

    public static void simpleEffectAdded(LivingEntity livingEntity, MobEffectInstance instance, @Nullable Entity source) {
        MobEffect mobEffect = instance.getEffect().value();
        int ampl = instance.getAmplifier();
        int duration = instance.getDuration();
        if (livingEntity instanceof ServerPlayer serverPlayer && instance.getEffect().value() == FTZMobEffects.DOOMED.value()) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.DOOMED.get());

        if (mobEffect == FTZMobEffects.HAEMORRHAGE.value()) {
            livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.EFFECT_HAEMORRHAGE_FLESH_RIPPING.get(), SoundSource.NEUTRAL,0.35f,1f);
            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(livingEntity.level());
            if (sources != null) livingEntity.hurt(sources.bleeding(), livingEntity.getHealth() * 0.1f);
            livingEntity.setData(FTZAttachmentTypes.HAEMORRHAGE_TO_HEAL,4f + 2 * instance.getAmplifier());
        } else if (mobEffect == FTZMobEffects.LAYERED_BARRIER.value()) {
            int layers = livingEntity.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS);
            livingEntity.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, Math.max(layers, ampl + 1));
            if (!livingEntity.level().isClientSide()) IPacket.layeredBarrierChanged(livingEntity, Math.max(layers, ampl + 1));
        } else if (mobEffect == FTZMobEffects.BARRIER.value()) {
            float health = livingEntity.getData(FTZAttachmentTypes.BARRIER_HEALTH);
            livingEntity.setData(FTZAttachmentTypes.BARRIER_HEALTH, Math.max(health, ampl + 1f));
            IPacket.barrierChanged(livingEntity, Math.max(health, ampl + 1));
        } else if (mobEffect == FTZMobEffects.PUPPETEERED.value() && source instanceof LivingEntity livingSource) {
            LivingEffectHelper.acceptConsumer(livingEntity, PuppeteeredEffectHolder.class, holder -> holder.enslave(livingSource));
            LivingEffectHelper.acceptConsumer(livingSource, PuppeteeredEffectHolder.class, holder -> holder.givePuppet(livingEntity));
        } else if (mobEffect == FTZMobEffects.DEAFENED.value()) {
            if (livingEntity instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.RINGING.get());
        }
    }

    public static void simpleEffectRemoved(LivingEntity entity, MobEffect effect) {
        if (entity instanceof ServerPlayer serverPlayer && effect == FTZMobEffects.DOOMED.value()) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.UNDOOMED.get());

        if (effect == FTZMobEffects.LAYERED_BARRIER.value()) {
            entity.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, 0);
            if (!entity.level().isClientSide()) IPacket.layeredBarrierChanged(entity, 0);
        } else if (effect == FTZMobEffects.BARRIER.value()) {
            entity.setData(FTZAttachmentTypes.BARRIER_HEALTH, 0f);
            if (!entity.level().isClientSide()) IPacket.barrierChanged(entity, 0f);
        }
    }

    public static void simpleEffectOnHit(LivingIncomingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getAmount();
        Entity attacker = source.getEntity();
        Entity direct = source.getDirectEntity();
        boolean piercesBarrier = source.is(FTZDamageTypeTags.PIERCES_BARRIER);
        boolean furious = livingEntity.hasEffect(FTZMobEffects.FURY);

        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && livingEntity.hasEffect(FTZMobEffects.ABSOLUTE_BARRIER)) {
            event.setCanceled(true);
            return;
        }

        if ((direct instanceof AbstractArrow || direct instanceof WindCharge) && livingEntity.hasEffect(FTZMobEffects.WITHERS_BARRIER)) {
            event.setCanceled(true);
            return;
        }

        float barrierHealth = livingEntity.getData(FTZAttachmentTypes.BARRIER_HEALTH);
        if (barrierHealth > 0 && !piercesBarrier) {
            if (livingEntity.invulnerableTime > 10f && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                event.setCanceled(true);
                return;
            }

            // damage still gives i-frames and depends on i-frames
            livingEntity.invulnerableTime = event.getContainer().getPostAttackInvulnerabilityTicks();
            IPacket.barrierDamaged(livingEntity, amount);
            float newHP = barrierHealth - amount;
            if (newHP > 0) {
                event.setCanceled(true);
                livingEntity.setData(FTZAttachmentTypes.BARRIER_HEALTH, newHP);
                livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.EFFECT_BARRIER_DAMAGE.get(), SoundSource.AMBIENT);
                int num = (int) Math.min(amount * 3f, 25);
                VisualHelper.particleOnEntityServer(livingEntity, furious ? FTZParticleTypes.PIECES_FURY.random() : FTZParticleTypes.PIECES.random(), ParticleMovement.FALL, num);
                return;
            } else {
                event.setAmount(-newHP);
                livingEntity.removeEffect(FTZMobEffects.BARRIER);
                livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.EFFECT_BARRIER_BREAK.get(), SoundSource.AMBIENT);
                if (event.getEntity().hasEffect(FTZMobEffects.BARRIER)) EffectCleansing.forceCleanse(event.getEntity(), FTZMobEffects.BARRIER);

                VisualHelper.particleOnEntityServer(livingEntity, furious ? FTZParticleTypes.PIECES_FURY.random() : FTZParticleTypes.PIECES.random(), ParticleMovement.FALL, 30);
            }
        }

        if (attacker instanceof LivingEntity livAtt && livAtt.hasEffect(FTZMobEffects.FURY)) {
            event.setAmount(event.getAmount() * 2);
            if (SpellHelper.spellAvailable(livAtt, Spells.DAMNED_WRATH)) LevelAttributesHelper.healEntityByOther(livAtt, livingEntity,0.15f * event.getAmount(), HealingSourcesHolder::lifesteal);
        }

        if (furious) {
            float multiplier = SpellHelper.spellAvailable(livingEntity, Spells.DAMNED_WRATH) ? 1.5f : 2f;
            event.setAmount(event.getAmount() * multiplier);
        }

        int layers = livingEntity.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS);
        if (layers > 0 && !piercesBarrier) {
            event.setCanceled(true);
            if (livingEntity.invulnerableTime > 10f && !source.is(DamageTypeTags.BYPASSES_COOLDOWN)) {
                return;
            }

            // damage still gives i-frames and depends on i-frames
            livingEntity.invulnerableTime = event.getContainer().getPostAttackInvulnerabilityTicks();

            layers--;
            if (layers <= 0) {
                EffectCleansing.forceCleanse(livingEntity, FTZMobEffects.LAYERED_BARRIER);
                livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.EFFECT_LAYERED_BARRIER_BREAK.get(), SoundSource.AMBIENT);
            } else livingEntity.level().playSound(null, livingEntity.blockPosition(), FTZSoundEvents.EFFECT_LAYERED_BARRIER_DAMAGE.get(), SoundSource.AMBIENT);
            livingEntity.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, layers);
            IPacket.layeredBarrierDamaged(livingEntity);
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

            Entity entity = source.getEntity();
            if (entity instanceof Player player) PlayerAbilityHelper.reduceRecharge(player, Spells.RING_OF_DOOM,40);
        }
    }

    public static void simpleEffectOnHit(LivingDamageEvent.Post event) {}

    public static boolean hasEffectSimple(LivingEntity livingEntity, MobEffect mobEffect) {
        SimpleMobEffectSyncHolder holder = takeHolder(livingEntity, SimpleMobEffectSyncHolder.class);
        return holder != null && holder.hasEffect(mobEffect);
    }



    public static boolean hasEffect(LivingEntity livingEntity, MobEffect mobEffect) {
        MobEffectDurationSyncHolder holder = takeHolder(livingEntity, MobEffectDurationSyncHolder.class);
        if (holder == null) return false;
        CurrentAndInitialValue currentAndInitialValue = holder.getDuration(mobEffect);
        return currentAndInitialValue != null && currentAndInitialValue.value() > 0;
    }

    public static @Nullable CurrentAndInitialValue getDurationHolder(LivingEntity livingEntity, MobEffect mobEffect) {
        MobEffectDurationSyncHolder holder = takeHolder(livingEntity, MobEffectDurationSyncHolder.class);
        return holder == null ? null : holder.getDuration(mobEffect);
    }

    public static float bleedingDamage(LivingEntity entity, Vec3 vec3) {
        float movement = (float) vec3.horizontalDistance() / 1.65f;

        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() <= 1) return 7.5f;

            if (player.isSprinting()) return 1.5f * movement;
            else if (player.isCrouching()) return 0.0625f * movement;
        }
        return movement;
    }

    public static boolean hasBarrier(LivingEntity livingEntity) {
        if (livingEntity.getData(FTZAttachmentTypes.BARRIER_HEALTH) > 0) return true;
        if (livingEntity.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS) > 0) return true;
        if (hasEffectSimple(livingEntity, FTZMobEffects.ABSOLUTE_BARRIER.value())) return true;
        return false;
    }

    public static void healStunPoints(LivingEntity livingEntity, int amount, boolean ignoreDelay) {
        acceptConsumer(livingEntity, StunEffectHolder.class, stunEffectHolder -> stunEffectHolder.healPoints(amount, ignoreDelay));
    }

    public static boolean hurtRedColor(LivingEntity livingEntity) {
        if (hasBarrier(livingEntity)) return false;
        if (livingEntity.getLastDamageSource() != null && livingEntity.getLastDamageSource().is(FTZDamageTypeTags.NOT_TURNING_RED)) return false;
        return true;
    }

    public static boolean hasStunPoints(LivingEntity livingEntity) {
        StunEffectHolder holder = takeHolder(livingEntity, StunEffectHolder.class);
        return holder != null && holder.getPoints() > 0;
    }

    public static boolean isStunned(LivingEntity livingEntity) {
        StunEffectHolder holder = takeHolder(livingEntity, StunEffectHolder.class);
        return holder != null && holder.duration() > 0;
    }
}
