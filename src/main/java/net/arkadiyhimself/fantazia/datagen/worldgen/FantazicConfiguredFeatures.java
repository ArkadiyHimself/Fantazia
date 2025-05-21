package net.arkadiyhimself.fantazia.datagen.worldgen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.FancyTrunkPlacer;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;

public class FantazicConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> OBSCURE_TREE1 = registryKey("obscure_tree1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OBSCURE_TREE2 = registryKey("obscure_tree2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_FANTAZIUM = registryKey("ore_fantazium");

    public static void bootStrap(BootstrapContext<ConfiguredFeature<?,?>> context) {
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> overworldFantaziumOres = List.of(
                OreConfiguration.target(stoneReplaceables, FTZBlocks.FANTAZIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, FTZBlocks.DEEPSLATE_FANTAZIUM_ORE.get().defaultBlockState())
        );

        register(context, OBSCURE_TREE1, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(FTZBlocks.OBSCURE_LOG.get()),
                new FancyTrunkPlacer(5, 4, 3),
                BlockStateProvider.simple(FTZBlocks.OBSCURE_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(2), ConstantInt.of(4),5),
                new TwoLayersFeatureSize(1, 0, 2)).build());

        register(context, OBSCURE_TREE2, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(FTZBlocks.OBSCURE_LOG.get()),
                new FancyTrunkPlacer(6, 2, 4),
                BlockStateProvider.simple(FTZBlocks.OBSCURE_LEAVES.get()),
                new FancyFoliagePlacer(ConstantInt.of(3), ConstantInt.of(5),3),
                new TwoLayersFeatureSize(1, 0, 3)).build());

        register(context, ORE_FANTAZIUM, Feature.ORE, new OreConfiguration(overworldFantaziumOres,3));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registryKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Fantazia.res(name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?,?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
