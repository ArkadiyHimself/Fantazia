package net.arkadiyhimself.fantazia.data.spawn;

import com.google.common.collect.ImmutableList;
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
    private final ImmutableList<EffectSpawnInstance> effectSpawnInstances;

    public EffectSpawnHolder(List<EntityType<?>> entityTypes, List<EffectSpawnInstance> effectSpawnInstances) {
        this.entityTypes = ImmutableList.copyOf(entityTypes);
        this.effectSpawnInstances = ImmutableList.copyOf(effectSpawnInstances);
    }
    public boolean isAffected(LivingEntity livingEntity) {
        return entityTypes.contains(livingEntity.getType());
    }
    public void tryAddEffects(LivingEntity livingEntity) {
        for (EffectSpawnInstance effectSpawnInstance : effectSpawnInstances) effectSpawnInstance.tryAddEffects(livingEntity);
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
        for (EffectSpawnInstance effectSpawnInstance : effectSpawnInstances) effectSpawnInstancesTag.add(effectSpawnInstance.serialize());
        tag.put("effect_instances", effectSpawnInstancesTag);

        return tag;
    }
    public static EffectSpawnHolder deserialize(CompoundTag tag) {
        List<EntityType<?>> entities = Lists.newArrayList();
        ListTag entityTypes = tag.getList("entity_types", Tag.TAG_STRING);
        for (int i = 0; i < entityTypes.size(); i++) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(entityTypes.getString(i)));
            entities.add(entityType);
        }

        List<EffectSpawnInstance> instances = Lists.newArrayList();
        ListTag effectInstances = tag.getList("effect_instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < effectInstances.size(); i++) instances.add(EffectSpawnInstance.deserialize(effectInstances.getCompound(i)));

        return new EffectSpawnHolder(entities, instances);
    }
    public static class Builder {
        private final List<EntityType<?>> entityTypes = Lists.newArrayList();
        private final ChaoticHierarchy<EffectSpawnInstance.Builder> effectSpawnInstances = new ChaoticHierarchy<>();
        public void addEntityType(ResourceLocation location) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(location);
            entityTypes.add(entityType);
        }
        public void addEffectInstance(Holder<MobEffect> effect, double chance, int level, boolean hidden) {
            effectSpawnInstances.addElement(new EffectSpawnInstance.Builder(effect, chance, level, hidden));
        }
        public EffectSpawnHolder build() {
            return new EffectSpawnHolder(entityTypes, effectSpawnInstances.transform(EffectSpawnInstance.Builder::build).getElements());
        }
    }
}
