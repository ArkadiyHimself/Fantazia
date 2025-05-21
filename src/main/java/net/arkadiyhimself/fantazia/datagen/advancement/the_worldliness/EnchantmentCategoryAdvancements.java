package net.arkadiyhimself.fantazia.datagen.advancement.the_worldliness;

import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EnchantmentCategoryAdvancements {

    public static void generate(HolderLookup.@NotNull Provider provider, @NotNull Consumer<AdvancementHolder> consumer, @NotNull ExistingFileHelper existingFileHelper) {
        Advancement.Builder.advancement().addCriterion(BuiltInRegistries.ITEM.getKey(Items.ENCHANTING_TABLE).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(Items.ENCHANTING_TABLE)).save(consumer, Fantazia.res("the_worldliness/enchantments/generic").toString());
    }
}
