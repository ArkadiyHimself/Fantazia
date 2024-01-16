package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Particless.ParticleRegistry;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Barrier extends LivingEntityCapability {
    public Barrier(LivingEntity entity) {
        super(entity);
        this.livingEntity = entity;
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), BarrierEffect.BARRIER_EEFFECT_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("barrierAmount", barrierAmount);
        tag.putFloat("barrierInitialAmount", barrierInitialAmount);
        tag.putFloat("barrierColor", barrierColor);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        barrierAmount = nbt.contains("barrierAmount") ? nbt.getFloat("barrierAmount") : 0;
        barrierInitialAmount = nbt.contains("barrierInitialAmount") ? nbt.getFloat("barrierInitialAmount") : 0;
        barrierColor = nbt.contains("barrierColor") ? nbt.getFloat("barrierColor") : 0;
    }
    public static List<DamageSource> unBlockable = new ArrayList<>() {{
        add(DamageSource.CRAMMING);
        add(DamageSource.DROWN);
        add(DamageSource.STARVE);
        add(DamageSource.OUT_OF_WORLD);
        add(DamageSource.IN_WALL);
    }};
    private final Random random = new Random();
    public LivingEntity livingEntity;
    public float barrierAmount;
    public float barrierInitialAmount;
    public float barrierColor;
    public void tick() {
        barrierColor = Math.max(0, barrierColor - 0.2f);
        updateTracking();
    }
    public boolean hasBarrier() {
        return barrierAmount > 0;
    }
    public void removeBarrier() {
        barrierAmount = 0;
        updateTracking();
    }
    public void addBarrier(float amount) {
        barrierInitialAmount = amount;
        barrierAmount = amount;
        updateTracking();
    }

    // if true, removes the barrier effect
    public void onHit(LivingHurtEvent event) {
        if (hasBarrier()) {
            barrierColor = 1;
            float dmg = event.getAmount();
            if (Barrier.unBlockable.contains(event.getSource())) {
                return;
            }
            float newDmg = dmg - barrierAmount;
            boolean keep = newDmg < 0;
            if (keep) {
                event.setCanceled(true);
                barrierAmount -= dmg;
                entity.level.playSound(null, entity.blockPosition(), SoundRegistry.BARRIER_HIT.get(), SoundSource.AMBIENT);
            } else {
                event.setAmount(newDmg);
                removeBarrier();
                entity.level.playSound(null, entity.blockPosition(), SoundRegistry.BARRIER_BREAK.get(), SoundSource.AMBIENT);
                if (event.getEntity().hasEffect(MobEffectRegistry.BARRIER.get())) {
                    event.getEntity().removeEffect(MobEffectRegistry.BARRIER.get());
                }
            }
            int num = switch (Minecraft.getInstance().options.particles().get()) {
                case ALL -> 20;
                case DECREASED -> 15;
                case MINIMAL -> 10;
            };
            for (int i = 1; i <= num; i++) {
                WhereMagicHappens.Abilities.createRandomParticleOnHumanoid((LivingEntity) this.entity,
                        ParticleRegistry.barrierPirecesParticles.get(random.nextInt(0, ParticleRegistry.barrierPirecesParticles.size())).get(),
                        WhereMagicHappens.Abilities.ParticleMovement.FALL);
            }
            updateTracking();
        }
    }
}
