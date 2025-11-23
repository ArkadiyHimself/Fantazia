package net.arkadiyhimself.fantazia.data.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.data.tags.FTZEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicEntityTypeTagsProvider extends EntityTypeTagsProvider {

    public FantazicEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia

        tag(FTZEntityTypeTags.RANGED_ATTACK).add(
                EntityType.BLAZE,
                EntityType.ENDER_DRAGON,
                EntityType.GHAST,
                EntityType.LLAMA,
                EntityType.SNOW_GOLEM,
                EntityType.WITHER
        );
        tag(FTZEntityTypeTags.AERIAL).add(
                EntityType.ALLAY,
                EntityType.BAT,
                EntityType.BEE,
                EntityType.BLAZE,
                EntityType.ENDER_DRAGON,
                EntityType.GHAST,
                EntityType.PARROT,
                EntityType.PHANTOM,
                EntityType.VEX,
                EntityType.WITHER
        );
        tag(FTZEntityTypeTags.VALID_WANDERERS_SPIRIT_TARGET).add(
                EntityType.FOX,
                EntityType.PIG,
                EntityType.FROG,
                EntityType.STRIDER,
                EntityType.OCELOT,
                EntityType.PANDA,
                EntityType.GOAT,
                EntityType.COW,
                EntityType.RABBIT,
                EntityType.CHICKEN,
                EntityType.BEE,
                EntityType.HORSE,
                EntityType.TURTLE,
                EntityType.AXOLOTL,
                EntityType.ARMADILLO,
                EntityType.SNIFFER,
                EntityType.WOLF,
                EntityType.PARROT,
                EntityType.CAT,
                EntityType.SHEEP,
                EntityType.WANDERING_TRADER,
                EntityType.VILLAGER
        );
        tag(FTZEntityTypeTags.IS_FROM_A_RECHARGEABLE_TOOL).add(
                FTZEntityTypes.PIMPILLO.value(),
                FTZEntityTypes.THROWN_PIN.value()
        );
    }
}
