package net.arkadiyhimself.fantazia.data.datagen.patchouli.categories;

import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.Categories;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.common.item.casters.AuraCasterItem;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Consumer;

public class ArtifactCategoryEntries implements SubProvider<PseudoEntryHolder> {

    private static final String AURACASTER = "book.fantazia.heading.artifacts.auracaster";
    private static final String SPELLCASTER = "book.fantazia.heading.artifacts.spellcaster";
    private static final String APPLIANCE = "book.fantazia.heading.artifacts.appliance";

    private Consumer<PseudoEntryHolder> enrtyConsumer;

    public static ArtifactCategoryEntries create() {
        return new ArtifactCategoryEntries();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoEntryHolder> consumer) {
        this.enrtyConsumer = consumer;
        auraCasterSpotLight(FTZItems.ACID_BOTTLE);
        auraCasterCraftable(FTZItems.AMPLIFIED_ICE);
        spellCasterSpotLight(FTZItems.ATHAME);
        spellCasterSpotLight(FTZItems.BLOODLUST_AMULET);
        spellCasterSpotLight(FTZItems.BROKEN_STAFF);
        spellCasterCraftable(FTZItems.CARD_DECK);
        spellCasterSpotLight(FTZItems.CAUGHT_THUNDER);
        spellCasterSpotLight(FTZItems.CONTAINED_SOUND);
        TheWorldlinessEntryHelper.makeItemEntrySpotlight(consumer, FTZItems.DASHSTONE.value(), Categories.ARTIFACTS, "book.fantazia.the_worldliness.artifacts.dashstone.spotlight", 2, 2, true);
        spellCasterCraftable(FTZItems.ENIGMATIC_CLOCK);
        spellCasterCraftable(FTZItems.ENTANGLER);
        spellCasterSpotLight(FTZItems.HEART_OF_SCULK);
        auraCasterSpotLight(FTZItems.LEADERS_HORN);
        spellCasterCraftable(FTZItems.MYSTIC_MIRROR);
        auraCasterCraftable(FTZItems.NECKLACE_OF_CLAIRVOYANCE);
        auraCasterSpotLight(FTZItems.NETHER_HEART);
        spellCasterCraftable(FTZItems.NIMBLE_DAGGER);
        auraCasterCraftable(FTZItems.OPTICAL_LENS);
        spellCasterCraftable(FTZItems.PUPPET_DOLL);
        spellCasterCraftable(FTZItems.ROAMERS_COMPASS);
        spellCasterCraftable(FTZItems.RUSTY_RING);
        spellCasterCraftable(FTZItems.SANDMANS_DUST);
        spellCasterSpotLight(FTZItems.SOUL_EATER);
        auraCasterSpotLight(FTZItems.SPIRAL_NEMESIS);
        auraCasterSpotLight(FTZItems.TRANQUIL_HERB);
        TheWorldlinessEntryHelper.makeItemEntryCrafting(consumer, FTZItems.WISDOM_CATCHER.value(), Categories.ARTIFACTS, APPLIANCE, 3, 2, true);
        spellCasterSpotLight(FTZItems.WITHERS_QUINTESSENCE);
    }

    private void auraCasterSpotLight(DeferredItem<AuraCasterItem> auraCasterItem) {
        TheWorldlinessEntryHelper.makeItemEntrySpotlight(enrtyConsumer, auraCasterItem.value(), Categories.ARTIFACTS, AURACASTER, 3, 2, true);
    }

    private void spellCasterSpotLight(DeferredItem<SpellCasterItem> spellCasterItem) {
        TheWorldlinessEntryHelper.makeItemEntrySpotlight(enrtyConsumer, spellCasterItem.value(), Categories.ARTIFACTS, SPELLCASTER, 3, 2,true);
    }

    private void spellCasterCraftable(DeferredItem<SpellCasterItem> spellCasterItem) {
        TheWorldlinessEntryHelper.makeItemEntryCrafting(enrtyConsumer, spellCasterItem.value(), Categories.ARTIFACTS, SPELLCASTER, 3, 2,true);
    }

    private void auraCasterCraftable(DeferredItem<AuraCasterItem> auraCasterItem) {
        TheWorldlinessEntryHelper.makeItemEntryCrafting(enrtyConsumer, auraCasterItem.value(), Categories.ARTIFACTS, AURACASTER, 3, 2, true);
    }
}
