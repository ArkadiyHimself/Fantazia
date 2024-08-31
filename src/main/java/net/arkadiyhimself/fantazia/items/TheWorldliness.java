package net.arkadiyhimself.fantazia.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class TheWorldliness extends Item {
    public TheWorldliness() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    }
}
