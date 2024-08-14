package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCap;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuraHelper {

    // sorts a list of aura instances with a complicated algorithm, removing an aura instance if entity doesn't Primary Conditions and then prioritising instances where entity matches Secondary Conditions
    public static <T extends Entity, M extends Entity> List<AuraInstance<T, M>> sortUniqueAura(List<AuraInstance<T, M>> instances, @NotNull T entity) {
        instances.removeIf(auraInstance -> auraInstance.notInside(entity));
        instances.removeIf(auraInstance -> !auraInstance.getAura().affectedClass().isInstance(entity) && !Fantazia.DEVELOPER_MODE);
        instances.removeIf(auraInstance -> !auraInstance.getAura().couldAffect(entity, auraInstance.getOwner()) && !Fantazia.DEVELOPER_MODE);
        List<AuraInstance<T, M>> unique = Lists.newArrayList();
        while (!instances.isEmpty()) {
            AuraInstance<T, M> instance = instances.get(0);
            AuraInstance<T, M> busyInstance = null;
            boolean sameAura = false;
            for (AuraInstance<T, M> tmAuraInstance : unique) {
                if (tmAuraInstance.getAura() == instance.getAura()) {
                    sameAura = true;
                    busyInstance = tmAuraInstance;
                    break;
                }
            }
            if (sameAura) {
                boolean secCond = instance.getAura().secondary(instance.getAura().affectedClass().cast(entity), instance.getOwner());
                boolean secCondBusy = busyInstance.getAura().secondary(instance.getAura().affectedClass().cast(entity), instance.getOwner());
                if (!secCondBusy && secCond) {
                    unique.remove(busyInstance);
                    unique.add(instance);
                }
            } else {
                unique.add(instance);
            }
            instances.remove(instance);
        }
        return unique;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity, M extends Entity> List<AuraInstance<T, M>> getAffectingAuras(@NotNull T entity) {
        LevelCap levelCap = LevelCapGetter.getLevelCap(entity.level());
        List<AuraInstance<T, M>> auras = Lists.newArrayList();
        if (levelCap == null) return auras;
        levelCap.getAuraInstances().forEach(auraInstance -> {
            if (auraInstance.getAura().affectedClass().isInstance(entity)) {
                auras.add((AuraInstance<T, M>) auraInstance);
            }
        });
        return sortUniqueAura(auras, entity);
    }

    public static boolean affected(Entity entity, BasicAura<?,?> aura) {
        for (AuraInstance<?,?> instance : getAffectingAuras(entity)) if (instance.getAura() == aura) return true;
        return false;
    }

    public static <T extends Entity, M extends Entity> List<ResourceKey<DamageType>> damageImmunities(@NotNull T entity) {
        List<ResourceKey<DamageType>> damageImmune = Lists.newArrayList();
        List<AuraInstance<T, M>> auraInstances = getAffectingAuras(entity);

        for (AuraInstance<T, M> auraInstance : auraInstances) for (ResourceKey<DamageType> resourceKey : auraInstance.getAura().immunities()) if (!damageImmune.contains(resourceKey)) damageImmune.add(resourceKey);

        return damageImmune;
    }

    public static <T extends Entity, M extends Entity> HashMap<ResourceKey<DamageType>, Float> damageMultipliers(@NotNull T entity) {
        HashMap<ResourceKey<DamageType>, Float> damageMultiply = Maps.newHashMap();
        List<AuraInstance<T, M>> auraInstances = getAffectingAuras(entity);

        for (AuraInstance<T, M> auraInstance : auraInstances)
            for (Map.Entry<ResourceKey<DamageType>, Float> entry : auraInstance.getAura().multipliers().entrySet())
                if (damageMultiply.containsKey(entry.getKey())) {
                    float multi1 = entry.getValue();
                    float multi2 = damageMultiply.get(entry.getKey());
                    damageMultiply.replace(entry.getKey(), multi1 * multi2);
                } else damageMultiply.put(entry.getKey(), entry.getValue());

        return damageMultiply;
    }
    public static <T extends Entity, M extends Entity> void auraTick(T entity, AuraInstance<T, M> auraInstance) {
        BasicAura<T, M> basicAura = auraInstance.getAura();
        if (!basicAura.canAffect(entity, auraInstance.getOwner())) return;
        if (entity instanceof LivingEntity livingEntity) for (Map.Entry<MobEffect, Integer> entry : basicAura.getMobEffects().entrySet()) EffectHelper.effectWithoutParticles(livingEntity, entry.getKey(), 2, entry.getValue());
    }
    public static <T extends Entity> void aurasTick(T entity) {
        List<AuraInstance<Entity, Entity>> affectingAuras = AuraHelper.getAffectingAuras(entity);
        for (AuraInstance<Entity, Entity> auraInstance : affectingAuras) auraTick(entity, auraInstance);
    }
}
