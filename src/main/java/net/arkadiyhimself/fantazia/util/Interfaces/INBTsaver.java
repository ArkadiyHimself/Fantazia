package net.arkadiyhimself.fantazia.util.Interfaces;

import net.minecraft.nbt.CompoundTag;

public interface INBTsaver {
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
}
