package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability;

import net.minecraft.world.item.ItemStack;

public interface ICurioListener {
    void onCurioEquip(ItemStack stack);
    void onCurioUnEquip(ItemStack stack);
}
