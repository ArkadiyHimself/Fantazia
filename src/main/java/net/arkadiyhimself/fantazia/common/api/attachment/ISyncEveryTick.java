package net.arkadiyhimself.fantazia.common.api.attachment;

import net.minecraft.nbt.CompoundTag;

public interface ISyncEveryTick {
    CompoundTag serializeTick();
    void deserializeTick(CompoundTag tag);
}
