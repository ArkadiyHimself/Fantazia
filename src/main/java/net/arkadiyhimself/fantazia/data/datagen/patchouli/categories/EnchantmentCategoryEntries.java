package net.arkadiyhimself.fantazia.data.datagen.patchouli.categories;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.Categories;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntry;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoPage;
import net.arkadiyhimself.fantazia.common.registries.FTZEnchantments;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.function.Consumer;

public class EnchantmentCategoryEntries implements SubProvider<PseudoEntryHolder> {

    private Consumer<PseudoEntryHolder> enrtyConsumer;
    private HolderLookup.Provider provider;
    private static final ResourceLocation advancement = Fantazia.location("the_worldliness/enchantments/generic");

    private static final String CROSSBOW = "book.fantazia.heading.enchantments.crossbow";
    private static final String SHARP_WEAPON = "book.fantazia.heading.enchantments.sharp_weapon";
    private static final String SWORD = "book.fantazia.heading.enchantments.sword";
    private static final String BOW = "book.fantazia.heading.enchantments.bow";
    private static final String HATCHET = "book.fantazia.heading.enchantments.hatchet";

    public static EnchantmentCategoryEntries create() {
        return new EnchantmentCategoryEntries();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoEntryHolder> consumer) {
        this.enrtyConsumer = consumer;
        this.provider = provider;

        simpleEnchantment(FTZEnchantments.BULLY, SHARP_WEAPON, List.of(), List.of(ItemTags.SHARP_WEAPON_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.DECISIVE_STRIKE, SWORD, List.of(), List.of(ItemTags.SWORD_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.DISINTEGRATION, SWORD, List.of(), List.of(ItemTags.SWORD_ENCHANTABLE));
        simpleEnchantment("duelist_ballista", CROSSBOW, List.of(), List.of(ItemTags.CROSSBOW_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.FREEZE, BOW, List.of(), List.of(ItemTags.BOW_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.BULLSEYE, HATCHET, List.of(), List.of(FTZItemTags.HATCHET_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.ICE_ASPECT, SWORD, List.of(), List.of(ItemTags.FIRE_ASPECT_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.PHASING, HATCHET, List.of(), List.of(FTZItemTags.HATCHET_ENCHANTABLE));
        simpleEnchantment(FTZEnchantments.RICOCHET, HATCHET, List.of(), List.of(FTZItemTags.HATCHET_ENCHANTABLE));
    }

    private void simpleEnchantment(ResourceKey<Enchantment> resourceKey, String title, List<Item> items, List<TagKey<Item>> itemTags) {
        String enchantments = Categories.ENCHANTMENTS.getPath();
        String enchantment = resourceKey.location().getPath();

        PseudoEntry.Builder builder = PseudoEntry.builder();
        String name = Util.makeDescriptionId("enchantment", resourceKey.location());
        builder.name(name).advancement(advancement).icon(Items.ENCHANTED_BOOK).category(Categories.ENCHANTMENTS);

        String basicTitle = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + enchantments + "." + enchantment + ".page.";
        PseudoPage.Builder page1 = PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicTitle + 1);
        builder.addPseudoPage(page1.build());

        PseudoPage.Builder page2 = PseudoPage.builder().type(TheWorldlinessEntryHelper.SPOTLIGHT).title(title);
        for (Item item : items) page2.item(item);
        for (TagKey<Item> itemTag : itemTags) page2.item(itemTag).text(basicTitle + 2);
        builder.addPseudoPage(page2.build());

        builder.build().save(enrtyConsumer, resourceKey.location().withPrefix(Categories.ENCHANTMENTS.getPath() + "/"));
    }

    private void simpleEnchantment(String entryName, String title, List<Item> items, List<TagKey<Item>> itemTags) {
        String enchantments = Categories.ENCHANTMENTS.getPath();

        PseudoEntry.Builder builder = PseudoEntry.builder();
        String name = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + Categories.ENCHANTMENTS.getPath() + "." + entryName + ".name";
        builder.name(name).advancement(advancement).icon(Items.ENCHANTED_BOOK).category(Categories.ENCHANTMENTS);

        String basicTitle = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + enchantments + "." + entryName + ".page.";
        PseudoPage.Builder page1 = PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicTitle + 1);
        builder.addPseudoPage(page1.build());

        PseudoPage.Builder page2 = PseudoPage.builder().type(TheWorldlinessEntryHelper.SPOTLIGHT).title(title);
        for (Item item : items) page2.item(item);
        for (TagKey<Item> itemTag : itemTags) page2.item(itemTag).text(basicTitle + 2);
        builder.addPseudoPage(page2.build());

        builder.build().save(enrtyConsumer, Fantazia.location(entryName).withPrefix(Categories.ENCHANTMENTS.getPath() + "/"));
    }
}
