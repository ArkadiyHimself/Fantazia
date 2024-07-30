package net.arkadiyhimself.fantazia.advanced.capacity.spellhandler;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.RenderingValues;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.items.casters.SpellCaster;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.library.Vector3;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;

import java.util.*;
import java.util.function.Predicate;

public class SpellHelper {
    public static final HashMap<ResourceLocation, SelfSpell> SELF_SPELLS = Maps.newHashMap();
    public static final HashMap<ResourceLocation, TargetedSpell<? extends LivingEntity>> TARGETED_SPELLS = Maps.newHashMap();
    public static final HashMap<ResourceLocation, PassiveSpell> PASSIVE_SPELLS = Maps.newHashMap();
    public static boolean trySelfSpell(LivingEntity entity, SelfSpell spell) {
        boolean flag = spell.conditions(entity);
        if (flag) {
            spell.onCast(entity);
            entity.level().playSound(null, entity.blockPosition(), FTZSoundEvents.ENTANGLE, SoundSource.NEUTRAL);
        }
        return flag;
    }
    private static <T extends LivingEntity> @Nullable T getTarget(LivingEntity caster, TargetedSpell<T> spell) {
        List<LivingEntity> targets = getTargets(caster, 1f, spell.getRange(), spell.thruWalls());
        targets.removeIf(Predicate.not(spell::canAffect));
        List<T> newTargets = (List<T>) targets;
        newTargets.removeIf(living -> !spell.conditions(caster, living));
        if (newTargets.isEmpty()) return null;
        return getClosestEntity(newTargets, caster);
    }
    public static <T extends LivingEntity> boolean tryTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null) return false;
        spell.before(caster, target);
        boolean blocked = false;
        if (spell.canBlock()) {
            TargetedResult result = abilityBlocking(target);
            blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
            if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) {
                spell.after(target, (T) caster);
                return false;
            }
        }
        if (!blocked) {
            spell.after(caster, target);
            return true;
        }
        return false;
    }
    @Nullable
    public static <T extends LivingEntity> T commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null || !spell.canAffect(target) || !spell.conditions(caster, target)) return null;
        spell.before(caster, target);
        boolean blocked = false;
        if (spell.canBlock()) {
            TargetedResult result = abilityBlocking(target);
            blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
            if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) spell.after(target, (T) caster);
        }
        if (!blocked) spell.after(caster, target);
        return target;
    }
    public static <T extends LivingEntity> List<T> commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell, Collection<LivingEntity> entities) {
        entities.removeIf(livingEntity -> !spell.canAffect(livingEntity) || livingEntity == caster);
        List<T> newTargets = (List<T>) entities;
        for (T target : newTargets) {
            if (!spell.canAffect(target) || !spell.conditions(caster, target)) continue;
            spell.before(caster, target);
            boolean blocked = false;
            if (spell.canBlock()) {
                TargetedResult result = abilityBlocking(target);
                blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
                if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) spell.after(target, (T) caster);
            }
            if (!blocked) spell.after(caster, target);
        }
        return newTargets;
    }
    public static TargetedResult abilityBlocking(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer) {
            List<SlotResult> slotResults = InventoryHelper.findAllCurio(serverPlayer, "passivecaster");
            for (SlotResult slotResult : slotResults) {
                Item item = slotResult.stack().getItem();
                if (item instanceof SpellCaster spellCaster && spellCaster.getSpell() == Spells.REFLECT && !serverPlayer.getCooldowns().isOnCooldown(spellCaster)) {
                    serverPlayer.getCooldowns().addCooldown(spellCaster, spellCaster.getSpell().getRecharge());
                    serverPlayer.level().playSound(null, serverPlayer.blockPosition(), spellCaster.getSpell().getCastSound(), SoundSource.PLAYERS);
                    AbilityManager abilityManager = AbilityGetter.getUnwrap(serverPlayer);
                    abilityManager.getAbility(RenderingValues.class).ifPresent(RenderingValues::onMirrorActivation);
                    return TargetedResult.REFLECTED;
                }
            }
        }
        int num = switch (Minecraft.getInstance().options.particles().get()) {
            case MINIMAL -> 20;
            case DECREASED -> 30;
            case ALL -> 40;
        };
        if (target.hasEffect(FTZMobEffects.REFLECT)) {
            for (int i = 0; i < num; i++) VisualHelper.randomParticleOnModel(target, ParticleTypes.ENCHANT, VisualHelper.ParticleMovement.FROM_CENTER);
            EffectCleansing.forceCleanse(target, FTZMobEffects.REFLECT);
            target.level().playSound(null, target.blockPosition(), FTZSoundEvents.REFLECT, SoundSource.PLAYERS);
            Minecraft.getInstance().level.playSound(null, target.blockPosition(), FTZSoundEvents.REFLECT, SoundSource.PLAYERS);
            if (target instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.REFLECT), serverPlayer);

            return TargetedResult.REFLECTED;
        }
        if (target.hasEffect(FTZMobEffects.DEFLECT)) {
            for (int i = 0; i < num; i++) VisualHelper.randomParticleOnModel(target, ParticleTypes.ENCHANT, VisualHelper.ParticleMovement.FROM_CENTER);
            EffectCleansing.forceCleanse(target, FTZMobEffects.DEFLECT);
            target.level().playSound(null, target.blockPosition(), FTZSoundEvents.DEFLECT, SoundSource.PLAYERS);
            Minecraft.getInstance().level.playSound(null, target.blockPosition(), FTZSoundEvents.REFLECT, SoundSource.PLAYERS);
            if (target instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DEFLECT), serverPlayer);

            return TargetedResult.BLOCKED;
        }
        return TargetedResult.DEFAULT;
    }
    public enum TargetedResult {
        DEFAULT, REFLECTED, BLOCKED;
    }
    public static boolean hasSpell(LivingEntity entity, Spell spell) {
        List<SlotResult> slotResults = Lists.newArrayList();
        slotResults.addAll(InventoryHelper.findCurios(entity, "passivecaster"));
        slotResults.addAll(InventoryHelper.findCurios(entity, "spellcaster"));
        boolean flag = false;
        for (SlotResult slotResult : slotResults) {
            if (slotResult.stack().getItem() instanceof SpellCaster spellCaster && spellCaster.getSpell() == spell) {
                flag = true;
            }
        }
        return flag;
    }
    public static boolean hasActiveSpell(LivingEntity entity, Spell spell) {
        List<SlotResult> slotResults = Lists.newArrayList();
        slotResults.addAll(InventoryHelper.findCurios(entity, "passivecaster"));
        slotResults.addAll(InventoryHelper.findCurios(entity, "spellcaster"));
        boolean flag = false;
        for (SlotResult slotResult : slotResults) {
            if (slotResult.stack().getItem() instanceof SpellCaster spellCaster && spellCaster.getSpell() == spell) {
                if (!(entity instanceof ServerPlayer serverPlayer)) return true;
                else {
                    if (!serverPlayer.getCooldowns().isOnCooldown(spellCaster)) {
                        flag = true;
                        serverPlayer.getCooldowns().addCooldown(spellCaster, spell.getRecharge());
                    }
                }
            }
        }
        return flag;
    }
    public static List<LivingEntity> getTargets(@NotNull LivingEntity caster, float range, float maxDist, boolean seeThruWalls) {
        Vector3 head = Vector3.fromEntityCenter(caster);
        List<LivingEntity> entities = new ArrayList<>();

        for (int distance = 1; distance < maxDist; ++distance) {
            head = head.add(new Vector3(caster.getLookAngle()).multiply(distance)).add(0.0, 0.5, 0.0);
            List<LivingEntity> list = caster.level().getEntitiesOfClass(LivingEntity.class, new AABB(head.x - range, head.y - range, head.z - range, head.x + range, head.y + range, head.z + range));
            list.removeIf(entity -> (entity == caster || (!caster.hasLineOfSight(entity) && !(seeThruWalls && Minecraft.getInstance().shouldEntityAppearGlowing(entity)))));
            entities.addAll(list);
        }

        return entities;
    }
    @Nullable
    public static <T extends LivingEntity>  T getClosestEntity(List<T> entities, LivingEntity player) {
        if (entities.isEmpty()) return null;
        if (entities.size() == 1) return entities.get(0);
        Map<Double, T> livingEntityMap = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (T entity : entities) {
            double distance = entity.distanceTo(player);
            livingEntityMap.put(distance, entity);
            distances.add(distance);
        }
        distances.sort(Comparator.comparing(Double::doubleValue));
        return livingEntityMap.get(distances.get(0));
    }
}