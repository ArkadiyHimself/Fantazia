package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.data.loot.LootInstanceManager;
import net.arkadiyhimself.fantazia.data.loot.LootModifierHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LootTableModifiersHolder extends AbilityHolder {
    private final List<LootModifierHolder> lootModifierHolders = LootInstanceManager.createModifiers();
    public LootTableModifiersHolder(Player player) {
        super(player);
    }
    @Override
    public String id() {
        return "loot_table";
    }
    @Override
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        if (!toDisk) return tag;
        ListTag lootModifiers = new ListTag();
        for (LootModifierHolder holder : lootModifierHolders) lootModifiers.add(holder.serialize());
        tag.put("lootModifiers", lootModifiers);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        if (!fromDisk) return;
        ListTag lootModifiers = tag.getList("lootModifiers", Tag.TAG_COMPOUND);
        List<LootModifierHolder> modifierHolders = Lists.newArrayList();
        for (int i = 0; i < lootModifiers.size(); i++) modifierHolders.add(LootModifierHolder.deserialize(lootModifiers.getCompound(i)));
        if (lootModifiers.isEmpty()) return;

        lootModifierHolders.clear();
        lootModifierHolders.addAll(modifierHolders);
    }
    public void attemptLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot, ResourceLocation location) {
        for (LootModifierHolder holder : lootModifierHolders) if (holder.isModified(location)) holder.tryModify(generatedLoot);
    }
    public void reset() {
        lootModifierHolders.clear();
        lootModifierHolders.addAll(LootInstanceManager.createModifiers());
    }
}
