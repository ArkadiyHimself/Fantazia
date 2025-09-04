package net.arkadiyhimself.fantazia.data.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.util.library.concept_of_consistency.ConCosInstance;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootInstance {

    private final @NotNull ItemStack added;
    private final ConCosInstance instance;
    private final @Nullable Item replaced;
    private final boolean firstTime;
    private boolean looted = false;

    public LootInstance(@NotNull ItemStack added, ConCosInstance instance, @Nullable Item replaced, boolean firstTime, boolean looted) {
        this.added = added;
        this.instance = instance;
        this.replaced = replaced;
        this.firstTime = firstTime;
        this.looted = looted;
    }

    public LootInstance(@NotNull ItemStack added, ConCosInstance instance, ResourceLocation replaced, boolean firstTime) {
        this.added = added;
        this.instance = instance;

        Item item = BuiltInRegistries.ITEM.get(replaced);
        this.replaced = item instanceof AirItem ? null : item;
        this.firstTime = firstTime;
    }

    public void tryAddLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot) {
        if (firstTime && looted) return;
        if (!instance.performAttempt()) return;
        generatedLoot.add(added.copy());
        if (replaced != null) generatedLoot.removeIf(stack -> stack.is(replaced));
        looted = true;
    }

    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        if (!added.isEmpty()) tag.put("addedInitial", added.save(provider));
        if (replaced != null) {
            ResourceLocation replacedID = BuiltInRegistries.ITEM.getKey(replaced);
            tag.putString("replaced", replacedID.toString());
        }

        tag.put("random", instance.serialize());
        tag.putBoolean("firstTime", firstTime);
        tag.putBoolean("looted", looted);
        return tag;
    }

    public static LootInstance deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {

        ItemStack added = ItemStack.parseOptional(provider, tag.getCompound("addedInitial"));

        ConCosInstance conCosInstance = ConCosInstance.deserialize(tag.getCompound("random"));
        Item replaced = null;
        if (tag.contains("replaced")) {
            ResourceLocation replacedID = ResourceLocation.parse(tag.getString("replaced"));
            replaced = BuiltInRegistries.ITEM.get(replacedID);
        }
        boolean firstTime = tag.getBoolean("firstTime");
        boolean looted = tag.getBoolean("looted");
        return new LootInstance(added, conCosInstance, replaced, firstTime, looted);
    }

    public record Builder(ItemStack item, double chance, ResourceLocation replaced, boolean firstTime, boolean disintegrate) {

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(builderInstance -> builderInstance.group(
                ItemStack.CODEC.fieldOf("addedInitial").forGetter(Builder::item),
                Codec.DOUBLE.optionalFieldOf("chance", 1.0).forGetter(Builder::chance),
                ResourceLocation.CODEC.lenientOptionalFieldOf("replaced", BuiltInRegistries.ITEM.getKey(Items.AIR)).forGetter(Builder::replaced),
                Codec.BOOL.optionalFieldOf("first_time", false).forGetter(Builder::firstTime),
                Codec.BOOL.optionalFieldOf("disintegrate", false).forGetter(Builder::disintegrate)
        ).apply(builderInstance, Builder::new));

        public static Builder of(Item item, double chance, Item replaced, boolean firstTime) {
            return new Builder(new ItemStack(item), chance, BuiltInRegistries.ITEM.getKey(replaced), firstTime, false);
        }

        public static Builder of(Item item, double chance, Item replaced) {
            return new Builder(new ItemStack(item), chance, BuiltInRegistries.ITEM.getKey(replaced), false, false);
        }

        public static Builder of(Item item, double chance, boolean firstTime) {
            return new Builder(new ItemStack(item), chance, BuiltInRegistries.ITEM.getKey(Items.AIR), firstTime, false);
        }

        public static Builder of(Item item, double chance) {
            return new Builder(new ItemStack(item), chance, BuiltInRegistries.ITEM.getKey(Items.AIR), false, false);
        }

        public static Builder of(ItemStack item, double chance, Item replaced, boolean firstTime) {
            return new Builder(item, chance, BuiltInRegistries.ITEM.getKey(replaced), firstTime, false);
        }

        public static Builder of(ItemStack item, double chance, Item replaced) {
            return new Builder(item, chance, BuiltInRegistries.ITEM.getKey(replaced), false, false);
        }

        public static Builder of(ItemStack item, double chance, boolean firstTime) {
            return new Builder(item, chance, BuiltInRegistries.ITEM.getKey(Items.AIR), firstTime, false);
        }

        public static Builder of(ItemStack item, double chance) {
            return new Builder(item, chance, BuiltInRegistries.ITEM.getKey(Items.AIR), false, false);
        }

        public LootInstance build() {
            if (!disintegrate) item.set(FTZDataComponentTypes.DISINTEGRATE, false);
            return new LootInstance(item, new ConCosInstance(chance), replaced, firstTime);
        }
    }
}