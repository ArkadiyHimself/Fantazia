package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LootTablesHelper {
    public static LootPool constructLootPool(String poolName, float minRolls, float maxRolls, @Nullable LootPoolEntryContainer.Builder<?>... entries) {
        LootPool.Builder poolBuilder = LootPool.lootPool();
        poolBuilder.name(poolName);
        poolBuilder.setRolls(UniformGenerator.between(minRolls, maxRolls));

        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            if (entry != null) {
                poolBuilder.add(entry);
            }
        }
        return poolBuilder.build();
    }
    public static LootPoolSingletonContainer.Builder<?> createOptionalLoot(Item item, int weight, float minCount, float maxCount) {
        return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(minCount, maxCount)));
    }
    public static LootPoolSingletonContainer.Builder<?> createOptionalLoot(Item item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight);
    }
    public static List<ResourceLocation> getTempleLootTable() {
        List<ResourceLocation> lootChestList = new ArrayList<>();
        lootChestList.add(BuiltInLootTables.DESERT_PYRAMID);
        lootChestList.add(BuiltInLootTables.JUNGLE_TEMPLE);

        return lootChestList;
    }
    public static List<ResourceLocation> getAncientCityLootTable() {
        List<ResourceLocation> lootChestList = new ArrayList<>();
        lootChestList.add(BuiltInLootTables.ANCIENT_CITY);
        lootChestList.add(BuiltInLootTables.ANCIENT_CITY_ICE_BOX);

        return lootChestList;
    }
    public static List<ResourceLocation> getNetherLootTable() {
        List<ResourceLocation> lootChestList = new ArrayList<>();
        lootChestList.add(BuiltInLootTables.BASTION_BRIDGE);
        lootChestList.add(BuiltInLootTables.BASTION_TREASURE);
        lootChestList.add(BuiltInLootTables.NETHER_BRIDGE);
        lootChestList.add(BuiltInLootTables.BASTION_HOGLIN_STABLE);
        lootChestList.add(BuiltInLootTables.BASTION_OTHER);

        return lootChestList;
    }
}
