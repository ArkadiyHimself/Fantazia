package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.registries.FTZBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class FantazicDataMapsProvider extends DataMapProvider {

    public FantazicDataMapsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(FTZBlocks.OBSCURE_SAPLING.getId(), new Compostable(0.3f), true)
                .add(FTZBlocks.OBSCURE_LEAVES.getId(), new Compostable(0.3f), true);
    }
}
