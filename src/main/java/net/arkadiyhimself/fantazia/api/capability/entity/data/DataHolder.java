package net.arkadiyhimself.fantazia.api.capability.entity.data;

import net.arkadiyhimself.fantazia.api.capability.INBTwrite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public abstract class DataHolder implements INBTwrite {
    private final LivingEntity livingEntity;
    public DataHolder(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }
    public LivingEntity getEntity() {
        return livingEntity;
    }
    @Override
    public CompoundTag serialize() {
        return new CompoundTag();
    }
    @Override
    public void deserialize(CompoundTag tag) {
    }
    public void respawn() {
    }

}
