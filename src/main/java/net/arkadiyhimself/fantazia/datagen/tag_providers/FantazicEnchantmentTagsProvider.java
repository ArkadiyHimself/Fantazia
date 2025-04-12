package net.arkadiyhimself.fantazia.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.tags.FTZEnchantmentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicEnchantmentTagsProvider extends EnchantmentTagsProvider {

    public FantazicEnchantmentTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // fantazia
        tag(FTZEnchantmentTags.CROSSBOW_DAMAGE_EXCLUSIVE).add(FTZEnchantments.DUELIST, FTZEnchantments.BALLISTA);
        tag(FTZEnchantmentTags.HATCHET_BEHAVIOUR_EXCLUSIVE).add(FTZEnchantments.PHASING, FTZEnchantments.RICOCHET);

        // minecraft
        tag(EnchantmentTags.DAMAGE_EXCLUSIVE).add(FTZEnchantments.BULLY);
    }
}
