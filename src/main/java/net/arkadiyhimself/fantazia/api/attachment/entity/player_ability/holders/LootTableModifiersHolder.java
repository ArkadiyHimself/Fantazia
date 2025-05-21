package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.data.loot.LootModifier;
import net.arkadiyhimself.fantazia.data.loot.ServerLootModifierManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class LootTableModifiersHolder extends PlayerAbilityHolder {

    private final List<LootModifier> lootModifiers = ServerLootModifierManager.createModifiers();

    public LootTableModifiersHolder(Player player) {
        super(player, Fantazia.res("loot_table_modifiers"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag lootModifiers = new ListTag();
        for (LootModifier holder : this.lootModifiers) lootModifiers.add(holder.serializeNBT(provider));
        tag.put("lootModifiers", lootModifiers);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        ListTag lootModifiers = compoundTag.getList("lootModifiers", Tag.TAG_COMPOUND);
        List<LootModifier> modifierHolders = Lists.newArrayList();
        for (int i = 0; i < lootModifiers.size(); i++)
            modifierHolders.add(LootModifier.deserializeNBT(provider, lootModifiers.getCompound(i)));
        if (lootModifiers.isEmpty()) return;

        this.lootModifiers.clear();
        this.lootModifiers.addAll(modifierHolders);
    }

    public void attemptLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot, ResourceLocation location) {
        for (LootModifier holder : lootModifiers) if (holder.isModified(location)) holder.tryModify(generatedLoot);
    }

    public void reset() {
        lootModifiers.clear();
        lootModifiers.addAll(ServerLootModifierManager.createModifiers());
    }
}
