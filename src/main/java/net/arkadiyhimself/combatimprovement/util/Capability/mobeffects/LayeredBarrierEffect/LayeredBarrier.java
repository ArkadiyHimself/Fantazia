package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.api.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.Barrier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public static List<ResourceKey<DamageType>> unBlockable = new ArrayList<>() {{
        add(DamageTypes.CRAMMING);
        add(DamageTypes.DROWN);
        add(DamageTypes.STARVE);
        add(DamageTypes.GENERIC_KILL);
        add(DamageTypes.IN_WALL);
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
        AtomicBoolean ret = new AtomicBoolean(false);
        LayeredBarrier.unBlockable.forEach(source -> {
            if (event.getSource().is(source)) ret.set(true);
        });
        if (ret.get()) return;
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
