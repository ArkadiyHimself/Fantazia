package net.arkadiyhimself.fantazia.api.capability.entity.data;

import net.arkadiyhimself.fantazia.api.capability.INBTwrite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public abstract class DataHolder implements INBTwrite {
    private final LivingEntity livingEntity;
    public DataHolder(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }
    public abstract String ID();
    @Override
    public abstract CompoundTag serialize(boolean toDisk);
    @Override
    public abstract void deserialize(CompoundTag tag, boolean fromDisk);
    public LivingEntity getEntity() {
        return livingEntity;
    }
    public void respawn() {}
}
