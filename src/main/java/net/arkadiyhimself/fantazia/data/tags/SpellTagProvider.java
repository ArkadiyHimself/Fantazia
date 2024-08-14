package net.arkadiyhimself.fantazia.data.tags;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.spell.Spell;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.arkadiyhimself.fantazia.tags.FTZSpellTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SpellTagProvider extends IntrinsicHolderTagsProvider<Spell> {
    public SpellTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, FantazicRegistry.Keys.SPELL, pLookupProvider, (spell -> FantazicRegistry.BakedRegistries.SPELL.get().getResourceKey(spell).get()), Fantazia.MODID, existingFileHelper);
    }
    @Override
    @SuppressWarnings("ConstantConditions")
    protected void addTags(HolderLookup.@NotNull Provider pProvider) {
        tag(FTZSpellTags.NOT_BLOCKABLE);
        tag(FTZSpellTags.NOT_REFLECTABLE).add(FTZSpells.DEVOUR);
        tag(FTZSpellTags.THROUGH_WALLS).add(FTZSpells.SONIC_BOOM);
    }
}
