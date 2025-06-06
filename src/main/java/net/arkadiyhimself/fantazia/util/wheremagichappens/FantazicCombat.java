package net.arkadiyhimself.fantazia.util.wheremagichappens;

import it.unimi.dsi.fastutil.ints.IntList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.niche_data_holders.ArrowEnchantmentsHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.EuphoriaHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.EffectsSpawnAppliersHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.api.data_component.HiddenPotentialHolder;
import net.arkadiyhimself.fantazia.entities.Shockwave;
import net.arkadiyhimself.fantazia.entities.ThrownHatchet;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.Runes;
import net.arkadiyhimself.fantazia.registries.custom.Spells;
import net.arkadiyhimself.fantazia.tags.FTZEntityTypeTags;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class FantazicCombat {

    public static void dropExperience(LivingEntity entity, float multiplier, LivingEntity killer) {
        if (entity.level() instanceof ServerLevel serverLevel) {
            int reward = (int) (entity.getExperienceReward(serverLevel, killer) * multiplier);
            ExperienceOrb.award(serverLevel, entity.position(), reward);
        }
    }

    public static boolean blocksDamage(LivingEntity entity) {
        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() > 1) return true;
        }

        if (entity.getData(FTZAttachmentTypes.BARRIER_HEALTH) > 0) return true;
        if (entity.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS) > 0) return true;
        if (LivingEffectHelper.hasEffectSimple(entity, FTZMobEffects.ABSOLUTE_BARRIER.value())) return true;

        return false;
    }

    public static boolean isInvulnerable(LivingEntity entity) {
        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() >= 2) return true;
        }
        return entity.isInvulnerable();
    }

    public static boolean isPhasing(LivingEntity entity) {
        if (entity instanceof Player player) {
            DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
            if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() >= 3) return true;
        }
        return false;
    }

    public static void meleeAttack(LivingDamageEvent.Pre event) {
        boolean meleeAttack = event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypes.MOB_ATTACK);
        boolean parry = event.getSource().is(FTZDamageTypes.PARRY);
        if (!meleeAttack && !parry || !(event.getSource().getEntity() instanceof LivingEntity livingAttacker)) return;
        LivingEntity target = event.getEntity();

        float amount = event.getNewDamage();

        ApplyEffect.unDisguise(livingAttacker);
        AttributeInstance lifeSteal = livingAttacker.getAttribute(FTZAttributes.LIFESTEAL);
        double heal = lifeSteal == null ? 0 : lifeSteal.getValue() * amount;
        if (heal > 0) LevelAttributesHelper.healEntityByOther(livingAttacker, target, (float) heal, HealingSourcesHolder::lifesteal);
        if (livingAttacker.getData(FTZAttachmentTypes.WALL_CLIMBING_POISON)) target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));

        Registry<Enchantment> enchantmentRegistry = target.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        Optional<Holder.Reference<Enchantment>> bully = enchantmentRegistry.getHolder(FTZEnchantments.BULLY);
        float bullyDMG = bully.map(enchantmentReference -> livingAttacker.getMainHandItem().getEnchantmentLevel(enchantmentReference) * 1.5f).orElse(0F);
        if (bullyDMG > 0) {
            ApplyEffect.microStun(target);
            StunEffectHolder stunEffect = LivingEffectHelper.takeHolder(target, StunEffectHolder.class);
            if (stunEffect != null && stunEffect.stunned()) event.setNewDamage(amount + bullyDMG);
        }

        if (livingAttacker instanceof ServerPlayer player) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) itemStack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialHolder.DEFAULT, holder -> holder.onAttack(parry, target));

            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).optionalHolder(EuphoriaHolder.class).ifPresent(euphoriaHolder -> euphoriaHolder.processAttack(event));
        }

        if (SpellHelper.hasActiveSpell(livingAttacker, Spells.SHOCKWAVE)) {
            int ampl = SpellHelper.getSpellAmplifier(livingAttacker, Spells.SHOCKWAVE);
            float multip = 0.5f + 0.05f * ampl;
            float sat = livingAttacker instanceof Player player ? (float) player.getFoodData().getFoodLevel() / 20 : 1f;
            Shockwave shockwave = new Shockwave(livingAttacker.level(), livingAttacker,amount * sat * multip);
            shockwave.setPos(target.getEyePosition().add(0,-0.45,0));
            livingAttacker.level().addFreshEntity(shockwave);
        }

        if (FantazicUtil.hasRune(livingAttacker, Runes.PURE_VESSEL) && livingAttacker.getY() > target.getY() && !livingAttacker.onGround()) {
            PlayerAbilityHelper.jumpInAir(livingAttacker);
            if (livingAttacker instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.pogo(serverPlayer);
        }

        if (SpellHelper.castPassiveSpell(livingAttacker, Spells.SUSTAIN).success()) target.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 2));
    }

    public static boolean isFlying(LivingEntity livingEntity) {
        return livingEntity.getType().is(FTZEntityTypeTags.AERIAL) || livingEntity.isFallFlying() || !livingEntity.onGround();
    }

    public static boolean isRanged(LivingEntity livingEntity) {
        if (livingEntity.getType().is(FTZEntityTypeTags.RANGED_ATTACK)) return true;
        ItemStack item = livingEntity.getItemInHand(InteractionHand.MAIN_HAND);
        return item.is(FTZItemTags.RANGED_WEAPON);
    }

    public static void arrowImpact(AbstractArrow arrow, LivingEntity entity) {
        ArrowEnchantmentsHolder arrowEnchantmentsHolder = arrow.getData(FTZAttachmentTypes.ARROW_ENCHANTMENTS);
        if (arrowEnchantmentsHolder.isFrozen()) ApplyEffect.makeFrozen(entity, 40);

        float damage = (float) arrow.getBaseDamage();
        int duel = arrowEnchantmentsHolder.getDuelist();
        if (duel > 0 && isRanged(entity)) arrow.setBaseDamage(damage + duel * 0.75f + 0.5f);
        int ball = arrowEnchantmentsHolder.getBallista();
        if (ball > 0 && isFlying(entity)) arrow.setBaseDamage(damage + ball * 0.75f + 0.5f);
    }

    public static boolean attemptEvasion(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!source.is(DamageTypes.MOB_ATTACK) && !source.is(DamageTypes.PLAYER_ATTACK)) return false;

        LivingEntity livingEntity = event.getEntity();
        EvasionHolder evasionHolder = LivingDataHelper.takeHolder(livingEntity, EvasionHolder.class);
        if (evasionHolder == null) return false;
        if (evasionHolder.getIFrames() > 0 || evasionHolder.tryEvade()) event.setCanceled(true);
        return event.isCanceled();
    }

    public static boolean attemptEvasion(ProjectileImpactEvent event) {
        if (!(event.getRayTraceResult() instanceof EntityHitResult entityHitResult) || !(entityHitResult.getEntity() instanceof LivingEntity livingEntity)) return false;
        if (event.getProjectile() instanceof ThrownHatchet thrownHatchet && thrownHatchet.isPhasing()) return false;
        EvasionHolder evasionHolder = LivingDataHelper.takeHolder(livingEntity, EvasionHolder.class);
        if (evasionHolder == null) return false;
        if (evasionHolder.getIFrames() > 0 || evasionHolder.tryEvade()) event.setCanceled(true);
        return event.isCanceled();
    }

    public static void grantEffectsOnSpawn(Mob mob) {
        if (!(mob.level() instanceof ServerLevel level)) return;
        LevelAttributesHelper.acceptConsumer(level, EffectsSpawnAppliersHolder.class, effectsSpawnAppliersHolder -> effectsSpawnAppliersHolder.tryApplyEffects(mob));
    }

    public static void clearTarget(Mob mob, @Nullable LivingEntity target) {
        if (mob.getTarget() == target || target == null) {
            mob.setTarget(null);
            for (WrappedGoal wrappedGoal : mob.targetSelector.getAvailableGoals()) {
                if (wrappedGoal.getGoal() instanceof HurtByTargetGoal goal) goal.stop();
                if (wrappedGoal.getGoal() instanceof SwellGoal goal) goal.stop();
            }
        }
    }
}
