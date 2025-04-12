package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesGetter;
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
    public static List<AuraInstance<? extends Entity>> sortUniqueAura(List<AuraInstance<? extends Entity>> instances, @NotNull Entity entity, boolean owned) {
        List<AuraInstance<? extends Entity>> ownedAuras = Lists.newArrayList();
        for (AuraInstance<? extends Entity> auraInstance : instances) if (auraInstance.getOwner() == entity) ownedAuras.add(auraInstance);
        instances.removeIf(auraInstance -> auraInstance.getOwner() == entity);

        instances.removeIf(auraInstance -> !auraInstance.isInside(entity));
        instances.removeIf(auraInstance -> !auraInstance.getAura().affectedClass().isInstance(entity) && !Fantazia.DEVELOPER_MODE);
        instances.removeIf(auraInstance -> !auraInstance.getAura().primary(entity, auraInstance.getOwner()) && !Fantazia.DEVELOPER_MODE);

        List<AuraInstance<? extends Entity>> unique = Lists.newArrayList();
        while (!instances.isEmpty()) {
            AuraInstance<? extends Entity> instance = instances.getFirst();
            AuraInstance<? extends Entity> busyInstance = null;
            boolean sameAura = false;
            for (AuraInstance<? extends Entity> tmAuraInstance : unique) {
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
            } else unique.add(instance);

            instances.remove(instance);
        }

        List<AuraInstance<? extends Entity>> finalList = Lists.newArrayList();
        if (owned) finalList.addAll(ownedAuras);
        finalList.addAll(unique);

        return finalList;
    }

    public static  List<AuraInstance<? extends Entity>> getAffectingAuras(@NotNull Entity entity) {
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesGetter.takeHolder(entity.level(), AurasInstancesHolder.class);
        List<AuraInstance<? extends Entity>> auras = Lists.newArrayList();
        if (aurasInstancesHolder == null) return auras;
        auras.addAll(aurasInstancesHolder.getAuraInstances());
        return sortUniqueAura(auras, entity, false);
    }

    public static List<AuraInstance<? extends Entity>> getAllAffectingAuras(@NotNull Entity entity) {
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesGetter.takeHolder(entity.level(), AurasInstancesHolder.class);
        List<AuraInstance<? extends Entity>> auras = Lists.newArrayList();
        if (aurasInstancesHolder == null) return auras;
        auras.addAll(aurasInstancesHolder.getAuraInstances());
        return sortUniqueAura(auras, entity, true);
    }

    public static boolean affected(Entity entity, BasicAura<?> aura) {
        for (AuraInstance<?> instance : getAffectingAuras(entity)) if (instance.getAura() == aura) return true;
        return false;
    }

    public static  boolean hasImmunityTo(@NotNull Entity entity, Holder<DamageType> holder) {
        for (AuraInstance<? extends Entity> auraInstance : getAffectingAuras(entity)) if (auraInstance.getAura().immunityTo(holder)) return true;
        return false;
    }

    public static float getDamageMultiplier(@NotNull Entity entity, Holder<DamageType> holder) {
        float d0 = 1f;
        for (AuraInstance<? extends Entity> auraInstance : getAffectingAuras(entity)) d0 *= auraInstance.getAura().multiplierFor(holder);
        return d0;
    }

    public static @Nullable AuraInstance<? extends Entity> ownedAuraInstance(@NotNull Entity entity, BasicAura<? extends Entity> basicAura) {
        List<AuraInstance<? extends Entity>> auraInstances = ownedAuras(entity);
        for (AuraInstance<? extends Entity> auraInstance : auraInstances) if (auraInstance.getAura() == basicAura) return auraInstance;
        return null;
    }

    public static @NotNull List<AuraInstance<? extends Entity>> ownedAuras(@NotNull Entity entity) {
        List<AuraInstance<? extends Entity>> auraInstances = Lists.newArrayList();
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesGetter.takeHolder(entity.level(), AurasInstancesHolder.class);
        if (aurasInstancesHolder == null) return auraInstances;

        for (AuraInstance<? extends Entity> auraInstance : aurasInstancesHolder.getAuraInstances()) if (auraInstance.getOwner() == entity) auraInstances.add(auraInstance);
        return auraInstances;
    }

    public static void auraTick(Entity entity, AuraInstance<? extends Entity> auraInstance) {
        BasicAura<? extends Entity> basicAura = auraInstance.getAura();
        if (!basicAura.canAffect(entity, auraInstance.getOwner())) return;
        if (entity instanceof LivingEntity livingEntity) for (Map.Entry<Holder<MobEffect>, Integer> entry : basicAura.getMobEffects().entrySet()) LivingEffectHelper.effectWithoutParticles(livingEntity, entry.getKey(), 2, entry.getValue());
    }

    public static void aurasTick(Entity entity) {
        for (AuraInstance<? extends Entity> auraInstance : AuraHelper.getAffectingAuras(entity)) auraTick(entity, auraInstance);
    }
}
