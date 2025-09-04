package net.arkadiyhimself.fantazia.data.datagen.tag_providers;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZSpellTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class FantazicSpellTagsProvider extends IntrinsicHolderTagsProvider<AbstractSpell> {

    public FantazicSpellTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, FantazicRegistries.Keys.SPELL, pLookupProvider, (spell -> FantazicRegistries.SPELLS.getResourceKey(spell).orElseThrow()), Fantazia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(FTZSpellTags.IS_CHAINED).add(
                Spells.REWIND.value(),
                Spells.WANDERERS_SPIRIT.value(),
                Spells.BOUNCE.value()
        );

        tag(FTZSpellTags.NOT_BLOCKABLE);
        tag(FTZSpellTags.NOT_REFLECTABLE).add(Spells.DEVOUR.value());
        tag(FTZSpellTags.THROUGH_WALLS).add(Spells.SONIC_BOOM.value());
    }
}
