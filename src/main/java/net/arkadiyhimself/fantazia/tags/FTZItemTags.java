package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface FTZItemTags {
    TagKey<Item> NO_DISINTEGRATION = create("no_disintegration");
    TagKey<Item> MELEE_BLOCK = create("melee_block");
    private static TagKey<Item> create(String pName) {
        return TagKey.create(Registries.ITEM, Fantazia.res(pName));
    }
    static boolean hasTag(Item item, TagKey<Item> tagKey) {
        Holder.Reference<Item> itemReference = BuiltInRegistries.ITEM.createIntrusiveHolder(item);
        return itemReference.is(tagKey);
    }

}
