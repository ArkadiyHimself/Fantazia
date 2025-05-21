package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class LootTablesUtil {

    public static List<ResourceLocation> nether() {
        List<ResourceLocation> locations = Lists.newArrayList();
        locations.add(BuiltInLootTables.BASTION_TREASURE.location());
        locations.add(BuiltInLootTables.BASTION_OTHER.location());
        locations.add(BuiltInLootTables.BASTION_BRIDGE.location());
        locations.add(BuiltInLootTables.BASTION_HOGLIN_STABLE.location());
        locations.add(BuiltInLootTables.NETHER_BRIDGE.location());
        locations.add(BuiltInLootTables.RUINED_PORTAL.location());
        return locations;
    }

    public static List<ResourceLocation> village() {
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
        return lootChestList;
    }

    public static List<ResourceLocation> ocean() {
        List<ResourceLocation> lootChestList = Lists.newArrayList();
        lootChestList.add(BuiltInLootTables.UNDERWATER_RUIN_SMALL.location());
        lootChestList.add(BuiltInLootTables.UNDERWATER_RUIN_BIG.location());
        lootChestList.add(BuiltInLootTables.SHIPWRECK_SUPPLY.location());
        lootChestList.add(BuiltInLootTables.SHIPWRECK_TREASURE.location());
        lootChestList.add(BuiltInLootTables.BURIED_TREASURE.location());
        return lootChestList;
    }

    public static boolean isVanillaChest(LootContext context) {
        return context.getQueriedLootTableId().toString().startsWith("minecraft:chests/");
    }
    public static boolean isEntityLoot(LootContext context) {
        return context.getQueriedLootTableId().getPath().startsWith("entities");
    }
}
