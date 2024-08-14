package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

public interface FTZItemTags {
    TagKey<Item> NO_DISINTEGRATION = create("no_disintegration");
    TagKey<Item> MELEE_BLOCK = create("melee_block");
    private static TagKey<Item> create(String pName) {
        return TagKey.create(Registries.ITEM, Fantazia.res(pName));
    }
    static boolean hasTag(Item item, TagKey<Item> tagKey) {
        ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();
        return tagManager != null && tagManager.getTag(tagKey).contains(item);
    }

}
