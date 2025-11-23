package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.common.advanced.rune.RuneHelper;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.EuphoriaHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.EffectsSpawnAppliersHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.HealingSourcesHolder;
import net.arkadiyhimself.fantazia.common.api.data_component.HiddenPotentialComponent;
import net.arkadiyhimself.fantazia.common.entity.Shockwave;
import net.arkadiyhimself.fantazia.common.entity.ThrownHatchet;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import javax.annotation.Nullable;

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
        DamageSource source = event.getSource();
        boolean meleeAttack = source.is(FTZDamageTypeTags.MELEE_ATTACK);
        if (!meleeAttack || !(source.getEntity() instanceof LivingEntity livingAttacker)) return;
        LivingEntity target = event.getEntity();

        float amount = event.getNewDamage();

        maybeSummonShockwave(livingAttacker);
        ApplyEffect.unDisguise(livingAttacker);
        AttributeInstance lifeSteal = livingAttacker.getAttribute(FTZAttributes.LIFESTEAL);
        double heal = lifeSteal == null ? 0 : lifeSteal.getValue() * amount;
        if (heal > 0) LevelAttributesHelper.healEntityByOther(livingAttacker, target, (float) heal, HealingSourcesHolder::lifesteal);
        if (livingAttacker.getData(FTZAttachmentTypes.WALL_CLIMBING_POISON)) target.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 2));

        if (livingAttacker instanceof ServerPlayer player) {
            ItemStack itemStack = player.getMainHandItem();
            if (itemStack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL)) itemStack.update(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialComponent.DEFAULT, holder -> holder.onAttack(source.is(FTZDamageTypeTags.DOUBLE_DAMAGE_INCREASE_FOR_HIDDEN_POTENTIAL), target));

            player.getData(FTZAttachmentTypes.ABILITY_MANAGER).optionalHolder(EuphoriaHolder.class).ifPresent(euphoriaHolder -> euphoriaHolder.processAttack(event));
        }

        if (RuneHelper.hasRune(livingAttacker, Runes.PURE_VESSEL) && livingAttacker.getY() > target.getY() && !livingAttacker.onGround()) {
            PlayerAbilityHelper.jumpInAir(livingAttacker);
            if (livingAttacker instanceof ServerPlayer serverPlayer) PlayerAbilityHelper.pogo(serverPlayer);
        }

        if (SpellHelper.spellAvailable(livingAttacker, Spells.SUSTAIN))
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 80, 2));
    }

    public static void arrowImpact(AbstractArrow arrow, LivingEntity entity) {
        Entity owner = arrow.getOwner();

        if (arrow.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).value() > 0) entity.getData(FTZAttachmentTypes.ANCIENT_FLAME_TICKS).set(80);

        if (owner instanceof LivingEntity attacker) {
            MobEffectInstance instance = attacker.getEffect(FTZMobEffects.MIGHT);
            if (instance != null) {
                int ampl = instance.getAmplifier();
                arrow.setBaseDamage(arrow.getBaseDamage() + 0.5 * (1 + ampl));
            }
        }
    }

    public static boolean attemptEvasion(LivingIncomingDamageEvent event) {
        DamageSource source = event.getSource();
        if (!source.is(FTZDamageTypeTags.CAN_BE_EVADED)) return false;

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

    public static void maybeSummonShockwave(LivingEntity attacker) {
        AttributeInstance attackDamage = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (SpellHelper.castPassiveSpell(attacker, Spells.SHOCKWAVE).success() && attackDamage != null) {
            int ampl = SpellHelper.getSpellAmplifier(attacker, Spells.SHOCKWAVE);
            float multiplier = 0.5f + 0.05f * ampl;
            float sat = attacker instanceof Player player ? (float) player.getFoodData().getFoodLevel() / 20 : 1f;
            Shockwave shockwave = new Shockwave(attacker.level(), attacker,(float) attackDamage.getValue() * sat * multiplier);
            shockwave.setPos(attacker.getEyePosition().add(0,-0.45,0));
            attacker.level().addFreshEntity(shockwave);
        }
    }
}
