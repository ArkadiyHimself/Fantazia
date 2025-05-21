package net.arkadiyhimself.fantazia.datagen.talent_reload.talent;

import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.talent.Talent;
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

public class FantazicTalentProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final List<SubProvider<TalentBuilderHolder>> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FantazicTalentProvider(PackOutput output, List<SubProvider<TalentBuilderHolder>> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(FantazicRegistries.Keys.TALENT);
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<TalentBuilderHolder> consumer = subProvider -> {
                if (!set.add(subProvider.id())) {
                    throw new IllegalStateException("Duplicate talent: " + subProvider.id());
                } else {
                    Path path = this.pathProvider.json(subProvider.id());
                    list.add(DataProvider.saveStable(cachedOutput, provider, Talent.Builder.CODEC, subProvider.talent(), path));
                }
            };

            for(SubProvider<TalentBuilderHolder> subProvider : this.subProviders) {
                subProvider.generate(provider, consumer);
            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "Talent";
    }
}
