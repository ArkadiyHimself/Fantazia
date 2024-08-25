package net.arkadiyhimself.fantazia.api.capability;

import net.minecraft.nbt.CompoundTag;

public interface INBTwrite {
    CompoundTag serialize(boolean toDisk);
    void deserialize(CompoundTag tag, boolean fromDisk);
}
