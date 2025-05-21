package net.arkadiyhimself.fantazia.datagen.talent_reload.hierarchy;

import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.talent.TalentHierarchyBuilder;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
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

public class FantazicTalentHierarchyProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final List<SubProvider<TalentHierarchyHolder>> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FantazicTalentHierarchyProvider(PackOutput output, List<SubProvider<TalentHierarchyHolder>> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(FantazicRegistries.Keys.TALENT_HIERARCHY);
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<TalentHierarchyHolder> consumer = subProvider -> {
                if (!set.add(subProvider.id())) {
                    throw new IllegalStateException("Duplicate talent: " + subProvider.id());
                } else {
                    Path path = this.pathProvider.json(subProvider.id());
                    list.add(DataProvider.saveStable(cachedOutput, provider, TalentHierarchyBuilder.CODEC, subProvider.hierarchy(), path));
                }
            };

            for(SubProvider<TalentHierarchyHolder> subProvider : this.subProviders) {
                subProvider.generate(provider, consumer);
            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "TalentHierarchy";
    }
}
