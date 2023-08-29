package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;

public class LayeredBarrier extends LivingEntityCapability {
    public LayeredBarrier(LivingEntity entity) {
        super(entity);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), LayeredBarrierEffect.LAYERED_BARRIER_EEFFECT_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("layers", this.layers);
        tag.putFloat("color", this.color);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.layers = nbt.contains("layers") ? nbt.getInt("layers") : 0;
        this.color = nbt.contains("color") ? nbt.getFloat("color") : 0;
    }
    public static List<DamageSource> unBlockable = new ArrayList<>() {{
        add(DamageSource.CRAMMING);
        add(DamageSource.DROWN);
        add(DamageSource.STARVE);
        add(DamageSource.OUT_OF_WORLD);
        add(DamageSource.IN_WALL);
    }};
    public int layers;
    public float color;
    public void tick() {
        color = Math.max(0, color - 0.2f);
        updateTracking();
    }
    public boolean hasBarrier() {
        return layers > 0;
    }
    public void addLayeredBarrier(int amount) {
        layers = amount;
        updateTracking();
    }
    public void removeLayeredBarrier() {
        layers = 0;
        updateTracking();
    }
    public void onHit(LivingHurtEvent event) {
        if (unBlockable.contains(event.getSource())) { return; }
        if (layers > 0) {
            color = 1f;
            event.setCanceled(true);
            layers--;
            if (layers == 0) {
                if (event.getEntity().hasEffect(MobEffectRegistry.LAYERED_BARRIER.get())) {
                    event.getEntity().removeEffect(MobEffectRegistry.LAYERED_BARRIER.get());
                }
            }
            updateTracking();
        }
    }
}
