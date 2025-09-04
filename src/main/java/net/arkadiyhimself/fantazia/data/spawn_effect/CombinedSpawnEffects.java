package net.arkadiyhimself.fantazia.data.spawn_effect;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public record CombinedSpawnEffects(ImmutableList<AuraSpawnInstance> auraSpawnInstances, ImmutableList<MobEffectSpawnInstance> mobEffectSpawnInstances) implements ISpawnEffectApplier {

    public CombinedSpawnEffects(List<AuraSpawnInstance> auraSpawnInstances, List<MobEffectSpawnInstance> mobEffectSpawnInstances) {
        this(ImmutableList.copyOf(auraSpawnInstances), ImmutableList.copyOf(mobEffectSpawnInstances));
    }

    @Override
    public void addEffects(LivingEntity entity) {
        for (AuraSpawnInstance auraSpawnInstance : auraSpawnInstances) auraSpawnInstance.addEffects(entity);
        for (MobEffectSpawnInstance mobEffectSpawnInstance : mobEffectSpawnInstances) mobEffectSpawnInstance.addEffects(entity);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ListTag auraInstancesTag = new ListTag();
        for (AuraSpawnInstance auraSpawnInstance : auraSpawnInstances) auraInstancesTag.add(auraSpawnInstance.serialize());

        tag.put("aura_instances", auraInstancesTag);

        ListTag mobEffectInstancesTag = new ListTag();
        for (MobEffectSpawnInstance mobEffectSpawnInstance : mobEffectSpawnInstances) mobEffectInstancesTag.add(mobEffectSpawnInstance.serialize());

        tag.put("mob_effect_instances", mobEffectInstancesTag);

        return tag;
    }

    public static CombinedSpawnEffects deserialize(CompoundTag tag) {
        List<AuraSpawnInstance> auraSpawnInstances = Lists.newArrayList();
        ListTag auraInstancesTag = tag.getList("aura_instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < auraInstancesTag.size(); i++) {
            CompoundTag entry = auraInstancesTag.getCompound(i);
            AuraSpawnInstance auraSpawnInstance = AuraSpawnInstance.deserialize(entry);
            auraSpawnInstances.add(auraSpawnInstance);
        }

        List<MobEffectSpawnInstance> mobEffectSpawnInstances = Lists.newArrayList();
        ListTag mobEffectInstancesTag = tag.getList("mob_effect_instances", Tag.TAG_COMPOUND);
        for (int i = 0; i < mobEffectInstancesTag.size(); i++) {
            CompoundTag entry = mobEffectInstancesTag.getCompound(i);
            MobEffectSpawnInstance mobEffectSpawnInstance = MobEffectSpawnInstance.deserialize(entry);
            mobEffectSpawnInstances.add(mobEffectSpawnInstance);
        }

        return new CombinedSpawnEffects(auraSpawnInstances, mobEffectSpawnInstances);
    }

    public static Builder builder(double chance) {
        return new Builder(chance, Lists.newArrayList(), Lists.newArrayList());
    }

    public record Builder(double chance, List<AuraSpawnInstance.Builder> auras, List<MobEffectSpawnInstance.Builder> mobEffects) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("chance").forGetter(Builder::chance),
                AuraSpawnInstance.Builder.CODEC.listOf().optionalFieldOf("aura_instances", Lists.newArrayList()).forGetter(Builder::auras),
                MobEffectSpawnInstance.Builder.CODEC.listOf().optionalFieldOf("mob_effect_instances", Lists.newArrayList()).forGetter(Builder::mobEffects)
        ).apply(instance, Builder::new));

        public Builder addAuraInstance(Holder<Aura> aura, int amplifier) {
            auras.add(new AuraSpawnInstance.Builder(aura, amplifier));
            return this;
        }

        public Builder addAuraInstance(Holder<Aura> aura) {
            return addAuraInstance(aura, 0);
        }

        public Builder addMobEffectInstance(Holder<MobEffect> effect, int amplifier, boolean hidden) {
            mobEffects.add(new MobEffectSpawnInstance.Builder(effect, amplifier, hidden));
            return this;
        }

        public Builder addMobEffectInstance(Holder<MobEffect> effect, int amplifier) {
            return addMobEffectInstance(effect, amplifier, false);
        }

        public Builder addMobEffectInstance(Holder<MobEffect> effect, boolean hidden) {
            return addMobEffectInstance(effect, 0, hidden);
        }

        public Builder addMobEffectInstance(Holder<MobEffect> effect) {
            return addMobEffectInstance(effect, 0, false);
        }

        public CombinedSpawnEffects build() {
            List<AuraSpawnInstance> auraSpawnInstances = ChaoticHierarchy.of(auras).transform(AuraSpawnInstance.Builder::build).toList();
            List<MobEffectSpawnInstance> mobEffectSpawnInstances = ChaoticHierarchy.of(mobEffects).transform(MobEffectSpawnInstance.Builder::build).toList();
            return new CombinedSpawnEffects(auraSpawnInstances, mobEffectSpawnInstances);
        }
    }
}
