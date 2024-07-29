package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.features.ArrowEnchant;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataManager;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.stackdata.HiddenPotential;
import net.arkadiyhimself.fantazia.advanced.healing.AdvancedHealing;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSource;
import net.arkadiyhimself.fantazia.advanced.healing.HealingTypes;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.ArrayList;

public class CombatHelper {
    public static ArrayList<Class<? extends LivingEntity>> FLYING = new ArrayList<>(){{
        add(Allay.class);
        add(Bat.class);
        add(Bee.class);
        add(Blaze.class);
        add(EnderDragon.class);
        add(Ghast.class);
        add(Parrot.class);
        add(Phantom.class);
        add(Vex.class);
        add(WitherBoss.class);
    }};
    public static ArrayList<Class<? extends LivingEntity>> RANGED = new ArrayList<>(){{
        add(Blaze.class);
        add(EnderDragon.class);
        add(Ghast.class);
        add(WitherBoss.class);
        add(Llama.class);
        add(SnowGolem.class);
        add(Witch.class);
    }};
    public static void dropExperience(LivingEntity entity, float multiplier) {
        if (entity.level() instanceof ServerLevel) {
            int reward = (int) (entity.getExperienceReward() * multiplier);
            ExperienceOrb.award((ServerLevel) entity.level(), entity.position(), reward);
        }
    }
    public static boolean blocksDamage(LivingEntity entity) {
        if (entity instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                Dash dash = abilityManager.takeAbility(Dash.class);
                if (dash != null && dash.isDashing() && dash.getLevel() > 1) return true;
            }
        }
        EffectManager effectManager = EffectGetter.getUnwrap(entity);
        if (effectManager != null) {
            BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
            if (barrierEffect != null && barrierEffect.hasBarrier()) return true;

            LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
            if (layeredBarrierEffect != null && layeredBarrierEffect.hasBarrier()) return true;

            AbsoluteBarrierEffect absoluteBarrierEffect = effectManager.takeEffect(AbsoluteBarrierEffect.class);
            if (absoluteBarrierEffect != null && absoluteBarrierEffect.hasBarrier()) return true;
        }
        return false;
    }
    public static boolean isInvulnerable(LivingEntity entity) {
        if (entity instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                Dash dash = abilityManager.takeAbility(Dash.class);
                if (dash != null && dash.isDashing() && dash.getLevel() >= 2) return true;
            }
        }
        return entity.isInvulnerable() || entity.hurtTime > 0;
    }
    public static boolean isPhasing(LivingEntity entity) {
        if (entity instanceof Player player) {
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager != null) {
                Dash dash = abilityManager.takeAbility(Dash.class);
                if (dash != null && dash.isDashing() && dash.getLevel() >= 3) return true;
            }
        }
        return false;
    }
    public static void meleeAttack(LivingHurtEvent event) {
        boolean meleeAttack = event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypes.MOB_ATTACK);
        boolean parry = event.getSource().is(FTZDamageTypes.PARRY);
        if (!meleeAttack && !parry || !(event.getSource().getEntity() instanceof LivingEntity livingAtt)) return;
        LivingEntity target = event.getEntity();
        float amount = event.getAmount();

        AttributeInstance lifeSteal = livingAtt.getAttribute(FTZAttributes.LIFESTEAL);
        double heal = lifeSteal == null ? 0 : lifeSteal.getValue() * event.getAmount();
        if (heal > 0) AdvancedHealing.heal(livingAtt, new HealingSource(HealingTypes.LIFESTEAL, target), (float) heal);

        float bullyDMG = livingAtt.getMainHandItem().getEnchantmentLevel(FTZEnchantments.BULLY) * 1.5f;
        if (bullyDMG > 0) {
            target.addEffect(new MobEffectInstance(FTZMobEffects.MICROSTUN));
            EffectManager effectManager = EffectGetter.getUnwrap(target);
            if (effectManager == null) return;
            StunEffect stunEffect = effectManager.takeEffect(StunEffect.class);
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
        return FLYING.contains(livingEntity.getClass()) || livingEntity.isFallFlying() || !livingEntity.onGround();
    }
    public static boolean isRanged(LivingEntity livingEntity) {
        if (RANGED.contains(livingEntity.getClass())) return true;
        Item item = livingEntity.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        return item instanceof BowItem || item instanceof TridentItem || item instanceof CrossbowItem;
    }
    public static void arrowImpact(AbstractArrow arrow, LivingEntity entity) {
        FeatureManager featureManager = FeatureGetter.getUnwrap(arrow);
        if (featureManager == null) return;
        ArrowEnchant arrowEnchant = featureManager.takeFeature(ArrowEnchant.class);
        if (arrowEnchant == null) return;
        float damage = (float) arrow.getBaseDamage();
        if (arrowEnchant.isFrozen()) EffectHelper.effectWithoutParticles(entity, FTZMobEffects.FROZEN, 40);
        int duel = arrowEnchant.getDuelist();
        if (duel > 0 && isRanged(entity)) arrow.setBaseDamage(damage + duel * 0.75f + 0.5f);
        int ball = arrowEnchant.getBallista();
        if (ball > 0 && isFlying(entity)) arrow.setBaseDamage(damage + ball * 0.75f + 0.5f);
    }
}
