package net.arkadiyhimself.fantazia.data.loot;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LootModifierHolder {
    private final ImmutableList<ResourceLocation> lootTables;
    private final ImmutableList<LootInstance> lootInstances;
    public LootModifierHolder(List<ResourceLocation> locations, List<LootInstance> instances) {
        this.lootTables = ImmutableList.copyOf(locations);
        this.lootInstances = ImmutableList.copyOf(instances);
    }
    public boolean isModified(ResourceLocation lootTable) {
        return lootTables.contains(lootTable);
    }
    public void tryModify(@NotNull ObjectArrayList<ItemStack> generatedLoot) {
        for (LootInstance instance : lootInstances) instance.tryAddLoot(generatedLoot);
    }
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ListTag lootTablesTag = new ListTag();
        for (ResourceLocation location : this.lootTables) lootTablesTag.add(StringTag.valueOf(location.toString()));
        tag.put("lootTables", lootTablesTag);

        ListTag lootInstancesTag = new ListTag();
        for (LootInstance lootInstance : this.lootInstances) lootInstancesTag.add(lootInstance.serialize());
        tag.put("lootInstances", lootInstancesTag);

        return tag;
    }
    public static LootModifierHolder deserialize(CompoundTag tag) {
        List<ResourceLocation> locations = Lists.newArrayList();
        ListTag lootTables = tag.getList("lootTables", Tag.TAG_STRING);
        for (int i = 0; i < lootTables.size(); i++) locations.add(new ResourceLocation(lootTables.getString(i)));

        List<LootInstance> instances = Lists.newArrayList();
        ListTag lootInstances = tag.getList("lootInstances", Tag.TAG_COMPOUND);
        for (int i = 0; i < lootTables.size(); i++) instances.add(LootInstance.deserialize(lootInstances.getCompound(i)));

        return new LootModifierHolder(locations, instances);
    }
    public static class Builder {
        private final List<ResourceLocation> lootTables = Lists.newArrayList();
        private final ChaoticHierarchy<LootInstance.Builder> lootInstances = new ChaoticHierarchy<>();
        public void addLootTable(ResourceLocation location) {
            lootTables.add(location);
        }
        public void addLootInstance(Item item, double chance, @Nullable Item replaced, boolean firstTime) {
            lootInstances.addElement(new LootInstance.Builder(item, chance, replaced, firstTime));
        }
        public LootModifierHolder build() {
            return new LootModifierHolder(lootTables, lootInstances.transform(LootInstance.Builder::build).getElements());
        }
    }
}
