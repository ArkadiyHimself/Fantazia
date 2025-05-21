package net.arkadiyhimself.fantazia.data.spawn_effect;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.datagen.effect_spawn_applier.EffectSpawnApplierHolder;
import net.arkadiyhimself.fantazia.util.library.concept_of_consistency.ConCosInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public record EffectSpawnApplier(ImmutableList<EntityType<?>> entityTypes, ImmutableMap<ConCosInstance, CombinedSpawnEffects> combinedSpawnEffects, boolean multiple) {

    public EffectSpawnApplier(List<EntityType<?>> entityTypes, Map<ConCosInstance, CombinedSpawnEffects> combinedSpawnEffects, boolean multiple) {
        this(ImmutableList.copyOf(entityTypes), ImmutableMap.copyOf(combinedSpawnEffects), multiple);
    }

    public boolean isAffected(Mob mob) {
        return entityTypes.contains(mob.getType());
    }

    public void tryAddEffects(Mob mob) {
        for (Map.Entry<ConCosInstance, CombinedSpawnEffects> entry : combinedSpawnEffects.entrySet()) {
            if (!entry.getKey().performAttempt()) continue;
            entry.getValue().addEffects(mob);
            if (!multiple) return;
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ListTag entityTypesTag = new ListTag();
        for (EntityType<?> entityType : entityTypes) {
            ResourceLocation entityID = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
            entityTypesTag.add(StringTag.valueOf(entityID.toString()));
        }
        tag.put("entity_types", entityTypesTag);

        ListTag effectsTag = new ListTag();
        for (Map.Entry<ConCosInstance, CombinedSpawnEffects> entry : combinedSpawnEffects.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.put("random", entry.getKey().serialize());
            entryTag.put("instance", entry.getValue().serialize());

            effectsTag.add(entryTag);
        }
        tag.put("spawn_effects", effectsTag);
        tag.putBoolean("multiple", multiple);

        return tag;
    }

    public static EffectSpawnApplier deserialize(CompoundTag tag) {
        List<EntityType<?>> entities = Lists.newArrayList();
        ListTag entityTypes = tag.getList("entity_types", Tag.TAG_STRING);
        for (int i = 0; i < entityTypes.size(); i++) {
            ResourceLocation id = ResourceLocation.parse(entityTypes.getString(i));
            if (!BuiltInRegistries.ENTITY_TYPE.containsKey(id)) continue;
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(id);
            entities.add(entityType);
        }

        Map<ConCosInstance, CombinedSpawnEffects> combinedSpawnEffectsMap = Maps.newHashMap();
        ListTag effectsTag = tag.getList("spawn_effects", Tag.TAG_COMPOUND);
        for (int i = 0; i < effectsTag.size(); i++) {
            CompoundTag entryTag = effectsTag.getCompound(i);

            ConCosInstance conCosInstance = ConCosInstance.deserialize(entryTag.getCompound("random"));
            CombinedSpawnEffects combinedSpawnEffects = CombinedSpawnEffects.deserialize(entryTag.getCompound("instance"));

            combinedSpawnEffectsMap.put(conCosInstance, combinedSpawnEffects);
        }

        boolean multiple = tag.getBoolean("multiple");

        return new EffectSpawnApplier(entities, combinedSpawnEffectsMap, multiple);
    }

    public static Builder builder(boolean multiple) {
        return new Builder(Lists.newArrayList(), Lists.newArrayList(), multiple);
    }

    public record Builder(List<EntityType<?>> entityTypes, @NotNull List<CombinedSpawnEffects.Builder> combinedEffects, boolean multiple) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.<Builder>create(instance -> instance.group(
                BuiltInRegistries.ENTITY_TYPE.byNameCodec().listOf().fieldOf("entity_types").forGetter(Builder::entityTypes),
                CombinedSpawnEffects.Builder.CODEC.listOf().optionalFieldOf("spawn_effects", Lists.newArrayList()).forGetter(Builder::combinedEffects),
                Codec.BOOL.optionalFieldOf("multiple", false).forGetter(Builder::multiple)
        ).apply(instance, Builder::new)).validate(Builder::validate);

        private static DataResult<Builder> validate(Builder builder) {
            if (builder.entityTypes.isEmpty()) return DataResult.error(() -> "Effect spawn applier has no entity types!");
            if (builder.combinedEffects.isEmpty()) return DataResult.error(() -> "Effect spawn applier has no spawn effects!");
            return DataResult.success(builder);
        }

        public Builder addEntityTypes(EntityType<?>... entityType) {
            entityTypes.addAll(Arrays.stream(entityType).toList());
            return this;
        }

        public Builder addEffects(CombinedSpawnEffects.Builder builder) {
            combinedEffects.add(builder);
            return this;
        }

        public EffectSpawnApplier build() {
            Map<ConCosInstance, CombinedSpawnEffects> combined = Maps.newHashMap();
            for (CombinedSpawnEffects.Builder builder : combinedEffects()) combined.put(new ConCosInstance(builder.chance()), builder.build());

            return new EffectSpawnApplier(entityTypes, combined, multiple);
        }

        public EffectSpawnApplierHolder holder(ResourceLocation id) {
            return new EffectSpawnApplierHolder(id, this);
        }

        public void save(Consumer<EffectSpawnApplierHolder> output, ResourceLocation id) {
            output.accept(holder(id));
        }
    }
}
