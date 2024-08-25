package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.features.ArrowEnchant;
import net.arkadiyhimself.fantazia.api.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.api.capability.itemstack.StackDataManager;
import net.arkadiyhimself.fantazia.api.capability.itemstack.stackdata.HiddenPotential;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.tags.FTZEntityTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class FantazicCombat {
    public static void dropExperience(LivingEntity entity, float multiplier) {
        if (entity.level() instanceof ServerLevel) {
            int reward = (int) (entity.getExperienceReward() * multiplier);
            ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), reward);
        }
    }
    public static boolean blocksDamage(LivingEntity entity) {
        if (entity instanceof Player player) {
            Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
            if (dash != null && dash.isDashing() && dash.getLevel() > 1) return true;
        }

        BarrierEffect barrierEffect = EffectGetter.takeEffectHolder(entity, BarrierEffect.class);
        if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

        LayeredBarrierEffect layeredBarrierEffect = EffectGetter.takeEffectHolder(entity, LayeredBarrierEffect.class);
        if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

        AbsoluteBarrierEffect absoluteBarrierEffect = EffectGetter.takeEffectHolder(entity, AbsoluteBarrierEffect.class);
        if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;

        return false;
    }
    public static boolean isInvulnerable(LivingEntity entity) {
        if (entity instanceof Player player) {
            Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
            if (dash != null && dash.isDashing() && dash.getLevel() >= 2) return true;
        }
        return entity.isInvulnerable() || entity.hurtTime > 0;
    }
    public static boolean isPhasing(LivingEntity entity) {
        if (entity instanceof Player player) {
            Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
            if (dash != null && dash.isDashing() && dash.getLevel() >= 3) return true;
        }
        return false;
    }
    public static void meleeAttack(LivingHurtEvent event) {
        boolean meleeAttack = event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypes.MOB_ATTACK);
        boolean parry = event.getSource().is(FTZDamageTypes.PARRY);
        if (!meleeAttack && !parry || !(event.getSource().getEntity() instanceof LivingEntity livingAtt)) return;
        LivingEntity target = event.getEntity();

        float amount = event.getAmount();

        AttributeInstance lifeSteal = livingAtt.getAttribute(FTZAttributes.LIFESTEAL.get());
        double heal = lifeSteal == null ? 0 : lifeSteal.getValue() * event.getAmount();
        HealingSources healingSources = LevelCapHelper.getHealingSources(livingAtt.level());
        if (heal > 0 && healingSources != null) AdvancedHealing.heal(livingAtt, healingSources.lifesteal(target), (float) heal);

        float bullyDMG = livingAtt.getMainHandItem().getEnchantmentLevel(FTZEnchantments.BULLY.get()) * 1.5f;
        if (bullyDMG > 0) {
            EffectHelper.microStun(target);
            StunEffect stunEffect = EffectGetter.takeEffectHolder(target, StunEffect.class);
            if (stunEffect != null && stunEffect.stunned()) event.setAmount(amount + bullyDMG);
        }

        if (livingAtt instanceof ServerPlayer player) {
            ItemStack itemStack = player.getMainHandItem();
            StackDataManager stackDataManager = StackDataGetter.getUnwrap(itemStack);
            if (stackDataManager == null) return;
            HiddenPotential hiddenPotential = stackDataManager.takeData(HiddenPotential.class);
            if (hiddenPotential == null) return;
            float dmg = amount + hiddenPotential.onAttack(parry, target);
            event.setAmount(dmg);
        }
    }
    public static boolean isFlying(LivingEntity livingEntity) {
        return livingEntity.getType().is(FTZEntityTypeTags.AERIAL) || livingEntity.isFallFlying() || !livingEntity.onGround();
    }
    public static boolean isRanged(LivingEntity livingEntity) {
        if (livingEntity.getType().is(FTZEntityTypeTags.RANGED_ATTACK)) return true;
        Item item = livingEntity.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        return item instanceof BowItem || item instanceof TridentItem || item instanceof CrossbowItem;
    }
    public static void arrowImpact(AbstractArrow arrow, LivingEntity entity) {
        FeatureManager featureManager = FeatureGetter.getUnwrap(arrow);
        if (featureManager == null) return;
        ArrowEnchant arrowEnchant = featureManager.takeFeature(ArrowEnchant.class);
        if (arrowEnchant == null) return;
        if (arrowEnchant.isFrozen()) EffectHelper.makeFrozen(entity, 40);

        float damage = (float) arrow.getBaseDamage();
        int duel = arrowEnchant.getDuelist();
        if (duel > 0 && isRanged(entity)) arrow.setBaseDamage(damage + duel * 0.75f + 0.5f);
        int ball = arrowEnchant.getBallista();
        if (ball > 0 && isFlying(entity)) arrow.setBaseDamage(damage + ball * 0.75f + 0.5f);
    }
    public static boolean attemptEvasion(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        boolean flag1 = source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK);
        if (!flag1) return false;

        LivingEntity livingEntity = event.getEntity();
        EvasionData evasionData = DataGetter.takeDataHolder(livingEntity, EvasionData.class);
        if (evasionData == null) return false;
        if (evasionData.getIFrames() > 0) event.setCanceled(true);
        else if (evasionData.tryEvade()) event.setCanceled(true);
        return event.isCanceled();
    }
    public static boolean attemptEvasion(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHitResult) || !(entityHitResult.getEntity() instanceof LivingEntity livingEntity)) return false;
        if (event.getProjectile() instanceof ThrownHatchet thrownHatchet && thrownHatchet.isPhasing()) return false;
        EvasionData evasionData = DataGetter.takeDataHolder(livingEntity, EvasionData.class);
        if (evasionData == null) return false;
        if (evasionData.getIFrames() > 0) event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
        else if (evasionData.tryEvade()) event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
        return event.getImpactResult() == ProjectileImpactEvent.ImpactResult.SKIP_ENTITY;
    }
}
