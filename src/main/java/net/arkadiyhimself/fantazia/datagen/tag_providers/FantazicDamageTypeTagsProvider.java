package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicDamageTypeTagsProvider extends DamageTypeTagsProvider {
    public FantazicDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia
        tag(FTZDamageTypeTags.ELECTRIC).add(DamageTypes.LIGHTNING_BOLT, FTZDamageTypes.ELECTRIC);
        tag(FTZDamageTypeTags.IS_ANCIENT_FLAME).add(FTZDamageTypes.ANCIENT_FLAME, FTZDamageTypes.ANCIENT_BURNING);
        tag(FTZDamageTypeTags.NO_HURT_SOUND).add(FTZDamageTypes.BLEEDING, FTZDamageTypes.REMOVAL);
        tag(FTZDamageTypeTags.NON_LETHAL).add(FTZDamageTypes.REMOVAL);
        tag(FTZDamageTypeTags.NOT_SHAKING_SCREEN).add(FTZDamageTypes.BLEEDING, FTZDamageTypes.REMOVAL);
        tag(FTZDamageTypeTags.NOT_STOPPING_DASH).add(DamageTypes.CRAMMING, DamageTypes.DROWN, DamageTypes.STARVE, DamageTypes.GENERIC, DamageTypes.IN_WALL).addTag(DamageTypeTags.NO_KNOCKBACK);
        tag(FTZDamageTypeTags.NOT_TURNING_RED).add(FTZDamageTypes.BLEEDING, FTZDamageTypes.REMOVAL);
        tag(FTZDamageTypeTags.PIERCES_BARRIER).add(DamageTypes.CRAMMING, DamageTypes.DROWN, DamageTypes.STARVE, DamageTypes.GENERIC, DamageTypes.GENERIC_KILL, DamageTypes.IN_WALL, FTZDamageTypes.SHOCKWAVE).addTag(FTZDamageTypeTags.NON_LETHAL);

        // minecraft
        tag(DamageTypeTags.BYPASSES_ARMOR).add(FTZDamageTypes.REMOVAL, FTZDamageTypes.ANCIENT_FLAME, FTZDamageTypes.ANCIENT_BURNING, FTZDamageTypes.BLEEDING, FTZDamageTypes.FROZEN, FTZDamageTypes.PARRY, FTZDamageTypes.ELECTRIC);
        tag(DamageTypeTags.BYPASSES_COOLDOWN).add(FTZDamageTypes.BLEEDING, FTZDamageTypes.REMOVAL);
        tag(DamageTypeTags.BYPASSES_EFFECTS).add(FTZDamageTypes.BLEEDING, FTZDamageTypes.REMOVAL, FTZDamageTypes.ANCIENT_FLAME, FTZDamageTypes.ANCIENT_BURNING);
        tag(DamageTypeTags.BYPASSES_INVULNERABILITY).add(FTZDamageTypes.REMOVAL);
        tag(DamageTypeTags.NO_IMPACT).add(FTZDamageTypes.REMOVAL, FTZDamageTypes.ANCIENT_FLAME, FTZDamageTypes.ANCIENT_BURNING, FTZDamageTypes.BLEEDING, FTZDamageTypes.FROZEN, FTZDamageTypes.ELECTRIC);
        tag(DamageTypeTags.NO_KNOCKBACK).add(FTZDamageTypes.REMOVAL, FTZDamageTypes.ANCIENT_FLAME, FTZDamageTypes.ANCIENT_BURNING, FTZDamageTypes.BLEEDING, FTZDamageTypes.FROZEN, FTZDamageTypes.ELECTRIC);
        tag(DamageTypeTags.IS_PROJECTILE).add(FTZDamageTypes.HATCHET, FTZDamageTypes.SIMPLE_CHASING_PROJECTILE);
    }
}
