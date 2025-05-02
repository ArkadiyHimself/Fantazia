package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZEntityTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
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
        tag(FTZMobEffects.Application.getWhiteList(FTZMobEffects.DEAFENED)).add(EntityType.WARDEN, EntityType.PLAYER);
        tag(FTZMobEffects.Application.getBlackList(FTZMobEffects.ELECTROCUTED)).add(EntityType.CREEPER, EntityType.ZOMBIFIED_PIGLIN, EntityType.WITCH, EntityType.MOOSHROOM);
        tag(FTZMobEffects.Application.getBlackList(FTZMobEffects.HAEMORRHAGE)).add(EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.WARDEN, EntityType.SNOW_GOLEM, EntityType.IRON_GOLEM, EntityType.VEX, EntityType.ALLAY, EntityType.SHULKER, EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN).addTag(EntityTypeTags.SKELETONS);
        tag(FTZEntityTypeTags.AERIAL).add(EntityType.BLAZE, EntityType.ENDER_DRAGON, EntityType.GHAST,  EntityType.LLAMA, EntityType.SNOW_GOLEM, EntityType.WITCH, EntityType.WITHER);
        tag(FTZEntityTypeTags.RANGED_ATTACK).add(EntityType.ALLAY, EntityType.BAT, EntityType.BEE, EntityType.BLAZE, EntityType.ENDER_DRAGON, EntityType.GHAST, EntityType.PARROT, EntityType.PHANTOM, EntityType.VEX, EntityType.WITHER);
    }
}
