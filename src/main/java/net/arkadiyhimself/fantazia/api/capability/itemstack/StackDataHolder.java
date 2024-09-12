package net.arkadiyhimself.fantazia.api.capability.itemstack;

import net.arkadiyhimself.fantazia.api.capability.INBTwrite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class StackDataHolder implements INBTwrite {
    private final ItemStack stack;
    protected StackDataHolder(ItemStack stack) {
        this.stack = stack;
    }
    public abstract String id();
    @Override
    public CompoundTag serialize(boolean toDisk) {
        return new CompoundTag();
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {

    }
    public ItemStack getStack() {
        return stack;
    }
}
