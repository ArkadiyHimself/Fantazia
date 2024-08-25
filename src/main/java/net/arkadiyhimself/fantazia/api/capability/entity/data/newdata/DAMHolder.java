package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;

public class DAMHolder extends DataHolder implements ITicking {
    private final Map<UUID, DynamicAttributeModifier> MODIFIERS = Maps.newHashMap();
    public DAMHolder(LivingEntity livingEntity) {
        super(livingEntity);
    }
    @Override
    public String ID() {
        return null;
    }

    @Override
    public CompoundTag serialize(boolean toDisk) {
        return new CompoundTag();
    }

    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
    }

    @Override
    public void tick() {
        MODIFIERS.values().forEach(dynamicAttributeModifier -> dynamicAttributeModifier.tick(getEntity()));
    }
    public void addDAM(DynamicAttributeModifier DAM) {
        UUID uuid = DAM.getId();
        if (!MODIFIERS.containsKey(uuid)) MODIFIERS.put(uuid, DAM);
    }
    private void removeDAM(UUID uuid) {
        if (!MODIFIERS.containsKey(uuid)) return;
        MODIFIERS.get(uuid).tryRemove(getEntity());
        MODIFIERS.remove(uuid);
    }
    public void removeDAM(DynamicAttributeModifier DAM) {
        removeDAM(DAM.getId());
    }
}
