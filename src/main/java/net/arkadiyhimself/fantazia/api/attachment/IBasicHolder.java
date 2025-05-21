package net.arkadiyhimself.fantazia.api.attachment;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IBasicHolder extends INBTSerializable<CompoundTag>, ISyncInitially {

    ResourceLocation id();
    default void serverTick() {}
    default void clientTick() {}

}
