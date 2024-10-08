package net.arkadiyhimself.fantazia.data.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class LootTablesHelper {
    private LootTablesHelper() {}
    public static boolean isVanillaChest(LootContext context) {
        return String.valueOf(context.getQueriedLootTableId()).startsWith("minecraft:chests/");
    }
    public static boolean isSlayed(LootContext context) {
        return context.getQueriedLootTableId().getPath().startsWith("entities");
    }

    public static boolean isVillage(ResourceLocation resLoc) {
        List<ResourceLocation> lootChestList = Lists.newArrayList();
        lootChestList.add(BuiltInLootTables.VILLAGE_WEAPONSMITH.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_TOOLSMITH.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_ARMORER.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_CARTOGRAPHER.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_SHEPHERD.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_MASON.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_BUTCHER.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_FLETCHER.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_FISHER.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_TANNERY.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_TEMPLE.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_DESERT_HOUSE.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_PLAINS_HOUSE.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_TAIGA_HOUSE.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_SNOWY_HOUSE.location());
        lootChestList.add(BuiltInLootTables.VILLAGE_SAVANNA_HOUSE.location());
        return lootChestList.contains(resLoc);
    }
}
