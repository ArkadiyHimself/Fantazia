package net.arkadiyhimself.fantazia.data.loot;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.LootModifierHolder;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChaoticHierarchy;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class LootModifier {

    private final ImmutableList<ResourceLocation> lootTables;
    private final ImmutableList<LootInstance> lootInstances;

    public LootModifier(List<ResourceLocation> locations, List<LootInstance> instances) {
        this.lootTables = ImmutableList.copyOf(locations);
        this.lootInstances = ImmutableList.copyOf(instances);
    }

    public boolean isModified(ResourceLocation lootTable) {
        return lootTables.contains(lootTable);
    }

    public void tryModify(@NotNull ObjectArrayList<ItemStack> generatedLoot) {
        for (LootInstance instance : lootInstances) instance.tryAddLoot(generatedLoot);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag lootTablesTag = new ListTag();
        for (ResourceLocation location : this.lootTables) lootTablesTag.add(StringTag.valueOf(location.toString()));
        tag.put("lootTables", lootTablesTag);

        ListTag lootInstancesTag = new ListTag();
        for (LootInstance lootInstance : this.lootInstances) lootInstancesTag.add(lootInstance.serializeNBT(provider));
        tag.put("lootInstances", lootInstancesTag);

        return tag;
    }

    public static LootModifier deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        List<ResourceLocation> locations = Lists.newArrayList();
        ListTag lootTables = tag.getList("lootTables", Tag.TAG_STRING);
        for (int i = 0; i < lootTables.size(); i++) locations.add(ResourceLocation.parse(lootTables.getString(i)));

        List<LootInstance> instances = Lists.newArrayList();
        ListTag lootInstances = tag.getList("lootInstances", Tag.TAG_COMPOUND);
        for (int i = 0; i < lootInstances.size(); i++) instances.add(LootInstance.deserializeNBT(provider, lootInstances.getCompound(i)));

        return new LootModifier(locations, instances);
    }

    public static Builder builder() {
        return new Builder();
    }

    public record Builder(List<ResourceLocation> lootTables, List<LootInstance.Builder> lootInstances) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.<Builder>create(instance -> instance.group(
               ResourceLocation.CODEC.listOf().optionalFieldOf("loot_tables", Lists.newArrayList()).forGetter(Builder::lootTables),
               LootInstance.Builder.CODEC.listOf().optionalFieldOf("loot_instances", Lists.newArrayList()).forGetter(Builder::lootInstances)
        ).apply(instance, Builder::new)).validate(Builder::validate);

        private Builder() {
            this(Lists.newArrayList(), Lists.newArrayList());
        }

        public static DataResult<Builder> validate(Builder builder) {
            if (builder.lootTables.isEmpty()) return DataResult.error(() -> "Can not have empty loot tables!");
            if (builder.lootInstances.isEmpty()) return DataResult.error(() -> "Can not have empty loot instances!");
            return DataResult.success(builder);
        }

        public LootModifier build() {
            return new LootModifier(lootTables, ChaoticHierarchy.of(lootInstances).transform(LootInstance.Builder::build).getElements());
        }

        public List<ResourceLocation> lootTables() {
            return lootTables;
        }

        public List<LootInstance.Builder> lootInstances() {
            return lootInstances;
        }

        public Builder addLootTables(ResourceLocation... lootTables) {
            this.lootTables.addAll(Arrays.stream(lootTables).toList());
            return this;
        }

        public Builder addLootTables(List<ResourceLocation> lootTables) {
            this.lootTables.addAll(lootTables);
            return this;
        }

        public Builder addLootInstance(LootInstance.Builder lootInstance) {
            this.lootInstances.add(lootInstance);
            return this;
        }

        public LootModifierHolder holder(ResourceLocation location) {
            return new LootModifierHolder(location, this);
        }

        public void save(Consumer<LootModifierHolder> output, ResourceLocation id) {
            output.accept(holder(id));
        }
    }
}
