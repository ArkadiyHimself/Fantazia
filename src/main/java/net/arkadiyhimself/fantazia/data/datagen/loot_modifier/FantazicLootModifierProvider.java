package net.arkadiyhimself.fantazia.data.datagen.loot_modifier;

import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.loot.LootModifier;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class FantazicLootModifierProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final List<SubProvider<LootModifierHolder>> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FantazicLootModifierProvider(PackOutput output, List<SubProvider<LootModifierHolder>> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(FantazicRegistries.Keys.LOOT_MODIFIER);
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<LootModifierHolder> consumer = subProvider -> {
                if (!set.add(subProvider.id())) {
                    throw new IllegalStateException("Duplicate fantazic loot modifier " + subProvider.id());
                } else {
                    Path path = this.pathProvider.json(subProvider.id());
                    list.add(DataProvider.saveStable(cachedOutput, provider, LootModifier.Builder.CODEC, subProvider.builder(), path));
                }
            };

            for(SubProvider<LootModifierHolder> lootModifierSubProvider : this.subProviders) {
                lootModifierSubProvider.generate(provider, consumer);
            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "LootModifier";
    }
}
