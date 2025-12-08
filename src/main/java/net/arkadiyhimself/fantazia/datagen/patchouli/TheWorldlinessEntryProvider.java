package net.arkadiyhimself.fantazia.datagen.patchouli;

import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TheWorldlinessEntryProvider implements DataProvider {

    public static final ResourceKey<Registry<PseudoEntry>> THE_WORLDLINESS_PSEUDO_ENTRY = ResourceKey.createRegistryKey(ResourceLocation.parse("patchouli_books/the_worldliness/en_us/entries/"));

    private final PackOutput.PathProvider pathProvider;
    private final List<SubProvider<PseudoEntryHolder>> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public TheWorldlinessEntryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, List<SubProvider<PseudoEntryHolder>> subProviders) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, Registries.elementsDirPath(THE_WORLDLINESS_PSEUDO_ENTRY));
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<PseudoEntryHolder> consumer = subProvider -> {
                if (!set.add(subProvider.id())) {
                    throw new IllegalStateException("Duplicate fantazic loot modifier " + subProvider.id());
                } else {
                    Path path = this.pathProvider.json(subProvider.id());
                    list.add(DataProvider.saveStable(cachedOutput, provider, PseudoEntry.CODEC, subProvider.entry(), path));
                }
            };

            for(SubProvider<PseudoEntryHolder> lootModifierSubProvider : this.subProviders) {
                lootModifierSubProvider.generate(provider, consumer);
            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "TheWorldlinessEntries";
    }


}
