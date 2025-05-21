package net.arkadiyhimself.fantazia.datagen.patchouli;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class TheWorldlinessCategories implements SubProvider<PseudoCategoryHolder> {

    public static TheWorldlinessCategories create() {
        return new TheWorldlinessCategories();
    }

    private Consumer<PseudoCategoryHolder> consumer;

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoCategoryHolder> consumer) {
        this.consumer = consumer;
        bakeCategory(Categories.ARTIFACTS, FTZItems.ENTANGLER.asItem());
        bakeCategory(Categories.ENCHANTMENTS, Items.ENCHANTED_BOOK);
        bakeCategory(Categories.EXPENDABLES, FTZItems.OBSCURE_SUBSTANCE.asItem());
        bakeCategory(Categories.MOB_EFFECTS, Fantazia.res("textures/mob_effect/fury.png"));
        bakeCategory(Categories.WEAPONS, FTZItems.FRAGILE_BLADE.asItem());
        bakeCategory(Categories.WORLD, Items.BOOK);
    }

    private void bakeCategory(ResourceLocation category, Item icon) {
        String name = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + category.getPath() + ".name";
        String description = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + category.getPath() + ".desc";
        PseudoCategory.builder().name(name).description(description).icon(icon).build().save(consumer, category);
    }

    private void bakeCategory(ResourceLocation category, ResourceLocation icon) {
        String name = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + category.getPath() + ".name";
        String description = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + category.getPath() + ".desc";
        PseudoCategory.builder().name(name).description(description).icon(icon).build().save(consumer, category);
    }
}
