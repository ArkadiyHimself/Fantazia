package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.util.library.pseudorandom.PSERANInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class LootTablePSERAN extends AbilityHolder {
    private final HashMap<Item, PSERANInstance> LOOT_TABLES = Maps.newHashMap();
    public LootTablePSERAN(Player player) {
        super(player);
        LootProvider.provide(this);
    }

    public void attemptLoot(Item item, @NotNull ObjectArrayList<ItemStack> generatedLoot) {
        if (!LOOT_TABLES.containsKey(item)) return;
        if (Fantazia.DEVELOPER_MODE) getPlayer().sendSystemMessage(Component.translatable(LOOT_TABLES.get(item).getSupposedChance() + " " + LOOT_TABLES.get(item).getActualChance()));
        if (LOOT_TABLES.get(item).performAttempt()) generatedLoot.add(new ItemStack(item));
    }
    public void attemptLoot(Item item, @NotNull ObjectArrayList<ItemStack> generatedLoot, Item replaced) {
        if (!LOOT_TABLES.containsKey(item)) return;
        if (Fantazia.DEVELOPER_MODE) getPlayer().sendSystemMessage(Component.translatable(LOOT_TABLES.get(item).getSupposedChance() + " " + LOOT_TABLES.get(item).getActualChance()));
        if (!LOOT_TABLES.get(item).performAttempt()) return;
        generatedLoot.removeIf(stack -> stack.is(replaced));
        generatedLoot.add(new ItemStack(item));
    }
    public void addLootInstance(Item item, float chance) {
        if (LOOT_TABLES.containsKey(item)) return;
        LOOT_TABLES.put(item, new PSERANInstance(chance));
    }
    private static class LootProvider {
        private static void provide(LootTablePSERAN capability) {
            capability.addLootInstance(FTZItems.SCULK_HEART, 0.15f);
            capability.addLootInstance(FTZItems.MYSTIC_MIRROR, 0.08f);
            capability.addLootInstance(FTZItems.GOLDEN_HATCHET, 0.35f);
            capability.addLootInstance(FTZItems.LEADERS_HORN, 0.15f);
            capability.addLootInstance(FTZItems.TRANQUIL_HERB, 0.2f);
            capability.addLootInstance(FTZItems.BLOODLUST_AMULET, 0.085f);
            capability.addLootInstance(FTZItems.SOUL_EATER, 0.06f);
        }
    }
}
