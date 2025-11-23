package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.api.prompt.Prompts;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;

public class ToolUtilisationHolder extends PlayerAbilityHolder implements ISyncEveryTick {

    private final Map<Item, Integer> capacities = Maps.newHashMap();
    private final List<Item> accounted = Lists.newArrayList();
    private int capacityUpgrades = 0;
    private int damageUpgrades = 0;
    private boolean rechargePrompt = false;

    public ToolUtilisationHolder(@NotNull Player player) {
        super(player, Fantazia.location("tool_utilization"));
        accountAllItems();
    }

    public void upgradeCapacity() {
        this.capacityUpgrades++;
    }

    public void downgradeCapacity() {
        this.capacityUpgrades--;
    }

    public int getCapacityUpgrades() {
        return capacityUpgrades;
    }

    public void upgradeDamage() {
        this.damageUpgrades++;
    }

    public void downgradeDamage() {
        this.damageUpgrades--;
    }

    public int getDamageUpgrades() {
        return damageUpgrades;
    }

    public int maxCapacity(Item item) {
        RechargeableToolData data = RechargeableToolData.getToolData(item);
        if (data == null) return -1;
        return data.getMaxAmount(capacityUpgrades);
    }

    public int remainingAmount(Item item) {
        if (!capacities.containsKey(item)) capacities.put(item, 0);
        return capacities.get(item);
    }

    public boolean canUse(Item item) {
        return remainingAmount(item) > 0;
    }

    public void consume(Item item, int amount) {
        int remaining = remainingAmount(item);
        int newAmount = Math.max(0, remaining - amount);
        capacities.replace(item, newAmount);
        if (newAmount < 2) rechargePrompt = true;
        if (rechargePrompt && getPlayer() instanceof ServerPlayer serverPlayer)
            Prompts.RECHARGE_TOOL.maybePromptPlayer(serverPlayer);
    }

    public void consume(Item item) {
        consume(item,1);
    }

    public void accountItem(Item item) {
        RechargeableToolData data = RechargeableToolData.getToolData(item);
        if (accounted.contains(item) || data == null) return;
        int maxAmount = data.getInitialAmount();
        if (maxAmount == -1) return;
        accounted.add(item);
        capacities.remove(item);
        capacities.put(item, maxAmount);
    }

    public void accountAllItems() {
        for (Item item : BuiltInRegistries.ITEM.stream().toList()) accountItem(item);
    }

    public Map<Item, Integer> getCapacities() {
        return Map.copyOf(capacities);
    }

    public void reset() {
        capacities.clear();
        accounted.clear();
        accountAllItems();
    }

    public boolean tryRecharge(Item item) {
        if (!capacities.containsKey(item)) capacities.put(item, 0);
        int value = capacities.get(item);

        RechargeableToolData data = RechargeableToolData.getToolData(item);
        if (data == null || data.getMaxAmount(capacityUpgrades) <= value) return false;

        if (getPlayer().hasInfiniteMaterials()) {
            if (getPlayer() instanceof ServerPlayer serverPlayer) Prompts.RECHARGE_TOOL.noLongerNeeded(serverPlayer);
            capacities.replace(item, ++value);
            return true;
        }

        List<RechargeableToolData.SimpleIngredient> ingredients = data.ingredients();
        Inventory inventory = getPlayer().getInventory();

        // checking if enough ingredients
        for (RechargeableToolData.SimpleIngredient ingredient : ingredients) {
            Item ing = ingredient.item();
            int required = ingredient.amount();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.is(ing)) continue;

                int count = stack.getCount();
                if (count >= required) {
                    required = 0;
                    break;
                }
                else {
                    required -= count;
                }
            }

            // if there is not enough amount of certain item, it returns false
            if (required > 0) return false;
        }

        if (getPlayer() instanceof ServerPlayer serverPlayer) Prompts.RECHARGE_TOOL.noLongerNeeded(serverPlayer);
        // if the code got here, that means that inventory has all required ingredients
        for (RechargeableToolData.SimpleIngredient ingredient : ingredients) {
            Item ing = ingredient.item();
            int required = ingredient.amount();

            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.is(ing)) continue;

                int count = stack.getCount();
                if (count >= required) {
                    stack.shrink(required);
                    break;
                } else {
                    required -= count;
                    stack.shrink(count);
                }
            }
        }
        capacities.replace(item, ++value);
        return true;
    }

    public int getCapacity(Item item) {
        return capacities.getOrDefault(item, -1);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("recharge_prompt", rechargePrompt);
        tag.putInt("capacity_upgrades", capacityUpgrades);
        tag.putInt("damage_upgrades", damageUpgrades);

        ListTag capacitiesTag = new ListTag();
        for (Map.Entry<Item, Integer> entry : capacities.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            String id = BuiltInRegistries.ITEM.getKey(entry.getKey()).toString();
            entryTag.putString("item", id);
            entryTag.putInt("remaining", entry.getValue());

            capacitiesTag.add(entryTag);
        }
        tag.put("capacities", capacitiesTag);

        ListTag obtainedTag = new ListTag();
        for (Item item : accounted) {
            StringTag entryTag = StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString());
            obtainedTag.add(entryTag);
        }
        tag.put("accounted", obtainedTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.rechargePrompt = tag.getBoolean("recharge_prompt");
        this.capacityUpgrades = Math.max(0, tag.getInt("capacity_upgrades"));
        this.damageUpgrades = tag.getInt("damage_upgrades");

        this.capacities.clear();
        ListTag capacitiesTag = tag.getList("capacities", Tag.TAG_COMPOUND);
        for (int i = 0; i < capacitiesTag.size(); i++) {
            CompoundTag entryTag = capacitiesTag.getCompound(i);

            ResourceLocation id = ResourceLocation.parse(entryTag.getString("item"));
            Item item = BuiltInRegistries.ITEM.get(id);
            int remaining = entryTag.getInt("remaining");

            capacities.put(item, remaining);
        }

        this.accounted.clear();
        ListTag obtainedTag = tag.getList("accounted", Tag.TAG_STRING);
        for (int i = 0; i < obtainedTag.size(); i++) {
            String str = obtainedTag.getString(i);
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(str));

            accounted.add(item);
        }
        accountAllItems();
    }

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("capacity_upgrades", capacityUpgrades);
        tag.putInt("damage_upgrades", damageUpgrades);

        ListTag capacitiesTag = new ListTag();
        for (Map.Entry<Item, Integer> entry : capacities.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            String id = BuiltInRegistries.ITEM.getKey(entry.getKey()).toString();
            entryTag.putString("item", id);
            entryTag.putInt("remaining", entry.getValue());

            capacitiesTag.add(entryTag);
        }
        tag.put("capacities", capacitiesTag);

        ListTag obtainedTag = new ListTag();
        for (Item item : accounted) {
            StringTag entryTag = StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString());
            obtainedTag.add(entryTag);
        }
        tag.put("accounted", obtainedTag);
        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        this.capacityUpgrades = tag.getInt("capacity_upgrades");
        this.damageUpgrades = tag.getInt("damage_upgrades");

        this.capacities.clear();
        ListTag capacitiesTag = tag.getList("capacities", Tag.TAG_COMPOUND);
        for (int i = 0; i < capacitiesTag.size(); i++) {
            CompoundTag entryTag = capacitiesTag.getCompound(i);

            ResourceLocation id = ResourceLocation.parse(entryTag.getString("item"));
            Item item = BuiltInRegistries.ITEM.get(id);
            int remaining = entryTag.getInt("remaining");

            capacities.put(item, remaining);
        }

        this.accounted.clear();
        ListTag obtainedTag = tag.getList("accounted", Tag.TAG_STRING);
        for (int i = 0; i < obtainedTag.size(); i++) {
            String str = obtainedTag.getString(i);
            Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(str));

            accounted.add(item);
        }
    }
}
