package net.arkadiyhimself.fantazia.advanced.capability.itemstack.Common;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class CommonItem extends ItemStackCapability {
    public CommonItem(ItemStack itemStack) {
        super(itemStack);
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
    }
    public boolean wasPickedUp = false;
}
