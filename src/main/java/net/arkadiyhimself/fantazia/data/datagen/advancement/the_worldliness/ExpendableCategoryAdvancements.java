package net.arkadiyhimself.fantazia.data.datagen.advancement.the_worldliness;

import net.arkadiyhimself.fantazia.common.registries.FTZItems;
import net.arkadiyhimself.fantazia.data.criterion.PossessItemTrigger;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class ExpendableCategoryAdvancements {

    public static void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        expendableCraftableObscureSubstance(consumer, FTZItems.ANCIENT_SPARK, Items.GUNPOWDER);
        expendableCraftableObscureSubstance(consumer, FTZItems.ARACHNID_EYE, Items.SPIDER_EYE);
        expendableObtained(consumer, FTZItems.INSIGHT_ESSENCE);
        expendableObtained(consumer, FTZItems.OBSCURE_SUBSTANCE);
        expendableCraftableObscureSubstance(consumer, FTZItems.UNFINISHED_WINGS, Items.PHANTOM_MEMBRANE);
        expendableObtained(consumer, FTZItems.VITALITY_FRUIT);
    }

    private static void expendableObtained(Consumer<AdvancementHolder> consumer, ItemLike expendable) {
        EntryAdvancementHolder holder = expendable();
        String name = BuiltInRegistries.ITEM.getKey(expendable.asItem()).getPath();
        holder.addCriterion(name, InventoryChangeTrigger.TriggerInstance.hasItems(expendable)).save(consumer, expendable);
    }

    private static void expendableCraftableObscureSubstance(Consumer<AdvancementHolder> consumer, ItemLike expendable, ItemLike... ingredients) {
        EntryAdvancementHolder holder = expendable();
        List<List<String>> requirements = Lists.newArrayList();
        String expendableCriteria = "found_" + BuiltInRegistries.ITEM.getKey(expendable.asItem()).getPath();
        String substanceCriteria = FTZItems.OBSCURE_SUBSTANCE.getId().getPath();
        holder.addCriterion(expendableCriteria, InventoryChangeTrigger.TriggerInstance.hasItems(expendable));
        holder.addCriterion(substanceCriteria, PossessItemTrigger.TriggerInstance.specificItems(FTZItems.OBSCURE_SUBSTANCE));
        for (ItemLike item : ingredients) {
            String name = BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
            Criterion<InventoryChangeTrigger.TriggerInstance> criterion = InventoryChangeTrigger.TriggerInstance.hasItems(item);
            holder.addCriterion(name, criterion);
            requirements.add(List.of(name, expendableCriteria));
        }
        requirements.add(List.of(expendableCriteria, substanceCriteria));
        holder.requirements(new AdvancementRequirements(requirements));
        holder.save(consumer, expendable);
    }

    private static void expendableCraftable(Consumer<AdvancementHolder> consumer, ItemLike expendable, ItemLike... ingredients) {
        EntryAdvancementHolder holder = expendable();
        for (ItemLike item : ingredients) {
            String name = BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
            Criterion<InventoryChangeTrigger.TriggerInstance> criterion = InventoryChangeTrigger.TriggerInstance.hasItems(item);
            holder.addCriterion(name, criterion);
        }
        holder.save(consumer, expendable);
    }

    public static EntryAdvancementHolder expendable() {
        return new EntryAdvancementHolder("expendables");
    }
}
