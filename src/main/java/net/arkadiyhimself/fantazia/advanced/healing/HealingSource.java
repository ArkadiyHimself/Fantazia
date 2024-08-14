package net.arkadiyhimself.fantazia.advanced.healing;

import net.arkadiyhimself.fantazia.util.library.RandomList;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class HealingSource {
    private final Holder<HealingType> type;
    private boolean noParticles = false;
    @Nullable
    private final Entity entity;
    @Override
    public String toString() {
        return "HealingSource (" + this.type().id() + ")";
    }
    public float getExhaustion() {
        return this.type().exhaustion();
    }
    public HealingSource(Holder<HealingType> type, @Nullable Entity entity) {
        this.type = type;
        this.entity = entity;
    }
    public HealingSource(Holder<HealingType> type) {
        this(type, null);
    }
    public HealingSource setNoParticles() {
        this.noParticles = true;
        return this;
    }
    public boolean noParticles() {
        return noParticles;
    }
    @Nullable
    public Entity getEntity() {
        return entity;
    }
    public String id() {
        return this.type().id();
    }
    public boolean is(TagKey<HealingType> healingTypeTagKey) {
        return this.type.is(healingTypeTagKey);
    }
    public boolean is(ResourceKey<HealingType> healingTypeTagKey) {
        return this.type.is(healingTypeTagKey);
    }
    public HealingType type() {
        return this.type.value();
    }
    public RandomList<SimpleParticleType> particleTypes() {
        RandomList<SimpleParticleType> types = RandomList.emptyRandomList();
        RandomList<ResourceLocation> resourceLocations = this.type().particleTypes();
        for (ResourceLocation resourceLocation : resourceLocations) if (ForgeRegistries.PARTICLE_TYPES.containsKey(resourceLocation) && ForgeRegistries.PARTICLE_TYPES.getValue(resourceLocation) instanceof SimpleParticleType simpleParticleType) types.add(simpleParticleType);
        return types;
    }
}
