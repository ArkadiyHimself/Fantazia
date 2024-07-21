package net.arkadiyhimself.fantazia.advanced.capability.entity.AuraCarrier;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.network.simple.SimpleChannel;

public class AuraCarrier extends LivingEntityCapability {
    public AuraCarrier(ArmorStand player) {
        super(player);
    }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), GetAuraCarrier.AURA_CARRIER_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
    }
    private AuraInstance<Entity, Entity> auraInstance;
    public AuraInstance<Entity, Entity> getAuraInstance() {
        return auraInstance;
    }
    public void setAuraInstance(BasicAura<Entity, Entity> basicAura) {
        this.auraInstance = new AuraInstance<>(entity, basicAura, entity.level());
    }
    public void setAuraInstance(ResourceLocation aura) {
        if (BasicAura.AURAS.containsKey(aura)) this.auraInstance = new AuraInstance<>(entity, BasicAura.AURAS.get(aura), entity.level());
    }
    public void onDeath() {
        if (auraInstance != null) auraInstance.discard();
    }
}
