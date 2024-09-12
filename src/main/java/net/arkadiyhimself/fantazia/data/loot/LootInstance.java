package net.arkadiyhimself.fantazia.data.loot;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.util.library.pseudorandom.PSERANInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootInstance {
    private final @NotNull Item added;
    private final PSERANInstance instance;
    private final @Nullable Item replaced;
    private final boolean firstTime;
    private boolean looted = false;
    public LootInstance(@NotNull Item added, PSERANInstance instance, @Nullable Item replaced, boolean firstTime, boolean looted) {
        this.added = added;
        this.instance = instance;
        this.replaced = replaced;
        this.firstTime = firstTime;
        this.looted = looted;
    }
    public LootInstance(@NotNull Item added, PSERANInstance instance, @Nullable Item replaced, boolean firstTime) {
        this.added = added;
        this.instance = instance;
        this.replaced = replaced;
        this.firstTime = firstTime;
    }
    public void tryAddLoot(@NotNull ObjectArrayList<ItemStack> generatedLoot) {
        if (firstTime && looted) return;
        if (!instance.performAttempt()) return;
        generatedLoot.add(new ItemStack(added));
        if (replaced != null) generatedLoot.removeIf(stack -> stack.is(replaced));
        looted = true;
    }
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ResourceLocation addedID = ForgeRegistries.ITEMS.getKey(added);
        if (addedID == null) throw new IllegalStateException("Item's id has not been found");
        tag.putString("added", addedID.toString());

        tag.put("random", instance.serialize());

        if (replaced != null) {
            ResourceLocation replacedID = ForgeRegistries.ITEMS.getKey(replaced);
            if (replacedID == null) throw new IllegalStateException("Item's id has not been found");
            tag.putString("replaced", replacedID.toString());
        }

        tag.putBoolean("firstTime", firstTime);
        tag.putBoolean("looted", looted);
        return tag;
    }
    public static LootInstance deserialize(CompoundTag tag) {
        ResourceLocation addedID = new ResourceLocation(tag.getString("added"));
        Item added = ForgeRegistries.ITEMS.getValue(addedID);
        if (added == null) throw new IllegalStateException("Item has not been found: " + addedID);

        PSERANInstance pseranInstance = PSERANInstance.deserialize(tag.getCompound("random"));

        Item replaced = null;
        if (tag.contains("replaced")) {
            ResourceLocation replacedID = new ResourceLocation(tag.getString("replaced"));
            replaced = ForgeRegistries.ITEMS.getValue(replacedID);
        }

        boolean firstTime = tag.getBoolean("firstTime");
        boolean looted = tag.getBoolean("looted");

        return new LootInstance(added, pseranInstance, replaced, firstTime, looted);
    }
    public static class Builder {
        private final Item item;
        private final double chance;
        private final @Nullable Item replaced;
        private final boolean firstTime;
        public Builder(Item item, double chance, @Nullable Item replaced, boolean firstTime) {
            this.item = item;
            this.chance = chance;
            this.replaced = replaced;
            this.firstTime = firstTime;
        }
        public LootInstance build() {
            return new LootInstance(item, new PSERANInstance(chance), replaced, firstTime);
        }
    }
}