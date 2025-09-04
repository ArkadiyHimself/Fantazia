package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.loot.LootModifier;
import net.arkadiyhimself.fantazia.data.loot.ServerLootModifierManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class LootTableModifiersHolder extends PlayerAbilityHolder {

    private final Map<ResourceLocation, LootModifier> lootModifiers = ServerLootModifierManager.createModifiers();

    public LootTableModifiersHolder(Player player) {
        super(player, Fantazia.location("loot_table_modifiers"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag lootModifiersTag = new ListTag();
        for (Map.Entry<ResourceLocation, LootModifier> entry : lootModifiers.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", entry.getKey().toString());
            entryTag.put("modifier", entry.getValue().serializeNBT(provider));
            lootModifiersTag.add(entryTag);
        }
        tag.put("lootModifiers", lootModifiersTag);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.lootModifiers.clear();

        ListTag lootModifiersTag = tag.getList("lootModifiers", Tag.TAG_COMPOUND);

        for (int i = 0; i <= lootModifiersTag.size(); i++) {
            CompoundTag entryTag = lootModifiersTag.getCompound(i);
            ResourceLocation id = ResourceLocation.parse(entryTag.getString("id"));
            LootModifier lootModifier = LootModifier.deserializeNBT(provider, entryTag.getCompound("modifier"));
            this.lootModifiers.put(id, lootModifier);
        }

        Map<ResourceLocation, LootModifier> original = ServerLootModifierManager.createModifiers();

        // adds new loot modifiers if they have been added to data pack
        for (Map.Entry<ResourceLocation, LootModifier> entry : original.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (!this.lootModifiers.containsKey(id))
                this.lootModifiers.put(id, entry.getValue());
        }

        // removes already existing modifiers if they have been removed from data pack
        for (ResourceLocation id : lootModifiers.keySet().stream().toList()) {
            if (!original.containsKey(id)) lootModifiers.remove(id);
        }
    }

    public void attemptLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot, ResourceLocation location) {
        for (LootModifier holder : lootModifiers.values()) if (holder.isModified(location)) holder.tryModify(generatedLoot);
    }

    public void reset() {
        lootModifiers.clear();
        lootModifiers.putAll(ServerLootModifierManager.createModifiers());
    }
}
