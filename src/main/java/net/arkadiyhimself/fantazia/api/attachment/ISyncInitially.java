package net.arkadiyhimself.fantazia.api.attachment;

import net.minecraft.nbt.CompoundTag;

public interface ISyncInitially {

    CompoundTag serializeInitial();
    void deserializeInitial(CompoundTag tag);

}
