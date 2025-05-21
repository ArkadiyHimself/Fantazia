package net.arkadiyhimself.fantazia.datagen.patchouli;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZCreativeModeTabs;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TheWorldlinessProvider implements DataProvider {

    public static final ResourceKey<Registry<PseudoEntry>> THE_WORLDLINESS = ResourceKey.createRegistryKey(ResourceLocation.parse("patchouli_books/the_worldliness/"));

    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public TheWorldlinessProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, Registries.elementsDirPath(THE_WORLDLINESS));
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cachedOutput) {
        return registries.thenCompose(provider -> {
            List<CompletableFuture<?>> list = new ArrayList<>();

            Path path = this.pathProvider.json(Fantazia.res("book"));
            list.add(DataProvider.saveStable(cachedOutput, provider, TheWorldliness.CODEC, new TheWorldliness(), path));

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public @NotNull String getName() {
        return "TheWorldlinessBook";
    }

    private static class TheWorldliness {
        static final Codec<TheWorldliness> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(b -> FTZItems.THE_WORLDLINESS.asItem().getDescriptionId()),
                Codec.STRING.fieldOf("landing_text").forGetter(b -> "book.fantazia.the_worldliness"),
                Codec.INT.fieldOf("version").forGetter(b -> 1),
                BuiltInRegistries.CREATIVE_MODE_TAB.byNameCodec().fieldOf("creative_tab").forGetter(b -> FTZCreativeModeTabs.ARTIFACTS),
                Codec.BOOL.fieldOf("i18n").forGetter(b -> true),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("custom_book_item").forGetter(b -> FTZItems.THE_WORLDLINESS.asItem()),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("model").forGetter(b -> FTZItems.THE_WORLDLINESS.asItem()),
                ResourceLocation.CODEC.fieldOf("book_texture").forGetter(b -> Fantazia.res("textures/gui/the_worldliness.png")),
                ResourceLocation.CODEC.fieldOf("crafting_texture").forGetter(b -> Fantazia.res("textures/gui/the_worldliness_crafting.png")),
                Codec.BOOL.fieldOf("use_resource_pack").forGetter(b -> true),
                Codec.BOOL.fieldOf("show_progress").forGetter(b -> false),
                Codec.STRING.fieldOf("text_color").forGetter(b -> "9A14F7"),
                Codec.BOOL.fieldOf("use_blocky_font").forGetter(b -> true),
                Codec.STRING.fieldOf("header_color").forGetter(b -> "4D00CB"),
                Codec.STRING.fieldOf("nameplate_color").forGetter(b -> "8944FC")
        ).apply(instance, (s, s2, integer, creativeModeTab, aBoolean, item, item2, location, location2, aBoolean2, aBoolean3, s3, aBoolean4, s4, s5) -> null));
    }
}
