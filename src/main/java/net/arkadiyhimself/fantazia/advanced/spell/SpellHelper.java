package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.cleansing.Cleanse;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.PassiveSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.SelfSpell;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.SpellInstancesHolder;
import net.arkadiyhimself.fantazia.client.render.ParticleMovement;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.Auras;
import net.arkadiyhimself.fantazia.registries.custom.Spells;
import net.arkadiyhimself.fantazia.tags.FTZSpellTags;
import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.arkadiyhimself.fantazia.util.library.Vector3;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class SpellHelper {

    public static final float RAY_RADIUS = 1.15F;

    public enum TargetedCastResult {
        DEFAULT, REFLECTED, BLOCKED
    }

    public static SpellCastResult castPassiveSpell(LivingEntity entity, Holder<AbstractSpell> spell) {
        if (!(entity instanceof ServerPlayer player)) return SpellCastResult.fail();
        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(player, SpellInstancesHolder.class);
        return spellInstancesHolder == null ? SpellCastResult.fail() : spellInstancesHolder.tryToCast(spell);
    }

    public static SpellCastResult tryPassiveSpell(LivingEntity entity, PassiveSpell spell) {
        if (true) {
            LivingEffectHelper.unDisguise(entity);
            if (spell.getCastSound() != null) entity.level().playSound(null, entity.blockPosition(), spell.getCastSound().value(), SoundSource.PLAYERS);
            return spell.onActivation(entity);
        }
        else return SpellCastResult.fail();
    }

    public static SpellCastResult trySelfSpell(LivingEntity entity, SelfSpell spell, boolean ignoreConditions) {
        if (spell.conditions(entity) || ignoreConditions) {
            LivingEffectHelper.unDisguise(entity);
            if (spell.getCastSound() != null) entity.level().playSound(null, entity.blockPosition(), spell.getCastSound().value(), SoundSource.PLAYERS);
            return spell.onCast(entity);
        }
        return SpellCastResult.fail();
    }

    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> SpellCastResult tryTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null) return SpellCastResult.fail();
        SpellCastResult result = spell.beforeBlockCheck(caster, target);

        boolean blocked = false;
        if (!spell.is(FTZSpellTags.NOT_BLOCKABLE)) {
            TargetedCastResult targetedCastResult = getTargetedCastResult(target);
            blocked = targetedCastResult == TargetedCastResult.REFLECTED || targetedCastResult == TargetedCastResult.BLOCKED;
            if (targetedCastResult == TargetedCastResult.REFLECTED && !spell.is(FTZSpellTags.NOT_REFLECTABLE) && spell.canAffect(caster)) spell.afterBlockCheck(target, (T) caster);
        }

        if (blocked) return SpellCastResult.blocked(result.wasteMana(), result.recharge(), result.success());

        LivingEffectHelper.unDisguise(caster);
        spell.afterBlockCheck(caster, target);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> @Nullable T getTarget(LivingEntity caster, TargetedSpell<T> spell) {
        List<LivingEntity> targets = getTargets(caster, RAY_RADIUS, spell.range(), spell.is(FTZSpellTags.THROUGH_WALLS) || AuraHelper.ownsAura(caster, Auras.UNCOVER));
        targets.removeIf(Predicate.not(spell::canAffect));
        List<T> newTargets = (List<T>) targets;
        newTargets.removeIf(living -> !spell.conditions(caster, living));
        if (newTargets.isEmpty()) return null;
        return getClosestEntity(newTargets, caster);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> T commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null || !spell.canAffect(target) || !spell.conditions(caster, target)) return null;
        spell.beforeBlockCheck(caster, target);
        boolean blocked = false;
        if (!spell.is(FTZSpellTags.NOT_BLOCKABLE)) {
            TargetedCastResult result = getTargetedCastResult(target);
            blocked = result == TargetedCastResult.REFLECTED || result == TargetedCastResult.BLOCKED;
            if (result == TargetedCastResult.REFLECTED && spell.canAffect(caster)) spell.afterBlockCheck(target, (T) caster);
        }
        if (!blocked) spell.afterBlockCheck(caster, target);
        return target;
    }

    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> List<T> commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell, Collection<LivingEntity> entities) {
        entities.removeIf(livingEntity -> !spell.canAffect(livingEntity) || livingEntity == caster);
        List<T> newTargets = (List<T>) entities;
        for (T target : newTargets) {
            if (!spell.canAffect(target) || !spell.conditions(caster, target)) continue;
            spell.beforeBlockCheck(caster, target);
            boolean blocked = false;
            if (!spell.is(FTZSpellTags.NOT_BLOCKABLE)) {
                TargetedCastResult result = getTargetedCastResult(target);
                blocked = result == TargetedCastResult.REFLECTED || result == TargetedCastResult.BLOCKED;
                if (result == TargetedCastResult.REFLECTED && spell.canAffect(caster)) spell.afterBlockCheck(target, (T) caster);
            }
            if (!blocked) spell.afterBlockCheck(caster, target);
        }
        return newTargets;
    }

    public static TargetedCastResult getTargetedCastResult(LivingEntity target) {
        if (castPassiveSpell(target, Spells.REFLECT).success()) return TargetedCastResult.REFLECTED;

        boolean reflect = target.hasEffect(FTZMobEffects.REFLECT);
        boolean deflect = target.hasEffect(FTZMobEffects.DEFLECT);

        if (reflect) EffectCleansing.reduceLevel(target, FTZMobEffects.REFLECT);
        else if (deflect) EffectCleansing.reduceLevel(target, FTZMobEffects.DEFLECT);
        else return TargetedCastResult.DEFAULT;

        VisualHelper.particleOnEntityServer(target, ParticleTypes.ENCHANT, ParticleMovement.REGULAR, 35);

        target.level().playSound(null, target.blockPosition(), reflect ? FTZSoundEvents.EFFECT_REFLECT.get() : FTZSoundEvents.EFFECT_DEFLECT.get(), SoundSource.PLAYERS);

        return reflect ? TargetedCastResult.REFLECTED : TargetedCastResult.BLOCKED;
    }

    public static boolean wardenSonicBoom(LivingIncomingDamageEvent event) {
        Entity att = event.getSource().getEntity();
        if (!(att instanceof Warden warden) || !event.getSource().is(DamageTypes.SONIC_BOOM)) return false;
        LivingEntity target = event.getEntity();

        if (castPassiveSpell(target, Spells.REFLECT).success()) {
            event.setCanceled(true);
            VisualHelper.rayOfParticles(target, warden, ParticleTypes.SONIC_BOOM);
            target.level().playSound(null, target.blockPosition(), FTZSoundEvents.REFLECT_CAST.get(), SoundSource.NEUTRAL);
            warden.hurt(event.getEntity().level().damageSources().sonicBoom(target), 15f);
            return true;
        }

        boolean reflect = target.hasEffect(FTZMobEffects.REFLECT);
        boolean deflect = target.hasEffect(FTZMobEffects.DEFLECT);

        if (reflect) EffectCleansing.reduceLevel(target, FTZMobEffects.REFLECT);
        else if (deflect) EffectCleansing.reduceLevel(target, FTZMobEffects.DEFLECT);
        else return false;

        event.setCanceled(true);

        VisualHelper.particleOnEntityServer(target, ParticleTypes.ENCHANT, ParticleMovement.ASCEND, 35);
        target.level().playSound(null, target.blockPosition(), reflect ? FTZSoundEvents.EFFECT_REFLECT.get() : FTZSoundEvents.EFFECT_DEFLECT.get(), SoundSource.NEUTRAL);

        if (reflect) {
            VisualHelper.rayOfParticles(target, warden, ParticleTypes.SONIC_BOOM);
            warden.hurt(warden.level().damageSources().sonicBoom(target), 15f);
        }

        return true;
    }

    public static boolean spellAvailable(LivingEntity entity, Holder<AbstractSpell> spell) {
        if (!(entity instanceof Player player)) return false;

        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(player, SpellInstancesHolder.class);
        return spellInstancesHolder != null && spellInstancesHolder.hasSpell(spell);
    }

    public static boolean hasActiveSpell(LivingEntity entity, Holder<AbstractSpell> spell) {
        if (!(entity instanceof Player player)) return false;

        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(player, SpellInstancesHolder.class);
        return spellInstancesHolder != null && spellInstancesHolder.hasActiveSpell(spell);
    }

    public static boolean onCooldown(LivingEntity entity, Holder<AbstractSpell> spell) {
        if (!(entity instanceof Player player)) return false;

        SpellInstancesHolder spellInstancesHolder = PlayerAbilityHelper.takeHolder(player, SpellInstancesHolder.class);
        return spellInstancesHolder != null && spellInstancesHolder.getOrCreate(spell).recharge() > 0;
    }

    public static List<LivingEntity> getTargets(@NotNull LivingEntity caster, float radius, float range, boolean ignoreObstacles) {
        Vector3 casterCenter = Vector3.fromEntityCenter(caster);
        Vector3 head;

        List<LivingEntity> entities = new ArrayList<>();

        AttributeInstance castRange = caster.getAttribute(FTZAttributes.CAST_RANGE_ADDITION);
        float addRange = castRange == null ? 0 : (float) castRange.getValue();
        float finalRange = range + addRange;

        for (int distance = 1; distance < finalRange; ++distance) {
            head = casterCenter.add(new Vector3(caster.getLookAngle().normalize()).multiply(distance)).add(0.0, 0.5, 0.0);
            List<LivingEntity> list = caster.level().getEntitiesOfClass(LivingEntity.class, FantazicMath.squareBoxFromCenterAndSide(head.toVec3D(), radius));
            list.removeIf(entity -> (entity == caster || (!caster.hasLineOfSight(entity) && !(ignoreObstacles && Minecraft.getInstance().shouldEntityAppearGlowing(entity)))));
            entities.addAll(list);
        }

        return entities;
    }

    public static <T extends LivingEntity> @Nullable T getClosestEntity(List<T> entities, LivingEntity livingEntity) {
        if (entities.isEmpty()) return null;
        if (entities.size() == 1) return entities.getFirst();
        Map<Double, T> livingEntityMap = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (T entity : entities) {
            double distance = entity.distanceTo(livingEntity);
            livingEntityMap.put(distance, entity);
            distances.add(distance);
        }
        distances.sort(Comparator.comparing(Double::doubleValue));
        return livingEntityMap.get(distances.getFirst());
    }

    public static void allIn1(LivingEntity owner) {
        for (int i = 0; i < 7; i++) FantazicCombat.summonRandomFirework(owner);
    }

    public static void allIn2(LivingEntity owner) {
        LivingEffectHelper.effectWithoutParticles(owner, FTZMobEffects.LAYERED_BARRIER, 200, 6);
        LivingEffectHelper.effectWithoutParticles(owner, FTZMobEffects.MIGHT, 200, 3);
        EffectCleansing.tryCleanseAll(owner, Cleanse.MEDIUM, MobEffectCategory.HARMFUL);
    }

    public static void allIn3(LivingEntity owner) {
        if (!(owner instanceof Player player)) return;

        SpellInstancesHolder holder = PlayerAbilityHelper.takeHolder(player, SpellInstancesHolder.class);
        if (holder == null) return;
        Map<Holder<AbstractSpell>, SpellInstance> availableSpells = holder.availableSpells();
        availableSpells.remove(Spells.ALL_IN);
        RandomList<SpellInstance> spellInstances = RandomList.emptyRandomList();
        for (SpellInstance instance : availableSpells.values()) if (instance.recharge() > 0) spellInstances.add(instance);

        if (spellInstances.isEmpty()) LivingEffectHelper.giveAceInTheHole(owner, 200);
        else spellInstances.performOnRandom(SpellInstance::resetRecharge);
    }

    public static void allIn4(LivingEntity owner) {
        owner.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,2));
        owner.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,100));
        owner.addEffect(new MobEffectInstance(FTZMobEffects.CORROSION,100,2));
    }
}
