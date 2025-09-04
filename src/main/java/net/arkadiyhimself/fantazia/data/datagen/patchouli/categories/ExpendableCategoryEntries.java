package net.arkadiyhimself.fantazia.data.datagen.patchouli.categories;

import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.Categories;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public class ExpendableCategoryEntries implements SubProvider<PseudoEntryHolder> {

    public static ExpendableCategoryEntries create() {
        return new ExpendableCategoryEntries();
    }

    private Consumer<PseudoEntryHolder> consumer;

    public static final String CONSUMABLE = "book.fantazia.heading.expendables.consumable";
    public static final String DISPENSABLE = "book.fantazia.heading.expendables.dispensable";
    public static final String INGREDIENT = "book.fantazia.heading.expendables.ingredient";

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoEntryHolder> consumer) {
        this.consumer = consumer;

        craftableExpendable(FTZItems.ANCIENT_SPARK.value(), DISPENSABLE);
        craftableExpendable(FTZItems.ARACHNID_EYE.value(), CONSUMABLE);
        spotlightExpendable(FTZItems.INSIGHT_ESSENCE.value(), CONSUMABLE);
        spotlightExpendable(FTZItems.OBSCURE_SUBSTANCE.value(), INGREDIENT);
        craftableExpendable(FTZItems.UNFINISHED_WINGS.value(), CONSUMABLE);
        spotlightExpendable(FTZItems.VITALITY_FRUIT.value(), CONSUMABLE);
    }

    private void craftableExpendable(Item item, String title) {
        TheWorldlinessEntryHelper.makeItemEntryCrafting(consumer, item, Categories.EXPENDABLES, title, 2, 2, true);
    }

    private void spotlightExpendable(Item item, String title) {
        TheWorldlinessEntryHelper.makeItemEntrySpotlight(consumer, item, Categories.EXPENDABLES, title, 2, 2, true);
    }
}
