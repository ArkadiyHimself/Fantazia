package net.arkadiyhimself.fantazia.datagen.advancement.the_worldliness;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.criterion.EuphoriaTrigger;
import net.arkadiyhimself.fantazia.data.criterion.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.talent.Talents;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.tags.FTZItemTags;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class WorldCategoryAdvancements {

    public static void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        obtainedTalent(consumer, "fate", Talents.DASH1);
        obtainedTalent(consumer, "mind", Talents.DASH2);
        obtainedTalent(consumer, "chaos", Talents.DASH3);
        worldSave().addCriterion("euphoria", EuphoriaTrigger.TriggerInstance.hasEuphoria()).save(consumer, Fantazia.res("euphoria"));
        anyOfItems(consumer, "fantazium", FTZItemTags.FROM_FANTAZIUM);
        anyOfItems(consumer, "obscure_tree", FTZItemTags.FROM_OBSCURE_TREE);
        anyOfItems(consumer, "runes", FTZItems.RUNE_WIELDER);
        obtainedTalent(consumer, "talents");
    }

    private static void obtainedTalent(Consumer<AdvancementHolder> consumer, String entry, ResourceLocation... talents) {
        EntryAdvancementHolder holder = worldSave();
        holder.addCriterion(entry, ObtainTalentTrigger.TriggerInstance.specific(talents)).save(consumer, Fantazia.res(entry));
    }

    private static void anyOfItems(Consumer<AdvancementHolder> consumer, String name, TagKey<Item> tagKey) {
        EntryAdvancementHolder holder = worldSave().requirements(AdvancementRequirements.Strategy.OR);

        holder.addCriterion(name, InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(tagKey)));
        holder.save(consumer, Fantazia.res(name));
    }

    private static void anyOfItems(Consumer<AdvancementHolder> consumer, String name, ItemLike... items) {
        EntryAdvancementHolder holder = worldSave().requirements(AdvancementRequirements.Strategy.OR);

        for (ItemLike itemLike : items) {
            String item = BuiltInRegistries.ITEM.getKey(itemLike.asItem()).getPath();
            holder.addCriterion(item, InventoryChangeTrigger.TriggerInstance.hasItems(itemLike.asItem()));
        }

        holder.save(consumer, Fantazia.res(name));
    }

    private static EntryAdvancementHolder worldSave() {
        return new EntryAdvancementHolder("world");
    }
}
