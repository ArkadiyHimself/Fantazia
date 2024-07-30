package net.arkadiyhimself.fantazia.api.capability.level;

import dev._100media.capabilitysyncer.core.GlobalLevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class LevelCap extends GlobalLevelCapability {
    public LevelCap(Level level) {
        super(level);
    }
    @Override
    public LevelCapabilityStatusPacket createUpdatePacket() {
        return new SimpleLevelCapabilityStatusPacket(LevelCapGetter.LEVEL_CAP_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        if (auraInstances.isEmpty()) return tag;
        tag.putInt("aura_instances", auraInstances.size());
        for (int i = 0; i < auraInstances.size(); i++) {
            BasicAura<?,?> aura = auraInstances.get(i).getAura();
            if (aura.getMapKey() != null) {
                tag.putInt("owner" + i, auraInstances.get(i).getOwner().getId());
                tag.putString("aura" + i, aura.getMapKey().toString());
            }
        }
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        auraInstances.clear();
        if (!nbt.contains("aura_instances") || !this.level.isClientSide) return;
        int auras = nbt.getInt("aura_instances");
        for (int i = 0; i < auras; i++) {
            String aura = "aura" + i;
            String owner = "owner" + i;
            if (nbt.contains(aura) && nbt.contains(owner)) {
                ResourceLocation aura_ = new ResourceLocation(nbt.getString(aura));
                int owner_ = nbt.getInt(owner);
                Entity entity = this.level.getEntity(owner_);
                if (BasicAura.AURAS.containsKey(aura_) && entity != null) auraInstances.add(new AuraInstance<>(entity, (BasicAura<Entity, Entity>) BasicAura.AURAS.get(aura_), this.level));
            }
        }
    }
    public void tick() {
        auraInstances.forEach(AuraInstance::tick);
    }
    private final List<AuraInstance<Entity, Entity>> auraInstances = Lists.newArrayList();
    public List<AuraInstance<Entity, Entity>> getAuraInstances() {
        return auraInstances;
    }
    public void addAuraInstance(AuraInstance<Entity, Entity> instance) {
        if (!auraInstances.contains(instance)) auraInstances.add(instance);
        updateTracking();
    }
    public void removeAuraInstance(AuraInstance<?,?> instance) {
        auraInstances.remove(instance);
        updateTracking();
    }
}
