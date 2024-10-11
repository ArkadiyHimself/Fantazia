package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.criterion.PossessItemTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;
import java.util.Map;

public class CustomCriteriaHolder extends PlayerAbilityHolder {

    // a list of items that were in player's inventory at some point
    private final List<Item> obtainedItems = Lists.newArrayList();

    // a list of items with a certain tag that were in player's inventory at one points
    private final Map<TagKey<Item>, List<Item>> obtainedTaggedItems = Maps.newHashMap();

    public CustomCriteriaHolder(@NotNull Player player) {
        super(player, Fantazia.res("custom_criteria"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag items = new ListTag();
        for (Item item : obtainedItems) items.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
        tag.put("obtainedItems", items);

        ListTag itemsByTag = new ListTag();
        for (Map.Entry<TagKey<Item>, List<Item>> entry : obtainedTaggedItems.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("tag", entry.getKey().location().toString());

            ListTag itemsTag = new ListTag();
            for (Item item : entry.getValue()) itemsTag.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
            entryTag.put("items", itemsTag);
            itemsByTag.add(entryTag);
        }
        tag.put("obtainedTaggedItems", itemsByTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        obtainedItems.clear();
        obtainedTaggedItems.clear();

        ListTag items = tag.getList("obtainedItems", ListTag.TAG_STRING);
        for (int i = 0; i < items.size(); i++) obtainedItems.add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(items.getString(i))));

        ListTag itemsByTag = tag.getList("obtainedTaggedItems", ListTag.TAG_COMPOUND);
        for (int i = 0; i < itemsByTag.size(); i++) {
            CompoundTag entryTag = itemsByTag.getCompound(i);

            String key = entryTag.getString("tag");
            ResourceLocation tagLocation = ResourceLocation.parse(key);
            TagKey<Item> itemTagKey = TagKey.create(Registries.ITEM, tagLocation);

            List<Item> itemList = Lists.newArrayList();
            ListTag itemsTag = entryTag.getList("items", ListTag.TAG_STRING);
            for (int j = 0; j < itemsTag.size(); j++) itemList.add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemsTag.getString(j))));

            getOrCreateTagList(itemTagKey).addAll(itemList);
        }
    }

    @Override
    public CompoundTag syncSerialize() {
        return new CompoundTag();
    }
    @Override
    public void syncDeserialize(CompoundTag tag) {}

    public List<Item> getObtainedItems() {
        return this.obtainedItems;
    }

    public List<Item> getOrCreateTagList(TagKey<Item> itemTagKey) {
        return obtainedTaggedItems.computeIfAbsent(itemTagKey, tagKey -> Lists.newArrayList());
    }

    public void obtainedItem(Item item) {
        Holder.Reference<Item> holder = item.builtInRegistryHolder();
        if (!obtainedItems.contains(item)) obtainedItems.add(item);

        for (Pair<TagKey<Item>, HolderSet.Named<Item>> pair : BuiltInRegistries.ITEM.getTags().toList()) {
            if (!pair.getSecond().contains(holder)) continue;
            List<Item> itemList = getOrCreateTagList(pair.getFirst());
            if (!itemList.contains(item)) itemList.add(item);
        }

        if (getPlayer() instanceof ServerPlayer serverPlayer) PossessItemTrigger.INSTANCE.trigger(serverPlayer,this);
    }

    public void reset() {
        obtainedItems.clear();
        obtainedTaggedItems.clear();
    }
}
