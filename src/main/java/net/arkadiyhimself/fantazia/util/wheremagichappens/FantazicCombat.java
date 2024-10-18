package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.BarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffect;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArrowEnchantmentsHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesGetter;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.EffectsOnSpawnHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.arkadiyhimself.fantazia.data.talent.TalentHelper;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.items.weapons.Range.HatchetItem;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.tags.FTZEntityTypeTags;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Optional;

public class FantazicCombat {
    private FantazicCombat() {}

    public static void dropExperience(LivingEntity entity, float multiplier, LivingEntity killer) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            int reward = (int) (entity.getExperienceReward(serverLevel, killer) * multiplier);
            ExperienceOrb.award(serverLevel, entity.position(), reward);
        }
    }

    public static boolean blocksDamage(LivingEntity entity) {
        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() > 1) return true;
        }

        BarrierEffect barrierEffect = LivingEffectGetter.takeHolder(entity, BarrierEffect.class);
        if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

        LayeredBarrierEffect layeredBarrierEffect = LivingEffectGetter.takeHolder(entity, LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

        AbsoluteBarrierEffect absoluteBarrierEffect = LivingEffectGetter.takeHolder(entity, AbsoluteBarrierEffect.class);
        if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;

        return false;
    }

    public static boolean isInvulnerable(LivingEntity entity) {
        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() >= 2) return true;
        }
        return entity.isInvulnerable() || entity.hurtTime > 0;
    }

    public static boolean isPhasing(LivingEntity entity) {
        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() >= 3) return true;
        }
        return false;
    }

    public static void meleeAttack(LivingDamageEvent.Pre event) {
        boolean meleeAttack = event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypes.MOB_ATTACK);
        boolean parry = event.getSource().is(FTZDamageTypes.PARRY);
        if (!meleeAttack && !parry || !(event.getSource().getEntity() instanceof LivingEntity livingAtt)) return;
        LivingEntity target = event.getEntity();

        float amount = event.getOriginalDamage();

        AttributeInstance lifeSteal = livingAtt.getAttribute(FTZAttributes.LIFESTEAL);
        double heal = lifeSteal == null ? 0 : lifeSteal.getValue() * amount;
        HealingSourcesHolder healingSources = LevelAttributesHelper.getHealingSources(livingAtt.level());
        if (heal > 0 && healingSources != null) AdvancedHealing.tryHeal(livingAtt, healingSources.lifesteal(target), (float) heal);

        Registry<Enchantment> enchantmentRegistry = target.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> bully = enchantmentRegistry.getHolder(FTZEnchantments.BULLY);
        float bullyDMG = bully.map(enchantmentReference -> livingAtt.getMainHandItem().getEnchantmentLevel(enchantmentReference) * 1.5f).orElse(0F);
        if (bullyDMG > 0) {
            LivingEffectHelper.microStun(target);
            StunEffect stunEffect = LivingEffectGetter.takeHolder(target, StunEffect.class);
            if (stunEffect != null && stunEffect.stunned()) event.setNewDamage(amount + bullyDMG);
        }

        if (livingAtt instanceof ServerPlayer player) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) itemStack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialHolder.DEFAULT, holder -> holder.onAttack(parry, target));

            if (TalentHelper.hasTalent(player, Fantazia.res("spider_powers/poison_attack"))) target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));
        }
    }

    public static boolean isFlying(LivingEntity livingEntity) {
        return livingEntity.getType().is(FTZEntityTypeTags.AERIAL) || livingEntity.isFallFlying() || !livingEntity.onGround();
    }

    public static boolean isRanged(LivingEntity livingEntity) {
        if (livingEntity.getType().is(FTZEntityTypeTags.RANGED_ATTACK)) return true;
        Item item = livingEntity.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        return item instanceof BowItem || item instanceof TridentItem || item instanceof CrossbowItem || item instanceof HatchetItem;
    }

    public static void arrowImpact(AbstractArrow arrow, LivingEntity entity) {
        ArrowEnchantmentsHolder arrowEnchantmentsHolder = arrow.getData(FTZAttachmentTypes.ARROW_ENCHANTMENTS);
        if (arrowEnchantmentsHolder.isFrozen()) LivingEffectHelper.makeFrozen(entity, 40);

        float damage = (float) arrow.getBaseDamage();
        int duel = arrowEnchantmentsHolder.getDuelist();
        if (duel > 0 && isRanged(entity)) arrow.setBaseDamage(damage + duel * 0.75f + 0.5f);
        int ball = arrowEnchantmentsHolder.getBallista();
        if (ball > 0 && isFlying(entity)) arrow.setBaseDamage(damage + ball * 0.75f + 0.5f);
    }

    public static boolean attemptEvasion(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        boolean flag1 = source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK);
        if (!flag1) return false;

        LivingEntity livingEntity = event.getEntity();
        EvasionHolder evasionHolder = LivingDataGetter.takeHolder(livingEntity, EvasionHolder.class);
        if (evasionHolder == null) return false;
        if (evasionHolder.getIFrames() > 0 || evasionHolder.tryEvade()) event.setCanceled(true);
        return event.isCanceled();
    }

    public static boolean attemptEvasion(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHitResult) || !(entityHitResult.getEntity() instanceof LivingEntity livingEntity)) return false;
        if (event.getProjectile() instanceof ThrownHatchet thrownHatchet && thrownHatchet.isPhasing()) return false;
        EvasionHolder evasionHolder = LivingDataGetter.takeHolder(livingEntity, EvasionHolder.class);
        if (evasionHolder == null) return false;
        if (evasionHolder.getIFrames() > 0 || evasionHolder.tryEvade()) event.setCanceled(true);
        return event.isCanceled();
    }

    public static void grantEffectsOnSpawn(LivingEntity livingEntity) {
        if (!(livingEntity.level() instanceof ServerLevel level)) return;
        LevelAttributesGetter.acceptConsumer(level, EffectsOnSpawnHolder.class, effectsOnSpawnHolder -> effectsOnSpawnHolder.tryApplyEffects(livingEntity));
    }
}
