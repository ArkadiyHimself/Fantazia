package net.arkadiyhimself.fantazia.api.capability.itemstack.stackdata;

import net.arkadiyhimself.fantazia.api.capability.itemstack.StackDataHolder;
import net.minecraft.world.item.ItemStack;

public class CommonStackData extends StackDataHolder {
    private boolean pickedUp = false;
    public CommonStackData(ItemStack stack) {
        super(stack);
    }
    public void picked() {
        pickedUp = true;
    }
    public void dropped() {
        pickedUp = false;
    }
    public boolean pickedUp() {
        return pickedUp;
    }
}
