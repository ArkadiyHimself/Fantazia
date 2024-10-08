package net.arkadiyhimself.fantazia.api.type.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IBasicHolder extends INBTSerializable<CompoundTag> {
    ResourceLocation id();
    default void tick() {}
    CompoundTag syncSerialize();
    void syncDeserialize(CompoundTag tag);
}
