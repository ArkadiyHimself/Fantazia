package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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
}
