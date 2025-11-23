package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.arkadiyhimself.fantazia.util.simpleobjects.RegistryObjectList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.EntityTypeTags;
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
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PLAYER),
                        Items.PLAYER_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.CREEPER),
                        Items.CREEPER_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ZOMBIE),
                        Items.ZOMBIE_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.SKELETON),
                        Items.SKELETON_SKULL, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.WITHER_SKELETON),
                        Items.WITHER_SKELETON_SKULL, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.ENDER_DRAGON),
                        Items.DRAGON_HEAD, true
                )
                .add(
                        BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.PIGLIN),
                        Items.PIGLIN_HEAD, true
                );

        this.builder(FTZDataMapTypes.RECHARGEABLE_TOOLS)
                .add(
                        FTZItems.PIMPILLO.getId(),
                        FTZItems.PIMPILLO.value().defaultData(),
                        true
                )
                .add(
                        FTZItems.THROWING_PIN.getId(),
                        FTZItems.THROWING_PIN.value().defaultData(),
                        true
                )
                .add(
                        FTZItems.BLOCK_FLY.getId(),
                        FTZItems.BLOCK_FLY.value().defaultData(),
                        true
                );

        this.builder(FTZDataMapTypes.MOB_EFFECT_WHITE_LIST)
                .add(
                        FTZMobEffects.DEAFENED.getId(),
                        RegistryObjectList.<EntityType<?>>builder()
                                .addObjects(
                                        EntityType.WARDEN,
                                        EntityType.PLAYER
                                )
                                .build(),
                        true
                );

        this.builder(FTZDataMapTypes.MOB_EFFECT_BLACK_LIST)
                .add(
                        FTZMobEffects.ELECTROCUTED.getId(),
                        RegistryObjectList.<EntityType<?>>builder()
                                .addObjects(
                                        EntityType.CREEPER,
                                        EntityType.ZOMBIFIED_PIGLIN,
                                        EntityType.WITCH,
                                        EntityType.MOOSHROOM
                                )
                                .build(),
                        true
                )
                .add(
                        FTZMobEffects.HAEMORRHAGE.getId(),
                        RegistryObjectList.<EntityType<?>>builder()
                                .addObjects(
                                        EntityType.SLIME,
                                        EntityType.MAGMA_CUBE,
                                        EntityType.WARDEN,
                                        EntityType.SNOW_GOLEM,
                                        EntityType.IRON_GOLEM,
                                        EntityType.VEX,
                                        EntityType.ALLAY,
                                        EntityType.SHULKER,
                                        EntityType.GUARDIAN,
                                        EntityType.ELDER_GUARDIAN
                                )
                                .addTags(
                                        EntityTypeTags.SKELETONS
                                )
                                .build(),
                        true
                )
                .add(
                        FTZMobEffects.STUN.getId(),
                        RegistryObjectList.<EntityType<?>>builder()
                                .addObjects(
                                        FTZEntityTypes.BLOCK_FLY.value()
                                )
                                .build(),
                        true
                );

        // neo forge
        this.builder(NeoForgeDataMaps.COMPOSTABLES)
                .add(FTZBlocks.OBSCURE_SAPLING.getId(), new Compostable(0.3f), true)
                .add(FTZBlocks.OBSCURE_LEAVES.getId(), new Compostable(0.3f), true);
    }
}
