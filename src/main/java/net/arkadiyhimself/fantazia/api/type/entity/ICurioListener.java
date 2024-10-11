package net.arkadiyhimself.fantazia.api.type.entity;

import net.minecraft.world.item.ItemStack;

public interface ICurioListener {
    void onCurioEquip(ItemStack stack);
    void onCurioUnEquip(ItemStack stack);
}
