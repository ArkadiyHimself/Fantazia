package net.arkadiyhimself.fantazia.util.interfaces;

import net.minecraft.nbt.CompoundTag;

public interface INBTsaver {
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
}
