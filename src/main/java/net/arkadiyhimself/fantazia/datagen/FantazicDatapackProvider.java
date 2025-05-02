package net.arkadiyhimself.fantazia.datagen;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZPaintingVariants;
import net.arkadiyhimself.fantazia.registries.custom.FTZHealingTypes;
import net.arkadiyhimself.fantazia.worldgen.FantazicBiomeModifiers;
import net.arkadiyhimself.fantazia.worldgen.FantazicConfiguredFeatures;
import net.arkadiyhimself.fantazia.worldgen.FantazicPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class FantazicDatapackProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(FantazicRegistries.Keys.HEALING_TYPE, FTZHealingTypes::bootStrap)
            .add(Registries.CONFIGURED_FEATURE, FantazicConfiguredFeatures::bootStrap)
            .add(Registries.PLACED_FEATURE, FantazicPlacedFeatures::bootStrap)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, FantazicBiomeModifiers::bootStrap)
            .add(Registries.PAINTING_VARIANT, FTZPaintingVariants::bootStrap)
            .add(Registries.DAMAGE_TYPE, FTZDamageTypes::bootStrap);

    public FantazicDatapackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Fantazia.MODID));
    }


}
