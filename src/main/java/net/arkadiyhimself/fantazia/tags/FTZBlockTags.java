package net.arkadiyhimself.fantazia.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface FTZBlockTags {

    TagKey<Block> OBSCURE_LOGS = create("obscure_logs");
    TagKey<Block> FANTAZIUM_ORES = create("fantazium_ores");
    TagKey<Block> FROM_OBSCURE_TREE = create("from_obscure_tree");
    TagKey<Block> FROM_FANTAZIUM = create("from_fantazium");

    private static TagKey<Block> create(String pName) {
        return TagKey.create(Registries.BLOCK, Fantazia.res(pName));
    }
}
