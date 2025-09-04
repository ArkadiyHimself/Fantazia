package net.arkadiyhimself.fantazia.common.advanced.aura;

import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.AurasInstancesHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuraHelper {

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

    public static void handleAuraAffectingOnTick(@NotNull Entity entity) {
        AurasInstancesHolder holder = LevelAttributesHelper.takeHolder(entity.level(), AurasInstancesHolder.class);
        if (holder == null) return;
        List<AuraInstance> levelAuraInstances = holder.getAuraInstances();
        levelAuraInstances.removeIf(auraInstance -> !auraInstance.isInside(entity) || auraInstance.getOwner() == entity);
        Map<Holder<Aura>, AuraInstance> affectingAuras = entity.getData(FTZAttachmentTypes.AFFECTING_AURAS);

        // remove auras that no longer entity leaves
        for (Holder<Aura> auraHolder : affectingAuras.keySet().stream().toList()) {
            AuraInstance instance = affectingAuras.get(auraHolder);
            if (instance.getOwner().isRemoved()) instance.discard();

            if (!instance.isInside(entity) || instance.removed()) {
                instance.unaffectedByAura(entity);
                affectingAuras.remove(auraHolder);
            }
        }

        for (AuraInstance auraInstance : levelAuraInstances) {
            Holder<Aura> auraHolder = auraInstance.getAura();
            if (!affectingAuras.containsKey(auraHolder)) {
                affectingAuras.put(auraHolder, auraInstance);
                continue;
            }

            AuraInstance presentInstance = affectingAuras.get(auraHolder);
            if (!auraInstance.matchesFilter(entity) || presentInstance == auraInstance) continue;

            if (!presentInstance.matchesFilter(entity) || presentInstance.distanceToCenter(entity) > auraInstance.distanceToCenter(entity)) {
                affectingAuras.replace(auraHolder, auraInstance);
                if (entity instanceof LivingEntity livingEntity) auraInstance.affectedByAura(livingEntity);
            }
        }

        for (AuraInstance unaffectingAura : affectingAuras.values().stream().filter(auraInstance -> !auraInstance.matchesFilter(entity)).toList()) {
            unaffectingAura.unaffectedByAura(entity);
        }

        for (AuraInstance affectingAura : affectingAuras.values().stream().filter(auraInstance -> auraInstance.matchesFilter(entity)).toList()) {
            affectingAura.tickOnEntity(entity);
            affectingAura.affectedByAura(entity);
            if (entity instanceof LivingEntity livingEntity) affectingAura.applyMobEffects(livingEntity);
        }
    }

    public static List<AuraInstance> getAurasForGui(LocalPlayer localPlayer) {
        List<AuraInstance> auraInstances = Lists.newArrayList();
        auraInstances.addAll(ownedAuras(localPlayer));
        auraInstances.addAll(localPlayer.getData(FTZAttachmentTypes.AFFECTING_AURAS).values());
        return auraInstances;
    }

    public static List<AuraInstance> ownedAuras(@NotNull Entity entity) {
        AurasInstancesHolder holder = LevelAttributesHelper.takeHolder(entity.level(), AurasInstancesHolder.class);
        List<AuraInstance> owned = Lists.newArrayList();
        if (holder == null) return owned;
        for (AuraInstance auraInstance : holder.getAuraInstances()) if (auraInstance.getOwner() == entity) owned.add(auraInstance);
        return owned;
    }

    public static List<AuraInstance> getAffectingAuras(@NotNull Entity entity) {
        List<AuraInstance> auraInstances = new ArrayList<>(entity.getData(FTZAttachmentTypes.AFFECTING_AURAS).values());
        auraInstances.removeIf(auraInstance -> !auraInstance.matchesFilter(entity));
        return auraInstances;
    }

    public static boolean affected(Entity entity, Aura aura) {
        for (AuraInstance instance : getAffectingAuras(entity)) if (instance.getAura() == aura) return true;
        return false;
    }

    public static boolean hasImmunityTo(@NotNull Entity entity, Holder<DamageType> holder) {
        for (AuraInstance auraInstance : getAffectingAuras(entity)) if (auraInstance.getAura().value().immunityTo(holder)) return true;
        return false;
    }

    public static float getDamageMultiplier(@NotNull Entity entity, Holder<DamageType> holder) {
        float d0 = 1f;
        for (AuraInstance auraInstance : getAffectingAuras(entity)) d0 *= auraInstance.getAura().value().multiplierFor(holder);
        return d0;
    }

    public static boolean ownsAura(@NotNull Entity entity, Holder<Aura> basicAura) {
        return ownedAuraInstance(entity, basicAura) != null;
    }

    public static @Nullable AuraInstance ownedAuraInstance(@NotNull Entity entity, Holder<Aura> basicAura) {
        List<AuraInstance> auraInstances = ownedAuras(entity);
        for (AuraInstance auraInstance : auraInstances) if (auraInstance.getAura().value() == basicAura.value()) return auraInstance;
        return null;
    }
}
