package net.arkadiyhimself.fantazia.advanced.healing;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

public class HealingSource {
    private final HealingType type;
    @Nullable
    private final Entity causingEntity;
    @Nullable
    private final Entity directEntity;
    @Nullable
    private final SimpleParticleType customParticle;
    private boolean noParticles = false;
    public HealingSource(HealingType type) {
        this.type = type;
        this.causingEntity = null;
        this.directEntity = null;
        this.customParticle = null;
    }
    public HealingSource(HealingType type, Entity causingEntity) {
        this.type = type;
        this.causingEntity = causingEntity;
        this.directEntity = null;
        this.customParticle = null;
    }
    public HealingSource(HealingType type, Entity causingEntity, Entity directEntity) {
        this.type = type;
        this.causingEntity = causingEntity;
        this.directEntity = directEntity;
        this.customParticle = null;
    }
    public HealingSource(HealingType type, Entity causingEntity, Entity directEntity, SimpleParticleType particleType) {
        this.type = type;
        this.causingEntity = causingEntity;
        this.directEntity = directEntity;
        this.customParticle = particleType;
    }
    public HealingType getType() {
        return type;
    }
    @Nullable
    public SimpleParticleType getCustomParticle() {
        return customParticle;
    }
    @Nullable
    public Entity getCausingEntity() {
        return causingEntity;
    }
    @Nullable
    public Entity getDirectEntity() {
        return directEntity;
    }
    public HealingSource setNoParticles() {
        this.noParticles = true;
        return this;
    }
    public boolean noParticles() {
        return noParticles;
    }
}
