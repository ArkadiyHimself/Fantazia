package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

public class DataSync extends PlayerCapability {
    public DataSync(Player player) { super(player); }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.livingEntity.getId(), AttachDataSync.DATA_SYNC_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("veinTR", this.veinTR);
        tag.putFloat("allTR", this.allTR);

        tag.putInt("heartbeat", this.heartbeat);

        tag.putString("animation", this.animation);

        tag.putDouble("dX", this.dX);
        tag.putDouble("dY", this.dY);
        tag.putDouble("dZ", this.dZ);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.veinTR = nbt.contains("veinTR") ? nbt.getFloat("veinTR") : 0;
        this.allTR = nbt.contains("allTR") ? nbt.getFloat("allTR") : 1;

        this.heartbeat = nbt.contains("heartbeat") ? nbt.getInt("heartbeat") : 0;

        this.animation = nbt.contains("animation") ? nbt.getString("animation") : "";

        this.dX = nbt.contains("dX") ? nbt.getDouble("dX") : 0;
        this.dY = nbt.contains("dY") ? nbt.getDouble("dY") : 0;
        this.dZ = nbt.contains("dZ") ? nbt.getDouble("dZ") : 0;
    }

    // fury heartbeat syncing
    private float veinTR = 0;
    private float allTR = 0;
    public int heartbeat = 0;
    public String animation = "";
    public void setAnimation(@Nullable String name) {
        this.animation = name;
        updateTracking();
    }
    public float getAllTR() { return allTR; }
    public void setAllTR(float allTR) { this.allTR = allTR; }
    public float getVeinTR() { return veinTR; }
    public void setVeinTR(float veinTR) { this.veinTR = veinTR; }

    // delta movement syncing
    public Vec3 getDeltaMovement() {
        return new Vec3(dX, dY, dZ);
    }
    public void setDeltaMovement(Vec3 movement) {
        dX = movement.x();
        dY = movement.y();
        dZ = movement.z();
        updateTracking();
    }
    public double dX;
    public double dY;
    public double dZ;
}
