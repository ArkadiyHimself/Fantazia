package net.arkadiyhimself.fantazia.data.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.common.registries.custom.HealingTypes;
import net.arkadiyhimself.fantazia.data.datagen.worldgen.FantazicBiomeModifiers;
import net.arkadiyhimself.fantazia.data.datagen.worldgen.FantazicConfiguredFeatures;
import net.arkadiyhimself.fantazia.data.datagen.worldgen.FantazicPlacedFeatures;
import net.arkadiyhimself.fantazia.data.tags.FTZPools;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FantazicDatapackProvider extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(FantazicRegistries.Keys.HEALING_TYPE, HealingTypes::bootStrap)
            .add(Registries.CONFIGURED_FEATURE, FantazicConfiguredFeatures::bootStrap)
            .add(Registries.PLACED_FEATURE, FantazicPlacedFeatures::bootStrap)
            .add(Registries.STRUCTURE, FTZStructures::bootStrap)
            .add(Registries.TEMPLATE_POOL, FTZPools::bootStrap)
            .add(Registries.STRUCTURE_SET, FTZStructureSets::bootStrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FantazicBiomeModifiers::bootStrap)
            .add(Registries.PAINTING_VARIANT, FTZPaintingVariants::bootStrap)
            .add(Registries.DAMAGE_TYPE, FTZDamageTypes::bootStrap)
            .add(Registries.ENCHANTMENT, FTZEnchantments::bootStrap);

    public FantazicDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Fantazia.MODID));
    }
}
