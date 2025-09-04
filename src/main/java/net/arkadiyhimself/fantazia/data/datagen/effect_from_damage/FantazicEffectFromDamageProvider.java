package net.arkadiyhimself.fantazia.data.datagen.effect_from_damage;

import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.effect_from_damage.EffectFromDamage;
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

public class FantazicEffectFromDamageProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final List<SubProvider<EffectFromDamageHolder>> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public FantazicEffectFromDamageProvider(PackOutput output, List<SubProvider<EffectFromDamageHolder>> subProviders, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createRegistryElementsPathProvider(FantazicRegistries.Keys.EFFECT_FROM_DAMAGE);
        this.subProviders = subProviders;
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return this.registries.thenCompose(provider -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<EffectFromDamageHolder> consumer = subProvider -> {
                if (!set.add(subProvider.id())) {
                    throw new IllegalStateException("Duplicate fantazic effect applier: " + subProvider.id());
                } else {
                    Path path = this.pathProvider.json(subProvider.id());
                    list.add(DataProvider.saveStable(cachedOutput, provider, EffectFromDamage.Builder.CODEC, subProvider.builder(), path));
                }
            };

            for(SubProvider<EffectFromDamageHolder> effectApplierSubProvider : this.subProviders) effectApplierSubProvider.generate(provider, consumer);


            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "EffectFromDamage";
    }
}
