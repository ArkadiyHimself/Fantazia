package net.arkadiyhimself.fantazia.advanced.healing;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.util.Arrays;
import java.util.List;

public class HealingType {
    private final List<HealingTag> tags;
    private final float exhaustion;
    private final ResourceLocation id;
    private final List<RegistryObject<SimpleParticleType>> regParticleType = Lists.newArrayList();
    private final List<SimpleParticleType> particleType = Lists.newArrayList();
    public HealingType(float exhaustion, ResourceLocation id, HealingTag... healingTags) {
        this.exhaustion = exhaustion;
        this.id = id;
        this.tags = Arrays.stream(healingTags).toList();
    }
    public List<HealingTag> getTags() {
        return tags;
    }
    public float getExhaustion() {
        return exhaustion;
    }
    public ResourceLocation getId() {
        return id;
    }
    public List<SimpleParticleType> getParticleTypes() {
        return particleType;
    }
    public List<RegistryObject<SimpleParticleType>> getRegParticleTypes() {
        return regParticleType;
    }
    public HealingType setParticles(List<SimpleParticleType> particles) {
        this.particleType.addAll(particles);
        return this;
    }
    public HealingType setParticle(SimpleParticleType particle) {
        this.particleType.add(particle);
        return this;
    }
}
