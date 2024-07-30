package net.arkadiyhimself.fantazia.api.capability;

import net.minecraft.nbt.CompoundTag;

public interface INBTwrite {
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
}
