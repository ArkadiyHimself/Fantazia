package net.arkadiyhimself.fantazia.advanced.capability.entity.feature;

import net.arkadiyhimself.fantazia.util.interfaces.INBTsaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;


public class FeatureHolder implements INBTsaver {
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
