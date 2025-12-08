package net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments;

import net.arkadiyhimself.fantazia.common.api.attachment.ISyncInitially;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.data.tags.FTZDamageTypeTags;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class PassiveBarrierHolder implements INBTSerializable<CompoundTag>, ISyncInitially {

    public static final int DEFAULT_RECHARGE = 240;

    private final IAttachmentHolder iAttachmentHolder;
    private boolean unlocked = false;
    private boolean enabled = true;
    private int time = DEFAULT_RECHARGE;

    public PassiveBarrierHolder(IAttachmentHolder iAttachmentHolder) {
        this.iAttachmentHolder = iAttachmentHolder;
    }

    public void tick() {
        if (!this.unlocked) return;
        if (this.time > 0) this.time--;
    }

    public void unlock() {
        this.unlocked = true;
    }

    public void lock() {
        this.unlocked = false;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getTime() {
        return time;
    }

    public void putOnRecharge() {
        this.time = DEFAULT_RECHARGE;
    }

    public void activate() {
        putOnRecharge();
        explode();
    }

    private void explode() {
        if (!(iAttachmentHolder instanceof Entity entity)) return;
        DamageSourcesHolder holder = LevelAttributesHelper.getDamageSources(entity.level());
        if (holder == null) return;
        DamageSource source = holder.passiveBarrierExplosion(entity);
        Vec3 pos = entity.getEyePosition();
        Explosion explosion = new Explosion(entity.level(), entity, source, null,
                pos.x, pos.y, pos.z, 2.5f, false,
                Explosion.BlockInteraction.KEEP, ParticleTypes.EXPLOSION,
                ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE
        );
        boolean client = entity.level().isClientSide();
        if (!EventHooks.onExplosionStart(entity.level(), explosion)) {
            if (client) explosion.finalizeExplosion(true);
            else explosion.explode();
        }
    }

    public void onHit(LivingIncomingDamageEvent event) {
        if (!this.unlocked || !this.enabled || event.getSource().is(FTZDamageTypeTags.IGNORED_BY_PASSIVE_BARRIER)) return;
        if (this.time <= 0) {
            activate();
            event.setCanceled(true);
            if (iAttachmentHolder instanceof ServerPlayer serverPlayer)
                IPacket.passiveBarrierActivation(serverPlayer);
        }
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("enabled", this.enabled);
        tag.putInt("time", this.time);
        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.unlocked = tag.getBoolean("unlocked");
        this.enabled = tag.getBoolean("enabled");
        this.time = tag.getInt("time");
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("unlocked", this.unlocked);
        tag.putBoolean("enabled", this.enabled);
        tag.putInt("time", this.time);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        this.unlocked = tag.getBoolean("unlocked");
        this.enabled = tag.getBoolean("enabled");
        this.time = tag.getInt("time");
    }
}
