package net.arkadiyhimself.fantazia.data.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.compress.utils.Lists;

import java.util.ArrayList;
import java.util.List;

public class LootTablesHelper {
    public static boolean isVanillaChest(LootContext context) {
        return String.valueOf(context.getQueriedLootTableId()).startsWith("minecraft:chests/");
    }
    public static boolean isTemple(ResourceLocation resLoc) {
        List<ResourceLocation> lootChestList = new ArrayList<>();
        lootChestList.add(BuiltInLootTables.DESERT_PYRAMID);
        lootChestList.add(BuiltInLootTables.JUNGLE_TEMPLE);
        return lootChestList.contains(resLoc);
    }
    public static boolean isNether(ResourceLocation resLoc) {
        List<ResourceLocation> lootChestList = Lists.newArrayList();
        lootChestList.add(BuiltInLootTables.BASTION_BRIDGE);
        lootChestList.add(BuiltInLootTables.BASTION_TREASURE);
        lootChestList.add(BuiltInLootTables.NETHER_BRIDGE);
        lootChestList.add(BuiltInLootTables.BASTION_HOGLIN_STABLE);
        lootChestList.add(BuiltInLootTables.BASTION_OTHER);
        lootChestList.add(BuiltInLootTables.RUINED_PORTAL);
        return lootChestList.contains(resLoc);
    }
    public static boolean isStronghold(ResourceLocation resLoc) {
        List<ResourceLocation> lootChestList = Lists.newArrayList();
        lootChestList.add(BuiltInLootTables.STRONGHOLD_CORRIDOR);
        lootChestList.add(BuiltInLootTables.STRONGHOLD_CROSSING);
        lootChestList.add(BuiltInLootTables.STRONGHOLD_LIBRARY);
        return lootChestList.contains(resLoc);
    }
    public static boolean isVillage(ResourceLocation resLoc) {
        List<ResourceLocation> lootChestList = Lists.newArrayList();
        lootChestList.add(BuiltInLootTables.VILLAGE_WEAPONSMITH);
        lootChestList.add(BuiltInLootTables.VILLAGE_TOOLSMITH);
        lootChestList.add(BuiltInLootTables.VILLAGE_ARMORER);
        lootChestList.add(BuiltInLootTables.VILLAGE_CARTOGRAPHER);
        lootChestList.add(BuiltInLootTables.VILLAGE_SHEPHERD);
        lootChestList.add(BuiltInLootTables.VILLAGE_MASON);
        lootChestList.add(BuiltInLootTables.VILLAGE_BUTCHER);
        lootChestList.add(BuiltInLootTables.VILLAGE_FLETCHER);
        lootChestList.add(BuiltInLootTables.VILLAGE_FISHER);
        lootChestList.add(BuiltInLootTables.VILLAGE_TANNERY);
        lootChestList.add(BuiltInLootTables.VILLAGE_TEMPLE);
        lootChestList.add(BuiltInLootTables.VILLAGE_DESERT_HOUSE);
        lootChestList.add(BuiltInLootTables.VILLAGE_PLAINS_HOUSE);
        lootChestList.add(BuiltInLootTables.VILLAGE_TAIGA_HOUSE);
        lootChestList.add(BuiltInLootTables.VILLAGE_SNOWY_HOUSE);
        lootChestList.add(BuiltInLootTables.VILLAGE_SAVANNA_HOUSE);
        return lootChestList.contains(resLoc);
    }
}
