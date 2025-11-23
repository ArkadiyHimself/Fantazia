package net.arkadiyhimself.fantazia.data.datagen.patchouli.categories;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.registries.FTZBlocks;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.Categories;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntry;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoPage;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class WorldCategoryEntries implements SubProvider<PseudoEntryHolder> {

    public static WorldCategoryEntries create() {
        return new WorldCategoryEntries();
    }

    private Consumer<PseudoEntryHolder> consumer;

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoEntryHolder> consumer) {
        this.consumer = consumer;

        simpleLore("fate", Items.BOOK, 4, true);
        simpleLore("mind", Items.BOOK, 4, true);
        simpleLore("chaos", Items.BOOK, 3, true);
        simpleLore("concept_of_consistency", Fantazia.location("textures/entries/concept_of_consistency.png"), 3, false);
        simpleLore("euphoria", Items.IRON_AXE, 2, true);
        simpleLore("talents", Items.BOOK, 4, true);

        withItemSpotlight("cleanse", Items.MILK_BUCKET, 3, false, 2);
        withItemSpotlight("fantazium", FTZItems.FANTAZIUM_INGOT.value(), 3, true, 2);
        withItemSpotlight(TheWorldlinessEntryHelper.THE_WORLDLINESS, FTZItems.THE_WORLDLINESS.value(), 5, false, 5);
        withItemSpotlight("runes", FTZItems.RUNE_WIELDER.value(), 2, true, 2);

        withImage("obscure_tree", FTZBlocks.OBSCURE_SAPLING.asItem(), Fantazia.location("textures/entries/obscure_tree.png"), 3, true, 2);
    }

    private void simpleLore(String entry, Item item, int pages, boolean unlocked) {
        String world = Categories.WORLD.getPath();
        String basicString = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + world + "." + entry + ".";
        ResourceLocation advancement = unlocked ? Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/" + world + "/" + entry) : null;

        PseudoEntry.Builder builder = PseudoEntry.builder().name(basicString + "ident").category(Categories.WORLD).advancement(advancement).icon(item);

        for (int i = 1; i <= pages; i++) builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicString + "page." + i).build());

        builder.build().save(consumer, Fantazia.location(world + "/" + entry));
    }

    private void simpleLore(String entry, ResourceLocation icon, int pages, boolean unlocked) {
        String world = Categories.WORLD.getPath();
        String basicString = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + world + "." + entry + ".";
        ResourceLocation advancement = unlocked ? Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/" + world + "/" + entry) : null;

        PseudoEntry.Builder builder = PseudoEntry.builder().name(basicString + "ident").category(Categories.WORLD).advancement(advancement).icon(icon);

        for (int i = 1; i <= pages; i++) builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicString + "page." + i).build());

        builder.build().save(consumer, Fantazia.location(world + "/" + entry));
    }

    private void withItemSpotlight(String entry, Item icon, int pages, boolean unlocked, int spotlight) {
        String world = Categories.WORLD.getPath();
        String basicString = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + world + "." + entry + ".";
        ResourceLocation advancement = unlocked ? Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/" + world + "/" + entry) : null;

        PseudoEntry.Builder builder = PseudoEntry.builder().name(basicString + "ident").category(Categories.WORLD).advancement(advancement).icon(icon);

        for (int i = 1; i <= pages; i++) {
            PseudoPage.Builder pseudoPage = PseudoPage.builder().text(basicString + "page." + i);
            if (i == spotlight) pseudoPage.type(TheWorldlinessEntryHelper.SPOTLIGHT).item(icon);
            else pseudoPage.type(TheWorldlinessEntryHelper.TEXT);

            builder.addPseudoPage(pseudoPage.build());
        }

        builder.build().save(consumer, Fantazia.location(world + "/" + entry));
    }

    private void withImage(String entry, Item icon, ResourceLocation image, int pages, boolean unlocked, int imagePage) {
        String world = Categories.WORLD.getPath();
        String basicString = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + world + "." + entry + ".";
        ResourceLocation advancement = unlocked ? Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/" + world + "/" + entry) : null;

        PseudoEntry.Builder builder = PseudoEntry.builder().name(basicString + "ident").category(Categories.WORLD).advancement(advancement).icon(icon);

        for (int i = 1; i <= pages; i++) {
            PseudoPage.Builder pseudoPage = PseudoPage.builder().text(basicString + "page." + i);
            if (i == imagePage) pseudoPage.type(TheWorldlinessEntryHelper.IMAGE).images(image).border();
            else pseudoPage.type(TheWorldlinessEntryHelper.TEXT);

            builder.addPseudoPage(pseudoPage.build());
        }

        builder.build().save(consumer, Fantazia.location(world + "/" + entry));
    }
}
