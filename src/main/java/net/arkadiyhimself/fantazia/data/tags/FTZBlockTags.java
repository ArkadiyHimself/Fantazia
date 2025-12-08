package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface FTZBlockTags {

    TagKey<Block> OBSCURE_LOGS = create("obscure_logs");
    TagKey<Block> FANTAZIUM_ORES = create("fantazium_ores");
    TagKey<Block> FROM_OBSCURE_TREE = create("from_obscure_tree");
    TagKey<Block> FROM_FANTAZIUM = create("from_fantazium");
    TagKey<Block> ORES_FANTAZIUM = create("ores/fantazium");
    TagKey<Block> ENGINEERING_TABLES = create("engineering_tables");
    TagKey<Block> NOT_TRIGGERING_ENHANCED_MINING_PERK = create("not_triggering_enhanced_mining_perk");
    TagKey<Block> IGNORED_BY_ENHANCED_MINING_PERK = create("ignored_by_enhanced_mining_perk");
    TagKey<Block> NOT_TRIGGERING_LUMBERJACK_PERK = create("not_triggering_lumberjack_perk");
    TagKey<Block> IGNORED_BY_LUMBERJACK_PERK = create("ignored_by_lumberjack_perk");
    TagKey<Block> IGNORED_BY_FERTILIZATION_PERK = create("ignored_by_fertilization_perk");

    private static TagKey<Block> create(String pName) {
        return TagKey.create(Registries.BLOCK, Fantazia.location(pName));
    }
}
