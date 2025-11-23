package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.data.criterion.PossessItemTrigger;
import net.arkadiyhimself.fantazia.data.criterion.PossessRuneTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomCriteriaHolder extends PlayerAbilityHolder {

    // a list of items that were in player's inventory at some point
    private final List<Item> obtainedItems = Lists.newArrayList();

    // a list of runes that were in player's inventory at some point
    private final List<Holder<Rune>> obtainedRunes = Lists.newArrayList();

    // string is an identifier of certain "action" performed by player, while integer value shows how many times the action was performed
    private final Map<ResourceLocation, AtomicInteger> timesPerformed = Maps.newHashMap();

    public CustomCriteriaHolder(@NotNull Player player) {
        super(player, Fantazia.location("custom_criteria"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag obtainedTag = new ListTag();
        for (Item item : obtainedItems) obtainedTag.add(StringTag.valueOf(BuiltInRegistries.ITEM.getKey(item).toString()));
        tag.put("obtainedItems", obtainedTag);

        ListTag runes = new ListTag();
        for (Holder<Rune> rune : obtainedRunes) runes.add(StringTag.valueOf(FantazicRegistries.RUNES.getKey(rune.value()).toString()));
        tag.put("obtainedRunes", runes);

        ListTag performedTag = new ListTag();
        for (Map.Entry<ResourceLocation, AtomicInteger> entry : timesPerformed.entrySet()) {
            CompoundTag actionTag = new CompoundTag();
            actionTag.putString("action", entry.getKey().toString());
            actionTag.putInt("amount", entry.getValue().intValue());
            performedTag.add(actionTag);
        }
        tag.put("timesPerformed", performedTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        obtainedItems.clear();
        obtainedRunes.clear();
        timesPerformed.clear();

        ListTag obtainedTag = tag.getList("obtainedItems", Tag.TAG_STRING);
        for (int i = 0; i < obtainedTag.size(); i++) obtainedItems.add(BuiltInRegistries.ITEM.get(ResourceLocation.parse(obtainedTag.getString(i))));

        ListTag runes = tag.getList("obtainedRunes", Tag.TAG_STRING);
        for (int i = 0; i < runes.size(); i++) {
            Optional<Holder.Reference<Rune>> optional = provider.holder(ResourceKey.create(FantazicRegistries.Keys.RUNE, ResourceLocation.parse(runes.getString(i))));
            optional.ifPresent(obtainedRunes::add);
        }

        ListTag performedTag = tag.getList("timesPerformed", Tag.TAG_COMPOUND);
        for (int i = 0; i < performedTag.size(); i++) {
            CompoundTag actionTag = performedTag.getCompound(i);
            timesPerformed.put(ResourceLocation.parse(actionTag.getString("action")), new AtomicInteger(actionTag.getInt("amount")));
        }
    }

    public List<Holder<Rune>> getObtainedRunes() {
        return new ArrayList<>(obtainedRunes);
    }

    public List<Item> getObtainedItems() {
        return new ArrayList<>(obtainedItems);
    }

    public void obtainedItem(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (!obtainedItems.contains(item)) obtainedItems.add(item);

        Holder<Rune> runeHolder = itemStack.get(FTZDataComponentTypes.RUNE);
        if (runeHolder != null && !obtainedRunes.contains(runeHolder) && !runeHolder.value().isEmpty()) obtainedRunes.add(runeHolder);


        if (getPlayer() instanceof ServerPlayer serverPlayer) {
            PossessItemTrigger.INSTANCE.trigger(serverPlayer,this);
            PossessRuneTrigger.INSTANCE.trigger(serverPlayer,this);
        }
    }

    public AtomicInteger getActionAmount(ResourceLocation action) {
        return timesPerformed.computeIfAbsent(action,location -> new AtomicInteger(0));
    }

    public int performAction(ResourceLocation action, int delta) {
        return getActionAmount(action).addAndGet(delta);
    }

    public void reset() {
        obtainedItems.clear();
        obtainedRunes.clear();
        timesPerformed.clear();
    }
}
