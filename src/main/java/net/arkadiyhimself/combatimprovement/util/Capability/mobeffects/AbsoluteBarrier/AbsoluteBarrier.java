package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.AbsoluteBarrier;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.simple.SimpleChannel;

public class AbsoluteBarrier extends LivingEntityCapability {
    public AbsoluteBarrier(LivingEntity entity) {
        super(entity);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), AbsoluteBarrierEffect.ABSOLUTE_BARRIER_EEFFECT_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("hasBarrier", this.hasBarrier);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.hasBarrier = nbt.contains("hasBarrier") && nbt.getBoolean("hasBarrier");

    }
    public boolean hasBarrier;

    public void setBarrier(boolean value) {
        hasBarrier = value;
        updateTracking();
    }
}
