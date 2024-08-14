package net.arkadiyhimself.fantazia.items.expendable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ExpendableItem extends Item {
    public ExpendableItem(Rarity rarity) {
        this(rarity, 64);
    }
    public ExpendableItem(Rarity rarity, int stackSize) {
        super(new Properties().fireResistant().rarity(rarity).stacksTo(stackSize));
    }
}
