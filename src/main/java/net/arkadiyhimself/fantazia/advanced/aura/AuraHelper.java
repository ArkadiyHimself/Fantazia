package net.arkadiyhimself.fantazia.advanced.aura;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesGetter;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.AurasInstancesHolder;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.core.Holder;
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
    private AuraHelper() {}

    // sorts a list of aura instances with a complicated algorithm, removing an aura instance if entity doesn't match Primary Conditions and then prioritising instances where entity matches Secondary Conditions
    public static <T extends Entity> List<AuraInstance<T>> sortUniqueAura(List<AuraInstance<T>> instances, @NotNull T entity) {
        instances.removeIf(auraInstance -> auraInstance.notInside(entity));
        instances.removeIf(auraInstance -> !auraInstance.getAura().affectedClass().isInstance(entity) && !Fantazia.DEVELOPER_MODE);
        instances.removeIf(auraInstance -> !auraInstance.getAura().couldAffect(entity, auraInstance.getOwner()) && !Fantazia.DEVELOPER_MODE);
        List<AuraInstance<T>> unique = Lists.newArrayList();
        while (!instances.isEmpty()) {
            AuraInstance<T> instance = instances.getFirst();
            AuraInstance<T> busyInstance = null;
            boolean sameAura = false;
            for (AuraInstance<T> tmAuraInstance : unique) {
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
        return unique;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> List<AuraInstance<T>> getAffectingAuras(@NotNull T entity) {
        AurasInstancesHolder aurasInstancesHolder = LevelAttributesGetter.takeHolder(entity.level(), AurasInstancesHolder.class);
        List<AuraInstance<T>> auras = Lists.newArrayList();
        if (aurasInstancesHolder == null) return auras;
        for (AuraInstance<? extends Entity> auraInstance : aurasInstancesHolder.getAuraInstances()) if (auraInstance.getAura().affectedClass().isInstance(entity)) auras.add((AuraInstance<T>) auraInstance);
        return sortUniqueAura(auras, entity);
    }

    public static boolean affected(Entity entity, BasicAura<?> aura) {
        for (AuraInstance<?> instance : getAffectingAuras(entity)) if (instance.getAura() == aura) return true;
        return false;
    }

    public static <T extends Entity> boolean hasImmunityTo(@NotNull T entity, Holder<DamageType> holder) {
        for (AuraInstance<T> auraInstance : getAffectingAuras(entity)) if (auraInstance.getAura().immunityTo(holder)) return true;
        return false;
    }

    public static float getDamageMultiplier(@NotNull Entity entity, Holder<DamageType> holder) {
        float d0 = 1f;
        for (AuraInstance<Entity> auraInstance : getAffectingAuras(entity)) d0 *= auraInstance.getAura().multiplierFor(holder);
        return d0;
    }

    public static <T extends Entity> Map<ResourceKey<DamageType>, Float> damageMultipliers(@NotNull T entity) {
        HashMap<ResourceKey<DamageType>, Float> damageMultiply = Maps.newHashMap();
        List<AuraInstance<T>> auraInstances = getAffectingAuras(entity);

        for (AuraInstance<T> auraInstance : auraInstances)
            for (Map.Entry<ResourceKey<DamageType>, Float> entry : auraInstance.getAura().multipliers().entrySet())
                if (damageMultiply.containsKey(entry.getKey())) {
                    float multi1 = entry.getValue();
                    float multi2 = damageMultiply.get(entry.getKey());
                    damageMultiply.replace(entry.getKey(), multi1 * multi2);
                } else damageMultiply.put(entry.getKey(), entry.getValue());

        return damageMultiply;
    }

    public static void auraTick(Entity entity, AuraInstance<Entity> auraInstance) {
        BasicAura<Entity> basicAura = auraInstance.getAura();
        if (!basicAura.canAffect(entity, auraInstance.getOwner())) return;
        if (entity instanceof LivingEntity livingEntity) for (Map.Entry<Holder<MobEffect>, Integer> entry : basicAura.getMobEffects().entrySet()) LivingEffectHelper.effectWithoutParticles(livingEntity, entry.getKey(), 2, entry.getValue());
    }

    public static void aurasTick(Entity entity) {
        for (AuraInstance<Entity> auraInstance : AuraHelper.getAffectingAuras(entity)) auraTick(entity, auraInstance);
    }
}
