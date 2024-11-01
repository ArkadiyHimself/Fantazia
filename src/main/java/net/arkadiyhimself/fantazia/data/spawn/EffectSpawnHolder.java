package net.arkadiyhimself.fantazia.data.spawn;

import com.google.common.collect.ImmutableList;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class EffectSpawnHolder {

    private final ImmutableList<EntityType<?>> entityTypes;
    private final ImmutableList<MobEffectSpawnInstance> mobEffectSpawnInstances;
    private final ImmutableList<AuraSpawnInstance> auraSpawnInstances;

    public EffectSpawnHolder(List<EntityType<?>> entityTypes, List<MobEffectSpawnInstance> mobEffectSpawnInstances, List<AuraSpawnInstance> auraSpawnInstances) {
        this.entityTypes = ImmutableList.copyOf(entityTypes);
        this.mobEffectSpawnInstances = ImmutableList.copyOf(mobEffectSpawnInstances);
        this.auraSpawnInstances = ImmutableList.copyOf(auraSpawnInstances);
    }
    public boolean isAffected(LivingEntity livingEntity) {
        return entityTypes.contains(livingEntity.getType());
    }

    public void tryAddEffects(LivingEntity livingEntity) {
        for (MobEffectSpawnInstance mobEffectSpawnInstance : mobEffectSpawnInstances) mobEffectSpawnInstance.tryAddEffects(livingEntity);
        for (AuraSpawnInstance auraSpawnInstance : auraSpawnInstances) auraSpawnInstance.tryAddAura(livingEntity);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ListTag entityTypesTag = new ListTag();
        for (EntityType<?> entityType : entityTypes) {
            ResourceLocation entityID = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            entityTypesTag.add(StringTag.valueOf(entityID.toString()));
        }
        tag.put("entity_types", entityTypesTag);

        ListTag effectSpawnInstancesTag = new ListTag();
        for (MobEffectSpawnInstance mobEffectSpawnInstance : mobEffectSpawnInstances) effectSpawnInstancesTag.add(mobEffectSpawnInstance.serialize());
        tag.put("effect_instances", effectSpawnInstancesTag);

        ListTag auraSpawnInstancesTag = new ListTag();
        for (AuraSpawnInstance auraSpawnInstance : auraSpawnInstances) auraSpawnInstancesTag.add(auraSpawnInstance.serialize());
        tag.put("aura_instances", auraSpawnInstancesTag);

        return tag;
    }
    public static EffectSpawnHolder deserialize(CompoundTag tag) {
        List<EntityType<?>> entities = Lists.newArrayList();
        ListTag entityTypes = tag.getList("entity_types", Tag.TAG_STRING);
        for (int i = 0; i < entityTypes.size(); i++) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityTypes.getString(i)));
            entities.add(entityType);
        }

        List<MobEffectSpawnInstance> mobEffectInstances = Lists.newArrayList();
        ListTag effectInstancesTag = tag.getList("effect_instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < effectInstancesTag.size(); i++) mobEffectInstances.add(MobEffectSpawnInstance.deserialize(effectInstancesTag.getCompound(i)));

        List<AuraSpawnInstance> auraInstances = Lists.newArrayList();
        ListTag auraInstancesTag = tag.getList("aura_instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < auraInstancesTag.size(); i++) auraInstances.add(AuraSpawnInstance.deserialize(auraInstancesTag.getCompound(i)));

        return new EffectSpawnHolder(entities, mobEffectInstances, auraInstances);
    }
    public static class Builder {

        private final List<EntityType<?>> entityTypes = Lists.newArrayList();

        private final ChaoticHierarchy<MobEffectSpawnInstance.Builder> effectSpawnInstances = new ChaoticHierarchy<>();
        private final ChaoticHierarchy<AuraSpawnInstance.Builder> auraSpawnInstances = new ChaoticHierarchy<>();

        public void addEntityType(ResourceLocation location) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(location);
            entityTypes.add(entityType);
        }

        public void addEffectInstance(Holder<MobEffect> effect, double chance, int level, boolean hidden) {
            effectSpawnInstances.addElement(new MobEffectSpawnInstance.Builder(effect, chance, level, hidden));
        }

        public void addAuraInstance(Holder<BasicAura<?>> aura, double chance) {
            auraSpawnInstances.addElement(new AuraSpawnInstance.Builder(aura, chance));
        }

        public EffectSpawnHolder build() {
            return new EffectSpawnHolder(entityTypes,
                    effectSpawnInstances.transform(MobEffectSpawnInstance.Builder::build).getElements(),
                    auraSpawnInstances.transform(AuraSpawnInstance.Builder::build).getElements()
            );
        }
    }
}
