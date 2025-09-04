package net.arkadiyhimself.fantazia.data.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.data.tags.FTZBlockTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicBlockTagsProvider extends BlockTagsProvider {

    public FantazicBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia tags
        tag(FTZBlockTags.OBSCURE_LOGS).add(FTZBlocks.OBSCURE_LOG.value(), FTZBlocks.OBSCURE_WOOD.value(), FTZBlocks.STRIPPED_OBSCURE_LOG.value(), FTZBlocks.STRIPPED_OBSCURE_WOOD.value());
        tag(FTZBlockTags.FANTAZIUM_ORES).add(FTZBlocks.FANTAZIUM_ORE.value(), FTZBlocks.DEEPSLATE_FANTAZIUM_ORE.value());
        tag(FTZBlockTags.FROM_OBSCURE_TREE).add(FTZBlocks.OBSCURE_PLANKS.value(), FTZBlocks.OBSCURE_SAPLING.value(), FTZBlocks.OBSCURE_LOG.value(), FTZBlocks.OBSCURE_WOOD.value(), FTZBlocks.OBSCURE_LEAVES.value(), FTZBlocks.OBSCURE_STAIRS.value(), FTZBlocks.STRIPPED_OBSCURE_LOG.value(), FTZBlocks.OBSCURE_WOOD.value(), FTZBlocks.OBSCURE_PRESSURE_PLATE.value(), FTZBlocks.OBSCURE_TRAPDOOR.value(), FTZBlocks.OBSCURE_SAPLING.value(), FTZBlocks.OBSCURE_BUTTON.value(), FTZBlocks.OBSCURE_SLAB.value(), FTZBlocks.OBSCURE_FENCE_GATE.value(), FTZBlocks.OBSCURE_FENCE.value(), FTZBlocks.OBSCURE_DOOR.value());
        tag(FTZBlockTags.FROM_FANTAZIUM).add(FTZBlocks.FANTAZIUM_ORE.value(), FTZBlocks.DEEPSLATE_FANTAZIUM_ORE.value(), FTZBlocks.FANTAZIUM_BLOCK.value(), FTZBlocks.RAW_FANTAZIUM_BLOCK.value());
        tag(FTZBlockTags.ORES_FANTAZIUM).addTag(FTZBlockTags.FANTAZIUM_ORES);

        // neo forge
        tag(Tags.Blocks.FENCE_GATES_WOODEN).add(
                FTZBlocks.OBSCURE_FENCE_GATE.value()
        );

        tag(Tags.Blocks.FENCES_WOODEN).add(
                FTZBlocks.OBSCURE_FENCE.value()
        );

        tag(Tags.Blocks.ORES).addTag(
                FTZBlockTags.FANTAZIUM_ORES
        );

        // mine-able folder
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(FTZBlocks.AMPLIFICATION_BENCH.value(), FTZBlocks.FANTAZIUM_ORE.value(), FTZBlocks.DEEPSLATE_FANTAZIUM_ORE.value(), FTZBlocks.FANTAZIUM_BLOCK.value(), FTZBlocks.RAW_FANTAZIUM_BLOCK.value());
        tag(BlockTags.MINEABLE_WITH_HOE).add(FTZBlocks.OBSCURE_LEAVES.value());
        tag(BlockTags.NEEDS_IRON_TOOL).add(FTZBlocks.FANTAZIUM_ORE.value(), FTZBlocks.DEEPSLATE_FANTAZIUM_ORE.value(), FTZBlocks.FANTAZIUM_BLOCK.value(), FTZBlocks.RAW_FANTAZIUM_BLOCK.value());
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(FTZBlocks.AMPLIFICATION_BENCH.value());

        // minecraft tags
        tag(BlockTags.CEILING_HANGING_SIGNS).add(FTZBlocks.OBSCURE_HANGING_SIGN.value());
        tag(BlockTags.FENCE_GATES).add(FTZBlocks.OBSCURE_FENCE_GATE.value());
        tag(BlockTags.FLOWER_POTS).add(FTZBlocks.POTTED_OBSCURE_SAPLING.value());
        tag(BlockTags.LEAVES).add(FTZBlocks.OBSCURE_LEAVES.value());
        tag(BlockTags.LOGS_THAT_BURN).addTag(FTZBlockTags.OBSCURE_LOGS);
        tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(FTZBlocks.OBSCURE_LOG.value());
        tag(BlockTags.PLANKS).add(FTZBlocks.OBSCURE_PLANKS.value());
        tag(BlockTags.SAPLINGS).add(FTZBlocks.OBSCURE_SAPLING.value());
        tag(BlockTags.STANDING_SIGNS).add(FTZBlocks.OBSCURE_SIGN.value());
        tag(BlockTags.WALL_HANGING_SIGNS).add(FTZBlocks.OBSCURE_WALL_HANGING_SIGN.value());
        tag(BlockTags.WALL_SIGNS).add(FTZBlocks.OBSCURE_WALL_SIGN.value());
        tag(BlockTags.WOODEN_BUTTONS).add(FTZBlocks.OBSCURE_BUTTON.value());
        tag(BlockTags.WOODEN_DOORS).add(FTZBlocks.OBSCURE_DOOR.value());
        tag(BlockTags.WOODEN_FENCES).add(FTZBlocks.OBSCURE_FENCE.value());
        tag(BlockTags.WOODEN_PRESSURE_PLATES).add(FTZBlocks.OBSCURE_PRESSURE_PLATE.value());
        tag(BlockTags.WOODEN_SLABS).add(FTZBlocks.OBSCURE_SLAB.value());
        tag(BlockTags.WOODEN_STAIRS).add(FTZBlocks.OBSCURE_STAIRS.value());
        tag(BlockTags.WOODEN_TRAPDOORS).add(FTZBlocks.OBSCURE_TRAPDOOR.value());
        tag(BlockTags.BEACON_BASE_BLOCKS).add(FTZBlocks.FANTAZIUM_BLOCK.value());
    }
}
