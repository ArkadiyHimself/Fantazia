package net.arkadiyhimself.fantazia.data.datagen.patchouli.categories;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.datagen.SubProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.TheWorldlinessEntryHelper;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.Categories;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntry;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoEntryHolder;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.PseudoPage;
import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Consumer;

public class WeaponCategoryEntries implements SubProvider<PseudoEntryHolder> {

    private static final String MELEE = "book.fantazia.heading.weapons.melee_combat";
    private static final String RANGED = "book.fantazia.heading.weapons.ranged_combat";

    private Consumer<PseudoEntryHolder> consumer;

    public static WeaponCategoryEntries create() {
        return new WeaponCategoryEntries();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<PseudoEntryHolder> consumer) {
        this.consumer = consumer;
        singleWeapon(FTZItems.FRAGILE_BLADE, MELEE);
        weaponType("hatchets", FTZItems.IRON_HATCHET.value(), RANGED);
    }

    private void singleWeapon(DeferredItem<? extends Item> weapon, String heading) {
        String name = weapon.asItem().getDescriptionId();
        ResourceLocation itemId = weapon.getId();
        ResourceLocation advancement = Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/" + Categories.WEAPONS.getPath() + "/" + itemId.getPath());

        PseudoEntry.Builder builder = PseudoEntry.builder().name(name).category(Categories.WEAPONS).icon(itemId).advancement(advancement);

        String basicText = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + Categories.WEAPONS.getPath() + "." + itemId.getPath() + ".page.";

        builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicText + 1).build());
        builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.CRAFTING).text(basicText + 2)
                .title(heading).recipe(weapon.asItem()).build());

        builder.build().save(consumer, weapon.getId().withPrefix(Categories.WEAPONS.getPath() + "/"));
    }

    private void weaponType(String entry, Item icon, String heading) {
        String name = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + Categories.WEAPONS.getPath() + "." + entry + ".name";
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(icon);
        ResourceLocation advancement = Fantazia.location(TheWorldlinessEntryHelper.THE_WORLDLINESS + "/" + Categories.WEAPONS.getPath() + "/" + entry);

        String basicText = "book." + Fantazia.MODID + "." + TheWorldlinessEntryHelper.THE_WORLDLINESS + "." + Categories.WEAPONS.getPath() + "." + entry + ".";

        PseudoEntry.Builder builder = PseudoEntry.builder().name(basicText + "name").icon(itemId).advancement(advancement).category(Categories.WEAPONS);


        builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.TEXT).text(basicText + "page." + 1).build());
        builder.addPseudoPage(PseudoPage.builder().type(TheWorldlinessEntryHelper.CRAFTING).text(basicText + "page." + 2)
                .title(heading).recipe(icon).build());

        builder.build().save(consumer, Fantazia.location(Categories.WEAPONS.getPath() + "/" + entry));
    }
}
