package net.arkadiyhimself.fantazia.common.item;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import vazkii.patchouli.api.PatchouliAPI;

public class TheWorldlinessItem extends Item {

    public TheWorldlinessItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    }

    public static ItemStack itemStack() {
        return PatchouliAPI.get().getBookStack(Fantazia.location("the_worldliness"));
    }
}
