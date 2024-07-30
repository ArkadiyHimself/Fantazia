package net.arkadiyhimself.fantazia.api.capability.entity.feature;

import net.arkadiyhimself.fantazia.api.capability.INBTwrite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;


public class FeatureHolder implements INBTwrite {
    private final Entity entity;
    public FeatureHolder(Entity entity) {
        this.entity = entity;
    }
    @Override
    public CompoundTag serialize() {
        return new CompoundTag();
    }

    @Override
    public void deserialize(CompoundTag tag) {
    }
    public Entity getEntity() {
        return entity;
    }
    public void onDeath() {}

}
