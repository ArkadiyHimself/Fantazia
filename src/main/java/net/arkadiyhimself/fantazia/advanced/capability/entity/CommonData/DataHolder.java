package net.arkadiyhimself.fantazia.advanced.capability.entity.CommonData;

import net.arkadiyhimself.fantazia.util.interfaces.INBTsaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public abstract class DataHolder implements INBTsaver {
    private final LivingEntity livingEntity;

    public DataHolder(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }
}
