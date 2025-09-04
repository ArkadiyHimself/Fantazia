package net.arkadiyhimself.fantazia.common.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.datagen.worldgen.structures.SimpleNetherStructure;
import net.arkadiyhimself.fantazia.data.tags.FTZBiomeTags;
import net.arkadiyhimself.fantazia.data.tags.FTZPools;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public interface FTZStructures {

    ResourceKey<Structure> BLACKSTONE_ALTAR = key("blackstone_altar");

    private static ResourceKey<Structure> key(String name) {
        return ResourceKey.create(Registries.STRUCTURE, Fantazia.location(name));
    }

    static void bootStrap(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomeHolderGetter = context.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> templatePoolHolderGetter = context.lookup(Registries.TEMPLATE_POOL);

        context.register(BLACKSTONE_ALTAR, new SimpleNetherStructure(
                new Structure.StructureSettings.Builder(biomeHolderGetter.getOrThrow(FTZBiomeTags.HAS_BLACKSTONE_ALTAR))
                        .terrainAdapation(TerrainAdjustment.BEARD_THIN)
                        .build(),
                templatePoolHolderGetter.getOrThrow(FTZPools.BLACKSTONE_ALTAR_START_POOL),
                5
        ));
    }
}
