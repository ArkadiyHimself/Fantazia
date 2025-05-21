package net.arkadiyhimself.fantazia.items;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.api.stub.StubPatchouliAPI;

public class TheWorldlinessItem extends Item {

    public TheWorldlinessItem() {
        super(new Properties().rarity(Rarity.EPIC).stacksTo(1).fireResistant());
    }

    public static ItemStack itemStack() {
        return PatchouliAPI.get().getBookStack(Fantazia.res("the_worldliness"));
    }
}
