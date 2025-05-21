package net.arkadiyhimself.fantazia.datagen.advancement.the_worldliness;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.criterion.PossessItemTrigger;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class WeaponCategoryAdvancements {

    public static void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        weaponCraftableSithSubstanceAndFantazium(consumer, FTZItems.FRAGILE_BLADE, Items.GLASS, Items.NETHERITE_INGOT);

        weaponSave().addCriterion("stick", InventoryChangeTrigger.TriggerInstance.hasItems(Items.STICK))
                .addCriterion("hatchet", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(FTZItemTags.HATCHETS)))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(consumer, Fantazia.res("hatchets"));
    }

    private static void weaponCraftableSithSubstanceAndFantazium(Consumer<AdvancementHolder> consumer, ItemLike artifact, ItemLike... ingredients) {
        EntryAdvancementHolder holder = weaponSave();
        for (ItemLike item : ingredients) {
            String name = BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
            Criterion<InventoryChangeTrigger.TriggerInstance> criterion = InventoryChangeTrigger.TriggerInstance.hasItems(item);
            holder.addCriterion(name, criterion);
        }
        holder.addCriterion(FTZItems.OBSCURE_SUBSTANCE.getId().getPath(), PossessItemTrigger.TriggerInstance.specificItems(FTZItems.OBSCURE_SUBSTANCE));
        holder.addCriterion("fantazium", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(FTZItemTags.FROM_FANTAZIUM)));
        holder.save(consumer, artifact);
    }

    private static EntryAdvancementHolder weaponSave() {
        return new EntryAdvancementHolder("weapons");
    }
}
