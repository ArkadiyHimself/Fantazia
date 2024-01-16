package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect;

import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Registries.AttributeRegistry;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.network.simple.SimpleChannel;

public class Stun extends LivingEntityCapability {
    public Stun(LivingEntity entity) {
        super(entity);
    }

    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.entity.getId(), StunEffect.STUN_EEFFECT_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() { return NetworkHandler.INSTANCE; }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stunPoints", this.stunPoints);
        tag.putInt("maxDuration", this.maxDuration);
        tag.putInt("duration", this.duration);
        tag.putInt("redColor", this.redColor);
        tag.putBoolean("colorUp", this.colorUp);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.stunPoints = nbt.contains("stunPoints") ? nbt.getInt("stunPoints") : 0;
        this.maxDuration = nbt.contains("maxDuration") ? nbt.getInt("maxDuration") : 0;
        this.duration = nbt.contains("duration") ? nbt.getInt("duration") : 0;
        this.redColor = nbt.contains("redColor") ? nbt.getInt("redColor") : 0;
        this.colorUp = nbt.contains("colorUp") && nbt.getBoolean("colorUp");
    }

    public final int MAX_STUN_POINTS = 200;
    public final int MIN_POINTS_FROM_HITS = 5;
    public final int STUN_DURATION_FROM_HITS = 80;
    public int redColor;
    public boolean colorUp;
    public int stunPoints;
    public int maxDuration;
    public int duration;
    private int decayDelay;
    public void tick() {
        decayDelay = Math.max(0, decayDelay - 1);
        if (decayDelay == 0) {
            stunPoints = Math.max(0, stunPoints - 1);
        }
        changeColor();
        updateTracking();
    }
    public void onRespawn() {
        this.stunPoints = 0;
        this.duration = 0;
        this.decayDelay = 0;
        updateTracking();
    }
    public void changeColor() {
        if (colorUp) {
            redColor += 15;
        } else { redColor -= 15; }
        if (redColor <= 160) { colorUp = true; }
        if (redColor >= 255) { colorUp = false; }
    }
    public int getMaxPoints() {
        return (int) this.livingEntity.getAttributeValue(AttributeRegistry.MAX_STUN_POINTS.get());
    }
    public void addStunPoints(int amount) {
        stunPoints = Math.min(getMaxPoints(), stunPoints + amount);
        updateTracking();
    }
    private void startStun() {
        decayDelay = 0;
        stunPoints = 0;
        entity.level.playSound(null, entity.blockPosition(), SoundRegistry.ATTACK_STUNNED.get(), SoundSource.AMBIENT);
        addStun(STUN_DURATION_FROM_HITS);
    }
    private void addStun(int duration) {
        ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffectRegistry.STUN.get(), duration, 0, false, false, true));
    }
    public void endStun() {
        duration = 0;
        updateTracking();
    }
    public void setMaxDur(int amount) {
        maxDuration = Math.max(1, amount);
        updateTracking();
    }
    public int getMaxStunPointsFromHit() {
        return (int) (getMaxPoints() * 0.7);
    }
    public boolean isStunned() {
        return duration > 0;
    }
    public boolean hasPoints() {
        return stunPoints > 0;
    }
    public boolean renderBar() {
        return isStunned() || hasPoints();
    }
    public void setDuration(int amount) {
        duration = amount;
        updateTracking();
    }
    public void onHit(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        float amount = event.getAmount();
        LivingEntity victim = (LivingEntity) entity;
        if (!isStunned()) {
            int premStun = stunPoints / getMaxPoints() * STUN_DURATION_FROM_HITS;
            if ("mob".equals(source.getMsgId()) || "player".equals(source.getMsgId())) {
                float multiplier = 1;
                int newStunPoints = (int) (amount / victim.getMaxHealth() * getMaxPoints()) * 2;
                int minStunPointsAdded = (int) (getMaxPoints() * 0.05f);
                newStunPoints = Math.max(minStunPointsAdded, newStunPoints);
                addStunPoints((int) (Math.min(getMaxStunPointsFromHit(), newStunPoints) * multiplier));
                decayDelay = 40;
                if (stunPoints == getMaxPoints()) {
                    startStun();
                }
            }
            int dur;
            if (source.isExplosion()) {
                dur = (int) Math.max(premStun, amount * 5);
                addStun(dur);
            }
            if (source.isFall()) {
                dur = (int) Math.max(premStun, amount * 5 / Math.max(1, victim.getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(Enchantments.FALL_PROTECTION)));
                addStun(dur);
            }
            updateTracking();
        }
    }
}
