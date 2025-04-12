package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface FTZItemTags {

    TagKey<Item> HATCHETS = create("hatchets");
    TagKey<Item> HATCHET_ENCHANTABLE = create("enchantable/hatchet");
    TagKey<Item> NO_DISINTEGRATION = create("no_disintegration");
    TagKey<Item> MELEE_BLOCK = create("melee_block");
    TagKey<Item> OBSCURE_LOGS = create("obscure_logs");
    TagKey<Item> FANTAZIUM_ORES = create("fantazium_ores");

    private static TagKey<Item> create(String pName) {
        return TagKey.create(Registries.ITEM, Fantazia.res(pName));
    }

}
