package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public interface FTZBiomeTags {

    TagKey<Biome> HAS_BLACKSTONE_ALTAR = create("has_structure/blackstone_altar");

    private static TagKey<Biome> create(String pName) {
        return TagKey.create(Registries.BIOME, Fantazia.location(pName));
    }
}
