package net.arkadiyhimself.fantazia.datagen.loot_modifier;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.datagen.patchouli.PseudoEntry;
import net.arkadiyhimself.fantazia.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.datagen.patchouli.PseudoPage;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class TheWorldlinessEntryHelper {

    public static final String THE_WORLDLINESS = "the_worldliness";

    public static final ResourceLocation TEXT = ResourceLocation.parse("patchouli:text");
    public static final ResourceLocation SPOTLIGHT = ResourceLocation.parse("patchouli:spotlight");
    public static final ResourceLocation IMAGE = ResourceLocation.parse("patchouli:image");
    public static final ResourceLocation CRAFTING = ResourceLocation.parse("patchouli:crafting");

    public static void makeItemEntrySpotlight(Consumer<PseudoEntryHolder> consumer, Item item, ResourceLocation category, @Nullable String title, int pages, int spotlight, boolean withAdvancement) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (!itemId.getNamespace().equals(Fantazia.MODID)) throw new IllegalStateException("This helper is exclusively for The Worldliness entries!");
        String name = Util.makeDescriptionId("item", itemId);
        ResourceLocation advancement = null;
        if (withAdvancement) advancement = itemId.withPrefix(THE_WORLDLINESS + "/" + category.getPath() + "/");
        PseudoEntry.Builder pseudoEntry = PseudoEntry.builder();
        pseudoEntry.icon(item).name(name).category(category).advancement(advancement);

        String basicText = "book." + itemId.getNamespace() + "." + THE_WORLDLINESS + "." + category.getPath() + "." + itemId.getPath() + ".page.";

        for (int i = 1; i <= pages; i++) {
            PseudoPage.Builder pseudoPage = PseudoPage.builder();
            pseudoPage.text(basicText + i);
            if (i == spotlight) {
                pseudoPage.type(SPOTLIGHT);
                pseudoPage.item(item);
                if (title != null) pseudoPage.title(title);
            } else {
                pseudoPage.type(TEXT);
            }
            pseudoEntry.addPseudoPage(pseudoPage.build());
        }

        pseudoEntry.build().save(consumer, itemId.withPrefix(category.getPath() + "/"));
    }

    public static void makeItemEntryCrafting(Consumer<PseudoEntryHolder> consumer, Item item, ResourceLocation category, @Nullable String title, int pages, int crafting, boolean withAdvancement) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (!itemId.getNamespace().equals(Fantazia.MODID)) throw new IllegalStateException("This helper is exclusively for The Worldliness entries!");
        String name = Util.makeDescriptionId("item", itemId);
        ResourceLocation advancement = null;
        if (withAdvancement) advancement = itemId.withPrefix(THE_WORLDLINESS + "/" + category.getPath() + "/");
        PseudoEntry.Builder pseudoEntry = PseudoEntry.builder();
        pseudoEntry.icon(item).name(name).category(category).advancement(advancement);

        String basicText = "book." + itemId.getNamespace() + "." + THE_WORLDLINESS + "." + category.getPath() + "." + itemId.getPath() + ".page.";

        for (int i = 1; i <= pages; i++) {
            PseudoPage.Builder pseudoPage = PseudoPage.builder();
            pseudoPage.text(basicText + i);
            if (i == crafting) {
                pseudoPage.type(CRAFTING);
                pseudoPage.recipe(item);
                if (title != null) pseudoPage.title(title);
            } else {
                pseudoPage.type(TEXT);
            }
            pseudoEntry.addPseudoPage(pseudoPage.build());
        }

        pseudoEntry.build().save(consumer, itemId.withPrefix(category.getPath() + "/"));
    }
}
