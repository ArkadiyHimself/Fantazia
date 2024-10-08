package net.arkadiyhimself.fantazia.advanced.spell;

import net.arkadiyhimself.fantazia.advanced.cleansing.EffectCleansing;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.ClientValuesHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.items.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZSpellTags;
import net.arkadiyhimself.fantazia.util.library.Vector3;
import net.arkadiyhimself.fantazia.util.wheremagichappens.InventoryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotResult;

import java.util.*;
import java.util.function.Predicate;

public class SpellHelper {
    public enum TargetedResult {
        DEFAULT, REFLECTED, BLOCKED
    }
    public static boolean trySelfSpell(LivingEntity entity, SelfSpell spell, boolean ignoreConditions) {
        if (spell.conditions(entity) || ignoreConditions) {
            spell.onCast(entity);
            if (spell.getCastSound() != null) entity.level().playSound(null, entity.blockPosition(), spell.getCastSound(), SoundSource.NEUTRAL);
            return true;
        }
        return false;
    }
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> boolean tryTargetedSpell(LivingEntity caster, TargetedSpell<T> spell) {
        T target = getTarget(caster, spell);
        if (target == null) return false;
        spell.before(caster, target);

        boolean blocked = false;
        if (!spell.is(FTZSpellTags.NOT_BLOCKABLE)) {
            TargetedResult result = spellDeflecting(target);
            blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
            if (result == TargetedResult.REFLECTED && !spell.is(FTZSpellTags.NOT_REFLECTABLE) && spell.canAffect(caster)) spell.after(target, (T) caster);
        }

        if (!blocked) spell.after(caster, target);

        return true;
    }
    @SuppressWarnings("unchecked")
    private static <T extends LivingEntity> @Nullable T getTarget(LivingEntity caster, TargetedSpell<T> spell) {
        List<LivingEntity> targets = getTargets(caster, 1.5f, spell.getRange(), spell.is(FTZSpellTags.THROUGH_WALLS));
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
        spell.before(caster, target);
        boolean blocked = false;
        if (!spell.is(FTZSpellTags.NOT_BLOCKABLE)) {
            TargetedResult result = spellDeflecting(target);
            blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
            if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) spell.after(target, (T) caster);
        }
        if (!blocked) spell.after(caster, target);
        return target;
    }
    @SuppressWarnings("unchecked")
    public static <T extends LivingEntity> List<T> commandTargetedSpell(LivingEntity caster, TargetedSpell<T> spell, Collection<LivingEntity> entities) {
        entities.removeIf(livingEntity -> !spell.canAffect(livingEntity) || livingEntity == caster);
        List<T> newTargets = (List<T>) entities;
        for (T target : newTargets) {
            if (!spell.canAffect(target) || !spell.conditions(caster, target)) continue;
            spell.before(caster, target);
            boolean blocked = false;
            if (!spell.is(FTZSpellTags.NOT_BLOCKABLE)) {
                TargetedResult result = spellDeflecting(target);
                blocked = result == TargetedResult.REFLECTED || result == TargetedResult.BLOCKED;
                if (result == TargetedResult.REFLECTED && spell.canAffect(caster)) spell.after(target, (T) caster);
            }
            if (!blocked) spell.after(caster, target);
        }
        return newTargets;
    }
    public static TargetedResult spellDeflecting(LivingEntity target) {
        if (target instanceof ServerPlayer serverPlayer && SpellHelper.hasActiveSpell(serverPlayer, FTZSpells.REFLECT.get())) {
            PlayerAbilityGetter.acceptConsumer(serverPlayer, ClientValuesHolder.class, ClientValuesHolder::onMirrorActivation);
            return TargetedResult.REFLECTED;
        }

        boolean reflect = target.hasEffect(FTZMobEffects.REFLECT);
        boolean deflect = target.hasEffect(FTZMobEffects.DEFLECT);

        if (reflect) EffectCleansing.forceCleanse(target, FTZMobEffects.REFLECT);
        else if (deflect) EffectCleansing.forceCleanse(target, FTZMobEffects.DEFLECT);
        else return TargetedResult.DEFAULT;

        for (int i = 0; i < 20 + Minecraft.getInstance().options.particles().get().getId() * 20; i++) VisualHelper.randomParticleOnModel(target, ParticleTypes.ENCHANT, VisualHelper.ParticleMovement.REGULAR);

        target.level().playSound(null, target.blockPosition(), reflect ? FTZSoundEvents.REFLECT.get() : FTZSoundEvents.DEFLECT.get(), SoundSource.PLAYERS);

        return reflect ? TargetedResult.REFLECTED : TargetedResult.BLOCKED;
    }
    public static boolean wardenSonicBoom(LivingIncomingDamageEvent event) {
        Entity att = event.getSource().getEntity();
        if (!(att instanceof Warden warden) || !event.getSource().is(DamageTypes.SONIC_BOOM)) return false;
        LivingEntity target = event.getEntity();

        if (target instanceof ServerPlayer player && SpellHelper.hasActiveSpell(player, FTZSpells.REFLECT.get())) {
            PlayerAbilityGetter.acceptConsumer(player, ClientValuesHolder.class, ClientValuesHolder::onMirrorActivation);
            event.setCanceled(true);
            VisualHelper.rayOfParticles(player, warden, ParticleTypes.SONIC_BOOM);
            player.level().playSound(null, player.blockPosition(), FTZSoundEvents.MYSTIC_MIRROR.get(), SoundSource.NEUTRAL);
            warden.hurt(event.getEntity().level().damageSources().sonicBoom(player), 15f);
            return true;
        }

        boolean reflect = target.hasEffect(FTZMobEffects.REFLECT);
        boolean deflect = target.hasEffect(FTZMobEffects.DEFLECT);

        if (reflect) EffectCleansing.forceCleanse(target, FTZMobEffects.REFLECT);
        else if (deflect) EffectCleansing.forceCleanse(target, FTZMobEffects.DEFLECT);
        else return false;

        event.setCanceled(true);

        for (int i = 0; i < 20 + Minecraft.getInstance().options.particles().get().getId() * 20; i++) VisualHelper.randomParticleOnModel(target, ParticleTypes.ENCHANT, VisualHelper.ParticleMovement.ASCEND);
        target.level().playSound(null, target.blockPosition(), reflect ? FTZSoundEvents.REFLECT.get() : FTZSoundEvents.DEFLECT.get(), SoundSource.NEUTRAL);

        if (reflect) {
            VisualHelper.rayOfParticles(target, warden, ParticleTypes.SONIC_BOOM);
            warden.hurt(warden.level().damageSources().sonicBoom(target), 15f);
        }
        return true;
    }
    public static boolean hasSpell(LivingEntity entity, AbstractSpell spell) {
        List<SlotResult> slotResults = Lists.newArrayList();
        slotResults.addAll(InventoryHelper.findCurios(entity, "passivecaster"));
        slotResults.addAll(InventoryHelper.findCurios(entity, "spellcaster"));
        boolean flag = false;
        for (SlotResult slotResult : slotResults) {
            if (slotResult.stack().getItem() instanceof SpellCasterItem spellCasterItem && spellCasterItem.getSpell() == spell) {
                flag = true;
            }
        }
        return flag;
    }
    public static boolean hasActiveSpell(LivingEntity entity, AbstractSpell spell) {
        List<SlotResult> slotResults = Lists.newArrayList();
        slotResults.addAll(InventoryHelper.findCurios(entity, "passivecaster"));
        slotResults.addAll(InventoryHelper.findCurios(entity, "spellcaster"));
        boolean flag = false;
        for (SlotResult slotResult : slotResults) {
            if (slotResult.stack().getItem() instanceof SpellCasterItem spellCasterItem && spellCasterItem.getSpell() == spell) {
                if (!(entity instanceof ServerPlayer serverPlayer)) return true;
                else {
                    if (serverPlayer.isCreative() || serverPlayer.isSpectator()) return true;
                    if (!serverPlayer.getCooldowns().isOnCooldown(spellCasterItem)) {
                        flag = true;
                        serverPlayer.getCooldowns().addCooldown(spellCasterItem, spell.getRecharge());
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
        if (entities.size() == 1) return entities.getFirst();
        Map<Double, T> livingEntityMap = new HashMap<>();
        List<Double> distances = new ArrayList<>();
        for (T entity : entities) {
            double distance = entity.distanceTo(player);
            livingEntityMap.put(distance, entity);
            distances.add(distance);
        }
        distances.sort(Comparator.comparing(Double::doubleValue));
        return livingEntityMap.get(distances.getFirst());
    }
}
