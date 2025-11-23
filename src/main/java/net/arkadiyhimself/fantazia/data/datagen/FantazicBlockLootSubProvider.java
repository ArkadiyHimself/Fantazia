package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FantazicBlockLootSubProvider extends BlockLootSubProvider {

    public FantazicBlockLootSubProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(FTZBlocks.OBSCURE_BUTTON.get());
        dropSelf(FTZBlocks.OBSCURE_FENCE.get());
        dropSelf(FTZBlocks.OBSCURE_FENCE_GATE.get());
        dropSelf(FTZBlocks.OBSCURE_HANGING_SIGN.get());
        dropSelf(FTZBlocks.OBSCURE_LOG.get());
        dropSelf(FTZBlocks.OBSCURE_PLANKS.get());
        dropSelf(FTZBlocks.OBSCURE_PRESSURE_PLATE.get());
        dropSelf(FTZBlocks.OBSCURE_SAPLING.get());
        dropSelf(FTZBlocks.OBSCURE_SIGN.get());
        dropSelf(FTZBlocks.OBSCURE_STAIRS.get());
        dropSelf(FTZBlocks.OBSCURE_TRAPDOOR.get());
        dropSelf(FTZBlocks.OBSCURE_WOOD.get());
        dropSelf(FTZBlocks.STRIPPED_OBSCURE_LOG.get());
        dropSelf(FTZBlocks.STRIPPED_OBSCURE_WOOD.get());
        dropSelf(FTZBlocks.FANTAZIUM_BLOCK.get());
        dropSelf(FTZBlocks.RAW_FANTAZIUM_BLOCK.get());
        dropSelf(FTZBlocks.AMPLIFICATION_BENCH.value());

        createEngineeringTableLoot(FTZBlocks.OAK_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.SPRUCE_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.BIRCH_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.JUNGLE_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.ACACIA_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.CHERRY_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.DARK_OAK_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.MANGROVE_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.BAMBOO_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.CRIMSON_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.WARPED_ENGINEERING_TABLE);
        createEngineeringTableLoot(FTZBlocks.OBSCURE_ENGINEERING_TABLE);

        add(FTZBlocks.OBSCURE_DOOR.get(), this::createDoorTable);
        add(FTZBlocks.OBSCURE_LEAVES.get(),block -> createLeavesDrops(block, FTZBlocks.OBSCURE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        add(FTZBlocks.OBSCURE_SLAB.get(), this::createSlabItemTable);
        add(FTZBlocks.POTTED_OBSCURE_SAPLING.get(), this::createPotFlowerItemTable);

        add(FTZBlocks.FANTAZIUM_ORE.get(),block -> createOreDrop(block, FTZItems.RAW_FANTAZIUM.value()));
        add(FTZBlocks.DEEPSLATE_FANTAZIUM_ORE.get(),block -> createOreDrop(block, FTZItems.RAW_FANTAZIUM.value()));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return FTZBlocks.REGISTER.getEntries().stream().map(Holder::value)::iterator;
    }

    protected void createEngineeringTableLoot(@NotNull DeferredBlock<EngineeringTableBlock> doorBlock) {
        this.add(doorBlock.value(), createSinglePropConditionTable(doorBlock.value(), EngineeringTableBlock.HALF, DoubleBlockHalf.LOWER));

    }
}
