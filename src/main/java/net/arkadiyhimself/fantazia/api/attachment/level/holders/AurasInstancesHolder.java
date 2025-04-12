package net.arkadiyhimself.fantazia.api.attachment.level.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class AurasInstancesHolder extends LevelAttributeHolder {
    private final List<AuraInstance<? extends Entity>> auraInstances = Lists.newArrayList();

    public AurasInstancesHolder(Level level) {
        super(level, Fantazia.res("aura_instances"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        if (auraInstances.isEmpty()) return tag;

        ListTag instances = new ListTag();
        for (AuraInstance<? extends Entity> auraInstance : auraInstances) instances.add(auraInstance.serialize());

        tag.put("auras", instances);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        auraInstances.clear();

        if (!compoundTag.contains("auras")) return;
        ListTag tags = compoundTag.getList("auras", Tag.TAG_COMPOUND);

        for (int i = 0; i < tags.size(); i++) {
            AuraInstance<?> auraInstance = AuraInstance.deserialize(tags.getCompound(i), getLevel());
            if (auraInstance != null) auraInstances.add(auraInstance);
        }
    }

    @Override
    public CompoundTag syncSerialize() {
        CompoundTag tag = new CompoundTag();
        if (auraInstances.isEmpty()) return tag;

        ListTag instances = new ListTag();
        for (AuraInstance<? extends Entity> auraInstance : auraInstances) instances.add(auraInstance.serialize());
        tag.put("auras", instances);

        return tag;
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        auraInstances.clear();

        if (!tag.contains("auras")) return;
        ListTag tags = tag.getList("auras", Tag.TAG_COMPOUND);

        for (int i = 0; i < tags.size(); i++) {
            AuraInstance<?> auraInstance = AuraInstance.deserialize(tags.getCompound(i), getLevel());
            if (auraInstance != null) auraInstances.add(auraInstance);
        }
    }

    @Override
    public void tick() {
        auraInstances.removeIf(AuraInstance::removed);
        auraInstances.forEach(AuraInstance::tick);
    }

    public List<AuraInstance<? extends Entity>> getAuraInstances() {
        return auraInstances;
    }

    public void addAuraInstance(AuraInstance<? extends Entity> instance) {
        if (!auraInstances.contains(instance)) auraInstances.add(instance);

        LevelAttributes.updateTracking(getLevel());
    }
}
