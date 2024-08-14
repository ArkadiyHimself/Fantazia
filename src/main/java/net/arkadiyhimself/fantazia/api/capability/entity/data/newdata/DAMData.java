package net.arkadiyhimself.fantazia.api.capability.entity.data.newdata;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataHolder;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;

public class DAMData extends DataHolder implements ITicking {
    private final Map<UUID, DynamicAttributeModifier> DAMsById = Maps.newHashMap();
    public DAMData(LivingEntity livingEntity) {
        super(livingEntity);
    }
    @Override
    public void tick() {
        DAMsById.values().forEach(dynamicAttributeModifier -> dynamicAttributeModifier.tick(getEntity()));
    }
    public void addDAM(DynamicAttributeModifier DAM) {
        UUID uuid = DAM.getId();
        if (!DAMsById.containsKey(uuid)) DAMsById.put(uuid, DAM);
    }
    private void removeDAM(UUID uuid) {
        if (!DAMsById.containsKey(uuid)) return;
        DAMsById.get(uuid).tryRemove(getEntity());
        DAMsById.remove(uuid);
    }
    public void removeDAM(DynamicAttributeModifier DAM) {
        removeDAM(DAM.getId());
    }
}
