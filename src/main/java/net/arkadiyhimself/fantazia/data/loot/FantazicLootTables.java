package net.arkadiyhimself.fantazia.data.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.LootTablePSERAN;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class FantazicLootTables {
    public static void addItem(@NotNull ObjectArrayList<ItemStack> generatedLoot, Item item, int max) {
        addItem(generatedLoot, item, max, 1);
    }
    public static void addItem(@NotNull ObjectArrayList<ItemStack> generatedLoot, Item item, int max, int min) {
        float rnd = Fantazia.RANDOM.nextFloat();
        int amo = Math.round(Mth.lerp(rnd, min, max));
        if (amo > 0) generatedLoot.add(new ItemStack(item, amo));
    }
    public static void addItem(@NotNull ObjectArrayList<ItemStack> generatedLoot, Item item) {
        addItem(generatedLoot, item, 1.0f);
    }
    public static void addItem(@NotNull ObjectArrayList<ItemStack> generatedLoot, Item item, double chance) {
        if (Fantazia.RANDOM.nextFloat() < chance) generatedLoot.add(new ItemStack(item));
    }
    public static void ancientCityPool(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootTablePSERAN LootTablePSERAN) {
        LootTablePSERAN.attemptLoot(FTZItems.SCULK_HEART, generatedLoot);
        LootTablePSERAN.attemptLoot(FTZItems.MYSTIC_MIRROR, generatedLoot);
    }
    public static void netherPool(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootTablePSERAN LootTablePSERAN) {
        LootTablePSERAN.attemptLoot(FTZItems.BLOODLUST_AMULET, generatedLoot);
        LootTablePSERAN.attemptLoot(FTZItems.SOUL_EATER, generatedLoot);
    }
    public static void ruinedPortalLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootTablePSERAN LootTablePSERAN) {
        LootTablePSERAN.attemptLoot(FTZItems.GOLDEN_HATCHET, generatedLoot);
    }
    public static void pillagerOutpostLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootTablePSERAN LootTablePSERAN) {
        LootTablePSERAN.attemptLoot(FTZItems.LEADERS_HORN, generatedLoot, Items.GOAT_HORN);
    }
    public static void mineshaftLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot, @NotNull LootTablePSERAN LootTablePSERAN) {
        LootTablePSERAN.attemptLoot(FTZItems.TRANQUIL_HERB, generatedLoot);
    }
 }
