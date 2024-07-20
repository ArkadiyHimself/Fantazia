package net.arkadiyhimself.fantazia.Items;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SnowballItem;

public class Consumable extends Item {
    public Consumable() {
        super(new Properties().stacksTo(1));
    }
    public InteractionResultHolder<ItemStack> consume(ItemStack itemStack) {
        itemStack.shrink(1);
        return InteractionResultHolder.pass(itemStack);
    }
}
