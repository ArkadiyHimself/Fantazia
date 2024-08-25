package net.arkadiyhimself.fantazia.items.expendable;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ObscureSubstance extends Item {
    public ObscureSubstance(Rarity rarity) {
        this(rarity, 64);
    }
    public ObscureSubstance(Rarity rarity, int stackSize) {
        super(new Properties().fireResistant().rarity(rarity).stacksTo(stackSize));
    }
}
