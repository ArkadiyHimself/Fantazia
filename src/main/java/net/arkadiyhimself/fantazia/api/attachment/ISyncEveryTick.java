package net.arkadiyhimself.fantazia.api.attachment;

import net.minecraft.nbt.CompoundTag;

public interface ISyncEveryTick {
    CompoundTag serializeTick();
    void deserializeTick(CompoundTag tag);
}
