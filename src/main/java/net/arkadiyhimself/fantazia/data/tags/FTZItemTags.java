package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface FTZItemTags {

    TagKey<Item> CURIOS_ACTIVECASTER = create("for_activecaster");
    TagKey<Item> CURIOS_PASSIVECASTER = create("for_passivecaster");
    TagKey<Item> CURIOS_DASHSTONE = create("for_dashstone");
    TagKey<Item> CURIOS_RUNE = create("for_rune");

    TagKey<Item> CASTER_ENCHANTABLE = create("enchantable/caster");
    TagKey<Item> HATCHETS = create("hatchets");
    TagKey<Item> HATCHET_ENCHANTABLE = create("enchantable/hatchet");
    TagKey<Item> ANCIENT_FLAME_ENCHANTABLE = create("enchantable/ancient_flame");
    TagKey<Item> NO_DISINTEGRATION = create("no_disintegration");
    TagKey<Item> MELEE_BLOCK = create("melee_block");
    TagKey<Item> OBSCURE_LOGS = create("obscure_logs");
    TagKey<Item> FANTAZIUM_ORES = create("fantazium_ores");
    TagKey<Item> FROM_OBSCURE_TREE = create("from_obscure_tree");
    TagKey<Item> FROM_FANTAZIUM = create("from_fantazium");
    TagKey<Item> DISABLED_BY_DISARM = create("disabled_by_disarm");
    TagKey<Item> SLOWED_BY_FROZEN = create("slowed_by_frozen");
    TagKey<Item> INGOTS_FANTAZIUM = create("ingots/fantazium");
    TagKey<Item> RAW_MATERIALS_FANTAZIUM = create("raw_materials/fantazium");
    TagKey<Item> RECHARGEABLE_TOOL = create("rechargeable_tool");
    TagKey<Item> ENGINEERING_TABLES = create("engineering_tables");

    // minecraft extension
    TagKey<Item> AXE_ENCHANTABLE = extension("enchantable/axe");

    private static TagKey<Item> create(String pName) {
        return Fantazia.tagKey(Registries.ITEM, pName);
    }

    private static TagKey<Item> extension(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace(name));
    }
}
