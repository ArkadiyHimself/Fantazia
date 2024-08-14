package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.registries.custom.FTZHealingTypes;
import net.arkadiyhimself.fantazia.tags.FTZHealingTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class HealingTypeTagsProvider extends TagsProvider<HealingType> {
    public HealingTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, FantazicRegistry.Keys.HEALING_TYPE, pLookupProvider, Fantazia.MODID, existingFileHelper);
    }
    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(FTZHealingTypeTags.REGEN).add(FTZHealingTypes.REGEN_AURA, FTZHealingTypes.NATURAL_REGEN, FTZHealingTypes.MOB_EFFECT_REGEN);
        tag(FTZHealingTypeTags.MOB_EFFECT).add(FTZHealingTypes.MOB_EFFECT, FTZHealingTypes.MOB_EFFECT_REGEN);
        tag(FTZHealingTypeTags.BYPASSES_INVULNERABILITY).add();
        tag(FTZHealingTypeTags.SCALES_FROM_SATURATION).add(FTZHealingTypes.NATURAL_REGEN);
        tag(FTZHealingTypeTags.NOT_CANCELLABLE).add();
        tag(FTZHealingTypeTags.SELF).add(FTZHealingTypes.NATURAL_REGEN, FTZHealingTypes.MOB_EFFECT_REGEN, FTZHealingTypes.GENERIC);
        tag(FTZHealingTypeTags.CONSENSUAL).add(FTZHealingTypes.REGEN_AURA);
        tag(FTZHealingTypeTags.UNHOLY).add(FTZHealingTypes.LIFESTEAL, FTZHealingTypes.DEVOUR);
    }
}
