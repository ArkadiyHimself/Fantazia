package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump;

import dev._100media.capabilitysyncer.core.PlayerCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.simple.SimpleChannel;

public class DJump extends PlayerCapability {
    public DJump(Player player) {
        super(player);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.player.getId(), AttachDJump.DJUMP_RL, this);
    }

    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("canDJump", this.canDJump);
        tag.putBoolean("startTick", this.startTick);
        tag.putBoolean("justJumped", this.justJumped);
        tag.putInt("jumpDelay", this.jumpDelay);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.canDJump = nbt.contains("canDJump") ? nbt.getBoolean("canDJump") : true;
        this.startTick = nbt.contains("startTick") ? nbt.getBoolean("startTick") : false;
        this.justJumped = nbt.contains("justJumped") ? nbt.getBoolean("justJumped") : false;
        this.jumpDelay = nbt.contains("jumpDelay") ? nbt.getInt("jumpDelay") : 0;
    }
    public boolean canDJump = true;
    public boolean startTick = true;
    public boolean justJumped = false;
    public int jumpDelay = 0;

    public void ticking() {
        if (startTick) {
            jumpDelay = Math.max(0, jumpDelay - 1);
        }
        if (jumpDelay == 0) {
            justJumped = false;
        }
        if (!canDJump) {
            canDJump = player.onGround();
        }
        updateTracking();
    }
    public boolean canDJump() {
        return !justJumped && canDJump && !player.onGround() && !player.isFallFlying()
                && !player.hasEffect(MobEffects.LEVITATION) && !player.hasEffect(MobEffects.SLOW_FALLING);
    }
}
