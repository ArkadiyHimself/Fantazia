package net.arkadiyhimself.fantazia.advanced.capability.itemstack;

import net.arkadiyhimself.fantazia.util.interfaces.INBTsaver;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class StackDataHolder implements INBTsaver {
    private final ItemStack stack;

    public StackDataHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public CompoundTag serialize() {
        return new CompoundTag();
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }
}
