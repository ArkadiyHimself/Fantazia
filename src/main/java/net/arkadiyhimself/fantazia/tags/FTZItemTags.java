package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface FTZItemTags {

    TagKey<Item> CURIOS_ACTIVECASTER = create("for_activecaster");
    TagKey<Item> CURIOS_PASSIVECASTER = create("for_passivecaster");
    TagKey<Item> CURIOS_DASHSTONE = create("for_dashstone");
    TagKey<Item> CURIOS_RUNE = create("for_rune");

    TagKey<Item> HATCHETS = create("hatchets");
    TagKey<Item> HATCHET_ENCHANTABLE = create("enchantable/hatchet");
    TagKey<Item> NO_DISINTEGRATION = create("no_disintegration");
    TagKey<Item> MELEE_BLOCK = create("melee_block");
    TagKey<Item> OBSCURE_LOGS = create("obscure_logs");
    TagKey<Item> FANTAZIUM_ORES = create("fantazium_ores");
    TagKey<Item> FROM_OBSCURE_TREE = create("from_obscure_tree");
    TagKey<Item> FROM_FANTAZIUM = create("from_fantazium");
    TagKey<Item> RANGED_WEAPON = create("range_weapon");

    private static TagKey<Item> create(String pName) {
        return TagKey.create(Registries.ITEM, Fantazia.res(pName));
    }

}
