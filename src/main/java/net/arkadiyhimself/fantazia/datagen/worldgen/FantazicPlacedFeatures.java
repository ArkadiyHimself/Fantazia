package net.arkadiyhimself.fantazia.datagen.worldgen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class FantazicPlacedFeatures {

    public static final ResourceKey<PlacedFeature> OBSCURE_TREES = registryKey("obscure_trees");
    public static final ResourceKey<PlacedFeature> ORE_FANTAZIUM = registryKey("ore_fantazium");

    public static void bootStrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, OBSCURE_TREES, configuredFeatures.getOrThrow(FantazicConfiguredFeatures.OBSCURE_TREE1),
                RarityFilter.onAverageOnceEvery(125), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP,
                PlacementUtils.filteredByBlockSurvival(FTZBlocks.OBSCURE_SAPLING.get()));

        register(context, ORE_FANTAZIUM, configuredFeatures.getOrThrow(FantazicConfiguredFeatures.ORE_FANTAZIUM),
                commonOrePlacement(25, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(96))));

    }

    public static ResourceKey<PlacedFeature> registryKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Fantazia.res(name));
    }

    private static void register
            (BootstrapContext<PlacedFeature> context,
             ResourceKey<PlacedFeature> key,
             Holder<ConfiguredFeature<?,?>> configuration,
             List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, modifiers));
    }

    public static List<PlacementModifier> orePlacement(PlacementModifier pCountPlacement, PlacementModifier pHeightRange) {
        return List.of(pCountPlacement, InSquarePlacement.spread(), pHeightRange, BiomeFilter.biome());
    }

    public static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
        return orePlacement(CountPlacement.of(pCount), pHeightRange);
    }

    public static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
        return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
    }
}
