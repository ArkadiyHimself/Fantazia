package net.arkadiyhimself.fantazia.worldgen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FantazicBiomeModifiers {

    public static final ResourceKey<BiomeModifier> OBSCURE_TREE = registryKey("obscure_tree");
    public static final ResourceKey<BiomeModifier> ORE_FANTAZIUM = registryKey("ore_fantazium");

    public static void bootStrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(OBSCURE_TREE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST),
                        biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST), biomes.getOrThrow(Biomes.TAIGA)),
                HolderSet.direct(placedFeatures.getOrThrow(FantazicPlacedFeatures.OBSCURE_TREES)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));

        context.register(ORE_FANTAZIUM, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(FantazicPlacedFeatures.ORE_FANTAZIUM)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
    }

    private static ResourceKey<BiomeModifier> registryKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Fantazia.res(name));
    }
}
