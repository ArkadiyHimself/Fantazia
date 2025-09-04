package net.arkadiyhimself.fantazia.data.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZPaintingVariants;
import net.arkadiyhimself.fantazia.data.tags.FTZPaintingVariantTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicPaintingVariantTagsProvider extends TagsProvider<PaintingVariant> {

    public FantazicPaintingVariantTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.PAINTING_VARIANT, lookupProvider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(FTZPaintingVariantTags.FANTAZIC_PLACAEBLE).add(
                FTZPaintingVariants.FANTAZIA,
                FTZPaintingVariants.KAPITON,
                FTZPaintingVariants.MICATLANGELO,
                FTZPaintingVariants.JAMES_CATFIELD
        );
    }
}
