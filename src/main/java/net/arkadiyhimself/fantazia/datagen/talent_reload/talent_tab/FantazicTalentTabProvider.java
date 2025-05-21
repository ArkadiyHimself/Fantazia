package net.arkadiyhimself.fantazia.datagen.talent_reload.talent_tab;

import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.client.screen.TalentTab;
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

public class FantazicTalentTabProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final List<SubProvider<TalentTabBuilderHolder>> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FantazicTalentTabProvider(PackOutput output, List<SubProvider<TalentTabBuilderHolder>> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(FantazicRegistries.Keys.TALENT_TAB);
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<TalentTabBuilderHolder> consumer = subProvider -> {
                if (!set.add(subProvider.id())) {
                    throw new IllegalStateException("Duplicate talent tab: " + subProvider.id());
                } else {
                    Path path = this.pathProvider.json(subProvider.id());
                    list.add(DataProvider.saveStable(cachedOutput, provider, TalentTab.Builder.CODEC, subProvider.talent(), path));
                }
            };

            for(SubProvider<TalentTabBuilderHolder> subProvider : this.subProviders) {
                subProvider.generate(provider, consumer);
            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "TalentTab";
    }
}
