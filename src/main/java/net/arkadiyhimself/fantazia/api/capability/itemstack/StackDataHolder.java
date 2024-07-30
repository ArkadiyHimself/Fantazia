package net.arkadiyhimself.fantazia.api.capability.itemstack;

import net.arkadiyhimself.fantazia.api.capability.INBTwrite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class StackDataHolder implements INBTwrite {
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
