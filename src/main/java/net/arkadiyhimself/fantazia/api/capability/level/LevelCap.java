package net.arkadiyhimself.fantazia.api.capability.level;

import dev._100media.capabilitysyncer.core.GlobalLevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class LevelCap extends GlobalLevelCapability {
    private final List<AuraInstance<Entity, Entity>> auraInstances = Lists.newArrayList();
    private final HealingSources healingSources;
    public LevelCap(Level level) {
        super(level);
        this.healingSources = new HealingSources(level.registryAccess());
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
            if (aura.getID() != null) {
                tag.putInt("owner" + i, auraInstances.get(i).getOwner().getId());
                tag.putString("aura" + i, aura.getID().toString());
            }
        }
        return tag;
    }
    @Override
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        auraInstances.clear();
        if (!nbt.contains("aura_instances") || !this.level.isClientSide()) return;
        int auras = nbt.getInt("aura_instances");
        for (int i = 0; i < auras; i++) {
            String auraStr = "aura" + i;
            String ownerStr = "owner" + i;
            if (nbt.contains(auraStr) && nbt.contains(ownerStr)) {
                ResourceLocation auraId = new ResourceLocation(nbt.getString(auraStr));
                int owner = nbt.getInt(ownerStr);
                Entity entity = this.level.getEntity(owner);

                List<RegistryObject<BasicAura<? extends Entity, ? extends Entity>>> registryObjects = new java.util.ArrayList<>(List.copyOf(FantazicRegistry.AURAS.getEntries()));
                for (RegistryObject<BasicAura<? extends Entity, ? extends Entity>> basicAuraRegistryObject : registryObjects) if (auraId.equals(basicAuraRegistryObject.getId())) auraInstances.add(new AuraInstance<>(entity, (BasicAura<Entity, Entity>) basicAuraRegistryObject.get(), this.level));
            }
        }
    }
    public void tick() {
        auraInstances.forEach(AuraInstance::tick);
    }
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
    public HealingSources healingSources() {
        return healingSources;
    }
}
