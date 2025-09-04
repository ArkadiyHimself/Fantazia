package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZDataMapTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
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
        this.builder(FTZDataMapTypes.SKULLS)
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER), Items.PLAYER_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.CREEPER), Items.CREEPER_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE), Items.ZOMBIE_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON), Items.SKELETON_SKULL, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER_SKELETON), Items.WITHER_SKELETON_SKULL, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDER_DRAGON), Items.DRAGON_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PIGLIN), Items.PIGLIN_HEAD, true
                );

        // neo forge
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(FTZBlocks.OBSCURE_SAPLING.getId(), new Compostable(0.3f), true)
                .add(FTZBlocks.OBSCURE_LEAVES.getId(), new Compostable(0.3f), true);
    }
}
