package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.registries.custom.HealingTypes;
import net.arkadiyhimself.fantazia.tags.FTZHealingTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicHealingTypeTagsProvider extends TagsProvider<HealingType> {

    public FantazicHealingTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, FantazicRegistries.Keys.HEALING_TYPE, pLookupProvider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(FTZHealingTypeTags.BYPASSES_INVULNERABILITY).add();
        tag(FTZHealingTypeTags.CONSENSUAL).add(HealingTypes.REGEN_AURA);
        tag(FTZHealingTypeTags.MOB_EFFECT).add(HealingTypes.MOB_EFFECT, HealingTypes.MOB_EFFECT_REGEN);
        tag(FTZHealingTypeTags.NOT_CANCELLABLE).add();
        tag(FTZHealingTypeTags.REGEN).add(HealingTypes.REGEN_AURA, HealingTypes.NATURAL_REGEN, HealingTypes.MOB_EFFECT_REGEN);
        tag(FTZHealingTypeTags.SCALES_FROM_SATURATION).add(HealingTypes.NATURAL_REGEN);
        tag(FTZHealingTypeTags.SELF).add(HealingTypes.NATURAL_REGEN, HealingTypes.MOB_EFFECT_REGEN, HealingTypes.GENERIC);
        tag(FTZHealingTypeTags.UNHOLY).add(HealingTypes.DEVOUR, HealingTypes.LIFESTEAL);
    }
}
