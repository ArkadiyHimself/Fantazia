package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.ComplexLivingEffectHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import javax.annotation.Nullable;
import java.util.UUID;

public class PuppeteeredEffectHolder extends ComplexLivingEffectHolder {

    private @Nullable UUID master = null;
    private @Nullable UUID puppet = null;
    private @Nullable Entity cachedPuppet = null;
    private boolean hasPuppet = false;

    public PuppeteeredEffectHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.location("puppeteered"), FTZMobEffects.PUPPETEERED);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        if (master != null) tag.putUUID("master", master);
        if (puppet != null) tag.putUUID("puppet", puppet);
        tag.putBoolean("hasPuppet", hasPuppet);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);

        if (compoundTag.hasUUID("master")) this.master = compoundTag.getUUID("master");
        if (compoundTag.hasUUID("puppet")) this.puppet = compoundTag.getUUID("puppet");
        this.hasPuppet = compoundTag.getBoolean("hasPuppet");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("hasPuppet", hasPuppet);
        return super.serializeInitial();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        this.hasPuppet = tag.getBoolean("hasPuppet");
    }

    @Override
    public void ended(MobEffect mobEffect) {
        super.ended(mobEffect);
        if (mobEffect != FTZMobEffects.PUPPETEERED.value()) return;
        if (getEntity().level() instanceof ServerLevel serverLevel && master != null) {
            Entity entity = serverLevel.getEntity(master);
            if (entity instanceof LivingEntity livingEntity) LivingEffectHelper.acceptConsumer(livingEntity, PuppeteeredEffectHolder.class, puppeteeredEffect -> puppeteeredEffect.removePuppet(getEntity().getUUID()));
        }
        this.master = null;
    }

    @Override
    public void serverTick() {
        if (cachedPuppet == null && getEntity().level() instanceof ServerLevel serverLevel && puppet != null) {
            cachedPuppet = serverLevel.getEntity(puppet);
        }
        if (cachedPuppet != null && !cachedPuppet.isAlive()) removePuppet(puppet);
    }

    public boolean isPuppeteeredBy(LivingEntity entity) {
        return entity.getUUID().equals(master);
    }

    public void enslave(LivingEntity master) {
        this.master = master.getUUID();
    }

    public void givePuppet(LivingEntity livingEntity) {
        UUID newPuppet = livingEntity.getUUID();
        if (newPuppet.equals(puppet) || !(getEntity().level() instanceof ServerLevel serverLevel)) return;
        if (puppet != null) {
            Entity entity = serverLevel.getEntity(puppet);
            if (entity != null) entity.kill();
        }
        this.puppet = newPuppet;
        this.hasPuppet = true;
        if (getEntity() instanceof ServerPlayer serverPlayer) IPacket.puppeteerChange(serverPlayer, hasPuppet);
    }

    public void removePuppet(UUID puppet) {
        if (puppet != this.puppet) return;
        this.puppet = null;
        this.cachedPuppet = null;
        this.hasPuppet = false;
        if (getEntity() instanceof ServerPlayer serverPlayer) IPacket.puppeteerChange(serverPlayer, hasPuppet);
    }

    public boolean hasPuppet() {
        return hasPuppet;
    }

    public void setPuppetBoolean(boolean value) {
        this.hasPuppet = value;
    }
}
