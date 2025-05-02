package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.AurasInstancesHolder;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class AuraHelper {
    private AuraHelper() {}

    // sorts a list of aura instances with a complicated algorithm, removing an aura instance if entity doesn't match Primary Conditions and then prioritising instances where entity matches Secondary Conditions
    public static List<AuraInstance> sortUniqueAura(List<AuraInstance> instances, @NotNull Entity entity, boolean owned) {
        List<AuraInstance> ownedAuras = Lists.newArrayList();
        for (AuraInstance auraInstance : instances) if (auraInstance.getOwner() == entity) ownedAuras.add(auraInstance);
        instances.removeIf(auraInstance -> auraInstance.getOwner() == entity);

        instances.removeIf(auraInstance -> !auraInstance.isInside(entity));
        instances.removeIf(auraInstance -> !auraInstance.getAura().value().primary(entity, auraInstance.getOwner()));

        List<AuraInstance> unique = Lists.newArrayList();
        while (!instances.isEmpty()) {
            AuraInstance instance = instances.getFirst();
            AuraInstance busyInstance = null;
            boolean sameAura = false;
            for (AuraInstance tmAuraInstance : unique) {
                if (tmAuraInstance.getAura().value() == instance.getAura()) {
                    sameAura = true;
                    busyInstance = tmAuraInstance;
                    break;
                }
            }
            if (sameAura) {
                boolean secCond = instance.getAura().value().secondary(entity, instance.getOwner());
                boolean secCondBusy = busyInstance.getAura().value().secondary(entity, instance.getOwner());
                if (!secCondBusy && secCond) {
                    unique.remove(busyInstance);
                    unique.add(instance);
                }
            } else unique.add(instance);

            instances.remove(instance);
        }

        List<AuraInstance> finalList = Lists.newArrayList();
        if (owned) finalList.addAll(ownedAuras);
        finalList.addAll(unique);

        return finalList;
    }

    public static List<AuraInstance> getAffectingAuras(@NotNull Entity entity) {
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesHelper.takeHolder(entity.level(), AurasInstancesHolder.class);
        List<AuraInstance> auras = Lists.newArrayList();
        if (aurasInstancesHolder == null) return auras;
        auras.addAll(aurasInstancesHolder.getAuraInstances());
        return sortUniqueAura(auras, entity, false);
    }

    public static List<AuraInstance> getAllAffectingAuras(@NotNull Entity entity) {
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesHelper.takeHolder(entity.level(), AurasInstancesHolder.class);
        List<AuraInstance> auras = Lists.newArrayList();
        if (aurasInstancesHolder == null) return auras;
        auras.addAll(aurasInstancesHolder.getAuraInstances());
        return sortUniqueAura(auras, entity, true);
    }

    public static boolean affected(Entity entity, BasicAura aura) {
        for (AuraInstance instance : getAffectingAuras(entity)) if (instance.getAura() == aura) return true;
        return false;
    }

    public static  boolean hasImmunityTo(@NotNull Entity entity, Holder<DamageType> holder) {
        for (AuraInstance auraInstance : getAffectingAuras(entity)) if (auraInstance.getAura().value().immunityTo(holder)) return true;
        return false;
    }

    public static float getDamageMultiplier(@NotNull Entity entity, Holder<DamageType> holder) {
        float d0 = 1f;
        for (AuraInstance auraInstance : getAffectingAuras(entity)) d0 *= auraInstance.getAura().value().multiplierFor(holder);
        return d0;
    }

    public static boolean ownsAura(@NotNull Entity entity, Holder<BasicAura> basicAura) {
        return ownedAuraInstance(entity, basicAura) != null;
    }

    public static @Nullable AuraInstance ownedAuraInstance(@NotNull Entity entity, Holder<BasicAura> basicAura) {
        List<AuraInstance> auraInstances = ownedAuras(entity);
        for (AuraInstance auraInstance : auraInstances) if (auraInstance.getAura().value() == basicAura.value()) return auraInstance;
        return null;
    }

    public static @NotNull List<AuraInstance> ownedAuras(@NotNull Entity entity) {
        List<AuraInstance> auraInstances = Lists.newArrayList();
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesHelper.takeHolder(entity.level(), AurasInstancesHolder.class);
        if (aurasInstancesHolder == null) return auraInstances;

        for (AuraInstance auraInstance : aurasInstancesHolder.getAuraInstances()) if (auraInstance.getOwner() == entity) auraInstances.add(auraInstance);
        return auraInstances;
    }

    public static void auraTick(Entity entity, AuraInstance auraInstance) {
        if (!auraInstance.getAura().value().affects(entity, auraInstance.getOwner())) return;
        if (entity instanceof LivingEntity livingEntity) for (Map.Entry<Holder<MobEffect>, Integer> entry : auraInstance.getAura().value().getMobEffects().entrySet()) LivingEffectHelper.effectWithoutParticles(livingEntity, entry.getKey(), 2, entry.getValue());
    }

    public static void aurasTick(Entity entity) {
        for (AuraInstance auraInstance : AuraHelper.getAffectingAuras(entity)) auraTick(entity, auraInstance);
    }
}
